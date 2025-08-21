package heavyindustry.util;

import arc.func.Func;

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
public class TreeSeq<T> implements Iterable<T> {
	private final LinkedList<T> tmp = new LinkedList<>();

	Comparator<T> comparator;

	int size;

	TreeSet<LinkedList<T>> set;

	public TreeSeq(Comparator<T> comp) {
		comparator = comp;
		set = new TreeSet<>((a, b) -> comp.compare(a.getFirst(), b.getFirst()));
	}

	public TreeSeq() {
		set = new TreeSet<>();
	}

	public void add(T item) {
		tmp.clear();
		tmp.addFirst(item);
		LinkedList<T> t = set.ceiling(tmp);
		if (t == null || set.floor(tmp) != t) {
			t = new LinkedList<>();
			t.addFirst(item);
			set.add(t);
		} else {
			t.addFirst(item);
		}
		size++;
	}

	public boolean remove(T item) {
		tmp.clear();
		tmp.addFirst(item);

		LinkedList<T> t = set.ceiling(tmp);
		if (t != null && set.floor(tmp) == t) {
			if (t.size() == 1 && t.getFirst().equals(item)) set.remove(t);
			t.remove(item);
			size--;
			return true;
		}
		return false;
	}

	public int size() {
		return size;
	}

	public boolean removeIf(Func<T, Boolean> boolf) {
		boolean test = false;
		TreeItr itr = iterator();
		T item;
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

	public T[] toArray(T[] arr) {
		T[] list = Arrays.copyOf(arr, size);
		int index = 0;
		for (T item : this) {
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
		for (LinkedList<T> list : set) {
			builder.append(list).append(", ");
		}
		return builder.substring(0, builder.length() - 2) + "}";
	}

	public class TreeItr implements Iterator<T> {
		Iterator<LinkedList<T>> itr = set.iterator();
		Iterator<T> listItr;
		LinkedList<T> curr;

		@Override
		public boolean hasNext() {
			return (listItr != null && listItr.hasNext()) || (itr.hasNext() && (listItr = (curr = itr.next()).iterator()).hasNext());
		}

		@Override
		public T next() {
			return listItr.next();
		}

		@Override
		public void remove() {
			listItr.remove();
			if (curr.isEmpty()) itr.remove();
		}
	}
}
