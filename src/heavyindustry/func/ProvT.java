package heavyindustry.func;

/** A prov that throws something. */
@FunctionalInterface
public interface ProvT <R, T extends Throwable> {
	R get() throws T;
}
