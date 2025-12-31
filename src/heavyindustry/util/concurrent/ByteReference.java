package heavyindustry.util.concurrent;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class ByteReference extends Number implements Serializable, Comparable<ByteReference> {
	private static final long serialVersionUID = -5505364929950384247l;

	public byte element;

	public ByteReference() {}

	public ByteReference(byte initialElement) {
		element = initialElement;
	}

	@Contract(value = "_ -> new", pure = true)
	public static ByteReference valueOf(byte value) {
		return new ByteReference(value);
	}

	@Contract(value = "_ -> new", pure = true)
	public static ByteReference valueOf(@NotNull String value) {
		try {
			return new ByteReference(Byte.parseByte(value));
		} catch (NumberFormatException e) {
			return new ByteReference();
		}
	}

	@Override
	public String toString() {
		return String.valueOf(element);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof ByteReference ref && ref.element == element;
	}

	@Override
	public int compareTo(@NotNull ByteReference o) {
		return Byte.compare(element, o.element);
	}

	@Override
	public int hashCode() {
		return element;
	}

	@Override
	public byte byteValue() {
		return element;
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
