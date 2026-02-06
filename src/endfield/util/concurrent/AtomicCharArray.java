package endfield.util.concurrent;

import arc.util.Strings;
import endfield.func.CharCharf;
import endfield.func.CharCharf2;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

public class AtomicCharArray implements CharSequence, Serializable {
	private static final long serialVersionUID = -8144643957578770056l;

	private static final VarHandle handle = MethodHandles.arrayElementVarHandle(char[].class);

	private final char[] array;

	public AtomicCharArray(int length) {
		array = new char[length];
	}

	public AtomicCharArray(char[] arr) {
		array = arr;
	}

	@Override
	public final int length() {
		return array.length;
	}

	@Override
	public char charAt(int index) {
		return get(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		int length = end - start;

		if (start < 0 || start > end || end > length())
			throw new StringIndexOutOfBoundsException(Strings.format("start @, end @, length @", start, end, length));

		char[] result = new char[length];

		System.arraycopy(array, start, result, 0, length);

		return new AtomicCharArray(result);
	}

	public final char get(int i) {
		return (char) handle.getVolatile(array, i);
	}

	public final void set(int i, char newValue) {
		handle.setVolatile(array, i, newValue);
	}

	public final void lazySet(int i, char newValue) {
		handle.setRelease(array, i, newValue);
	}

	public final char getAndSet(int i, char newValue) {
		return (char) handle.getAndSet(array, i, newValue);
	}

	public final boolean compareAndSet(int i, char expectedValue, char newValue) {
		return handle.compareAndSet(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetPlain(int i, char expectedValue, char newValue) {
		return handle.weakCompareAndSetPlain(array, i, expectedValue, newValue);
	}

	public final char getAndUpdate(int i, CharCharf updateFunction) {
		char prev = get(i), next = 0;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = updateFunction.get(prev);
			if (weakCompareAndSetVolatile(i, prev, next))
				return prev;
			haveNext = (prev == (prev = get(i)));
		}
	}

	public final char updateAndGet(int i, CharCharf updateFunction) {
		char prev = get(i), next = 0;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = updateFunction.get(prev);
			if (weakCompareAndSetVolatile(i, prev, next))
				return next;
			haveNext = (prev == (prev = get(i)));
		}
	}

	public final char getAndAccumulate(int i, char x, CharCharf2 accumulatorFunction) {
		char prev = get(i), next = 0;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = accumulatorFunction.get(prev, x);
			if (weakCompareAndSetVolatile(i, prev, next))
				return prev;
			haveNext = (prev == (prev = get(i)));
		}
	}

	public final char accumulateAndGet(int i, char x, CharCharf2 accumulatorFunction) {
		char prev = get(i), next = 0;
		for (boolean haveNext = false; ; ) {
			if (!haveNext)
				next = accumulatorFunction.get(prev, x);
			if (weakCompareAndSetVolatile(i, prev, next))
				return next;
			haveNext = (prev == (prev = get(i)));
		}
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

	public final char getPlain(int i) {
		return (char) handle.get(array, i);
	}

	public final void setPlain(int i, char newValue) {
		handle.set(array, i, newValue);
	}

	public final char getOpaque(int i) {
		return (char) handle.getOpaque(array, i);
	}

	public final void setOpaque(int i, char newValue) {
		handle.setOpaque(array, i, newValue);
	}

	public final char getAcquire(int i) {
		return (char) handle.getAcquire(array, i);
	}

	public final void setRelease(int i, char newValue) {
		handle.setRelease(array, i, newValue);
	}

	public final char compareAndExchange(int i, char expectedValue, char newValue) {
		return (char) handle.compareAndExchange(array, i, expectedValue, newValue);
	}

	public final char compareAndExchangeAcquire(int i, char expectedValue, char newValue) {
		return (char) handle.compareAndExchangeAcquire(array, i, expectedValue, newValue);
	}

	public final char compareAndExchangeRelease(int i, char expectedValue, char newValue) {
		return (char) handle.compareAndExchangeRelease(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetVolatile(int i, char expectedValue, char newValue) {
		return handle.weakCompareAndSet(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetAcquire(int i, char expectedValue, char newValue) {
		return handle.weakCompareAndSetAcquire(array, i, expectedValue, newValue);
	}

	public final boolean weakCompareAndSetRelease(int i, char expectedValue, char newValue) {
		return handle.weakCompareAndSetRelease(array, i, expectedValue, newValue);
	}
}
