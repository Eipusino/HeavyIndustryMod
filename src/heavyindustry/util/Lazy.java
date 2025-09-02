package heavyindustry.util;

import arc.func.Prov;

public class Lazy<T> {
	private static final Object UNINITIALIZED_VALUE = new Object();

	private final Object lock = new Object();

	private Prov<T> initializer;
	private Object value = UNINITIALIZED_VALUE;

	public Lazy(Prov<T> init) {
		initializer = init;
	}

	public void set(T val) {
		value = val;
	}

	@SuppressWarnings("unchecked")
	public T get() {
		Object o = value;
		if (o != UNINITIALIZED_VALUE) {
			return (T) o;
		}

		synchronized (lock) {
			Object s = value;
			if (s != UNINITIALIZED_VALUE) {
				return (T) s;
			}
			T typedValue = initializer.get();
			value = typedValue;
			initializer = null;
			return typedValue;
		}
	}
}
