package endfield.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface PlatformImpl {
	default Field getField(Class<?> type, String name) {
		try {
			return type.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			return null;
		}
	}

	default Method getMethod(Class<?> type, String name, Class<?>... parameterTypes) {
		try {
			return type.getDeclaredMethod(name, parameterTypes);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	default <T> Constructor<T> getConstructor(Class<T> type, Class<?>... parameterTypes) {
		try {
			return type.getDeclaredConstructor(parameterTypes);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	default Field[] getFields(Class<?> type) {
		return type.getDeclaredFields();
	}

	default Method[] getMethods(Class<?> type) {
		return type.getDeclaredMethods();
	}

	@SuppressWarnings("unchecked")
	default <T> Constructor<T>[] getConstructors(Class<T> type) {
		return (Constructor<T>[]) type.getDeclaredConstructors();
	}

	default void setOverride(AccessibleObject object) {
		Reflects.setAccessible(object);
	}

	default void setPublic(Class<?> type) {}

	/** @return The caller class of the current method. */
	Class<?> callerClass();

	/**
	 * Converts an array of bytes into an instance of class.
	 *
	 * @param bytes The bytes that make up the class data.
	 * @since 1.0.9
	 */
	default Class<?> defineClass(String name, byte[] bytes, ClassLoader loader) throws ClassFormatError {
		return null;
	}
}
