package heavyindustry.util;

import arc.func.Prov;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Lazy<T> {
	public final boolean allowSet;

	protected T value;
	protected volatile Prov<T> prov;

	public Lazy(@NotNull Prov<T> init) {
		this(init, true);
	}

	/**
	 * @param init initialization function
	 * @param mod Whether the value can be modified
	 */
	public Lazy(@NotNull Prov<T> init, boolean mod) {
		prov = init;//if (init == null) throw new NullPointerException("The prov cannot be null.");
		allowSet = mod;
	}

	public static <T> Lazy<T> of(@NotNull Prov<T> prov) {
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
		if (allowSet) value = newValue;

		return value;
	}

	@Override
	public boolean equals(Object o) {
		return o == this || o instanceof Lazy<?> lazy && Objects.equals(value, lazy.value);
	}

	@Override
	public int hashCode() {
		return value == null ? 0 : value.hashCode();
	}

	@Override
	public String toString() {
		return "Lazy{" +
				"value=" + value +
				", modifiable=" + allowSet +
				'}';
	}
}
