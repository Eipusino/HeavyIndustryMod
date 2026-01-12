package heavyindustry.util.concurrent;

import arc.util.Strings;
import arc.util.pooling.Pool.Poolable;
import heavyindustry.util.Objects2;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class LongReference extends Number implements Serializable, Comparable<LongReference>, Poolable {
	private static final long serialVersionUID = 6421798427509969426l;

	public long element;

	public LongReference() {}

	public LongReference(long initialElement) {
		element = initialElement;
	}

	@Contract(value = "_ -> new", pure = true)
	public static LongReference valueOf(long value) {
		return new LongReference(value);
	}

	@Contract(value = "_ -> new", pure = true)
	public static LongReference valueOf(String value) {
		return new LongReference(Strings.parseLong(value, 0l));
	}

	@Override
	public String toString() {
		return String.valueOf(element);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof LongReference ref && ref.element == element;
	}

	@Override
	public int hashCode() {
		return Objects2.longToHash(element);
	}

	@Override
	public int compareTo(@NotNull LongReference o) {
		return Long.compare(element, o.element);
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
