/*
	Copyright (c) Eipusino 2021
	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
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
import arc.func.Prov;
import arc.struct.Seq;
import arc.util.Eachable;
import heavyindustry.func.BoolBoolf;
import heavyindustry.func.ByteBytef;
import heavyindustry.util.ref.ObjectHolder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;

public final class Arrays2 {
	private Arrays2() {}

	/**
	 * Convert vararg to an array.
	 * Returns an array containing the specified elements.
	 * <p>Can be used to create arrays with generic types, avoiding {@code @SuppressWarnings("unchecked")}.
	 * <p>Example: <pre>{@code
	 *     public Seq<Class<?>>[] sequences = arrayOf(new Seq<>(), Seq.with(Block.class, UnitType.class));
	 * }</pre>
	 */
	@SafeVarargs
	@Contract(value = "_ -> param1", pure = true)
	public static <T> T[] arrayOf(T... elements) {
		return elements;
	}

	@Contract(value = "_ -> param1", pure = true)
	public static boolean[] boolOf(boolean... bools) {
		return bools;
	}

	@Contract(value = "_ -> param1", pure = true)
	public static byte[] byteOf(byte... bytes) {
		return bytes;
	}

	@Contract(value = "_ -> param1", pure = true)
	public static short[] shortOf(short... shorts) {
		return shorts;
	}

	@Contract(value = "_ -> param1", pure = true)
	public static int[] intOf(int... ints) {
		return ints;
	}

	@Contract(value = "_ -> param1", pure = true)
	public static long[] longOf(long... longs) {
		return longs;
	}

	@Contract(value = "_ -> param1", pure = true)
	public static float[] floatOf(float... floats) {
		return floats;
	}

	@Contract(value = "_ -> param1", pure = true)
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
	 * @throws NullPointerException If {@code array} is null.
	 */
	@Contract(pure = true)
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
	 * @throws NullPointerException If {@code array} is null.
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
	 * not contain the element.
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
	 * not contain the element.
	 * @throws NullPointerException If {@code array} is null.
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
	 * @throws NullPointerException If {@code originalArray} is null.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] insertAtFirst(T[] originalArray, T element) {
		T[] newArray = (T[]) Array.newInstance(originalArray.getClass().getComponentType(), originalArray.length + 1);

		newArray[0] = element;

		System.arraycopy(originalArray, 0, newArray, 1, originalArray.length);

		return newArray;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] removeFirst(T[] originalArray) {
		if (originalArray.length <= 1) {
			return (T[]) Array.newInstance(originalArray.getClass().getComponentType(), 0);
		}

		T[] newArray = (T[]) Array.newInstance(originalArray.getClass().getComponentType(), originalArray.length - 1);

		System.arraycopy(originalArray, 1, newArray, 0, originalArray.length - 1);

		return newArray;
	}

	public static <T> T get(T[] array, int index, T def) {
		return index < 0 || index >= array.length ? def : array[index];
	}

	public static <T> T getNull(T[] array, int index) {
		return index < 0 || index >= array.length ? null : array[index];
	}

	public static boolean getBool(boolean[] array, int index, boolean def) {
		return index < 0 || index >= array.length ? def : array[index];
	}

	public static byte getByte(byte[] array, int index, byte def) {
		return index < 0 || index >= array.length ? def : array[index];
	}

	public static short getShort(short[] array, int index, short def) {
		return index < 0 || index >= array.length ? def : array[index];
	}

	public static int getInt(int[] array, int index, int def) {
		return index < 0 || index >= array.length ? def : array[index];
	}

	public static long getLong(long[] array, int index, long def) {
		return index < 0 || index >= array.length ? def : array[index];
	}

	public static float getFloat(float[] array, int index, float def) {
		return index < 0 || index >= array.length ? def : array[index];
	}

	public static double getDouble(double[] array, int index, double def) {
		return index < 0 || index >= array.length ? def : array[index];
	}

	public static char getChar(char[] array, int index, char def) {
		return index < 0 || index >= array.length ? def : array[index];
	}

	public static <T> T get(T[] array, int index, Prov<T> def) {
		return index < 0 || index >= array.length ? def.get() : array[index];
	}

	public static <T> Seq<T> asSeq(Seq<T> seq, T[] newArray) {
		if (seq.items.getClass().getComponentType() == newArray.getClass().getComponentType()) return seq;

		System.arraycopy(seq.items, 0, newArray, 0, Math.min(newArray.length, seq.items.length));
		seq.items = newArray;
		return seq;
	}

	@SuppressWarnings("unchecked")
	public static <T> Seq<T> asSeq(Seq<T> seq, Class<?> type) {
		if (seq.items.getClass().getComponentType() == type) return seq;

		T[] newArray = (T[]) Array.newInstance(type, seq.items.length);
		System.arraycopy(seq.items, 0, newArray, 0, newArray.length);
		seq.items = newArray;
		return seq;
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
	public static <K, V> ObjectHolder<K, V> copyOf(Entry<? extends K, ? extends V> e) {
		if (e instanceof ObjectHolder<? extends K, ? extends V>) {
			return (ObjectHolder<K, V>) e;
		} else {
			return new ObjectHolder<>(e.getKey(), e.getValue());
		}
	}

	public static <T> T select(T[] items, Comparator<T> comp, int kthLowest, int size) {
		int idx = selectIndex(items, comp, kthLowest, size);
		return items[idx];
	}

	public static <T> int selectIndex(T[] items, Comparator<T> comp, int kthLowest, int size) {
		if (size < 1) {
			throw new IllegalArgumentException("cannot select from empty array (size < 1)");
		} else if (kthLowest > size) {
			throw new IllegalArgumentException("Kth rank is larger than size. k: " + kthLowest + ", size: " + size);
		}
		int idx;
		// naive partial selection sort almost certain to outperform quickselect where n is min or max
		if (kthLowest == 1) {
			// find min
			idx = fastMin(items, comp, size);
		} else if (kthLowest == size) {
			// find max
			idx = fastMax(items, comp, size);
		} else {
			// quickselect a better choice for cases of k between min and max
			idx = selectIndex1(items, comp, kthLowest, size);
		}
		return idx;
	}

	/** Faster than quickselect for n = min */
	static <T> int fastMin(T[] items, Comparator<T> comp, int size) {
		int lowestIdx = 0;
		for (int i = 1; i < size; i++) {
			int comparison = comp.compare(items[i], items[lowestIdx]);
			if (comparison < 0) {
				lowestIdx = i;
			}
		}
		return lowestIdx;
	}

	/** Faster than quickselect for n = max */
	static <T> int fastMax(T[] items, Comparator<T> comp, int size) {
		int highestIdx = 0;
		for (int i = 1; i < size; i++) {
			int comparison = comp.compare(items[i], items[highestIdx]);
			if (comparison > 0) {
				highestIdx = i;
			}
		}
		return highestIdx;
	}

	public static <T> int selectIndex1(T[] items, Comparator<T> comp, int kthLowest, int size) {
		return selectIndex2(items, comp, 0, size - 1, kthLowest);
	}

	static <T> int partition(T[] array, Comparator<? super T> comp, int left, int right, int pivot) {
		T pivotValue = array[pivot];
		swap(array, right, pivot);
		int storage = left;
		for (int i = left; i < right; i++) {
			if (comp.compare(array[i], pivotValue) < 0) {
				swap(array, storage, i);
				storage++;
			}
		}
		swap(array, right, storage);
		return storage;
	}

	static <T> int selectIndex2(T[] array, Comparator<? super T> comp, int left, int right, int kthLowest) {
		if (left == right) return left;
		int pivotIndex = medianOfThreePivot(array, comp, left, right);
		int pivotNewIndex = partition(array, comp, left, right, pivotIndex);
		int pivotDist = (pivotNewIndex - left) + 1;
		int result;
		if (pivotDist == kthLowest) {
			result = pivotNewIndex;
		} else if (kthLowest < pivotDist) {
			result = selectIndex2(array, comp, left, pivotNewIndex - 1, kthLowest);
		} else {
			result = selectIndex2(array, comp, pivotNewIndex + 1, right, kthLowest - pivotDist);
		}
		return result;
	}

	/** Median of Three has the potential to outperform a random pivot, especially for partially sorted arrays */
	static <T> int medianOfThreePivot(T[] array, Comparator<? super T> comp, int leftIdx, int rightIdx) {
		T left = array[leftIdx];
		int midIdx = (leftIdx + rightIdx) / 2;
		T mid = array[midIdx];
		T right = array[rightIdx];

		// spaghetti median of three algorithm
		// does at most 3 comparisons
		if (comp.compare(left, mid) > 0) {
			if (comp.compare(mid, right) > 0) {
				return midIdx;
			} else if (comp.compare(left, right) > 0) {
				return rightIdx;
			} else {
				return leftIdx;
			}
		} else {
			if (comp.compare(left, right) > 0) {
				return leftIdx;
			} else if (comp.compare(mid, right) > 0) {
				return rightIdx;
			} else {
				return midIdx;
			}
		}
	}

	static <T> void swap(T[] array, int left, int right) {
		T tmp = array[left];
		array[left] = array[right];
		array[right] = tmp;
	}

	/**
	 * Used to avoid performance overhead caused by creating an instance of {@link StringBuilder}.
	 *
	 * @throws NullPointerException If {@code builder} is null.
	 * @see Arrays#toString(Object[])
	 */
	public static void append(StringBuilder builder, Object[] array) {
		if (array == null) {
			builder.append("null");
			return;
		}

		int max = array.length - 1;
		if (max == -1) {
			builder.append("[]");
			return;
		}

		builder.append('[');
		for (int i = 0; i < array.length; i++) {
			builder.append(array[i]);
			if (i == max) {
				builder.append(']');
				break;
			}
			builder.append(", ");
		}
	}

	public static void appendBool(StringBuilder builder, boolean[] array) {
		if (array == null) {
			builder.append("null");
			return;
		}
		int max = array.length - 1;
		if (max == -1) {
			builder.append("[]");
			return;
		}

		builder.append('[');
		for (int i = 0; i < array.length; i++) {
			builder.append(array[i]);
			if (i == max) {
				builder.append(']');
				break;
			}
			builder.append(", ");
		}
	}

	public static void appendByte(StringBuilder builder, byte[] array) {
		if (array == null) {
			builder.append("null");
			return;
		}
		int max = array.length - 1;
		if (max == -1) {
			builder.append("[]");
			return;
		}

		builder.append('[');
		for (int i = 0; i < array.length; i++) {
			builder.append(array[i]);
			if (i == max) {
				builder.append(']');
				break;
			}
			builder.append(", ");
		}
	}

	public static void appendShort(StringBuilder builder, short[] array) {
		if (array == null) {
			builder.append("null");
			return;
		}
		int max = array.length - 1;
		if (max == -1) {
			builder.append("[]");
			return;
		}

		builder.append('[');
		for (int i = 0; i < array.length; i++) {
			builder.append(array[i]);
			if (i == max) {
				builder.append(']');
				break;
			}
			builder.append(", ");
		}
	}

	public static void appendInt(StringBuilder builder, int[] array) {
		if (array == null) {
			builder.append("null");
			return;
		}
		int max = array.length - 1;
		if (max == -1) {
			builder.append("[]");
			return;
		}

		builder.append('[');
		for (int i = 0; i < array.length; i++) {
			builder.append(array[i]);
			if (i == max) {
				builder.append(']');
				break;
			}
			builder.append(", ");
		}
	}

	public static void appendLong(StringBuilder builder, long[] array) {
		if (array == null) {
			builder.append("null");
			return;
		}
		int max = array.length - 1;
		if (max == -1) {
			builder.append("[]");
			return;
		}

		builder.append('[');
		for (int i = 0; i < array.length; i++) {
			builder.append(array[i]);
			if (i == max) {
				builder.append(']');
				break;
			}
			builder.append(", ");
		}
	}

	public static void appendChar(StringBuilder builder, char[] array) {
		if (array == null) {
			builder.append("null");
			return;
		}
		int max = array.length - 1;
		if (max == -1) {
			builder.append("[]");
			return;
		}

		builder.append('[');

		for (int i = 0; i < array.length; i++) {
			builder.append(array[i]);
			if (i == max) {
				builder.append(']');
				break;
			}
			builder.append(", ");
		}
	}

	public static void appendFloat(StringBuilder builder, float[] array) {
		if (array == null) {
			builder.append("null");
			return;
		}
		int max = array.length - 1;
		if (max == -1) {
			builder.append("[]");
			return;
		}

		builder.append('[');
		for (int i = 0; i < array.length; i++) {
			builder.append(array[i]);
			if (i == max) {
				builder.append(']');
				break;
			}
			builder.append(", ");
		}
	}

	public static void appendDouble(StringBuilder builder, double[] array) {
		if (array == null) {
			builder.append("null");
			return;
		}
		int max = array.length - 1;
		if (max == -1) {
			builder.append("[]");
			return;
		}

		builder.append('[');
		for (int i = 0; i < array.length; i++) {
			builder.append(array[i]);
			if (i == max) {
				builder.append(']');
				break;
			}
			builder.append(", ");
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

	public static <T> float sumFloat(T[] array, Floatf<T> extract) {
		return reduceFloat(array, 0, (item, accum) -> accum + extract.get(item));
	}

	public static <T> float averageFloat(T[] array, Floatf<T> extract) {
		return reduceFloat(array, 0f, (item, accum) -> accum + extract.get(item)) / array.length;
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] resize(T[] array, int newSize, T fill) {
		return resize(array, size -> (T[]) Array.newInstance(array.getClass().getComponentType(), newSize), newSize, fill);
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
		public @NotNull Single<T> iterator() {
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
		public @NotNull Iter<T> iterator() {
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
		public @NotNull Chain<T> iterator() {
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
