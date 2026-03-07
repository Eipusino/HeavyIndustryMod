package endfield.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface MethodInvokeHelper {
	<T> T invoke(Object object, String name, Object... args);

	<T> T invokeStatic(Class<?> clazz, String name, Object... args);

	<T> T newInstance(Class<T> type, Object... args);

	<T> T invokeWithAsType(Object object, String name, Class<?>[] parameterTypes, Object... args);

	<T> T invokeStaticWithAsType(Class<?> clazz, String name, Class<?>[] parameterTypes, Object... args);

	<T> T newInstanceWithAsType(Class<T> type, Class<?>[] parameterTypes, Object... args);

	@SuppressWarnings("unchecked")
	default <T> T invoke(Method method, Object object, boolean access, Object... args) {
		try {
			if (access) method.setAccessible(true);
			return (T) method.invoke(object, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	default <T> T invokeStatic(Method method, boolean access, Object... args) {
		try {
			if (access) method.setAccessible(true);
			return (T) method.invoke(null, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

	default <T> T newInstance(Constructor<T> constructor, boolean access, Object... args) {
		try {
			if (access) constructor.setAccessible(true);
			return constructor.newInstance(args);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
