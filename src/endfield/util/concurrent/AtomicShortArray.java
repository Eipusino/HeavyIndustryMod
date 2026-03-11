package endfield.util.concurrent;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class AtomicShortArray implements Serializable {
	private static final long serialVersionUID = 631493142077646624l;

	private static final VarHandle handle = MethodHandles.arrayElementVarHandle(short[].class);

	private final short[] array;

	public AtomicShortArray(int length) {
		array = new short[length];
	}

	public AtomicShortArray(short[] array) {
		this.array = array.clone();
	}

	public final int length() {
		return array.length;
	}

	public final short get(int i) {
		return (short) handle.getVolatile(array, i);
	}

	public final void set(int i, short newValue) {
		handle.setVolatile(array, i, newValue);
	}

	public final void lazySet(int i, short newValue) {
		handle.setRelease(array, i, newValue);
	}

	public final short getAndSet(int i, short newValue) {
		return (short) handle.getAndSet(array, i, newValue);
	}

	public final boolean compareAndSet(int i, short expectedValue, short newValue) {
		return handle.compareAndSet(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetPlain(int i, short expectedValue, short newValue) {
		return handle.weakCompareAndSetPlain(array, i, expectedValue, newValue);
	}

	public final short getAndAdd(int i, short delta) {
		return (short) handle.getAndAdd(array, i, delta);
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

	public final short getPlain(int i) {
		return (short) handle.get(array, i);
	}

	public final void setPlain(int i, short newValue) {
		handle.set(array, i, newValue);
	}

	public final short getOpaque(int i) {
		return (short) handle.getOpaque(array, i);
	}

	public final void setOpaque(int i, short newValue) {
		handle.setOpaque(array, i, newValue);
	}

	public final short getAcquire(int i) {
		return (short) handle.getAcquire(array, i);
	}

	public final void setRelease(int i, short newValue) {
		handle.setRelease(array, i, newValue);
	}

	public final short compareAndExchange(int i, short expectedValue, short newValue) {
		return (short) handle.compareAndExchange(array, i, expectedValue, newValue);
	}

	public final short compareAndExchangeAcquire(int i, short expectedValue, short newValue) {
		return (short) handle.compareAndExchangeAcquire(array, i, expectedValue, newValue);
	}

	public final short compareAndExchangeRelease(int i, short expectedValue, short newValue) {
		return (short) handle.compareAndExchangeRelease(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetVolatile(int i, short expectedValue, short newValue) {
		return handle.weakCompareAndSet(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetAcquire(int i, short expectedValue, short newValue) {
		return handle.weakCompareAndSetAcquire(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetRelease(int i, short expectedValue, short newValue) {
		return handle.weakCompareAndSetRelease(array, i, expectedValue, newValue);
	}
}
