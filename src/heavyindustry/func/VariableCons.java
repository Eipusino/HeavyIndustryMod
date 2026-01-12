package heavyindustry.func;

@FunctionalInterface
public interface VariableCons<T> {
	@SuppressWarnings("unchecked")
	void apply(T... args);
}
