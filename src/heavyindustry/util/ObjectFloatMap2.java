package heavyindustry.util;

import arc.func.Cons;
import arc.math.Mathf;
import arc.struct.FloatSeq;
import arc.struct.Seq;
import arc.util.ArcRuntimeException;
import arc.util.Eachable;
import heavyindustry.util.pair.ObjectFloatPair;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static heavyindustry.util.Constant.PRIME2;
import static heavyindustry.util.Constant.PRIME3;

/**
 * An unordered map where the values are floats. This implementation is a cuckoo hash map using 3 hashes, random walking, and a
 * small stash for problematic keys. Null keys are not allowed. No allocation is done except when growing the table size. <br>
 * <br>This map performs very fast get, containsKey, and remove (typically O(1), worst case O(log(n))). Put may be a bit slower,
 * depending on hash collisions. Load factors greater than 0.91 greatly increase the chances the map will have to rehash to the
 * next higher POT size.
 * <p><strong>Eipusino modification: add null judgment to some methods to prevent throw NullPointerException.</strong>
 *
 * @author Nathan Sweet
 * @author Eipusino
 */
public class ObjectFloatMap2<K> implements Iterable<ObjectFloatPair<K>>, Eachable<ObjectFloatPair<K>> {
	public int size;

	public final Class<?> keyComponentType;

	public K[] keyTable;
	public float[] valueTable;
	public int capacity, stashSize;

	float loadFactor;
	int hashShift, mask, threshold;
	int stashCapacity;
	int pushIterations;

	Entries<K> entries1, entries2;
	Values<K> values1, values2;
	Keys<K> keys1, keys2;

	/** Creates a new map with an initial capacity of 51 and a load factor of 0.8. */
	public ObjectFloatMap2(Class<?> keyType) {
		this(keyType, 51, 0.8f);
	}

	/**
	 * Creates a new map with a load factor of 0.8.
	 *
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
	 */
	public ObjectFloatMap2(Class<?> keyType, int initialCapacity) {
		this(keyType, initialCapacity, 0.8f);
	}

	/**
	 * Creates a new map with the specified initial capacity and load factor. This map will hold initialCapacity items before
	 * growing the backing table.
	 *
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
	 */
	@SuppressWarnings("unchecked")
	public ObjectFloatMap2(Class<?> keyType, int initialCapacity, float loadFactor) {
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

		keyComponentType = keyType;

		keyTable = (K[]) Array.newInstance(keyType, capacity + stashCapacity);
		valueTable = new float[keyTable.length];
	}

	/** Creates a new map identical to the specified map. */
	public ObjectFloatMap2(ObjectFloatMap2<? extends K> map) {
		this(map.keyComponentType, (int) Math.floor(map.capacity * map.loadFactor), map.loadFactor);
		stashSize = map.stashSize;
		System.arraycopy(map.keyTable, 0, keyTable, 0, map.keyTable.length);
		System.arraycopy(map.valueTable, 0, valueTable, 0, map.valueTable.length);
		size = map.size;
	}

	@Override
	public void each(Cons<? super ObjectFloatPair<K>> cons) {
		for (ObjectFloatPair<K> e : entries()) {
			cons.get(e);
		}
	}

	public void put(K key, float value) {
		if (key == null) return;

		// Check for existing keys.
		int hashCode = key.hashCode();
		int index1 = hashCode & mask;
		K key1 = keyTable[index1];
		if (key.equals(key1)) {
			valueTable[index1] = value;
			return;
		}

		int index2 = hash2(hashCode);
		K key2 = keyTable[index2];
		if (key.equals(key2)) {
			valueTable[index2] = value;
			return;
		}

		int index3 = hash3(hashCode);
		K key3 = keyTable[index3];
		if (key.equals(key3)) {
			valueTable[index3] = value;
			return;
		}

		// Update key in the stash.
		for (int i = capacity, n = i + stashSize; i < n; i++) {
			if (key.equals(keyTable[i])) {
				valueTable[i] = value;
				return;
			}
		}

		// Check for empty buckets.
		if (key1 == null) {
			keyTable[index1] = key;
			valueTable[index1] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		if (key2 == null) {
			keyTable[index2] = key;
			valueTable[index2] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		if (key3 == null) {
			keyTable[index3] = key;
			valueTable[index3] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		push(key, value, index1, key1, index2, key2, index3, key3);
	}

	public void putAll(ObjectFloatMap2<? extends K> map) {
		for (ObjectFloatPair<? extends K> entry : map.entries()) put(entry.key, entry.value);
	}

	/** Skips checks for existing keys. */
	private void putResize(K key, float value) {
		if (key == null) return;

		// Check for empty buckets.
		int hashCode = key.hashCode();
		int index1 = hashCode & mask;
		K key1 = keyTable[index1];
		if (key1 == null) {
			keyTable[index1] = key;
			valueTable[index1] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		int index2 = hash2(hashCode);
		K key2 = keyTable[index2];
		if (key2 == null) {
			keyTable[index2] = key;
			valueTable[index2] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		int index3 = hash3(hashCode);
		K key3 = keyTable[index3];
		if (key3 == null) {
			keyTable[index3] = key;
			valueTable[index3] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return;
		}

		push(key, value, index1, key1, index2, key2, index3, key3);
	}

	private void push(K insertKey, float insertValue, int index1, K key1, int index2, K key2, int index3, K key3) {
		// Push keys until an empty bucket is found.
		K evictedKey;
		float evictedValue;
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
			int hashCode = evictedKey.hashCode();
			index1 = hashCode & mask;
			key1 = keyTable[index1];
			if (key1 == null) {
				keyTable[index1] = evictedKey;
				valueTable[index1] = evictedValue;
				if (size++ >= threshold) resize(capacity << 1);
				return;
			}

			index2 = hash2(hashCode);
			key2 = keyTable[index2];
			if (key2 == null) {
				keyTable[index2] = evictedKey;
				valueTable[index2] = evictedValue;
				if (size++ >= threshold) resize(capacity << 1);
				return;
			}

			index3 = hash3(hashCode);
			key3 = keyTable[index3];
			if (key3 == null) {
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

	private void putStash(K key, float value) {
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

	/** @param defaultValue Returned if the key was not associated with a value. */
	public float get(K key, float defaultValue) {
		if (key == null) return defaultValue;

		int hashCode = key.hashCode();
		int index = hashCode & mask;
		if (!key.equals(keyTable[index])) {
			index = hash2(hashCode);
			if (!key.equals(keyTable[index])) {
				index = hash3(hashCode);
				if (!key.equals(keyTable[index])) return getStash(key, defaultValue);
			}
		}
		return valueTable[index];
	}

	private float getStash(K key, float defaultValue) {
		for (int i = capacity, n = i + stashSize; i < n; i++)
			if (key.equals(keyTable[i])) return valueTable[i];
		return defaultValue;
	}

	/**
	 * Returns the key's current value and increments the stored value. If the key is not in the map, defaultValue + increment is
	 * put into the map.
	 */
	public float increment(K key, float defaultValue, float increment) {
		if (key == null) return defaultValue;

		int hashCode = key.hashCode();
		int index = hashCode & mask;
		if (!key.equals(keyTable[index])) {
			index = hash2(hashCode);
			if (!key.equals(keyTable[index])) {
				index = hash3(hashCode);
				if (!key.equals(keyTable[index])) return getAndIncrementStash(key, defaultValue, increment);
			}
		}
		float value = valueTable[index];
		valueTable[index] = value + increment;
		return value;
	}

	private float getAndIncrementStash(K key, float defaultValue, float increment) {
		for (int i = capacity, n = i + stashSize; i < n; i++)
			if (key.equals(keyTable[i])) {
				float value = valueTable[i];
				valueTable[i] = value + increment;
				return value;
			}
		put(key, defaultValue + increment);
		return defaultValue;
	}

	public void remove(K key) {
		if (key == null) return;

		int hashCode = key.hashCode();
		int index = hashCode & mask;
		if (key.equals(keyTable[index])) {
			keyTable[index] = null;
			size--;
			return;
		}

		index = hash2(hashCode);
		if (key.equals(keyTable[index])) {
			keyTable[index] = null;
			size--;
			return;
		}

		index = hash3(hashCode);
		if (key.equals(keyTable[index])) {
			keyTable[index] = null;
			size--;
			return;
		}

		removeStash(key);
	}

	public float remove(K key, float defaultValue) {
		if (key == null) return defaultValue;

		int hashCode = key.hashCode();
		int index = hashCode & mask;
		if (key.equals(keyTable[index])) {
			keyTable[index] = null;
			float oldValue = valueTable[index];
			size--;
			return oldValue;
		}

		index = hash2(hashCode);
		if (key.equals(keyTable[index])) {
			keyTable[index] = null;
			float oldValue = valueTable[index];
			size--;
			return oldValue;
		}

		index = hash3(hashCode);
		if (key.equals(keyTable[index])) {
			keyTable[index] = null;
			float oldValue = valueTable[index];
			size--;
			return oldValue;
		}

		return removeStash(key, defaultValue);
	}

	void removeStash(K key) {
		for (int i = capacity, n = i + stashSize; i < n; i++) {
			if (key.equals(keyTable[i])) {
				removeStashIndex(i);
				size--;
				break;
			}
		}
	}

	float removeStash(K key, float defaultValue) {
		for (int i = capacity, n = i + stashSize; i < n; i++) {
			if (key.equals(keyTable[i])) {
				float oldValue = valueTable[i];
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
			keyTable[lastIndex] = null;
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
		size = 0;
		resize(maximumCapacity);
	}

	public void clear() {
		if (size == 0) return;
		for (int i = capacity + stashSize; i-- > 0; )
			keyTable[i] = null;
		size = 0;
		stashSize = 0;
	}

	/**
	 * Returns true if the specified value is in the map. Note this traverses the entire map and compares every value, which may be
	 * an expensive operation.
	 */
	public boolean containsValue(float value) {
		for (int i = capacity + stashSize; i-- > 0; )
			if (keyTable[i] != null && valueTable[i] == value) return true;
		return false;
	}

	public boolean containsKey(Object key) {
		if (key == null) return false;

		int hashCode = key.hashCode();
		int index = hashCode & mask;
		if (!key.equals(keyTable[index])) {
			index = hash2(hashCode);
			if (!key.equals(keyTable[index])) {
				index = hash3(hashCode);
				if (!key.equals(keyTable[index])) return containsKeyStash(key);
			}
		}
		return true;
	}

	private boolean containsKeyStash(Object key) {
		for (int i = capacity, n = i + stashSize; i < n; i++)
			if (key.equals(keyTable[i])) return true;
		return false;
	}

	/**
	 * Returns the key for the specified value, or null if it is not in the map. Note this traverses the entire map and compares
	 * every value, which may be an expensive operation.
	 */
	public K findKey(float value) {
		for (int i = capacity + stashSize; i-- > 0; )
			if (keyTable[i] != null && valueTable[i] == value) return keyTable[i];
		return null;
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
	private void resize(int newSize) {
		int oldEndIndex = capacity + stashSize;

		capacity = newSize;
		threshold = (int) (newSize * loadFactor);
		mask = newSize - 1;
		hashShift = 31 - Integer.numberOfTrailingZeros(newSize);
		stashCapacity = Math.max(3, (int) Math.ceil(Math.log(newSize)) * 2);
		pushIterations = Math.max(Math.min(newSize, 8), (int) Math.sqrt(newSize) / 8);

		K[] oldKeyTable = keyTable;
		float[] oldValueTable = valueTable;

		keyTable = (K[]) Array.newInstance(keyComponentType, newSize + stashCapacity);
		valueTable = new float[newSize + stashCapacity];

		int oldSize = size;
		size = 0;
		stashSize = 0;
		if (oldSize > 0) {
			for (int i = 0; i < oldEndIndex; i++) {
				K key = oldKeyTable[i];
				if (key != null) putResize(key, oldValueTable[i]);
			}
		}
	}

	private int hash2(int h) {
		h *= PRIME2;
		return (h ^ h >>> hashShift) & mask;
	}

	private int hash3(int h) {
		h *= PRIME3;
		return (h ^ h >>> hashShift) & mask;
	}

	@Override
	public int hashCode() {
		int h = 0;
		for (int i = 0, n = capacity + stashSize; i < n; i++) {
			K key = keyTable[i];
			if (key != null) {
				h += key.hashCode() * 31;

				float value = valueTable[i];
				h += Float.floatToIntBits(value);
			}
		}
		return h;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof ObjectFloatMap2<?> map) || map.keyComponentType != keyComponentType) return false;
		ObjectFloatMap2<K> other = (ObjectFloatMap2<K>) map;
		if (other.size != size) return false;
		for (int i = 0, n = capacity + stashSize; i < n; i++) {
			K key = keyTable[i];
			if (key != null) {
				float otherValue = other.get(key, 0f);
				if (otherValue == 0f && !other.containsKey(key)) return false;
				float value = valueTable[i];
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
		while (i-- > 0) {
			K key = keyTable[i];
			if (key == null) continue;
			buffer.append(key);
			buffer.append('=');
			buffer.append(valueTable[i]);
			break;
		}
		while (i-- > 0) {
			K key = keyTable[i];
			if (key == null) continue;
			buffer.append(", ");
			buffer.append(key);
			buffer.append('=');
			buffer.append(valueTable[i]);
		}
		buffer.append('}');
		return buffer.toString();
	}

	@Override
	public Entries<K> iterator() {
		return entries();
	}

	/**
	 * Returns an iterator for the entries in the map. Remove is supported. Note that the same iterator instance is returned each
	 * time this method is called. Use the {@link Entries} constructor for nested or multithreaded iteration.
	 */
	public Entries<K> entries() {
		if (entries1 == null) {
			entries1 = new Entries<>(this);
			entries2 = new Entries<>(this);
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
	public Values<K> values() {
		if (values1 == null) {
			values1 = new Values<>(this);
			values2 = new Values<>(this);
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
	public Keys<K> keys() {
		if (keys1 == null) {
			keys1 = new Keys<>(this);
			keys2 = new Keys<>(this);
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

	private static class MapIterator<K> {
		final ObjectFloatMap2<K> map;

		public boolean hasNext;

		int nextIndex, currentIndex;
		boolean valid = true;

		public MapIterator(ObjectFloatMap2<K> map) {
			this.map = map;
			reset();
		}

		public void reset() {
			currentIndex = -1;
			nextIndex = -1;
			findNextIndex();
		}

		void findNextIndex() {
			hasNext = false;
			K[] keyTable = map.keyTable;
			for (int n = map.capacity + map.stashSize; ++nextIndex < n; ) {
				if (keyTable[nextIndex] != null) {
					hasNext = true;
					break;
				}
			}
		}

		public void remove() {
			if (currentIndex < 0) throw new IllegalStateException("next must be called before remove.");
			if (currentIndex >= map.capacity) {
				map.removeStashIndex(currentIndex);
				nextIndex = currentIndex - 1;
				findNextIndex();
			} else {
				map.keyTable[currentIndex] = null;
			}
			currentIndex = -1;
			map.size--;
		}
	}

	public static class Entries<K> extends MapIterator<K> implements Iterable<ObjectFloatPair<K>>, Iterator<ObjectFloatPair<K>> {
		ObjectFloatPair<K> entry = new ObjectFloatPair<>();

		public Entries(ObjectFloatMap2<K> map) {
			super(map);
		}

		/** Note the same entry instance is returned each time this method is called. */
		@Override
		public ObjectFloatPair<K> next() {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			K[] keyTable = map.keyTable;
			entry.key = keyTable[nextIndex];
			entry.value = map.valueTable[nextIndex];
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
		public Entries<K> iterator() {
			return this;
		}
	}

	public static class Values<K> extends MapIterator<K> {
		public Values(ObjectFloatMap2<K> map) {
			super(map);
		}

		public boolean hasNext() {
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			return hasNext;
		}

		public float next() {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			float value = map.valueTable[nextIndex];
			currentIndex = nextIndex;
			findNextIndex();
			return value;
		}

		/** Returns a new array containing the remaining values. */
		public FloatSeq toSeq() {
			FloatSeq array = new FloatSeq(true, map.size);
			while (hasNext)
				array.add(next());
			return array;
		}

		public float[] toArray() {
			float[] array = new float[map.size];
			int i = 0;
			while (hasNext) {
				array[i] = next();
				i++;
			}
			return array;
		}
	}

	public static class Keys<K> extends MapIterator<K> implements Iterable<K>, Iterator<K> {
		public Keys(ObjectFloatMap2<K> map) {
			super(map);
		}

		@Override
		public boolean hasNext() {
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			return hasNext;
		}

		@Override
		public K next() {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			K key = map.keyTable[nextIndex];
			currentIndex = nextIndex;
			findNextIndex();
			return key;
		}

		@Override
		public Keys<K> iterator() {
			return this;
		}

		public Seq<K> toSeq() {
			Seq<K> seq = new Seq<>(true, map.size, map.keyComponentType);
			while (hasNext)
				seq.add(next());
			return seq;
		}

		public Seq<K> toSeq(Seq<K> seq) {
			while (hasNext)
				seq.add(next());
			return seq;
		}

		/** Returns a new array containing the remaining keys. */
		public CollectionList<K> toList() {
			CollectionList<K> array = new CollectionList<>(true, map.size, map.keyComponentType);
			while (hasNext)
				array.add(next());
			return array;
		}

		/** Adds the remaining keys to the array. */
		public CollectionList<K> toList(CollectionList<K> array) {
			while (hasNext)
				array.add(next());
			return array;
		}

		@SuppressWarnings("unchecked")
		public K[] toArray() {
			K[] array = (K[]) Array.newInstance(map.keyComponentType, map.size);
			int i = 0;
			while (hasNext) {
				array[i] = next();
				i++;
			}
			return array;
		}
	}
}
