package heavyindustry.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;

public class CircularQueue<T> {
	public final Class<?> componentType;

	protected final T[] queue;
	protected int head;
	protected int tail;
	protected final int capacity;

	@SuppressWarnings("unchecked")
	public CircularQueue(int cap, @NotNull Class<?> type) {
		componentType = type;
		capacity = cap;
		queue = (T[]) Array.newInstance(type, cap);
		head = -1;
		tail = -1;
	}

	public boolean isEmpty() {
		return head == -1;
	}

	public boolean isFull() {
		return ((tail + 1) % capacity) == head;
	}

	public boolean enqueueFront(T value) {
		if (isFull())
			return false;

		if (isEmpty())
			head = tail = 0;
		else
			head = (head - 1 + capacity) % capacity;

		queue[head] = value;
		return true;
	}

	public boolean enqueueRear(T value) {
		if (isFull())
			return false;

		if (isEmpty())
			head = 0;

		tail = (tail + 1) % capacity;
		queue[tail] = value;
		return true;
	}

	public T dequeueFront() {
		if (isEmpty())
			return null;

		T value = queue[head];
		if (head == tail)
			head = tail = -1;
		else
			head = (head + 1) % capacity;

		return value;
	}

	public T dequeueRear() {
		if (isEmpty())
			return null;

		T value = queue[tail];
		if (head == tail)
			head = tail = -1;
		else
			tail = (tail - 1 + capacity) % capacity;

		return value;
	}
}
