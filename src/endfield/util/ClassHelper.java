package endfield.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ClassHelper {
	/**
	 * Return to search for fields in the class by name, including private ones. If not found, return {@code null}.
	 *
	 * @see Class#getDeclaredField(String)
	 */
	default Field getField(Class<?> clazz, String name) {
		try {
			return clazz.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			return null;
		}
	}

	/**
	 * Return to search for methods in the class based on name and parameter type, including private
	 * ones. If not found, return {@code null}.
	 *
	 * @see Class#getDeclaredMethod(String, Class[])
	 */
	default Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		try {
			return clazz.getDeclaredMethod(name, parameterTypes);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	/**
	 * Return the constructor function in the class based on the parameter type, including private ones. If
	 * it cannot be found, it will return {@code null}.
	 *
	 * @see Class#getDeclaredConstructor(Class[])
	 */
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

	/**
	 * Create an instance object of a class directly by bypassing the constructor, where all field values
	 * within the object are in an uninitialized state. Cannot support primitive classes, abstract classes, and
	 * interfaces.
	 * <p><strong>If {@code null} is passed in, it will cause the JVM to crash.</strong>
	 */
	<T> T allocateInstance(Class<? extends T> clazz);

	/**
	 * Create a class using a class file in the form of a given byte array. If it does not comply with the JVM's
	 * class specifications, an exception will be thrown.
	 */
	Class<?> defineClass(String name, byte[] bytes, ClassLoader loader);
}
