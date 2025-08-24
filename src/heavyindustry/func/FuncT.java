package heavyindustry.func;

public interface FuncT<P, R, T extends Throwable> {
	R get(P param) throws T;
}
