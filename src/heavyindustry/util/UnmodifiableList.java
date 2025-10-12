package heavyindustry.util;

import arc.func.Cons;
import arc.struct.Seq;
import arc.util.Eachable;
import heavyindustry.math.Mathm;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * The unmodifiable List class, Used to prevent elements in an array from being altered.
 *
 * @since 1.0.8
 */
public class UnmodifiableList<E> extends AbstractList<E> implements Eachable<E> {
	final E[] items;

	final int size;

	Iter<E> iterator1, iterator2;
	ListIter<E> listIterator1;

	/**
	 * @see #with(E[])
	 * @see #cpy(E[])
	 */
	public UnmodifiableList(E[] array) {
		items = array;
		size = array.length;
	}

	/** Directly use the given array. */
	@SafeVarargs
	public static <T> UnmodifiableList<T> with(T... array) {
		return new UnmodifiableList<>(array);
	}

	/** Clone an array instead of directly applying the original one. */
	public static <T> UnmodifiableList<T> cpy(T[] array) {
		return new UnmodifiableList<>(Arrays.copyOf(array, array.length));
	}

	@Override
	public void each(Cons<? super E> cons) {
		for (int i = 0; i < size; i++) {
			cons.get(items[i]);
		}
	}

	@Override
	public E get(int index) {
		if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
		return items[index];
	}

	@Override
	public int size() {
		return size;
	}

	/** Returns the element at the specified position in this list, but does not replace the element. */
	@Override
	public E set(int index, E element) {
		if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
		return items[index];
	}

	/** @return Does not support any change operations, always returns false. */
	@Override
	public boolean add(E e) {
		return false;
	}

	/** No modification operations are supported, nothing will happen. */
	@Override
	public void add(int index, E element) {}

	/** @return Does not support any change operations, always returns false. */
	@Override
	public boolean addAll(Collection<? extends E> c) {
		return false;
	}

	/** @return Does not support any change operations, always returns false. */
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		return false;
	}

	/** Returns the element at the specified position in this list, but does not delete the element. */
	@Override
	public E remove(int index) {
		if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
		return items[index];
	}

	/** @return Does not support any change operations, always returns false. */
	@Override
	public boolean remove(Object o) {
		return false;
	}

	/** @return Does not support any change operations, always returns false. */
	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	/** No modification operations are supported, nothing will happen. */
	@Override
	protected void removeRange(int fromIndex, int toIndex) {}

	/** @return Does not support any change operations, always returns false. */
	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	/** No modification operations are supported, nothing will happen. */
	@Override
	public void sort(Comparator<? super E> c) {}

	/** No modification operations are supported, nothing will happen. */
	@Override
	public void clear() {}

	@Override
	public int indexOf(Object o) {
		return ArrayUtils.indexOf(items, o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return ArrayUtils.lastIndexOf(items, o);
	}

	/** Returns the hash code value for this list. */
	@Override
	public int hashCode() {
		int h = 1;
		for (int i = 0; i < size; i++) {
			h *= 31;
			E item = items[i];
			if (item != null) h += item.hashCode();
		}
		return h;
	}

	/**
	 * Returns a string representation of this collection.  The string
	 * representation consists of a list of the collection's elements in the
	 * order they are returned by its iterator, enclosed in square brackets
	 * ({@code "[]"}).  Adjacent elements are separated by the characters
	 * {@code ", "} (comma and space).  Elements are converted to strings as
	 * by {@link String#valueOf(Object)}.
	 *
	 * @return a string representation of this collection
	 */
	@Override
	public String toString() {
		if (size == 0) return "[]";
		StringBuilder buffer = new StringBuilder(32);
		buffer.append('[');
		buffer.append(items[0]);
		for (int i = 1; i < size; i++) {
			buffer.append(", ");
			buffer.append(items[i]);
		}
		buffer.append(']');
		return buffer.toString();
	}

	/** A copy of this list element. */
	@Override
	public E[] toArray() {
		return Arrays.copyOf(items, items.length);
	}

	/** Convert this list to a {@code CollectionsList}. */
	public CollectionList<E> toList() {
		return CollectionList.with(items);
	}

	/** Convert this list to a {@code Seq}. */
	public Seq<E> toSeq() {
		return Seq.with(items);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return new SubList<>(this, fromIndex, toIndex);
	}

	@Override
	public Iterator<E> iterator() {
		if (iterator1 == null) iterator1 = new Iter<>(this);

		if (iterator1.done) {
			iterator1.cursor = 0;
			iterator1.done = false;
			return iterator1;
		}

		if (iterator2 == null) iterator2 = new Iter<>(this);

		if (iterator2.done) {
			iterator2.cursor = 0;
			iterator2.done = false;
			return iterator2;
		}
		//allocate new iterator in the case of 3+ nested loops.
		return new Iter<>(this);
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		if (listIterator1 == null) listIterator1 = new ListIter<>(this, index);

		if (listIterator1.done) {
			listIterator1.cursor = index;
			listIterator1.done = false;
			return listIterator1;
		}

		return new ListIter<>(this, index);
	}

	public static class Iter<E> implements Iterator<E> {
		UnmodifiableList<E> list;

		int cursor = 0;
		boolean done = true;

		public Iter(UnmodifiableList<E> ls) {
			list = ls;
		}

		@Override
		public boolean hasNext() {
			if (cursor >= list.size) done = true;
			return cursor < list.size;
		}

		@Override
		public E next() {
			if (cursor >= list.size) throw new NoSuchElementException(String.valueOf(cursor));
			return list.items[cursor++];
		}
	}

	public static class ListIter<E> extends Iter<E> implements ListIterator<E> {
		public ListIter(UnmodifiableList<E> ls, int index) {
			super(ls);
			cursor = Mathm.clamp(index, 0, list.size);
		}

		@Override
		public boolean hasPrevious() {
			return cursor > 0;
		}

		@Override
		public E previous() {
			return list.items[cursor - 1];
		}

		@Override
		public int nextIndex() {
			return cursor;
		}

		@Override
		public int previousIndex() {
			return cursor - 1;
		}

		@Override
		public void remove() {}

		@Override
		public void set(E e) {}

		@Override
		public void add(E e) {}
	}

	/** Sublist class, It also does not support any modification operations. */
	public static class SubList<T> extends AbstractList<T> implements Eachable<T> {
		final UnmodifiableList<T> parent;
		final int offset;
		final int size;

		public SubList(UnmodifiableList<T> array, int from, int to) {
			parent = array;
			offset = from;
			size = to - from;
		}

		@Override
		public T get(int index) {
			return parent.get(offset + index);
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public void each(Cons<? super T> cons) {
			parent.each(cons);
		}

		@Override
		public boolean add(T t) {
			return false;
		}

		@Override
		public void add(int index, T element) {}

		@Override
		public boolean addAll(int index, Collection<? extends T> c) {
			return false;
		}

		@Override
		public boolean addAll(Collection<? extends T> c) {
			return false;
		}

		@Override
		public T set(int index, T element) {
			return parent.get(index);
		}

		@Override
		public boolean remove(Object o) {
			return false;
		}

		@Override
		public T remove(int index) {
			return parent.remove(index);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return false;
		}

		@Override
		protected void removeRange(int fromIndex, int toIndex) {}

		@Override
		public boolean retainAll(Collection<?> c) {
			return false;
		}

		@Override
		public int indexOf(Object o) {
			return ArrayUtils.indexOf(parent.items, o, offset, size);
		}

		@Override
		public int lastIndexOf(Object o) {
			return ArrayUtils.lastIndexOf(parent.items, o);
		}

		@Override
		public int hashCode() {
			int h = 1;
			for (int i = offset; i < size; i++) {
				h *= 31;
				T item = parent.items[i];
				if (item != null) h += item.hashCode();
			}
			return h;
		}

		@Override
		public String toString() {
			if (size == 0) return "[]";
			StringBuilder buffer = new StringBuilder(32);
			buffer.append('[');
			buffer.append(parent.items[offset]);
			for (int i = offset + 1; i < size; i++) {
				buffer.append(", ");
				buffer.append(parent.items[i]);
			}
			buffer.append(']');
			return buffer.toString();
		}

		@Override
		public List<T> subList(int fromIndex, int toIndex) {
			int absoluteFromIndex = offset + fromIndex;
			int absoluteToIndex = offset + toIndex;

			return parent.subList(absoluteFromIndex, absoluteToIndex);
		}
	}
}
