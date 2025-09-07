package heavyindustry.func;

/** A prov that throws something. */
public interface ProvT <T, E extends Throwable> {
	T get() throws E;
}
