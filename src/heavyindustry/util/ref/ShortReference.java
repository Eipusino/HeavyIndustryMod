package heavyindustry.util.ref;

import arc.util.pooling.Pool.Poolable;

import java.io.Serializable;

public class ShortReference extends Number implements Serializable, Poolable {
	private static final long serialVersionUID = -6456250884875681558l;

	public short element;

	public ShortReference() {}

	public ShortReference(short initialElement) {
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
