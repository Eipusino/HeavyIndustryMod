package endfield.util;

import arc.func.Boolf;
import arc.func.Cons;
import arc.func.Floatf;
import arc.func.Func;
import arc.func.Func2;
import arc.func.Intf;
import arc.func.Prov;
import arc.math.Mathf;
import arc.math.Rand;
import arc.struct.FloatSeq;
import arc.struct.IntSeq;
import arc.util.ArcRuntimeException;
import arc.util.Structs;
import endfield.math.Mathm;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public interface IList<E> extends List<E>, ICollection<E>, IIterable<E> {
	default float sumf(Floatf<E> summer) {
		float sum = 0f;
		for (E e : this) {
			sum += summer.get(e);
		}
		return sum;
	}

	default int sum(Intf<E> summer) {
		int sum = 0;
		for (E e : this) {
			sum += summer.get(e);
		}
		return sum;
	}

	@SuppressWarnings("unchecked")
	default <T extends E> void each(Boolf<? super E> predicate, Cons<T> consumer) {
		for (E e : this) {
			if (predicate.get(e)) consumer.get((T) e);
		}
	}

	default void replace(Func<E, E> mapper) {
		for (int i = 0; i < size(); i++) {
			set(i, mapper.get(get(i)));
		}
	}

	default IntSeq mapInt(Intf<E> mapper) {
		IntSeq arr = new IntSeq(size());
		for (E e : this) {
			arr.add(mapper.get(e));
		}
		return arr;
	}

	default IntSeq mapInt(Intf<E> mapper, Boolf<E> retain) {
		IntSeq arr = new IntSeq(size());
		for (E e : this) {
			if (retain.get(e)) {
				arr.add(mapper.get(e));
			}
		}
		return arr;
	}

	default FloatSeq mapFloat(Floatf<E> mapper) {
		FloatSeq arr = new FloatSeq(size());
		for (E e : this) {
			arr.add(mapper.get(e));
		}
		return arr;
	}

	default <R> R reduce(R initial, Func2<E, R, R> reducer) {
		R result = initial;
		for (E e : this) {
			result = reducer.get(e, result);
		}
		return result;
	}

	default boolean allMatch(Boolf<E> predicate) {
		for (E e : this) {
			if (!predicate.get(e)) {
				return false;
			}
		}
		return true;
	}

	default boolean contains(Boolf<E> predicate) {
		for (E e : this) {
			if (predicate.get(e)) {
				return true;
			}
		}
		return false;
	}

	default E min(Comparator<? super E> func) {
		E result = null;
		for (E t : this) {
			if (result == null || func.compare(result, t) > 0) {
				result = t;
			}
		}
		return result;
	}

	default E max(Comparator<? super E> func) {
		E result = null;
		for (E t : this) {
			if (result == null || func.compare(result, t) < 0) {
				result = t;
			}
		}
		return result;
	}

	default E min(Boolf<E> filter, Floatf<E> func) {
		E result = null;
		float min = Float.MAX_VALUE;
		for (E t : this) {
			if (!filter.get(t)) continue;
			float val = func.get(t);
			if (val <= min) {
				result = t;
				min = val;
			}
		}
		return result;
	}

	default E min(Boolf<E> filter, Comparator<? super E> func) {
		E result = null;
		for (E t : this) {
			if (filter.get(t) && (result == null || func.compare(result, t) > 0)) {
				result = t;
			}
		}
		return result;
	}

	default E min(Floatf<E> func) {
		E result = null;
		float min = Float.MAX_VALUE;
		for (E t : this) {
			float val = func.get(t);
			if (val <= min) {
				result = t;
				min = val;
			}
		}
		return result;
	}

	default E max(Floatf<E> func) {
		E result = null;
		float max = Float.NEGATIVE_INFINITY;
		for (E t : this) {
			float val = func.get(t);
			if (val >= max) {
				result = t;
				max = val;
			}
		}
		return result;
	}

	default @Nullable E find(Boolf<E> predicate) {
		for (E e : this) {
			if (predicate.get(e)) {
				return e;
			}
		}
		return null;
	}

	default boolean addUnique(E value) {
		if (!contains(value)) {
			add(value);
			return true;
		}
		return false;
	}

	default boolean add(E value1, E value2) {
		add(value1);
		add(value2);
		return true;
	}

	default boolean add(E value1, E value2, E value3) {
		add(value1);
		add(value2);
		add(value3);
		return true;
	}

	default boolean add(E value1, E value2, E value3, E value4) {
		add(value1);
		add(value2);
		add(value3);
		add(value4);
		return true;
	}

	default void add(E[] array) {
		for (E e : array) {
			add(e);
		}
	}

	default void set(E[] array) {
		clear();
		for (E e : array) {
			add(e);
		}
	}

	default E getFrac(float index) {
		if (isEmpty()) return null;
		return get(Mathm.clamp((int) (index * size()), 0, size() - 1));
	}

	default E get(int index, E def) {
		if (index >= size() || index <= 0) return def;
		return get(index);
	}

	default void insert(int index, E element) {}

	default void swap(int first, int second) {
		if (first >= size()) throw new IndexOutOfBoundsException("first can't be >= size: " + first + " >= " + size());
		if (second >= size()) throw new IndexOutOfBoundsException("second can't be >= size: " + second + " >= " + size());
		E firstValue = get(first);
		set(first, get(second));
		set(second, firstValue);
	}

	default boolean replace(E from, E to) {
		return false;
	}

	default boolean contains(Object o, boolean identity) {
		int i = size() - 1;
		if (identity || o == null) {
			while (i >= 0)
				if (get(i--) == o) return true;
		} else {
			while (i >= 0)
				if (o.equals(get(i--))) return true;
		}
		return false;
	}

	default int indexOf(Object o, boolean identity) {
		if (identity || o == null) {
			for (int i = 0, n = size(); i < n; i++)
				if (get(i) == o) return i;
		} else {
			for (int i = 0, n = size(); i < n; i++)
				if (o.equals(get(i))) return i;
		}
		return -1;
	}

	default int indexOf(Boolf<E> value) {
		for (int i = 0, n = size(); i < n; i++)
			if (value.get(get(i))) return i;
		return -1;
	}

	default int lastIndexOf(Object o, boolean identity) {
		if (identity || o == null) {
			for (int i = size() - 1; i >= 0; i--)
				if (get(i) == o) return i;
		} else {
			for (int i = size() - 1; i >= 0; i--)
				if (o.equals(get(i))) return i;
		}
		return -1;
	}

	default boolean remove(Boolf<E> value) {
		for (int i = 0; i < size(); i++) {
			if (value.get(get(i))) {
				remove(i);
				return true;
			}
		}
		return false;
	}

	default boolean remove(@Nullable Object o, boolean identity) {
		if (identity || o == null) {
			for (int i = 0, n = size(); i < n; i++) {
				if (get(i) == o) {
					remove(i);
					return true;
				}
			}
		} else {
			for (int i = 0, n = size(); i < n; i++) {
				if (o.equals(get(i))) {
					remove(i);
					return true;
				}
			}
		}
		return false;
	}

	default boolean removeAll(@Nullable Object o, boolean identity) {
		boolean modified = false;

		if (identity || o == null) {
			for (int i = 0, n = size(); i < n; i++) {
				if (get(i) == o) {
					remove(i);
					modified = true;
				}
			}
		} else {
			for (int i = 0, n = size(); i < n; i++) {
				if (o.equals(get(i))) {
					remove(i);
					modified = true;
				}
			}
		}
		return modified;
	}

	default void removeAll(Boolf<E> pred) {
		Iterator<E> iter = iterator();
		while (iter.hasNext()) {
			if (pred.get(iter.next())) {
				iter.remove();
			}
		}
	}

	default E peek() {
		if (size() == 0) throw new IllegalStateException("Array is empty.");
		return get(size() - 1);
	}

	default E peek(Prov<E> constructor) {
		if (size() == 0) return constructor.get();
		return get(size() - 1);
	}

	default E first(Prov<E> constructor) {
		if (size() == 0) return constructor.get();
		return get(0);
	}

	default E firstOpt() {
		if (size() == 0) return null;
		return get(0);
	}

	default void sort() {}

	default void sort(Floatf<? super E> comparator) {
		sort(Structs.comparingFloat(comparator));
	}

	default <U extends Comparable<? super U>> void sortComparing(Func<? super E, ? extends U> keyExtractor) {
		sort(Structs.comparing(keyExtractor));
	}

	default void distinct() {
		Set<E> set = new HashSet<>(this);
		clear();
		addAll(set);
	}

	default void retainAll(Boolf<E> predicate) {
		removeAll(e -> !predicate.get(e));
	}

	default int count(Boolf<E> predicate) {
		int count = 0;
		for (E e : this) {
			if (predicate.get(e)) {
				count++;
			}
		}
		return count;
	}

	@SuppressWarnings("unchecked")
	default E selectRanked(Comparator<? super E> comparator, int kthLowest) {
		if (kthLowest < 1) {
			throw new ArcRuntimeException("nth_lowest must be greater than 0, 1 = first, 2 = second...");
		}
		return Arrays2.select(toArray((E[]) Array.newInstance(componentType(), size())), comparator, kthLowest, size());
	}

	@SuppressWarnings("unchecked")
	default int selectRankedIndex(Comparator<? super E> comparator, int kthLowest) {
		if (kthLowest < 1) {
			throw new ArcRuntimeException("nth_lowest must be greater than 0, 1 = first, 2 = second...");
		}
		return Arrays2.selectIndex(toArray((E[]) Array.newInstance(componentType(), size())), comparator, kthLowest, size());
	}

	default void reverse() {
		for (int i = 0, lastIndex = size() - 1, n = size() / 2; i < n; i++) {
			int ii = lastIndex - i;
			E temp = get(i);
			set(i, get(ii));
			set(ii, temp);
		}
	}

	default void shuffle() {
		for (int i = size() - 1; i >= 0; i--) {
			int j = Mathf.random(i);
			E temp = get(i);
			set(i, get(j));
			set(j, temp);
		}
	}

	default E random(Rand rand) {
		if (size() == 0) return null;
		return get(rand.random(0, size() - 1));
	}

	default E random() {
		return random(Mathf.rand);
	}

	default E random(E exclude) {
		if (exclude == null) return random();
		if (size() == 0) return null;
		if (size() == 1) return first();

		int eidx = indexOf(exclude);
		//this item isn't even in the array!
		if (eidx == -1) return random();

		//shift up the index
		int index = Mathf.random(0, size() - 2);
		if (index >= eidx) {
			index++;
		}
		return get(index);
	}

	default void replaceAll(Func<E, E> operator) {
		final ListIterator<E> li = listIterator();
		while (li.hasNext()) {
			li.set(operator.get(li.next()));
		}
	}
}
