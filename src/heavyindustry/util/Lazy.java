package heavyindustry.util;

import arc.func.Prov;

public class Lazy<T> {
	private T t;
	private Prov<T> prov;

	public Lazy(Prov<T> p) {
		if (p == null) throw new IllegalArgumentException("The prov cannot be null.");
		prov = p;
	}

	public static <T> Lazy<T> of(Prov<T> prov) {
		return new Lazy<>(prov);
	}

	public T get() {
		if (prov == null) return t;

		t = prov.get();
		prov = null;
		return t;
	}

	public void set(T value) {
		t = value;
	}
}
