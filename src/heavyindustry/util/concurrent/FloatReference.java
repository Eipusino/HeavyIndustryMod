package heavyindustry.util.concurrent;

import java.io.Serializable;

public class FloatReference implements Serializable {
	private static final long serialVersionUID = 2272494129790516325l;

	public float element;

	public FloatReference() {}

	public FloatReference(float initialElement) {
		element = initialElement;
	}

	@Override
	public String toString() {
		return String.valueOf(element);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof FloatReference ref && ref.element == element;
	}

	@Override
	public int hashCode() {
		return Float.floatToIntBits(element);
	}
}
