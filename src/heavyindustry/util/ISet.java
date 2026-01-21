package heavyindustry.util;

import java.util.Set;

public interface ISet<E> extends Set<E>, ICollection<E> {
	default void addAll(Set<? extends E> set) {}
}
