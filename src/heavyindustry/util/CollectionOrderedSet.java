package heavyindustry.util;

import arc.struct.Seq;

import java.util.NoSuchElementException;

public class CollectionOrderedSet<E> extends CollectionObjectSet<E> {
	public Seq<E> orderedItems;

	public CollectionOrderedSet(Class<?> type) {
		super(type);
		setSet(type, 16);
	}

	public CollectionOrderedSet(Class<?> type, int initialCapacity) {
		super(type, initialCapacity);
		setSet(type, initialCapacity);
	}

	public CollectionOrderedSet(Class<?> type, int initialCapacity, float loadFactor) {
		super(type, initialCapacity, loadFactor);
		setSet(type, initialCapacity);
	}

	public CollectionOrderedSet(CollectionObjectSet<? extends E> set) {
		super(set);
		setSet(set.componentType, set.capacity);
	}

	protected void setSet(Class<?> keyType, int capacity) {
		orderedItems = new Seq<>(true, capacity, keyType);
	}

	@Override
	public E first() {
		return orderedItems.first();
	}

	@Override
	public boolean add(E key) {
		if (!super.add(key)) return false;
		orderedItems.add(key);
		return true;
	}

	public boolean add(E key, int index) {
		if (!super.add(key)) {
			orderedItems.remove(key, true);
			orderedItems.insert(index, key);
			return false;
		}
		orderedItems.insert(index, key);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object key) {
		if (!super.remove(key)) return false;
		orderedItems.remove((E) key, false);
		return true;
	}

	public E removeIndex(int index) {
		E key = orderedItems.remove(index);
		super.remove(key);
		return key;
	}

	@Override
	public void clear(int maximumCapacity) {
		orderedItems.clear();
		super.clear(maximumCapacity);
	}

	@Override
	public void clear() {
		orderedItems.clear();
		super.clear();
	}

	public Seq<E> orderedItems() {
		return orderedItems;
	}

	@Override
	public CollectionOrderedSetIterator iterator() {
		if (iterator1 == null) {
			iterator1 = new CollectionOrderedSetIterator();
			iterator2 = new CollectionOrderedSetIterator();
		}

		if (iterator1.done) {
			iterator1.reset();
			return (CollectionOrderedSetIterator) iterator1;
		}

		if (iterator2.done) {
			iterator2.reset();
			return (CollectionOrderedSetIterator) iterator2;
		}

		return new CollectionOrderedSetIterator();
	}

	@Override
	public String toString() {
		if (size == 0) return "{}";
		E[] es = orderedItems.items;
		StringBuilder buffer = new StringBuilder(32);
		buffer.append('{');
		buffer.append(es[0]);
		for (int i = 1; i < size; i++) {
			buffer.append(", ");
			buffer.append(es[i]);
		}
		buffer.append('}');
		return buffer.toString();
	}

	@Override
	public String toString(String separator) {
		return orderedItems.toString(separator);
	}

	public class CollectionOrderedSetIterator extends CollectionObjectSetIterator {
		@Override
		public void reset() {
			super.reset();
			nextIndex = 0;
			hasNext = size > 0;
		}

		@Override
		public E next() {
			if (!hasNext) throw new NoSuchElementException();
			E key = orderedItems.get(nextIndex);
			nextIndex++;
			hasNext = nextIndex < size;
			return key;
		}

		@Override
		public void remove() {
			if (nextIndex < 0) throw new IllegalStateException("next must be called before remove.");
			nextIndex--;
			removeIndex(nextIndex);
		}
	}
}
