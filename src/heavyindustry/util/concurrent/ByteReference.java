package heavyindustry.util.concurrent;

import java.io.Serializable;

public class ByteReference implements Serializable {
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
	public boolean equals(Object obj) {
		return obj == this || obj instanceof ByteReference ref && ref.element == element;
	}

	@Override
	public int hashCode() {
		return element;
	}
}
