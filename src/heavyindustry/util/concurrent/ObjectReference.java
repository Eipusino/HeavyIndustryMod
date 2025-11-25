package heavyindustry.util.concurrent;

import heavyindustry.util.Objects2;

import java.io.Serializable;

public class ObjectReference<T> implements Serializable {
	private static final long serialVersionUID = -9054478421223311650l;

	public T element;

	@Override
	public String toString() {
		return String.valueOf(element);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof ObjectReference<?> ref && Objects2.equals(ref.element, element);
	}

	@Override
	public int hashCode() {
		return Objects2.hashCode(element);
	}
}
