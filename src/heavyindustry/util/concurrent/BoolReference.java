package heavyindustry.util.concurrent;

import heavyindustry.util.Objects2;

import java.io.Serializable;

public class BoolReference implements Serializable {
	private static final long serialVersionUID = -7120385114040352042l;

	public boolean element;

	public BoolReference() {}

	public BoolReference(boolean initialElement) {
		element = initialElement;
	}

	@Override
	public String toString() {
		return String.valueOf(element);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof BoolReference ref && ref.element == element;
	}

	@Override
	public int hashCode() {
		return Objects2.hashCodeBool(element);
	}
}
