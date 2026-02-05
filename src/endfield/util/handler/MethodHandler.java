package endfield.util.handler;

import java.util.HashMap;
import java.util.function.Function;

import static endfield.Vars2.methodInvokeHelper;

/**
 * A utility set for method invocation, including invocation, instantiation, etc.
 *
 * @since 1.0.9
 */
@SuppressWarnings("unchecked")
public class MethodHandler<T> {
	static final Function<Class<?>, MethodHandler<?>> function = MethodHandler::new;
	static final HashMap<Class<?>, MethodHandler<?>> defaultMap = new HashMap<>();

	protected final Class<T> clazz;

	public MethodHandler(Class<T> c) {
		clazz = c;
	}

	/**
	 * Create a method handler using default guidelines and cache it for method invocation operations.
	 *
	 * @see MethodHandler#invoke(Object, String, Object...)
	 */
	public static <O, R> R invokeDefault(O object, String name, Object... args) {
		return ((MethodHandler<O>) defaultMap.computeIfAbsent(object.getClass(), function)).invoke(object, name, args);
	}

	/**
	 * Create a method handler using default guidelines and cache it for static method invocation
	 * operations.
	 *
	 * @see MethodHandler#invokeStatic(String, Object...)
	 */
	public static <U, R> R invokeDefault(Class<U> clazz, String name, Object... args) {
		return defaultMap.computeIfAbsent(clazz, function).invokeStatic(name, args);
	}

	/**
	 * Create a method handler using default guidelines to perform method call operations, but do not
	 * cache the handler.
	 *
	 * @see MethodHandler#invoke(Object, String, Object...)
	 */
	public static <O, R> R invokeTemp(O object, String name, Object... args) {
		return ((MethodHandler<O>) new MethodHandler<>(object.getClass())).invoke(object, name, args);
	}

	/**
	 * Create a method handler using default guidelines for static method calls, but do not cache the
	 * handler.
	 *
	 * @see MethodHandler#invokeStatic(String, Object...)
	 */
	public static <U, R> R invokeTemp(Class<U> clazz, String name, Object... args) {
		return new MethodHandler<>(clazz).invokeStatic(name, args);
	}

	/**
	 * Create a method handler using default guidelines and cache it for constructor calls.
	 *
	 * @see MethodHandler#newInstance(Object...)
	 */
	public static <U> U newInstanceDefault(Class<U> clazz, Object... args) {
		return (U) defaultMap.computeIfAbsent(clazz, function).newInstance(args);
	}

	/**
	 * Create a method handler using default guidelines to call the constructor, but do not cache the
	 * handler.
	 *
	 * @see MethodHandler#newInstance(Object...)
	 */
	public static <U> U newInstanceTemp(Class<U> clazz, Object... args) {
		return new MethodHandler<>(clazz).newInstance(args);
	}

	/**
	 * Call a method on an object with its given name and parameter type, which is not affected by access
	 * modifiers. The null in the parameter will be treated as a universal bit, that is, the null bit can be
	 * matched with any type.
	 *
	 * @param object Call the target object executed by the method
	 * @param name   method name
	 * @param args   List of parameters passed to method
	 * @return Return value of the target method
	 */
	public <R> R invoke(T object, String name, Object... args) {
		return methodInvokeHelper.invoke(object, name, args);
	}

	/**
	 * Call a static method with a specified name and parameter type in the type specified by the
	 * processor, which is not affected by access modifiers. The null in the parameter will be treated as a
	 * generic bit, meaning that any type can be matched with a null bit.
	 *
	 * @param name method name
	 * @param args List of parameters passed to method
	 * @return Return value of the target method
	 */
	public <R> R invokeStatic(String name, Object... args) {
		return methodInvokeHelper.invokeStatic(clazz, name, args);
	}

	/**
	 * Instantiate the class specified by this processor to obtain an instance of that type. The null in the
	 * passed parameters will be treated as a universal bit, meaning that any type can be matched with a
	 * null bit.
	 *
	 * @param args List of parameters passed to constructor
	 */
	public T newInstance(Object... args) {
		return methodInvokeHelper.newInstance(clazz, args);
	}

	/**
	 * Specify strict parameter calling methods. If there are strange issues with using the {@link #invoke(Object, String, Object[])}
	 * method, you can use the method.
	 */
	public <R> R invokeWithAsType(T object, String name, Class<?>[] parameterTypes, Object... args) {
		return methodInvokeHelper.invoke(object, name, parameterTypes, args);
	}

	/**
	 * Specify strict parameter calling methods. If there are strange issues with using the {@link #invokeStatic(String, Object[])}
	 * method, you can use the method.
	 */
	public <R> R invokeStaticWithAsType(String name, Class<?>[] parameterTypes, Object... args) {
		return methodInvokeHelper.invokeStatic(clazz, name, parameterTypes, args);
	}

	/**
	 * Specify strict parameter calling methods. If there are strange issues with using the {@link #newInstance(Object...)}
	 * method, you can use the method.
	 */
	public T newInstanceWithAsType(Class<?>[] parameterTypes, Object... args) {
		return methodInvokeHelper.newInstance(clazz, parameterTypes, args);
	}
}
