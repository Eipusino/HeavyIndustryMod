package endfield.func;

/** A run that throws something. */
@FunctionalInterface
public interface RunT<T extends Throwable> {
	void run() throws T;
}
