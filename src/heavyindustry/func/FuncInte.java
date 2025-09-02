package heavyindustry.func;

import arc.func.Boolc;
import arc.func.Boolf;
import arc.func.Boolp;
import arc.func.Cons;
import arc.func.Cons2;
import arc.func.Cons3;
import arc.func.Cons4;
import arc.func.Floatp;
import arc.func.Func;
import arc.func.Prov;

public final class FuncInte {
	public static final Runnable RUNNABLE_NOTHING = () -> {};
	public static final Floatp ZERO_FLT = () -> 0f;
	public static final Boolc BOOLC_NOTHING = b -> {};
	public static final Boolp BOOLP_TRUE = () -> true;
	public static final Boolp BOOLP_FALSE = () -> false;

	private FuncInte() {}

	public static <T> Cons<T> cons() {
		return t -> {};
	}

	public static <T, U> Cons2<T, U> cons2() {
		return (t, u) -> {};
	}

	public static <T, U, V> Cons3<T, U, V> cons3() {
		return (t, u, v) -> {};
	}

	public static <T, U, V, W> Cons4<T, U, V, W> cons4() {
		return (t, u, v, w) -> {};
	}

	public static <T> Prov<T> prov(T t) {
		return () -> t;
	}

	public static <T> Boolf<T> boolf(boolean b) {
		return t -> b;
	}

	public static <T, R> Func<T, R> func(R r) {
		return t -> r;
	}
}
