package heavyindustry.util;

import arc.func.Cons;
import arc.struct.Seq;
import arc.util.Eachable;
import heavyindustry.math.Mathm;

import java.util.AbstractList;
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
	transient final E[] items;

	final int size;

	Itr iterator1, iterator2;
	ListItr listIterator1;

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

	@Override
	public E set(int index, E element) {
		if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
		return element;
	}

	@Override
	public boolean add(E e) {
		return false;
	}

	@Override
	public void add(int index, E element) {}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		return false;
	}

	@Override
	public E remove(int index) {
		return null;
	}

	@Override
	public boolean remove(Object o) {
		return false;
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
	public void sort(Comparator<? super E> c) {}

	@Override
	public void clear() {}

	@Override
	public int indexOf(Object o) {
		return Utils.indexOf(items, o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return Utils.lastIndexOf(items, o);
	}

	@Override
	public int hashCode() {
		int h = 1;
		for (int i = 0; i < size; i++) {
			h *= 31;
			Object item = items[i];
			if (item != null) h += item.hashCode();
		}
		return h;
	}

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

	@SafeVarargs
	public static <T> UnmodifiableList<T> with(T... array) {
		return new UnmodifiableList<>(array);
	}

	// Clone an array instead of directly applying the original one.
	public static <T> UnmodifiableList<T> cpy(T[] array) {
		return new UnmodifiableList<>(array.clone());
	}

	@Override
	public E[] toArray() {
		return items.clone();
	}

	public CollectionList<E> toList() {
		return CollectionList.with(items);
	}

	public Seq<E> toSeq() {
		return Seq.with(items);
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return new SubList<>(this, fromIndex, toIndex);
	}

	@Override
	public Iterator<E> iterator() {
		if (iterator1 == null) iterator1 = new Itr();

		if (iterator1.done) {
			iterator1.cursor = 0;
			iterator1.done = false;
			return iterator1;
		}

		if (iterator2 == null) iterator2 = new Itr();

		if (iterator2.done) {
			iterator2.cursor = 0;
			iterator2.done = false;
			return iterator2;
		}
		//allocate new iterator in the case of 3+ nested loops.
		return new Itr();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		if (listIterator1 == null) listIterator1 = new ListItr(index);

		if (listIterator1.done) {
			listIterator1.cursor = index;
			listIterator1.done = false;
			return listIterator1;
		}

		return new ListItr(index);
	}

	class Itr implements Iterator<E> {
		int cursor = 0;
		boolean done = true;

		Itr() {}

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

	class ListItr extends Itr implements ListIterator<E> {
		ListItr(int index) {
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
		public void remove() {}

		@Override
		public void set(E e) {}

		@Override
		public void add(E e) {}
	}

	static class SubList<T> extends UnmodifiableList<T> {
		final UnmodifiableList<T> parent;
		final int offset;
		final int subSize;

		public SubList(UnmodifiableList<T> array, int from, int to) {
			super(Utils.arrayOf());

			parent = array;
			offset = from;
			subSize = to - from;
		}

		@Override
		public T get(int index) {
			return parent.get(index);
		}

		@Override
		public int size() {
			return subSize;
		}

		@Override
		public void each(Cons<? super T> cons) {
			parent.each(cons);
		}

		@Override
		public int indexOf(Object o) {
			return parent.indexOf(o);
		}

		@Override
		public int lastIndexOf(Object o) {
			return parent.lastIndexOf(o);
		}

		@Override
		public int hashCode() {
			return parent.hashCode();
		}

		@Override
		public String toString() {
			return parent.toString();
		}
	}
}
