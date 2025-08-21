package heavyindustry.util;

import arc.func.Prov;
import arc.struct.ObjectMap;

import java.util.Iterator;

public class ObjectTable<K, V> implements Iterable<ObjectTable.Entry<K, V>> {
	protected final ObjectMap<K, V> map12 = new ObjectMap<>();
	protected final ObjectMap<V, K> map21 = new ObjectMap<>();

	public int size() {
		return map12.size;
	}

	public K getKey(V key) {
		return map21.get(key);
	}

	public K getKey(V key, Prov<Entry<K, V>> supplier) {
		K res = getKey(key);
		if (res == null) {
			put(supplier.get());
			return getKey(key);
		}
		return res;
	}

	public V getValue(K key) {
		return map12.get(key);
	}

	public V getValue(K key, Prov<Entry<K, V>> supplier) {
		V res = getValue(key);
		if (res == null) {
			put(supplier.get());
			return getValue(key);
		}
		return res;
	}

	public void put(K a1, V a2) {
		if (map12.containsKey(a1)) return;
		map12.put(a1, a2);
		map21.put(a2, a1);
	}

	public void put(Entry<K, V> entry) {
		put(entry.key, entry.value);
	}

	public void removeByKey(K arg) {
		if (!map12.containsKey(arg)) return;
		map12.remove(arg);
		map21.remove(getValue(arg));
	}

	public void removeByValue(V arg) {
		if (!map21.containsKey(arg)) return;
		map21.remove(arg);
		map12.remove(getKey(arg));
	}

	@SuppressWarnings("unchecked")
	public Object get(Object key, Prov<Entry<K, V>> supplier, boolean argT1) {
		return argT1 ? getValue((K) key, supplier) : getKey((V) key, supplier);
	}

	public boolean containsKey(K arg) {
		return map12.containsKey(arg);
	}

	public boolean containsValue(V arg) {
		return map21.containsKey(arg);
	}

	@Override
	public Iterator<Entry<K, V>> iterator() {
		return new TableIterator<>(map12);
	}

	@Override
	public String toString() {
		return "ObjectTable{" + map12 +
				", size=" + size() +
				'}';
	}

	public void clear() {
		map12.clear();
		map21.clear();
	}

	public static class Entry<K, V> {
		public final K key;
		public final V value;

		public Entry(K a1, V a2) {
			key = a1;
			value = a2;
		}
	}

	public static class TableIterator<K, V> implements Iterator<Entry<K, V>> {
		protected final ObjectMap.Entries<K, V> iterator;

		public TableIterator(ObjectMap<K, V> map) {
			iterator = map.iterator();
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext;
		}

		@Override
		public Entry<K, V> next() {
			ObjectMap.Entry<K, V> next = iterator.next();
			return new Entry<>(next.key, next.value);
		}

		@Override
		public void remove() {
			iterator.remove();
		}
	}
}
