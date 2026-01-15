package heavyindustry.util;

import arc.func.Cons;
import arc.util.Eachable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class HierarchyList<T> extends AbstractList<T> implements Eachable<T>, Cloneable {
	public final Class<?> componentType;

	public T[] array;
	public float[] scores;

	public int size = 0;

	protected @Nullable HierarchyIterator iterator1, iterator2;

	public HierarchyList(Class<?> arrayType) {
		this(16, arrayType);
	}

	@SuppressWarnings("unchecked")
	public HierarchyList(int size, Class<?> arrayType) {
		componentType = arrayType;

		array = (T[]) Array.newInstance(arrayType, size);
		scores = new float[size];
	}

	@SuppressWarnings("unchecked")
	public HierarchyList<T> copy() {
		try {
			HierarchyList<T> arr = (HierarchyList<T>) super.clone();
			arr.array = Arrays.copyOf(array, size);
			arr.scores = Arrays.copyOf(scores, size);
			return arr;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public T get(int idx) {
		if (idx >= size) return null;
		return array[idx];
	}

	public void add(T item, float score) {
		if (size >= array.length) return;

		for (int i = 0; i < array.length; i++) {
			T c = array[i];
			float s = scores[i];

			if (c == null) {
				array[i] = item;
				scores[i] = score;
				size++;
				break;
			} else {
				if (score > s) {
					array[i] = item;
					scores[i] = score;

					item = c;
					score = s;
				}
			}
		}
	}

	@Override
	public boolean remove(Object item) {
		for (int i = 0; i < size; i++) {
			T c = array[i];
			if (c == item) {
				remove(i);
				return true;
			}
		}
		return false;
	}

	@Override
	public T remove(int index) {
		T last = array[index];

		for (int i = index; i < size - 1; i++) {
			T n = array[i + 1];
			float scr = scores[i + 1];
			array[i] = n;
			array[i + 1] = null;
			scores[i] = scr;
			scores[i + 1] = 0f;
		}
		array[size - 1] = null;
		scores[size - 1] = 0f;
		size--;

		return last;
	}

	@Override
	public void clear() {
		Arrays.fill(array, null);
		Arrays.fill(scores, 0f);
		size = 0;
	}

	@Override
	public void each(Cons<? super T> cons) {
		for (int i = 0; i < size; i++) {
			cons.get(array[i]);
		}
	}

	@Override
	public @NotNull HierarchyIterator iterator() {
		if (iterator1 == null) iterator1 = new HierarchyIterator();

		if (iterator1.done) {
			iterator1.index = 0;
			iterator1.done = false;
			return iterator1;
		}

		if (iterator2 == null) iterator2 = new HierarchyIterator();

		if (iterator2.done) {
			iterator2.index = 0;
			iterator2.done = false;
			return iterator2;
		}

		return new HierarchyIterator();
	}

	@Override
	public int size() {
		return size;
	}

	public class HierarchyIterator implements Iterator<T> {
		protected int index = 0;
		protected boolean done = true;

		@Override
		public boolean hasNext() {
			if (index >= size) done = true;
			return index < size;
		}

		@Override
		public T next() {
			if (index >= size) throw new NoSuchElementException(String.valueOf(index));
			return array[index++];
		}

		@Override
		public void remove() {
			index--;
			HierarchyList.this.remove(index);
		}
	}
}
