package heavyindustry.util.ref;

import arc.util.pooling.Pool.Poolable;

import java.io.Serializable;

/** @since 1.0.8 */
public class IntReference extends Number implements Serializable, Poolable {
	private static final long serialVersionUID = -2015042737234032560l;

	public int element;

	public IntReference() {}

	public IntReference(int initialElement) {
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
