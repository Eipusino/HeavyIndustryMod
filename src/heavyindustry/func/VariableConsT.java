package heavyindustry.func;

/** A variable-cons that throws something. */
public interface VariableConsT<T, E extends Throwable> {
	@SuppressWarnings("unchecked")
	void apply(T... args) throws E;
}
