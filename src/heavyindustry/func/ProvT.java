package heavyindustry.func;

public interface ProvT <T, E extends Throwable> {
	T get() throws E;
}
