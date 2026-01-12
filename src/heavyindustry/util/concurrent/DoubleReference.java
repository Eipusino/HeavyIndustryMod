package heavyindustry.util.concurrent;

import arc.util.Strings;
import arc.util.pooling.Pool.Poolable;
import heavyindustry.util.Objects2;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class DoubleReference extends Number implements Serializable, Comparable<DoubleReference>, Poolable {
	private static final long serialVersionUID = 3872914250117543122l;

	public double element;

	public DoubleReference() {}

	public DoubleReference(double initialElement) {
		element = initialElement;
	}

	@Contract(value = "_ -> new", pure = true)
	public static DoubleReference valueOf(double value) {
		return new DoubleReference(value);
	}

	@Contract(value = "_ -> new", pure = true)
	public static DoubleReference valueOf(String value) {
		return new DoubleReference(Strings.parseDouble(value, 0d));
	}

	@Override
	public String toString() {
		return String.valueOf(element);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof DoubleReference ref && ref.element == element;
	}

	@Override
	public int hashCode() {
		return Objects2.longToHash(Double.doubleToLongBits(element));
	}

	@Override
	public int compareTo(@NotNull DoubleReference o) {
		return Double.compare(element, o.element);
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
