package heavyindustry.util.ref;

import arc.util.pooling.Pool.Poolable;
import org.jetbrains.annotations.Contract;

import java.io.Serializable;

public class CharReference implements Serializable, Poolable {
	private static final long serialVersionUID = -6890675570138946042l;

	public char element;

	public CharReference() {}

	public CharReference(char initialElement) {
		element = initialElement;
	}

	@Contract(value = "_ -> new", pure = true)
	public static CharReference valueOf(char value) {
		return new CharReference(value);
	}

	@Override
	public String toString() {
		return String.valueOf(element);
	}

	@Override
	public void reset() {
		element = 0;
	}
}
