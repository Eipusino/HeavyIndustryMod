package heavyindustry.util.ref;

import arc.util.pooling.Pool.Poolable;
import org.jetbrains.annotations.Contract;

import java.io.Serializable;

public class BoolReference implements Serializable, Poolable {
	private static final long serialVersionUID = -7120385114040352042l;

	public boolean element;

	public BoolReference() {}

	public BoolReference(boolean initialElement) {
		element = initialElement;
	}

	@Contract(value = "_ -> new", pure = true)
	public static BoolReference valueOf(boolean value) {
		return new BoolReference(value);
	}

	@Contract(value = "_ -> new", pure = true)
	public static BoolReference valueOf(String value) {
		return new BoolReference(Boolean.parseBoolean(value));
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
