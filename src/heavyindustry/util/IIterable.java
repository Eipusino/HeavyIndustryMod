package heavyindustry.util;

import arc.func.Cons;
import arc.util.Eachable;

public interface IIterable<T> extends Iterable<T>, Eachable<T> {
	default void forEach(Cons<? super T> cons) {
		each(cons);
	}

	@Override
	default void each(Cons<? super T> cons) {
		for (T t : this) {
			cons.get(t);
		}
	}
}
