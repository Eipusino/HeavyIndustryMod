package endfield.util.concurrent;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class AtomicByteArray implements Serializable {
	private static final long serialVersionUID = -8156138186138649677l;

	private static final VarHandle handle = MethodHandles.arrayElementVarHandle(byte[].class);

	private final byte[] array;

	public AtomicByteArray(int length) {
		array = new byte[length];
	}

	public AtomicByteArray(byte[] array) {
		this.array = array.clone();
	}

	public final int length() {
		return array.length;
	}

	public final byte get(int i) {
		return (byte) handle.getVolatile(array, i);
	}

	public final void set(int i, byte newValue) {
		handle.setVolatile(array, i, newValue);
	}

	public final void lazySet(int i, byte newValue) {
		handle.setRelease(array, i, newValue);
	}

	public final byte getAndSet(int i, byte newValue) {
		return (byte) handle.getAndSet(array, i, newValue);
	}

	public final boolean compareAndSet(int i, byte expectedValue, byte newValue) {
		return handle.compareAndSet(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetPlain(int i, byte expectedValue, byte newValue) {
		return handle.weakCompareAndSetPlain(array, i, expectedValue, newValue);
	}

	public final byte getAndAdd(int i, byte delta) {
		return (byte) handle.getAndAdd(array, i, delta);
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

	public final byte getPlain(int i) {
		return (byte) handle.get(array, i);
	}

	public final void setPlain(int i, byte newValue) {
		handle.set(array, i, newValue);
	}

	public final byte getOpaque(int i) {
		return (byte) handle.getOpaque(array, i);
	}

	public final void setOpaque(int i, byte newValue) {
		handle.setOpaque(array, i, newValue);
	}

	public final byte getAcquire(int i) {
		return (byte) handle.getAcquire(array, i);
	}

	public final void setRelease(int i, byte newValue) {
		handle.setRelease(array, i, newValue);
	}

	public final byte compareAndExchange(int i, byte expectedValue, byte newValue) {
		return (byte) handle.compareAndExchange(array, i, expectedValue, newValue);
	}

	public final byte compareAndExchangeAcquire(int i, byte expectedValue, byte newValue) {
		return (byte) handle.compareAndExchangeAcquire(array, i, expectedValue, newValue);
	}

	public final byte compareAndExchangeRelease(int i, byte expectedValue, byte newValue) {
		return (byte) handle.compareAndExchangeRelease(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetVolatile(int i, byte expectedValue, byte newValue) {
		return handle.weakCompareAndSet(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetAcquire(int i, byte expectedValue, byte newValue) {
		return handle.weakCompareAndSetAcquire(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetRelease(int i, byte expectedValue, byte newValue) {
		return handle.weakCompareAndSetRelease(array, i, expectedValue, newValue);
	}
}
