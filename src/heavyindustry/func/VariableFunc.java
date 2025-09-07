package heavyindustry.func;

public interface VariableFunc<P, R> {
	@SuppressWarnings("unchecked")
	R apply(P... apply);
}
