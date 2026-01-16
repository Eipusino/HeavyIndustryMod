package heavyindustry.util;

import arc.func.Prov;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Used to delay the initialization of fields, values are only initialized when they are obtained for the first
 * time.
 *
 * @since 1.0.8
 */
public class Lazy<T> {
	/** Indicate whether the value can be reset again. default to true. */
	public final boolean allowSet;

	protected T value;
	protected volatile Prov<T> prov;

	public Lazy(@NotNull Prov<T> init) {
		this(init, true);
	}

	/**
	 * @param init initialization function
	 * @param set Whether the value allow to be set
	 */
	public Lazy(@NotNull Prov<T> init, boolean set) {
		prov = init;//if (init == null) throw new NullPointerException("The prov cannot be null.");
		allowSet = set;
	}

	@Contract(pure = true)
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
	public boolean equals(Object obj) {
		return obj == this || obj instanceof Lazy<?> lazy && Objects.equals(get(), lazy.get());
	}

	@Override
	public int hashCode() {
		Object obj = get();
		return obj == null ? 0 : obj.hashCode();
	}

	@Override
	public String toString() {
		return "Lazy{" +
				"value=" + get() +
				", allowSet=" + allowSet +
				'}';
	}
}
