package dynamilize;

import dynamilize.DataPool.*;
import dynamilize.classmaker.Parameterf;
import dynamilize.classmaker.*;
import dynamilize.classmaker.code.*;
import dynamilize.classmaker.code.annotation.*;
import dynamilize.runtimeannos.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.regex.*;

import static dynamilize.classmaker.ClassInfo.*;
import static dynamilize.classmaker.CodeBlock.*;
import static dynamilize.runtimeannos.FuzzyMatch.Exact.*;

/**
 * The core factory type of dynamic type operation is used to construct dynamic delegate types and their instances by combining the passed dynamic types with delegate base classes.
 * <p>Defined a default generator implementation based on {@link ASMGenerator}.
 * <p>If a special implementation is required, it is necessary to rewrite/implement such methods, mainly through:
 * <ul>
 * <li><strong>{@link DynamicMaker#makeClassInfo(Class, Class[], Class[])}</strong>: Determine how this factory constructs the description information of the delegation class.
 * <li><strong>{@link DynamicMaker#generateClass(Class, Class[], Class[])}</strong>: Deciding how to parse descriptive information to generate bytecode and how to load bytecode into Java classes.
 * </ul>
 * When implementing, the basic implementation must be provided according to the method description, and the original minimum context request should not be changed.
 * <pre>
 * For usage instructions, please refer to:
 *   {@link DynamicClass}
 * The method of DynamicMaker:
 *   {@link DynamicMaker#newInstance(DynamicClass)}
 *   {@link DynamicMaker#newInstance(Class[], Class[], DynamicClass)}
 *   {@link DynamicMaker#newInstance(Class, Class[], DynamicClass, Object...)}
 *   {@link DynamicMaker#newInstance(Class, Class[], Class[], DynamicClass, Object...)}
 * </pre>
 * @see DynamicClass
 * @see AspectInterface
 * @see DynamicObject
 * @see DataPool
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class DynamicMaker {
    public static final String CALLSUPER = "$super";

    private static final HashSet<String> INTERNAL_FIELD = new HashSet<>(Arrays.asList(
            "$dynamic_type$",
            "$datapool$",
            "$varValuePool$",
            "$superbasepointer$"
    ));

    public static final ClassInfo<DynamicClass> DYNAMIC_CLASS_TYPE = asType(DynamicClass.class);
    public static final ClassInfo<DynamicObject> DYNAMIC_OBJECT_TYPE = asType(DynamicObject.class);
    public static final ClassInfo<DataPool> DATA_POOL_TYPE = asType(DataPool.class);
    public static final ClassInfo<FunctionType> FUNCTION_TYPE_TYPE = asType(FunctionType.class);
    public static final MethodInfo<FunctionType, FunctionType> TYPE_INST = FUNCTION_TYPE_TYPE.getMethod(
            FUNCTION_TYPE_TYPE,
            "inst",
            CLASS_TYPE.asArray());
    public static final ClassInfo<Function> FUNCTION_TYPE = asType(Function.class);
    public static final ClassInfo<DataPool.ReadOnlyPool> READONLY_POOL_TYPE = asType(DataPool.ReadOnlyPool.class);
    public static final ClassInfo<Integer> INTEGER_CLASS_TYPE = asType(Integer.class);
    public static final ClassInfo<HashMap> HASH_MAP_TYPE = asType(HashMap.class);
    public static final ClassInfo<Variablec> VAR_TYPE = ClassInfo.asType(Variablec.class);
    public static final ClassInfo<NoSuchMethodException> NOSUCH_METHOD = asType(NoSuchMethodException.class);
    public static final ClassInfo<ArgumentList> ARG_LIST_TYPE = asType(ArgumentList.class);
    public static final ClassInfo<Function.SuperGetFunction> SUPER_GET_FUNC_TYPE = ClassInfo.asType(Function.SuperGetFunction.class);
    public static final ClassInfo<FunctionEntryc> FUNC_ENTRY_TYPE = ClassInfo.asType(FunctionEntryc.class);

    public static final Methodc<DataPool, ReadOnlyPool> GET_READER = DATA_POOL_TYPE.getMethod(READONLY_POOL_TYPE, "getReader", DYNAMIC_OBJECT_TYPE);
    public static final Methodc<HashMap, Object> MAP_GET = HASH_MAP_TYPE.getMethod(OBJECT_TYPE, "get", OBJECT_TYPE);
    public static final Methodc<HashMap, Object> MAP_GET_DEF = HASH_MAP_TYPE.getMethod(OBJECT_TYPE, "getOrDefault", OBJECT_TYPE, OBJECT_TYPE);
    public static final Methodc<Integer, Integer> VALUE_OF = INTEGER_CLASS_TYPE.getMethod(INTEGER_CLASS_TYPE, "valueOf", INT_TYPE);
    public static final Methodc<HashMap, Object> MAP_PUT = HASH_MAP_TYPE.getMethod(OBJECT_TYPE, "put", OBJECT_TYPE, OBJECT_TYPE);
    public static final Methodc<DataPool, Variablec> GET_VAR = DATA_POOL_TYPE.getMethod(VAR_TYPE, "getVariable", STRING_TYPE);
    public static final Methodc<DataPool, Void> SET_VAR = DATA_POOL_TYPE.getMethod(ClassInfo.VOID_TYPE, "setVariable", VAR_TYPE);
    public static final Methodc<DataPool, Void> SETFUNC = DATA_POOL_TYPE.getMethod(ClassInfo.VOID_TYPE, "setFunction", STRING_TYPE, FUNCTION_TYPE, ClassInfo.CLASS_TYPE.asArray());
    public static final Methodc<DataPool, Void> SETFUNC2 = DATA_POOL_TYPE.getMethod(ClassInfo.VOID_TYPE, "setFunction", STRING_TYPE, SUPER_GET_FUNC_TYPE, ClassInfo.CLASS_TYPE.asArray());
    public static final Methodc<DataPool, FunctionEntryc> SELECT = DATA_POOL_TYPE.getMethod(FUNC_ENTRY_TYPE, "select", STRING_TYPE, FUNCTION_TYPE_TYPE);
    public static final Methodc<DataPool, Void> INIT = DATA_POOL_TYPE.getMethod(VOID_TYPE, "init", DYNAMIC_OBJECT_TYPE, OBJECT_TYPE.asArray());
    public static final Methodc<DynamicObject, Object> INVOKE = DYNAMIC_OBJECT_TYPE.getMethod(OBJECT_TYPE, "invokeFunc", FUNCTION_TYPE_TYPE, STRING_TYPE, OBJECT_TYPE.asArray());
    public static final Methodc<ArgumentList, Object[]> GET_LIST = ARG_LIST_TYPE.getMethod(OBJECT_TYPE.asArray(), "getList", INT_TYPE);
    public static final Methodc<ArgumentList, Void> RECYCLE_LIST = ARG_LIST_TYPE.getMethod(VOID_TYPE, "recycleList", OBJECT_TYPE.asArray());

    private static final Map<String, Set<FunctionType>> OVERRIDES = new HashMap<>();
    private static final Map<String, Set<FunctionType>> FINALS = new HashMap<>();
    private static final Map<String, Set<FunctionType>> ABSTRACTS = new HashMap<>();
    private static final Set<Class<?>> INTERFACE_TEMP = new HashSet<>();
    private static final Stack<Class<?>> INTERFACE_STACK = new Stack<>();
    private static final Class[] EMPTY_CLASSES = new Class[0];
    public static final Localc[] LOCALS_EMP = new Localc[0];
    public static final HashSet EMP_SET = new HashSet<>();
    public static final HashMap EMP_MAP = new HashMap<>();
    public static final String ANY = "ANY";
    private final JavaHandleHelper helper;

    private final HashMap<ClassImplements<?>, Class<?>> classPool = new HashMap<>();
    private final HashMap<Class<?>, DataPool> classPoolsMap = new HashMap<>();
    private final HashMap<Class<?>, HashMap<FunctionType, Constructor<?>>> constructors = new HashMap<>();

    private final HashMap<Class<?>, DataPool> wrapClassPoolMap = new HashMap<>();

    /**
     * Create an instance and pass in the {@linkplain JavaHandleHelper Java Behavior Supporter} it wants to use.
     * Subclass references this constructor, which may directly set the default behavior supporter without external input.
     */
    protected DynamicMaker(JavaHandleHelper helper) {
        this.helper = helper;
    }

    public void clearAllCache() {
        classPool.clear();
        classPoolsMap.clear();
        constructors.clear();
        wrapClassPoolMap.clear();
    }

    /** Wrap the incoming object as a {@link WrappedObject} */
    public <T> DynamicObject<T> wrapInstance(T object) {
        Class<?> curr = object.getClass();

        LinkedList<Class<?>> l = new LinkedList<>();
        while (curr != null) {
            if (curr.getAnnotation(DynamicType.class) != null)
                throw new IllegalHandleException("Cannot wrap a dynamic type instance");

            l.addFirst(curr);
            curr = curr.getSuperclass();
        }

        DataPool res = null;
        for (Class<?> clazz : l) {
            DataPool fr = res;
            res = wrapClassPoolMap.computeIfAbsent(clazz, c -> {
                DataPool r = new DataPool(fr);

                for (Method method : clazz.getDeclaredMethods()) {
                    if (Modifier.isStatic(method.getModifiers())) continue;

                    r.setFunction(helper.genJavaMethodRef(method));
                }

                for (Field field : clazz.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers())) continue;

                    r.setVariable(helper.genJavaVariableRef(field));
                }

                return r;
            });
        }

        return new WrappedObject<>(object, res);
    }

    /**
     * Construct a fully delegated dynamic instance derived from {@link Object}.
     *
     * @see DynamicMaker#newInstance(Class, Class[], Class[], DynamicClass, Object...)
     */
    public DynamicObject<Object> newInstance(DynamicClass dynamicClass) {
        return newInstance(Object.class, dynamicClass);
    }

    /**
     * Construct a fully delegated dynamic instance from the given parameter list.
     *
     * @see DynamicMaker#newInstance(Class, Class[], Class[], DynamicClass, Object...)
     */
    public DynamicObject<Object> newInstance(Class<?>[] interfaces, DynamicClass dynamicClass) {
        return newInstance(Object.class, interfaces, (Class<?>[]) null, dynamicClass);
    }

    /**
     * Construct dynamic instances using the provided interface list.
     * <p>The given {@linkplain AspectInterface} table will determine the dynamic behavior of this instance. If it is null (<strong>non-empty array, empty array indicates not executing delegation</strong>), it is a full delegation.
     *
     * @see DynamicMaker#newInstance(Class, Class[], Class[], DynamicClass, Object...)
     */
    public DynamicObject<Object> newInstance(Class<?>[] interfaces, Class<?>[] aspects, DynamicClass dynamicClass) {
        return newInstance(Object.class, interfaces, aspects, dynamicClass);
    }

    /**
     * Construct an instance of a fully delegated dynamic class using the given constructor parameters, and the parameter table must have a matching available constructor in the delegated Java type.
     * The instance has no additional interfaces, and the type delegation is determined by parameters.
     *
     * @see DynamicMaker#newInstance(Class, Class[], Class[], DynamicClass, Object...)
     */
    public <T> DynamicObject<T> newInstance(Class<T> base, DynamicClass dynamicClass, Object... args) {
        return newInstance(base, EMPTY_CLASSES, null, dynamicClass, args);
    }

    /**
     * Construct an instance of a dynamic class using the given constructor parameters, and the parameter table must have a matching available constructor in the delegated Java type.
     * The instance has no additional interfaces, and the type delegation is determined by parameters.
     * <p>The given {@linkplain AspectInterface} table will determine the dynamic behavior of this instance. If it is null (<strong>non-empty array, empty array indicates not executing delegation</strong>), it is a full delegation.
     *
     * @see DynamicMaker#newInstance(Class, Class[], Class[], DynamicClass, Object...)
     */
    public <T> DynamicObject<T> newInstance(Class<T> base, Class<?>[] aspects, DynamicClass dynamicClass, Object... args) {
        return newInstance(base, EMPTY_CLASSES, aspects, dynamicClass, args);
    }

    /**
     * Construct an instance of a dynamic class using the given constructor parameters, and the parameter table must have a matching available constructor in the delegated Java type.
     * The interface list provided by the instance implementation, with type delegation determined by parameters.
     * <p>The given {@linkplain AspectInterface} table will determine the dynamic behavior of this instance. If it is null (<strong>non-empty array, empty array indicates not executing delegation</strong>), it is a full delegation.
     * <p>See {@link AspectInterface} for details.
     *
     * <p><strong>Note that when you use the slicing interface to limit the scope of delegation, you must ensure that all abstract methods (from abstract class delegation and implemented interfaces) are within the slicing scope, otherwise an error will be thrown.</strong>
     *
     * @param base The Java type that executes the delegation will determine the types that this instance can be assigned to.
     * @param interfaces List of interfaces implemented by instances.
     * @param aspects List of sectional interfaces.
     * @param dynamicClass Dynamic types used for instantiation.
     * @param args Constructor parameter.
     * @return Constructed dynamic instances.
     * @throws IllegalHandleException If the constructor argument cannot match the corresponding constructor, or if there are abstract methods that have not been processed.
     * @see AspectInterface
     */
    public <T> DynamicObject<T> newInstance(Class<T> base, Class<?>[] interfaces, Class<?>[] aspects, DynamicClass dynamicClass, Object... args) {
        checkBase(base);

        Class<? extends T> clazz = getDynamicBase(base, interfaces, aspects);
        try {
            List<Object> argsLis = new ArrayList<>(Arrays.asList(
                    dynamicClass,
                    genPool(clazz, dynamicClass),
                    classPoolsMap.get(clazz)
            ));
            argsLis.addAll(Arrays.asList(args));

            Constructor<?> cstr = null;
            for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                FunctionType t;
                if ((t = FunctionType.from(constructor)).match(argsLis.toArray())) {
                    cstr = constructor;
                    break;
                }
                t.recycle();
            }
            if (cstr == null)
                throw new NoSuchMethodError("no matched constructor found with parameter " + Arrays.toString(args));

            FunctionType type = FunctionType.inst(cstr.getParameterTypes());
            Constructor c = cstr;
            DynamicObject<T> inst = (DynamicObject<T>) constructors.computeIfAbsent(clazz, e -> new HashMap<>())
                    .computeIfAbsent(type, t -> {
                        helper.makeAccess(c);
                        return c;
                    }).newInstance(argsLis.toArray());

            type.recycle();

            return inst;
        } catch (Throwable e) {
            throw new IllegalHandleException(e);
        }
    }

    /**
     * Get the {@linkplain JavaHandleHelper Java Behavior Supporter} for this maker.
     */
    public JavaHandleHelper getHelper() {
        return helper;
    }

    /**
     * Check the availability of the delegate base class. If the base class is final or an interface, throw an exception.
     */
    protected <T> void checkBase(Class<T> base) {
        if (base.isInterface())
            throw new IllegalHandleException("interface cannot use to derive a dynamic class, please write it in parameter \"interfaces\" to implement this interface");

        if (Modifier.isFinal(base.getModifiers()))
            throw new IllegalHandleException("cannot derive a dynamic class with a final class");
    }

    /**
     * Generate a data pool corresponding to the dynamic type, and the data pool completes the iterative allocation of data pool information to the superclass based on the dynamic type.
     * At the same time, generate function/variable entries for all methods and fields generated by the delegate's dynamic type and place them in the data pool.
     *
     * @param base Dynamic delegation class
     * @param dynamicClass Describe the dynamic types of behavior
     * @return Generated dynamic type data pool
     */
    protected <T> DataPool genPool(Class<? extends T> base, DynamicClass dynamicClass) {
        DataPool basePool = classPoolsMap.computeIfAbsent(base, clazz -> {
            AtomicBoolean immutable = new AtomicBoolean();
            DataPool res = new DataPool(null) {
                @Override
                public void setFunction(String name, Function<?, ?> function, Class<?>... argsType) {
                    if (immutable.get())
                        throw new IllegalHandleException("immutable pool");

                    super.setFunction(name, function, argsType);
                }

                @Override
                public void setVariable(Variablec var) {
                    if (immutable.get())
                        throw new IllegalHandleException("immutable pool");

                    super.setVariable(var);
                }
            };

            Class<?> curr = clazz;
            while (curr != null) {
                if (curr.getAnnotation(DynamicType.class) != null) {
                    for (Method method : curr.getDeclaredMethods()) {
                        DynamicMethod callSuper = method.getAnnotation(DynamicMethod.class);
                        if (callSuper != null) {
                            String signature = FunctionType.signature(method.getName(), method.getParameterTypes());
                            res.setFunction(
                                    method.getName(),
                                    (self, args) -> ((SuperInvoker) self).invokeSuper(signature, args.args()),
                                    method.getParameterTypes()
                            );
                        }
                    }
                    curr = curr.getSuperclass();
                    continue;
                }

                for (Field field : curr.getDeclaredFields()) {
                    if (Modifier.isStatic(field.getModifiers()) || isInternalField(field.getName())) continue;

                    res.setVariable(helper.genJavaVariableRef(field));
                }
                curr = curr.getSuperclass();
            }

            immutable.set(true);

            return res;
        });

        return dynamicClass.genPool(basePool);
    }

    private static boolean isInternalField(String name) {
        return INTERNAL_FIELD.contains(name);
    }

    /**
     * Obtain the type of the generated dynamic type instance based on the delegated base class and implemented interface.
     * The type will be generated and put into the pool, and the next retrieval will directly retrieve the type from the pool.
     *
     * @param base Delegate base class
     * @param interfaces List of interfaces that need to be implemented
     */
    protected <T> Class<? extends T> getDynamicBase(Class<T> base, Class<?>[] interfaces, Class<?>[] aspects) {
        return (Class<? extends T>) classPool.computeIfAbsent(new ClassImplements<>(base, interfaces, aspects), e -> {
            Class<?> c = base;

            while (c != null) {
                helper.makeAccess(c);
                c = c.getSuperclass();
            }

            return generateClass(handleBaseClass(base), interfaces, aspects);
        });
    }

    /**
     * Establish a dynamic class packaging name based on the base class and interface list,
     * with unique (or sufficiently discrete, avoiding frequent collisions) and invariant packaging names.
     *
     * @param baseClass Base class
     * @param interfaces interface list
     * @return A packaging name consisting of the hash values of the base class name and all interface names
     */
    public static <T> String getDynamicName(Class<T> baseClass, Class<?>... interfaces) {
        return ensurePackage(baseClass.getName()) + "$dynamic$" + FunctionType.typeNameHash(interfaces);
    }

    private static String ensurePackage(String name) {
        if (name.startsWith("java.")) {
            return name.replaceFirst("java\\.", "lava.");
        }
        return name;
    }

    /**
     * The method of continuing to delegate the creation of code for the types generated by dynamic delegation should pass the primary processing up to the superclass.
     * Similar to the behavior of {@link DynamicMaker#makeClassInfo(Class, Class[], Class[])}, but some of the behavior needs to be adapted to superclass that already has delegated behavior.
     */
    protected <T> ClassInfo<? extends T> makeClassInfoOnDynamic(Class<T> baseClass, Class<?>[] interfaces, Class<?>[] aspects) {
        LinkedHashSet<ClassInfo<?>> inter = new LinkedHashSet<>();
        inter.add(asType(DynamicObject.class));
        inter.add(asType(SuperInvoker.class));

        for (Class<?> i : interfaces) {
            inter.add(asType(i));
        }

        INTERFACE_STACK.clear();
        INTERFACE_TEMP.clear();
        HashMap<String, HashMap<FunctionType, FuzzyMatcher>> aspectPoints = new HashMap<>();
        if (aspects != null) {
            for (Class<?> i : aspects) {
                inter.add(asType(i));
                INTERFACE_STACK.push(i);
            }

            while (!INTERFACE_STACK.isEmpty()) {
                Class<?> i = INTERFACE_STACK.pop();
                if (i.getAnnotation(AspectInterface.class) == null)
                    throw new IllegalHandleException("aspect interfaces must has AspectInterface annotated, but " + i + " doesn't");

                if (INTERFACE_TEMP.add(i)) {
                    for (Class<?> ai : i.getInterfaces()) {
                        INTERFACE_STACK.push(ai);
                    }

                    for (Method method : i.getMethods()) {
                        if (method.isDefault())
                            throw new IllegalHandleException("aspect interface must be full-abstract, but method " + method + " in " + i + " was implemented");

                        aspectPoints.computeIfAbsent(method.getName(), e -> new HashMap())
                                .computeIfAbsent(FunctionType.from(method), e -> {
                                    FuzzyMatch match = method.getAnnotation(FuzzyMatch.class);
                                    Parameter[] parameters = method.getParameters();
                                    Annotation[] argMatchers = new Annotation[parameters.length];

                                    if (match != null) {
                                        for (int l = 0; l < parameters.length; l++) {
                                            Annotation[] alternatives = parameters[l].getAnnotations();
                                            for (Annotation ann : alternatives) {
                                                if (ann instanceof FuzzyMatch.AnyType || ann instanceof FuzzyMatch.TypeAssignable || ann instanceof FuzzyMatch.Exact) {
                                                    if (argMatchers[l] != null)
                                                        throw new IllegalHandleException("cannot declare parameter matcher twice on a parameter");

                                                    argMatchers[l] = ann;
                                                }
                                                else argMatchers[l] = INSTANCE;
                                            }
                                        }
                                    }

                                    return new FuzzyMatcher(parameters, match, argMatchers);
                                });
                    }
                }
            }
        } else {
            aspectPoints.put(ANY, null);
        }

        ClassInfo<? extends T> classInfo = new ClassInfo<>(
                Modifier.PUBLIC,
                ensurePackage(baseClass.getName()) + "$" + FunctionType.typeNameHash(interfaces),
                asType(baseClass),
                inter.toArray(new ClassInfo[0])
        );
        FieldInfo<HashMap> methodIndex = classInfo.declareField(
                Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL,
                "$methodIndex$",
                HASH_MAP_TYPE,
                null
        );

        CodeBlock<Void> clinit = classInfo.getClinitBlock();
        Localc<HashMap> caseIndex = clinit.local(HASH_MAP_TYPE);
        clinit.newInstance(
                asType(HashMap.class).getConstructor(),
                caseIndex
        );
        clinit.assign(null, caseIndex, methodIndex);

        HashMap<Methodc<?, ?>, Integer> callSuperCaseMap = new HashMap<>();

        // public <init>(*parameters*) {
        //     super(*parameters*);
        // }
        for (Constructor<?> cstr : baseClass.getDeclaredConstructors()) {
            if ((cstr.getModifiers() & (Modifier.PUBLIC | Modifier.PROTECTED)) == 0) continue;
            if (Modifier.isFinal(cstr.getModifiers())) continue;

            ArrayList<Classc<?>> args = new ArrayList<>();
            for (Class<?> type : cstr.getParameterTypes()) {
                args.add(ClassInfo.asType(type));
            }
            Classc<?>[] argArr = args.toArray(new Classc[0]);
            Methodc<?, Void> constructor = classInfo.superClass().getConstructor(argArr);

            CodeBlock<Void> code = classInfo.declareConstructor(Modifier.PUBLIC, Parameterf.trans(argArr));
            code.invokeSuper(code.getThis(), constructor, null, code.getParamList().toArray(new Localc<?>[0]));
        }

        OVERRIDES.clear();
        FINALS.clear();

        //Exclude methods that have been marked as final, as the type being delegated does not iterate upwards, exclude final methods in advance.
        Class<?> curr = baseClass;
        while (curr != null) {
            for (Method method : curr.getDeclaredMethods()) {
                filterMethod(method);
            }

            curr = curr.getSuperclass();
        }

        OVERRIDES.clear();

        //Only handle newly implemented interfaces and sectional interfaces.
        ArrayList<Class<?>> lis = new ArrayList<>(Arrays.asList(interfaces));
        if (aspects != null) lis.addAll(Arrays.asList(aspects));

        for (Class<?> interf : lis) {
            ClassInfo<?> typeClass = asType(interf);
            for (Method method : interf.getDeclaredMethods()) {
                if (!filterMethod(method)) continue;

                String methodName = method.getName();
                ClassInfo<?> returnType = asType(method.getReturnType());

                MethodInfo<?, ?> superMethod = !Modifier.isAbstract(method.getModifiers()) || (interf.isInterface() && method.isDefault()) ? typeClass.getMethod(
                        returnType,
                        methodName,
                        Arrays.stream(method.getParameterTypes()).map(ClassInfo::asType).toArray(ClassInfo[]::new)
                ) : null;

                if (!aspectPoints.containsKey(ANY) && !aspectPoints.getOrDefault(method.getName(), EMP_MAP).containsKey(FunctionType.from(method))
                        && aspectPoints.getOrDefault(method.getName(), ((HashMap<FunctionType, FuzzyMatcher>) EMP_MAP)).values().stream().noneMatch(e -> e.match(method,
                        INTERFACE_TEMP.contains(interf),
                        superMethod == null))
                ) {
                    if (superMethod == null)
                        throw new IllegalHandleException("method " + method + " in " + interf + " was abstract, but no aspects handle this action");

                    continue;
                }

                if (superMethod != null) callSuperCaseMap.put(superMethod, callSuperCaseMap.size());

                String typeF = methodName + "$" + FunctionType.typeNameHash(method.getParameterTypes());
                FieldInfo<FunctionType> funType = classInfo.declareField(
                        Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL,
                        typeF,
                        FUNCTION_TYPE_TYPE,
                        null
                );

                // private static final FunctionType FUNCTION_TYPE$*name*;
                // static {
                //     ...
                //     FUNCTION_TYPE$*signature* = FunctionType.as(*paramTypes*);
                //     methodIndex.put(*signature*, *index*);
                //     ...
                // }
                Integer index = callSuperCaseMap.get(superMethod);
                genCinit(method, clinit, funType, methodIndex, index == null? -1: index);

                // @DynamicMethod
                // public *returnType* *name*(*parameters*) {
                //     *[return]* this.invokeFunc(FUNCTION_TYPE$*signature* ,"*name*", parameters);
                // }
                invokeProxy(classInfo, method, methodName, returnType, funType);
            }
        }

        //switch super
        // public Object invokeSuper(String signature, Object... args);{
        //     Integer ind = methodIndex.get(signature);
        //     if (ind == null) return super.invokeSuper(signature, args)
        //     switch (ind) {
        //     ...
        //         case *index*: super.*method*(args[0], args[1],...); break;
        //     ...
        //     }
        // }
        {
            CodeBlock<Object> code = classInfo.declareMethod(
                    Modifier.PUBLIC,
                    "invokeSuper",
                    OBJECT_TYPE,
                    new ClassInfo[]{
                            ClassInfo.asType(NoSuchMethodException.class)
                    },
                    Parameterf.trans(
                            STRING_TYPE,
                            OBJECT_TYPE.asArray()
                    )
            );

            Methodc<T, Object> superCaller = (Methodc<T, Object>) classInfo.superClass().getMethod(code.owner().returnType(), code.owner().name(),
                    code.owner().parameters().stream().map(Parameterf::getType).toArray(Classc[]::new));

            Label end = code.label();
            code.assign(null, methodIndex, stack(HASH_MAP_TYPE));
            code.assign(code.getRealParam(0), stack(OBJECT_TYPE));
            code.loadConstant(stack(INT_TYPE), -1);
            code.invokeStatic(VALUE_OF, stack(INTEGER_CLASS_TYPE), stack(INT_TYPE));

            code.invoke(
                    stack(HASH_MAP_TYPE),
                    MAP_GET_DEF,
                    stack(OBJECT_TYPE),
                    stack(OBJECT_TYPE)
            );

            code.cast(stack(OBJECT_TYPE), stack(INTEGER_CLASS_TYPE));
            code.invoke(
                    stack(INTEGER_CLASS_TYPE),
                    INTEGER_CLASS_TYPE.getMethod(INT_TYPE, "intValue"),
                    stack(INT_TYPE)
            );

            Switchc<Integer> switchc = code.switchDef(stack(INT_TYPE), end);

            Localc<Object[]> args = code.getRealParam(1);
            makeSwitch(classInfo, callSuperCaseMap, code, switchc, args);

            code.markLabel(end);
            code.assign(code.getThis(), stack(classInfo));

            code.invokeSuper(stack(classInfo), superCaller, stack(superCaller.returnType()), code.getParamList().toArray(new Localc[0]));
            code.returnValue(stack(OBJECT_TYPE));
        }

        return classInfo;
    }

    /**
     * Create a type identifier for dynamic instance types, which should cover all methods of the delegate target class and methods in the implemented interfaces.
     * If a member method of the superclass is not abstract, the entry to the superclass method should be retained,
     * Rewrite this method again to provide certain identification for the entry of superclass methods for generating reference functions for base class data pools.
     * <p>For a given list of base classes and interfaces, the names of the generated dynamic instance base classes are unique (or sufficiently discrete to make collisions almost impossible).
     * <p>For the work required to implement this method, please refer to the description in {@link DynamicObject}.
     *
     * @param baseClass Delegate base class
     * @param interfaces List of implemented interfaces
     * @return Completed all necessary type identification descriptions
     */
    protected <T> ClassInfo<? extends T> makeClassInfo(Class<T> baseClass, Class<?>[] interfaces, Class<?>[] aspects) {
        if (baseClass.getAnnotation(DynamicType.class) != null)
            return makeClassInfoOnDynamic(baseClass, interfaces, aspects);

        LinkedHashSet<ClassInfo<?>> inter = new LinkedHashSet<>();
        inter.add(asType(DynamicObject.class));
        inter.add(asType(SuperInvoker.class));

        for (Class<?> i : interfaces) {
            inter.add(asType(i));
        }

        INTERFACE_STACK.clear();
        INTERFACE_TEMP.clear();
        HashMap<String, HashMap<FunctionType, FuzzyMatcher>> aspectPoints = new HashMap<>();
        if (aspects != null) {
            for (Class<?> i : aspects) {
                inter.add(asType(i));
                INTERFACE_STACK.push(i);
            }

            while (!INTERFACE_STACK.isEmpty()) {
                Class<?> i = INTERFACE_STACK.pop();
                if (!i.isInterface())
                    throw new IllegalHandleException("aspects must be interface, but find class: " + i);

                if (i.getAnnotation(AspectInterface.class) == null)
                    throw new IllegalHandleException("aspect interfaces must has AspectInterface annotated, but " + i + " doesn't");

                if (INTERFACE_TEMP.add(i)) {
                    for (Class<?> ai : i.getInterfaces()) {
                        INTERFACE_STACK.push(ai);
                    }

                    for (Method method : i.getMethods()) {
                        if (method.isDefault())
                            throw new IllegalHandleException("aspect interface must be full-abstract, but method " + method + " in " + i + " was implemented");

                        aspectPoints.computeIfAbsent(method.getName(), e -> new HashMap())
                                .computeIfAbsent(FunctionType.from(method), e -> {
                                    FuzzyMatch match = method.getAnnotation(FuzzyMatch.class);
                                    Parameter[] parameters = method.getParameters();
                                    Annotation[] argMatchers = new Annotation[parameters.length];

                                    if (match != null) {
                                        for (int l = 0; l < parameters.length; l++) {
                                            Annotation[] alternatives = parameters[l].getAnnotations();
                                            for (Annotation ann : alternatives) {
                                                if (ann instanceof FuzzyMatch.AnyType || ann instanceof FuzzyMatch.TypeAssignable || ann instanceof FuzzyMatch.Exact) {
                                                    if (argMatchers[l] != null)
                                                        throw new IllegalHandleException("cannot declare parameter matcher twice on a parameter");

                                                    argMatchers[l] = ann;
                                                } else argMatchers[l] = INSTANCE;
                                            }
                                        }
                                    }

                                    return new FuzzyMatcher(parameters, match, argMatchers);
                                });
                    }
                }
            }
        } else {
            aspectPoints.put(ANY, null);
        }

        ClassInfo<? extends T> classInfo = new ClassInfo<>(
                Modifier.PUBLIC,
                getDynamicName(baseClass, interfaces),
                asType(baseClass),
                inter.toArray(new ClassInfo[0])
        );

        FieldInfo<DynamicClass> dyType = classInfo.declareField(
                Modifier.PRIVATE | Modifier.FINAL,
                "$dynamic_type$",
                DYNAMIC_CLASS_TYPE,
                null
        );
        FieldInfo<DataPool> dataPool = classInfo.declareField(
                Modifier.PRIVATE | Modifier.FINAL,
                "$datapool$",
                DATA_POOL_TYPE,
                null
        );
        FieldInfo<HashMap<String, Object>> varPool = (FieldInfo) classInfo.declareField(
                Modifier.PRIVATE | Modifier.FINAL,
                "$varValuePool$",
                HASH_MAP_TYPE,
                null
        );
        FieldInfo<DataPool.ReadOnlyPool> basePoolPointer = classInfo.declareField(
                Modifier.PRIVATE | Modifier.FINAL,
                "$superbasepointer$",
                READONLY_POOL_TYPE,
                null
        );
        FieldInfo<HashMap> methodIndex = classInfo.declareField(
                Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL,
                "$methodIndex$",
                HASH_MAP_TYPE,
                null
        );

        CodeBlock<Void> clinit = classInfo.getClinitBlock();
        Localc<HashMap> caseIndex = clinit.local(HASH_MAP_TYPE);
        clinit.newInstance(
                asType(HashMap.class).getConstructor(),
                caseIndex
        );
        clinit.assign(null, caseIndex, methodIndex);

        HashMap<Methodc<?, ?>, Integer> callSuperCaseMap = new HashMap<>();

        // public <init>(DynamicClass $dyC$, DataPool $datP$, DataPool.ReadOnlyPool $basePool$, *parameters*) {
        //     this.$dynamic_type$ = $dyC$;
        //     this.$datapool$ = $datP$;
        //     this.$varValuePool$ = new HashMap<>();
        //     super(*parameters*);
        //     this.$superbasepointer$ = $datapool$.getReader(this);
        //
        //     this.$datapool$.init(this, *parameters*);
        // }
        for (Constructor<?> cstr : baseClass.getDeclaredConstructors()) {
            if ((cstr.getModifiers() & (Modifier.PUBLIC | Modifier.PROTECTED)) == 0) continue;
            if (Modifier.isFinal(cstr.getModifiers())) continue;

            List<Parameterf<?>> params = new ArrayList<>(Arrays.asList(Parameterf.as(
                    0, DynamicClass.class, "$dyc$",
                    0, DataPool.class, "$datP$",
                    0, DataPool.class, "$basePool$"
            )));
            List<Parameterf<?>> superParams = Arrays.asList(Parameterf.asParameter(cstr.getParameters()));
            params.addAll(superParams);

            Methodc<?, Void> constructor = classInfo.superClass().getConstructor(superParams.stream().map(Parameterf::getType).toArray(Classc[]::new));

            CodeBlock<Void> code = classInfo.declareConstructor(Modifier.PUBLIC, params.toArray(new Parameterf[0]));
            List<Localc<?>> l = code.getParamList();
            Localc<T> self = code.getThis();

            Localc<DynamicClass> dyC = code.getParam(1);
            Localc<DataPool> datP = code.getParam(2);
            code.assign(self, dyC, dyType);
            code.assign(self, datP, dataPool);

            code.assign(self, stack(classInfo));
            code.newInstance(HASH_MAP_TYPE.getConstructor(), stack(HASH_MAP_TYPE));
            code.assign(stack(classInfo), stack(HASH_MAP_TYPE), varPool);

            code.invokeSuper(self, constructor, null, l.subList(3, l.size()).toArray(LOCALS_EMP));

            code.assign(self, stack(classInfo));
            code.invoke(code.getParam(3), GET_READER, stack(READONLY_POOL_TYPE), self);
            code.assign(stack(classInfo), stack(READONLY_POOL_TYPE), basePoolPointer);

            Localc<Object[]> argList = code.local(OBJECT_TYPE.asArray());
            code.loadConstant(stack(INT_TYPE), cstr.getParameterCount());
            code.invoke(null, GET_LIST, argList, stack(INT_TYPE));
            if (cstr.getParameterCount() > 0) {
                for (int i = 3; i < code.getParamList().size(); i++) {
                    code.assign(argList, stack(OBJECT_TYPE.asArray()));
                    code.loadConstant(stack(INT_TYPE), i - 3);
                    code.arrayPut(stack(OBJECT_TYPE.asArray()), stack(INT_TYPE), code.getRealParam(i));
                }
            }
            code.invoke(datP, INIT, null, self, argList);

            code.invoke(null, RECYCLE_LIST, null, argList);
        }

        OVERRIDES.clear();
        FINALS.clear();
        INTERFACE_TEMP.clear();

        ArrayList<Class<?>> lis = new ArrayList<>(Arrays.asList(interfaces));
        if (aspects != null) lis.addAll(Arrays.asList(aspects));

        for (Class<?> ic : lis) {
            INTERFACE_STACK.push(ic);
            INTERFACE_TEMP.add(ic);
        }

        Class<?> curr = baseClass;
        while (curr != null || !INTERFACE_STACK.empty()) {
            if (curr != null) {
                for (Class<?> i : curr.getInterfaces()) {
                    if (INTERFACE_TEMP.add(i)) INTERFACE_STACK.push(i);
                }
            } else curr = INTERFACE_STACK.pop();

            ClassInfo<?> typeClass = asType(curr);
            for (Method method : curr.getDeclaredMethods()) {
                if (!filterMethod(method)) continue;

                String methodName = method.getName();
                ClassInfo<?> returnType = asType(method.getReturnType());

                MethodInfo<?, ?> superMethod = !Modifier.isAbstract(method.getModifiers()) || (curr.isInterface() && method.isDefault()) ? typeClass.getMethod(
                        returnType,
                        methodName,
                        Arrays.stream(method.getParameterTypes()).map(ClassInfo::asType).toArray(ClassInfo[]::new)
                ) : null;

                Class<?> finalCurr = curr;
                if (!aspectPoints.containsKey(ANY) && !aspectPoints.getOrDefault(method.getName(), EMP_MAP).containsKey(FunctionType.from(method))
                        && aspectPoints.getOrDefault(method.getName(), ((HashMap<FunctionType, FuzzyMatcher>) EMP_MAP)).values().stream().noneMatch(e -> e.match(method,
                        finalCurr == baseClass || INTERFACE_TEMP.contains(finalCurr),
                        superMethod == null
                ))) {
                    if (superMethod == null)
                        throw new IllegalHandleException("method " + method + " in " + curr + " was abstract, but no aspects handle this action");

                    continue;
                }

                if (superMethod != null) callSuperCaseMap.put(superMethod, callSuperCaseMap.size());

                String typeF = methodName + "$" + FunctionType.typeNameHash(method.getParameterTypes());
                FieldInfo<FunctionType> funType = classInfo.declareField(
                        Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL,
                        typeF,
                        FUNCTION_TYPE_TYPE,
                        null
                );

                // private static final FunctionType FUNCTION_TYPE$*name*;
                // static {
                //     ...
                //     FUNCTION_TYPE$*signature* = FunctionType.as(*paramTypes*);
                //     methodIndex.put(*signature*, *index*);
                //     ...
                // }
                Integer index = callSuperCaseMap.get(superMethod);
                genCinit(method, clinit, funType, methodIndex, index == null? -1: index);

                // @DynamicMethod
                // public *returnType* *name*(*parameters*) {
                //     *[return]* this.invokeFunc(FUNCTION_TYPE$*signature* ,"*name*", parameters);
                // }
                invokeProxy(classInfo, method, methodName, returnType, funType);
            }

            if (!curr.isInterface()) {
                curr = curr.getSuperclass();
            } else curr = null;
        }

        // public Object invokeSuper(String signature, Object... args) {
        //     return switch(methodIndex.get(signature)) {
        //         ...
        //         case *index* -> super.*method*(args[0], args[1],...);
        //         ...
        //     }
        // }
        genCallSuper(classInfo, methodIndex, callSuperCaseMap);

        // public DataPool.ReadOnlyPool baseSuperPool() {
        //     return this.$superbasepointer$;
        // }
        {
            CodeBlock<DataPool.ReadOnlyPool> code = classInfo.declareMethod(
                    Modifier.PUBLIC,
                    "baseSuperPointer",
                    READONLY_POOL_TYPE
            );
            code.assign(code.getThis(), basePoolPointer, stack(READONLY_POOL_TYPE));
            code.returnValue(stack(READONLY_POOL_TYPE));
        }

        // public DynamicClass<Self> getDyClass() {
        //     return this.$dynamic_type$;
        // }
        {
            CodeBlock<DynamicClass> code = classInfo.declareMethod(
                    Modifier.PUBLIC,
                    "getDyClass",
                    DYNAMIC_CLASS_TYPE
            );
            code.assign(code.getThis(), dyType, stack(DYNAMIC_CLASS_TYPE));
            code.returnValue(stack(DYNAMIC_CLASS_TYPE));
        }

        // public <T> T varValueGet(String name) {
        //     return this.$varValuePool$.get(name);
        // }
        {
            CodeBlock<Object> code = classInfo.declareMethod(
                    Modifier.PUBLIC,
                    "varValueGet",
                    OBJECT_TYPE,
                    Parameterf.trans(STRING_TYPE)
            );
            code.assign(code.getThis(), varPool, stack(HASH_MAP_TYPE));
            code.invoke(stack(HASH_MAP_TYPE), MAP_GET, stack(OBJECT_TYPE), code.getRealParam(0));
            code.returnValue(stack(OBJECT_TYPE));
        }

        // public <T> varValueSet(String name, Object value) {
        //     return this.$varValuePool$.put(name, value);
        // }
        {
            CodeBlock<Void> code = classInfo.declareMethod(
                    Modifier.PUBLIC,
                    "varValueSet",
                    VOID_TYPE,
                    Parameterf.trans(
                            STRING_TYPE,
                            OBJECT_TYPE
                    )
            );
            code.assign(code.getThis(), varPool, stack(HASH_MAP_TYPE));
            code.invoke(stack(HASH_MAP_TYPE), MAP_PUT, null, code.getRealParam(0), code.getRealParam(1));
        }

        // public Variablec getVariable(String name) {
        //     return this.$datapool$.getVariable(name);
        // }
        {
            CodeBlock<Variablec> code = classInfo.declareMethod(
                    Modifier.PUBLIC,
                    "getVariable",
                    VAR_TYPE,
                    Parameterf.as(0, STRING_TYPE, "name")
            );
            code.assign(code.getThis(), dataPool, stack(DATA_POOL_TYPE));
            code.invoke(stack(DATA_POOL_TYPE), GET_VAR, stack(VAR_TYPE), code.getParam(1));
            code.returnValue(stack(VAR_TYPE));
        }

        // public <T> void setVariable(Variablec var) {
        //     this.$datapool$.setVariable(var);
        // }
        {
            CodeBlock<Void> code = classInfo.declareMethod(
                    Modifier.PUBLIC,
                    "setVariable",
                    VOID_TYPE,
                    Parameterf.as(0, VAR_TYPE, "var")
            );
            code.assign(code.getThis(), dataPool, stack(DATA_POOL_TYPE));
            code.invoke(stack(DATA_POOL_TYPE), SET_VAR, null, code.getParam(1));
            code.returnVoid();
        }

        // public FunctionEntryc getFunc(String name, FunctionType type) {
        //     return this.$datapool$.select(name, type);
        // }
        {
            CodeBlock<FunctionEntryc> code = classInfo.declareMethod(
                    Modifier.PUBLIC,
                    "getFunc",
                    FUNC_ENTRY_TYPE,
                    Parameterf.as(
                            0, STRING_TYPE, "name",
                            0, FUNCTION_TYPE_TYPE, "type"
                    )
            );
            code.assign(code.getThis(), dataPool, stack(DATA_POOL_TYPE));

            code.invoke(stack(DATA_POOL_TYPE), SELECT, stack(FUNC_ENTRY_TYPE), code.getParam(1), code.getParam(2));
            code.returnValue(stack(FUNC_ENTRY_TYPE));
        }

        // public <R> void setFunc(String name, Function<Self, R> func, Class<?>... argTypes) {
        //     this.$datapool$.set(name, func, argTypes);
        // }
        {
            CodeBlock<Void> code = classInfo.declareMethod(
                    Modifier.PUBLIC,
                    "setFunc",
                    VOID_TYPE,
                    Parameterf.as(
                            0, STRING_TYPE, "name",
                            0, FUNCTION_TYPE, "func",
                            0, CLASS_TYPE.asArray(), "argTypes"
                    )
            );

            code.assign(code.getThis(), dataPool, stack(DATA_POOL_TYPE));
            code.invoke(stack(DATA_POOL_TYPE), SETFUNC, null, code.getParam(1), code.getParam(2), code.getParam(3));
        }

        // public <R> void setFunc(String name, Function<Self, R> func, Class<?>... argTypes) {
        //     this.$datapool$.set(name, func, argTypes);
        // }
        {
            CodeBlock<Void> code = classInfo.declareMethod(
                    Modifier.PUBLIC,
                    "setFunc",
                    VOID_TYPE,
                    Parameterf.as(
                            0, STRING_TYPE, "name",
                            0, SUPER_GET_FUNC_TYPE, "func",
                            0, CLASS_TYPE.asArray(), "argTypes"
                    )
            );

            Localc<DataPool> pool = code.local(DATA_POOL_TYPE);
            code.assign(code.getThis(), dataPool, pool);

            code.invoke(pool, SETFUNC2, null, code.getParam(1), code.getParam(2), code.getParam(3));
        }

        AnnotationType<DynamicType> dycAnno = AnnotationType.asAnnotationType(DynamicType.class);
        dycAnno.annotateTo(classInfo, null);

        return classInfo;
    }

    private <T> void genCallSuper(ClassInfo<? extends T> classInfo, FieldInfo<HashMap> methodIndex, HashMap<Methodc<?, ?>, Integer> callSuperCaseMap) {
        CodeBlock<Object> code = classInfo.declareMethod(
                Modifier.PUBLIC,
                "invokeSuper",
                OBJECT_TYPE,
                new ClassInfo[]{
                        ClassInfo.asType(NoSuchMethodException.class)
                },
                Parameterf.trans(
                        STRING_TYPE,
                        OBJECT_TYPE.asArray()
                )
        );

        Label end = code.label();
        code.assign(null, methodIndex, stack(HASH_MAP_TYPE));
        code.assign(code.getRealParam(0), stack(OBJECT_TYPE));
        code.loadConstant(stack(INT_TYPE), -1);
        code.invokeStatic(VALUE_OF, stack(INTEGER_CLASS_TYPE), stack(INT_TYPE));

        code.invoke(
                stack(HASH_MAP_TYPE),
                MAP_GET_DEF,
                stack(OBJECT_TYPE),
                stack(OBJECT_TYPE)
        );

        code.cast(stack(OBJECT_TYPE), stack(INTEGER_CLASS_TYPE));
        code.invoke(
                stack(INTEGER_CLASS_TYPE),
                INTEGER_CLASS_TYPE.getMethod(INT_TYPE, "intValue"),
                stack(INT_TYPE)
        );

        Switchc<Integer> switchc = code.switchDef(stack(INT_TYPE), end);

        Localc<Object[]> args = code.getRealParam(1);
        makeSwitch(classInfo, callSuperCaseMap, code, switchc, args);

        code.markLabel(end);

        Localc<String> msg = code.local(STRING_TYPE);
        code.loadConstant(stack(STRING_TYPE), "no such method in baseclass signature with ");
        code.operate(stack(STRING_TYPE), Operatec.OPCode.ADD, code.getRealParam(0), msg);
        code.newInstance(
                NOSUCH_METHOD.getConstructor(STRING_TYPE),
                stack(NOSUCH_METHOD),
                msg
        );
        code.thr(stack(NOSUCH_METHOD));
    }

    private static void genCinit(Method method, CodeBlock<Void> clinit, FieldInfo<FunctionType> funType, FieldInfo<HashMap> methodIndex, int callSuperIndex) {
        String signature = FunctionType.signature(method);
        clinit.loadConstant(stack(INT_TYPE), method.getParameterCount());
        clinit.newArray(
                CLASS_TYPE,
                stack(CLASS_TYPE.asArray()),
                stack(INT_TYPE)
        );

        Class<?>[] paramTypes = method.getParameterTypes();
        for (int i = 0; i < paramTypes.length; i++) {
            clinit.assign(stack(CLASS_TYPE.asArray()), stack(CLASS_TYPE.asArray()));
            clinit.loadConstant(stack(INT_TYPE), i);
            clinit.loadConstant(stack(CLASS_TYPE), paramTypes[i]);

            clinit.arrayPut(stack(CLASS_TYPE.asArray()), stack(INT_TYPE), stack(CLASS_TYPE));
        }

        clinit.invoke(
                null,
                TYPE_INST,
                stack(FUNCTION_TYPE_TYPE),
                stack(CLASS_TYPE.asArray())
        );

        clinit.assign(null, stack(FUNCTION_TYPE_TYPE), funType);

        if (callSuperIndex == -1) return;

        clinit.assign(null, methodIndex, stack(HASH_MAP_TYPE));
        clinit.loadConstant(stack(STRING_TYPE), signature);

        clinit.loadConstant(stack(INT_TYPE), callSuperIndex);
        clinit.invoke(
                null,
                VALUE_OF,
                stack(INTEGER_CLASS_TYPE),
                stack(INT_TYPE)
        );

        clinit.invoke(
                stack(HASH_MAP_TYPE),
                MAP_PUT,
                null,
                stack(OBJECT_TYPE)
        );
    }

    private static <T> void invokeProxy(ClassInfo<? extends T> classInfo, Method method, String methodName, ClassInfo<?> returnType, FieldInfo<FunctionType> funType) {
        CodeBlock<?> code = classInfo.declareMethod(
                Modifier.PUBLIC,
                methodName,
                returnType,
                Parameterf.asParameter(method.getParameters())
        );
        AnnotationDef<DynamicMethod> anno = new AnnotationDef<>(
                ClassInfo.asType(DynamicMethod.class).asAnnotation(EMP_MAP),
                code.owner(),
                EMP_MAP
        );
        code.owner().addAnnotation(anno);

        code.loadConstant(stack(INT_TYPE), method.getParameterCount());
        code.invoke(null, GET_LIST, stack(OBJECT_TYPE.asArray()), stack(INT_TYPE));

        if (method.getParameterCount() > 0) {
            for (int i = 0; i < code.getParamList().size(); i++) {
                code.assign(stack(OBJECT_TYPE), stack(OBJECT_TYPE.asArray()));
                code.loadConstant(stack(INT_TYPE), i);
                code.arrayPut(stack(OBJECT_TYPE.asArray()), stack(INT_TYPE), code.getRealParam(i));
            }
        }

        Localc<Object[]> argList = code.local(OBJECT_TYPE.asArray());
        code.assign(stack(OBJECT_TYPE.asArray()), argList);

        if (returnType != VOID_TYPE) {
            code.assign(code.getThis(), stack(classInfo));
            code.assign(null, funType, stack(FUNCTION_TYPE_TYPE));
            code.loadConstant(stack(STRING_TYPE), method.getName());
            code.assign(argList, stack(OBJECT_TYPE.asArray()));

            code.invoke(stack(classInfo), INVOKE, stack(OBJECT_TYPE), stack(OBJECT_TYPE));
            code.invoke(null, RECYCLE_LIST, null, argList);
            code.cast(stack(OBJECT_TYPE), stack(returnType));
            code.returnValue(stack((Classc) returnType));
        } else {
            code.assign(code.getThis(), stack(classInfo));
            code.assign(null, funType, stack(FUNCTION_TYPE_TYPE));
            code.loadConstant(stack(STRING_TYPE), method.getName());
            code.assign(argList, stack(OBJECT_TYPE.asArray()));

            code.invoke(stack(classInfo), INVOKE, null, stack(OBJECT_TYPE));
            code.invoke(null, RECYCLE_LIST, null, argList);
        }
    }

    private static boolean filterMethod(Method method) {
        //对于已经被声明为final的方法将被添加到排除列表
        if (Modifier.isFinal(method.getModifiers())) {
            DynamicMaker.FINALS.computeIfAbsent(method.getName(), e -> new HashSet<>()).add(FunctionType.from(method));
            return false;
        }

        //If the method is static or not visible to subclasses, do not override this method.
        if (Modifier.isStatic(method.getModifiers())) return false;
        if ((method.getModifiers() & (Modifier.PUBLIC | Modifier.PROTECTED)) == 0) return false;

        return !DynamicMaker.FINALS.computeIfAbsent(method.getName(), e -> new HashSet<>()).contains(FunctionType.from(method))
                && DynamicMaker.OVERRIDES.computeIfAbsent(method.getName(), e -> new HashSet<>()).add(FunctionType.from(method));
    }

    protected void makeSwitch(Classc<?> owner, HashMap<Methodc<?, ?>, Integer> callSuperCaseMap, CodeBlock<Object> code, Switchc<Integer> switchc, Localc<Object[]> args) {
        for (Map.Entry<Methodc<?, ?>, Integer> entry : callSuperCaseMap.entrySet()) {
            Label l = code.label();
            code.markLabel(l);

            switchc.addCase(entry.getValue(), l);

            Methodc<?, ?> superMethod = entry.getKey();
            if (Modifier.isInterface(superMethod.owner().modifiers())) {
                Classc<?> c = code.owner().owner().superClass();
                boolean found = false;
                t:
                while (c != null && c != OBJECT_TYPE) {
                    for (Classc<?> interf : c.interfaces()) {
                        if (interf == superMethod.owner()) {
                            found = true;
                            c = interf;
                            break t;
                        }
                    }

                    c = c.superClass();
                }

                if (found) {
                    c.getMethod(
                            superMethod.returnType(),
                            superMethod.name(),
                            superMethod.parameters().stream().map(Parameterf::getType).toArray(Classc[]::new)
                    );
                }
            }

            code.assign(code.getThis(), stack(owner));
            for (int in = 0; in < superMethod.parameters().size(); in++) {
                code.assign(args, stack(OBJECT_TYPE.asArray()));
                code.loadConstant(stack(INT_TYPE), in);
                code.arrayGet(stack(OBJECT_TYPE.asArray()), stack(INT_TYPE), stack(OBJECT_TYPE));
                code.cast(stack(OBJECT_TYPE), stack(superMethod.parameters().get(in).getType()));
            }

            if (superMethod.returnType() != VOID_TYPE) {
                code.invokeSuper(stack(owner), superMethod, stack(OBJECT_TYPE), stack(OBJECT_TYPE));
                code.returnValue(stack(OBJECT_TYPE));
            } else {
                code.invokeSuper(stack(owner), superMethod, null, stack(OBJECT_TYPE));
                code.loadConstant(stack(OBJECT_TYPE), null);
                code.returnValue(stack(OBJECT_TYPE));
            }
        }
    }

    /**
     * For the mapping process of delegate base classes, it is usually done by directly returning the type itself.
     * Overwriting of this method is only necessary when private access to the package is required.
     * <br>Specifically, when delegating private methods of a package, this mapping requires the creation of a class hierarchy that elevates the private method access modifier to access the protected domains of each superclass in the base class's package.
     * <br><strong>The default implementation is non package private delegation</strong>
     *
     * @param baseClass Base class for dynamic delegation
     * @return The class that maps the base class is, by default, the class itself
     */
    protected <T> Class<? extends T> handleBaseClass(Class<T> baseClass) {
        return baseClass;
    }

    /**
     * Generate delegation from the base class and implement the given interface list of types.
     * Please refer to {@link DynamicMaker#makeClassInfo(Class, Class[], Class[])} for the behavior description of the class. The type description will be generated in this method.
     * <p>The usual task of this method is to generate the type identifier obtained from makeClassInfo and load the Java type it represents.
     *
     * @param baseClass Delegate base class
     * @param interfaces List of implemented interfaces
     * @return Types of dynamic delegation for the entire method
     */
    protected abstract <T> Class<? extends T> generateClass(Class<T> baseClass, Class<?>[] interfaces, Class<?>[] aspects);

    protected static class FuzzyMatcher{
        Parameter[] parameters;
        FuzzyMatch matcher;
        Annotation[] argsMatcher;

        public FuzzyMatcher(Parameter[] parameters, FuzzyMatch match, Annotation[] argMatchers) {
            this.parameters = parameters;
            this.matcher = match;
            this.argsMatcher = argMatchers;
        }

        public boolean match(Method method, boolean inBase, boolean isAbstract) {
            if (matcher == null) return false;

            if (!((matcher.inBaseClass() && inBase) || (matcher.inSuperClass() && !inBase))) return false;
            if (matcher.abstractOnly() && !isAbstract) return false;
            if (matcher.anySameName()) return true;

            Parameter[] params = method.getParameters();
            if (argsMatcher.length != params.length) return false;
            for (int i = 0; i < params.length; i++) {
                if (argsMatcher[i] instanceof FuzzyMatch.AnyType a) {
                    if (!Pattern.matches(a.value(), method.getName())) return false;
                } else if (argsMatcher[i] instanceof FuzzyMatch.TypeAssignable a) {
                    if (!Pattern.matches(a.value(), method.getName())) return false;
                    if (!parameters[i].getType().isAssignableFrom(params[i].getType())) return false;
                } else if (argsMatcher[i] instanceof FuzzyMatch.Exact e) {
                    if (parameters[i].getType() != params[i].getType()) return false;
                } else throw new IllegalHandleException("what?");
            }

            return true;
        }
    }

    /**
     * Dynamic delegation type identifier, so all dynamic delegation types generated by the factory will have this annotation identifier.
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface DynamicType {}

    /**
     * Dynamic method identification, all dynamically delegated methods will carry this annotation to identify the basic entry point for dynamic class behavior.
     */
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface DynamicMethod {}

    public interface SuperInvoker {
        Object invokeSuper(String signature, Object... args);
    }
}
