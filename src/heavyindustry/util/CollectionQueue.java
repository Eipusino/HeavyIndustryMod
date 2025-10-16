package heavyindustry.util;

import arc.func.Boolf;
import arc.func.Cons;
import arc.util.ArcRuntimeException;
import arc.util.Eachable;
import arc.util.Nullable;

import java.lang.reflect.Array;
import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A resizable, ordered array of objects with efficient add and remove at the beginning and end. Values in the backing array may
 * wrap back to the beginning, making add and remove at the beginning and end O(1) (unless the backing array needs to resize when
 * adding). Deque functionality is provided via {@link #removeLast()} and {@link #addFirst(Object)}.
 */
public class CollectionQueue<E> extends AbstractQueue<E> implements Eachable<E> {
	public final Class<E> componentType;

	/** Number of elements in the queue. */
	public int size = 0;
	/** Contains the values in the queue. Head and tail indices go in a circle around this array, wrapping at the end. */
	public E[] values;
	/** Index of first element. Logically smaller than tail. Unless empty, it points to a valid element inside queue. */
	protected int head = 0;
	/**
	 * Index of last element. Logically bigger than head. Usually points to an empty position, but points to the head when full
	 * (size == values.length).
	 */
	protected int tail = 0;

	@Nullable QueueIterable<E> iterable;

	/** Creates a new Queue which can hold 16 values without needing to resize backing array. */
	public CollectionQueue(Class<E> type) {
		this(16, type);
	}

	/**
	 * Creates a new Queue which can hold the specified number of values without needing to resize backing array. This creates
	 * backing array of the specified type via reflection, which is necessary only when accessing the backing array directly.
	 */
	@SuppressWarnings("unchecked")
	public CollectionQueue(int initialSize, Class<E> type) {
		componentType = type;

		values = (E[]) Array.newInstance(type, initialSize);
	}

	/**
	 * Append given object to the tail. (enqueue to tail) Unless backing array needs resizing, operates in O(1) time.
	 *
	 * @param object can be null
	 */
	public void addLast(E object) {
		if (size == values.length) {
			resize(values.length << 1);// * 2
		}

		values[tail++] = object;
		if (tail == values.length) {
			tail = 0;
		}
		size++;
	}

	/** Adds an object to the tail. */
	@Override
	public boolean add(E e) {
		addLast(e);
		return true;
	}

	@Override
	public boolean offer(E e) {
		return false;
	}

	@Override
	public E poll() {
		if (size < 1) return null;
		E value = values[0];

		remove(value);

		return value;
	}

	@Override
	public E element() {
		if (size < 1) throw new NoSuchElementException("this values is empty");

		return values[0];
	}

	@Override
	public E peek() {
		if (size < 1) return null;

		return values[0];
	}

	/**
	 * Prepend given object to the head. (enqueue to head) Unless backing array needs resizing, operates in O(1) time.
	 *
	 * @param object can be null
	 * @see #addLast(Object)
	 */
	public void addFirst(E object) {
		if (size == values.length) {
			resize(values.length << 1);// * 2
		}

		head--;
		if (head == -1) {
			head = values.length - 1;
		}
		values[head] = object;

		size++;
	}

	/**
	 * Reduces the size of the backing array to the size of the actual items. This is useful to release memory when many items
	 * have been removed, or if it is known that more items will not be added.
	 *
	 * @return {@link #values}
	 */
	public E[] shrink() {
		if (values.length != size) resize(size);
		return values;
	}

	/**
	 * Increases the size of the backing array to accommodate the specified number of additional items. Useful before adding many
	 * items to avoid multiple backing array resizes.
	 */
	public void ensureCapacity(int additional) {
		final int needed = size + additional;
		if (values.length < needed) {
			resize(needed);
		}
	}

	/** Resize backing array. newSize must be bigger than current size. */
	@SuppressWarnings("unchecked")
	protected void resize(int newSize) {
		final E[] newArray = (E[]) Array.newInstance(componentType, newSize);
		if (head < tail) {
			// Continuous
			System.arraycopy(values, head, newArray, 0, tail - head);
		} else if (size > 0) {
			// Wrapped
			final int rest = values.length - head;
			System.arraycopy(values, head, newArray, 0, rest);
			System.arraycopy(values, 0, newArray, rest, tail);
		}
		values = newArray;
		head = 0;
		tail = size;
	}

	/**
	 * Remove the first item from the queue. (dequeue from head) Always O(1).
	 *
	 * @return removed object
	 * @throws NoSuchElementException when queue is empty
	 */
	public E removeFirst() {
		if (size == 0) {
			// Underflow
			throw new NoSuchElementException("Queue is empty.");
		}

		final E result = values[head];
		values[head] = null;
		head++;
		if (head == values.length) {
			head = 0;
		}
		size--;

		return result;
	}

	/**
	 * Remove the last item from the queue. (dequeue from tail) Always O(1).
	 *
	 * @return removed object
	 * @throws NoSuchElementException when queue is empty
	 * @see #removeFirst()
	 */
	public E removeLast() {
		if (size == 0) {
			throw new NoSuchElementException("Queue is empty.");
		}

		tail--;
		if (tail == -1) {
			tail = values.length - 1;
		}
		final E result = values[tail];
		values[tail] = null;
		size--;

		return result;
	}

	@Override
	public boolean contains(Object value) {
		return contains(value, true);
	}

	public boolean contains(Object value, boolean identity) {
		return indexOf(value, identity) != -1;
	}

	/**
	 * Returns the index of first occurrence of value in the queue, or -1 if no such value exists.
	 *
	 * @param identity If true, == comparison will be used. If false, .equals() comparison will be used.
	 * @return An index of first occurrence of value in queue or -1 if no such value exists
	 */
	public int indexOf(Object value, boolean identity) {
		if (size == 0) return -1;
		if (identity || value == null) {
			if (head < tail) {
				for (int i = head; i < tail; i++)
					if (values[i] == value) return i - head;
			} else {
				for (int i = head, n = values.length; i < n; i++)
					if (values[i] == value) return i - head;
				for (int i = 0; i < tail; i++)
					if (values[i] == value) return i + values.length - head;
			}
		} else {
			if (head < tail) {
				for (int i = head; i < tail; i++)
					if (value.equals(values[i])) return i - head;
			} else {
				for (int i = head, n = values.length; i < n; i++)
					if (value.equals(values[i])) return i - head;
				for (int i = 0; i < tail; i++)
					if (value.equals(values[i])) return i + values.length - head;
			}
		}
		return -1;
	}

	public int indexOf(Boolf<E> value) {
		if (size == 0) return -1;
		if (head < tail) {
			for (int i = head; i < tail; i++)
				if (value.get(values[i])) return i - head;
		} else {
			for (int i = head, n = values.length; i < n; i++)
				if (value.get(values[i])) return i - head;
			for (int i = 0; i < tail; i++)
				if (value.get(values[i])) return i + values.length - head;
		}
		return -1;
	}

	public boolean remove(Boolf<E> value) {
		int i = indexOf(value);
		if (i != -1) {
			removeIndex(i);
			return true;
		}
		return false;
	}

	@Override
	public boolean remove(Object value) {
		return remove(value, false);
	}

	/**
	 * Removes the first instance of the specified value in the queue.
	 *
	 * @param identity If true, == comparison will be used. If false, .equals() comparison will be used.
	 * @return true if value was found and removed, false otherwise
	 */
	public boolean remove(Object value, boolean identity) {
		int index = indexOf(value, identity);
		if (index == -1) return false;
		removeIndex(index);
		return true;
	}

	/** Removes and returns the item at the specified index. */
	public E removeIndex(int index) {
		if (index < 0) throw new IndexOutOfBoundsException("index can't be < 0: " + index);
		if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);

		index += head;
		E value;
		if (head < tail) { // index is between head and tail.
			value = values[index];
			System.arraycopy(values, index + 1, values, index, tail - index);
			values[tail] = null;
			tail--;
		} else if (index >= values.length) { // index is between 0 and tail.
			index -= values.length;
			value = values[index];
			System.arraycopy(values, index + 1, values, index, tail - index);
			tail--;
		} else { // index is between head and values.length.
			value = values[index];
			System.arraycopy(values, head, values, head + 1, index - head);
			values[head] = null;
			head++;
			if (head == values.length) {
				head = 0;
			}
		}
		size--;
		return value;
	}

	@Override
	public int size() {
		return size;
	}

	/** Returns true if the queue is empty. */
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns the first (head) item in the queue (without removing it).
	 *
	 * @throws NoSuchElementException when queue is empty
	 * @see #addFirst(Object)
	 * @see #removeFirst()
	 */
	public E first() {
		if (size == 0) {
			// Underflow
			throw new NoSuchElementException("Queue is empty.");
		}
		return values[head];
	}

	/**
	 * Returns the last (tail) item in the queue (without removing it).
	 *
	 * @throws NoSuchElementException when queue is empty
	 * @see #addLast(Object)
	 * @see #removeLast()
	 */
	public E last() {
		if (size == 0) {
			// Underflow
			throw new NoSuchElementException("Queue is empty.");
		}
		tail--;
		if (tail == -1) {
			tail = values.length - 1;
		}
		return values[tail];
	}

	/**
	 * Retrieves the value in queue without removing it. Indexing is from the front to back, zero based. Therefore get(0) is the
	 * same as {@link #first()}.
	 *
	 * @throws IndexOutOfBoundsException when the index is negative or >= size
	 */
	public E get(int index) {
		if (index < 0) throw new IndexOutOfBoundsException("index can't be < 0: " + index);
		if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);

		int i = head + index;
		if (i >= values.length) {
			i -= values.length;
		}
		return values[i];
	}

	/**
	 * Removes all values from this queue. Values in backing array are set to null to prevent memory leak, so this operates in
	 * O(n).
	 */
	@Override
	public void clear() {
		if (size == 0) return;

		if (head < tail) {
			// Continuous
			for (int i = head; i < tail; i++) {
				values[i] = null;
			}
		} else {
			// Wrapped
			for (int i = head; i < values.length; i++) {
				values[i] = null;
			}
			for (int i = 0; i < tail; i++) {
				values[i] = null;
			}
		}
		head = 0;
		tail = 0;
		size = 0;
	}

	/**
	 * Returns an iterator for the items in the queue. Remove is supported. Note that the same iterator instance is returned each
	 * time this method is called. Use the constructor for nested or multithreaded iteration.
	 */
	@Override
	public Iterator<E> iterator() {
		if (iterable == null) iterable = new QueueIterable<>(this);
		return iterable.iterator();
	}

	@SuppressWarnings("unchecked")
	@Override
	public E[] toArray() {
		E[] out = (E[]) Array.newInstance(componentType, size);
		for (int i = 0; i < size; i++) {
			out[i] = get(i);
		}
		return out;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		final int size;
		if ((size = size()) > a.length)
			return toArray((Class<T[]>) a.getClass());
		final E[] es = values;
		for (int i = head, j = 0, len = Math.min(size, es.length - i);
				; i = 0, len = tail) {
			System.arraycopy(es, i, a, j, len);
			if ((j += len) == size) break;
		}
		if (size < a.length)
			a[size] = null;
		return a;
	}

	<T> T[] toArray(Class<T[]> c) {
		final E[] es = values;
		final T[] a;
		final int end;
		if ((end = tail + ((head <= tail) ? 0 : es.length)) >= 0) {
			// Uses null extension feature of copyOfRange
			a = Arrays.copyOfRange(es, head, end, c);
		} else {
			// integer overflow!
			a = Arrays.copyOfRange(es, 0, end - head, c);
			System.arraycopy(es, head, a, 0, es.length - head);
		}
		if (end != tail)
			System.arraycopy(es, 0, a, es.length - head, tail);
		return a;
	}

	@Override
	public void each(Cons<? super E> c) {
		for (int i = 0; i < size; i++) {
			c.get(get(i));
		}
	}

	@Override
	public String toString() {
		if (size == 0) {
			return "[]";
		}

		StringBuilder sb = new StringBuilder(64);
		sb.append('[');
		sb.append(values[head]);
		for (int i = (head + 1) % values.length; i != tail; i = (i + 1) % values.length) {
			sb.append(", ").append(values[i]);
		}
		sb.append(']');
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int backingLength = values.length;
		int index = head;

		int hash = size + 1;
		for (int s = 0; s < size; s++) {
			final E value = values[index];

			hash *= 31;
			if (value != null) hash += value.hashCode();

			index++;
			if (index == backingLength) index = 0;
		}

		return hash;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof CollectionQueue<?> other)) return false;

		if (other.size != size) return false;

		int index = head;
		int itsIndex = other.head;
		for (int s = 0; s < size; s++) {
			E myValue = values[index];
			Object itsValue = other.values[itsIndex];

			if (!(myValue == null ? itsValue == null : myValue.equals(itsValue))) return false;
			index++;
			itsIndex++;
			if (index == values.length) index = 0;
			if (itsIndex == other.values.length) itsIndex = 0;
		}
		return true;
	}

	public static class QueueIterable<T> implements Iterable<T> {
		final CollectionQueue<T> queue;
		final boolean allowRemove;
		private QueueIterator iterator1, iterator2;

		public QueueIterable(CollectionQueue<T> queue) {
			this(queue, true);
		}

		public QueueIterable(CollectionQueue<T> queue, boolean allowRemove) {
			this.queue = queue;
			this.allowRemove = allowRemove;
		}

		@Override
		public Iterator<T> iterator() {
			if (iterator1 == null) {
				iterator1 = new QueueIterator();
				iterator2 = new QueueIterator();
			}

			if (iterator1.done) {
				iterator1.index = 0;
				iterator1.done = false;
				return iterator1;
			}

			if (iterator2.done) {
				iterator2.index = 0;
				iterator2.done = false;
				return iterator2;
			}
			//allocate new iterator in the case of 3+ nested loops.
			return new QueueIterator();
		}

		class QueueIterator implements Iterator<T>, Iterable<T> {
			int index;
			boolean done = true;

			QueueIterator() {}

			@Override
			public boolean hasNext() {
				if (index >= queue.size) done = true;
				return index < queue.size;
			}

			@Override
			public T next() {
				if (index >= queue.size) throw new NoSuchElementException(String.valueOf(index));
				return queue.get(index++);
			}

			@Override
			public void remove() {
				if (!allowRemove) throw new ArcRuntimeException("Remove not allowed.");
				index--;
				queue.removeIndex(index);
			}

			public void reset() {
				index = 0;
			}

			@Override
			public Iterator<T> iterator() {
				return this;
			}
		}
	}
}
