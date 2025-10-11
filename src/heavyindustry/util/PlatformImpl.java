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

	/** @return The caller class of the current method. */
	Class<?> callerClass();

	/** @return The highest authority lookup. */
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

	/**
	 * Call {@link MethodHandle#invokeWithArguments(Object[])} without throwing an exception. Poor performance, not
	 * recommended for use.
	 *
	 * @throws NoSuchMethodError Cannot be used on the IOS platform.
	 */
	@SuppressWarnings("unchecked")
	default <T> T invokeWithArguments(MethodHandle handle, Object... args) {
		try {
			return (T) handle.invokeWithArguments(args);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
