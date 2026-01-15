package heavyindustry.util.ref;

import arc.util.pooling.Pool.Poolable;
import org.jetbrains.annotations.Contract;

import java.io.Serializable;

public class ByteReference extends Number implements Serializable, Poolable {
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
	public static ByteReference valueOf(String value) {
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
	public void reset() {
		element = 0;
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
