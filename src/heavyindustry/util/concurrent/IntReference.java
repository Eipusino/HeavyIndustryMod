package heavyindustry.util.concurrent;

import arc.util.Strings;
import arc.util.pooling.Pool.Poolable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class IntReference extends Number implements Serializable, Comparable<IntReference>, Poolable {
	private static final long serialVersionUID = -2015042737234032560l;

	public int element;

	public IntReference() {}

	public IntReference(int initialElement) {
		element = initialElement;
	}

	@Contract(value = "_ -> new", pure = true)
	public static IntReference valueOf(int value) {
		return new IntReference(value);
	}

	@Contract(value = "_ -> new", pure = true)
	public static IntReference valueOf(String value) {
		return new IntReference(Strings.parseInt(value, 0));
	}

	@Override
	public String toString() {
		return String.valueOf(element);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof IntReference ref && ref.element == element;
	}

	@Override
	public int hashCode() {
		return element;
	}

	@Override
	public int compareTo(@NotNull IntReference o) {
		return Integer.compare(element, o.element);
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
