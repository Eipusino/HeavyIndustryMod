package heavyindustry.util;

import arc.func.Cons;
import arc.func.Cons2;
import arc.func.Prov;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.ArcRuntimeException;
import arc.util.Eachable;
import heavyindustry.util.holder.ObjectHolder;

import java.lang.reflect.Array;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static heavyindustry.util.Constant.PRIME2;
import static heavyindustry.util.Constant.PRIME3;

/**
 * Implementation of Java Collection Framework Map based on {@code ObjectMap} wrapper,
 * used in places that require Java specifications and the feature of not creating nodes in ObjectMap.
 * <p><strong>It is not recommended to use primitive types.</strong>
 *
 * @author Eipusino
 */
public class CollectionObjectMap<K, V> implements Iterable<ObjectHolder<K, V>>, Map<K, V>, Eachable<ObjectHolder<K, V>>, Cloneable {
	public int size;

	public final Class<?> keyComponentType;
	public final Class<?> valueComponentType;

	public K[] keyTable;
	public V[] valueTable;

	public int capacity, stashSize;

	float loadFactor;
	int hashShift, mask, threshold;
	int stashCapacity;
	int pushIterations;

	Entries<K, V> entries1, entries2;
	Values<K, V> values1, values2;
	Keys<K, V> keys1, keys2;

	@SuppressWarnings("unchecked")
	public static <K, V> CollectionObjectMap<K, V> of(Class<?> keyType, Class<?> valueType, Object... values) {
		CollectionObjectMap<K, V> map = new CollectionObjectMap<>(keyType, valueType);

		for (int i = 0; i < values.length / 2; i++) {
			map.put((K) values[i * 2], (V) values[i * 2 + 1]);
		}

		return map;
	}

	/** Creates a new map with an initial capacity of 51 and a load factor of 0.8. */
	public CollectionObjectMap(Class<?> keyType, Class<?> valueType) {
		this(keyType, valueType, 51, 0.8f);
	}

	/**
	 * Creates a new map with a load factor of 0.8.
	 *
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
	 */
	public CollectionObjectMap(Class<?> keyType, Class<?> valueType, int initialCapacity) {
		this(keyType, valueType, initialCapacity, 0.8f);
	}

	/**
	 * Creates a new map with the specified initial capacity and load factor. This map will hold initialCapacity items before
	 * growing the backing table.
	 *
	 * @param initialCapacity If not a power of two, it is increased to the next nearest power of two.
	 */
	@SuppressWarnings("unchecked")
	public CollectionObjectMap(Class<?> keyType, Class<?> valueType, int initialCapacity, float loadFactor) {
		if (initialCapacity < 0) throw new IllegalArgumentException("initialCapacity must be >= 0: " + initialCapacity);
		initialCapacity = Mathf.nextPowerOfTwo((int) Math.ceil(initialCapacity / loadFactor));
		if (initialCapacity > 0x40000000)
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
		valueComponentType = valueType;

		keyTable = (K[]) Array.newInstance(keyType, capacity + stashCapacity);
		valueTable = (V[]) Array.newInstance(valueType, keyTable.length);
	}

	/** Creates a new map identical to the specified map. */
	public CollectionObjectMap(CollectionObjectMap<? extends K, ? extends V> map) {
		this(map.keyComponentType, map.valueComponentType, (int) Math.floor(map.capacity * map.loadFactor), map.loadFactor);
		stashSize = map.stashSize;
		System.arraycopy(map.keyTable, 0, keyTable, 0, map.keyTable.length);
		System.arraycopy(map.valueTable, 0, valueTable, 0, map.valueTable.length);
		size = map.size;
	}

	/** Iterates through key/value pairs. */
	public void each(Cons2<? super K, ? super V> cons) {
		for (ObjectHolder<K, V> entry : iterator()) {
			cons.get(entry.key, entry.value);
		}
	}

	@Override
	public void each(Cons<? super ObjectHolder<K, V>> cons) {
		for (ObjectHolder<K, V> entry : iterator()) {
			cons.get(entry);
		}
	}

	@SuppressWarnings("unchecked")
	public CollectionObjectMap<K, V> copy() {
		try {
			CollectionObjectMap<K, V> out = (CollectionObjectMap<K, V>) super.clone();
			out.keyTable = Arrays.copyOf(keyTable, keyTable.length);
			out.valueTable = Arrays.copyOf(valueTable, valueTable.length);
			// Do not copy these fields.
			out.entries1 = out.entries2 = null;
			out.keys1 = out.keys2 = null;
			out.values1 = out.values2 = null;
			return out;
		} catch (CloneNotSupportedException e) {
			CollectionObjectMap<K, V> out = new CollectionObjectMap<>(keyComponentType, valueComponentType);
			out.putAll(this);
			return out;
		}
	}

	/** Returns the old value associated with the specified key, or null. */
	@Override
	public V put(K key, V value) {
		if (key == null) return null;

		// Check for existing keys.
		int hashCode = key.hashCode();
		int index1 = hashCode & mask;
		K key1 = keyTable[index1];
		if (key.equals(key1)) {
			V oldValue = valueTable[index1];
			valueTable[index1] = value;
			return oldValue;
		}

		int index2 = hash2(hashCode);
		K key2 = keyTable[index2];
		if (key.equals(key2)) {
			V oldValue = valueTable[index2];
			valueTable[index2] = value;
			return oldValue;
		}

		int index3 = hash3(hashCode);
		K key3 = keyTable[index3];
		if (key.equals(key3)) {
			V oldValue = valueTable[index3];
			valueTable[index3] = value;
			return oldValue;
		}

		// Update key in the stash.
		for (int i = capacity, n = i + stashSize; i < n; i++) {
			if (key.equals(keyTable[i])) {
				V oldValue = valueTable[i];
				valueTable[i] = value;
				return oldValue;
			}
		}

		// Check for empty buckets.
		if (key1 == null) {
			keyTable[index1] = key;
			valueTable[index1] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return null;
		}

		if (key2 == null) {
			keyTable[index2] = key;
			valueTable[index2] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return null;
		}

		if (key3 == null) {
			keyTable[index3] = key;
			valueTable[index3] = value;
			if (size++ >= threshold) resize(capacity << 1);
			return null;
		}

		push(key, value, index1, key1, index2, key2, index3, key3);
		return null;
	}

	public V put(Entry<? extends K, ? extends V> entry) {
		return put(entry.getKey(), entry.getValue());
	}

	public void putAll(CollectionObjectMap<? extends K, ? extends V> map) {
		ensureCapacity(map.size);
		for (ObjectHolder<? extends K, ? extends V> entry : map)
			put(entry.key, entry.value);
	}

	@SuppressWarnings("unchecked")
	public void putAll(Object... values) {
		for (int i = 0; i < values.length / 2; i++) {
			put((K) values[i * 2], (V) values[i * 2 + 1]);
		}
	}

	/// Put all the keys of this other map into this map, and return this map for chaining.
	public CollectionObjectMap<K, V> merge(CollectionObjectMap<? extends K, ? extends V> map) {
		putAll(map);
		return this;
	}

	/// Skips checks for existing keys.
	void putResize(K key, V value) {
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

	void push(K insertKey, V insertValue, int index1, K key1, int index2, K key2, int index3, K key3) {
		// Push keys until an empty bucket is found.
		K evictedKey;
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

	void putStash(K key, V value) {
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

	public V getThrow(K key, Prov<? extends RuntimeException> error) {
		if (!containsKey(key)) {
			throw error.get();
		}
		return get(key);
	}

	/**
	 * Tries to get the value. If it does not exist, it creates a new instance using the supplier and places it,
	 * returning the value.
	 */
	public V get(K key, Prov<V> supplier) {
		V val = get(key);
		if (val == null) {
			put(key, val = supplier.get());
		}
		return val;
	}

	/// Get, with a nullable key.
	public V getNull(K key) {
		return key == null ? null : get(key);
	}

	/// Returns the value for the specified key, or null if the key is not in the map.
	@Override
	public V get(Object key) {
		if (key == null) return null;

		int hashCode = key.hashCode();
		int index = hashCode & mask;
		if (!key.equals(keyTable[index])) {
			index = hash2(hashCode);
			if (!key.equals(keyTable[index])) {
				index = hash3(hashCode);
				if (!key.equals(keyTable[index])) return getStash(key, null);
			}
		}
		return valueTable[index];
	}

	/// Returns the value for the specified key, or the default value if the key is not in the map.
	public V get(K key, V defaultValue) {
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

	V getStash(Object key, V defaultValue) {
		for (int i = capacity, n = i + stashSize; i < n; i++)
			if (key.equals(keyTable[i])) return valueTable[i];
		return defaultValue;
	}

	/// Returns the value associated with the key, or null.
	@Override
	public V remove(Object key) {
		if (key == null) return null;

		int hashCode = key.hashCode();
		int index = hashCode & mask;
		if (key.equals(keyTable[index])) {
			keyTable[index] = null;
			V oldValue = valueTable[index];
			valueTable[index] = null;
			size--;
			return oldValue;
		}

		index = hash2(hashCode);
		if (key.equals(keyTable[index])) {
			keyTable[index] = null;
			V oldValue = valueTable[index];
			valueTable[index] = null;
			size--;
			return oldValue;
		}

		index = hash3(hashCode);
		if (key.equals(keyTable[index])) {
			keyTable[index] = null;
			V oldValue = valueTable[index];
			valueTable[index] = null;
			size--;
			return oldValue;
		}

		return removeStash(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (ObjectHolder<K, V> set : iterator()) {
			put(set.getKey(), set.getValue());
		}
	}

	V removeStash(Object key) {
		for (int i = capacity, n = i + stashSize; i < n; i++) {
			if (key.equals(keyTable[i])) {
				V oldValue = valueTable[i];
				removeStashIndex(i);
				size--;
				return oldValue;
			}
		}
		return null;
	}

	void removeStashIndex(int index) {
		// If the removed location was not last, move the last tuple to the removed location.
		stashSize--;
		int lastIndex = capacity + stashSize;
		if (index < lastIndex) {
			keyTable[index] = keyTable[lastIndex];
			valueTable[index] = valueTable[lastIndex];
			keyTable[lastIndex] = null;
			valueTable[lastIndex] = null;
		} else {
			keyTable[index] = null;
			valueTable[index] = null;
		}
	}

	@Override
	public int size() {
		return size;
	}

	/** Returns true if the map is empty. */
	@Override
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

	/**
	 * Clears the map and reduces the size of the backing arrays to be the specified capacity, if they are larger. The reduction
	 * is done by allocating new arrays, though for large arrays this can be faster than clearing the existing array.
	 */
	public void clear(int maximumCapacity) {
		if (capacity <= maximumCapacity) {
			clear();
			return;
		}
		size = 0;
		resize(maximumCapacity);
	}

	/**
	 * Clears the map, leaving the backing arrays at the current capacity. When the capacity is high and the population is low,
	 * iteration can be unnecessarily slow. {@link #clear(int)} can be used to reduce the capacity.
	 */
	@Override
	public void clear() {
		if (size == 0) return;
		for (int i = capacity + stashSize; i-- > 0; ) {
			keyTable[i] = null;
			valueTable[i] = null;
		}
		size = 0;
		stashSize = 0;
	}

	/**
	 * Returns true if the specified value is in the map. Note this traverses the entire map and compares every value, which may
	 * be an expensive operation.
	 *
	 * @param identity If true, uses == to compare the specified value with values in the map. If false, uses
	 *                 {@link #equals(Object)}.
	 */
	public boolean containsValue(Object value, boolean identity) {
		if (value == null) {
			for (int i = capacity + stashSize; i-- > 0; )
				if (keyTable[i] != null && valueTable[i] == null) return true;
		} else if (identity) {
			for (int i = capacity + stashSize; i-- > 0; )
				if (valueTable[i] == value) return true;
		} else {
			for (int i = capacity + stashSize; i-- > 0; )
				if (value.equals(valueTable[i])) return true;
		}
		return false;
	}

	@Override
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

	@Override
	public boolean containsValue(Object value) {
		return containsValue(value, false);
	}

	boolean containsKeyStash(Object key) {
		for (int i = capacity, n = i + stashSize; i < n; i++)
			if (key.equals(keyTable[i])) return true;
		return false;
	}

	/**
	 * Returns the key for the specified value, or null if it is not in the map. Note this traverses the entire map and compares
	 * every value, which may be an expensive operation.
	 *
	 * @param identity If true, uses == to compare the specified value with values in the map. If false, uses
	 *                 {@link #equals(Object)}.
	 */
	public K findKey(Object value, boolean identity) {
		if (value == null) {
			for (int i = capacity + stashSize; i-- > 0; )
				if (keyTable[i] != null && valueTable[i] == null) return keyTable[i];
		} else if (identity) {
			for (int i = capacity + stashSize; i-- > 0; )
				if (valueTable[i] == value) return keyTable[i];
		} else {
			for (int i = capacity + stashSize; i-- > 0; )
				if (value.equals(valueTable[i])) return keyTable[i];
		}
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
	void resize(int newSize) {
		int oldEndIndex = capacity + stashSize;

		capacity = newSize;
		threshold = (int) (newSize * loadFactor);
		mask = newSize - 1;
		hashShift = 31 - Integer.numberOfTrailingZeros(newSize);
		stashCapacity = Math.max(3, (int) Math.ceil(Math.log(newSize)) * 2);
		pushIterations = Math.max(Math.min(newSize, 8), (int) Math.sqrt(newSize) / 8);

		K[] oldKeyTable = keyTable;
		V[] oldValueTable = valueTable;

		keyTable = (K[]) Array.newInstance(keyComponentType, newSize + stashCapacity);
		valueTable = (V[]) Array.newInstance(valueComponentType, newSize + stashCapacity);

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

	int hash2(int h) {
		h *= PRIME2;
		return (h ^ h >>> hashShift) & mask;
	}

	int hash3(int h) {
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
		if (!(o instanceof CollectionObjectMap<?, ?> other)) return false;
		if (other.size != size) return false;
		for (int i = 0, n = capacity + stashSize; i < n; i++) {
			K key = keyTable[i];
			if (key != null) {
				V value = valueTable[i];
				if (value == null) {
					if (!other.containsKey(key) || other.get(key) != null) return false;
				} else {
					if (!value.equals(other.get(key))) return false;
				}
			}
		}
		return true;
	}

	public String toString(String separator) {
		return toString(separator, false);
	}

	@Override
	public String toString() {
		return toString(", ", true);
	}

	public String toString(String separator, boolean braces) {
		if (size == 0) return braces ? "{}" : "";
		StringBuilder buffer = new StringBuilder(32);
		if (braces) buffer.append('{');
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
			buffer.append(separator);
			buffer.append(key);
			buffer.append('=');
			buffer.append(valueTable[i]);
		}
		if (braces) buffer.append('}');
		return buffer.toString();
	}

	/**
	 * Returns an iterator for the entries in the map. Remove is supported. Note that the same iterator instance is returned each
	 * time this method is called. Use the {@link Entries} constructor for nested or multithreaded iteration.
	 */
	@Override
	public Entries<K, V> iterator() {
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

	/// @deprecated see {@link #iterator()}.
	@Deprecated
	public Entries<K, V> entries() {
		return iterator();
	}

	/**
	 * Returns an iterator for the values in the map. Remove is supported. Note that the same iterator instance is returned each
	 * time this method is called. Use the {@link Values} constructor for nested or multithreaded iteration.
	 */
	@Override
	public Values<K, V> values() {
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

	@Override
	public Set<Entry<K, V>> entrySet() {
		return new EntrySet<>(this);
	}

	/**
	 * Returns an iterator for the keys in the map. Remove is supported. Note that the same iterator instance is returned each
	 * time this method is called. Use the {@link Keys} constructor for nested or multithreaded iteration.
	 */
	@Override
	public Keys<K, V> keySet() {
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

	public static class EntrySet<K, V> extends AbstractSet<Entry<K, V>> {
		final CollectionObjectMap<K, V> map;

		final MapItr itr = new MapItr();
		final MapEnt ent = new MapEnt();

		public EntrySet(CollectionObjectMap<K, V> m) {
			map = m;
		}

		@Override
		public int size() {
			return map.size;
		}

		@Override
		public void clear() {
			map.clear();
		}

		@Override
		public Iterator<Entry<K, V>> iterator() {
			itr.entries = map.iterator();
			return itr;
		}

		@Override
		public boolean contains(Object o) {
			if (!(o instanceof Entry<?, ?> e))
				return false;
			Object key = e.getKey();
			return map.containsKey(key);
		}

		@Override
		public boolean remove(Object o) {
			if (o instanceof Entry<?, ?> e) {
				Object key = e.getKey();
				return map.remove(key) != null;
			}
			return false;
		}

		class MapItr implements Iterator<Entry<K, V>> {
			Entries<K, V> entries;

			@Override
			public boolean hasNext() {
				return entries.hasNext();
			}

			@Override
			public Entry<K, V> next() {
				ent.entry = entries.next();
				return ent;
			}
		}

		class MapEnt implements Entry<K, V> {
			ObjectHolder<K, V> entry;

			@Override
			public K getKey() {
				return entry.key;
			}

			@Override
			public V getValue() {
				return entry.value;
			}

			@Override
			public V setValue(V value) {
				return map.put(entry.key, value);
			}
		}
	}

	abstract static class MapIterator<K, V, I> implements Iterable<I>, Iterator<I> {
		final CollectionObjectMap<K, V> map;

		public boolean hasNext;

		int nextIndex, currentIndex;
		boolean valid = true;

		public MapIterator(CollectionObjectMap<K, V> m) {
			map = m;
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

		@Override
		public void remove() {
			if (currentIndex < 0) throw new IllegalStateException("next must be called before remove.");
			if (currentIndex >= map.capacity) {
				map.removeStashIndex(currentIndex);
				nextIndex = currentIndex - 1;
				findNextIndex();
			} else {
				map.keyTable[currentIndex] = null;
				map.valueTable[currentIndex] = null;
			}
			currentIndex = -1;
			map.size--;
		}
	}

	public static class Entries<K, V> extends MapIterator<K, V, ObjectHolder<K, V>> {
		ObjectHolder<K, V> entry = new ObjectHolder<>();

		public Entries(CollectionObjectMap<K, V> map) {
			super(map);
		}

		/** Note the same entry instance is returned each time this method is called. */
		@Override
		public ObjectHolder<K, V> next() {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");

			entry.key = map.keyTable[nextIndex];
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
		public Entries<K, V> iterator() {
			return this;
		}
	}

	public static class Values<K, V> extends MapIterator<K, V, V> implements Collection<V> {
		public Values(CollectionObjectMap<K, V> map) {
			super(map);
		}

		@Override
		public boolean hasNext() {
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			return hasNext;
		}

		@Override
		public V next() {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			V value = map.valueTable[nextIndex];
			currentIndex = nextIndex;
			findNextIndex();
			return value;
		}

		@Override
		public int size() {
			return map.size;
		}

		@Override
		public boolean isEmpty() {
			return map.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return map.containsValue(o);
		}

		@Override
		public Values<K, V> iterator() {
			return this;
		}

		@Override
		public V[] toArray() {
			return Arrays.copyOf(map.valueTable, map.size);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T[] toArray(T[] a) {
			if (a.length < map.size)
				// Make a new array of a's runtime type, but my contents:
				return (T[]) Arrays.copyOf(map.valueTable, map.size, a.getClass());
			System.arraycopy(map.valueTable, 0, a, 0, map.size);
			if (a.length > map.size)
				a[map.size] = null;
			return a;
		}

		@Override
		public boolean add(V v) {
			return false;
		}

		@Override
		public boolean addAll(Collection<? extends V> c) {
			return false;
		}

		@Override
		public boolean remove(Object o) {
			if (o == null) {
				while (hasNext()) {
					if (next() == null) {
						remove();
						return true;
					}
				}
			} else {
				while (hasNext()) {
					if (o.equals(next())) {
						remove();
						return true;
					}
				}
			}
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object e : c)
				if (!contains(e))
					return false;
			return true;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			boolean modified = false;
			while (hasNext()) {
				if (c.contains(next())) {
					remove();
					modified = true;
				}
			}
			return modified;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			boolean modified = false;
			while (hasNext()) {
				if (!c.contains(next())) {
					remove();
					modified = true;
				}
			}
			return modified;
		}

		@Override
		public void clear() {
			map.clear();
		}

		/** Returns a new array containing the remaining values. */
		public Seq<V> toSeq() {
			return toSeq(new Seq<>(true, map.size, map.valueComponentType));
		}

		/** Adds the remaining values to the specified array. */
		public Seq<V> toSeq(Seq<V> array) {
			while (hasNext)
				array.add(next());
			return array;
		}
	}

	public static class Keys<K, V> extends MapIterator<K, V, K> implements Set<K> {
		public Keys(CollectionObjectMap<K, V> map) {
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
		public int hashCode() {
			int h = 0;
			for (K obj : this) {
				if (obj != null)
					h += obj.hashCode();
			}
			return h;
		}

		@Override
		public int size() {
			return map.size;
		}

		@Override
		public boolean isEmpty() {
			return map.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return map.containsKey(o);
		}

		@Override
		public Keys<K, V> iterator() {
			return this;
		}

		@Override
		public K[] toArray() {
			return toSeq().toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return toSeq().toArray(a.getClass().getComponentType());
		}

		@Override
		public boolean add(K k) {
			return false;
		}

		@Override
		public boolean remove(Object o) {
			return map.remove(o) != null;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object e : c)
				if (!contains(e))
					return false;
			return true;
		}

		@Override
		public boolean addAll(Collection<? extends K> c) {
			boolean modified = false;
			for (K e : c)
				if (add(e))
					modified = true;
			return modified;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			boolean modified = false;
			Iterator<K> it = iterator();
			while (it.hasNext()) {
				if (!c.contains(it.next())) {
					it.remove();
					modified = true;
				}
			}
			return modified;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			boolean modified = false;

			if (size() > c.size()) {
				for (Object e : c)
					modified |= remove(e);
			} else {
				for (Iterator<?> i = iterator(); i.hasNext(); ) {
					if (c.contains(i.next())) {
						i.remove();
						modified = true;
					}
				}
			}
			return modified;
		}

		@Override
		public void clear() {
			map.clear();
		}

		/** Returns a new array containing the remaining keys. */
		public Seq<K> toSeq() {
			return toSeq(new Seq<>(true, map.size, map.keyComponentType));
		}

		/** Adds the remaining keys to the array. */
		public Seq<K> toSeq(Seq<K> array) {
			while (hasNext)
				array.add(next());
			return array;
		}
	}
}
