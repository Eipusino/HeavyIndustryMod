package heavyindustry.core;

import heavyindustry.util.ReflectImpl;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.AccessibleObject;

public class DefaultImpl implements ReflectImpl {
	@Override
	public void setOverride(AccessibleObject override) {
		override.setAccessible(true);
	}

	@Override
	public void setPublic(Class<?> type) {
		// not
	}

	@Override
	public Class<?> callerClass() {
		Thread thread = Thread.currentThread();
		StackTraceElement[] trace = thread.getStackTrace();

		try {
			return Class.forName(trace[3].getClassName());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	@Override
	public Lookup lookup() {
		return MethodHandles.lookup();
	}
}
