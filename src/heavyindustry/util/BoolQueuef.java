package heavyindustry.util;

import java.util.NoSuchElementException;

/** Queue for booleans. */
public class BoolQueuef {
	/** Number of elements in the queue. */
	public int size = 0;
	/** Contains the values in the queue. Head and tail indices go in a circle around this array, wrapping at the end. */
	public boolean[] values;
	/** Index of first element. Logically smaller than tail. Unless empty, it points to a valid element inside queue. */
	protected int head = 0;
	/**
	 * Index of last element. Logically bigger than head. Usually points to an empty position, but points to the head when full
	 * (size == values.length).
	 */
	protected int tail = 0;

	/** Creates a new Queue which can hold 16 values without needing to resize backing array. */
	public BoolQueuef() {
		this(16);
	}

	/** Creates a new Queue which can hold the specified number of values without needing to resize backing array. */
	public BoolQueuef(int initialSize) {
		values = new boolean[initialSize];
	}

	/**
	 * Append given object to the tail. (enqueue to tail) Unless backing array needs resizing, operates in O(1) time.
	 *
	 * @param object can be null
	 */
	public void addLast(boolean object) {
		if (size == values.length) {
			resize(values.length << 1);// * 2
		}

		values[tail++] = object;
		if (tail == values.length) {
			tail = 0;
		}
		size++;
	}

	/**
	 * Prepend given object to the head. (enqueue to head) Unless backing array needs resizing, operates in O(1) time.
	 *
	 * @param object can be null
	 * @see #addLast(boolean)
	 */
	public void addFirst(boolean object) {
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
	public boolean[] shrink() {
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
	protected void resize(int newSize) {
		final boolean[] newArray = new boolean[newSize];
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
	public boolean removeFirst() {
		if (size == 0) {
			// Underflow
			throw new NoSuchElementException("Queue is empty.");
		}

		final boolean result = values[head];
		values[head] = false;
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
	public boolean removeLast() {
		if (size == 0) {
			throw new NoSuchElementException("Queue is empty.");
		}

		tail--;
		if (tail == -1) {
			tail = values.length - 1;
		}
		final boolean result = values[tail];
		values[tail] = false;
		size--;

		return result;
	}

	/**
	 * Returns the index of first occurrence of value in the queue, or -1 if no such value exists.
	 *
	 * @return An index of first occurrence of value in queue or -1 if no such value exists
	 */
	public int indexOf(boolean value) {
		if (size == 0) return -1;

		if (head < tail) {
			for (int i = head; i < tail; i++)
				if (values[i] == value) return i - head;
		} else {
			for (int i = head, n = values.length; i < n; i++)
				if (values[i] == value) return i - head;
			for (int i = 0; i < tail; i++)
				if (values[i] == value) return i + values.length - head;
		}
		return -1;
	}

	/**
	 * Removes the first instance of the specified value in the queue.
	 *
	 * @return true if value was found and removed, false otherwise
	 */
	public boolean removeValue(boolean value) {
		int index = indexOf(value);
		if (index == -1) return false;
		removeIndex(index);
		return true;
	}

	/** Removes and returns the item at the specified index. */
	public boolean removeIndex(int index) {
		if (index < 0) throw new IndexOutOfBoundsException("index can't be < 0: " + index);
		if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);

		index += head;
		boolean value;
		if (head < tail) { // index is between head and tail.
			value = values[index];
			System.arraycopy(values, index + 1, values, index, tail - index);
			tail--;
		} else if (index >= values.length) { // index is between 0 and tail.
			index -= values.length;
			value = values[index];
			System.arraycopy(values, index + 1, values, index, tail - index);
			tail--;
		} else { // index is between head and values.length.
			value = values[index];
			System.arraycopy(values, head, values, head + 1, index - head);
			head++;
			if (head == values.length) {
				head = 0;
			}
		}
		size--;
		return value;
	}

	/** Returns true if the queue is empty. */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns the first (head) item in the queue (without removing it).
	 *
	 * @throws NoSuchElementException when queue is empty
	 * @see #addFirst(boolean)
	 * @see #removeFirst()
	 */
	public boolean first() {
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
	 * @see #addLast(boolean)
	 * @see #removeLast()
	 */
	public boolean last() {
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
	public boolean get(int index) {
		if (index < 0) throw new IndexOutOfBoundsException("index can't be < 0: " + index);
		if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);

		int i = head + index;
		if (i >= values.length) {
			i -= values.length;
		}
		return values[i];
	}

	public void set(int index, boolean value) {
		if (index < 0) throw new IndexOutOfBoundsException("index can't be < 0: " + index);
		if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);

		int i = head + index;
		if (i >= values.length) {
			i -= values.length;
		}
		values[i] = value;
	}

	/** Removes all values from this queue; O(1). */
	public void clear() {
		if (size == 0) return;
		head = 0;
		tail = 0;
		size = 0;
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
}
