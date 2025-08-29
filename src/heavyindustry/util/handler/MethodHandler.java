package heavyindustry.util.handler;

import java.util.HashMap;

import static heavyindustry.HVars.methodInvokeHelper;

/**
 * 对方法调用的实用工具集，包含调用，实例化等
 *
 * @author EBwilson
 * @since 1.2
 */
@SuppressWarnings("unchecked")
public class MethodHandler<T> {
	private static final HashMap<Class<?>, MethodHandler<?>> defaultMap = new HashMap<>();

	private final Class<T> clazz;

	public MethodHandler(Class<T> c) {
		clazz = c;
	}

	/**
	 * 使用默认准则创建一个方法处理器并缓存它，使用它来进行方法调用操作
	 *
	 * @see MethodHandler#invoke(Object, String, Object...)
	 */
	public static <O, R> R invokeDefault(O object, String name, Object... args) {
		return ((MethodHandler<O>) defaultMap.computeIfAbsent(object.getClass(), e -> new MethodHandler<>(object.getClass()))).invoke(object, name, args);
	}

	/**
	 * 使用默认准则创建一个方法处理器并缓存它，使用它来进行方法静态调用操作
	 *
	 * @see MethodHandler#invokeStatic(String, Object...)
	 */
	public static <U, R> R invokeDefault(Class<U> clazz, String name, Object... args) {
		return defaultMap.computeIfAbsent(clazz, e -> new MethodHandler<>(clazz)).invokeStatic(name, args);
	}

	/**
	 * 使用默认准则创建一个方法处理器，使用它来进行方法调用操作，但不缓存这个处理器
	 *
	 * @see MethodHandler#invoke(Object, String, Object...)
	 */
	public static <O, R> R invokeTemp(O object, String name, Object... args) {
		return ((MethodHandler<O>) new MethodHandler<>(object.getClass())).invoke(object, name, args);
	}

	/**
	 * 使用默认准则创建一个方法处理器，使用它来进行静态方法调用，但不缓存这个处理器
	 *
	 * @see MethodHandler#invokeStatic(String, Object...)
	 */
	public static <U, R> R invokeTemp(Class<U> clazz, String name, Object... args) {
		return new MethodHandler<>(clazz).invokeStatic(name, args);
	}

	/**
	 * 使用默认准则创建一个方法处理器并缓存它，使用它来进行构造函数调用
	 *
	 * @see MethodHandler#newInstance(Object...)
	 */
	public static <U> U newInstanceDefault(Class<U> clazz, Object... args) {
		return (U) defaultMap.computeIfAbsent(clazz, e -> new MethodHandler<>(clazz)).newInstance(args);
	}

	/**
	 * 使用默认准则创建一个方法处理器，使用它来调用构造函数，但不缓存这个处理器
	 *
	 * @see MethodHandler#newInstance(Object...)
	 */
	public static <U> U newInstanceTemp(Class<U> clazz, Object... args) {
		return new MethodHandler<>(clazz).newInstance(args);
	}

	/**
	 * 对一个对象调用其给定名称和参数类型的方法，这不受访问修饰符影响，参数中的null会按通用位处理，即null位任何类型都可以进行匹配
	 *
	 * @param object 调用所方法执行的目标对象
	 * @param name   方法名称
	 * @param args   传递给方法的参数列表
	 * @return 目标方法的返回值
	 */
	public <R> R invoke(T object, String name, Object... args) {
		return methodInvokeHelper.invoke(object, name, args);
	}

	/**
	 * 调用该处理器指定的类型中具有指定名称和参数类型的静态方法，这不受访问修饰符影响，参数中的null会按通用位处理，即null位任何类型都可以进行匹配
	 *
	 * @param name 方法名称
	 * @param args 传递给方法的参数列表
	 * @return 目标方法的返回值
	 */
	public <R> R invokeStatic(String name, Object... args) {
		return methodInvokeHelper.invokeStatic(clazz, name, args);
	}

	/**
	 * 实例化这个处理器指定的类，获得一个该类型的实例，传入的参数中的null会按通用位处理，即null位任何类型都可以进行匹配
	 *
	 * @param args 传递给构造器的参数列表
	 */
	public T newInstance(Object... args) {
		return methodInvokeHelper.newInstance(clazz, args);
	}
}
