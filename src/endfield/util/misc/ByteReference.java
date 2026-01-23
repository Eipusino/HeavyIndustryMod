package endfield.util.misc;

import arc.util.pooling.Pool.Poolable;

import java.io.Serializable;

/** @since 1.0.8 */
public class ByteReference extends Number implements Serializable, Poolable {
	private static final long serialVersionUID = -5505364929950384247l;

	public byte element;

	public ByteReference() {}

	public ByteReference(byte initialElement) {
		element = initialElement;
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
