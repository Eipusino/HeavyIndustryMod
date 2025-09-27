package heavyindustry.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface PlatformImpl {
	void setOverride(AccessibleObject override);

	void setPublic(Class<?> type);

	Class<?> callerClass();

	Lookup lookup();

	default Field[] getFields(Class<?> cls) {
		return cls.getDeclaredFields();
	}

	default Method[] getMethods(Class<?> cls) {
		return cls.getDeclaredMethods();
	}

	default Constructor<?>[] getConstructors(Class<?> cls) {
		return cls.getDeclaredConstructors();
	}

	@SuppressWarnings("unchecked")
	default <T> T invoke(MethodHandle handle, Object... args) {
		try {
			return (T) handle.invokeWithArguments(args);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
