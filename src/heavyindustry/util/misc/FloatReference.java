package heavyindustry.util.misc;

import arc.func.Floatc;
import arc.func.Floatp;
import arc.func.Prov;
import arc.util.pooling.Pool.Poolable;

import java.io.Serializable;
import java.util.NoSuchElementException;

/**
 * If atomic operations are required, please use {@link heavyindustry.util.concurrent.AtomicFloat}.
 *
 * @since 1.0.8
 */
public class FloatReference extends Number implements Serializable, Poolable {
	private static final long serialVersionUID = 2272494129790516325l;

	public float element;

	public FloatReference() {}

	public FloatReference(float initialElement) {
		element = initialElement;
	}

	public float get() {
		if (isEmpty()) {
			throw new NoSuchElementException("No value present");
		}
		return element;
	}

	public boolean isPresent() {
		return !Float.isNaN(element);
	}

	public boolean isEmpty() {
		return Float.isNaN(element);
	}

	public void ifPresent(Floatc action) {
		if (isPresent()) {
			action.get(element);
		}
	}

	public void ifPresentOrElse(Floatc action, Runnable emptyAction) {
		if (isPresent()) {
			action.get(element);
		} else {
			emptyAction.run();
		}
	}

	public float orElse(float other) {
		return isPresent() ? element : other;
	}

	public float orElseGet(Floatp supplier) {
		return isPresent() ? element : supplier.get();
	}

	public float orElseThrow() {
		if (isEmpty())
			throw new NoSuchElementException("No value present");

		return element;
	}

	public<X extends Throwable> float orElseThrow(Prov<? extends X> exceptionSupplier) throws X {
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
		element = 0f;
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
		return element;
	}

	@Override
	public double doubleValue() {
		return element;
	}
}
