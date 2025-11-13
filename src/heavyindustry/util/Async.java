package heavyindustry.util;

import arc.Core;
import arc.func.Prov;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Provides multithreading utilities, primarily synchronizations from threads to the main thread for OpenGL purposes.
 *
 * @since 1.0.7
 */
public final class Async {
	private Async() {}

	public static <T> T get(Future<T> future) {
		try {
			return future.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	public static void postWait(Runnable runSync) {
		Semaphore flag = new Semaphore(0);
		Core.app.post(() -> {
			try {
				runSync.run();
			} finally {
				flag.release();
			}
		});

		try {
			flag.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T postWait(Prov<T> runSync) {
		Semaphore flag = new Semaphore(0);

		Object[] out = new Object[1];
		Core.app.post(() -> {
			try {
				out[0] = runSync.get();
			} finally {
				flag.release();
			}
		});

		try {
			flag.acquire();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		return (T) out[0];
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
