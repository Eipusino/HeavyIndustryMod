package heavyindustry.util.concurrent;

import java.io.Serializable;

public class CharReference implements Serializable {
	private static final long serialVersionUID = -6890675570138946042l;

	public char element;

	public CharReference() {}

	public CharReference(char initialElement) {
		element = initialElement;
	}

	@Override
	public String toString() {
		return String.valueOf(element);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof CharReference ref && ref.element == element;
	}

	@Override
	public int hashCode() {
		return element;
	}
}
