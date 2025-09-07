package heavyindustry.func;

/** A variable-func that throws something. */
public interface VariableFuncT<P, R, E extends Throwable> {
	@SuppressWarnings("unchecked")
	R apply(P... apply) throws E;
}
