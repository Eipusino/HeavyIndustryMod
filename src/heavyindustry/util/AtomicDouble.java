package heavyindustry.util;

import java.util.concurrent.atomic.AtomicLong;

public class AtomicDouble extends Number {
	private static final long serialVersionUID = 4461617770826287723l;

	protected final AtomicLong bits;

	public AtomicDouble() {
		this(0f);
	}

	public AtomicDouble(double initialValue) {
		bits = new AtomicLong(Double.doubleToLongBits(initialValue));
	}

	public final double addAndGet(float delta) {
		double expect, update;
		do {
			expect = get();
			update = expect + delta;
		} while (!compareAndSet(expect, update));

		return update;
	}

	public final double getAndAdd(float delta) {
		double expect, update;
		do {
			expect = get();
			update = expect + delta;
		} while (!compareAndSet(expect, update));

		return expect;
	}

	public final double getAndDecrement() {
		return getAndAdd(-1);
	}

	public final double decrementAndGet() {
		return addAndGet(-1);
	}

	public final double getAndIncrement() {
		return getAndAdd(1);
	}

	public final double incrementAndGet() {
		return addAndGet(1);
	}

	public final double getAndSet(double newValue) {
		double expect;
		do {
			expect = get();
		} while (!compareAndSet(expect, newValue));

		return expect;
	}

	public final boolean compareAndSet(double expect, double update) {
		return bits.compareAndSet(Double.doubleToLongBits(expect), Double.doubleToLongBits(update));
	}

	public final void set(double newValue) {
		bits.set(Double.doubleToLongBits(newValue));
	}

	public final double get() {
		return Double.longBitsToDouble(bits.get());
	}

	@Override
	public float floatValue() {
		return (float) get();
	}

	@Override
	public double doubleValue() {
		return get();
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
	public byte byteValue() {
		return (byte) get();
	}

	@Override
	public short shortValue() {
		return (short) get();
	}

	@Override
	public String toString() {
		return Double.toString(get());
	}
}
