package heavyindustry.util.concurrent;

import heavyindustry.util.Objects2;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class BoolReference implements Serializable, Comparable<BoolReference> {
	private static final long serialVersionUID = -7120385114040352042l;

	public boolean element;

	public BoolReference() {}

	public BoolReference(boolean initialElement) {
		element = initialElement;
	}

	@Contract(value = "_ -> new", pure = true)
	public static BoolReference valueOf(boolean value) {
		return new BoolReference(value);
	}

	@Contract(value = "_ -> new", pure = true)
	public static BoolReference valueOf(String value) {
		return new BoolReference(Boolean.parseBoolean(value));
	}

	@Override
	public String toString() {
		return String.valueOf(element);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof BoolReference ref && ref.element == element;
	}

	@Override
	public int hashCode() {
		return Objects2.boolToHash(element);
	}

	@Override
	public int compareTo(@NotNull BoolReference o) {
		return Boolean.compare(element, o.element);
	}
}
