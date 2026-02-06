package endfield.util.concurrent;

import endfield.func.BoolBoolf;
import endfield.func.BoolBoolf2;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class AtomicBooleanArray implements Serializable {
	private static final long serialVersionUID = 9216641675318893956l;

	private static final VarHandle handle = MethodHandles.arrayElementVarHandle(boolean[].class);

	private final boolean[] array;

	public AtomicBooleanArray(int length) {
		array = new boolean[length];
	}

	public AtomicBooleanArray(boolean[] arr) {
		array = arr;
	}

	public final int length() {
		return array.length;
	}

	public final boolean get(int i) {
		return (boolean) handle.getVolatile(array, i);
	}

	public final void set(int i, boolean newValue) {
		handle.setVolatile(array, i, newValue);
	}

	public final void lazySet(int i, boolean newValue) {
		handle.setRelease(array, i, newValue);
	}

	public final boolean compareAndSet(int i, boolean expectedValue, boolean newValue) {
		return handle.compareAndSet(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetPlain(int i, boolean expectedValue, boolean newValue) {
		return handle.weakCompareAndSetPlain(array, i, expectedValue, newValue);
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

	public final boolean getAndUpdate(int i, BoolBoolf updateFunction) {
		boolean prev = get(i), next = false;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = updateFunction.get(prev);
			if (weakCompareAndSetVolatile(i, prev, next))
				return prev;
			haveNext = (prev == (prev = get(i)));
		}
	}

	public final boolean updateAndGet(int i, BoolBoolf updateFunction) {
		boolean prev = get(i), next = false;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = updateFunction.get(prev);
			if (weakCompareAndSetVolatile(i, prev, next))
				return next;
			haveNext = (prev == (prev = get(i)));
		}
	}

	public final boolean getAndAccumulate(int i, boolean x, BoolBoolf2 accumulatorFunction) {
		boolean prev = get(i), next = false;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = accumulatorFunction.get(prev, x);
			if (weakCompareAndSetVolatile(i, prev, next))
				return prev;
			haveNext = (prev == (prev = get(i)));
		}
	}

	public final boolean accumulateAndGet(int i, boolean x, BoolBoolf2 accumulatorFunction) {
		boolean prev = get(i), next = false;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = accumulatorFunction.get(prev, x);
			if (weakCompareAndSetVolatile(i, prev, next))
				return next;
			haveNext = (prev == (prev = get(i)));
		}
	}

	public final boolean getPlain(int i) {
		return (boolean) handle.get(array, i);
	}

	public final void setPlain(int i, boolean newValue) {
		handle.set(array, i, newValue);
	}

	public final boolean getOpaque(int i) {
		return (boolean) handle.getOpaque(array, i);
	}

	public final void setOpaque(int i, boolean newValue) {
		handle.setOpaque(array, i, newValue);
	}

	public final boolean getAcquire(int i) {
		return (boolean) handle.getAcquire(array, i);
	}

	public final void setRelease(int i, boolean newValue) {
		handle.setRelease(array, i, newValue);
	}

	public final boolean compareAndExchange(int i, boolean expectedValue, boolean newValue) {
		return (boolean) handle.compareAndExchange(array, i, expectedValue, newValue);
	}

	public final boolean compareAndExchangeAcquire(int i, boolean expectedValue, boolean newValue) {
		return (boolean) handle.compareAndExchangeAcquire(array, i, expectedValue, newValue);
	}

	public final boolean compareAndExchangeRelease(int i, boolean expectedValue, boolean newValue) {
		return (boolean) handle.compareAndExchangeRelease(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetVolatile(int i, boolean expectedValue, boolean newValue) {
		return handle.weakCompareAndSet(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetAcquire(int i, boolean expectedValue, boolean newValue) {
		return handle.weakCompareAndSetAcquire(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetRelease(int i, boolean expectedValue, boolean newValue) {
		return handle.weakCompareAndSetRelease(array, i, expectedValue, newValue);
	}
}
