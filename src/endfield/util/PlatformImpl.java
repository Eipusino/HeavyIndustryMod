package endfield.util;

import sun.nio.ch.DirectBuffer;

import java.lang.invoke.MethodHandles.Lookup;
import java.nio.Buffer;

public interface PlatformImpl {
	Lookup lookup(Class<?> clazz);

	void putBuffer(Buffer src, int srcOffset, Buffer dst, int dstOffset, int numBytes);

	default long addressOf(Buffer buffer) {
		return ((DirectBuffer) buffer).address();
	}
}
