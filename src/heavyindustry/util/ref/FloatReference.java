package heavyindustry.util.ref;

import arc.util.Strings;
import arc.util.pooling.Pool.Poolable;
import org.jetbrains.annotations.Contract;

import java.io.Serializable;

public class FloatReference extends Number implements Serializable, Poolable {
	private static final long serialVersionUID = 2272494129790516325l;

	public float element;

	public FloatReference() {}

	public FloatReference(float initialElement) {
		element = initialElement;
	}

	@Contract(value = "_ -> new", pure = true)
	public static FloatReference valueOf(float value) {
		return new FloatReference(value);
	}

	@Contract(value = "_ -> new", pure = true)
	public static FloatReference valueOf(String value) {
		return new FloatReference(Strings.parseFloat(value, 0f));
	}

	@Override
	public String toString() {
		return String.valueOf(element);
	}

	@Override
	public void reset() {
		element = 0f;
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
		return element;
	}

	@Override
	public double doubleValue() {
		return element;
	}
}
