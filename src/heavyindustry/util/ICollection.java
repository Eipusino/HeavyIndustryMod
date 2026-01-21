package heavyindustry.util;

import arc.func.Boolf;
import heavyindustry.func.Intg;

import java.util.Collection;
import java.util.Iterator;

public interface ICollection<E> extends Collection<E>, IIterable<E> {
	Class<?> componentType();

	@SuppressWarnings("unchecked")
	default void addAll(E... array) {}

	default void addAll(E[] array, int offset, int length) {}

	default void addAll(Iterable<? extends E> items) {}

	default void removeAll(E[] array, int offset, int length) {
		for (int i = offset, n = i + length; i < n; i++)
			remove(array[i]);
	}

	default void removeAll(E[] array) {
		for (E e : array) {
			remove(e);
		}
	}

	E first();

	boolean any();

	default <T> T[] toArray(Intg<T[]> generator) {
		return toArray(generator.get(0));
	}

	default boolean removeIf(Boolf<? super E> filter) {
		boolean removed = false;
		final Iterator<E> each = iterator();
		while (each.hasNext()) {
			if (filter.get(each.next())) {
				each.remove();
				removed = true;
			}
		}
		return removed;
	}
}
