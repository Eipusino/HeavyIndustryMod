package heavyindustry.util;

import java.lang.reflect.AccessibleObject;

public interface ReflectImpl {
	void setOverride(AccessibleObject override);

	void setPublic(Class<?> type);

	default Class<?> getCallerClass() {
		try {
			Thread thread = Thread.currentThread();
			StackTraceElement[] trace = thread.getStackTrace();
			return Class.forName(trace[3].getClassName());
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
}
