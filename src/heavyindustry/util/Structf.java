package heavyindustry.util;

import arc.func.*;
import arc.util.Eachable;
import arc.util.Log;
import heavyindustry.func.ProvT;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;

/**
 * Struct utilities, providing some stateless iterative utilities such as reduce.
 *
 * @author Eipusino
 */
public final class Structf {
	/** Don't let anyone instantiate this class. */
	private Structf() {}

	public static boolean equals(Object a, Object b) {
		return a == b || a != null && a.equals(b);
	}

	/**
	 * Convert vararg to an array.
	 * Returns an array containing the specified elements.
	 */
	@SafeVarargs
	public static <T> T[] arrayOf(T... elements) {
		return elements;
	}

	public static boolean[] boolOf(boolean... bools) {
		return bools;
	}

	public static byte[] byteOf(byte... bytes) {
		return bytes;
	}

	public static short[] shortOf(short... shorts) {
		return shorts;
	}

	public static int[] intOf(int... ints) {
		return ints;
	}

	public static long[] longOf(long... longs) {
		return longs;
	}

	public static float[] floatOf(float... floats) {
		return floats;
	}

	public static double[] doubleOf(double... doubles) {
		return doubles;
	}

	public static <T> int indexOf(T[] array, T element) {
		for (int i = 0; i < array.length; i++) {
			if (equals(array[i], element)) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(boolean[] array, boolean element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(byte[] array, byte element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(short[] array, short element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(int[] array, int element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(long[] array, long element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(float[] array, float element) {
		for (int i = 0; i < array.length; i++) {
			if (Float.compare(array[i], element) == 0) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(double[] array, double element) {
		for (int i = 0; i < array.length; i++) {
			if (Double.compare(array[i], element) == 0) {
				return i;
			}
		}
		return -1;
	}

	public static int hash(Object... values) {
		return hashCode(values);
	}

	public static int hashCode(Object... values) {
		if (values == null)
			return 0;

		int result = 1;

		for (Object element : values)
			result = 31 * result + (element == null ? 0 : element.hashCode());

		return result;
	}

	public static <T> T apply(T obj, Cons<T> cons) {
		cons.get(obj);
		return obj;
	}

	public static <T> void get(ConsT<T, Exception> cons, T obj) {
		try {
			cons.get(obj);
		} catch (Exception e) {
			Log.err(e);
		}
	}

	public static <T> T get(ProvT<T, Exception> prov, T def) {
		try {
			return prov.get();
		} catch (Exception e) {
			Log.err(e);

			return def;
		}
	}

	public static <T> T get(ProvT<T, Exception> prov, ConsT<T, Exception> cons, T def) {
		try {
			T t = prov.get();
			cons.get(t);
			return t;
		} catch (Exception e) {
			Log.err(e);

			return def;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T cast(Object obj) {
		return (T) obj;
	}

	@SuppressWarnings("unchecked")
	public static <T> T cast(Object obj, Class<T> type, T def) {
		if (obj != null && !type.isInstance(obj))
			return def;
		return (T) obj;
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

	@SafeVarargs
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
		private final T[] array;
		private final int offset, length;
		private int index = 0;

		public Iter(T[] arr, int off, int len) {
			array = arr;
			offset = off;
			length = len;
		}

		public int length() {
			return length;
		}

		public void reset() {
			index = 0;
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
			return hasNext() ? array[offset + index++] : null;
		}

		@Override
		public void each(Cons<? super T> cons) {
			while (hasNext()) cons.get(array[offset + index++]);
		}
	}

	public static class Chain<T> implements Iterable<T>, Iterator<T>, Eachable<T> {
		private final Iterator<T> first, second;

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
