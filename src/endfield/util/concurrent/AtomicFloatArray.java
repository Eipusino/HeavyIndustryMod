package endfield.util.concurrent;

import arc.func.FloatFloatf;
import endfield.func.FloatFloatf2;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class AtomicFloatArray implements Serializable {
	private static final long serialVersionUID = 2434937495610854296l;

	private static final VarHandle handle = MethodHandles.arrayElementVarHandle(float[].class);

	private final float[] array;

	public AtomicFloatArray(int length) {
		array = new float[length];
	}

	public AtomicFloatArray(float[] arr) {
		array = arr.clone();
	}

	public int length() {
		return array.length;
	}

	public final float get(int i) {
		return (float) handle.getVolatile(array, i);
	}

	public final void set(int i, float newValue) {
		handle.setVolatile(array, i, newValue);
	}

	public final void lazySet(int i, float newValue) {
		handle.setRelease(array, i, newValue);
	}

	public final float getAndSet(int i, float newValue) {
		return (float) handle.getAndSet(array, i, newValue);
	}

	public final boolean compareAndSet(int i, float expectedValue, float newValue) {
		return handle.compareAndSet(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetPlain(int i, float expectedValue, float newValue) {
		return handle.weakCompareAndSetPlain(array, i, expectedValue, newValue);
	}

	public final float getAndIncrement(int i) {
		return (float) handle.getAndAdd(array, i, 1f);
	}

	public final float getAndDecrement(int i) {
		return (float) handle.getAndAdd(array, i, -1f);
	}

	public final float getAndAdd(int i, float delta) {
		return (float) handle.getAndAdd(array, i, delta);
	}

	public final float incrementAndGet(int i) {
		return (float) handle.getAndAdd(array, i, 1f) + 1f;
	}

	public final float decrementAndGet(int i) {
		return (float) handle.getAndAdd(array, i, -1f) - 1f;
	}

	public final float addAndGet(int i, float delta) {
		return (float) handle.getAndAdd(array, i, delta) + delta;
	}

	public final float getAndUpdate(int i, FloatFloatf updateFunction) {
		float prev = get(i), next = 0f;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = updateFunction.get(prev);
			if (weakCompareAndSetVolatile(i, prev, next))
				return prev;
			haveNext = (prev == (prev = get(i)));
		}
	}

	public final float updateAndGet(int i, FloatFloatf updateFunction) {
		float prev = get(i), next = 0f;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = updateFunction.get(prev);
			if (weakCompareAndSetVolatile(i, prev, next))
				return next;
			haveNext = (prev == (prev = get(i)));
		}
	}

	public final float getAndAccumulate(int i, float x, FloatFloatf2 accumulatorFunction) {
		float prev = get(i), next = 0f;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = accumulatorFunction.get(prev, x);
			if (weakCompareAndSetVolatile(i, prev, next))
				return prev;
			haveNext = (prev == (prev = get(i)));
		}
	}

	public final float accumulateAndGet(int i, float x, FloatFloatf2 accumulatorFunction) {
		float prev = get(i), next = 0f;
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

	public final float getPlain(int i) {
		return (float) handle.get(array, i);
	}

	public final void setPlain(int i, float newValue) {
		handle.set(array, i, newValue);
	}

	public final float getOpaque(int i) {
		return (float) handle.getOpaque(array, i);
	}

	public final void setOpaque(int i, float newValue) {
		handle.setOpaque(array, i, newValue);
	}

	public final float getAcquire(int i) {
		return (float) handle.getAcquire(array, i);
	}

	public final void setRelease(int i, float newValue) {
		handle.setRelease(array, i, newValue);
	}

	public final float compareAndExchange(int i, float expectedValue, float newValue) {
		return (float) handle.compareAndExchange(array, i, expectedValue, newValue);
	}

	public final float compareAndExchangeAcquire(int i, float expectedValue, float newValue) {
		return (float) handle.compareAndExchangeAcquire(array, i, expectedValue, newValue);
	}

	public final float compareAndExchangeRelease(int i, float expectedValue, float newValue) {
		return (float) handle.compareAndExchangeRelease(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetVolatile(int i, float expectedValue, float newValue) {
		return handle.weakCompareAndSet(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetAcquire(int i, float expectedValue, float newValue) {
		return handle.weakCompareAndSetAcquire(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetRelease(int i, float expectedValue, float newValue) {
		return handle.weakCompareAndSetRelease(array, i, expectedValue, newValue);
	}
}
