package heavyindustry.util.misc;

import arc.func.Prov;
import arc.util.pooling.Pool.Poolable;
import heavyindustry.func.Doublec;
import heavyindustry.func.Doublep;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.stream.DoubleStream;

/**
 * If atomic operations are required, please use {@link heavyindustry.util.concurrent.AtomicDouble}.
 *
 * @since 1.0.9
 */
public class DoubleReference extends Number implements Serializable, Poolable {
	private static final long serialVersionUID = 3872914250117543122l;

	public double element;

	public DoubleReference() {}

	public DoubleReference(double initialElement) {
		element = initialElement;
	}

	public double get() {
		if (isPresent()) return element;

		throw new NoSuchElementException("No value present");
	}

	public boolean isPresent() {
		return !Double.isNaN(element);
	}

	public boolean isEmpty() {
		return Double.isNaN(element);
	}

	public void ifPresent(Doublec action) {
		if (isPresent()) {
			action.get(element);
		}
	}

	public void ifPresentOrElse(Doublec action, Runnable emptyAction) {
		if (isPresent()) {
			action.get(element);
		} else {
			emptyAction.run();
		}
	}

	public DoubleStream stream() {
		if (isPresent()) {
			return DoubleStream.of(element);
		} else {
			return DoubleStream.empty();
		}
	}

	public double orElse(double other) {
		return isPresent() ? element : other;
	}

	public double orElseGet(Doublep supplier) {
		return isPresent() ? element : supplier.get();
	}

	public double orElseThrow() {
		if (!isPresent()) {
			throw new NoSuchElementException("No value present");
		}
		return element;
	}

	public<X extends Throwable> double orElseThrow(Prov<? extends X> exceptionSupplier) throws X {
		if (isPresent()) {
			return element;
		} else {
			throw exceptionSupplier.get();
		}
	}

	@Override
	public String toString() {
		return String.valueOf(element);
	}

	@Override
	public void reset() {
		element = 0d;
	}

	@Override
	public int intValue() {
		return (int) element;
	}

	@Override
	public long longValue() {
		return (long) element;
	}

	@Override
	public float floatValue() {
		return (float) element;
	}

	@Override
	public double doubleValue() {
		return element;
	}
}
