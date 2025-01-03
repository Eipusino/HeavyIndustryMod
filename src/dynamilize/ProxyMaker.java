package dynamilize;

import java.lang.reflect.*;
import java.util.*;

/**
 * Proxy creation tool, used to generate aspect oriented proxy instances similar to {@linkplain  Proxy java proxy tool}, but different in that it allows delegation from type, similar to <i>cglib</i>.
 * <p>The proxy instance created through this tool will transfer all available (non-static/final/private/package private) method calls into the proxy call handler, which is declared as {@link ProxyMaker#invoke(DynamicObject, FuncMarker, FuncMarker, ArgumentList)} in this tool.
 * <p>When using this tool, you need to provide a {@link DynamicMaker} to build a dynamic proxy instance, although you may only need a {@linkplain DynamicClass dynamic type} with proxy behavior.
 * <p>In addition, functions declared in dynamic types will also be intercepted by proxies, and their timing of action is consistent with the behavior of dynamic class instantiation. Please refer to {@link DynamicClass Timing of Dynamic Type Function Changes}.
 * <p>You can use lambda expressions to reference {@link ProxyMaker#getDefault(DynamicMaker, ProxyHandler)} to obtain the default lambda implementation,
 * Or implement such abstract methods {@link ProxyMaker#invoke(DynamicObject, FuncMarker, FuncMarker, ArgumentList)}.
 * <pre>{@code
 * //A simple use case:
 * ArrayList list = ProxyMaker.getDefault(DynamicMaker.getDefault(), (self, method, args) -> {
 *     System.out.println(method);
 *     return method.invoke(self, args);
 * }).newProxyInstance(ArrayList.class).self();
 *
 * //All available methods on this list will print the signature of the method itself when executed.
 * }</pre>
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class ProxyMaker {
    private static final Map<DynamicClass, Map<ClassImplements<?>, DynamicClass>> proxyMap = new WeakHashMap<>();
    private static final Map<ClassImplements<?>, DynamicClass> nonSuperProxy = new HashMap<>();
    public static final Class<?>[] EMPTY_CLASSES = new Class[0];
    public static final Object[] EMPTY_ARGS = new Object[0];

    protected final DynamicMaker maker;

    protected ProxyMaker(DynamicMaker maker) {
        this.maker = maker;
    }

    /**
     * To obtain the default implementation instance of the proxy generator, a {@linkplain DynamicMaker dynamic generator} needs to be provided to build the instance.
     *
     * @param maker Generator used for creating dynamic objects.
     * @param proxyHandler Intercept the {@link ProxyMaker#invoke(DynamicObject, FuncMarker, FuncMarker, ArgumentList)} method used for proxy processing, and all valid method calls will be intercepted and passed into this method.
     * @return default instance
     */
    public static ProxyMaker getDefault(DynamicMaker maker, ProxyHandler proxyHandler) {
        return new ProxyMaker(maker) {
            @Override
            public Object invoke(DynamicObject<?> proxy, FuncMarker method, FuncMarker proxiedMethod, ArgumentList args) {
                return proxyHandler.invoke(proxy, method, proxiedMethod, args);
            }
        };
    }

    public DynamicObject<Object> newProxyInstance(Class<?>[] interfaces) {
        return newProxyInstance(interfaces, null);
    }

    public <T> DynamicObject<T> newProxyInstance(Class<T> base, Object... args) {
        return newProxyInstance(base, EMPTY_CLASSES, null, null, args);
    }

    public <T> DynamicObject<T> newProxyInstance(Class<T> base, DynamicClass dyClass, Object... args) {
        return newProxyInstance(base, EMPTY_CLASSES, null, dyClass, args);
    }

    public DynamicObject<Object> newProxyInstance(Class<?>[] interfaces, Class<?>[] aspects) {
        return newProxyInstance(Object.class, interfaces, aspects, EMPTY_ARGS);
    }

    public <T> DynamicObject<T> newProxyInstance(Class<T> base, Class<?>[] aspects, Object... args) {
        return newProxyInstance(base, EMPTY_CLASSES, aspects, null, args);
    }

    public <T> DynamicObject<T> newProxyInstance(Class<T> base, Class<?>[] aspects, DynamicClass superDyClass, Object... args) {
        return newProxyInstance(base, EMPTY_CLASSES, aspects, superDyClass, args);
    }

    public <T> DynamicObject<T> newProxyInstance(Class<T> base, Class<?>[] interfaces, Class<?>[] aspects, Object... args) {
        return newProxyInstance(base, interfaces, aspects, null, args);
    }

    /**
     * Create a proxy instance where all valid methods have been intercepted by the provided proxy processor.
     *
     * @param base The Java base class for proxy delegation, and proxy instances can be correctly assigned to this type
     * @param interfaces List of interfaces implemented by the proxy, all methods will be implemented to call the proxy handling function
     * @param dynamicClass The dynamic type of the proxy instance can be empty
     * @param args Parameters of Constructors Available in Proxy Delegation Types
     * @return A proxy instance
     */
    public <T> DynamicObject<T> newProxyInstance(Class<T> base, Class<?>[] interfaces, Class<?>[] aspects, DynamicClass dynamicClass, Object... args) {
        DynamicClass dyClass = getProxyDyClass(dynamicClass, base, interfaces, aspects);

        return maker.newInstance(base, interfaces, aspects, dyClass, args);
    }

    /**
     * Obtain dynamic types declared as proxies from class and interface implementations,
     * and the dynamic types given by parameters will serve as direct superclass of that type, which can be empty.
     *
     * @param dynamicClass When the direct superclass of the result dynamic type is empty, it indicates that the type superclass is not clear
     * @param base Delegate base class
     * @param interfaces Proxy implemented interface
     * @return Declare dynamic types implemented as proxies
     */
    private <T> DynamicClass getProxyDyClass(DynamicClass dynamicClass, Class<T> base, Class<?>[] interfaces, Class<?>[] aspects) {
        ClassImplements<T> impl = new ClassImplements<>(base, interfaces, aspects);
        DynamicClass dyc = dynamicClass == null? nonSuperProxy.get(impl): proxyMap.computeIfAbsent(dynamicClass, e -> new HashMap<>()).get(impl);

        if (dyc == null) {
            dyc = dynamicClass == null ? DynamicClass.get("defProxy$" + impl) : DynamicClass.declare(dynamicClass.getName() + "$proxy$" + impl, dynamicClass);

            Class<?> dyBase = maker.getDynamicBase(base, interfaces, aspects);
            for (Method method: dyBase.getDeclaredMethods()) {
                String methodName = method.getName();
                if (method.getAnnotation(DynamicMaker.DynamicMethod.class) != null) {
                    FunctionType type = FunctionType.from(method);
                    FuncMarker caller = new FuncMarker() {
                        @Override
                        public String getName() {
                            return methodName;
                        }

                        @Override
                        public FunctionType getType() {
                            return type;
                        }

                        @Override
                        public Object invoke(DynamicObject<?> self, ArgumentList args) {
                            return self.invokeFunc(type, methodName, args.args());
                        }
                    };

                    dyc.setFunction(
                            methodName,
                            (s, su, a) -> {
                                FunctionMarker superCaller = FunctionMarker.make(su.getFunc(methodName, type));
                                try {
                                    Object res = invoke(s, caller, superCaller, a);
                                    superCaller.recycle();
                                    return res;
                                } catch (Throwable e) {
                                    superCaller.recycle();
                                    throwException(e);
                                    return null;
                                }
                            },
                            method.getParameterTypes()
                    );
                }
            }

            (dynamicClass == null ? nonSuperProxy : proxyMap.get(dynamicClass)).put(impl, dyc);
        }

        return dyc;
    }

    /**
     * Proxy processor, all methods intercepted by the proxy will be transferred to this method, and methods/functions will be passed to this method in the form of an anonymous function.
     * <p>The default implementation call will pass the given anonymous function, otherwise subclasses should implement this method according to the required proxy processing method.
     *
     * @param proxy Dynamic proxy instance
     * @param method Methods of interception
     * @param args Actual parameter list
     * @return return value
     */
    protected abstract Object invoke(DynamicObject<?> proxy, FuncMarker method, FuncMarker proxiedMethod, ArgumentList args);

    /**
     * Exception handler, this method is used to handle any exceptions that occur during proxy operation. By default, it is directly encapsulated as a RuntimeException and thrown.
     *
     * @param thr Anomalies captured during operation.
     */
    public void throwException(Throwable thr) {
        throw new RuntimeException(thr);
    }

    public static class FunctionMarker implements FuncMarker {
        public static int maxPoolSize = 4096;

        private static FunctionMarker[] pool = new FunctionMarker[128];
        private static int cursor = -1;

        private String name;
        private FunctionType type;
        private FunctionEntryc entry;
        private Function<?, Object> function;

        private static FunctionMarker make(FunctionEntryc functionEntry) {
            FunctionMarker res = cursor < 0? new FunctionMarker(): pool[cursor];

            res.name = functionEntry.getName();
            res.type = functionEntry.getType();
            res.entry = functionEntry;
            res.function = functionEntry.getFunction();

            return res;
        }

        @Override
        public String getName() {
            return entry.getName();
        }

        @Override
        public FunctionType getType() {
            return entry.getType();
        }

        @Override
        public Object invoke(DynamicObject<?> self, ArgumentList args) {
            return function.invoke((DynamicObject) self, args);
        }

        @Override
        public String toString() {
            return "function: " + name + type;
        }

        private void recycle() {
            name = null;
            type = null;
            function = null;
            entry = null;

            if (cursor >= maxPoolSize) return;

            cursor++;
            if (cursor >= pool.length) {
                pool = Arrays.copyOf(pool, pool.length*2);
            }

            pool[cursor] = this;
        }
    }

    /** Calling the wrapper provides a method {@link FuncMarker#invoke(DynamicObject, ArgumentList)} to directly call the method or function encapsulated by this object. */
    public interface FuncMarker {
        default String signature() {
            return FunctionType.signature(getName(), getType());
        }

        String getName();

        FunctionType getType();

        Object invoke(DynamicObject<?> self, ArgumentList args);

        default Object invoke(DynamicObject<?> self, Object... args) {
            ArgumentList lis = ArgumentList.as(args);
            Object r = invoke(self, lis);
            lis.type().recycle();
            lis.recycle();
            return r;
        }

        default Object invoke(DynamicObject<?> self, FunctionType type, Object... args) {
            ArgumentList lis = ArgumentList.asWithType(type, args);
            Object r = invoke(self, lis);
            lis.recycle();
            return r;
        }
    }

    public interface ProxyHandler {
        Object invoke(DynamicObject<?> proxy, FuncMarker func, FuncMarker superFunction, ArgumentList args);
    }
}
