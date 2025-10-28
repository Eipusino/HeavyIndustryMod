package heavyindustry.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Since Java does not provide AtomicFloat, I implemented one myself.
 * <p>Android API level 14 cannot use VarHandle, so Bits conversion is used.
 */
public class AtomicFloat extends Number {
	private static final long serialVersionUID = 4906256511037633568l;

	protected final AtomicInteger bits;

	public AtomicFloat() {
		this(0f);
	}

	public AtomicFloat(float initialValue) {
		bits = new AtomicInteger(Float.floatToIntBits(initialValue));
	}

	public final float addAndGet(float delta) {
		float expect, update;
		do {
			expect = get();
			update = expect + delta;
		} while (!compareAndSet(expect, update));

		return update;
	}

	public final float getAndAdd(float delta) {
		float expect, update;
		do {
			expect = get();
			update = expect + delta;
		} while (!compareAndSet(expect, update));

		return expect;
	}

	public final float getAndDecrement() {
		return getAndAdd(-1);
	}

	public final float decrementAndGet() {
		return addAndGet(-1);
	}

	public final float getAndIncrement() {
		return getAndAdd(1);
	}

	public final float incrementAndGet() {
		return addAndGet(1);
	}

	public final float getAndSet(float newValue) {
		float expect;
		do {
			expect = get();
		} while (!compareAndSet(expect, newValue));

		return expect;
	}

	public final boolean compareAndSet(float expect, float update) {
		return bits.compareAndSet(Float.floatToIntBits(expect), Float.floatToIntBits(update));
	}

	public final void set(float newValue) {
		bits.set(Float.floatToIntBits(newValue));
	}

	public final float get() {
		return Float.intBitsToFloat(bits.get());
	}

	@Override
	public float floatValue() {
		return get();
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
		return Float.toString(get());
	}
}
