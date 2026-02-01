package endfield.util.concurrent;

import endfield.func.DoubleDoublef;
import endfield.func.DoubleDoublef2;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class AtomicDoubleArray implements Serializable {
	private static final long serialVersionUID = 4307791984143544057l;

	private static final VarHandle handle = MethodHandles.arrayElementVarHandle(double[].class);

	private final double[] array;

	public AtomicDoubleArray(int length) {
		array = new double[length];
	}

	public AtomicDoubleArray(double[] arr) {
		array = arr.clone();
	}

	public int length() {
		return array.length;
	}

	public final double get(int i) {
		return (double) handle.getVolatile(array, i);
	}

	public final void set(int i, double newValue) {
		handle.setVolatile(array, i, newValue);
	}

	public final void lazySet(int i, double newValue) {
		handle.setRelease(array, i, newValue);
	}

	public final double getAndSet(int i, double newValue) {
		return (double) handle.getAndSet(array, i, newValue);
	}

	public final boolean compareAndSet(int i, double expectedValue, double newValue) {
		return handle.compareAndSet(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetPlain(int i, double expectedValue, double newValue) {
		return handle.weakCompareAndSetPlain(array, i, expectedValue, newValue);
	}

	public final double getAndIncrement(int i) {
		return (double) handle.getAndAdd(array, i, 1d);
	}

	public final double getAndDecrement(int i) {
		return (double) handle.getAndAdd(array, i, -1d);
	}

	public final double getAndAdd(int i, double delta) {
		return (double) handle.getAndAdd(array, i, delta);
	}

	public final double incrementAndGet(int i) {
		return (double) handle.getAndAdd(array, i, 1d) + 1d;
	}

	public final double decrementAndGet(int i) {
		return (double) handle.getAndAdd(array, i, -1d) - 1d;
	}

	public final double addAndGet(int i, double delta) {
		return (double) handle.getAndAdd(array, i, delta) + delta;
	}

	public final double getAndUpdate(int i, DoubleDoublef updateFunction) {
		double prev = get(i), next = 0f;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = updateFunction.get(prev);
			if (weakCompareAndSetVolatile(i, prev, next))
				return prev;
			haveNext = (prev == (prev = get(i)));
		}
	}

	public final double updateAndGet(int i, DoubleDoublef updateFunction) {
		double prev = get(i), next = 0f;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = updateFunction.get(prev);
			if (weakCompareAndSetVolatile(i, prev, next))
				return next;
			haveNext = (prev == (prev = get(i)));
		}
	}

	public final double getAndAccumulate(int i, double x, DoubleDoublef2 accumulatorFunction) {
		double prev = get(i), next = 0f;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = accumulatorFunction.get(prev, x);
			if (weakCompareAndSetVolatile(i, prev, next))
				return prev;
			haveNext = (prev == (prev = get(i)));
		}
	}

	public final double accumulateAndGet(int i, double x, DoubleDoublef2 accumulatorFunction) {
		double prev = get(i), next = 0f;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = accumulatorFunction.get(prev, x);
			if (weakCompareAndSetVolatile(i, prev, next))
				return next;
			haveNext = (prev == (prev = get(i)));
		}
	}

	@Override
	public String toString() {
		int iMax = array.length - 1;
		if (iMax == -1)
			return "[]";

		StringBuilder b = new StringBuilder();
		b.append('[');
		for (int i = 0; ; i++) {
			b.append(get(i));
			if (i == iMax)
				return b.append(']').toString();
			b.append(',').append(' ');
		}
	}

	public final double getPlain(int i) {
		return (double) handle.get(array, i);
	}

	public final void setPlain(int i, double newValue) {
		handle.set(array, i, newValue);
	}

	public final double getOpaque(int i) {
		return (double) handle.getOpaque(array, i);
	}

	public final void setOpaque(int i, double newValue) {
		handle.setOpaque(array, i, newValue);
	}

	public final double getAcquire(int i) {
		return (double) handle.getAcquire(array, i);
	}

	public final void setRelease(int i, double newValue) {
		handle.setRelease(array, i, newValue);
	}

	public final double compareAndExchange(int i, double expectedValue, double newValue) {
		return (double) handle.compareAndExchange(array, i, expectedValue, newValue);
	}

	public final double compareAndExchangeAcquire(int i, double expectedValue, double newValue) {
		return (double) handle.compareAndExchangeAcquire(array, i, expectedValue, newValue);
	}

	public final double compareAndExchangeRelease(int i, double expectedValue, double newValue) {
		return (double) handle.compareAndExchangeRelease(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetVolatile(int i, double expectedValue, double newValue) {
		return handle.weakCompareAndSet(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetAcquire(int i, double expectedValue, double newValue) {
		return handle.weakCompareAndSetAcquire(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetRelease(int i, double expectedValue, double newValue) {
		return handle.weakCompareAndSetRelease(array, i, expectedValue, newValue);
	}
}
