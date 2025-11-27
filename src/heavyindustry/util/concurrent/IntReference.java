package heavyindustry.util.concurrent;

import java.io.Serializable;

public class IntReference implements Serializable {
	private static final long serialVersionUID = -2015042737234032560l;

	public int element;

	public IntReference() {}

	public IntReference(int initialElement) {
		element = initialElement;
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
}
