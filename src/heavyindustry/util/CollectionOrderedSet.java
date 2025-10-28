package heavyindustry.util;

import java.util.NoSuchElementException;

/**
 * An {@link CollectionObjectSet} that also stores keys in an {@link CollectionList} using the insertion order. {@link #iterator() Iteration} is
 * ordered and faster than an unordered set. Keys can also be accessed and the order changed using {@link #orderedItems()}. There
 * is some additional overhead for put and remove. When used for faster iteration versus ObjectSet and the order does not actually
 * matter, copying during remove can be greatly reduced by setting {@link CollectionList#ordered} to false for
 * {@link CollectionOrderedSet#orderedItems()}.
 *
 * @author Nathan Sweet
 * @author Eipusino
 */
public class CollectionOrderedSet<E> extends CollectionObjectSet<E> {
	public CollectionList<E> orderedItems;

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
		setSet(set.keyComponentType, set.capacity);
	}

	protected void setSet(Class<?> keyType, int capacity) {
		orderedItems = new CollectionList<>(true, capacity, keyType);
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

	public CollectionList<E> orderedItems() {
		return orderedItems;
	}

	@Override
	public Iter<E> iterator() {
		if (iterator1 == null) iterator1 = new OrderedIter<>(this);

		if (iterator1.done) {
			iterator1.reset();
			return iterator1;
		}

		if (iterator2 == null) iterator2 = new OrderedIter<>(this);

		if (iterator2.done) {
			iterator2.reset();
			return iterator2;
		}

		return new OrderedIter<>(this);
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

	public static class OrderedIter<E> extends Iter<E> {
		protected final CollectionOrderedSet<E> set;

		public OrderedIter(CollectionOrderedSet<E> s) {
			super(s);
			set = s;
		}

		@Override
		public void reset() {
			super.reset();
			nextIndex = 0;
			hasNext = set.size > 0;
		}

		@Override
		public E next() {
			if (!hasNext) throw new NoSuchElementException();
			E key = set.orderedItems.get(nextIndex);
			nextIndex++;
			hasNext = nextIndex < set.size;
			return key;
		}

		@Override
		public void remove() {
			if (nextIndex < 0) throw new IllegalStateException("next must be called before remove.");
			nextIndex--;
			set.removeIndex(nextIndex);
		}
	}
}
