package heavyindustry.util;

import arc.func.Cons;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.ArcRuntimeException;
import arc.util.Eachable;
import heavyindustry.util.pair.ObjectPair;

import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * An ordered or unordered map of objects. This implementation uses arrays to store the keys and values, which means
 * {@link #getKey(Object, boolean) gets} do a comparison for each key in the map. This is slower than a typical hash map
 * implementation, but may be acceptable for small maps and has the benefits that keys and values can be accessed by index, which
 * makes iteration fast. Like {@link Seq}, if ordered is false, this class avoids a memory copy when removing elements (the last
 * element is moved to the removed element's position).
 *
 * @author Nathan Sweet
 * @author Eipusino
 */
public class CollectionArrayMap<K, V> implements Iterable<ObjectPair<K, V>>, Map<K, V>, Eachable<ObjectPair<K, V>>, Cloneable {
	public final Class<?> keyComponentType;
	public final Class<?> valueComponentType;

	public K[] keys;
	public V[] values;
	public int size;
	public boolean ordered;

	Entries<K, V> entries1, entries2;
	Values<V> valuesIter1, valuesIter2;
	Keys<K> keysIter1, keysIter2;

	/** Creates an ordered map with a capacity of 16. */
	public CollectionArrayMap(Class<?> keyType, Class<?> valueType) {
		this(true, 16, keyType, valueType);
	}

	/** Creates an ordered map with the specified capacity. */
	public CollectionArrayMap(int capacity, Class<?> keyType, Class<?> valueType) {
		this(true, capacity, keyType, valueType);
	}

	/**
	 * Creates a new map with {@link #keys} and {@link #values} of the specified type.
	 *
	 * @param ordered  If false, methods that remove elements may change the order of other elements in the arrays, which avoids a
	 *                 memory copy.
	 * @param capacity Any elements added beyond this will cause the backing arrays to be grown.
	 */
	@SuppressWarnings("unchecked")
	public CollectionArrayMap(boolean ordered, int capacity, Class<?> keyType, Class<?> valueType) {
		this.ordered = ordered;

		keyComponentType = keyType;
		valueComponentType = valueType;

		keys = (K[]) Array.newInstance(keyType, capacity);
		values = (V[]) Array.newInstance(valueType, capacity);
	}

	/**
	 * Creates a new map containing the elements in the specified map. The new map will have the same type of backing arrays and
	 * will be ordered if the specified map is ordered. The capacity is set to the number of elements, so any subsequent elements
	 * added will cause the backing arrays to be grown.
	 */
	public CollectionArrayMap(CollectionArrayMap<? extends K, ? extends V> array) {
		this(array.ordered, array.size, array.keyComponentType, array.valueComponentType);
		size = array.size;
		System.arraycopy(array.keys, 0, keys, 0, size);
		System.arraycopy(array.values, 0, values, 0, size);
	}

	@SuppressWarnings("unchecked")
	public CollectionArrayMap<K, V> copy() {
		try {
			CollectionArrayMap<K, V> map = (CollectionArrayMap<K, V>) super.clone();
			map.size = size;
			map.keys = Arrays.copyOf(keys, keys.length);
			map.values = Arrays.copyOf(values, values.length);

			map.entries1 = map.entries2 = null;
			map.keysIter1 = map.keysIter2 = null;
			map.valuesIter1 = map.valuesIter2 = null;
			return map;
		} catch (CloneNotSupportedException e) {
			return new CollectionArrayMap<>(this);
		}
	}

	@Override
	public void each(Cons<? super ObjectPair<K, V>> cons) {
		for (ObjectPair<K, V> entry : entries()) {
			cons.get(entry);
		}
	}

	@Override
	public V put(K key, V value) {
		if (key == null) return null;

		int index = indexOfKey(key);
		if (index == -1) {
			if (size == keys.length) resize(Math.max(8, (int) (size * 1.75f)));
			index = size++;
		}
		keys[index] = key;
		values[index] = value;
		return value;
	}

	@Override
	public V remove(Object key) {
		return removeKey(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (var e : m.entrySet()) {
			put(e.getKey(), e.getValue());
		}
	}

	public int put(K key, V value, int index) {
		int existingIndex = indexOfKey(key);
		if (existingIndex != -1)
			removeIndex(existingIndex);
		else if (size == keys.length) //
			resize(Math.max(8, (int) (size * 1.75f)));
		System.arraycopy(keys, index, keys, index + 1, size - index);
		System.arraycopy(values, index, values, index + 1, size - index);
		keys[index] = key;
		values[index] = value;
		size++;
		return index;
	}

	public void putAll(CollectionArrayMap<? extends K, ? extends V> map) {
		putAll(map, 0, map.size);
	}

	public void putAll(CollectionArrayMap<? extends K, ? extends V> map, int offset, int length) {
		if (offset + length > map.size)
			throw new IllegalArgumentException("offset + length must be <= size: " + offset + " + " + length + " <= " + map.size);
		int sizeNeeded = size + length - offset;
		if (sizeNeeded >= keys.length) resize(Math.max(8, (int) (sizeNeeded * 1.75f)));
		System.arraycopy(map.keys, offset, keys, size, length);
		System.arraycopy(map.values, offset, values, size, length);
		size += length;
	}

	/**
	 * Returns the value for the specified key. Note this does a .equals() comparison of each key in reverse order until the
	 * specified key is found.
	 */
	@Override
	public V get(Object key) {
		int i = size - 1;
		if (key == null) {
			for (; i >= 0; i--)
				if (keys[i] == null) return values[i];
		} else {
			for (; i >= 0; i--)
				if (key.equals(keys[i])) return values[i];
		}
		return null;
	}

	/**
	 * Returns the key for the specified value. Note this does a comparison of each value in reverse order until the specified
	 * value is found.
	 *
	 * @param identity If true, == comparison will be used. If false, .equals() comparison will be used.
	 */
	public K getKey(V value, boolean identity) {
		int i = size - 1;
		if (identity || value == null) {
			for (; i >= 0; i--)
				if (values[i] == value) return keys[i];
		} else {
			for (; i >= 0; i--)
				if (value.equals(values[i])) return keys[i];
		}
		return null;
	}

	public K getKeyAt(int index) {
		if (index >= size) throw new IndexOutOfBoundsException(String.valueOf(index));
		return keys[index];
	}

	public V getValueAt(int index) {
		if (index >= size) throw new IndexOutOfBoundsException(String.valueOf(index));
		return values[index];
	}

	public K firstKey() {
		if (size == 0) throw new IllegalStateException("Map is empty.");
		return keys[0];
	}

	public V firstValue() {
		if (size == 0) throw new IllegalStateException("Map is empty.");
		return values[0];
	}

	public void setKey(int index, K key) {
		if (index >= size) throw new IndexOutOfBoundsException(String.valueOf(index));
		keys[index] = key;
	}

	public void setValue(int index, V value) {
		if (index >= size) throw new IndexOutOfBoundsException(String.valueOf(index));
		values[index] = value;
	}

	public void insert(int index, K key, V value) {
		if (index > size) throw new IndexOutOfBoundsException(String.valueOf(index));
		if (size == keys.length) resize(Math.max(8, (int) (size * 1.75f)));
		if (ordered) {
			System.arraycopy(keys, index, keys, index + 1, size - index);
			System.arraycopy(values, index, values, index + 1, size - index);
		} else {
			keys[size] = keys[index];
			values[size] = values[index];
		}
		size++;
		keys[index] = key;
		values[index] = value;
	}

	@Override
	public boolean containsKey(Object key) {
		int i = size - 1;
		if (key == null) {
			while (i >= 0)
				if (keys[i--] == null) return true;
		} else {
			while (i >= 0)
				if (key.equals(keys[i--])) return true;
		}
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		return false;
	}

	/** @param identity If true, == comparison will be used. If false, .equals() comparison will be used. */
	public boolean containsValue(V value, boolean identity) {
		int i = size - 1;
		if (identity || value == null) {
			while (i >= 0)
				if (values[i--] == value) return true;
		} else {
			while (i >= 0)
				if (value.equals(values[i--])) return true;
		}
		return false;
	}

	public int indexOfKey(K key) {
		if (key == null) {
			for (int i = 0, n = size; i < n; i++)
				if (keys[i] == null) return i;
		} else {
			for (int i = 0, n = size; i < n; i++)
				if (key.equals(keys[i])) return i;
		}
		return -1;
	}

	public int indexOfValue(V value, boolean identity) {
		if (identity || value == null) {
			for (int i = 0, n = size; i < n; i++)
				if (values[i] == value) return i;
		} else {
			for (int i = 0, n = size; i < n; i++)
				if (value.equals(values[i])) return i;
		}
		return -1;
	}

	public V removeKey(Object key) {
		if (key == null) {
			for (int i = 0, n = size; i < n; i++) {
				if (keys[i] == null) {
					V value = values[i];
					removeIndex(i);
					return value;
				}
			}
		} else {
			for (int i = 0, n = size; i < n; i++) {
				if (key.equals(keys[i])) {
					V value = values[i];
					removeIndex(i);
					return value;
				}
			}
		}
		return null;
	}

	public boolean removeValue(V value, boolean identity) {
		if (identity || value == null) {
			for (int i = 0, n = size; i < n; i++) {
				if (values[i] == value) {
					removeIndex(i);
					return true;
				}
			}
		} else {
			for (int i = 0, n = size; i < n; i++) {
				if (value.equals(values[i])) {
					removeIndex(i);
					return true;
				}
			}
		}
		return false;
	}

	/** Removes and returns the key/values pair at the specified index. */
	public void removeIndex(int index) {
		if (index >= size) throw new IndexOutOfBoundsException(String.valueOf(index));
		size--;
		if (ordered) {
			System.arraycopy(keys, index + 1, keys, index, size - index);
			System.arraycopy(values, index + 1, values, index, size - index);
		} else {
			keys[index] = keys[size];
			values[index] = values[size];
		}
		keys[size] = null;
		values[size] = null;
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

	/** Returns the last key. */
	public K peekKey() {
		return keys[size - 1];
	}

	/** Returns the last value. */
	public V peekValue() {
		return values[size - 1];
	}

	/** Clears the map and reduces the size of the backing arrays to be the specified capacity if they are larger. */
	public void clear(int maximumCapacity) {
		if (keys.length <= maximumCapacity) {
			clear();
			return;
		}
		size = 0;
		resize(maximumCapacity);
	}

	@Override
	public void clear() {
		for (int i = 0, n = size; i < n; i++) {
			keys[i] = null;
			values[i] = null;
		}
		size = 0;
	}

	@Override
	public Set<K> keySet() {
		return keys();
	}

	/**
	 * Reduces the size of the backing arrays to the size of the actual number of entries. This is useful to release memory when
	 * many items have been removed, or if it is known that more entries will not be added.
	 */
	public void shrink() {
		if (keys.length == size) return;
		resize(size);
	}

	/**
	 * Increases the size of the backing arrays to accommodate the specified number of additional entries. Useful before adding
	 * many entries to avoid multiple backing array resizes.
	 */
	public void ensureCapacity(int additionalCapacity) {
		if (additionalCapacity < 0)
			throw new IllegalArgumentException("additionalCapacity must be >= 0: " + additionalCapacity);
		int sizeNeeded = size + additionalCapacity;
		if (sizeNeeded >= keys.length) resize(Math.max(8, sizeNeeded));
	}

	@SuppressWarnings("unchecked")
	protected void resize(int newSize) {
		K[] newKeys = (K[]) Array.newInstance(keys.getClass().getComponentType(), newSize);
		System.arraycopy(keys, 0, newKeys, 0, Math.min(size, newKeys.length));
		keys = newKeys;

		V[] newValues = (V[]) Array.newInstance(values.getClass().getComponentType(), newSize);
		System.arraycopy(values, 0, newValues, 0, Math.min(size, newValues.length));
		values = newValues;
	}

	public void reverse() {
		for (int i = 0, lastIndex = size - 1, n = size / 2; i < n; i++) {
			int ii = lastIndex - i;
			K tempKey = keys[i];
			keys[i] = keys[ii];
			keys[ii] = tempKey;

			V tempValue = values[i];
			values[i] = values[ii];
			values[ii] = tempValue;
		}
	}

	public void shuffle() {
		for (int i = size - 1; i >= 0; i--) {
			int ii = Mathf.random(i);
			K tempKey = keys[i];
			keys[i] = keys[ii];
			keys[ii] = tempKey;

			V tempValue = values[i];
			values[i] = values[ii];
			values[ii] = tempValue;
		}
	}

	/**
	 * Reduces the size of the arrays to the specified size. If the arrays are already smaller than the specified size, no action
	 * is taken.
	 */
	public void truncate(int newSize) {
		if (size <= newSize) return;
		for (int i = newSize; i < size; i++) {
			keys[i] = null;
			values[i] = null;
		}
		size = newSize;
	}

	@Override
	public int hashCode() {
		int h = 0;
		for (int i = 0, n = size; i < n; i++) {
			K key = keys[i];
			V value = values[i];
			if (key != null) h += key.hashCode() * 31;
			if (value != null) h += value.hashCode();
		}
		return h;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (!(obj instanceof CollectionArrayMap<?, ?> map) || map.keyComponentType != keyComponentType || map.valueComponentType != valueComponentType)
			return false;
		CollectionArrayMap<K, V> other = (CollectionArrayMap<K, V>) map;
		if (other.size != size) return false;
		for (int i = 0, n = size; i < n; i++) {
			K key = keys[i];
			V value = values[i];
			if (value == null) {
				if (!other.containsKey(key) || other.get(key) != null) return false;
			} else {
				if (!value.equals(other.get(key))) return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		if (size == 0) return "{}";
		StringBuilder buffer = new StringBuilder(32);
		buffer.append('{');
		buffer.append(keys[0]);
		buffer.append('=');
		buffer.append(values[0]);
		for (int i = 1; i < size; i++) {
			buffer.append(", ");
			buffer.append(keys[i]);
			buffer.append('=');
			buffer.append(values[i]);
		}
		buffer.append('}');
		return buffer.toString();
	}

	@Override
	public Iterator<ObjectPair<K, V>> iterator() {
		return entries();
	}

	/**
	 * Returns an iterator for the entries in the map. Remove is supported. Note that the same iterator instance is returned each
	 * time this method is called. Use the {@link Entries} constructor for nested or multithreaded iteration.
	 */
	public Entries<K, V> entries() {
		if (entries1 == null) {
			entries1 = new Entries<>(this);
			entries2 = new Entries<>(this);
		}
		if (!entries1.valid) {
			entries1.index = 0;
			entries1.valid = true;
			entries2.valid = false;
			return entries1;
		}
		entries2.index = 0;
		entries2.valid = true;
		entries1.valid = false;
		return entries2;
	}

	/**
	 * Returns an iterator for the values in the map. Remove is supported. Note that the same iterator instance is returned each
	 * time this method is called. Use the {@link Entries} constructor for nested or multithreaded iteration.
	 */
	@Override
	public Values<V> values() {
		if (valuesIter1 == null) {
			valuesIter1 = new Values<>(this);
			valuesIter2 = new Values<>(this);
		}
		if (!valuesIter1.valid) {
			valuesIter1.index = 0;
			valuesIter1.valid = true;
			valuesIter2.valid = false;
			return valuesIter1;
		}
		valuesIter2.index = 0;
		valuesIter2.valid = true;
		valuesIter1.valid = false;
		return valuesIter2;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return new MapEntrySet<>(this);
	}

	/**
	 * Returns an iterator for the keys in the map. Remove is supported. Note that the same iterator instance is returned each
	 * time this method is called. Use the {@link Entries} constructor for nested or multithreaded iteration.
	 */
	public Keys<K> keys() {
		if (keysIter1 == null) {
			keysIter1 = new Keys<>(this);
			keysIter2 = new Keys<>(this);
		}
		if (!keysIter1.valid) {
			keysIter1.index = 0;
			keysIter1.valid = true;
			keysIter2.valid = false;
			return keysIter1;
		}
		keysIter2.index = 0;
		keysIter2.valid = true;
		keysIter1.valid = false;
		return keysIter2;
	}

	public static class MapEntrySet<K, V> extends AbstractSet<Entry<K, V>> {
		final CollectionArrayMap<K, V> map;

		final MapItr itr = new MapItr();
		final MapEnt ent = new MapEnt();

		public MapEntrySet(CollectionArrayMap<K, V> map) {
			this.map = map;
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
			itr.entries = map.entries();
			return itr;
		}

		@Override
		public boolean contains(Object o) {
			if (!(o instanceof Map.Entry<?, ?> e))
				return false;
			Object key = e.getKey();
			return map.containsKey(key);
		}

		@Override
		public boolean remove(Object o) {
			if (o instanceof Map.Entry<?, ?> e) {
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
			ObjectPair<K, V> entry;

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

	public static class Entries<K, V> implements Iterable<ObjectPair<K, V>>, Iterator<ObjectPair<K, V>> {
		final CollectionArrayMap<K, V> map;

		ObjectPair<K, V> entry = new ObjectPair<>();
		int index;
		boolean valid = true;

		public Entries(CollectionArrayMap<K, V> map) {
			this.map = map;
		}

		@Override
		public boolean hasNext() {
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			return index < map.size;
		}

		@Override
		public Iterator<ObjectPair<K, V>> iterator() {
			return this;
		}

		/** Note the same entry instance is returned each time this method is called. */
		@Override
		public ObjectPair<K, V> next() {
			if (index >= map.size) throw new NoSuchElementException(String.valueOf(index));
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			entry.key = map.keys[index];
			entry.value = map.values[index++];
			return entry;
		}

		@Override
		public void remove() {
			index--;
			map.removeIndex(index);
		}

		public void reset() {
			index = 0;
		}
	}

	public static class Values<V> extends AbstractCollection<V> implements Iterator<V> {
		final CollectionArrayMap<?, V> map;

		int index;
		boolean valid = true;

		public Values(CollectionArrayMap<?, V> map) {
			this.map = map;
		}

		@Override
		public boolean hasNext() {
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			return index < map.size;
		}

		@Override
		public Iterator<V> iterator() {
			return this;
		}

		@Override
		public int size() {
			return map.size;
		}

		@Override
		public V next() {
			if (index >= map.size) throw new NoSuchElementException(String.valueOf(index));
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			return map.values[index++];
		}

		@Override
		public void remove() {
			index--;
			map.removeIndex(index);
		}

		public void reset() {
			index = 0;
		}

		public Seq<V> toSeq() {
			return new Seq<>(true, map.values, index, map.size - index);
		}

		public Seq<V> toSeq(Seq<V> array) {
			array.addAll(map.values, index, map.size - index);
			return array;
		}
	}

	public static class Keys<K> extends AbstractSet<K> implements Iterable<K>, Iterator<K> {
		final CollectionArrayMap<K, ?> map;

		int index;
		boolean valid = true;

		public Keys(CollectionArrayMap<K, ?> map) {
			this.map = map;
		}

		@Override
		public boolean hasNext() {
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			return index < map.size;
		}

		@Override
		public Iterator<K> iterator() {
			return this;
		}

		@Override
		public int size() {
			return map.size;
		}

		@Override
		public K next() {
			if (index >= map.size) throw new NoSuchElementException(String.valueOf(index));
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			return map.keys[index++];
		}

		@Override
		public boolean add(K k) {
			return false;
		}

		@Override
		public void remove() {
			index--;
			map.removeIndex(index);
		}

		public void reset() {
			index = 0;
		}

		public Seq<K> toSeq() {
			return new Seq<>(true, map.keys, index, map.size - index);
		}

		public Seq<K> toSeq(Seq<K> array) {
			array.addAll(map.keys, index, map.size - index);
			return array;
		}
	}
}
