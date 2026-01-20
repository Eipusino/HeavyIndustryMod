package heavyindustry.async;

import arc.func.Prov;

import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Provides multithreading utilities, primarily synchronizations from threads to the main thread for OpenGL
 * purposes.
 *
 * @since 1.0.7
 */
public final class Asyncs {
	private Asyncs() {}

	/** Please use {@link AsyncsKt#get(Future)} */
	@Deprecated
	public static <T> T get(Future<T> future) {
		return AsyncsKt.get(future);
	}

	/** Please use {@link AsyncsKt#postWait(Runnable)} */
	@Deprecated
	public static void postWait(Runnable runSync) {
		AsyncsKt.postWait(runSync);
	}

	/** Please use {@link AsyncsKt#postWait(Prov)} */
	@Deprecated
	public static <T> T postWait(Prov<T> runSync) {
		return AsyncsKt.postWait(runSync);
	}

	public static <T> T lock(Lock lock, Prov<T> prov) {
		lock.lock();
		T out = prov.get();

		lock.unlock();
		return out;
	}

	public static void lock(Lock lock, Runnable run) {
		lock.lock();
		run.run();
		lock.unlock();
	}

	public static <T> T read(ReadWriteLock lock, Prov<T> prov) {
		return lock(lock.readLock(), prov);
	}

	public static void read(ReadWriteLock lock, Runnable run) {
		lock(lock.readLock(), run);
	}

	public static <T> T write(ReadWriteLock lock, Prov<T> prov) {
		return lock(lock.writeLock(), prov);
	}

	public static void write(ReadWriteLock lock, Runnable run) {
		lock(lock.writeLock(), run);
	}
}
