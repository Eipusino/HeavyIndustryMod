package heavyindustry.util;

import java.lang.reflect.AccessibleObject;

public interface ReflectImpl {
	void setOverride(AccessibleObject override) throws Exception;

	void setPublic(Class<?> obj) throws Exception;

	default Class<?> getCallerClass() {
		try {
			Thread thread = Thread.currentThread();
			StackTraceElement[] trace = thread.getStackTrace();
			return Class.forName(trace[3].getClassName());
		} catch (ClassNotFoundException e) {
			// This situation usually does not occur.
			return null;
		}
	}
}
