package heavyindustry.util.pools;

import arc.func.Prov;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.pooling.Pool;

@SuppressWarnings({"unchecked", "rawtypes"})
public final class ThreadSafePools {
	private static final ObjectMap<Class, ThreadSafePool> typePools = new ObjectMap<>();
	private static final Object poolCreateLock = new Object();
	private static final Object poolSetLock = new Object();

	/** Don't let anyone instantiate this class. */
	private ThreadSafePools() {}

	/**
	 * Returns a new or existing pool for the specified type, stored in a Class to {@link Pool} map. Note that the max size is ignored for some reason.
	 * if this is not the first time this pool has been requested.
	 */
	public static <T> ThreadSafePool<T> get(Class<T> type, Prov<T> supplier, int max) {
		ThreadSafePool<T> pool = typePools.get(type);
		if (pool == null) {
			synchronized (poolCreateLock) {
				ThreadSafePool<T> otherPool = typePools.get(type);
				if (otherPool == null) {
					pool = new ThreadSafePoolImpl<>(4, max, supplier);
					typePools.put(type, pool);
				} else {
					pool = otherPool;
				}
			}
		}
		return pool;
	}

	/**
	 * Returns a new or existing pool for the specified type, stored in a Class to {@link Pool} map. The max size of the pool used
	 * is 5000.
	 */
	public static <T> ThreadSafePool<T> get(Class<T> type, Prov<T> supplier) {
		return get(type, supplier, 5000);
	}

	/**
	 * Sets an existing pool for the specified type, stored in a Class to {@link Pool} map.
	 */
	public static <T> void set(Class<T> type, ThreadSafePool<T> pool) {
		synchronized (poolSetLock) {
			typePools.put(type, pool);
		}
	}

	/**
	 * Obtains an object from the {@link #get(Class, Prov) pool}.
	 */
	public static synchronized <T> T obtain(Class<T> type, Prov<T> supplier) {
		return get(type, supplier).obtain();
	}

	/**
	 * Frees an object from the {@link #get(Class, Prov) pool}.
	 */
	public static synchronized void free(Object object) {
		if (object == null) throw new IllegalArgumentException("Object cannot be null.");
		Pool pool = typePools.get(object.getClass());
		if (pool == null) return; // Ignore freeing an object that was never retained.
		pool.free(object);
	}

	/**
	 * Frees the specified objects from the {@link #get(Class, Prov) pool}. Null objects within the array are silently ignored. Objects
	 * don't need to be from the same pool.
	 */
	public static void freeAll(Seq objects) {
		freeAll(objects, false);
	}

	/**
	 * Frees the specified objects from the {@link #get(Class, Prov) pool}. Null objects within the array are silently ignored.
	 *
	 * @param samePool If true, objects don't need to be from the same pool but the pool must be looked up for each object.
	 */
	public static void freeAll(Seq objects, boolean samePool) {
		if (objects == null) throw new IllegalArgumentException("Objects cannot be null.");
		Pool pool = null;
		for (int i = 0, n = objects.size; i < n; i++) {
			Object object = objects.get(i);
			if (object == null) continue;
			if (pool == null) {
				pool = typePools.get(object.getClass());
				if (pool == null) continue; // Ignore freeing an object that was never retained.
			}
			pool.free(object);
			if (!samePool) pool = null;
		}
	}
}
