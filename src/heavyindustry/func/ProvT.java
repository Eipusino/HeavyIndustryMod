package heavyindustry.func;

/** A prov that throws something. */
@FunctionalInterface
public interface ProvT <T, E extends Throwable> {
	T get() throws E;
}
