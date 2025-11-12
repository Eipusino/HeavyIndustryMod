package heavyindustry.core;

import heavyindustry.util.PlatformImpl;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DefaultImpl implements PlatformImpl {
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
	public Field[] getFields(Class<?> cls) {
		return cls.getDeclaredFields();
	}

	@Override
	public Method[] getMethods(Class<?> cls) {
		return cls.getDeclaredMethods();
	}

	@Override
	public Constructor<?>[] getConstructors(Class<?> cls) {
		return cls.getDeclaredConstructors();
	}
}
