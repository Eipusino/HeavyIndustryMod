package heavyindustry.func;

@FunctionalInterface
public interface VariableFunc<P, R> {
	@SuppressWarnings("unchecked")
	R apply(P... params);
}
