package heavyindustry.util.concurrent;

import arc.util.pooling.Pool.Poolable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;

public class CharReference implements Serializable, Comparable<CharReference>, Poolable {
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
	public boolean equals(Object obj) {
		return obj == this || obj instanceof CharReference ref && ref.element == element;
	}

	@Override
	public int hashCode() {
		return element;
	}

	@Override
	public int compareTo(@NotNull CharReference o) {
		return Character.compare(element, o.element);
	}

	@Override
	public void reset() {
		element = 0;
	}
}
