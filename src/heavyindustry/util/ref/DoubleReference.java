package heavyindustry.util.ref;

import arc.util.pooling.Pool.Poolable;

import java.io.Serializable;

/**
 * If atomic operations are required, please use {@link heavyindustry.util.concurrent.AtomicDouble}.
 *
 * @since 1.0.9
 */
public class DoubleReference extends Number implements Serializable, Poolable {
	private static final long serialVersionUID = 3872914250117543122l;

	public double element;

	public DoubleReference() {}

	public DoubleReference(double initialElement) {
		element = initialElement;
	}

	@Override
	public String toString() {
		return String.valueOf(element);
	}

	@Override
	public void reset() {
		element = 0d;
	}

	@Override
	public int intValue() {
		return (int) element;
	}

	@Override
	public long longValue() {
		return (long) element;
	}

	@Override
	public float floatValue() {
		return (float) element;
	}

	@Override
	public double doubleValue() {
		return element;
	}
}
