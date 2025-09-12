package heavyindustry.util;

import arc.func.Prov;

public class Lazy<T> {
	T value;
	volatile Prov<T> prov;

	public Lazy(Prov<T> p) {
		if (p == null) throw new IllegalArgumentException("The prov cannot be null.");
		prov = p;
	}

	public static <T> Lazy<T> of(Prov<T> prov) {
		return new Lazy<>(prov);
	}

	public T get() {
		if (prov != null) {
			synchronized (this) {
				if (prov != null) {
					value = prov.get();
					prov = null;
				}
			}
		}
		return value;
	}

	public void set(T v) {
		value = v;
	}
}
