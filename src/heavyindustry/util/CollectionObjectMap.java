package heavyindustry.util;

import arc.struct.ObjectMap;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of Java Collection Framework Map based on {@link ObjectMap} wrapper,
 * used in places that require Java specifications and the feature of not creating nodes in ObjectiMap.
 */
@SuppressWarnings("unchecked")
public class CollectionObjectMap<K, V> implements Map<K, V> {
	protected ObjectMap<K, V> map;

	private final Set<K> keys = new AbstractSet<>() {
		@Override
		public int size() {
			return map.size;
		}

		@Override
		public void clear() {
			map.clear();
		}

		@Override
		public Iterator<K> iterator() {
			return map.keys();
		}

		@Override
		public boolean contains(Object o) {
			return containsKey(o);
		}

		@Override
		public boolean remove(Object key) {
			return CollectionObjectMap.this.remove(key) != null;
		}

		@Override
		public Object[] toArray() {
			return map.keys().toSeq().toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return map.keys().toSeq().toArray(a.getClass().getComponentType());
		}
	};

	private final Collection<V> values = new AbstractCollection<>() {
		@Override
		public Iterator<V> iterator() {
			return map.values();
		}

		@Override
		public int size() {
			return map.size;
		}

		@Override
		public void clear() {
			CollectionObjectMap.this.clear();
		}

		@Override
		public boolean contains(Object o) {
			return containsValue(o);
		}

		@Override
		public Object[] toArray() {
			return map.values().toSeq().toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return map.values().toSeq().toArray(a.getClass().getComponentType());
		}
	};

	private final Set<Entry<K, V>> entrySet = new AbstractSet<>() {
		private final Itr itr = new Itr();
		private final CollEnt ent = new CollEnt();

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
			return containsKey(key);
		}

		@Override
		public boolean remove(Object o) {
			if (o instanceof Map.Entry<?, ?> e) {
				Object key = e.getKey();
				return CollectionObjectMap.this.remove(key) != null;
			}
			return false;
		}

		class Itr implements Iterator<Entry<K, V>> {
			ObjectMap.Entries<K, V> entries;

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

		class CollEnt implements Entry<K, V> {
			ObjectMap.Entry<K, V> entry;

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
	};

	public CollectionObjectMap() {
		setMap(16, 0.75f);
	}

	public CollectionObjectMap(int capacity) {
		setMap(capacity, 0.75f);
	}

	public CollectionObjectMap(int capacity, float loadFactor) {
		setMap(capacity, loadFactor);
	}

	public CollectionObjectMap(ObjectMap<? extends K, ? extends V> map) {
		setMap(16, 0.75f);
		this.map.putAll(map);
	}

	protected void setMap(int capacity, float loadFactor) {
		map = new ObjectMap<>(capacity, loadFactor);
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
	public boolean containsKey(Object key) {
		return map.containsKey((K) key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value, false);
	}

	@Override
	public V get(Object key) {
		return map.get((K) key);
	}

	@Override
	public V getOrDefault(Object key, V defaultValue) {
		return map.get((K) key, defaultValue);
	}

	@Override
	public V put(K key, V value) {
		return map.put(key, value);
	}

	@Override
	public V remove(Object key) {
		return map.remove((K) key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		for (var set : m.entrySet()) {
			put(set.getKey(), set.getValue());
		}
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public Set<K> keySet() {
		return keys;
	}

	@Override
	public Collection<V> values() {
		return values;
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return entrySet;
	}
}
