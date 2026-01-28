package endfield.util;

import arc.func.Boolf;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

/**
 * An ordered and reconfigurable set based on {@link TreeSet}, adding elements to this set will insert them into positions of appropriate size.
 * According to the comparator, the elements in this set must be ordered.
 * <p>Different from {@link TreeSet}, this set allows multiple comparators to consider objects as equal.
 * <p>The insertion complexity is usually o (logn), but if the values compared by the comparator are concentrated on this set may degrade to o (n).
 * When traversing this set, the elements obtained are ordered.
 */
public class TreeSeq<E> implements Iterable<E> {
	protected final LinkedList<E> tmp = new LinkedList<>();

	protected Comparator<? super E> comparator;

	protected int size;

	protected TreeSet<LinkedList<E>> set;

	public TreeSeq(Comparator<? super E> comp) {
		comparator = comp;
		set = new TreeSet<>((a, b) -> comp.compare(a.getFirst(), b.getFirst()));
	}

	public TreeSeq() {
		set = new TreeSet<>();
	}

	public void add(E item) {
		tmp.clear();
		tmp.addFirst(item);
		LinkedList<E> e = set.ceiling(tmp);
		if (e == null || set.floor(tmp) != e) {
			e = new LinkedList<>();
			e.addFirst(item);
			set.add(e);
		} else {
			e.addFirst(item);
		}
		size++;
	}

	public boolean remove(E item) {
		tmp.clear();
		tmp.addFirst(item);

		LinkedList<E> e = set.ceiling(tmp);
		if (e != null && set.floor(tmp) == e) {
			if (e.size() == 1 && e.getFirst().equals(item)) set.remove(e);
			e.remove(item);
			size--;
			return true;
		}
		return false;
	}

	public int size() {
		return size;
	}

	public boolean removeIf(Boolf<E> boolf) {
		boolean test = false;
		TreeItr itr = iterator();
		E item;
		while (itr.hasNext()) {
			item = itr.next();
			if (boolf.get(item)) {
				itr.remove();
				size--;
				test = true;
			}
		}

		return test;
	}

	public void clear() {
		set.clear();
		size = 0;
	}

	public boolean isEmpty() {
		return set.isEmpty();
	}

	public E[] toArray(E[] arr) {
		E[] list = Arrays.copyOf(arr, size);
		int index = 0;
		for (E item : this) {
			list[index++] = item;
		}
		return list;
	}

	@Override
	public TreeItr iterator() {
		return new TreeItr();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("{");
		for (LinkedList<E> list : set) {
			builder.append(list).append(", ");
		}
		return builder.substring(0, builder.length() - 2) + "}";
	}

	public class TreeItr implements Iterator<E> {
		protected Iterator<LinkedList<E>> itr = set.iterator();
		protected Iterator<E> listItr;
		protected LinkedList<E> curr;

		@Override
		public boolean hasNext() {
			return (listItr != null && listItr.hasNext()) || (itr.hasNext() && (listItr = (curr = itr.next()).iterator()).hasNext());
		}

		@Override
		public E next() {
			return listItr.next();
		}

		@Override
		public void remove() {
			listItr.remove();
			if (curr.isEmpty()) itr.remove();
		}
	}
}
