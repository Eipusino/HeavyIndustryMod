package heavyindustry.util;

import arc.func.Cons;
import arc.func.Func2;
import arc.func.Intc2;
import arc.func.Prov;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.util.Eachable;
import arc.util.Nullable;

import java.lang.reflect.Array;
import java.util.Iterator;

public class Map2D<T> implements Iterable<T>, Eachable<T> {
	public final Class<?> componentType;
	public final int width, height;

	public final T[] array;

	Iter<T> iterator1, iterator2;

	@SuppressWarnings("unchecked")
	public Map2D(int wid, int hei, Class<?> type) {
		componentType = type;
		array = (T[]) Array.newInstance(type, wid * hei);
		width = wid;
		height = hei;
	}

	public void each(Intc2 cons) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				cons.get(x, y);
			}
		}
	}

	public void fill(Prov<T> constructor) {
		for (int i = 0; i < array.length; i++) {
			array[i] = constructor.get();
		}
	}

	public void fill(Func2<Integer, Integer, T> constructor) {
		for (int i = 0; i < array.length; i++) {
			array[i] = constructor.get(i % width, i / width);
		}
	}

	public int x(int v) {
		return v % width;
	}

	public int y(int v) {
		return v / width;
	}

	/** set a tile at a position; does not range-check. use with caution. */
	public void set(int x, int y, T tile) {
		array[y * width + x] = tile;
	}

	/** @return whether these coordinates are in bounds */
	public boolean in(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	/** @return a tile at coordinates, or null if out of bounds */
	@Nullable
	public T get(int x, int y) {
		return (x < 0 || x >= width || y < 0 || y >= height) ? null : array[y * width + x];
	}

	public int index(int x, int y) {
		return y * width + x;
	}

	public boolean has(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}

	public boolean has(int v) {
		return v < array.length && v >= 0;
	}

	/** @return a tile at coordinates; throws an exception if out of bounds */
	public T getThrow(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			throw new IllegalArgumentException(x + ", " + y + " out of bounds: width=" + width + ", height=" + height);
		return array[y * width + x];
	}

	/** @return a tile at coordinates, clamped. */
	public T getClamp(int x, int y) {
		x = Mathf.clamp(x, 0, width - 1);
		y = Mathf.clamp(y, 0, height - 1);
		return array[y * width + x];
	}

	/** @return a tile at an iteration index [0, width * height] */
	public T getIndex(int idx) {
		return array[idx];
	}

	/** @return a tile at an int position (not equivalent to getIndex) */
	@Nullable
	public T getPos(int pos) {
		return get(Point2.x(pos), Point2.y(pos));
	}

	@Override
	public void each(Cons<? super T> cons) {
		for (T tile : array) {
			cons.get(tile);
		}
	}

	@Override
	public Iterator<T> iterator() {
		if (iterator1 == null) iterator1 = new Iter<>(this);

		if (!iterator1.hasNext()) {
			iterator1.index = 0;
			return iterator1;
		}

		if (iterator2 == null) iterator2 = new Iter<>(this);

		if (!iterator2.hasNext()) {
			iterator2.index = 0;
			return iterator2;
		}

		return new Iter<>(this);
	}

	public static class Iter<T> implements Iterator<T> {
		final Map2D<T> map;

		int index = 0;

		public Iter(Map2D<T> m) {
			map = m;
		}

		@Override
		public boolean hasNext() {
			return index < map.array.length;
		}

		@Override
		public T next() {
			return map.array[index++];
		}
	}
}
