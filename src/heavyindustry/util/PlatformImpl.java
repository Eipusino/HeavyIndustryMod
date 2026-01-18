package heavyindustry.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface PlatformImpl {
	default void setOverride(AccessibleObject object) {
		Reflects.setAccessible(object);
	}

	default void setPublic(Class<?> type) {}

	/** @return The caller class of the current method. */
	Class<?> callerClass();

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
}
