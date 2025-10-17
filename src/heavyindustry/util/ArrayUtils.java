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
import heavyindustry.util.holder.ObjectHolder;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;

public final class ArrayUtils {
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

	public static <T> int indexOf(T[] array, Object element) {
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
	public static <T> int indexOf(T[] array, Object element, boolean identity) {
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

	public static <T> int indexOf(T[] array, T element, int src, int end) {
		return indexOf(array, element, src, end, true);
	}

	/**
	 * @param src starting position
	 * @param end final position
	 * @since 1.0.8
	 */
	public static <T> int indexOf(T[] array, T element, int src, int end, boolean identity) {
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
	public static int indexOfBool(boolean[] array, boolean element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int lastIndexOfBool(boolean[] array, boolean element) {
		for (int i = array.length -1; i >= 0; i--) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOfByte(byte[] array, byte element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOfShort(short[] array, short element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOfInt(int[] array, int element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOfLong(long[] array, long element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOfFloat(float[] array, float element) {
		for (int i = 0; i < array.length; i++) {
			if (Float.compare(array[i], element) == 0) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOfDouble(double[] array, double element) {
		for (int i = 0; i < array.length; i++) {
			if (Double.compare(array[i], element) == 0) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOfChar(char[] array, char element) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == element) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOfFloat(float[] array, float element, float epsilon) {
		for (int i = 0; i < array.length; i++) {
			if (Math.abs(array[i] - element) <= epsilon) {
				return i;
			}
		}
		return -1;
	}

	public static int indexOfDouble(double[] array, double element, double epsilon) {
		for (int i = 0; i < array.length; i++) {
			if (Math.abs(array[i] - element) <= epsilon) {
				return i;
			}
		}
		return -1;
	}

	public static <T> T[] copyArray(T[] array, Func<T, T> copy) {
		T[] out = array.clone();
		for (int i = 0, len = out.length; i < len; i++) out[i] = copy.get(out[i]);
		return out;
	}

	public static boolean[] copyArray(boolean[] array, BoolBoolf copy) {
		boolean[] out = array.clone();
		for (int i = 0, len = out.length; i < len; i++) out[i] = copy.get(out[i]);
		return out;
	}

	public static byte[] copyArray(byte[] array, ByteBytef copy) {
		byte[] out = array.clone();
		for (int i = 0, len = out.length; i < len; i++) out[i] = copy.get(out[i]);
		return out;
	}

	public static int[] copyArray(int[] array, IntIntf copy) {
		int[] out = array.clone();
		for (int i = 0, len = out.length; i < len; i++) out[i] = copy.get(out[i]);
		return out;
	}

	public static float[] copyArray(float[] array, FloatFloatf copy) {
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

	public static int[] sortInt(int[] arr) {
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

	public static void shellSortInt(int[] arr) {
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

	/**
	 * Returns a comparator that compares {@link Entry} in natural order on key.
	 *
	 * <p>The returned comparator is serializable and throws {@link
	 * NullPointerException} when comparing an entry with a null key.
	 *
	 * @param <K> the {@link Comparable} type of then map keys
	 * @param <V> the type of the map values
	 * @return a comparator that compares {@link Entry} in natural order on key.
	 * @see Comparable
	 * @since 1.0.7
	 */
	public static <K extends Comparable<? super K>, V> Comparator<Entry<K, V>> comparingByKey() {
		return (c1, c2) -> c1.getKey().compareTo(c2.getKey());
	}

	/**
	 * Returns a comparator that compares {@link Entry} in natural order on value.
	 * <p>The returned comparator is serializable and throws {@link
	 * NullPointerException} when comparing an entry with null values.
	 *
	 * @param <K> the type of the map keys
	 * @param <V> the {@link Comparable} type of the map values
	 * @return a comparator that compares {@link Entry} in natural order on value.
	 * @see Comparable
	 * @since 1.0.7
	 */
	public static <K, V extends Comparable<? super V>> Comparator<Entry<K, V>> comparingByValue() {
		return (c1, c2) -> c1.getValue().compareTo(c2.getValue());
	}

	/**
	 * Returns a comparator that compares {@link Entry} by key using the given {@link Comparator}.
	 * <p>The returned comparator is serializable if the specified comparator
	 * is also serializable.
	 *
	 * @param <K> the type of the map keys
	 * @param <V> the type of the map values
	 * @param cmp the key {@link Comparator}
	 * @return a comparator that compares {@link Entry} by the key.
	 * @since 1.0.7
	 */
	public static <K, V> Comparator<Entry<K, V>> comparingByKey(Comparator<? super K> cmp) {
		return (c1, c2) -> cmp.compare(c1.getKey(), c2.getKey());
	}

	/**
	 * Returns a comparator that compares {@link Entry} by value using the given {@link Comparator}.
	 * <p>The returned comparator is serializable if the specified comparator
	 * is also serializable.
	 *
	 * @param <K> the type of the map keys
	 * @param <V> the type of the map values
	 * @param cmp the value {@link Comparator}
	 * @return a comparator that compares {@link Entry} by the value.
	 * @since 1.0.7
	 */
	public static <K, V> Comparator<Entry<K, V>> comparingByValue(Comparator<? super V> cmp) {
		return (c1, c2) -> cmp.compare(c1.getValue(), c2.getValue());
	}

	/**
	 * Returns a copy of the given {@code Map.MapEntry}. The returned instance is not associated with any map.
	 * The returned instance has the same characteristics as instances returned by the {@link java.util.Map#entry Map::entry}
	 * method.
	 *
	 * @param <K> the type of the key
	 * @param <V> the type of the value
	 * @param e   the entry to be copied
	 * @return a map entry equal to the given entry
	 * @throws NullPointerException if e is null
	 * @apiNote An instance obtained from a map's entry-set view has a connection to that map.
	 * The {@code copyOf}  method may be used to create a {@code Map.MapEntry} instance,
	 * containing the same key and value, that is independent of any map.
	 * @implNote If the given entry was obtained from a call to {@code copyOf} or {@code Map::entry},
	 * calling {@code copyOf} will generally not create another copy.
	 * @since 1.0.7
	 */
	@SuppressWarnings("unchecked")
	public static <K, V> Entry<K, V> copyOf(Entry<? extends K, ? extends V> e) {
		if (e instanceof ObjectHolder<? extends K, ? extends V>) {
			return (Entry<K, V>) e;
		} else {
			return new ObjectHolder<>(e.getKey(), e.getValue());
		}
	}

	/**
	 * Used to avoid performance overhead caused by creating an instance of {@link StringBuilder}.
	 *
	 * @see Arrays#toString(Object[])
	 */
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

	public static void appendBool(StringBuilder b, boolean... a) {
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

	public static void appendByte(StringBuilder b, byte... a) {
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

	public static void appendShort(StringBuilder b, short... a) {
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

	public static void appendInt(StringBuilder b, int... a) {
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

	public static void appendLong(StringBuilder b, long... a) {
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

	public static void appendChar(StringBuilder b, char... a) {
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

	public static void appendFloat(StringBuilder b, float... a) {
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

	public static void appendDouble(StringBuilder b, double... a) {
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

	public static <T> int reduceInt(T[] array, int initial, ReduceInt<T> reduce) {
		for (T item : array) initial = reduce.get(item, initial);
		return initial;
	}

	public static <T> int sumInt(T[] array, Intf<T> extract) {
		return reduceInt(array, 0, (item, accum) -> accum + extract.get(item));
	}

	public static <T> float reduceFloat(T[] array, float initial, ReduceFloat<T> reduce) {
		for (T item : array) initial = reduce.get(item, initial);
		return initial;
	}

	public static <T> float average(T[] array, Floatf<T> extract) {
		return reduceFloat(array, 0f, (item, accum) -> accum + extract.get(item)) / array.length;
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

	public static <T> boolean arrayEquals(T[] first, T[] second, Boolf2<T, T> equals) {
		if (first.length != second.length) return false;
		for (int i = 0; i < first.length; i++) {
			if (!equals.get(first[i], second[i])) return false;
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
		protected final T[] array;
		protected final int offset, length;
		protected int index = 0;

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
