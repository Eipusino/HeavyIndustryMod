package dynamilize;

import java.util.*;

/**
 * The information container used for storing and handling dynamic object data should not be accessed externally.
 * Each dynamic object is bound to a data pool to store information such as variables/functions of the object.
 * <p>For an instance of the {@linkplain DynamicClass dynamic class}, the instance's data pool must have a parent pool that is initialized with information from the direct superclass description of the dynamic class.
 * <p>Access to pool information is always based on the nearest principle, which means that if no data is found in the current pool, the data from the parent pool with this variable/function that is closest to the instance's pool will be used as the reference.
 */
@SuppressWarnings({"unchecked"})
public class DataPool {
    private static final String init = "<init>";

    private static final List<FunctionEntryc> TMP_LIS = new ArrayList<>();
    public static final FunctionEntryc[] EMP_METS = new FunctionEntryc[0];

    private static final List<Variablec> TMP_VAR = new ArrayList<>();
    public static final Variablec[] EMP_VARS = new Variablec[0];

    private final DataPool superPool;

    private final Map<String, Map<FunctionType, FunctionEntryc>> funcPool = new HashMap<>();
    private final Map<String, Variablec> varPool = new HashMap<>();

    /**
     * Create a pool object and bind it to the parent pool, which can be null.
     * In this case, the pool should be a method/field reference of the delegated type.
     * <p><strong>Generally speaking, you should not instantiate this type anywhere outside of {@link DynamicMaker}.</strong>
     * @param superPool The parent pool of this pool.
     */
    public DataPool(DataPool superPool) {
        this.superPool = superPool;
    }

    public void init(DynamicObject<?> self, Object... args) {
        DynamicClass curr = self.getDyClass();
        HashSet<String> varSetted = new HashSet<>();

        while (curr != null) {
            for (Variablec var : curr.getVariables()) {
                if (varSetted.add(var.name())) {
                    var.init(self);
                }
            }

            curr = curr.superDyClass();
        }

        ArgumentList lis = ArgumentList.as(args);
        FunctionEntryc fun = select(init, lis.type());
        if (fun == null) return;

        fun.getFunction().invoke((DynamicObject<Object>) self, args);
        lis.type().recycle();
        lis.recycle();
    }

    public void setConstructor(Function<?, ?> function, Class<?>... argType) {
        setFunction(init, function, argType);
    }

    public Function<?, ?> getConstructor(Class<?>... argType) {
        FunctionType type = FunctionType.inst(argType);
        Function<?, ?> fun = select(init, type).getFunction();
        type.recycle();
        return fun;
    }

    /**
     * Set a function in this pool, regardless of whether the parent pool has a function with the same name and type.
     * If a function with the same name and type exists in this pool, the old function will be overwritten.
     * <p>The name, behavior, parameter type, and Java method of the function are consistent,
     * and the return value is determined by the anonymous function passed in the behavior, for example:
     * <pre>{@code
     * java defined methods
     * int getTime(String location) {
     *     return foo(location);
     * }
     * Equivalent to setting a function.
     * set("getTime", (self, args) -> return foo(args.get(0)), String.class);
     * }</pre>
     *
     * @param name Function name.
     * @param argsType List of parameter types for functions.
     * @param function An anonymous function that describes the behavior of this function.
     */
    public void setFunction(String name, Function<?, ?> function, Class<?>... argsType) {
        FunctionType type = FunctionType.inst(argsType);
        funcPool.computeIfAbsent(name, n -> new HashMap<>())
                .put(type, new FunctionEntry<>(name, function, type));
    }

    public <R, S> void setFunction(String name, Function.SuperGetFunction<S,R> func, Class<?>[] argTypes) {
        FunctionType type = FunctionType.inst(argTypes);
        funcPool.computeIfAbsent(name, n -> new HashMap<>())
                .put(type, new FunctionEntry<>(name, func, type, this));
    }

    public void setFunction(FunctionEntryc functionEntry) {
        funcPool.computeIfAbsent(functionEntry.getName(), e -> new HashMap<>()).put(functionEntry.getType(), functionEntry);
    }

    /**
     * Retrieve the object of variables from the class hierarchy.
     * @param name Variable Name.
     * @return Variable object
     */
    public Variablec getVariable(String name) {
        Variablec var = varPool.get(name);
        if (var != null) return var;

        if (superPool != null) {
            return superPool.getVariable(name);
        }

        return null;
    }

    /**
     * Adding a variable object to the pool is generally only used when marking Java fields as variables.
     * @param var Variables added to the pool.
     */
    public void setVariable(Variablec var) {
        varPool.putIfAbsent(var.name(), var);
    }

    /**
     * Output the functions defined in the class hierarchy as function entries, and prioritize searching for functions with the same type signature.
     * If no identical function is found, the function will be transferred to a type signature matching function,
     * Therefore, when calling functions with high performance requirements, it is recommended to clearly declare the type signature on the actual parameter list,
     * which can effectively improve the speed of overload decision-making.
     * <p>If the function is not defined, return empty.
     * @param name The name of the function
     * @param type Parameterf types of functions
     * @return Select the function entry of the selected function
     */
    public FunctionEntryc select(String name, FunctionType type) {
        Map<FunctionType, FunctionEntryc> map;
        FunctionEntryc res;

        DataPool curr = this;
        while (curr != null) {
            map = curr.funcPool.get(name);
            if (map != null) {
                res = map.get(type);
                if (res != null) return res;
            }

            curr = curr.superPool;
        }

        curr = this;
        while (curr != null) {
            map = curr.funcPool.get(name);
            if (map != null) {
                for (Map.Entry<FunctionType, FunctionEntryc> entry : map.entrySet()) {
                    if (entry.getKey().match(type.getTypes())) {
                        return entry.getValue();
                    }
                }
            }

            curr = curr.superPool;
        }

        return null;
    }

    public Variablec[] getVariables() {
        TMP_VAR.clear();
        TMP_VAR.addAll(varPool.values());
        return TMP_VAR.toArray(EMP_VARS);
    }

    public FunctionEntryc[] getFunctions() {
        TMP_LIS.clear();
        for (Map<FunctionType, FunctionEntryc> entry : funcPool.values()) {
            TMP_LIS.addAll(entry.values());
        }

        return TMP_LIS.toArray(EMP_METS);
    }

    /** Obtain read-only objects for the pool. */
    public <S> ReadOnlyPool getReader(DynamicObject<S> owner) {
        return new ReadOnlyPool(this, owner, null);
    }

    public <T> ReadOnlyPool getSuper(DynamicObject<T> owner, ReadOnlyPool alternative) {
        return superPool == null ? alternative : ReadOnlyPool.get(superPool, owner, alternative);
    }

    public static class ReadOnlyPool {
        public static int MAX_CHANCES = 2048;
        private static final Stack<ReadOnlyPool> POOLS = new Stack<>();

        private DataPool pool;
        private DynamicObject<?> owner;
        private ReadOnlyPool alternative;

        private final boolean hold;

        private ReadOnlyPool() {
            hold = false;
        }

        private ReadOnlyPool(DataPool pool, DynamicObject<?> owner, ReadOnlyPool alternative) {
            this.pool = pool;
            this.owner = owner;
            this.alternative = alternative;

            hold = true;
        }

        private static ReadOnlyPool get(DataPool source, DynamicObject<?> owner, ReadOnlyPool alternative) {
            ReadOnlyPool res = POOLS.isEmpty()? new ReadOnlyPool() : POOLS.pop();
            res.pool = source;
            res.owner = owner;
            res.alternative = alternative;

            return res;
        }

        public void recycle() {
            if (hold) return;

            pool = null;
            owner = null;
            alternative = null;

            if (POOLS.size() < MAX_CHANCES) POOLS.push(this);
        }

        /** @see DynamicObject#getVar(String) */
        public <T> T getVar(String name) {
            Variablec var = pool.getVariable(name);

            if (var == null) var = alternative == null ? null : alternative.getVar(name);

            if (var == null)
                throw new IllegalHandleException("variable " + name + " was not definer");

            return var.get(owner);
        }

        /** @see DynamicObject#getFunc(String, FunctionType) */
        public FunctionEntryc getFunc(String name, FunctionType type) {
            FunctionEntryc func = pool.select(name, type);
            if (func == null) {
                if (alternative == null)
                    throw new IllegalHandleException("no such function: " + name + type);

                return alternative.getFunc(name, type);
            }

            return func;
        }

        /** @see DynamicObject#invokeFunc(String, Object...) */
        public <R> R invokeFunc(String name, Object... args) {
            ArgumentList lis = ArgumentList.as(args);
            R r = invokeFunc(name, lis);
            lis.type().recycle();
            lis.recycle();
            return r;
        }

        public <R> R invokeFunc(FunctionType type, String name, Object... args) {
            ArgumentList lis = ArgumentList.asWithType(type, args);
            R r = invokeFunc(name, lis);
            lis.recycle();
            return r;
        }

        /** @see DynamicObject#invokeFunc(String, ArgumentList) */
        public <R> R invokeFunc(String name, ArgumentList args) {
            FunctionType type = args.type();

            return (R) getFunc(name, type).getFunction().invoke((DynamicObject<Object>) owner, args);
        }
    }
}
