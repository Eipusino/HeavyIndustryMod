package heavyindustry.util;

import arc.func.Boolf;
import arc.func.Boolf2;
import arc.func.Cons;
import arc.func.Floatf;
import arc.func.Func;
import arc.func.Func2;
import arc.func.Intf;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Structs;

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

	public static <T> int indexOf(@Nullable T[] array, T element) {
		if (array == null) return -1;
		for (int i = 0; i < array.length; i++) {
			if (Structs.eq(array[i], element)) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(@Nullable boolean[] array, boolean element) {
		if (array == null) return -1;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(@Nullable byte[] array, byte element) {
		if (array == null) return -1;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(@Nullable short[] array, short element) {
		if (array == null) return -1;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(@Nullable int[] array, int element) {
		if (array == null) return -1;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(@Nullable long[] array, long element) {
		if (array == null) return -1;
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(@Nullable float[] array, float element) {
		if (array == null) return -1;
		for (int i = 0; i < array.length; i++) {
			if (Float.compare(array[i], element) == 0) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOf(@Nullable double[] array, double element) {
		if (array == null) return -1;
		for (int i = 0; i < array.length; i++) {
			if (Double.compare(array[i], element) == 0) {
				return i;
			}
		}
		return -1;
	}

	/** @deprecated see {@link #hashCode(Object...)} */
	@Deprecated
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

		public int size() {
			return length;
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
