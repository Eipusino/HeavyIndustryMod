package heavyindustry.util;

import arc.util.ArcRuntimeException;
import heavyindustry.util.holder.ObjectHolder;

import java.util.NoSuchElementException;

/**
 * Implementation of Ordered Map based on {@code OrderedMap} wrapper for Java collection framework
 * used in places where Java specifications are required and OrderedMap does not create nodes.
 */
public class CollectionOrderedMap<K, V> extends CollectionObjectMap<K, V> {
	public CollectionList<K> orderedKeys;

	public CollectionOrderedMap(Class<?> keyType, Class<?> valueType) {
		super(keyType, valueType, 16, 0.75f);
		setMap(keyType, 16);
	}

	public CollectionOrderedMap(Class<?> keyType, Class<?> valueType, int capacity) {
		super(keyType, valueType, capacity, 0.75f);
		setMap(keyType, capacity);
	}

	public CollectionOrderedMap(Class<?> keyType, Class<?> valueType, int capacity, float loadFactor) {
		super(keyType, valueType, capacity, loadFactor);
		setMap(keyType, capacity);
	}

	public CollectionOrderedMap(CollectionOrderedMap<? extends K, ? extends V> map) {
		super(map);
		setMap(map.keyComponentType, 16);
		putAll(map);
	}

	protected void setMap(Class<?> keyType, int capacity) {
		orderedKeys = new CollectionList<>(true, capacity, keyType);
	}

	@Override
	public V put(K key, V value) {
		if (!containsKey(key)) orderedKeys.add(key);
		return super.put(key, value);
	}

	@Override
	public V remove(Object key) {
		orderedKeys.remove(key, false);
		return super.remove(key);
	}

	public V removeIndex(int index) {
		return super.remove(orderedKeys.remove(index));
	}

	@Override
	public void clear(int maximumCapacity) {
		orderedKeys.clear();
		super.clear(maximumCapacity);
	}

	@Override
	public void clear() {
		orderedKeys.clear();
		super.clear();
	}

	public CollectionList<K> orderedKeys() {
		return orderedKeys;
	}

	@Override
	public Entries<K, V> iterator() {
		if (entries1 == null) {
			entries1 = new OrderedMapEntries<>(this);
			entries2 = new OrderedMapEntries<>(this);
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
	 * Returns an iterator for the keys in the map. Remove is supported. Note that the same iterator instance is returned each
	 * time this method is called. Use the {@link OrderedMapKeys} constructor for nested or multithreaded iteration.
	 */
	@Override
	public Keys<K, V> keySet() {
		if (keys1 == null) {
			keys1 = new OrderedMapKeys<>(this);
			keys2 = new OrderedMapKeys<>(this);
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
			values1 = new OrderedMapValues<>(this);
			values2 = new OrderedMapValues<>(this);
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
	public String toString() {
		if (size == 0) return "{}";
		StringBuilder buffer = new StringBuilder(32);
		buffer.append('{');
		for (int i = 0, n = orderedKeys.size; i < n; i++) {
			K key = orderedKeys.get(i);
			if (i > 0) buffer.append(", ");
			buffer.append(key);
			buffer.append('=');
			buffer.append(get(key));
		}
		buffer.append('}');
		return buffer.toString();
	}

	public static class OrderedMapEntries<K, V> extends Entries<K, V> {
		protected CollectionList<K> keys;

		public OrderedMapEntries(CollectionOrderedMap<K, V> map) {
			super(map);
			keys = map.orderedKeys;
		}

		@Override
		public void reset() {
			nextIndex = 0;
			hasNext = map.size > 0;
		}

		@Override
		public ObjectHolder<K, V> next() {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			entry.key = keys.get(nextIndex);
			entry.value = map.get(entry.key);
			nextIndex++;
			hasNext = nextIndex < map.size;
			return entry;
		}

		@Override
		public void remove() {
			if (currentIndex < 0) throw new IllegalStateException("next must be called before remove.");
			map.remove(entry.key);
			nextIndex--;
		}
	}

	public static class OrderedMapKeys<K, V> extends Keys<K, V> {
		protected CollectionList<K> keys;

		public OrderedMapKeys(CollectionOrderedMap<K, V> map) {
			super(map);
			keys = map.orderedKeys;
		}

		@Override
		public void reset() {
			nextIndex = 0;
			hasNext = map.size > 0;
		}

		@Override
		public K next() {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			K key = keys.get(nextIndex);
			currentIndex = nextIndex;
			nextIndex++;
			hasNext = nextIndex < map.size;
			return key;
		}

		@Override
		public void remove() {
			if (currentIndex < 0) throw new IllegalStateException("next must be called before remove.");
			((CollectionOrderedMap<?, ?>) map).removeIndex(nextIndex - 1);
			nextIndex = currentIndex;
			currentIndex = -1;
		}
	}

	public static class OrderedMapValues<K, V> extends Values<K, V> {
		protected CollectionList<K> keys;

		public OrderedMapValues(CollectionOrderedMap<K, V> map) {
			super(map);
			keys = map.orderedKeys;
		}

		@Override
		public void reset() {
			nextIndex = 0;
			hasNext = map.size > 0;
		}

		@Override
		public V next() {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			V value = map.get(keys.get(nextIndex));
			currentIndex = nextIndex;
			nextIndex++;
			hasNext = nextIndex < map.size;
			return value;
		}

		@Override
		public void remove() {
			if (currentIndex < 0) throw new IllegalStateException("next must be called before remove.");
			((CollectionOrderedMap<?, ?>) map).removeIndex(currentIndex);
			nextIndex = currentIndex;
			currentIndex = -1;
		}
	}
}
