package endfield.util.concurrent;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.VarHandle;

public class AtomicShort extends Number {
	private static final long serialVersionUID = 6169410170337329345l;
	private static final VarHandle handle;

	private volatile short value;

	static {
		try {
			Lookup lookup = MethodHandles.lookup();
			handle = lookup.findVarHandle(AtomicShort.class, "value", short.class);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public AtomicShort() {}

	public AtomicShort(short initialValue) {
		value = initialValue;
	}

	public final short get() {
		return value;
	}

	public final void set(short newValue) {
		value = newValue;
	}

	public final void lazySet(short newValue) {
		handle.setRelease(this, newValue);
	}

	public final short getAndSet(short newValue) {
		return (short) handle.getAndSet(this, newValue);
	}

	public final boolean compareAndSet(short expectedValue, short newValue) {
		return handle.compareAndSet(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetPlain(short expectedValue, short newValue) {
		return handle.weakCompareAndSetPlain(this, expectedValue, newValue);
	}

	public final short getAndAdd(short delta) {
		return (short) handle.getAndAdd(this, delta);
	}

	@Override
	public String toString() {
		return String.valueOf(get());
	}

	@Override
	public short shortValue() {
		return get();
	}

	@Override
	public int intValue() {
		return get();
	}

	@Override
	public long longValue() {
		return get();
	}

	@Override
	public float floatValue() {
		return get();
	}

	@Override
	public double doubleValue() {
		return get();
	}

	public final short getPlain() {
		return (short) handle.get(this);
	}

	public final void setPlain(short newValue) {
		handle.set(this, newValue);
	}

	public final short getOpaque() {
		return (short) handle.getOpaque(this);
	}

	public final void setOpaque(short newValue) {
		handle.setOpaque(this, newValue);
	}

	public final short getAcquire() {
		return (short) handle.getAcquire(this);
	}

	public final void setRelease(short newValue) {
		handle.setRelease(this, newValue);
	}

	public final short compareAndExchange(short expectedValue, short newValue) {
		return (short) handle.compareAndExchange(this, expectedValue, newValue);
	}

	public final short compareAndExchangeAcquire(short expectedValue, short newValue) {
		return (short) handle.compareAndExchangeAcquire(this, expectedValue, newValue);
	}

	public final short compareAndExchangeRelease(short expectedValue, short newValue) {
		return (short) handle.compareAndExchangeRelease(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetVolatile(short expectedValue, short newValue) {
		return handle.weakCompareAndSet(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetAcquire(short expectedValue, short newValue) {
		return handle.weakCompareAndSetAcquire(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetRelease(short expectedValue, short newValue) {
		return handle.weakCompareAndSetRelease(this, expectedValue, newValue);
	}
}
