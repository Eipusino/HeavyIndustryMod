package endfield.util;

import arc.func.Boolf;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface ClassHelper {
	/**
	 * Return to search for fields in the class by name, including private ones. If not found, return {@code null}.
	 *
	 * @see Class#getDeclaredField(String)
	 */
	default Field findField(Class<?> clazz, String name) {
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
	default Method findMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
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
	default <T> Constructor<T> findConstructor(Class<T> clazz, Class<?>... parameterTypes) {
		try {
			return clazz.getDeclaredConstructor(parameterTypes);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	/**
	 * Return to search for fields in the class by name, including private ones. If it cannot be found, a
	 * {@code RuntimeException} will be thrown.
	 *
	 * @see Class#getDeclaredField(String)
	 */
	default Field getField(Class<?> clazz, String name) {
		try {
			return clazz.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Return to search for methods in the class based on name and parameter type, including private
	 * ones. If it cannot be found, a {@code RuntimeException} will be thrown.
	 *
	 * @see Class#getDeclaredMethod(String, Class[])
	 */
	default Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		try {
			return clazz.getDeclaredMethod(name, parameterTypes);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Return the constructor function in the class based on the parameter type, including private ones. If
	 * it cannot be found, a {@code RuntimeException} will be thrown.
	 *
	 * @see Class#getDeclaredConstructor(Class[])
	 */
	default <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameterTypes) {
		try {
			return clazz.getDeclaredConstructor(parameterTypes);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
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

	default Field findField(Class<?> clazz, Boolf<Field> filler) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (filler.get(field)) return field;
		}
		return null;
	}

	default Method findMethod(Class<?> clazz, Boolf<Method> filler) {
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (filler.get(method)) return method;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	default <T> Constructor<T> findConstructor(Class<T> clazz, Boolf<Constructor<T>> filler) {
		Constructor<T>[] constructors = (Constructor<T>[]) clazz.getDeclaredConstructors();
		for (Constructor<T> constructor : constructors) {
			if (filler.get(constructor)) return constructor;
		}
		return null;
	}

	default Field getField(Class<?> clazz, Boolf<Field> filler) {
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			if (filler.get(field)) return field;
		}
		throw new RuntimeException("Field not found");
	}

	default Method getMethod(Class<?> clazz, Boolf<Method> filler) {
		Method[] methods = clazz.getDeclaredMethods();
		for (Method method : methods) {
			if (filler.get(method)) return method;
		}
		throw new RuntimeException("Method not found");
	}

	@SuppressWarnings("unchecked")
	default <T> Constructor<T> getConstructor(Class<T> clazz, Boolf<Constructor<T>> filler) {
		Constructor<T>[] constructors = (Constructor<T>[]) clazz.getDeclaredConstructors();
		for (Constructor<T> constructor : constructors) {
			if (filler.get(constructor)) return constructor;
		}
		throw new RuntimeException("Constructor not found");
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
