package endfield.util.concurrent;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.VarHandle;

public class AtomicByte extends Number {
	private static final long serialVersionUID = 913606535282412697l;
	private static final VarHandle handle;

	private volatile byte value;

	static {
		try {
			Lookup lookup = MethodHandles.lookup();
			handle = lookup.findVarHandle(AtomicByte.class, "value", byte.class);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public AtomicByte() {}

	public AtomicByte(byte initialValue) {
		value = initialValue;
	}

	public final byte get() {
		return value;
	}

	public final void set(byte newValue) {
		value = newValue;
	}

	public final void lazySet(byte newValue) {
		handle.setRelease(this, newValue);
	}

	public final byte getAndSet(byte newValue) {
		return (byte) handle.getAndSet(this, newValue);
	}

	public final boolean compareAndSet(byte expectedValue, byte newValue) {
		return handle.compareAndSet(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetPlain(byte expectedValue, byte newValue) {
		return handle.weakCompareAndSetPlain(this, expectedValue, newValue);
	}

	public final byte getAndAdd(byte delta) {
		return (byte) handle.getAndAdd(this, delta);
	}

	@Override
	public String toString() {
		return String.valueOf(get());
	}

	@Override
	public byte byteValue() {
		return get();
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

	public final byte getPlain() {
		return (byte) handle.get(this);
	}

	public final void setPlain(byte newValue) {
		handle.set(this, newValue);
	}

	public final byte getOpaque() {
		return (byte) handle.getOpaque(this);
	}

	public final void setOpaque(byte newValue) {
		handle.setOpaque(this, newValue);
	}

	public final byte getAcquire() {
		return (byte) handle.getAcquire(this);
	}

	public final void setRelease(byte newValue) {
		handle.setRelease(this, newValue);
	}

	public final byte compareAndExchange(byte expectedValue, byte newValue) {
		return (byte) handle.compareAndExchange(this, expectedValue, newValue);
	}

	public final byte compareAndExchangeAcquire(byte expectedValue, byte newValue) {
		return (byte) handle.compareAndExchangeAcquire(this, expectedValue, newValue);
	}

	public final byte compareAndExchangeRelease(byte expectedValue, byte newValue) {
		return (byte) handle.compareAndExchangeRelease(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetVolatile(byte expectedValue, byte newValue) {
		return handle.weakCompareAndSet(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetAcquire(byte expectedValue, byte newValue) {
		return handle.weakCompareAndSetAcquire(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetRelease(byte expectedValue, byte newValue) {
		return handle.weakCompareAndSetRelease(this, expectedValue, newValue);
	}
}
