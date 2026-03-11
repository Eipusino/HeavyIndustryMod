package endfield.util;

import mindustry.Vars;
import sun.nio.ch.DirectBuffer;

import java.lang.invoke.MethodHandles.Lookup;
import java.nio.Buffer;

public interface PlatformImpl {
	/**
	 * @return Retrieve a {@code lookup} that can access all members within a given {@code class}.
	 */
	Lookup lookup(Class<?> clazz);

	/**
	 * Reflect to call the clone method of Object.
	 */
	<T> T clone(T object);

	default Class<?> getCallerClass() {
		Thread thread = Thread.currentThread();
		StackTraceElement[] trace = thread.getStackTrace();
		try {
			return Class.forName(trace[3].getClassName(), false, Vars.mods.mainLoader());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	void put(Object src, int srcOffset, Object dst, int dstOffset, int numBytes);

	/**
	 * @return The memory address of DirectBuffer
	 * @throws IllegalArgumentException If {@code src} or {@code dst} is not a DirectBuffer
	 */
	default long addressOf(Buffer buffer) {
		if (!buffer.isDirect()) throw new IllegalArgumentException("Only applicable to DirectBuffer");

		return ((DirectBuffer) buffer).address();
	}
}
