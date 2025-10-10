package heavyindustry.util;

import arc.func.Boolf;
import arc.func.Boolf2;
import arc.func.Cons;
import arc.func.FloatFloatf;
import arc.func.Floatf;
import arc.func.Func;
import arc.func.Func2;
import arc.func.IntIntf;
import arc.func.Intf;
import arc.util.Eachable;
import arc.util.Reflect;
import heavyindustry.func.BoolBoolf;
import heavyindustry.func.ByteBytef;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;

public final class ArrayUtils {
	public static final int softMaxArrayLength = Integer.MAX_VALUE - 8;

	private ArrayUtils() {}

	/**
	 * Convert vararg to an array.
	 * Returns an array containing the specified elements.
	 *
	 * @apiNote Never use for generic array fields.
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
		return indexOf(array, element, true);
	}

	/**
	 * Returns the index of the first occurrence of the specified element in this list, or -1 if this list does not
	 * contain the element. More formally, returns the lowest index {@code i} such that {@code Objects.equals(o,
	 * get(i))}, or -1 if there is no such index.
	 *
	 * @return the index of the first occurrence of the specified element in this list, or -1 if this list does
	 * not contain the element
	 */
	public static <T> int indexOf(T[] array, T element, boolean identity) {
		if (identity || element == null) {
			for (int i = 0; i < array.length; i++) {
				if (array[i] == element) {
					return i;
				}
			}
		} else {
			for (int i = 0; i < array.length; i++) {
				if (element.equals(array[i])) {
					return i;
				}
			}
		}
		return -1;
	}

	public static <T> int indexOf(T[] array, int src, int end, T element) {
		return indexOf(array, src, end, element, true);
	}

	/**
	 * @param src starting position
	 * @param end final position
	 * @since 1.0.8
	 */
	public static <T> int indexOf(T[] array, int src, int end, T element, boolean identity) {
		int a = Math.max(0, src), b = Math.min(array.length, end);

		if (identity || element == null) {
			for (int i = a; i < b; i++) {
				if (array[i] == element) {
					return i;
				}
			}
		} else {
			for (int i = a; i < b; i++) {
				if (element.equals(array[i])) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Returns the index of the last occurrence of the specified element in this list, or -1 if this list does not
	 * contain the element. More formally, returns the highest index {@code i} such that {@code Objects.equals(o,
	 * get(i))}, or -1 if there is no such index.
	 *
	 * @return the index of the last occurrence of the specified element in this list, or -1 if this list does
	 * not contain the element
	 * @since 1.0.8
	 */
	public static <T> int lastIndexOf(T[] array, T element) {
		return lastIndexOf(array, element, true);
	}

	/**
	 * Returns the index of the last occurrence of the specified element in this list, or -1 if this list does not
	 * contain the element. More formally, returns the highest index {@code i} such that {@code Objects.equals(o,
	 * get(i))}, or -1 if there is no such index.
	 *
	 * @return the index of the last occurrence of the specified element in this list, or -1 if this list does
	 * not contain the element
	 * @since 1.0.8
	 */
	public static <T> int lastIndexOf(T[] array, T element, boolean identity) {
		if (identity || element == null) {
			for (int i = array.length - 1; i >= 0; i--) {
				if (array[i] == element) {
					return i;
				}
			}
		} else {
			for (int i = array.length - 1; i >= 0; i--) {
				if (element.equals(array[i])) {
					return i;
				}
			}
		}
		return -1;
	}

	// To prevent JS from being unable to match methods, it is necessary to distinguish them.
	public static int indexOfZ(boolean[] array, boolean element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOfB(byte[] array, byte element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOfS(short[] array, short element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOfI(int[] array, int element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOfJ(long[] array, long element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOfF(float[] array, float element) {
		for (int i = 0; i < array.length; i++) {
			if (Float.compare(array[i], element) == 0) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOfD(double[] array, double element) {
		for (int i = 0; i < array.length; i++) {
			if (Double.compare(array[i], element) == 0) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOfC(char[] array, char element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOfF(float[] array, float element, float epsilon) {
		for (int i = 0; i < array.length; i++) {
			if (Math.abs(array[i] - element) <= epsilon) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOfD(double[] array, double element, double epsilon) {
		for (int i = 0; i < array.length; i++) {
			if (Math.abs(array[i] - element) <= epsilon) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Computes a new array length given an array's current length, a minimum growth
	 * amount, and a preferred growth amount. The computation is done in an overflow-safe
	 * fashion.
	 * <p>This method is used by objects that contain an array that might need to be grown
	 * in order to fulfill some immediate need (the minimum growth amount) but would also
	 * like to request more space (the preferred growth amount) in order to accommodate
	 * potential future needs. The returned length is usually clamped at the soft maximum
	 * length in order to avoid hitting the JVM implementation limit. However, the soft
	 * maximum will be exceeded if the minimum growth amount requires it.
	 * <p>If the preferred growth amount is less than the minimum growth amount, the
	 * minimum growth amount is used as the preferred growth amount.
	 * <p>The preferred length is determined by adding the preferred growth amount to the
	 * current length. If the preferred length does not exceed the soft maximum length
	 * (SOFT_MAX_ARRAY_LENGTH) then the preferred length is returned.
	 * <p>If the preferred length exceeds the soft maximum, we use the minimum growth
	 * amount. The minimum required length is determined by adding the minimum growth
	 * amount to the current length. If the minimum required length exceeds Integer.MAX_VALUE,
	 * then this method throws OutOfMemoryError. Otherwise, this method returns the greater of
	 * the soft maximum or the minimum required length.
	 * <p>Note that this method does not do any array allocation itself; it only does array
	 * length growth computations. However, it will throw OutOfMemoryError as noted above.
	 * <p>Note also that this method cannot detect the JVM's implementation limit, and it
	 * may compute and return a length value up to and including Integer.MAX_VALUE that
	 * might exceed the JVM's implementation limit. In that case, the caller will likely
	 * attempt an array allocation with that length and encounter an OutOfMemoryError.
	 * Of course, regardless of the length value returned from this method, the caller
	 * may encounter OutOfMemoryError if there is insufficient heap to fulfill the request.
	 *
	 * @param oldLength   current length of the array (must be nonnegative)
	 * @param minGrowth   minimum required growth amount (must be positive)
	 * @param prefGrowth  preferred growth amount
	 * @return the new array length
	 * @throws OutOfMemoryError if the new length would exceed Integer.MAX_VALUE
	 */
	public static int newLength(int oldLength, int minGrowth, int prefGrowth) {
		// preconditions not checked because of inlining
		// assert oldLength >= 0
		// assert minGrowth > 0

		int prefLength = oldLength + Math.max(minGrowth, prefGrowth); // might overflow
		if (0 < prefLength && prefLength <= softMaxArrayLength) {
			return prefLength;
		}
		// put code cold in a separate method
		return hugeLength(oldLength, minGrowth);
	}

	private static int hugeLength(int oldLength, int minGrowth) {
		int minLength = oldLength + minGrowth;
		if (minLength < 0) { // overflow
			throw new OutOfMemoryError("Required array length " + oldLength + " + " + minGrowth + " is too large");
		}
		return Math.max(minLength, softMaxArrayLength);
	}

	public static <T> T[] copyArray(T[] array, Func<T, T> copy) {
		T[] out = array.clone();
		for (int i = 0, len = out.length; i < len; i++) out[i] = copy.get(out[i]);
		return out;
	}

	public static boolean[] copyZArray(boolean[] array, BoolBoolf copy) {
		boolean[] out = array.clone();
		for (int i = 0, len = out.length; i < len; i++) out[i] = copy.get(out[i]);
		return out;
	}

	public static byte[] copyBArray(byte[] array, ByteBytef copy) {
		byte[] out = array.clone();
		for (int i = 0, len = out.length; i < len; i++) out[i] = copy.get(out[i]);
		return out;
	}

	public static int[] copyIArray(int[] array, IntIntf copy) {
		int[] out = array.clone();
		for (int i = 0, len = out.length; i < len; i++) out[i] = copy.get(out[i]);
		return out;
	}

	public static float[] copyFArray(float[] array, FloatFloatf copy) {
		float[] out = array.clone();
		for (int i = 0, len = out.length; i < len; i++) out[i] = copy.get(out[i]);
		return out;
	}

	/**
	 * Insert an element at the first position of the array. Low performance.
	 *
	 * @param originalArray the source array.
	 * @param element       Inserted elements.
	 * @return Array after inserting elements.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] insertAtFirst(T[] originalArray, T element) {
		T[] newArray = (T[]) Array.newInstance(originalArray.getClass().componentType(), originalArray.length + 1);

		newArray[0] = element;

		System.arraycopy(originalArray, 0, newArray, 1, originalArray.length);

		return newArray;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] removeFirst(T[] originalArray) {
		if (originalArray.length <= 1) {
			return (T[]) Array.newInstance(originalArray.getClass().componentType(), 0);
		}

		T[] newArray = (T[]) Array.newInstance(originalArray.getClass().componentType(), originalArray.length - 1);

		System.arraycopy(originalArray, 1, newArray, 0, originalArray.length - 1);

		return newArray;
	}

	public static int[] sortI(int[] arr) {
		for (int i = 1; i < arr.length; i++) {
			int tmp = arr[i];

			int j = i;
			while (j > 0 && tmp < arr[j - 1]) {
				arr[j] = arr[j - 1];
				j--;
			}

			if (j != i) {
				arr[j] = tmp;
			}
		}
		return arr;
	}

	public static void shellSortI(int[] arr) {
		int temp;
		for (int step = arr.length / 2; step >= 1; step /= 2) {
			for (int i = step; i < arr.length; i++) {
				temp = arr[i];
				int j = i - step;
				while (j >= 0 && arr[j] > temp) {
					arr[j + step] = arr[j];
					j -= step;
				}
				arr[j + step] = temp;
			}
		}
	}

	/** Used to avoid performance overhead caused by creating an instance of {@link StringBuilder}. */
	public static void appendZ(StringBuilder b, boolean... a) {
		if (a == null) {
			b.append("null");
			return;
		}
		int max = a.length - 1;
		if (max == -1) {
			b.append("[]");
			return;
		}

		b.append('[');
		for (int i = 0; i < a.length; i++) {
			b.append(a[i]);
			if (i == max) {
				b.append(']');
				break;
			}
			b.append(", ");
		}
	}

	public static void appendB(StringBuilder b, byte... a) {
		if (a == null) {
			b.append("null");
			return;
		}
		int max = a.length - 1;
		if (max == -1) {
			b.append("[]");
			return;
		}

		b.append('[');
		for (int i = 0; i < a.length; i++) {
			b.append(a[i]);
			if (i == max) {
				b.append(']');
				break;
			}
			b.append(", ");
		}
	}

	public static void appendS(StringBuilder b, short... a) {
		if (a == null) {
			b.append("null");
			return;
		}
		int max = a.length - 1;
		if (max == -1) {
			b.append("[]");
			return;
		}

		b.append('[');
		for (int i = 0; i < a.length; i++) {
			b.append(a[i]);
			if (i == max) {
				b.append(']');
				break;
			}
			b.append(", ");
		}
	}

	public static void appendI(StringBuilder b, int... a) {
		if (a == null) {
			b.append("null");
			return;
		}
		int max = a.length - 1;
		if (max == -1) {
			b.append("[]");
			return;
		}

		b.append('[');
		for (int i = 0; i < a.length; i++) {
			b.append(a[i]);
			if (i == max) {
				b.append(']');
				break;
			}
			b.append(", ");
		}
	}

	public static void appendJ(StringBuilder b, long... a) {
		if (a == null) {
			b.append("null");
			return;
		}
		int max = a.length - 1;
		if (max == -1) {
			b.append("[]");
			return;
		}

		b.append('[');
		for (int i = 0; i < a.length; i++) {
			b.append(a[i]);
			if (i == max) {
				b.append(']');
				break;
			}
			b.append(", ");
		}
	}

	public static void appendC(StringBuilder b, char... a) {
		if (a == null) {
			b.append("null");
			return;
		}
		int max = a.length - 1;
		if (max == -1) {
			b.append("[]");
			return;
		}

		b.append('[');

		for (int i = 0; i < a.length; i++) {
			b.append(a[i]);
			if (i == max) {
				b.append(']');
				break;
			}
			b.append(", ");
		}
	}

	public static void appendF(StringBuilder b, float... a) {
		if (a == null) {
			b.append("null");
			return;
		}
		int max = a.length - 1;
		if (max == -1) {
			b.append("[]");
			return;
		}

		b.append('[');
		for (int i = 0; i < a.length; i++) {
			b.append(a[i]);
			if (i == max) {
				b.append(']');
				break;
			}
			b.append(", ");
		}
	}

	public static void appendD(StringBuilder b, double... a) {
		if (a == null) {
			b.append("null");
			return;
		}
		int max = a.length - 1;
		if (max == -1) {
			b.append("[]");
			return;
		}

		b.append('[');
		for (int i = 0; i < a.length; i++) {
			b.append(a[i]);
			if (i == max) {
				b.append(']');
				break;
			}
			b.append(", ");
		}
	}

	public static void append(StringBuilder b, Object... a) {
		if (a == null) {
			b.append("null");
			return;
		}

		int max = a.length - 1;
		if (max == -1) {
			b.append("[]");
			return;
		}

		b.append('[');
		for (int i = 0; i < a.length; i++) {
			b.append(a[i]);
			if (i == max) {
				b.append(']');
				break;
			}
			b.append(", ");
		}
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

	public static <T> int reducei(T[] array, int initial, ReduceInt<T> reduce) {
		for (T item : array) initial = reduce.get(item, initial);
		return initial;
	}

	public static <T> int sumi(T[] array, Intf<T> extract) {
		return reducei(array, 0, (item, accum) -> accum + extract.get(item));
	}

	public static <T> float reducef(T[] array, float initial, ReduceFloat<T> reduce) {
		for (T item : array) initial = reduce.get(item, initial);
		return initial;
	}

	public static <T> float average(T[] array, Floatf<T> extract) {
		return reducef(array, 0f, (item, accum) -> accum + extract.get(item)) / array.length;
	}

	public static <T> T[] resize(T[] array, int newSize, T fill) {
		return resize(array, size -> Reflect.newArray(array, newSize), newSize, fill);
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

	public interface ReduceInt<T> {
		int get(T item, int accum);
	}

	public interface ReduceFloat<T> {
		float get(T item, float accum);
	}

	public interface ArrayCreator<T> {
		T[] get(int size);
	}
}
