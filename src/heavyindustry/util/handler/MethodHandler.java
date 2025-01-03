package heavyindustry.util.handler;

import java.util.*;

import static heavyindustry.core.HeavyIndustryMod.*;

/** A utility set for method invocation, including invocation, instantiation, etc. */
@SuppressWarnings("unchecked")
public class MethodHandler<T> {
    private static final HashMap<Class<?>, MethodHandler<?>> defaultMap = new HashMap<>();

    private final Class<T> clazz;

    public MethodHandler(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Create a method handler using default guidelines and cache it for method invocation operations.
     *
     * @see MethodHandler#invoke(Object, String, Object...)
     */
    public static <A, B> B invokeDefault(A object, String name, Object... args) {
        return ((MethodHandler<A>) defaultMap.computeIfAbsent(object.getClass(), e -> new MethodHandler<>(object.getClass()))).invoke(object, name, args);
    }

    /**
     * Create a method handler using default guidelines and cache it for static method invocation operations.
     *
     * @see MethodHandler#invokeStatic(String, Object...)
     */
    public static <A, B> B invokeDefault(Class<A> clazz, String name, Object... args) {
        return defaultMap.computeIfAbsent(clazz, e -> new MethodHandler<>(clazz)).invokeStatic(name, args);
    }

    /**
     * Create a method handler using default guidelines to perform method call operations, but do not cache the handler.
     *
     * @see MethodHandler#invoke(Object, String, Object...)
     */
    public static <A, B> B invokeTemp(A object, String name, Object... args) {
        return ((MethodHandler<A>) new MethodHandler<>(object.getClass())).invoke(object, name, args);
    }

    /**
     * Create a method handler using default guidelines for static method calls, but do not cache the handler.
     *
     * @see MethodHandler#invokeStatic(String, Object...)
     */
    public static <A, B> B invokeTemp(Class<A> clazz, String name, Object... args) {
        return new MethodHandler<>(clazz).invokeStatic(name, args);
    }

    /**
     * Create a method handler using default guidelines and cache it for constructor calls.
     *
     * @see MethodHandler#newInstance(Object...)
     */
    public static <A> A newInstanceDefault(Class<A> clazz, Object... args) {
        return (A) defaultMap.computeIfAbsent(clazz, e -> new MethodHandler<>(clazz)).newInstance(args);
    }

    /**
     * Create a method handler using default guidelines to call the constructor, but do not cache the handler.
     *
     * @see MethodHandler#newInstance(Object...)
     */
    public static <A> A newInstanceTemp(Class<A> clazz, Object... args) {
        return new MethodHandler<>(clazz).newInstance(args);
    }

    /**
     * Call a method on an object with its given name and parameter type, which is not affected by access modifiers.
     * The null in the parameter will be treated as a universal bit, that is, the null bit can be matched with any type.
     *
     * @param object Call the target object executed by the method
     * @param name   Method name
     * @param args   List of parameters passed to method
     * @return B value of the target method
     */
    public <R> R invoke(T object, String name, Object... args) {
        return methodInvokeHelper.invoke(object, name, args);
    }

    /**
     * Call a static method with a specified name and parameter type in the type specified by the processor, which is not affected by access modifiers.
     * The null in the parameter will be treated as a generic bit, meaning that any type can be matched with a null bit.
     *
     * @param name Method name
     * @param args List of parameters passed to method
     * @return B value of the target method
     */
    public <R> R invokeStatic(String name, Object... args) {
        return methodInvokeHelper.invokeStatic(clazz, name, args);
    }

    /**
     * Instantiate the class specified by this processor to obtain an instance of that type.
     * The null in the past parameters will be treated as a universal bit, meaning that any type can be matched with a null bit.
     *
     * @param args List of parameters passed to constructor
     */
    public T newInstance(Object... args) {
        return methodInvokeHelper.newInstance(clazz, args);
    }
}
