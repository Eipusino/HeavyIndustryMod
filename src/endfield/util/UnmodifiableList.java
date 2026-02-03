package endfield.util;

import arc.func.Cons;
import arc.util.Eachable;
import endfield.math.Mathm;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * The unmodifiable List class, Used to prevent elements in an array from being altered.
 *
 * @since 1.0.8
 */
public class UnmodifiableList<E> extends AbstractList<E> implements Iterable<E>, Eachable<E> {
	final E[] items;

	final int size;

	transient Iter iterator1, iterator2;
	transient ListIter listIterator1;

	public UnmodifiableList(E[] array) {
		items = array;
		size = array.length;
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

	public boolean any() {
		return size > 0;
	}

	public E first() {
		return items[0];
	}

	/** Returns the element at the specified position in this list, but does not replace the element. */
	@Override
	public E set(int index, E element) {
		if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
		return items[index];
	}

	/** Returns the element at the specified position in this list, but does not delete the element. */
	@Override
	public E remove(int index) {
		if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
		return items[index];
	}

	@Override
	public boolean contains(Object o) {
		return contains(o, false);
	}

	public boolean contains(Object o, boolean identity) {
		int i = size - 1;
		if (identity || o == null) {
			while (i >= 0)
				if (items[i--] == o) return true;
		} else {
			while (i >= 0)
				if (o.equals(items[i--])) return true;
		}
		return false;
	}

	@Override
	public int indexOf(Object o) {
		return Arrays2.indexOf(items, o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return Arrays2.lastIndexOf(items, o);
	}

	/** Returns the hash code value for this list. */
	@Override
	public int hashCode() {
		int hashCode = 1;
		for (int i = 0; i < size; i++) {
			E item = items[i];
			hashCode = 31 * hashCode + (item == null ? 0 : item.hashCode());
		}
		return hashCode;
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
	public E @NotNull [] toArray() {
		return items.clone();
	}

	@SuppressWarnings("unchecked")
	public <T> T @NotNull [] toArray(Class<?> type) {
		T[] result = (T[]) Array.newInstance(type, size);
		System.arraycopy(items, 0, result, 0, size);
		return result;
	}

	@Override
	public <T> T @NotNull [] toArray(T[] a) {
		if (a.length < size) {
			// Make a new array of a's runtime type, but my contents:
			return toArray(a.getClass().getComponentType());
		}

		System.arraycopy(items, 0, a, 0, size);
		if (a.length > size)
			a[size] = null;
		return a;
	}

	/** Convert this list to a {@code CollectionsList}. */
	public CollectionList<E> toList() {
		return CollectionList.with(items);
	}

	@Override
	public @NotNull List<E> subList(int fromIndex, int toIndex) {
		return new SubList<>(this, fromIndex, toIndex);
	}

	@Override
	public @NotNull Iterator<E> iterator() {
		if (iterator1 == null) iterator1 = new Iter();

		if (iterator1.done) {
			iterator1.cursor = 0;
			iterator1.done = false;
			return iterator1;
		}

		if (iterator2 == null) iterator2 = new Iter();

		if (iterator2.done) {
			iterator2.cursor = 0;
			iterator2.done = false;
			return iterator2;
		}
		//allocate new iterator in the case of 3+ nested loops.
		return new Iter();
	}

	@Override
	public @NotNull ListIterator<E> listIterator(int index) {
		if (listIterator1 == null) listIterator1 = new ListIter(index);

		if (listIterator1.done) {
			listIterator1.cursor = index;
			listIterator1.done = false;
			return listIterator1;
		}

		return new ListIter(index);
	}

	public class Iter implements Iterator<E> {
		int cursor = 0;
		boolean done = true;

		public Iter() {}

		@Override
		public boolean hasNext() {
			if (cursor >= size) done = true;
			return cursor < size;
		}

		@Override
		public E next() {
			if (cursor >= size) throw new NoSuchElementException(String.valueOf(cursor));
			return items[cursor++];
		}
	}

	public class ListIter extends Iter implements ListIterator<E> {
		public ListIter(int index) {
			cursor = Mathm.clamp(index, 0, size);
		}

		@Override
		public boolean hasPrevious() {
			return cursor > 0;
		}

		@Override
		public E previous() {
			return items[cursor - 1];
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
		public void remove() {
			throw new UnsupportedOperationException("remove");
		}

		@Override
		public void set(E e) {
			throw new UnsupportedOperationException("set");
		}

		@Override
		public void add(E e) {
			throw new UnsupportedOperationException("add");
		}
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
		public int indexOf(Object o) {
			return Arrays2.indexOf(parent.items, o, offset, size);
		}

		@Override
		public int lastIndexOf(Object o) {
			return Arrays2.lastIndexOf(parent.items, o);
		}

		@Override
		public int hashCode() {
			int hashCode = 1;
			for (int i = offset; i < size; i++) {
				T item = parent.items[i];
				hashCode = 31 * hashCode + (item == null ? 0 : item.hashCode());
			}
			return hashCode;
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
		public @NotNull List<T> subList(int fromIndex, int toIndex) {
			int absoluteFromIndex = offset + fromIndex;
			int absoluteToIndex = offset + toIndex;

			return parent.subList(absoluteFromIndex, absoluteToIndex);
		}
	}
}
