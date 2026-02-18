package endfield.util;

public interface MethodInvokeHelper {
	<T> T invoke(Object object, String name, Object... args);

	<T> T invokeStatic(Class<?> clazz, String name, Object... args);

	<T> T newInstance(Class<T> type, Object... args);

	<T> T invokeWithAsType(Object object, String name, Class<?>[] parameterTypes, Object... args);

	<T> T invokeStaticWithAsType(Class<?> clazz, String name, Class<?>[] parameterTypes, Object... args);

	<T> T newInstanceWithAsType(Class<T> type, Class<?>[] parameterTypes, Object... args);
}
