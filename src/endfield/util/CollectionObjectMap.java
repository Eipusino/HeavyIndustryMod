package endfield.util;

import arc.func.Cons;
import arc.func.Cons2;
import arc.func.Prov;
import arc.math.Mathf;
import arc.util.ArcRuntimeException;
import arc.util.Eachable;
import endfield.math.Mathm;
import endfield.util.holder.ObjectHolder;

import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import static endfield.util.Constant.PRIME2;
import static endfield.util.Constant.PRIME3;

/**
 * Implementation of Java Collection Framework Map based on {@code ObjectMap} wrapper,
 * used in places that require Java specifications and the feature of not creating nodes in ObjectMap.
 * <p><strong>It is not recommended to use primitive types.</strong>
 *
 * @author Eipusino
 */
public class CollectionObjectMap<K, V> extends AbstractMap<K, V> implements Iterable<ObjectHolder<K, V>>, Eachable<ObjectHolder<K, V>>, Cloneable {
	public int size;

	public final Class<?> keyComponentType;
	public final Class<?> valueComponentType;

	public K[] keyTable;
	public V[] valueTable;

	//public V nullValue;
	//public boolean hasNullValue;

	public int capacity, stashSize;

	protected float loadFactor;
	protected int hashShift, mask, threshold;
	protected int stashCapacity;
	protected int pushIterations;

	protected transient Entries entries1, entries2;
	protected transient Values values1, values2;
	protected transient Keys keys1, keys2;

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
		pushIterations = Mathm.clamp(capacity, 8, (int) Math.sqrt(capacity) / 8);

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

	public CollectionObjectMap(Map<? extends K, ? extends V> map, Class<?> keyType, Class<?> valueType) {
		this(keyType, valueType, map.size());
		putAll(map);
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
			out.keyTable = keyTable.clone();
			out.valueTable = valueTable.clone();

			out.entries1 = out.entries2 = null;
			out.values1 = out.values2 = null;
			out.keys1 = out.keys2 = null;

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
		if (entry == null) return null;

		return put(entry.getKey(), entry.getValue());
	}

	@SuppressWarnings("unchecked")
	public void putAll(Object... values) {
		for (int i = 0; i < values.length / 2; i++) {
			put((K) values[i * 2], (V) values[i * 2 + 1]);
		}
	}

	/** Put all the keys of this other map into this map, and return this map for chaining. */
	public CollectionObjectMap<K, V> merge(CollectionObjectMap<? extends K, ? extends V> map) {
		putAll(map);
		return this;
	}

	/** Skips checks for existing keys. */
	protected void putResize(K key, V value) {
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

	protected void push(K insertKey, V insertValue, int index1, K key1, int index2, K key2, int index3, K key3) {
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

	protected void putStash(K key, V value) {
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
		V value = get(key);
		if (value == null) {
			put(key, value = supplier.get());
		}
		return value;
	}

	/** Returns the value for the specified key, or null if the key is not in the map. */
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

	/** Returns the value for the specified key, or the default value if the key is not in the map. */
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

	protected V getStash(Object key, V defaultValue) {
		for (int i = capacity, n = i + stashSize; i < n; i++)
			if (key.equals(keyTable[i])) return valueTable[i];
		return defaultValue;
	}

	/** Returns the value associated with the key, or null. */
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
		ensureCapacity(m.size());
		for (ObjectHolder<K, V> holder : iterator()) {
			put(holder.getKey(), holder.getValue());
		}
	}

	protected V removeStash(Object key) {
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

	protected void removeStashIndex(int index) {
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

	protected boolean containsKeyStash(Object key) {
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
	protected void resize(int newSize) {
		int oldEndIndex = capacity + stashSize;

		capacity = newSize;
		threshold = (int) (newSize * loadFactor);
		mask = newSize - 1;
		hashShift = 31 - Integer.numberOfTrailingZeros(newSize);
		stashCapacity = Math.max(3, (int) Math.ceil(Math.log(newSize)) * 2);
		pushIterations = Mathm.clamp(newSize, 8, (int) Math.sqrt(newSize) / 8);

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
	public Entries iterator() {
		return entries();
	}

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
	 * time this method is called. Use the {@link Values} constructor for nested or multithreaded iteration.
	 */
	@Override
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

	@Override
	public EntrySet entrySet() {
		return new EntrySet();
	}

	/**
	 * Returns an iterator for the keys in the map. Remove is supported. Note that the same iterator instance is returned each
	 * time this method is called. Use the {@link Keys} constructor for nested or multithreaded iteration.
	 */
	@Override
	public Keys keySet() {
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

	public class EntrySet extends AbstractSet<Entry<K, V>> {
		protected final MapItr itr = new MapItr();
		protected final MapEnt ent = new MapEnt();

		@Override
		public int size() {
			return size;
		}

		@Override
		public void clear() {
			CollectionObjectMap.this.clear();
		}

		@Override
		public Iterator<Entry<K, V>> iterator() {
			itr.entries = CollectionObjectMap.this.iterator();
			return itr;
		}

		@Override
		public boolean contains(Object o) {
			if (o instanceof Entry<?, ?> e) {
				Object key = e.getKey();
				return containsKey(key);
			}
			return false;
		}

		@Override
		public boolean remove(Object o) {
			if (o instanceof Entry<?, ?> e) {
				Object key = e.getKey();
				return CollectionObjectMap.this.remove(key) != null;
			}
			return false;
		}

		protected class MapItr implements Iterator<Entry<K, V>> {
			Entries entries;

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

		protected class MapEnt implements Entry<K, V> {
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
				return put(entry.key, value);
			}
		}
	}

	protected abstract class MapIterator<I> extends AbstractSet<I> implements Iterable<I>, Iterator<I> {
		public boolean hasNext;

		public int nextIndex, currentIndex;
		public boolean valid = true;

		public MapIterator() {
			reset();
		}

		public void reset() {
			currentIndex = -1;
			nextIndex = -1;
			findNextIndex();
		}

		protected void findNextIndex() {
			hasNext = false;
			K[] keyTable = CollectionObjectMap.this.keyTable;
			for (int n = capacity + stashSize; ++nextIndex < n; ) {
				if (keyTable[nextIndex] != null) {
					hasNext = true;
					break;
				}
			}
		}

		@Override
		public void remove() {
			if (currentIndex < 0) throw new IllegalStateException("next must be called before remove.");
			if (currentIndex >= capacity) {
				removeStashIndex(currentIndex);
				nextIndex = currentIndex - 1;
				findNextIndex();
			} else {
				keyTable[currentIndex] = null;
				valueTable[currentIndex] = null;
			}
			currentIndex = -1;
			size--;
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public boolean isEmpty() {
			return CollectionObjectMap.this.isEmpty();
		}

		@Override
		public boolean remove(Object o) {
			return CollectionObjectMap.this.remove(o) != null;
		}

		@Override
		public boolean add(I i) {
			return false;
		}

		@Override
		public boolean addAll(Collection<? extends I> c) {
			return false;
		}

		@Override
		public Object[] toArray() {
			return toList().toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return toList().toArray(a);
		}

		@Override
		public void clear() {
			CollectionObjectMap.this.clear();
		}

		/** Returns a new array containing the remaining keys. */
		public abstract CollectionList<I> toList();

		/** Adds the remaining keys to the array. */
		public CollectionList<I> toList(CollectionList<I> array) {
			while (hasNext)
				array.add(next());
			return array;
		}
	}

	public class Entries extends MapIterator<ObjectHolder<K, V>> {
		protected ObjectHolder<K, V> entry = new ObjectHolder<>();

		/** Note the same entry instance is returned each time this method is called. */
		@Override
		public ObjectHolder<K, V> next() {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");

			entry.key = keyTable[nextIndex];
			entry.value = valueTable[nextIndex];

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
		public Entries iterator() {
			return this;
		}

		@Override
		public CollectionList<ObjectHolder<K, V>> toList() {
			return toList(new CollectionList<>(true, size, ObjectHolder.class));
		}
	}

	public class Values extends MapIterator<V> implements Collection<V> {
		@Override
		public boolean hasNext() {
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			return hasNext;
		}

		@Override
		public V next() {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			V value = valueTable[nextIndex];
			currentIndex = nextIndex;
			findNextIndex();
			return value;
		}

		@Override
		public boolean contains(Object o) {
			return containsValue(o);
		}

		@Override
		public Values iterator() {
			return this;
		}

		@Override
		public CollectionList<V> toList() {
			return toList(new CollectionList<>(true, size, valueComponentType));
		}
	}

	public class Keys extends MapIterator<K> implements Set<K> {
		@Override
		public boolean hasNext() {
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			return hasNext;
		}

		@Override
		public K next() {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			K key = keyTable[nextIndex];
			currentIndex = nextIndex;
			findNextIndex();
			return key;
		}

		@Override
		public int hashCode() {
			int hashCode = 0;
			for (K obj : this) {
				if (obj != null)
					hashCode += obj.hashCode();
			}
			return hashCode;
		}

		@Override
		public boolean contains(Object o) {
			return containsKey(o);
		}

		@Override
		public Keys iterator() {
			return this;
		}

		@Override
		public CollectionList<K> toList() {
			return toList(new CollectionList<>(true, size, keyComponentType));
		}
	}
}
