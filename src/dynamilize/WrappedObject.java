package dynamilize;

import java.lang.reflect.*;

/**
 * A wrapper object used for dynamic access to objects, with only limited dynamic behavior (considered as a reflective wrapper).
 * <p>
 * For a packaged dynamic object that can only dynamically access its original properties and methods, the following behavior will throw an exception:
 * <ul>
 *   <li>Attempt to access members (fields, methods) that are not defined in the hierarchy of the packaged object.</li>
 *   <li>Attempt to modify the dynamic type of the packaging dynamic object.</li>
 *   <li>Attempt to convert the packaged dynamic object into the packaged object type.</li>
 * </ul>
 */
@SuppressWarnings("unchecked")
public class WrappedObject<T> implements DynamicObject<T> {
    private static final DynamicClass $wrappedDef$ = new DynamicClass("$wrappedDef$", null) {
        @Override
        public void setVariable(String name, Initializer.Producer<?> prov) {
            throw new IllegalHandleException("wrapped object class is immutable");
        }

        @Override
        public <S, R> void setFunction(String name, Function<S, R> func, Class<?>... argTypes) {
            throw new IllegalHandleException("wrapped object class is immutable");
        }

        @Override
        public void visitClass(Class<?> template, JavaHandleHelper helper) {
            throw new IllegalHandleException("wrapped object class is immutable");
        }

        @Override
        public void visitField(Field field) {
            throw new IllegalHandleException("wrapped object class is immutable");
        }

        @Override
        public void visitMethod(Method method, JavaHandleHelper helper) {
            throw new IllegalHandleException("wrapped object class is immutable");
        }
    };

    private final DataPool pool;
    private final T obj;

    WrappedObject(T obj, DataPool pool) {
        this.pool = pool;
        this.obj = obj;
    }

    @Override
    public T objSelf() {
        return obj;
    }

    @Override
    public DynamicClass getDyClass() {
        return $wrappedDef$;
    }

    @Override
    public Variablec getVariable(String name) {
        return pool.getVariable(name);
    }

    @Override
    public DataPool.ReadOnlyPool baseSuperPointer() {
        return null;
    }

    @Override
    public void setVariable(Variablec variable) {
        throw new IllegalHandleException("wrapped object cannot add new variable");
    }

    @Override
    public <U> U varValueGet(String name) {
        throw new IllegalHandleException("unsupported operation");
    }

    @Override
    public <U> void varValueSet(String name, U value) {
        throw new IllegalHandleException("unsupported operation");
    }

    @Override
    public FunctionEntryc getFunc(String name, FunctionType type) {
        return pool.select(name, type);
    }

    @Override
    public <R> void setFunc(String name, Function<T, R> func, Class<?>... argTypes) {
        throw new IllegalHandleException("wrapped object cannot add new function");
    }

    @Override
    public <R> void setFunc(String name, Function.SuperGetFunction<T, R> func, Class<?>... argTypes) {
        throw new IllegalHandleException("wrapped object cannot add new function");
    }
}
