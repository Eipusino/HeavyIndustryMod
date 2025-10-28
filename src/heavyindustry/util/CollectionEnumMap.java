package heavyindustry.util;

import arc.func.Cons;
import arc.struct.Seq;
import arc.util.Eachable;
import heavyindustry.util.holder.ObjectHolder;

import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * {@code Map} based on enumeration index. allow null values.
 *
 * @since 1.0.8
 */
public class CollectionEnumMap<K extends Enum<K>, V> extends AbstractMap<K, V> implements Iterable<ObjectHolder<K, V>>, Eachable<ObjectHolder<K, V>>, Cloneable {
	public final Class<K> keyComponentType;
	public final Class<?> valueComponentType;

	/** Indicates the number of non-{@code null} values in the {@link #valueTable}. */
	public int size = 0;

	public K[] keyTable; // Do not modify
	public V[] valueTable;

	public boolean[] enableTable;

	protected Entries<K, V> entries1, entries2;
	protected Values<K, V> values1, values2;
	protected Keys<K, V> keys1, keys2;

	@SuppressWarnings("unchecked")
	public CollectionEnumMap(Class<K> keyType, Class<?> valueType) {
		keyComponentType = keyType;
		valueComponentType = valueType;

		keyTable = keyType.getEnumConstants();
		valueTable = (V[]) Array.newInstance(valueType, keyTable.length);
		enableTable = new boolean[keyTable.length];
	}

	@Override
	public void each(Cons<? super ObjectHolder<K, V>> cons) {
		for (ObjectHolder<K, V> entry : iterator()) {
			cons.get(entry);
		}
	}

	public void eachValue(Cons<? super V> cons) {
		for (int i = 0; i < valueTable.length; i++) {
			if (enableTable[i]) {
				V value = valueTable[i];
				cons.get(value);
			}
		}
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean containsKey(Object key) {
		if (key == null) return false;

		for (K k : keyTable) {
			if (key.equals(k)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		if (value == null) {
			for (V v : valueTable) {
				if (v == null) {
					return true;
				}
			}
		} else {
			for (V v : valueTable) {
				if (value.equals(v)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public V get(Object key) {
		return isValidKey(key) ? valueTable[((Enum<?>) key).ordinal()] : null;
	}

	@Override
	public V put(K key, V value) {
		int index = key.ordinal();
		V oldValue = valueTable[index];
		valueTable[index] = value;
		enableTable[index] = true;

		resize();

		return oldValue;
	}

	@Override
	public V remove(Object key) {
		if (!isValidKey(key)) return null;

		int index = ((Enum<?>) key).ordinal();
		V oldValue = valueTable[index];
		valueTable[index] = null;
		enableTable[index] = false;

		resize();

		return oldValue;
	}

	protected void resize() {
		size = 0;

		for (boolean enable : enableTable) {
			if (enable)
				size++;
		}
	}

	@Override
	public void clear() {
		Arrays.fill(valueTable, null);
		Arrays.fill(enableTable, false);
		size = 0;
	}

	@Override
	public EntrySet<K, V> entrySet() {
		return new EntrySet<>(this);
	}

	public boolean isValidKey(Object key) {
		if (key == null) return false;

		// Cheaper than instanceof Enum followed by getDeclaringClass
		Class<?> keyClass = key.getClass();
		return keyClass == keyComponentType || keyClass.getSuperclass() == keyComponentType;
	}

	@SuppressWarnings("unchecked")
	public CollectionEnumMap<K, V> copy() {
		try {
			CollectionEnumMap<K, V> map = (CollectionEnumMap<K, V>) super.clone();
			map.valueTable = map.valueTable.clone();
			//map.entrySet = null;
			return map;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException("oh no", e);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;

		if (o instanceof CollectionEnumMap<?, ?> map) {
			if (map.keyComponentType != keyComponentType) return false;

			// Key types match, compare each value
			for (int i = 0; i < keyTable.length; i++) {
				V ourValue = valueTable[i];
				Object hisValue = map.valueTable[i];
				if (hisValue != ourValue && (hisValue == null || !hisValue.equals(ourValue)))
					return false;
			}
			return true;
		}

		return false;
	}

	/**
	 * Returns the hash code value for this map.  The hash code of a map is
	 * defined to be the sum of the hash codes of each entry in the map.
	 */
	public int hashCode() {
		int h = 0;

		for (int i = 0; i < keyTable.length; i++) {
			if (enableTable[i] && valueTable[i] != null) {
				h += entryHashCode(i);
			}
		}

		return h;
	}

	protected int entryHashCode(int index) {
		return keyTable[index].hashCode() ^ valueTable[index].hashCode();
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
			if (enableTable[i]) continue;
			buffer.append(keyTable[i]);
			buffer.append('=');
			buffer.append(valueTable[i]);
			break;
		}
		while (i-- > 0) {
			if (enableTable[i]) continue;
			buffer.append(separator);
			buffer.append(keyTable[i]);
			buffer.append('=');
			buffer.append(valueTable[i]);
		}
		if (braces) buffer.append('}');
		return buffer.toString();
	}

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

	protected abstract static class MapIterator<K extends Enum<K>, V, I> implements Iterable<I>, Iterator<I> {
		protected final CollectionEnumMap<K, V> map;

		protected int nextIndex, currentIndex;

		protected boolean valid = true;

		protected MapIterator(CollectionEnumMap<K, V> m) {
			map = m;
			reset();
		}

		public void reset() {
			currentIndex = -1;
			nextIndex = 0;
		}

		@Override
		public boolean hasNext() {
			while (nextIndex < map.valueTable.length && map.enableTable[nextIndex])
				nextIndex++;
			return valid && nextIndex != map.valueTable.length;
		}

		@Override
		public void remove() {
			if (currentIndex < 0) return;

			map.valueTable[currentIndex] = null;
			map.enableTable[currentIndex] = false;
			map.resize();

			currentIndex = -1;
		}
	}

	public static class Entries<K extends Enum<K>, V> extends MapIterator<K, V, ObjectHolder<K, V>> {
		protected ObjectHolder<K, V> entry = new ObjectHolder<>();

		public Entries(CollectionEnumMap<K, V> m) {
			super(m);
		}

		@Override
		public Iterator<ObjectHolder<K, V>> iterator() {
			return this;
		}

		@Override
		public ObjectHolder<K, V> next() {
			if (!hasNext()) throw new NoSuchElementException();

			int index = nextIndex++;
			entry.key = map.keyTable[index];
			entry.value = map.valueTable[index];
			return entry;
		}
	}

	public static class Keys<K extends Enum<K>, V> extends MapIterator<K, V, K> implements Set<K> {
		public Keys(CollectionEnumMap<K, V> m) {
			super(m);
		}

		@Override
		public int size() {
			return map.size;
		}

		@Override
		public boolean isEmpty() {
			return map.size == 0;
		}

		@Override
		public boolean contains(Object o) {
			return map.containsKey(o);
		}

		@Override
		public K[] toArray() {
			return Arrays.copyOf(map.keyTable, map.keyTable.length);
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
			int oldSize = map.size;
			map.remove(o);
			return map.size != oldSize;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			for (Object o : c) {
				if (contains(o)) return true;
			}

			return false;
		}

		@Override
		public boolean addAll(Collection<? extends K> c) {
			return false;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return false;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return false;
		}

		@Override
		public void clear() {
			map.clear();
		}

		@Override
		public Iterator<K> iterator() {
			return this;
		}

		@Override
		public K next() {
			if (!hasNext()) throw new NoSuchElementException();
			currentIndex = nextIndex++;
			return map.keyTable[currentIndex];
		}

		/** Returns a new array containing the remaining keys. */
		public Seq<K> toSeq() {
			return toSeq(new Seq<>(true, map.size, map.keyComponentType));
		}

		/** Adds the remaining keys to the array. */
		public Seq<K> toSeq(Seq<K> array) {
			while (hasNext())
				array.add(next());
			return array;
		}
	}

	public static class Values<K extends Enum<K>, V> extends MapIterator<K, V, V> implements Collection<V> {
		public Values(CollectionEnumMap<K, V> m) {
			super(m);
		}

		@Override
		public int size() {
			return map.size;
		}

		@Override
		public boolean isEmpty() {
			return map.size == 0;
		}

		@Override
		public boolean contains(Object o) {
			return map.containsValue(o);
		}

		@Override
		public V[] toArray() {
			return toSeq().toArray(map.valueComponentType);
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return toSeq().toArray(a.getClass().getComponentType());
		}

		@Override
		public boolean add(V v) {
			return false;
		}

		@Override
		public boolean remove(Object o) {
			return map.remove(o) != null;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return false;
		}

		@Override
		public boolean addAll(Collection<? extends V> c) {
			return false;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			for (Object o : c) {
				if (remove(o)) return true;
			}

			return false;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return false;
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
			while (hasNext())
				array.add(next());
			return array;
		}

		@Override
		public Iterator<V> iterator() {
			return this;
		}

		@Override
		public V next() {
			if (!hasNext()) throw new NoSuchElementException();
			currentIndex = nextIndex++;
			return map.valueTable[currentIndex];
		}
	}

	public static class EntrySet<K extends Enum<K>, V> extends AbstractSet<Entry<K, V>> {
		protected final CollectionEnumMap<K, V> map;

		protected final MapItr itr = new MapItr();
		protected final MapEnt ent = new MapEnt();

		public EntrySet(CollectionEnumMap<K, V> m) {
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

		protected class MapItr implements Iterator<Entry<K, V>> {
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
				return map.put(entry.key, value);
			}
		}
	}
}
