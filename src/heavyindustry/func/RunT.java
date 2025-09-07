package heavyindustry.func;

/** A run that throws something. */
public interface RunT<T extends Throwable> {
	void run() throws T;
}
