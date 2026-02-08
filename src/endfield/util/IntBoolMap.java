package endfield.util;

import arc.math.Mathf;
import arc.struct.BoolSeq;
import arc.struct.IntSeq;
import arc.util.ArcRuntimeException;
import endfield.util.holder.IntBoolHolder;

import java.util.Iterator;
import java.util.NoSuchElementException;

import static endfield.util.Constant.EMPTY;
import static endfield.util.Constant.INDEX_ILLEGAL;
import static endfield.util.Constant.INDEX_ZERO;
import static endfield.util.Constant.PRIME2;
import static endfield.util.Constant.PRIME3;

/**
 * An unordered map where the keys are ints and values are booleans. This implementation is a cuckoo hash map using 3 hashes, random
 * walking, and a small stash for problematic keys. Null keys are not allowed. No allocation is done except when growing the table
 * size. <br>
 * <br>
 * This map performs very fast get, containsKey, and remove (typically O(1), worst case O(log(n))). Put may be a bit slower,
 * depending on hash collisions. Load factors greater than 0.91 greatly increase the chances the map will have to rehash to the
 * next higher POT size.
 * @author Nathan Sweet
 */
public class IntBoolMap implements Iterable<IntBoolHolder>, Cloneable {
	public int size;

	protected int[] keyTable;
	protected boolean[] valueTable;
	protected int capacity, stashSize;
	protected boolean zeroValue;
	protected boolean hasZeroValue;

	protected float loadFactor;
	protected int hashShift, mask, threshold;
	protected int stashCapacity;
	protected int pushIterations;

	protected transient Entries entries1, entries2;
	protected transient Values values1, values2;
	protected transient Keys keys1, keys2;

	/** Creates a new map with an initial capacity of 51 and a load factor of 0.8. */
	public IntBoolMap() {
		this(51, 0.8f);
	}

	/**
	 * Creates a new map with a load factor of 0.8.
	 *
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
	 */
	public IntBoolMap(int initialCapacity) {
		this(initialCapacity, 0.8f);
	}

	/**
	 * Creates a new map with the specified initial capacity and load factor. This map will hold initialCapacity items before
	 * growing the backing table.
	 *
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
	 */
	public IntBoolMap(int initialCapacity, float loadFactor) {
		if (initialCapacity < 0) throw new IllegalArgumentException("initialCapacity must be >= 0: " + initialCapacity);
		initialCapacity = Mathf.nextPowerOfTwo((int) Math.ceil(initialCapacity / loadFactor));
		if (initialCapacity > 1 << 30)
			throw new IllegalArgumentException("initialCapacity is too large: " + initialCapacity);
		capacity = initialCapacity;

		if (loadFactor <= 0) throw new IllegalArgumentException("loadFactor must be > 0: " + loadFactor);
		this.loadFactor = loadFactor;

		threshold = (int) (capacity * loadFactor);
		mask = capacity - 1;
		hashShift = 31 - Integer.numberOfTrailingZeros(capacity);
		stashCapacity = Math.max(3, (int) Math.ceil(Math.log(capacity)) * 2);
		pushIterations = Math.max(Math.min(capacity, 8), (int) Math.sqrt(capacity) / 8);

		keyTable = new int[capacity + stashCapacity];
		valueTable = new boolean[keyTable.length];
	}

	/** Creates a new map identical to the specified map. */
	public IntBoolMap(IntBoolMap map) {
		this((int) Math.floor(map.capacity * map.loadFactor), map.loadFactor);
		stashSize = map.stashSize;
		System.arraycopy(map.keyTable, 0, keyTable, 0, map.keyTable.length);
		System.arraycopy(map.valueTable, 0, valueTable, 0, map.valueTable.length);
		size = map.size;
		zeroValue = map.zeroValue;
		hasZeroValue = map.hasZeroValue;
	}

	public static IntBoolMap of(Object... values) {
		IntBoolMap map = new IntBoolMap();
		for (int i = 0; i < values.length; i += 2) {
			map.put((int) values[i], (boolean) values[i + 1]);
		}
		return map;
	}

	public IntBoolMap copy() {
		try {
			IntBoolMap out = (IntBoolMap) super.clone();
			out.keyTable = keyTable.clone();
			out.valueTable = valueTable.clone();

			out.entries1 = out.entries2 = null;
			out.values1 = out.values2 = null;
			out.keys1 = out.keys2 = null;

			return out;
		} catch (CloneNotSupportedException e) {
			return new IntBoolMap(this);
		}
	}

	public void put(int key, boolean value) {
		if (key == 0) {
			zeroValue = value;
			if (!hasZeroValue) {
				hasZeroValue = true;
				size++;
			}
			return;
		}

		// Check for existing keys.
		int index1 = key & mask;
		int key1 = keyTable[index1];
		if (key == key1) {
			valueTable[index1] = value;
			return;
		}

		int index2 = hash2(key);
		int key2 = keyTable[index2];
		if (key == key2) {
			valueTable[index2] = value;
			return;
		}

		int index3 = hash3(key);
		int key3 = keyTable[index3];
		if (key == key3) {
			valueTable[index3] = value;
			return;
		}

		// Update key in the stash.
		for (int i = capacity, n = i + stashSize; i < n; i++) {
			if (key == keyTable[i]) {
				valueTable[i] = value;
				return;
			}
		}

		// Check for empty buckets.
		if (key1 == EMPTY) {
			keyTable[index1] = key;
			valueTable[index1] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		if (key2 == EMPTY) {
			keyTable[index2] = key;
			valueTable[index2] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		if (key3 == EMPTY) {
			keyTable[index3] = key;
			valueTable[index3] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		push(key, value, index1, key1, index2, key2, index3, key3);
	}

	public void putAll(IntBoolMap map) {
		for (IntBoolHolder entry : map.entries())
			put(entry.key, entry.value);
	}

	/** Skips checks for existing keys. */
	protected void putResize(int key, boolean value) {
		if (key == 0) {
			zeroValue = value;
			hasZeroValue = true;
			return;
		}

		// Check for empty buckets.
		int index1 = key & mask;
		int key1 = keyTable[index1];
		if (key1 == EMPTY) {
			keyTable[index1] = key;
			valueTable[index1] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		int index2 = hash2(key);
		int key2 = keyTable[index2];
		if (key2 == EMPTY) {
			keyTable[index2] = key;
			valueTable[index2] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		int index3 = hash3(key);
		int key3 = keyTable[index3];
		if (key3 == EMPTY) {
			keyTable[index3] = key;
			valueTable[index3] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		push(key, value, index1, key1, index2, key2, index3, key3);
	}

	protected void push(int insertKey, boolean insertValue, int index1, int key1, int index2, int key2, int index3, int key3) {
		// Push keys until an empty bucket is found.
		int evictedKey;
		boolean evictedValue;
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
			index1 = evictedKey & mask;
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

	protected void putStash(int key, boolean value) {
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

	public boolean get(int key) {
		return get(key, false);
	}

	/** @param defaultValue Returned if the key was not associated with a value. */
	public boolean get(int key, boolean defaultValue) {
		if (key == 0) {
			if (!hasZeroValue) return defaultValue;
			return zeroValue;
		}
		int index = key & mask;
		if (keyTable[index] != key) {
			index = hash2(key);
			if (keyTable[index] != key) {
				index = hash3(key);
				if (keyTable[index] != key) return getStash(key, defaultValue);
			}
		}
		return valueTable[index];
	}

	/**
	 * Only inserts into the map if value is not present.
	 *
	 * @param key   The key.
	 * @param value The value.
	 * @return The associated value if key is present in the map, else {@code value}.
	 *
	 */
	public boolean getOrPut(final int key, final boolean value) {
		if (key == 0) {
			if (!hasZeroValue) {
				zeroValue = value;
				hasZeroValue = true;
				size++;
			}
			return zeroValue;
		}

		// Check for existing keys.
		int index1 = key & mask;
		int key1 = keyTable[index1];
		if (key == key1) {
			return valueTable[index1];
		}

		int index2 = hash2(key);
		int key2 = keyTable[index2];
		if (key == key2) {
			return valueTable[index2];
		}

		int index3 = hash3(key);
		int key3 = keyTable[index3];
		if (key == key3) {
			return valueTable[index3];
		}

		// Update key in the stash.
		for (int i = capacity, n = i + stashSize; i < n; i++) {
			if (key == keyTable[i]) {
				return valueTable[i];
			}
		}

		// Check for empty buckets.
		if (key1 == EMPTY) {
			keyTable[index1] = key;
			valueTable[index1] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return value;
		}

		if (key2 == EMPTY) {
			keyTable[index2] = key;
			valueTable[index2] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return value;
		}

		if (key3 == EMPTY) {
			keyTable[index3] = key;
			valueTable[index3] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return value;
		}

		push(key, value, index1, key1, index2, key2, index3, key3);
		return value;
	}

	protected boolean getStash(int key, boolean defaultValue) {
		int[] keyTable = this.keyTable;
		for (int i = capacity, n = i + stashSize; i < n; i++)
			if (key == keyTable[i]) return valueTable[i];
		return defaultValue;
	}

	public boolean remove(int key) {
		return remove(key, false);
	}

	public boolean remove(int key, boolean defaultValue) {
		if (key == 0) {
			if (!hasZeroValue) return defaultValue;
			hasZeroValue = false;
			size--;
			return zeroValue;
		}

		int index = key & mask;
		if (key == keyTable[index]) {
			keyTable[index] = EMPTY;
			boolean oldValue = valueTable[index];
			size--;
			return oldValue;
		}

		index = hash2(key);
		if (key == keyTable[index]) {
			keyTable[index] = EMPTY;
			boolean oldValue = valueTable[index];
			size--;
			return oldValue;
		}

		index = hash3(key);
		if (key == keyTable[index]) {
			keyTable[index] = EMPTY;
			boolean oldValue = valueTable[index];
			size--;
			return oldValue;
		}

		return removeStash(key, defaultValue);
	}

	boolean removeStash(int key, boolean defaultValue) {
		for (int i = capacity, n = i + stashSize; i < n; i++) {
			if (key == keyTable[i]) {
				boolean oldValue = valueTable[i];
				removeStashIndex(i);
				size--;
				return oldValue;
			}
		}
		return defaultValue;
	}

	void removeStashIndex(int index) {
		// If the removed location was not last, move the last tuple to the removed location.
		stashSize--;
		int lastIndex = capacity + stashSize;
		if (index < lastIndex) {
			keyTable[index] = keyTable[lastIndex];
			valueTable[index] = valueTable[lastIndex];
		}
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
		hasZeroValue = false;
		size = 0;
		resize(maximumCapacity);
	}

	public void clear() {
		if (size == 0) return;
		for (int i = capacity + stashSize; i-- > 0; )
			keyTable[i] = EMPTY;
		size = 0;
		stashSize = 0;
		hasZeroValue = false;
	}

	/**
	 * Returns true if the specified value is in the map. Note this traverses the entire map and compares every value, which may be
	 * an expensive operation.
	 */
	public boolean containsValue(boolean value) {
		if (hasZeroValue && zeroValue == value) return true;
		for (int i = capacity + stashSize; i-- > 0; )
			if (keyTable[i] != 0 && valueTable[i] == value) return true;
		return false;
	}

	public boolean containsKey(int key) {
		if (key == 0) return hasZeroValue;
		int index = key & mask;
		if (keyTable[index] != key) {
			index = hash2(key);
			if (keyTable[index] != key) {
				index = hash3(key);
				if (keyTable[index] != key) return containsKeyStash(key);
			}
		}
		return true;
	}

	protected boolean containsKeyStash(int key) {
		for (int i = capacity, n = i + stashSize; i < n; i++)
			if (key == keyTable[i]) return true;
		return false;
	}

	/**
	 * Returns the key for the specified value, or null if it is not in the map. Note this traverses the entire map and compares
	 * every value, which may be an expensive operation.
	 */
	public int findKey(boolean value, int notFound) {
		if (hasZeroValue && zeroValue == value) return 0;
		for (int i = capacity + stashSize; i-- > 0; )
			if (keyTable[i] != 0 && valueTable[i] == value) return keyTable[i];
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

	protected void resize(int newSize) {
		int oldEndIndex = capacity + stashSize;

		capacity = newSize;
		threshold = (int) (newSize * loadFactor);
		mask = newSize - 1;
		hashShift = 31 - Integer.numberOfTrailingZeros(newSize);
		stashCapacity = Math.max(3, (int) Math.ceil(Math.log(newSize)) * 2);
		pushIterations = Math.max(Math.min(newSize, 8), (int) Math.sqrt(newSize) / 8);

		int[] oldKeyTable = keyTable;
		boolean[] oldValueTable = valueTable;

		keyTable = new int[newSize + stashCapacity];
		valueTable = new boolean[newSize + stashCapacity];

		int oldSize = size;
		size = hasZeroValue ? 1 : 0;
		stashSize = 0;
		if (oldSize > 0) {
			for (int i = 0; i < oldEndIndex; i++) {
				int key = oldKeyTable[i];
				if (key != EMPTY) putResize(key, oldValueTable[i]);
			}
		}
	}

	protected int hash2(int h) {
		h *= PRIME2;
		return (h ^ h >>> hashShift) & mask;
	}

	protected int hash3(int h) {
		h *= PRIME3;
		return (h ^ h >>> hashShift) & mask;
	}

	@Override
	public int hashCode() {
		int h = 0;
		if (hasZeroValue) {
			h += Boolean.hashCode(zeroValue);
		}
		for (int i = 0, n = capacity + stashSize; i < n; i++) {
			int key = keyTable[i];
			if (key != EMPTY) {
				h += key * 31;

				boolean value = valueTable[i];
				h += Boolean.hashCode(value);
			}
		}
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof IntBoolMap other)) return false;
		if (other.size != size) return false;
		if (other.hasZeroValue != hasZeroValue) return false;
		if (hasZeroValue && other.zeroValue != zeroValue) {
			return false;
		}
		for (int i = 0, n = capacity + stashSize; i < n; i++) {
			int key = keyTable[i];
			if (key != EMPTY) {
				boolean otherValue = other.get(key, false);
				if (!other.containsKey(key)) return false;
				boolean value = valueTable[i];
				if (otherValue != value) return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		if (size == 0) return "{}";
		StringBuilder buffer = new StringBuilder(32);
		buffer.append('{');
		int i = keyTable.length;
		if (hasZeroValue) {
			buffer.append("0=");
			buffer.append(zeroValue);
		} else {
			while (i-- > 0) {
				int key = keyTable[i];
				if (key == EMPTY) continue;
				buffer.append(key);
				buffer.append('=');
				buffer.append(valueTable[i]);
				break;
			}
		}
		while (i-- > 0) {
			int key = keyTable[i];
			if (key == EMPTY) continue;
			buffer.append(", ");
			buffer.append(key);
			buffer.append('=');
			buffer.append(valueTable[i]);
		}
		buffer.append('}');
		return buffer.toString();
	}

	@Override
	public Iterator<IntBoolHolder> iterator() {
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

	/**
	 * Returns an iterator for the keys in the map. Remove is supported. Note that the same iterator instance is returned each time
	 * this method is called. Use the {@link Entries} constructor for nested or multithreaded iteration.
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

	public class MapIterator {
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
			for (int n = capacity + stashSize; ++nextIndex < n; ) {
				if (keyTable[nextIndex] != EMPTY) {
					hasNext = true;
					break;
				}
			}
		}

		public void remove() {
			if (currentIndex == INDEX_ZERO && hasZeroValue) {
				hasZeroValue = false;
			} else if (currentIndex < 0) {
				throw new IllegalStateException("next must be called before remove.");
			} else if (currentIndex >= capacity) {
				removeStashIndex(currentIndex);
				nextIndex = currentIndex - 1;
				findNextIndex();
			} else {
				keyTable[currentIndex] = EMPTY;
			}
			currentIndex = INDEX_ILLEGAL;
			size--;
		}
	}

	public class Entries extends MapIterator implements Iterable<IntBoolHolder>, Iterator<IntBoolHolder> {
		protected IntBoolHolder entry = new IntBoolHolder();

		public Entries() {}

		/** Note the same entry instance is returned each time this method is called. */
		public IntBoolHolder next() {
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
		public Iterator<IntBoolHolder> iterator() {
			return this;
		}
	}

	public class Values extends MapIterator {
		public Values() {}

		public boolean hasNext() {
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			return hasNext;
		}

		public boolean next() {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			boolean value;
			if (nextIndex == INDEX_ZERO)
				value = zeroValue;
			else
				value = valueTable[nextIndex];
			currentIndex = nextIndex;
			findNextIndex();
			return value;
		}

		/** Returns a new array containing the remaining values. */
		public BoolSeq toSeq() {
			BoolSeq array = new BoolSeq(true, size);
			while (hasNext)
				array.add(next());
			return array;
		}
	}

	public class Keys extends MapIterator {
		public Keys() {}

		public boolean hasNext() {
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			return hasNext;
		}

		public int next() {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			int key = nextIndex == INDEX_ZERO ? 0 : keyTable[nextIndex];
			currentIndex = nextIndex;
			findNextIndex();
			return key;
		}

		/** Returns a new array containing the remaining keys. */
		public IntSeq toSeq() {
			IntSeq array = new IntSeq(true, size);
			while (hasNext)
				array.add(next());
			return array;
		}
	}
}
