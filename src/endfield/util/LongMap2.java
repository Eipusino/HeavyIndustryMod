package endfield.util;

import arc.func.Cons;
import arc.math.Mathf;
import arc.struct.LongSeq;
import arc.struct.Seq;
import arc.util.ArcRuntimeException;
import arc.util.Eachable;
import endfield.math.Mathm;
import endfield.util.misc.LongHolder;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static endfield.util.Constant.EMPTY;
import static endfield.util.Constant.INDEX_ILLEGAL;
import static endfield.util.Constant.INDEX_ZERO;
import static endfield.util.Constant.PRIME2;
import static endfield.util.Constant.PRIME3;

public class LongMap2<V> implements Iterable<LongHolder<V>>, Eachable<LongHolder<V>>, Cloneable {
	public final Class<?> valueComponentType;

	public int size;

	public long[] keyTable;
	public V[] valueTable;
	public int capacity, stashSize;
	public V zeroValue;
	public boolean hasZeroValue;

	protected float loadFactor;
	protected int hashShift, mask, threshold;
	protected int stashCapacity;
	protected int pushIterations;

	protected transient Entries entries1, entries2;
	protected transient Values values1, values2;
	protected transient Keys keys1, keys2;

	/** Creates a new map with an initial capacity of 51 and a load factor of 0.8. */
	public LongMap2(Class<?> keyType) {
		this(51, 0.8f, keyType);
	}

	/**
	 * Creates a new map with a load factor of 0.8.
	 *
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
	 */
	public LongMap2(int initialCapacity, Class<?> keyType) {
		this(initialCapacity, 0.8f, keyType);
	}

	/**
	 * Creates a new map with the specified initial capacity and load factor. This map will hold initialCapacity items before
	 * growing the backing table.
	 *
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
	 */
	@SuppressWarnings("unchecked")
	public LongMap2(int initialCapacity, float loadFactor, Class<?> keyType) {
		if (initialCapacity < 0) throw new IllegalArgumentException("initialCapacity must be >= 0: " + initialCapacity);
		initialCapacity = Mathf.nextPowerOfTwo((int) Math.ceil(initialCapacity / loadFactor));
		if (initialCapacity > 1 << 30)
			throw new IllegalArgumentException("initialCapacity is too large: " + initialCapacity);
		capacity = initialCapacity;

		if (loadFactor <= 0) throw new IllegalArgumentException("loadFactor must be > 0: " + loadFactor);
		this.loadFactor = loadFactor;

		threshold = (int) (capacity * loadFactor);
		mask = capacity - 1;
		hashShift = 63 - Long.numberOfTrailingZeros(capacity);
		stashCapacity = Math.max(3, (int) Math.ceil(Math.log(capacity)) * 2);
		pushIterations = Mathm.clamp(capacity, 8, (int) Math.sqrt(capacity) / 8);

		valueComponentType = keyType;

		keyTable = new long[capacity + stashCapacity];
		valueTable = (V[]) Array.newInstance(keyType, keyTable.length);
	}

	/** Creates a new map identical to the specified map. */
	public LongMap2(LongMap2<? extends V> map) {
		this((int) Math.floor(map.capacity * map.loadFactor), map.loadFactor, map.valueComponentType);
		stashSize = map.stashSize;
		System.arraycopy(map.keyTable, 0, keyTable, 0, map.keyTable.length);
		System.arraycopy(map.valueTable, 0, valueTable, 0, map.valueTable.length);
		size = map.size;
		zeroValue = map.zeroValue;
		hasZeroValue = map.hasZeroValue;
	}

	@SuppressWarnings("unchecked")
	public LongMap2<V> copy() {
		try {
			LongMap2<V> out = (LongMap2<V>) super.clone();
			out.keyTable = keyTable.clone();
			out.valueTable = valueTable.clone();

			out.entries1 = out.entries2 = null;
			out.values1 = out.values2 = null;
			out.keys1 = out.keys2 = null;

			return out;
		} catch (CloneNotSupportedException e) {
			return new LongMap2<>(this);
		}
	}

	@Override
	public void each(Cons<? super LongHolder<V>> cons) {
		for (LongHolder<V> entry : entries()) {
			cons.get(entry);
		}
	}

	public V put(long key, V value) {
		if (key == 0) {
			V oldValue = zeroValue;
			zeroValue = value;
			if (!hasZeroValue) {
				hasZeroValue = true;
				size++;
			}
			return oldValue;
		}

		// Check for existing keys.
		int index1 = (int) (key & mask);
		long key1 = keyTable[index1];
		if (key1 == key) {
			V oldValue = valueTable[index1];
			valueTable[index1] = value;
			return oldValue;
		}

		int index2 = hash2(key);
		long key2 = keyTable[index2];
		if (key2 == key) {
			V oldValue = valueTable[index2];
			valueTable[index2] = value;
			return oldValue;
		}

		int index3 = hash3(key);
		long key3 = keyTable[index3];
		if (key3 == key) {
			V oldValue = valueTable[index3];
			valueTable[index3] = value;
			return oldValue;
		}

		// Update key in the stash.
		for (int i = capacity, n = i + stashSize; i < n; i++) {
			if (keyTable[i] == key) {
				V oldValue = valueTable[i];
				valueTable[i] = value;
				return oldValue;
			}
		}

		// Check for empty buckets.
		if (key1 == EMPTY) {
			keyTable[index1] = key;
			valueTable[index1] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return null;
		}

		if (key2 == EMPTY) {
			keyTable[index2] = key;
			valueTable[index2] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return null;
		}

		if (key3 == EMPTY) {
			keyTable[index3] = key;
			valueTable[index3] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return null;
		}

		push(key, value, index1, key1, index2, key2, index3, key3);
		return null;
	}

	public void putAll(LongMap2<? extends V> map) {
		for (LongHolder<? extends V> entry : map.entries())
			put(entry.key, entry.value);
	}

	/** Skips checks for existing keys. */
	protected void putResize(long key, V value) {
		if (key == 0) {
			zeroValue = value;
			hasZeroValue = true;
			return;
		}

		// Check for empty buckets.
		int index1 = (int) (key & mask);
		long key1 = keyTable[index1];
		if (key1 == EMPTY) {
			keyTable[index1] = key;
			valueTable[index1] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		int index2 = hash2(key);
		long key2 = keyTable[index2];
		if (key2 == EMPTY) {
			keyTable[index2] = key;
			valueTable[index2] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		int index3 = hash3(key);
		long key3 = keyTable[index3];
		if (key3 == EMPTY) {
			keyTable[index3] = key;
			valueTable[index3] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		push(key, value, index1, key1, index2, key2, index3, key3);
	}

	protected void push(long insertKey, V insertValue, int index1, long key1, int index2, long key2, int index3, long key3) {
		// Push keys until an empty bucket is found.
		long evictedKey;
		V evictedValue;
		int i = 0;
		do {
			// Replace the key and value for one of the hashes.
			switch (Mathf.random(2)) {
				case 0:
					evictedKey = key1;
					evictedValue = valueTable[index1];
					keyTable[index1] = insertKey;
					valueTable[index1] = insertValue;
					break;
				case 1:
					evictedKey = key2;
					evictedValue = valueTable[index2];
					keyTable[index2] = insertKey;
					valueTable[index2] = insertValue;
					break;
				default:
					evictedKey = key3;
					evictedValue = valueTable[index3];
					keyTable[index3] = insertKey;
					valueTable[index3] = insertValue;
					break;
			}

			// If the evicted key hashes to an empty bucket, put it there and stop.
			index1 = (int) (evictedKey & mask);
			key1 = keyTable[index1];
			if (key1 == EMPTY) {
				keyTable[index1] = evictedKey;
				valueTable[index1] = evictedValue;
				if (size++ >= threshold) resize(capacity << 1);
				return;
			}

			index2 = hash2(evictedKey);
			key2 = keyTable[index2];
			if (key2 == EMPTY) {
				keyTable[index2] = evictedKey;
				valueTable[index2] = evictedValue;
				if (size++ >= threshold) resize(capacity << 1);
				return;
			}

			index3 = hash3(evictedKey);
			key3 = keyTable[index3];
			if (key3 == EMPTY) {
				keyTable[index3] = evictedKey;
				valueTable[index3] = evictedValue;
				if (size++ >= threshold) resize(capacity << 1);
				return;
			}

			if (++i == pushIterations) break;

			insertKey = evictedKey;
			insertValue = evictedValue;
		} while (true);

		putStash(evictedKey, evictedValue);
	}

	protected void putStash(long key, V value) {
		if (stashSize == stashCapacity) {
			// Too many pushes occurred and the stash is full, increase the table size.
			resize(capacity << 1);
			putResize(key, value);
			return;
		}
		// Store key in the stash.
		int index = capacity + stashSize;
		keyTable[index] = key;
		valueTable[index] = value;
		stashSize++;
		size++;
	}

	public V get(long key) {
		if (key == 0) {
			if (!hasZeroValue) return null;
			return zeroValue;
		}
		int index = (int) (key & mask);
		if (keyTable[index] != key) {
			index = hash2(key);
			if (keyTable[index] != key) {
				index = hash3(key);
				if (keyTable[index] != key) return getStash(key, null);
			}
		}
		return valueTable[index];
	}

	public V get(long key, V defaultValue) {
		if (key == 0) {
			if (!hasZeroValue) return defaultValue;
			return zeroValue;
		}
		int index = (int) (key & mask);
		if (keyTable[index] != key) {
			index = hash2(key);
			if (keyTable[index] != key) {
				index = hash3(key);
				if (keyTable[index] != key) return getStash(key, defaultValue);
			}
		}
		return valueTable[index];
	}

	protected V getStash(long key, V defaultValue) {
		for (int i = capacity, n = i + stashSize; i < n; i++)
			if (keyTable[i] == key) return valueTable[i];
		return defaultValue;
	}

	public V remove(long key) {
		if (key == 0) {
			if (!hasZeroValue) return null;
			V oldValue = zeroValue;
			zeroValue = null;
			hasZeroValue = false;
			size--;
			return oldValue;
		}

		int index = (int) (key & mask);
		if (keyTable[index] == key) {
			keyTable[index] = EMPTY;
			V oldValue = valueTable[index];
			valueTable[index] = null;
			size--;
			return oldValue;
		}

		index = hash2(key);
		if (keyTable[index] == key) {
			keyTable[index] = EMPTY;
			V oldValue = valueTable[index];
			valueTable[index] = null;
			size--;
			return oldValue;
		}

		index = hash3(key);
		if (keyTable[index] == key) {
			keyTable[index] = EMPTY;
			V oldValue = valueTable[index];
			valueTable[index] = null;
			size--;
			return oldValue;
		}

		return removeStash(key);
	}

	protected V removeStash(long key) {
		for (int i = capacity, n = i + stashSize; i < n; i++) {
			if (keyTable[i] == key) {
				V oldValue = valueTable[i];
				removeStashIndex(i);
				size--;
				return oldValue;
			}
		}
		return null;
	}

	protected void removeStashIndex(int index) {
		// If the removed location was not last, move the last tuple to the removed location.
		stashSize--;
		int lastIndex = capacity + stashSize;
		if (index < lastIndex) {
			keyTable[index] = keyTable[lastIndex];
			valueTable[index] = valueTable[lastIndex];
			valueTable[lastIndex] = null;
		} else
			valueTable[index] = null;
	}

	/** Returns true if the map is empty. */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Reduces the size of the backing arrays to be the specified capacity or less. If the capacity is already less, nothing is
	 * done. If the map contains more items than the specified capacity, the next highest power of two capacity is used instead.
	 */
	public void shrink(int maximumCapacity) {
		if (maximumCapacity < 0) throw new IllegalArgumentException("maximumCapacity must be >= 0: " + maximumCapacity);
		if (size > maximumCapacity) maximumCapacity = size;
		if (capacity <= maximumCapacity) return;
		maximumCapacity = Mathf.nextPowerOfTwo(maximumCapacity);
		resize(maximumCapacity);
	}

	/** Clears the map and reduces the size of the backing arrays to be the specified capacity if they are larger. */
	public void clear(int maximumCapacity) {
		if (capacity <= maximumCapacity) {
			clear();
			return;
		}
		zeroValue = null;
		hasZeroValue = false;
		size = 0;
		resize(maximumCapacity);
	}

	public void clear() {
		if (size == 0) return;
		for (int i = capacity + stashSize; i-- > 0; ) {
			keyTable[i] = EMPTY;
			valueTable[i] = null;
		}
		size = 0;
		stashSize = 0;
		zeroValue = null;
		hasZeroValue = false;
	}

	/**
	 * Returns true if the specified value is in the map. Note this traverses the entire map and compares every value, which may
	 * be an expensive operation.
	 */
	public boolean containsValue(Object value, boolean identity) {
		if (value == null) {
			if (hasZeroValue && zeroValue == null) return true;
			for (int i = capacity + stashSize; i-- > 0; )
				if (keyTable[i] != EMPTY && valueTable[i] == null) return true;
		} else if (identity) {
			if (value == zeroValue) return true;
			for (int i = capacity + stashSize; i-- > 0; )
				if (valueTable[i] == value) return true;
		} else {
			if (hasZeroValue && value.equals(zeroValue)) return true;
			for (int i = capacity + stashSize; i-- > 0; )
				if (value.equals(valueTable[i])) return true;
		}
		return false;
	}

	public boolean containsKey(long key) {
		if (key == 0) return hasZeroValue;
		int index = (int) (key & mask);
		if (keyTable[index] != key) {
			index = hash2(key);
			if (keyTable[index] != key) {
				index = hash3(key);
				if (keyTable[index] != key) return containsKeyStash(key);
			}
		}
		return true;
	}

	protected boolean containsKeyStash(long key) {
		for (int i = capacity, n = i + stashSize; i < n; i++)
			if (keyTable[i] == key) return true;
		return false;
	}

	/**
	 * Returns the key for the specified value, or <tt>notFound</tt> if it is not in the map. Note this traverses the entire map
	 * and compares every value, which may be an expensive operation.
	 *
	 * @param identity If true, uses == to compare the specified value with values in the map. If false, uses
	 *                 {@link #equals(Object)}.
	 */
	public long findKey(Object value, boolean identity, long notFound) {
		if (value == null) {
			if (hasZeroValue && zeroValue == null) return 0;
			for (int i = capacity + stashSize; i-- > 0; )
				if (keyTable[i] != EMPTY && valueTable[i] == null) return keyTable[i];
		} else if (identity) {
			if (value == zeroValue) return 0;
			for (int i = capacity + stashSize; i-- > 0; )
				if (valueTable[i] == value) return keyTable[i];
		} else {
			if (hasZeroValue && value.equals(zeroValue)) return 0;
			for (int i = capacity + stashSize; i-- > 0; )
				if (value.equals(valueTable[i])) return keyTable[i];
		}
		return notFound;
	}

	/**
	 * Increases the size of the backing array to accommodate the specified number of additional items. Useful before adding many
	 * items to avoid multiple backing array resizes.
	 */
	public void ensureCapacity(int additionalCapacity) {
		if (additionalCapacity < 0)
			throw new IllegalArgumentException("additionalCapacity must be >= 0: " + additionalCapacity);
		int sizeNeeded = size + additionalCapacity;
		if (sizeNeeded >= threshold) resize(Mathf.nextPowerOfTwo((int) Math.ceil(sizeNeeded / loadFactor)));
	}

	@SuppressWarnings("unchecked")
	protected void resize(int newSize) {
		int oldEndIndex = capacity + stashSize;

		capacity = newSize;
		threshold = (int) (newSize * loadFactor);
		mask = newSize - 1;
		hashShift = 63 - Long.numberOfTrailingZeros(newSize);
		stashCapacity = Math.max(3, (int) Math.ceil(Math.log(newSize)) * 2);
		pushIterations = Mathm.clamp(newSize, 8, (int) Math.sqrt(newSize) / 8);

		long[] oldKeyTable = keyTable;
		V[] oldValueTable = valueTable;

		keyTable = new long[newSize + stashCapacity];
		valueTable = (V[]) Array.newInstance(valueComponentType, newSize + stashCapacity);

		int oldSize = size;
		size = hasZeroValue ? 1 : 0;
		stashSize = 0;
		if (oldSize > 0) {
			for (int i = 0; i < oldEndIndex; i++) {
				long key = oldKeyTable[i];
				if (key != EMPTY) putResize(key, oldValueTable[i]);
			}
		}
	}

	protected int hash2(long h) {
		h *= PRIME2;
		return (int) ((h ^ h >>> hashShift) & mask);
	}

	protected int hash3(long h) {
		h *= PRIME3;
		return (int) ((h ^ h >>> hashShift) & mask);
	}

	@Override
	public int hashCode() {
		int h = 0;
		if (hasZeroValue && zeroValue != null) {
			h += zeroValue.hashCode();
		}
		for (int i = 0, n = capacity + stashSize; i < n; i++) {
			long key = keyTable[i];
			if (key != EMPTY) {
				h += (int) (key ^ (key >>> 32)) * 31;

				V value = valueTable[i];
				if (value != null) {
					h += value.hashCode();
				}
			}
		}
		return h;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof LongMap2<?> map) || map.valueComponentType != valueComponentType) return false;

		if (map.size != size) return false;
		if (map.hasZeroValue != hasZeroValue) return false;
		if (hasZeroValue) {
			if (map.zeroValue == null) {
				if (zeroValue != null) return false;
			} else {
				if (!map.zeroValue.equals(zeroValue)) return false;
			}
		}
		for (int i = 0, n = capacity + stashSize; i < n; i++) {
			long key = keyTable[i];
			if (key != EMPTY) {
				V value = valueTable[i];
				if (value == null) {
					if (!map.containsKey(key) || map.get(key) != null) return false;
				} else {
					if (!value.equals(map.get(key))) return false;
				}
			}
		}
		return true;
	}

	@Override
	public String toString() {
		if (size == 0) return "[]";
		StringBuilder buffer = new StringBuilder(32);
		buffer.append('[');
		int i = keyTable.length;
		while (i-- > 0) {
			long key = keyTable[i];
			if (key == EMPTY) continue;
			buffer.append(key);
			buffer.append('=');
			buffer.append(valueTable[i]);
			break;
		}
		while (i-- > 0) {
			long key = keyTable[i];
			if (key == EMPTY) continue;
			buffer.append(", ");
			buffer.append(key);
			buffer.append('=');
			buffer.append(valueTable[i]);
		}
		buffer.append(']');
		return buffer.toString();
	}

	@Override
	public @NotNull Iterator<LongHolder<V>> iterator() {
		return entries();
	}

	/**
	 * Returns an iterator for the entries in the map. Remove is supported. Note that the same iterator instance is returned each
	 * time this method is called. Use the {@link Entries} constructor for nested or multithreaded iteration.
	 */
	public Entries entries() {
		if (entries1 == null) {
			entries1 = new Entries();
			entries2 = new Entries();
		}
		if (!entries1.valid) {
			entries1.reset();
			entries1.valid = true;
			entries2.valid = false;
			return entries1;
		}
		entries2.reset();
		entries2.valid = true;
		entries1.valid = false;
		return entries2;
	}

	/**
	 * Returns an iterator for the values in the map. Remove is supported. Note that the same iterator instance is returned each
	 * time this method is called. Use the {@link Entries} constructor for nested or multithreaded iteration.
	 */
	public Values values() {
		if (values1 == null) {
			values1 = new Values();
			values2 = new Values();
		}
		if (!values1.valid) {
			values1.reset();
			values1.valid = true;
			values2.valid = false;
			return values1;
		}
		values2.reset();
		values2.valid = true;
		values1.valid = false;
		return values2;
	}

	public void eachValue(Cons<V> cons) {
		boolean hasNext = false;

		int nextIndex = INDEX_ZERO;

		if (hasZeroValue) {
			hasNext = true;
		} else {
			for (int n = capacity + stashSize; ++nextIndex < n; ) {
				if (keyTable[nextIndex] != EMPTY) {
					hasNext = true;
					break;
				}
			}
		}

		while (hasNext) {
			cons.get(nextIndex == INDEX_ZERO ? zeroValue : valueTable[nextIndex]);

			hasNext = false;
			for (int n = capacity + stashSize; ++nextIndex < n; ) {
				if (keyTable[nextIndex] != EMPTY) {
					hasNext = true;
					break;
				}
			}
		}
	}

	/**
	 * Returns an iterator for the keys in the map. Remove is supported. Note that the same iterator instance is returned each
	 * time this method is called. Use the {@link Entries} constructor for nested or multithreaded iteration.
	 */
	public Keys keys() {
		if (keys1 == null) {
			keys1 = new Keys();
			keys2 = new Keys();
		}
		if (!keys1.valid) {
			keys1.reset();
			keys1.valid = true;
			keys2.valid = false;
			return keys1;
		}
		keys2.reset();
		keys2.valid = true;
		keys1.valid = false;
		return keys2;
	}

	protected class MapIterator {
		public boolean hasNext;

		protected int nextIndex, currentIndex;
		protected boolean valid = true;

		public MapIterator() {
			reset();
		}

		public void reset() {
			currentIndex = INDEX_ILLEGAL;
			nextIndex = INDEX_ZERO;
			if (hasZeroValue)
				hasNext = true;
			else
				findNextIndex();
		}

		protected void findNextIndex() {
			hasNext = false;
			long[] keyTable = LongMap2.this.keyTable;
			for (int n = capacity + stashSize; ++nextIndex < n; ) {
				if (keyTable[nextIndex] != EMPTY) {
					hasNext = true;
					break;
				}
			}
		}

		public void remove() {
			if (currentIndex == INDEX_ZERO && hasZeroValue) {
				zeroValue = null;
				hasZeroValue = false;
			} else if (currentIndex < 0) {
				throw new IllegalStateException("next must be called before remove.");
			} else if (currentIndex >= capacity) {
				removeStashIndex(currentIndex);
				nextIndex = currentIndex - 1;
				findNextIndex();
			} else {
				keyTable[currentIndex] = EMPTY;
				valueTable[currentIndex] = null;
			}
			currentIndex = INDEX_ILLEGAL;
			size--;
		}
	}

	public class Entries extends MapIterator implements Iterable<LongHolder<V>>, Iterator<LongHolder<V>> {
		protected LongHolder<V> entry = new LongHolder<>();

		/** Note the same entry instance is returned each time this method is called. */
		@Override
		public LongHolder<V> next() {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");

			if (nextIndex == INDEX_ZERO) {
				entry.key = 0;
				entry.value = zeroValue;
			} else {
				entry.key = keyTable[nextIndex];
				entry.value = valueTable[nextIndex];
			}
			currentIndex = nextIndex;
			findNextIndex();
			return entry;
		}

		@Override
		public boolean hasNext() {
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			return hasNext;
		}

		@Override
		public @NotNull Iterator<LongHolder<V>> iterator() {
			return this;
		}
	}

	public class Values extends MapIterator implements Iterable<V>, Iterator<V> {
		@Override
		public boolean hasNext() {
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			return hasNext;
		}

		@Override
		public V next() {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			V value;
			if (nextIndex == INDEX_ZERO)
				value = zeroValue;
			else
				value = valueTable[nextIndex];
			currentIndex = nextIndex;
			findNextIndex();
			return value;
		}

		@Override
		public @NotNull Iterator<V> iterator() {
			return this;
		}

		/** Returns a new array containing the remaining values. */
		public Seq<V> toArray() {
			Seq<V> array = new Seq<>(true, size, valueComponentType);
			while (hasNext)
				array.add(next());
			return array;
		}
	}

	public class Keys extends MapIterator {
		public long next() {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			long key = nextIndex == INDEX_ZERO ? 0 : keyTable[nextIndex];
			currentIndex = nextIndex;
			findNextIndex();
			return key;
		}

		/** Returns a new array containing the remaining values. */
		public LongSeq toArray() {
			LongSeq array = new LongSeq(true, size);
			while (hasNext)
				array.add(next());
			return array;
		}
	}
}
