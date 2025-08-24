package heavyindustry.func;

public interface RunT<T extends Throwable> {
	void run() throws T;
}
