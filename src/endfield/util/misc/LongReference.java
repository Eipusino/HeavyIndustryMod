package endfield.util.misc;

import arc.util.pooling.Pool.Poolable;

import java.io.Serializable;
import java.util.stream.LongStream;

/** @since 1.0.8 */
public class LongReference extends Number implements Serializable, Poolable {
	private static final long serialVersionUID = 6421798427509969426l;

	public long element;

	public LongReference() {}

	public LongReference(long initialElement) {
		element = initialElement;
	}

	public LongStream stream() {
		return LongStream.of(element);
	}

	@Override
	public String toString() {
		return String.valueOf(element);
	}

	@Override
	public void reset() {
		element = 0l;
	}

	@Override
	public int intValue() {
		return (int) element;
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
