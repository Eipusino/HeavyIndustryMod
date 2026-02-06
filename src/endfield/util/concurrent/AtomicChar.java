package endfield.util.concurrent;

import endfield.func.CharCharf;
import endfield.func.CharCharf2;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.VarHandle;

public class AtomicChar implements Serializable {
	private static final long serialVersionUID = -1593944861369051563l;
	private static final VarHandle handle;

	private volatile char value;

	static {
		try {
			Lookup lookup = MethodHandles.lookup();
			handle = lookup.findVarHandle(AtomicChar.class, "value", char.class);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public final char get() {
		return value;
	}

	public final void set(char newValue) {
		value = newValue;
	}

	public final void lazySet(char newValue) {
		handle.setRelease(this, newValue);
	}

	public final char getAndSet(char newValue) {
		return (char) handle.getAndSet(this, newValue);
	}

	public final boolean compareAndSet(char expectedValue, char newValue) {
		return handle.compareAndSet(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetPlain(char expectedValue, char newValue) {
		return handle.weakCompareAndSetPlain(this, expectedValue, newValue);
	}

	public final char getAndUpdate(CharCharf updateFunction) {
		char prev = get(), next = 0;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = updateFunction.get(prev);
			if (weakCompareAndSetVolatile(prev, next))
				return prev;
			haveNext = (prev == (prev = get()));
		}
	}

	public final char updateAndGet(CharCharf updateFunction) {
		char prev = get(), next = 0;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = updateFunction.get(prev);
			if (weakCompareAndSetVolatile(prev, next))
				return next;
			haveNext = (prev == (prev = get()));
		}
	}

	public final char getAndAccumulate(char x, CharCharf2 accumulatorFunction) {
		char prev = get(), next = 0;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = accumulatorFunction.get(prev, x);
			if (weakCompareAndSetVolatile(prev, next))
				return prev;
			haveNext = (prev == (prev = get()));
		}
	}

	public final char accumulateAndGet(char x, CharCharf2 accumulatorFunction) {
		char prev = get(), next = 0;
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
		return Character.toString(get());
	}

	public final char getPlain() {
		return (char) handle.get(this);
	}

	public final void setPlain(char newValue) {
		handle.set(this, newValue);
	}

	public final char getOpaque() {
		return (char) handle.getOpaque(this);
	}

	public final void setOpaque(char newValue) {
		handle.setOpaque(this, newValue);
	}

	public final char getAcquire() {
		return (char) handle.getAcquire(this);
	}

	public final void setRelease(char newValue) {
		handle.setRelease(this, newValue);
	}

	public final char compareAndExchange(char expectedValue, char newValue) {
		return (char) handle.compareAndExchange(this, expectedValue, newValue);
	}

	public final char compareAndExchangeAcquire(char expectedValue, char newValue) {
		return (char) handle.compareAndExchangeAcquire(this, expectedValue, newValue);
	}

	public final char compareAndExchangeRelease(char expectedValue, char newValue) {
		return (char) handle.compareAndExchangeRelease(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetVolatile(char expectedValue, char newValue) {
		return handle.weakCompareAndSet(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetAcquire(char expectedValue, char newValue) {
		return handle.weakCompareAndSetAcquire(this, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetRelease(char expectedValue, char newValue) {
		return handle.weakCompareAndSetRelease(this, expectedValue, newValue);
	}
}
