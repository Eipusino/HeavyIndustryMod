package heavyindustry.util;

import arc.func.Prov;

public class Lazy<T> {
	public final boolean modifiable;

	T value;
	volatile Prov<T> prov;

	public Lazy(Prov<T> init) {
		this(init, true);
	}

	/**
	 * @param init initialization function
	 * @param mod Whether the value can be modified
	 */
	public Lazy(Prov<T> init, boolean mod) {
		prov = ObjectUtils.requireNonNull(init, "The prov cannot be null.");
		modifiable = mod;
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

	public T set(T newValue) {
		if (modifiable) value = newValue;

		return value;
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof Lazy<?> lazy && ObjectUtils.equals(value, lazy.value);
	}

	@Override
	public int hashCode() {
		return value == null ? 0 : value.hashCode();
	}

	@Override
	public String toString() {
		return "Lazy{" +
				"value=" + value +
				", modifiable=" + modifiable +
				'}';
	}
}
