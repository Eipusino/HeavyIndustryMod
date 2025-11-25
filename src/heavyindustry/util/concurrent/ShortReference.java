package heavyindustry.util.concurrent;

import java.io.Serializable;

public class ShortReference implements Serializable {
	private static final long serialVersionUID = -6456250884875681558l;

	public short element;

	@Override
	public String toString() {
		return String.valueOf(element);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof ShortReference ref && ref.element == element;
	}

	@Override
	public int hashCode() {
		return element;
	}
}
