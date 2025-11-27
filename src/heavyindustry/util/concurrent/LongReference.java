package heavyindustry.util.concurrent;

import heavyindustry.util.Objects2;

import java.io.Serializable;

public class LongReference implements Serializable {
	private static final long serialVersionUID = 6421798427509969426l;

	public long element;

	public LongReference() {}

	public LongReference(long initialElement) {
		element = initialElement;
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
		return Objects2.hashCodeLong(element);
	}
}
