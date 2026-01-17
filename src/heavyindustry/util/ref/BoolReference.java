package heavyindustry.util.ref;

import arc.util.pooling.Pool.Poolable;

import java.io.Serializable;

public class BoolReference implements Serializable, Poolable {
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
	public void reset() {
		element = false;
	}
}
