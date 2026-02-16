package endfield.util;

import sun.nio.ch.DirectBuffer;

import java.lang.invoke.MethodHandles.Lookup;
import java.nio.Buffer;

public interface PlatformImpl {
	/** @return Retrieve a {@code lookup} that can access all members within a given {@code class}. */
	Lookup lookup(Class<?> clazz);

	/**
	 * Reflect to call the clone method of Object. If the object does not implement {@link Cloneable}, an
	 * exception will be thrown.
	 */
	<T> T clone(T object);

	void putBuffer(Buffer src, int srcOffset, Buffer dst, int dstOffset, int numBytes);

	default long addressOf(Buffer buffer) {
		return ((DirectBuffer) buffer).address();
	}
}
