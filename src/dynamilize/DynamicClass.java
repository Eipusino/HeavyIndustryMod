package dynamilize;

import dynamilize.runtimeannos.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * The dynamic type that stores dynamic object behavior information describes the shared behavior and variable information of the object.
 * <p>In the instance construction method of {@link DynamicMaker}, dynamic types are used to construct dynamic objects, which will have the behavior described by their types. The same method described in the base class and dynamic class will handle the overlay relationship normally.
 * <p>Unlike functions set for objects, methods described in dynamic classes are common to all instances of this class, i.e.:
 * <ul>
 * <li><strong>When a method of an object's dynamic type changes, the behavior of that method will also change, and the change will take effect immediately</strong>
 * <li><strong>When the method behavior of a superclass in a dynamic class changes, if the superclass method is referenced in the class's behavior, the class's behavior will change accordingly</strong>
 * <p><strong>The above changes will only take effect when the methods of the dynamic object come from the behavior described by the dynamic class</strong>
 * </ul>
 * <p>Describing the behavior of dynamic classes requires {@linkplain DynamicClass#visitClass(Class,JavaHandleHelper) behavior template} or function expressions to edit type behavior in incremental mode. Methods and default variables can only be added/changed and cannot be deleted.
 * <pre>{@code
 * //Here is a simple example:
 * public class Template{
 *     public static String str = "string0";
 *
 *     public static void method(@This final DynamicObject self, String arg0) {
 *         System.out.println(self.getVar(arg0));
 *     }
 * }
 *
 * //quote:
 * DynamicMaker maker = DynamicMaker.getDefault();
 * DynamicClass dyClass = DynamicClass.get("Sample");
 * dyClass.visitClass(Template.class, maker.getHelper());
 * DynamicObject<Object> dyObject = maker.newInstance(dyClass);
 * dyObject.invokeFunc("method", "str");
 *
 * >>> string0
 * }</pre>
 *
 * <strong>The initial value of a variable declared by a dynamic type is only valid when the object is created, and changing the initial value of a class variable at any time will not affect existing instances</strong>
 *
 * @see DynamicMaker#newInstance(Class, Class[], DynamicClass, Object...)
 * @see DynamicMaker#newInstance(DynamicClass)
 * @see DynamicMaker#newInstance(Class[], DynamicClass)
 * @see DynamicMaker#newInstance(Class, DynamicClass, Object...)
 */
@SuppressWarnings({"unchecked"})
public class DynamicClass {
    /** All instances of dynamic classes have been saved. Typically, dynamic types only exit the pool when actively deleted. For obsolete classes, please remember to use {@link DynamicClass#delete()} to delete them, otherwise it may cause memory leaks. */
    private static final HashMap<String, DynamicClass> classPool = new HashMap<>();

    /** Unique qualified name of type. */
    private final String name;

    /** The direct superclass of this dynamic class. */
    private final DynamicClass superDyClass;

    private final DataPool data;

    /** Abandoned tag: After the type has been abandoned, it cannot be instantiated again. */
    private boolean isObsoleted;

    /**
     * Declare a dynamic type, and if the type indicated by this name does not exist, create a new dynamic class using the given name.
     * @param name Unique qualified name of type.
     * @param superDyClass The direct superclass of this dynamic type.
     * @return A dynamic class instance with a specified name.
     * @throws IllegalHandleException If a type with that name already exists.
     */
    public static DynamicClass declare(String name, DynamicClass superDyClass) {
        if (classPool.containsKey(name))
            throw new IllegalHandleException("cannot declare two dynamic class with same name");

        DynamicClass dyc = new DynamicClass(name, superDyClass);
        classPool.put(name, dyc);
        return dyc;
    }

    /**
     * Get a dynamic class instance, and if the type indicated by this name does not exist, create a new dynamic class using the given name.
     * <p>The new class created from this method does not have a clear direct superclass. The instance will use the delegated base class as the direct superclass. If you need a type with a clear direct superclass, please declare it using {@link DynamicClass#declare(String, DynamicClass)}.
     * @param name Unique qualified name of type.
     * @return A dynamic class instance with a specified name.
     */
    public static DynamicClass get(String name) {
        return classPool.computeIfAbsent(name, n -> new DynamicClass(n, null));
    }

    public static DynamicClass visit(String name, Class<?> clazz, DynamicClass superDyClass, JavaHandleHelper helper) {
        DynamicClass res = superDyClass == null ? get(name) : declare(name, superDyClass);
        res.visitClass(clazz, helper);
        return res;
    }

    /** Create type instances, this method should not be called externally to construct instances. */
    DynamicClass(String name, DynamicClass superDyClass) {
        this.name = name;
        this.superDyClass = superDyClass;
        this.data = new DataPool(superDyClass == null? null: superDyClass.data);
    }

    /**
     * Remove this type of object from the pool and discard it. Any dynamic class that is no longer in use should be deleted correctly.
     * <p>Before you call this method, <strong>Please ensure that there are no more references to this type.</strong>
     */
    public void delete() {
        checkFinalized();

        classPool.remove(name);
        isObsoleted = true;
    }

    /**
     * Get the name of this dynamic type.
     * @return Unique qualified name of type.
     */
    public String getName() {
        checkFinalized();

        return name;
    }

    /**
     * Get the direct superclass of this dynamic type, which may be empty. If it is empty,
     * it indicates that the instance of this class uses the delegate type as the direct superclass.
     * @return Direct superclass of type.
     */
    public DynamicClass superDyClass() {
        checkFinalized();

        return superDyClass;
    }

    public DataPool genPool(DataPool basePool) {
        return new DataPool(data) {
            @Override
            public FunctionEntryc select(String name1, FunctionType type) {
                FunctionEntryc res1 = super.select(name1, type);
                if (res1 != null) return res1;

                return basePool.select(name1, type);
            }

            @Override
            public Variablec getVariable(String name1) {
                Variablec var = super.getVariable(name1);
                if (var != null) return var;

                return basePool.getVariable(name1);
            }
        };
    }

    public FunctionEntryc[] getFunctions() {
        return data.getFunctions();
    }

    public Variablec[] getVariables() {
        return data.getVariables();
    }

    /**
     * Accessing a class as a behavior template, using the fields/methods declared in the class to describe dynamic class behavior, and declaring<strong>static members</strong>in the class will produce the following effect:
     * <ul>
     * <li><strong>Method</strong>: Describes instance shared methods for dynamic typing. If a method with the same name and parameters is passed in repeatedly, the old method will be overwritten by the new one.
     * <p>The method template will be created as a method entry, and when this method of the instance is called, the call will actually be passed into this template method, and the 'this' pointer will be passed as an argument (optional)
     * <p>To receive the pointer, you need to annotate the first parameter of the template method with {@link This} and the parameter should be final,
     * The parameter type marked as this pointer must be an assignable type (dynamic instances ensure that the interface DynamicObject has been implemented), and this parameter will not occupy a matching position in the parameter table:
     * <p>For example, the method<pre>{@code sample(@This final DynamicObject self, String str)}</pre>can correctly match the function of the object.<pre>{@code sample(String str)}</pre>
     * <p><strong>Only the replaced method can change the behavior of the instance. The newly added behavior will not affect the behavior of the existing instance. The newly added behavior will only make the newly generated instance have this default function.</strong>
     *
     * <li><strong>Field</strong>: Describes the default variable table for dynamic types, and uses the value stored by the field during dynamic object instantiation as the initial data for the variable.
     * <p>If the type of the field is {@link Initializer.Producer}, this function will be used as the factory for the value, and the data produced by the function will be used as the default value for the variable when initializing the dynamic instance.
     * <p><strong>Except for being accessed as a behavioral template, any changes in the value of timing variables will not have a direct impact on the behavior of the type.</strong>
     * </ul>
     * If there are fields or methods in the template that you do not want to be used as samples, you can use the {@link Exclude} annotation to mark this target for exclusion.
     * <p><strong>All methods and variables that describe dynamic class behavior must have the public static modifier.</strong>
     */
    public void visitClass(Class<?> template, JavaHandleHelper helper) {
        checkFinalized();

        for (Method method: template.getDeclaredMethods()) {
            if (method.getAnnotation(Exclude.class) != null) continue;

            if (!Modifier.isStatic(method.getModifiers()) || !Modifier.isPublic(method.getModifiers())) continue;

            setFunctionWithMethod(helper, method);
        }

        for (Field field: template.getDeclaredFields()) {
            if (field.getAnnotation(Exclude.class) != null) continue;

            if (!Modifier.isStatic(field.getModifiers()) || !Modifier.isPublic(field.getModifiers())) continue;

            setVariableWithField(field);
        }
    }

    /**
     * Accessing a method template, unlike {@link DynamicClass#visitClass(Class,JavaHandleHelper)}, this method only accesses a single method and creates its behavior template.
     * <p>For the specific behavior of this method, please refer to the {@linkplain  DynamicClass#visitClass(Class,JavaHandleHelper) method section}.
     * @param method Sample version of access method.
     */
    public void visitMethod(Method method, JavaHandleHelper helper) {
        if (!Modifier.isStatic(method.getModifiers()) || !Modifier.isPublic(method.getModifiers()))
            throw new IllegalHandleException("method template must be public and static");

        setFunctionWithMethod(helper, method);
    }

    /**
     * Accessing a field template, unlike {@link DynamicClass#visitClass(Class,JavaHandleHelper)}, this method only accesses a single field and creates its behavior template.
     * <p>For the specific behavior of this method, please refer to the {@linkplain  DynamicClass#visitClass(Class,JavaHandleHelper) Field section}.
     * @param field Field sample for access.
     */
    public void visitField(Field field) {
        if (!Modifier.isStatic(field.getModifiers()) || !Modifier.isPublic(field.getModifiers()))
            throw new IllegalHandleException("field template must be public and static");

        setVariableWithField(field);
    }

    /**
     * Set functions in lambda mode, use anonymous functions to describe type behavior, and the effect of changing type behavior is the same as the method part of {@link DynamicClass#visitClass(Class,JavaHandleHelper)}.
     * @param name Function name
     * @param func Anonymous functions that describe function behavior
     * @param argTypes Formal parameter types of functions
     */
    public <S, R> void setFunction(String name, Function<S, R> func, Class<?>... argTypes) {
        data.setFunction(name, func, argTypes);
    }

    public <S, R> void setFunction(String name, Function.SuperGetFunction<S, R> func, Class<?>... argTypes) {
        data.setFunction(name, func, argTypes);
    }

    /** Same as {@link DynamicClass#setFunction(String, Function, Class[])}, except that the anonymous function has no return value. */
    public <S> void setFunction(String name, Function.NonRetFunction<S> func, Class<?>... argTypes) {
        this.<S, Object>setFunction(name, (s, a) -> {
            func.invoke(s, a);
            return null;
        }, argTypes);
    }

    /** Same as {@link DynamicClass#setFunction(String, Function.SuperGetFunction, Class[])}, except that the anonymous function has no return value. */
    public <S> void setFunction(String name, Function.NonRetSuperGetFunc<S> func, Class<?>... argTypes) {
        this.<S, Object>setFunction(name, (s, sup, a) -> {
            func.invoke(s, sup, a);
            return null;
        }, argTypes);
    }

    /**
     * The constant mode sets the initial value of the variable, which behaves similarly to the {@link DynamicClass#visitClass(Class,JavaHandleHelper)} field.
     * @param name Variable name
     * @param value constant value
     */
    public void setVariable(String name, Object value) {
        setVariable(name, () -> value);
    }

    /**
     * Function mode setting variable initialization factory, behavior is the same as the {@link DynamicClass#visitClass(Class,JavaHandleHelper)} field part.
     * @param name Variable name
     * @param prov Factory function for initial values of production variables
     */
    public void setVariable(String name, Initializer.Producer<?> prov) {
        data.setVariable(new Variable(name, new Initializer<>(prov)));
    }

    private void setFunctionWithMethod(JavaHandleHelper helper, Method method) {
        if (!Modifier.isStatic(method.getModifiers()))
            throw new IllegalHandleException("cannot assign a non-static method to function");

        Parameter[] parameters = method.getParameters();
        ArrayList<Class<?>> arg = new ArrayList<>();

        boolean thisPointer = false, superPointer = false;
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            if (param.getAnnotation(This.class) != null) {
                if (thisPointer)
                    throw new IllegalHandleException("only one self-pointer can exist");
                if (i != 0)
                    throw new IllegalHandleException("self-pointer must be the first in parameters");

                thisPointer = true;
            } else if (param.getAnnotation(Super.class) != null) {
                if (superPointer)
                    throw new IllegalHandleException("only one super-pointer can exist");
                if (i != (thisPointer ? 1 : 0))
                    throw new IllegalHandleException("super-pointer must be the first in parameters or the next of self-pointer(if self pointer was existed)");

                superPointer = true;
            } else arg.add(param.getType());
        }

        boolean thisP = thisPointer;
        boolean superP = superPointer;

        Function<Object, Object> v = helper.genJavaMethodRef(method).getFunction();

        int offset = thisP? superP? 2: 1: 0;
        FunctionType type = FunctionType.inst(method);

        data.setFunction(name, (self, args) -> {
            Object[] argsArray = args.args();
            Object[] realArgArr = ArgumentList.getList(argsArray.length + offset);

            if (thisP) realArgArr[0] = self;

            DataPool.ReadOnlyPool superPool = null;
            if (thisP && superP) realArgArr[1] = data.getSuper(self, superPool = self.baseSuperPointer());
            else if (!thisP && superP) realArgArr[0] = data.getSuper(self, superPool = self.baseSuperPointer());

            if (argsArray.length != 0) System.arraycopy(argsArray, 0, realArgArr, offset, argsArray.length);

            try {
                Object res = v.invoke(self, type, realArgArr);
                ArgumentList.recycleList(realArgArr);
                if (superPool != null) superPool.recycle();
                return res;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }, arg.toArray(new Class[0]));
    }

    private void setVariableWithField(Field field) {
        Object value;
        try {
            value = field.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        data.setVariable(new Variable(field.getName(), new Initializer<>(value instanceof Initializer.Producer? (Initializer.Producer<? super Object>) value: () -> {
            try {
                return field.get(null);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        })));
    }

    private void checkFinalized() {
        if (isObsoleted)
            throw new IllegalHandleException("cannot do anything on obsoleted dynamic class");
    }

    @Override
    public String toString() {
        return "dynamic class:" + name;
    }
}
