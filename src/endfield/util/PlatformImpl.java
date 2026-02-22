package endfield.util;

import mindustry.Vars;
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

	default Class<?> getCallerClass() {
		Thread thread = Thread.currentThread();
		StackTraceElement[] trace = thread.getStackTrace();
		try {
			return Class.forName(trace[3].getClassName(), false, Vars.mods.mainLoader());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	void putBuffer(Buffer src, int srcOffset, Buffer dst, int dstOffset, int numBytes);

	default long addressOf(Buffer buffer) {
		return ((DirectBuffer) buffer).address();
	}
}
