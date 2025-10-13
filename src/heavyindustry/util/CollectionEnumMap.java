package heavyindustry.util;

import arc.func.Cons;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Reflect;
import heavyindustry.util.pair.ObjectPair;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class CollectionEnumMap<K extends Enum<K>, V> extends AbstractMap<K, V> implements Iterable<ObjectPair<K, V>>, Eachable<ObjectPair<K, V>>, Cloneable {
	public final Class<K> keyComponentType;
	public final Class<?> valueComponentType;

	public int size = 0;

	public K[] keyTable;
	public V[] valueTable;

	@SuppressWarnings("unchecked")
	public CollectionEnumMap(Class<K> keyType, Class<?> valueType) {
		keyComponentType = keyType;
		valueComponentType = valueType;

		keyTable = keyType.getEnumConstants();
		valueTable = (V[]) Reflect.newArray(valueType, keyTable.length);
	}

	@Override
	public void each(Cons<? super ObjectPair<K, V>> cons) {
		for (ObjectPair<K, V> entry : iterator()) {
			cons.get(entry);
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
		if (oldValue == null && value != null) size++;
		return oldValue;
	}

	@Override
	public V remove(Object key) {
		if (!isValidKey(key)) return null;

		int index = ((Enum<?>) key).ordinal();
		V oldValue = valueTable[index];
		valueTable[index] = null;
		if (oldValue != null)
			size--;
		return oldValue;
	}

	@Override
	public void clear() {
		Arrays.fill(valueTable, null);
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
			throw new RuntimeException(e);
		}
	}

	@Override
	public Entries<K, V> iterator() {
		return new Entries<>(this);
	}

	@Override
	public Keys<K, V> keySet() {
		return new Keys<>(this);
	}

	@Override
	public Values<K, V> values() {
		return new Values<>(this);
	}

	abstract static class MapIterator<K extends Enum<K>, V, I> implements Iterable<I>, Iterator<I> {
		final CollectionEnumMap<K, V> map;

		public boolean hasNext;

		int nextIndex, currentIndex;

		MapIterator(CollectionEnumMap<K, V> m) {
			map = m;
			reset();
		}

		public void reset() {
			currentIndex = -1;
			nextIndex = 0;
		}

		@Override
		public boolean hasNext() {
			while (nextIndex < map.valueTable.length && map.valueTable[nextIndex] == null)
				nextIndex++;
			return nextIndex != map.valueTable.length;
		}

		@Override
		public void remove() {
			if (currentIndex < 0) return;

			if (map.valueTable[currentIndex] != null) {
				map.valueTable[currentIndex] = null;
				map.size--;
			}
			currentIndex = -1;
		}
	}

	public static class EntrySet<K extends Enum<K>, V> extends AbstractSet<Entry<K, V>> {
		final CollectionEnumMap<K, V> map;

		final MapItr itr = new MapItr();
		final MapEnt ent = new MapEnt();

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

	public static class Entries<K extends Enum<K>, V> extends MapIterator<K, V, ObjectPair<K, V>> {
		ObjectPair<K, V> entry = new ObjectPair<>();

		public Entries(CollectionEnumMap<K, V> m) {
			super(m);
		}

		@Override
		public Iterator<ObjectPair<K, V>> iterator() {
			return this;
		}

		@Override
		public ObjectPair<K, V> next() {
			if (!hasNext) throw new NoSuchElementException();

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
			while (hasNext)
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
			while (hasNext)
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
}
