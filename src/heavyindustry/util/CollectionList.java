package heavyindustry.util;

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
import arc.struct.Seq;
import arc.struct.Sort;
import arc.util.ArcRuntimeException;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Select;
import arc.util.Structs;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * A resizable, ordered or unordered array of objects. If unordered, this class avoids a memory copy when removing elements (the
 * last element is moved to the removed element's position).
 *
 * @author Nathan Sweet
 * @author Eipusino
 */
public class CollectionList<E> extends AbstractList<E> implements Eachable<E> {
	/** Debugging variable to count total number of iterators allocated. */
	public static int iteratorsAllocated = 0;

	public final Class<?> componentType;
	/**
	 * Provides direct access to the underlying array. If the Array's generic type is not Object, this field may only be accessed
	 * if the {@link CollectionList#CollectionList(boolean, int, Class)} constructor was used.
	 */
	public E[] items;

	public int size;
	public boolean ordered;

	@Nullable SeqIterable<E> iterable;

	/** Creates an ordered array with a capacity of 16. */
	public CollectionList(Class<?> type) {
		this(true, 16, type);
	}

	/** Creates an ordered array with the specified capacity. */
	public CollectionList(int capacity, Class<?> type) {
		this(true, capacity, type);
	}

	/** Creates an ordered/unordered array with the specified capacity. */
	public CollectionList(boolean ordered, Class<?> type) {
		this(ordered, 16, type);
	}

	/**
	 * Creates a new array with {@link #items} of the specified type.
	 *
	 * @param ordered  If false, methods that remove elements may change the order of other elements in the array, which avoids a
	 *                 memory copy.
	 * @param capacity Any elements added beyond this will cause the backing array to be grown.
	 */
	@SuppressWarnings("unchecked")
	public CollectionList(boolean ordered, int capacity, Class<?> type) {
		this.ordered = ordered;
		componentType = type;
		items = (E[]) Array.newInstance(type, capacity);
	}

	/**
	 * Creates a new array containing the elements in the specified array. The new array will have the same type of backing array
	 * and will be ordered if the specified array is ordered. The capacity is set to the number of elements, so any subsequent
	 * elements added will cause the backing array to be grown.
	 */
	public CollectionList(CollectionList<? extends E> array) {
		this(array.ordered, array.size, array.componentType);
		size = array.size;
		System.arraycopy(array.items, 0, items, 0, size);
	}

	/**
	 * Creates a new ordered array containing the elements in the specified array. The new array will have the same type of
	 * backing array. The capacity is set to the number of elements, so any subsequent elements added will cause the backing array
	 * to be grown.
	 */
	public CollectionList(E[] array) {
		this(true, array, 0, array.length);
	}

	/**
	 * Creates a new array containing the elements in the specified array. The new array will have the same type of backing array.
	 * The capacity is set to the number of elements, so any subsequent elements added will cause the backing array to be grown.
	 *
	 * @param ordered If false, methods that remove elements may change the order of other elements in the array, which avoids a
	 *                memory copy.
	 */
	public CollectionList(boolean ordered, E[] array, int start, int count) {
		this(ordered, count, array.getClass().getComponentType());
		size = count;
		System.arraycopy(array, start, items, 0, size);
	}

	@SuppressWarnings("unchecked")
	public static <T> CollectionList<T> withArrays(Class<?> arrayType, Object... arrays) {
		CollectionList<T> result = new CollectionList<>(arrayType);
		for (Object a : arrays) {
			if (a instanceof CollectionList) {
				result.addAll((CollectionList<? extends T>) a);
			} else {
				result.add((T) a);
			}
		}
		return result;
	}

	/** @see #CollectionList(Object[]) */
	@SuppressWarnings("unchecked")
	public static <T> CollectionList<T> with(T... array) {
		return new CollectionList<>(array);
	}

	public static <T> CollectionList<T> with(Class<?> arrayType, Iterable<T> array) {
		CollectionList<T> out = new CollectionList<>(arrayType);
		for (T thing : array) {
			out.add(thing);
		}
		return out;
	}

	/** @see #CollectionList(Object[]) */
	public static <T> CollectionList<T> select(T[] array, Boolf<T> test) {
		CollectionList<T> out = new CollectionList<>(array.length, array.getClass().getComponentType());
		for (T t : array) {
			if (test.get(t)) {
				out.add(t);
			}
		}
		return out;
	}

	public <K, V> CollectionObjectMap<K, V> asMap(Func<E, K> keygen, Func<E, V> valgen, Class<?> keyType, Class<?> valueType) {
		CollectionObjectMap<K, V> map = new CollectionObjectMap<>(keyType, valueType);
		for (int i = 0; i < size; i++) {
			map.put(keygen.get(items[i]), valgen.get(items[i]));
		}
		return map;
	}

	public <K> CollectionObjectMap<K, E> asMap(Func<E, K> keygen, Class<?> keyType, Class<?> valueType) {
		return asMap(keygen, t -> t, keyType, valueType);
	}

	public CollectionObjectSet<E> asSet() {
		return CollectionObjectSet.with(this);
	}

	public CollectionList<E> copy() {
		return new CollectionList<>(this);
	}

	public float sumf(Floatf<E> summer) {
		float sum = 0;
		for (int i = 0; i < size; i++) {
			sum += summer.get(items[i]);
		}
		return sum;
	}

	public int sum(Intf<E> summer) {
		int sum = 0;
		for (int i = 0; i < size; i++) {
			sum += summer.get(items[i]);
		}
		return sum;
	}

	@SuppressWarnings("unchecked")
	public <T extends E> void each(Boolf<? super E> pred, Cons<T> consumer) {
		for (int i = 0; i < size; i++) {
			if (pred.get(items[i])) consumer.get((T) items[i]);
		}
	}

	@Override
	public void each(Cons<? super E> consumer) {
		for (int i = 0; i < size; i++) {
			consumer.get(items[i]);
		}
	}

	/** Replaces values without creating a new array. */
	public void replace(Func<E, E> mapper) {
		for (int i = 0; i < size; i++) {
			items[i] = mapper.get(items[i]);
		}
	}

	/** Flattens this array of arrays into one array. Allocates a new instance. */
	@SuppressWarnings("unchecked")
	public <R> CollectionList<R> flatten() {
		CollectionList<R> arr = new CollectionList<>(componentType);
		for (int i = 0; i < size; i++) {
			arr.addAll((CollectionList<R>) items[i]);
		}
		return arr;
	}

	/** Returns a new array with the mapped values. */
	public <R> CollectionList<R> flatMap(Func<E, Iterable<R>> mapper) {
		CollectionList<R> arr = new CollectionList<>(size, componentType);
		for (int i = 0; i < size; i++) {
			arr.addAll(mapper.get(items[i]));
		}
		return arr;
	}

	/** Returns a new array with the mapped values. */
	public <R> CollectionList<R> map(Func<E, R> mapper) {
		CollectionList<R> arr = new CollectionList<>(size, componentType);
		for (int i = 0; i < size; i++) {
			arr.add(mapper.get(items[i]));
		}
		return arr;
	}

	public <R> Seq<R> mapSeq(Func<E, R> mapper) {
		Seq<R> arr = new Seq<>(true, size, componentType);
		for (int i = 0; i < size; i++) {
			arr.add(mapper.get(items[i]));
		}
		return arr;
	}

	/** @return a new int array with the mapped values. */
	public IntSeq mapInt(Intf<E> mapper) {
		IntSeq arr = new IntSeq(size);
		for (int i = 0; i < size; i++) {
			arr.add(mapper.get(items[i]));
		}
		return arr;
	}

	/** @return a new int array with the mapped values. */
	public IntSeq mapInt(Intf<E> mapper, Boolf<E> retain) {
		IntSeq arr = new IntSeq(size);
		for (int i = 0; i < size; i++) {
			E item = items[i];
			if (retain.get(item)) {
				arr.add(mapper.get(item));
			}
		}
		return arr;
	}

	/** @return a new float array with the mapped values. */
	public FloatSeq mapFloat(Floatf<E> mapper) {
		FloatSeq arr = new FloatSeq(size);
		for (int i = 0; i < size; i++) {
			arr.add(mapper.get(items[i]));
		}
		return arr;
	}

	public <R> R reduce(R initial, Func2<E, R, R> reducer) {
		R result = initial;
		for (int i = 0; i < size; i++) {
			result = reducer.get(items[i], result);
		}
		return result;
	}

	public boolean allMatch(Boolf<E> predicate) {
		for (int i = 0; i < size; i++) {
			if (!predicate.get(items[i])) {
				return false;
			}
		}
		return true;
	}

	public boolean contains(Boolf<E> predicate) {
		for (int i = 0; i < size; i++) {
			if (predicate.get(items[i])) {
				return true;
			}
		}
		return false;
	}

	public E min(Comparator<E> func) {
		E result = null;
		for (int i = 0; i < size; i++) {
			E t = items[i];
			if (result == null || func.compare(result, t) > 0) {
				result = t;
			}
		}
		return result;
	}

	public E max(Comparator<E> func) {
		E result = null;
		for (int i = 0; i < size; i++) {
			E t = items[i];
			if (result == null || func.compare(result, t) < 0) {
				result = t;
			}
		}
		return result;
	}

	public E min(Boolf<E> filter, Floatf<E> func) {
		E result = null;
		float min = Float.MAX_VALUE;
		for (int i = 0; i < size; i++) {
			E t = items[i];
			if (!filter.get(t)) continue;
			float val = func.get(t);
			if (val <= min) {
				result = t;
				min = val;
			}
		}
		return result;
	}

	public E min(Boolf<E> filter, Comparator<E> func) {
		E result = null;
		for (int i = 0; i < size; i++) {
			E t = items[i];
			if (filter.get(t) && (result == null || func.compare(result, t) > 0)) {
				result = t;
			}
		}
		return result;
	}

	public E min(Floatf<E> func) {
		E result = null;
		float min = Float.MAX_VALUE;
		for (int i = 0; i < size; i++) {
			E t = items[i];
			float val = func.get(t);
			if (val <= min) {
				result = t;
				min = val;
			}
		}
		return result;
	}

	public E max(Floatf<E> func) {
		E result = null;
		float max = Float.NEGATIVE_INFINITY;
		for (int i = 0; i < size; i++) {
			E t = items[i];
			float val = func.get(t);
			if (val >= max) {
				result = t;
				max = val;
			}
		}
		return result;
	}

	@Nullable
	public E find(Boolf<E> predicate) {
		for (int i = 0; i < size; i++) {
			if (predicate.get(items[i])) {
				return items[i];
			}
		}
		return null;
	}

	public CollectionList<E> with(Cons<CollectionList<E>> cons) {
		cons.get(this);
		return this;
	}

	/**
	 * Adds a value if it was not already in this sequence.
	 *
	 * @return whether this value was added successfully.
	 */
	public boolean addUnique(E value) {
		if (!contains(value)) {
			add(value);
			return true;
		}
		return false;
	}

	@Override
	public boolean add(E value) {
		if (size == items.length) items = resize(Math.max(8, (int) (size * 1.75f)));
		items[size++] = value;
		return true;
	}

	public CollectionList<E> add(E value1, E value2) {
		if (size + 1 >= items.length) items = resize(Math.max(8, (int) (size * 1.75f)));
		items[size] = value1;
		items[size + 1] = value2;
		size += 2;
		return this;
	}

	public CollectionList<E> add(E value1, E value2, E value3) {
		if (size + 2 >= items.length) items = resize(Math.max(8, (int) (size * 1.75f)));
		items[size] = value1;
		items[size + 1] = value2;
		items[size + 2] = value3;
		size += 3;
		return this;
	}

	public CollectionList<E> add(E value1, E value2, E value3, E value4) {
		if (size + 3 >= items.length)
			items = resize(Math.max(8, (int) (size * 1.8f))); // 1.75 isn't enough when size=5.
		items[size] = value1;
		items[size + 1] = value2;
		items[size + 2] = value3;
		items[size + 3] = value4;
		size += 4;
		return this;
	}

	public CollectionList<E> add(CollectionList<? extends E> array) {
		addAll(array.items, 0, array.size);
		return this;
	}

	public CollectionList<E> add(E[] array) {
		addAll(array, 0, array.length);
		return this;
	}

	public CollectionList<E> addAll(CollectionList<? extends E> array) {
		addAll(array.items, 0, array.size);
		return this;
	}

	public CollectionList<E> addAll(CollectionList<? extends E> array, int start, int count) {
		if (start + count > array.size)
			throw new IllegalArgumentException("start + count must be <= size: " + start + " + " + count + " <= " + array.size);
		addAll(array.items, start, count);
		return this;
	}

	@SuppressWarnings("unchecked")
	public CollectionList<E> addAll(E... array) {
		addAll(array, 0, array.length);
		return this;
	}

	public CollectionList<E> addAll(E[] array, int start, int count) {
		int sizeNeeded = size + count;
		if (sizeNeeded > items.length) items = resize(Math.max(8, (int) (sizeNeeded * 1.75f)));
		System.arraycopy(array, start, items, size, count);
		size += count;
		return this;
	}

	@SuppressWarnings("unchecked")
	public CollectionList<E> addAll(Iterable<? extends E> items) {
		if (items instanceof CollectionList) {
			addAll((CollectionList<E>) items);
		} else {
			for (E t : items) {
				add(t);
			}
		}
		return this;
	}

	/** Sets this array's contents to the specified array. */
	public void set(CollectionList<? extends E> array) {
		clear();
		addAll(array);
	}

	/** Sets this array's contents to the specified array. */
	public void set(E[] array) {
		clear();
		addAll(array);
	}

	@Nullable
	public E getFrac(float index) {
		if (isEmpty()) return null;
		return get(Mathf.clamp((int) (index * size), 0, size - 1));
	}

	@Override
	public E get(int index) {
		if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
		return items[index];
	}

	public E get(int index, E def) {
		if (index >= size || index <= 0) return def;
		return items[index];
	}

	@Override
	public E set(int index, E value) {
		if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
		items[index] = value;
		return value;
	}

	@Override
	public void add(int index, E element) {
		final int s;
		Object[] elementData;
		if ((s = size) == (elementData = items).length)
			elementData = grow();
		System.arraycopy(elementData, index,
				elementData, index + 1,
				s - index);
		elementData[index] = element;
		size = s + 1;
	}

	/**
	 * Increases the capacity to ensure that it can hold at least the
	 * number of elements specified by the minimum capacity argument.
	 *
	 * @param minCapacity the desired minimum capacity
	 * @throws OutOfMemoryError if minCapacity is less than zero
	 */
	@SuppressWarnings("unchecked")
	Object[] grow(int minCapacity) {
		int oldCapacity = items.length;
		if (oldCapacity > 0) {
			int newCapacity = Utils.newLength(oldCapacity,
					minCapacity - oldCapacity, // minimum growth
					oldCapacity >> 1);// preferred growth
			return items = Arrays.copyOf(items, newCapacity);
		} else {
			return items = (E[]) Array.newInstance(componentType, Math.max(16, minCapacity));
		}
	}

	Object[] grow() {
		return grow(size + 1);
	}

	public void insert(int index, E value) {
		if (index > size) throw new IndexOutOfBoundsException("index can't be > size: " + index + " > " + size);
		if (size == items.length) items = resize(Math.max(8, (int) (size * 1.75f)));
		if (ordered)
			System.arraycopy(items, index, items, index + 1, size - index);
		else
			items[size] = items[index];
		size++;
		items[index] = value;
	}

	public void swap(int first, int second) {
		if (first >= size) throw new IndexOutOfBoundsException("first can't be >= size: " + first + " >= " + size);
		if (second >= size) throw new IndexOutOfBoundsException("second can't be >= size: " + second + " >= " + size);
		E firstValue = items[first];
		items[first] = items[second];
		items[second] = firstValue;
	}

	/**
	 * Replaces the first occurrence of 'from' with 'to'.
	 *
	 * @return whether anything was replaced.
	 *
	 */
	public boolean replace(E from, E to) {
		int idx = indexOf(from);
		if (idx != -1) {
			items[idx] = to;
			return true;
		}
		return false;
	}

	/** @return whether this sequence contains every other element in the other sequence. */
	public boolean containsAll(CollectionList<E> seq) {
		return containsAll(seq, false);
	}

	/** @return whether this sequence contains every other element in the other sequence. */
	public boolean containsAll(CollectionList<E> seq, boolean identity) {
		E[] others = seq.items;

		for (int i = 0; i < seq.size; i++) {
			if (!contains(others[i], identity)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean contains(Object value) {
		return contains(value, false);
	}

	/**
	 * Returns if this array contains value.
	 *
	 * @param value    May be null.
	 * @param identity If true, == comparison will be used. If false, .equals() comparison will be used.
	 * @return true if array contains value, false if it doesn't
	 */
	public boolean contains(Object value, boolean identity) {
		int i = size - 1;
		if (identity || value == null) {
			while (i >= 0)
				if (items[i--] == value) return true;
		} else {
			while (i >= 0)
				if (value.equals(items[i--])) return true;
		}
		return false;
	}

	@Override
	public int indexOf(Object value) {
		return indexOf(value, false);
	}

	@Override
	public int lastIndexOf(Object o) {
		if (o == null) {
			for (int i = size; i >= 0; i--)
				if (items[i] == null) return i;
		} else {
			for (int i = size; i >= 0; i--)
				if (o.equals(items[i])) return i;
		}
		return -1;
	}

	/**
	 * Returns the index of first occurrence of value in the array, or -1 if no such value exists.
	 *
	 * @param value    May be null.
	 * @param identity If true, == comparison will be used. If false, .equals() comparison will be used.
	 * @return An index of first occurrence of value in array or -1 if no such value exists
	 */
	public int indexOf(Object value, boolean identity) {
		if (identity || value == null) {
			for (int i = 0, n = size; i < n; i++)
				if (items[i] == value) return i;
		} else {
			for (int i = 0, n = size; i < n; i++)
				if (value.equals(items[i])) return i;
		}
		return -1;
	}

	public int indexOf(Boolf<E> value) {
		for (int i = 0, n = size; i < n; i++)
			if (value.get(items[i])) return i;
		return -1;
	}

	/**
	 * Returns an index of last occurrence of value in array or -1 if no such value exists. Search is started from the end of an
	 * array.
	 *
	 * @param value    May be null.
	 * @param identity If true, == comparison will be used. If false, .equals() comparison will be used.
	 * @return An index of last occurrence of value in array or -1 if no such value exists
	 */
	public int lastIndexOf(E value, boolean identity) {
		if (identity || value == null) {
			for (int i = size - 1; i >= 0; i--)
				if (items[i] == value) return i;
		} else {
			for (int i = size - 1; i >= 0; i--)
				if (value.equals(items[i])) return i;
		}
		return -1;
	}

	/** Removes a value, without using identity. */
	public boolean remove(Object value) {
		return remove(value, false);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for (Object e : c)
			if (!contains(e))
				return false;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		boolean modified = false;
		for (E e : c) {
			add(e);
			modified = true;
		}
		return modified;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		boolean modified = false;
		for (E e : c) {
			add(index++, e);
			modified = true;
		}
		return modified;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean modified = false;
		Iterator<?> it = iterator();
		while (it.hasNext()) {
			if (c.contains(it.next())) {
				it.remove();
				modified = true;
			}
		}
		return modified;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean modified = false;
		Iterator<E> it = iterator();
		while (it.hasNext()) {
			if (!c.contains(it.next())) {
				it.remove();
				modified = true;
			}
		}
		return modified;
	}

	/**
	 * Removes a single value by predicate.
	 *
	 * @return whether the item was found and removed.
	 */
	public boolean remove(Boolf<E> value) {
		for (int i = 0; i < size; i++) {
			if (value.get(items[i])) {
				remove(i);
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes the first instance of the specified value in the array.
	 *
	 * @param value    May be null.
	 * @param identity If true, == comparison will be used. If false, .equals() comparison will be used.
	 * @return true if value was found and removed, false otherwise
	 */
	public boolean remove(Object value, boolean identity) {
		if (identity || value == null) {
			for (int i = 0, n = size; i < n; i++) {
				if (items[i] == value) {
					remove(i);
					return true;
				}
			}
		} else {
			for (int i = 0, n = size; i < n; i++) {
				if (value.equals(items[i])) {
					remove(i);
					return true;
				}
			}
		}
		return false;
	}

	public boolean removeAll(Object value, boolean identity) {
		boolean modified = false;

		if (identity || value == null) {
			for (int i = 0, n = size; i < n; i++) {
				if (items[i] == value) {
					remove(i);
					modified = true;
				}
			}
		} else {
			for (int i = 0, n = size; i < n; i++) {
				if (value.equals(items[i])) {
					remove(i);
					modified = true;
				}
			}
		}
		return modified;
	}

	/** Removes and returns the item at the specified index. */
	@Override
	public E remove(int index) {
		if (index >= size) throw new IndexOutOfBoundsException("index can't be >= size: " + index + " >= " + size);
		E value = items[index];
		size--;
		if (ordered)
			System.arraycopy(items, index + 1, items, index, size - index);
		else
			items[index] = items[size];
		items[size] = null;
		return value;
	}

	/** Removes the items between the specified indices, inclusive. */
	@Override
	public void removeRange(int start, int end) {
		if (end >= size) throw new IndexOutOfBoundsException("end can't be >= size: " + end + " >= " + size);
		if (start > end) throw new IndexOutOfBoundsException("start can't be > end: " + start + " > " + end);
		int count = end - start + 1;
		if (ordered)
			System.arraycopy(items, start + count, items, start, size - (start + count));
		else {
			int lastIndex = size - 1;
			for (int i = 0; i < count; i++)
				items[start + i] = items[lastIndex - i];
		}
		size -= count;
	}

	/** @return this object */
	public CollectionList<E> removeAll(Boolf<E> pred) {
		Iterator<E> iter = iterator();
		while (iter.hasNext()) {
			if (pred.get(iter.next())) {
				iter.remove();
			}
		}
		return this;
	}

	public boolean removeAll(CollectionList<? extends E> array) {
		return removeAll(array, false);
	}

	/**
	 * Removes from this array all of elements contained in the specified array.
	 *
	 * @param identity True to use ==, false to use .equals().
	 * @return true if this array was modified.
	 */
	public boolean removeAll(CollectionList<? extends E> array, boolean identity) {
		int localSize = size;
		int startSize = localSize;
		if (identity) {
			for (int i = 0, n = array.size; i < n; i++) {
				E item = array.get(i);
				for (int ii = 0; ii < localSize; ii++) {
					if (item == items[ii]) {
						remove(ii);
						localSize--;
						break;
					}
				}
			}
		} else {
			for (int i = 0, n = array.size; i < n; i++) {
				E item = array.get(i);
				for (int ii = 0; ii < localSize; ii++) {
					if (item.equals(items[ii])) {
						remove(ii);
						localSize--;
						break;
					}
				}
			}
		}
		return localSize != startSize;
	}

	/**
	 * If this array is empty, returns an object specified by the constructor.
	 * Otherwise, acts like pop().
	 */
	public E pop(Prov<E> constructor) {
		if (size == 0) return constructor.get();
		return pop();
	}

	/** Removes and returns the last item. */
	public E pop() {
		if (size == 0) throw new IllegalStateException("Array is empty.");
		--size;
		E item = items[size];
		items[size] = null;
		return item;
	}

	/** Returns the last item. */
	public E peek() {
		if (size == 0) throw new IllegalStateException("Array is empty.");
		return items[size - 1];
	}

	/** Returns the first item. */
	public E first() {
		if (size == 0) throw new IllegalStateException("Array is empty.");
		return items[0];
	}

	public E peek(Prov<E> constructor) {
		if (size == 0) return constructor.get();
		return items[size - 1];
	}

	public E first(Prov<E> constructor) {
		if (size == 0) return constructor.get();
		return items[0];
	}

	/** Returns the first item, or null if this Seq is empty. */
	@Nullable
	public E firstOpt() {
		if (size == 0) return null;
		return items[0];
	}

	@Override
	public int size() {
		return size;
	}

	/** Returns true if the array is empty. */
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	public boolean any() {
		return size > 0;
	}

	@Override
	public void clear() {
		for (int i = 0, n = size; i < n; i++)
			items[i] = null;
		size = 0;
	}

	/**
	 * Reduces the size of the backing array to the size of the actual items. This is useful to release memory when many items
	 * have been removed, or if it is known that more items will not be added.
	 *
	 * @return {@link #items}
	 */
	public E[] shrink() {
		if (items.length != size) resize(size);
		return items;
	}

	/**
	 * Increases the size of the backing array to accommodate the specified number of additional items. Useful before adding many
	 * items to avoid multiple backing array resizes.
	 *
	 * @return {@link #items}
	 */
	public E[] ensureCapacity(int additionalCapacity) {
		if (additionalCapacity < 0)
			throw new IllegalArgumentException("additionalCapacity must be >= 0: " + additionalCapacity);
		int sizeNeeded = size + additionalCapacity;
		if (sizeNeeded > items.length) resize(Math.max(8, sizeNeeded));
		return items;
	}

	/**
	 * Sets the array size, leaving any values beyond the current size null.
	 *
	 * @return {@link #items}
	 */
	public E[] setSize(int newSize) {
		truncate(newSize);
		if (newSize > items.length) resize(Math.max(8, newSize));
		size = newSize;
		return items;
	}

	/** Creates a new backing array with the specified size containing the current items. */
	@SuppressWarnings("unchecked")
	protected E[] resize(int newSize) {
		//avoid reflection when possible
		E[] newItems = (E[]) (items.getClass() == Object[].class ? new Object[newSize] : Array.newInstance(items.getClass().getComponentType(), newSize));
		System.arraycopy(items, 0, newItems, 0, Math.min(size, newItems.length));
		items = newItems;
		return newItems;
	}

	/**
	 * Sorts this array. The array elements must implement {@link Comparable}. This method is not thread safe (uses
	 * {@link Sort#instance()}).
	 */
	public CollectionList<E> sort() {
		Sort.instance().sort(items, 0, size);
		return this;
	}

	/** Sorts the array. This method is not thread safe (uses {@link Sort#instance()}). */
	@Override
	public void sort(Comparator<? super E> comparator) {
		Sort.instance().sort(items, comparator, 0, size);
	}

	public CollectionList<E> sort(Floatf<? super E> comparator) {
		Sort.instance().sort(items, Structs.comparingFloat(comparator), 0, size);
		return this;
	}

	public <U extends Comparable<? super U>> CollectionList<E> sortComparing(Func<? super E, ? extends U> keyExtractor) {
		sort(Structs.comparing(keyExtractor));
		return this;
	}

	public CollectionList<E> selectFrom(CollectionList<E> base, Boolf<E> predicate) {
		clear();
		base.each(t -> {
			if (predicate.get(t)) {
				add(t);
			}
		});
		return this;
	}

	/** Note that this allocates a new set. Mutates. */
	public CollectionList<E> distinct() {
		CollectionObjectSet<E> set = asSet();
		clear();
		addAll(set);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <R> CollectionList<R> as() {
		return (CollectionList<R>) this;
	}

	/** Allocates a new array with all elements that match the predicate. */
	public CollectionList<E> select(Boolf<E> predicate) {
		CollectionList<E> arr = new CollectionList<>(componentType);
		for (int i = 0; i < size; i++) {
			if (predicate.get(items[i])) {
				arr.add(items[i]);
			}
		}
		return arr;
	}

	/** Removes everything that does not match this predicate. */
	public CollectionList<E> retainAll(Boolf<E> predicate) {
		return removeAll(e -> !predicate.get(e));
	}

	public int count(Boolf<E> predicate) {
		int count = 0;
		for (int i = 0; i < size; i++) {
			if (predicate.get(items[i])) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Selects the nth-lowest element from the Seq according to Comparator ranking. This might partially sort the Array. The
	 * array must have a size greater than 0, or a {@link ArcRuntimeException} will be thrown.
	 *
	 * @param comparator used for comparison
	 * @param kthLowest  rank of desired object according to comparison, n is based on ordinal numbers, not array indices. for min
	 *                   value use 1, for max value use size of array, using 0 results in runtime exception.
	 * @return the value of the Nth lowest ranked object.
	 * @see Select
	 */
	public E selectRanked(Comparator<E> comparator, int kthLowest) {
		if (kthLowest < 1) {
			throw new ArcRuntimeException("nth_lowest must be greater than 0, 1 = first, 2 = second...");
		}
		return Select.instance().select(items, comparator, kthLowest, size);
	}

	/**
	 * @param comparator used for comparison
	 * @param kthLowest  rank of desired object according to comparison, n is based on ordinal numbers, not array indices. for min
	 *                   value use 1, for max value use size of array, using 0 results in runtime exception.
	 * @return the index of the Nth lowest ranked object.
	 * @see CollectionList#selectRanked(java.util.Comparator, int)
	 */
	public int selectRankedIndex(Comparator<E> comparator, int kthLowest) {
		if (kthLowest < 1) {
			throw new ArcRuntimeException("nth_lowest must be greater than 0, 1 = first, 2 = second...");
		}
		return Select.instance().selectIndex(items, comparator, kthLowest, size);
	}

	public CollectionList<E> reverse() {
		for (int i = 0, lastIndex = size - 1, n = size / 2; i < n; i++) {
			int ii = lastIndex - i;
			E temp = items[i];
			items[i] = items[ii];
			items[ii] = temp;
		}

		return this;
	}

	public CollectionList<E> shuffle() {
		for (int i = size - 1; i >= 0; i--) {
			int j = Mathf.random(i);
			E temp = items[i];
			items[i] = items[j];
			items[j] = temp;
		}

		return this;
	}

	/**
	 * Reduces the size of the array to the specified size. If the array is already smaller than the specified size, no action is
	 * taken.
	 */
	public void truncate(int newSize) {
		if (newSize < 0) throw new IllegalArgumentException("newSize must be >= 0: " + newSize);
		if (size <= newSize) return;
		for (int i = newSize; i < size; i++)
			items[i] = null;
		size = newSize;
	}

	public E random(Rand rand) {
		if (size == 0) return null;
		return items[rand.random(0, size - 1)];
	}

	/** Returns a random item from the array, or null if the array is empty. */
	public E random() {
		return random(Mathf.rand);
	}

	/**
	 * Returns a random item from the array, excluding the specified element. If the array is empty, returns null.
	 * If this array only has one element, returns that element.
	 */
	public E random(E exclude) {
		if (exclude == null) return random();
		if (size == 0) return null;
		if (size == 1) return first();

		int eidx = indexOf(exclude);
		//this item isn't even in the array!
		if (eidx == -1) return random();

		//shift up the index
		int index = Mathf.random(0, size - 2);
		if (index >= eidx) {
			index++;
		}
		return items[index];
	}

	/**
	 * Returns the items as an array. Note the array is typed, so the {@link #CollectionList(Class)} constructor must have been used.
	 * Otherwise use {@link #toArray()} to specify the array type.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public E[] toArray() {
		E[] result = (E[]) Array.newInstance(componentType, size);
		System.arraycopy(items, 0, result, 0, size);
		return result;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		if (a.length < size)
			// Make a new array of a's runtime type, but my contents:
			return (T[]) Arrays.copyOf(items, size, a.getClass());
		System.arraycopy(items, 0, a, 0, size);
		if (a.length > size)
			a[size] = null;
		return a;
	}

	@Override
	public int hashCode() {
		if (!ordered) return super.hashCode();
		int h = 1;
		for (int i = 0, n = size; i < n; i++) {
			h *= 31;
			Object item = items[i];
			if (item != null) h += item.hashCode();
		}
		return h;
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) return true;
		if (!ordered) return false;
		if (!(object instanceof CollectionList<?> array)) return false;
		if (!array.ordered) return false;
		int n = size;
		if (n != array.size) return false;
		Object[] items1 = items;
		Object[] items2 = array.items;
		for (int i = 0; i < n; i++) {
			Object o1 = items1[i];
			Object o2 = items2[i];
			if (!(o1 == null ? o2 == null : o1.equals(o2))) return false;
		}
		return true;
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

	public String toString(String separator, Func<E, String> stringifier) {
		if (size == 0) return "";
		StringBuilder buffer = new StringBuilder(32);
		buffer.append(stringifier.get(items[0]));
		for (int i = 1; i < size; i++) {
			buffer.append(separator);
			buffer.append(stringifier.get(items[i]));
		}
		return buffer.toString();
	}

	public String toString(String separator) {
		return toString(separator, String::valueOf);
	}

	/**
	 * Returns an iterator for the items in the array. Remove is supported. Note that the same iterator instance is returned each
	 * time this method is called, unless you are using nested loops.
	 * <b>Never, ever</b> access this iterator's method manually, e.g. hasNext()/next().
	 * Note that calling 'break' while iterating will permanently clog this iterator, falling back to an implementation that allocates new ones.
	 */
	@Override
	public Iterator<E> iterator() {
		if (iterable == null) iterable = new SeqIterable<>(this);
		return iterable.iterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		if (index > size || index < 0)
			throw new IndexOutOfBoundsException("index can't be > size: " + index + " > " + size);

		if (iterable == null) iterable = new SeqIterable<>(this);

		return iterable.listIterator(index);
	}

	public Seq<E> toSeq() {
		Seq<E> seq = new Seq<>(true, size, componentType);
		for (E e : this) {
			seq.add(e);
		}
		return seq;
	}

	public static class SeqIterable<T> implements Iterable<T> {
		final CollectionList<T> array;
		final boolean allowRemove;

		SeqIterator iterator1, iterator2, lastIterator1;

		public SeqIterable(CollectionList<T> array) {
			this(array, true);
		}

		public SeqIterable(CollectionList<T> arr, boolean remove) {
			array = arr;
			allowRemove = remove;
		}

		@Override
		public Iterator<T> iterator() {
			if (iterator1 == null) iterator1 = new SeqIterator();

			if (iterator1.done) {
				iterator1.cursor = 0;
				iterator1.done = false;
				return iterator1;
			}

			if (iterator2 == null) iterator2 = new SeqIterator();

			if (iterator2.done) {
				iterator2.cursor = 0;
				iterator2.done = false;
				return iterator2;
			}
			//allocate new iterator in the case of 3+ nested loops.
			return new SeqIterator();
		}

		public ListIterator<T> listIterator(int index) {
			if (lastIterator1 == null) lastIterator1 = new SeqIterator(index);

			if (lastIterator1.done) {
				lastIterator1.cursor = index;
				lastIterator1.done = false;
				return lastIterator1;
			}

			return new SeqIterator(index);
		}

		class SeqIterator implements ListIterator<T> {
			int cursor;
			boolean done = true;

			SeqIterator(int index) {
				cursor = index;
				iteratorsAllocated++;
			}

			SeqIterator() {
				iteratorsAllocated++;
			}

			@Override
			public boolean hasNext() {
				if (cursor >= array.size) done = true;
				return cursor < array.size;
			}

			@Override
			public T next() {
				if (cursor >= array.size) throw new NoSuchElementException(String.valueOf(cursor));
				return array.items[cursor++];
			}

			@Override
			public boolean hasPrevious() {
				return cursor > 0;
			}

			@Override
			public T previous() {
				return array.items[cursor - 1];
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
			public void remove() {
				if (!allowRemove) throw new ArcRuntimeException("Remove not allowed.");
				cursor--;
				array.remove(cursor);
			}

			@Override
			public void set(T t) {
				array.set(cursor, t);
			}

			@Override
			public void add(T t) {
				array.add(t);
			}
		}
	}
}
