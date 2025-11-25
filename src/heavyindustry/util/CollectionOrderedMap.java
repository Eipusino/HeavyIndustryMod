package heavyindustry.util;

import arc.util.ArcRuntimeException;
import heavyindustry.util.concurrent.holder.ObjectHolder;

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
	public Entries iterator() {
		if (entries1 == null) {
			entries1 = new OrderedMapEntries();
			entries2 = new OrderedMapEntries();
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
	public Keys keySet() {
		if (keys1 == null) {
			keys1 = new OrderedMapKeys();
			keys2 = new OrderedMapKeys();
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
	public Values values() {
		if (values1 == null) {
			values1 = new OrderedMapValues();
			values2 = new OrderedMapValues();
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

	public class OrderedMapEntries extends Entries {
		@Override
		public void reset() {
			nextIndex = 0;
			hasNext = size > 0;
		}

		@Override
		public ObjectHolder<K, V> next() {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			entry.key = orderedKeys.get(nextIndex);
			entry.value = get(entry.key);
			nextIndex++;
			hasNext = nextIndex < size;
			return entry;
		}

		@Override
		public void remove() {
			if (currentIndex < 0) throw new IllegalStateException("next must be called before remove.");
			CollectionOrderedMap.this.remove(entry.key);
			nextIndex--;
		}
	}

	public class OrderedMapKeys extends Keys {
		@Override
		public void reset() {
			nextIndex = 0;
			hasNext = size > 0;
		}

		@Override
		public K next() {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			K key = orderedKeys.get(nextIndex);
			currentIndex = nextIndex;
			nextIndex++;
			hasNext = nextIndex < size;
			return key;
		}

		@Override
		public void remove() {
			if (currentIndex < 0) throw new IllegalStateException("next must be called before remove.");
			removeIndex(nextIndex - 1);
			nextIndex = currentIndex;
			currentIndex = -1;
		}
	}

	public class OrderedMapValues extends Values {
		@Override
		public void reset() {
			nextIndex = 0;
			hasNext = size > 0;
		}

		@Override
		public V next() {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			V value = get(orderedKeys.get(nextIndex));
			currentIndex = nextIndex;
			nextIndex++;
			hasNext = nextIndex < size;
			return value;
		}

		@Override
		public void remove() {
			if (currentIndex < 0) throw new IllegalStateException("next must be called before remove.");
			removeIndex(currentIndex);
			nextIndex = currentIndex;
			currentIndex = -1;
		}
	}
}
