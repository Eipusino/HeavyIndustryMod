package endfield.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ClassHelper {
	default Field getField(Class<?> clazz, String name) {
		try {
			return clazz.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			return null;
		}
	}

	default Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		try {
			return clazz.getDeclaredMethod(name, parameterTypes);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	default <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameterTypes) {
		try {
			return clazz.getDeclaredConstructor(parameterTypes);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	default Field[] getFields(Class<?> clazz) {
		return clazz.getDeclaredFields();
	}

	default Method[] getMethods(Class<?> clazz) {
		return clazz.getDeclaredMethods();
	}

	@SuppressWarnings("unchecked")
	default <T> Constructor<T>[] getConstructors(Class<T> clazz) {
		return (Constructor<T>[]) clazz.getDeclaredConstructors();
	}

	default void setPublic(Class<?> clazz) {}

	Class<?> defineClass(String name, byte[] bytes, ClassLoader loader);
}
