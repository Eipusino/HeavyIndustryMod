package heavyindustry.util;

import arc.func.Cons;
import arc.util.Eachable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class HierarchyArray<T> implements Iterable<T>, Eachable<T> {
	public final Class<?> componentType;

	public final T[] array;
	public final float[] scores;

	public int size = 0;

	protected HierarchyIterator iterator1, iterator2;

	public HierarchyArray(Class<?> arrayType) {
		this(16, arrayType);
	}

	@SuppressWarnings("unchecked")
	public HierarchyArray(int size, Class<?> arrayType) {
		componentType = arrayType;

		array = (T[]) Array.newInstance(arrayType, size);
		scores = new float[size];
	}

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

	public void remove(T item) {
		for (int i = 0; i < size; i++) {
			T c = array[i];
			if (c == item) {
				remove(i);
				break;
			}
		}
	}

	public void remove(int idx) {
		for (int i = idx; i < size - 1; i++) {
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
	}

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
	public HierarchyIterator iterator() {
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
			HierarchyArray.this.remove(index);
		}
	}
}
