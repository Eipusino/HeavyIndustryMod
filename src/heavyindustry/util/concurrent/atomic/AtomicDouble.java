package heavyindustry.util.concurrent.atomic;

import heavyindustry.func.DoubleDoublef;
import heavyindustry.func.DoubleDoublef2;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.VarHandle;

/**
 * Since Java does not provide {@code AtomicDouble}, we implemented it ourselves using {@code VarHandle}.
 *
 * @since 1.0.9
 */
public class AtomicDouble extends Number implements Serializable {
	private static final long serialVersionUID = 7663420361921571242l;

	private static final VarHandle handle;

	static {
		try {
			Lookup lookup = MethodHandles.lookup();
			handle = lookup.findVarHandle(AtomicDouble.class, "value", double.class);
		} catch (ReflectiveOperationException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	private volatile double value;

	public AtomicDouble(double initialValue) {
		value = initialValue;
	}

	public AtomicDouble() {}

	public final double get() {
		return value;
	}

	public final void set(double newValue) {
		value = newValue;
	}

	public final void lazySet(double newValue) {
		handle.setRelease(this, newValue);
	}

	public final double getAndSet(double newValue) {
		return (double) handle.getAndSet(this, newValue);
	}

	public final boolean compareAndSet(double expectedValue, double newValue) {
		return handle.compareAndSet(this, expectedValue, newValue);
	}

	@Deprecated(since = "9")
	public final boolean weakCompareAndSet(double expectedValue, double newValue) {
		return handle.weakCompareAndSetPlain(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetPlain(double expectedValue, double newValue) {
		return handle.weakCompareAndSetPlain(this, expectedValue, newValue);
	}

	public final double getAndIncrement() {
		return (double) handle.getAndAdd(this, 1d);
	}

	public final double getAndDecrement() {
		return (double) handle.getAndAdd(this, -1d);
	}

	public final double getAndAdd(double delta) {
		return (double) handle.getAndAdd(this, delta);
	}

	public final double incrementAndGet() {
		return (double) handle.getAndAdd(this, 1d) + 1d;
	}

	public final double decrementAndGet() {
		return (double) handle.getAndAdd(this, -1d) - 1d;
	}

	public final double addAndGet(double delta) {
		return (double) handle.getAndAdd(this, delta) + delta;
	}

	public final double getAndUpdate(DoubleDoublef updateFunction) {
		double prev = get(), next = 0d;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = updateFunction.get(prev);
			if (weakCompareAndSetVolatile(prev, next))
				return prev;
			haveNext = (prev == (prev = get()));
		}
	}

	public final double updateAndGet(DoubleDoublef updateFunction) {
		double prev = get(), next = 0d;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = updateFunction.get(prev);
			if (weakCompareAndSetVolatile(prev, next))
				return next;
			haveNext = (prev == (prev = get()));
		}
	}

	public final double getAndAccumulate(double x, DoubleDoublef2 accumulatorFunction) {
		double prev = get(), next = 0d;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = accumulatorFunction.get(prev, x);
			if (weakCompareAndSetVolatile(prev, next))
				return prev;
			haveNext = (prev == (prev = get()));
		}
	}

	public final double accumulateAndGet(double x, DoubleDoublef2 accumulatorFunction) {
		double prev = get(), next = 0d;
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
		return Double.toString(get());
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
		return (float) get();
	}

	@Override
	public double doubleValue() {
		return get();
	}

	public final double getPlain() {
		return (double) handle.get(this);
	}

	public final void setPlain(double newValue) {
		handle.set(this, newValue);
	}

	public final double getOpaque() {
		return (double) handle.getOpaque(this);
	}

	public final void setOpaque(double newValue) {
		handle.setOpaque(this, newValue);
	}

	public final double getAcquire() {
		return (double) handle.getAcquire(this);
	}

	public final void setRelease(double newValue) {
		handle.setRelease(this, newValue);
	}

	public final double compareAndExchange(double expectedValue, double newValue) {
		return (double) handle.compareAndExchange(this, expectedValue, newValue);
	}

	public final double compareAndExchangeAcquire(double expectedValue, double newValue) {
		return (double) handle.compareAndExchangeAcquire(this, expectedValue, newValue);
	}

	public final double compareAndExchangeRelease(double expectedValue, double newValue) {
		return (double) handle.compareAndExchangeRelease(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetVolatile(double expectedValue, double newValue) {
		return handle.weakCompareAndSet(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetAcquire(double expectedValue, double newValue) {
		return handle.weakCompareAndSetAcquire(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetRelease(double expectedValue, double newValue) {
		return handle.weakCompareAndSetRelease(this, expectedValue, newValue);
	}
}
