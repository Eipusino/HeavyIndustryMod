package endfield.util.concurrent;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.VarHandle;

public class AtomicBoolean2 implements Serializable {
	private static final long serialVersionUID = 4654671469794556979l;
	private static final VarHandle handle;

	private volatile boolean value;

	static {
		try {
			Lookup lookup = MethodHandles.lookup();
			handle = lookup.findVarHandle(AtomicBoolean2.class, "value", boolean.class);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public AtomicBoolean2(boolean initialValue) {
		value = initialValue;
	}

	public AtomicBoolean2() {}

	public final boolean get() {
		return value;
	}

	public final boolean compareAndSet(boolean expectedValue, boolean newValue) {
		return handle.compareAndSet(this, expectedValue, newValue);
	}

	public boolean weakCompareAndSetPlain(boolean expectedValue, boolean newValue) {
		return handle.weakCompareAndSetPlain(this, expectedValue, newValue);
	}

	public final void set(boolean newValue) {
		value = newValue;
	}

	public final void lazySet(boolean newValue) {
		handle.setRelease(this, newValue);
	}

	public final boolean getAndSet(boolean newValue) {
		return (boolean) handle.getAndSet(this, newValue);
	}

	@Override
	public String toString() {
		return Boolean.toString(get());
	}

	public final boolean getPlain() {
		return (boolean) handle.get(this);
	}

	public final void setPlain(boolean newValue) {
		handle.set(this, newValue);
	}

	public final boolean getOpaque() {
		return (boolean) handle.getOpaque(this);
	}

	public final void setOpaque(boolean newValue) {
		handle.setOpaque(this, newValue);
	}

	public final boolean getAcquire() {
		return (boolean) handle.getAcquire(this);
	}

	public final void setRelease(boolean newValue) {
		handle.setRelease(this, newValue);
	}

	public final boolean compareAndExchange(boolean expectedValue, boolean newValue) {
		return (boolean) handle.compareAndExchange(this, expectedValue, newValue);
	}

	public final boolean compareAndExchangeAcquire(boolean expectedValue, boolean newValue) {
		return (boolean) handle.compareAndExchangeAcquire(this, expectedValue, newValue);
	}

	public final boolean compareAndExchangeRelease(boolean expectedValue, boolean newValue) {
		return (boolean) handle.compareAndExchangeRelease(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetVolatile(boolean expectedValue, boolean newValue) {
		return handle.weakCompareAndSet(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetAcquire(boolean expectedValue, boolean newValue) {
		return handle.weakCompareAndSetAcquire(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetRelease(boolean expectedValue, boolean newValue) {
		return handle.weakCompareAndSetRelease(this, expectedValue, newValue);
	}
}
