package heavyindustry.util;

import arc.util.ArcRuntimeException;
import heavyindustry.util.ref.ObjectBoolHolder;

import java.util.NoSuchElementException;

public class ObjectBoolOrderedMap<K> extends ObjectBoolMap<K> {
	public CollectionList<K> orderedKeys;

	public ObjectBoolOrderedMap(Class<?> keyType) {
		super(keyType);
		setMap(keyType, capacity);
	}

	public ObjectBoolOrderedMap(Class<?> keyType, int initialCapacity) {
		super(keyType, initialCapacity);
		setMap(keyType, capacity);
	}

	public ObjectBoolOrderedMap(Class<?> keyType, int initialCapacity, float loadFactor) {
		super(keyType, initialCapacity, loadFactor);
		setMap(keyType, capacity);
	}

	public ObjectBoolOrderedMap(ObjectBoolMap<? extends K> map) {
		super(map);
		setMap(map.keyComponentType, 16);
	}

	protected void setMap(Class<?> keyType, int capacity) {
		orderedKeys = new CollectionList<>(true, capacity, keyType);
	}

	@Override
	public void put(K key, boolean value) {
		if (!containsKey(key)) orderedKeys.add(key);
		super.put(key, value);
	}

	@Override
	public boolean remove(K key) {
		orderedKeys.remove(key, false);
		return super.remove(key);
	}

	public boolean removeIndex(int index) {
		return super.remove(orderedKeys.remove(index));
	}

	@Override
	public void clear() {
		orderedKeys.clear();
		super.clear();
	}

	@Override
	public void clear(int maximumCapacity) {
		orderedKeys.clear();
		super.clear(maximumCapacity);
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
	public Keys keys() {
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
		public ObjectBoolHolder<K> next() {
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
			ObjectBoolOrderedMap.this.remove(entry.key);
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
		public boolean next() {
			if (!hasNext) throw new NoSuchElementException();
			if (!valid) throw new ArcRuntimeException("#iterator() cannot be used nested.");
			boolean value = get(orderedKeys.get(nextIndex));
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
