package heavyindustry.util;

import arc.func.Boolf;
import arc.func.Boolf2;
import arc.func.Cons;
import arc.func.Floatf;
import arc.func.Func;
import arc.func.Func2;
import arc.func.Intf;
import arc.util.Eachable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Struct utilities, providing some stateless iterative utilities such as reduce.
 *
 * @author Eipusino
 */
public final class Structf {
	private static final Object[] emptyArray = new Object[0];
	private static final Empty<?> empty = new Empty<>();

	/** Don't let anyone instantiate this class. */
	private Structf() {}

	@SuppressWarnings("unchecked")
	public static <T> Empty<T> empty() {
		// SAFETY: Has no references or casts to T, so type erasure shouldn't mess everything up.
		return (Empty<T>) empty;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] emptyArray() {
		// SAFETY: If users require the type to be exactly T[], they can use reflection instead.
		return (T[]) emptyArray;
	}

	public static <T> T[] copyArray(T[] array, Func<T, T> copy) {
		T[] out = array.clone();
		for (int i = 0, len = out.length; i < len; i++) out[i] = copy.get(out[i]);
		return out;
	}

	public static <T> boolean any(T[] array, Boolf<T> pred) {
		for (T e : array) if (pred.get(e)) return true;
		return false;
	}

	public static <T> boolean all(T[] array, Boolf<T> pred) {
		for (T e : array) if (!pred.get(e)) return false;
		return true;
	}

	public static <T> void each(T[] array, Cons<? super T> cons) {
		each(array, 0, array.length, cons);
	}

	public static <T> void each(T[] array, int offset, int length, Cons<? super T> cons) {
		for (int i = offset, len = i + length; i < len; i++) cons.get(array[i]);
	}

	public static <T> Single<T> iter(T item) {
		return new Single<>(item);
	}

	@SuppressWarnings("unchecked")
	public static <T> Iter<T> iter(T... array) {
		return iter(array, 0, array.length);
	}

	public static <T> Iter<T> iter(T[] array, int offset, int length) {
		return new Iter<>(array, offset, length);
	}

	public static <T> Chain<T> chain(Iterator<T> first, Iterator<T> second) {
		return new Chain<>(first, second);
	}

	public static <T, R> R reduce(T[] array, R initial, Func2<T, R, R> reduce) {
		for (T item : array) initial = reduce.get(item, initial);
		return initial;
	}

	public static <T> int reducei(T[] array, int initial, Reducei<T> reduce) {
		for (T item : array) initial = reduce.get(item, initial);
		return initial;
	}

	public static <T> int sumi(T[] array, Intf<T> extract) {
		return reducei(array, 0, (item, accum) -> accum + extract.get(item));
	}

	public static <T> float reducef(T[] array, float initial, Reducef<T> reduce) {
		for (T item : array) initial = reduce.get(item, initial);
		return initial;
	}

	public static <T> float average(T[] array, Floatf<T> extract) {
		return reducef(array, 0f, (item, accum) -> accum + extract.get(item)) / array.length;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] resize(T[] array, int newSize, T fill) {
		Class<?> type = array.getClass().getComponentType();
		return resize(array, size -> (T[]) Array.newInstance(type, size), newSize, fill);
	}

	public static <T> T[] resize(T[] array, ArrayCreator<T> create, int newSize, T fill) {
		if (array.length == newSize) return array;

		T[] out = create.get(newSize);
		System.arraycopy(array, 0, out, 0, Math.min(array.length, newSize));

		if (fill != null && newSize > array.length) Arrays.fill(out, array.length, newSize, fill);
		return out;
	}

	public static <T> boolean arrayEq(T[] first, T[] second, Boolf2<T, T> eq) {
		if (first.length != second.length) return false;
		for (int i = 0; i < first.length; i++) {
			if (!eq.get(first[i], second[i])) return false;
		}
		return true;
	}

	public interface Reducei<T> {
		int get(T item, int accum);
	}

	public interface Reducef<T> {
		float get(T item, float accum);
	}

	public interface ArrayCreator<T> {
		T[] get(int size);
	}

	public static class Empty<T> implements Iterable<T>, Iterator<T>, Eachable<T> {
		@Override
		public Empty<T> iterator() {
			return this;
		}

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public T next() {
			return null; //Empty<T> has no elements.
		}

		@Override
		public void each(Cons<? super T> cons) {}
	}

	public static class Single<T> implements Iterable<T>, Iterator<T>, Eachable<T> {
		protected final T item;
		protected boolean done;

		public Single(T t) {
			item = t;
		}

		@Override
		public Single<T> iterator() {
			return this;
		}

		@Override
		public boolean hasNext() {
			return !done;
		}

		@Override
		public T next() {
			if (done) return null;
			done = true;
			return item;
		}

		@Override
		public void each(Cons<? super T> cons) {
			if (!done) cons.get(item);
		}
	}

	public static class Iter<T> implements Iterable<T>, Iterator<T>, Eachable<T> {
		protected final T[] array;
		protected final int offset, length;
		protected int index = 0;

		public Iter(T[] arr, int off, int len) {
			array = arr;
			offset = off;
			length = len;
		}

		@Override
		public Iter<T> iterator() {
			return this;
		}

		@Override
		public boolean hasNext() {
			return index < length - offset;
		}

		@Override
		public T next() {
			if (hasNext()) return array[offset + index++];
			else return null;
		}

		@Override
		public void each(Cons<? super T> cons) {
			while (hasNext()) cons.get(array[offset + index++]);
		}
	}

	public static class Chain<T> implements Iterable<T>, Iterator<T>, Eachable<T> {
		protected final Iterator<T> first, second;

		public Chain(Iterator<T> fir, Iterator<T> sec) {
			first = fir;
			second = sec;
		}

		@Override
		public Chain<T> iterator() {
			return this;
		}

		@Override
		public boolean hasNext() {
			return first.hasNext() || second.hasNext();
		}

		@Override
		public T next() {
			return first.hasNext() ? first.next() : second.next();
		}

		@Override
		public void each(Cons<? super T> cons) {
			while (first.hasNext()) cons.get(first.next());
			while (second.hasNext()) cons.get(second.next());
		}
	}
}
