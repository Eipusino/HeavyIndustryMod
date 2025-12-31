package heavyindustry.util.concurrent;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class ShortReference extends Number implements Serializable, Comparable<ShortReference> {
	private static final long serialVersionUID = -6456250884875681558l;

	public short element;

	public ShortReference() {}

	public ShortReference(short initialElement) {
		element = initialElement;
	}

	@Contract(value = "_ -> new", pure = true)
	public static ShortReference valueOf(short value) {
		return new ShortReference(value);
	}

	@Contract(value = "_ -> new", pure = true)
	public static ShortReference valueOf(@NotNull String value) {
		try {
			return new ShortReference(Short.parseShort(value));
		} catch (NumberFormatException e) {
			return new ShortReference();
		}
	}

	@Override
	public String toString() {
		return String.valueOf(element);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof ShortReference ref && ref.element == element;
	}

	@Override
	public int hashCode() {
		return element;
	}

	@Override
	public int compareTo(@NotNull ShortReference o) {
		return Short.compare(element, o.element);
	}

	@Override
	public byte byteValue() {
		return (byte) element;
	}

	@Override
	public short shortValue() {
		return element;
	}

	@Override
	public int intValue() {
		return element;
	}

	@Override
	public long longValue() {
		return element;
	}

	@Override
	public float floatValue() {
		return element;
	}

	@Override
	public double doubleValue() {
		return element;
	}
}
