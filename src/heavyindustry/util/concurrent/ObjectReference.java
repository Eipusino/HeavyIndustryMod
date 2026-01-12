package heavyindustry.util.concurrent;

import arc.util.pooling.Pool.Poolable;

import java.io.Serializable;
import java.util.Objects;

public class ObjectReference<T> implements Serializable, Poolable {
	private static final long serialVersionUID = -9054478421223311650l;

	public T element;

	public ObjectReference() {}

	public ObjectReference(T initialElement) {
		element = initialElement;
	}

	@Override
	public String toString() {
		return String.valueOf(element);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof ObjectReference<?> ref && Objects.equals(ref.element, element);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(element);
	}

	@Override
	public void reset() {
		element = null;
	}
}
