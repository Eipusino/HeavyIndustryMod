package heavyindustry.util.concurrent;

import heavyindustry.util.Objects2;

import java.io.Serializable;

public class DoubleReference implements Serializable {
	private static final long serialVersionUID = 3872914250117543122l;

	public double element;

	public DoubleReference() {}

	public DoubleReference(double initialElement) {
		element = initialElement;
	}

	@Override
	public String toString() {
		return String.valueOf(element);
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this || obj instanceof DoubleReference ref && ref.element == element;
	}

	@Override
	public int hashCode() {
		return Objects2.hashCodeLong(Double.doubleToLongBits(element));
	}
}
