package heavyindustry.util.concurrent.atomic;

import arc.func.FloatFloatf;
import heavyindustry.func.FloatFloatf2;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.VarHandle;

/**
 * Since Java does not provide {@code AtomicFloat}, we implemented it ourselves using {@code VarHandle}.
 *
 * @since 1.0.9
 */
public class AtomicFloat extends Number implements Serializable {
	private static final long serialVersionUID = -4167511778980629918l;

	private static final VarHandle handle;

	static {
		try {
			Lookup lookup = MethodHandles.lookup();
			handle = lookup.findVarHandle(AtomicFloat.class, "value", float.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private volatile float value;

	public AtomicFloat(float initialValue) {
		value = initialValue;
	}

	public AtomicFloat() {}

	public final float get() {
		return value;
	}

	public final void set(float newValue) {
		value = newValue;
	}

	public final void lazySet(float newValue) {
		handle.setRelease(this, newValue);
	}

	public final float getAndSet(float newValue) {
		return (float) handle.getAndSet(this, newValue);
	}

	public final boolean compareAndSet(float expectedValue, float newValue) {
		return handle.compareAndSet(this, expectedValue, newValue);
	}

	@Deprecated(since = "9")
	public final boolean weakCompareAndSet(float expectedValue, float newValue) {
		return handle.weakCompareAndSetPlain(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetPlain(float expectedValue, float newValue) {
		return handle.weakCompareAndSetPlain(this, expectedValue, newValue);
	}

	public final float getAndIncrement() {
		return (float) handle.getAndAdd(this, 1f);
	}

	public final float getAndDecrement() {
		return (float) handle.getAndAdd(this, -1f);
	}

	public final float getAndAdd(float delta) {
		return (float) handle.getAndAdd(this, delta);
	}

	public final float incrementAndGet() {
		return (float) handle.getAndAdd(this, 1f) + 1f;
	}

	public final float decrementAndGet() {
		return (float) handle.getAndAdd(this, -1f) - 1f;
	}

	public final float addAndGet(float delta) {
		return (float) handle.getAndAdd(this, delta) + delta;
	}

	public final float getAndUpdate(FloatFloatf updateFunction) {
		float prev = get(), next = 0f;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = updateFunction.get(prev);
			if (weakCompareAndSetVolatile(prev, next))
				return prev;
			haveNext = (prev == (prev = get()));
		}
	}

	public final float updateAndGet(FloatFloatf updateFunction) {
		float prev = get(), next = 0f;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = updateFunction.get(prev);
			if (weakCompareAndSetVolatile(prev, next))
				return next;
			haveNext = (prev == (prev = get()));
		}
	}

	public final float getAndAccumulate(float x, FloatFloatf2 accumulatorFunction) {
		float prev = get(), next = 0f;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = accumulatorFunction.get(prev, x);
			if (weakCompareAndSetVolatile(prev, next))
				return prev;
			haveNext = (prev == (prev = get()));
		}
	}

	public final float accumulateAndGet(float x, FloatFloatf2 accumulatorFunction) {
		float prev = get(), next = 0f;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = accumulatorFunction.get(prev, x);
			if (weakCompareAndSetVolatile(prev, next))
				return next;
			haveNext = (prev == (prev = get()));
		}
	}

	@Override
	public String toString() {
		return Float.toString(get());
	}

	@Override
	public int intValue() {
		return (int) get();
	}

	@Override
	public long longValue() {
		return (long) get();
	}

	@Override
	public float floatValue() {
		return get();
	}

	@Override
	public double doubleValue() {
		return get();
	}

	public final float getPlain() {
		return (float) handle.get(this);
	}

	public final void setPlain(float newValue) {
		handle.set(this, newValue);
	}

	public final float getOpaque() {
		return (float) handle.getOpaque(this);
	}

	public final void setOpaque(float newValue) {
		handle.setOpaque(this, newValue);
	}

	public final float getAcquire() {
		return (float) handle.getAcquire(this);
	}

	public final void setRelease(float newValue) {
		handle.setRelease(this, newValue);
	}

	public final float compareAndExchange(float expectedValue, float newValue) {
		return (float) handle.compareAndExchange(this, expectedValue, newValue);
	}

	public final float compareAndExchangeAcquire(float expectedValue, float newValue) {
		return (float) handle.compareAndExchangeAcquire(this, expectedValue, newValue);
	}

	public final float compareAndExchangeRelease(float expectedValue, float newValue) {
		return (float) handle.compareAndExchangeRelease(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetVolatile(float expectedValue, float newValue) {
		return handle.weakCompareAndSet(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetAcquire(float expectedValue, float newValue) {
		return handle.weakCompareAndSetAcquire(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetRelease(float expectedValue, float newValue) {
		return handle.weakCompareAndSetRelease(this, expectedValue, newValue);
	}
}
