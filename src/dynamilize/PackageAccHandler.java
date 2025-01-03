package dynamilize;

import dynamilize.classmaker.Parameterf;
import dynamilize.classmaker.*;
import dynamilize.classmaker.code.*;

import java.lang.reflect.*;
import java.util.*;

/**
 * The utility class used to access and elevate package private methods is used to create a string of inheritance subtrees for elevating package private methods on a target class,
 * in order to achieve the purpose of rewriting package private methods.
 * <br>Specifically, after passing the target base class into the {@link PackageAccHandler#handle(Class)} method, it will sequentially check whether there are package private methods in each parent type of this type,
 * If there is a package private method, the package name of this class will be used to inherit the previous layer type once, and this class will be loaded into the same protected domain through {@link PackageAccHandler#loadClass(ClassInfo, Class)},
 * At this point, inheriting the type can access the private methods of the target class package, rewriting and raising its access modifier to {@code protected}.
 * After completing the traversal of all superclass in the base class, all package private methods in the hierarchical structure of that class can be accessed.
 *
 * <p>Intuitively speaking, this class does the following:
 * <pre>{@code
 * //Assuming there are several categories:
 *
 * package pac1;
 * public class A {
 *     void method1() { //Package private methods
 *
 *     }
 *
 *     public void test() {
 *         method1();
 *     }
 * }
 *
 * package pac2;
 * import pac1.A;
 * public class B extends A {
 *     void method2() { //Package private methods
 *
 *     }
 *
 *     @Override
 *     public void test() {
 *         super.test();
 *         method2();
 *     }
 * }
 *
 * //If the handle method is passed into class B, the following two types will be created:
 *
 * package pac2:
 * public class B$packageAccess$0 extends B {
 *     @Override
 *     protected void method2() {
 *         super.method2();
 *     }
 * }
 *
 * package pac1:
 * import pac2.B$packageAccess$0;
 * public class A$packageAccess$0 extends B$packageAccess$0 {
 *     @Override
 *     protected void method1() {
 *         super.method2();
 *     }
 * }
 *
 * //Finally, the handle method will return the class object pac1.A$packageAccess $0. At this point, all package private methods have been promoted to protected, and the subclass's rewriting of both package private methods will take effect when calling the test method.
 * }</pre>
 * <strong>be careful:<ul>
 *   <li>The method of cross package inheritance and class rewriting shown above is actually illegal in Java compilers, but this logic is valid in JVM. The above code is only for logical illustration</li>
 *   <li>Due to the limitation of Java package namespace, it is not possible to load Java open package name types from external sources. Therefore, open package private methods do not apply to classes with package names starting with 'java.'</li>
 * </ul></strong>
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class PackageAccHandler {
    public static final int PAC_PRI_FLAGS = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE | Modifier.STATIC | Modifier.FINAL;
    public static final Localc[] A = new Localc[0];
    private final Map<Class<?>, Class<?>> classMap = new HashMap<>();

    public <T> Class<? extends T> handle(Class<T> baseClass) {
        return (Class<? extends T>) classMap.computeIfAbsent(baseClass, c -> {
            Class<?> curr = c;
            Class<?> opening = null;

            while (curr != null) {
                if (shouldOpen(curr)) {
                    ClassInfo<?> ci = makeClassInfo(opening == null ? ClassInfo.asType(curr) : ClassInfo.asType(opening), curr);
                    opening = loadClass(ci, curr);
                }

                curr = curr.getSuperclass();
            }

            return opening == null? c: opening;
        });
    }

    protected boolean shouldOpen(Class<?> checking) {
        if (checking.getPackage().getName().startsWith("java.")) return false;

        for (Method method : checking.getDeclaredMethods()) {
            if ((method.getModifiers() & PAC_PRI_FLAGS) == 0) {
                return true;
            }
        }
        return false;
    }

    protected <T> ClassInfo<? extends T> makeClassInfo(ClassInfo<?> superClass, Class<?> base) {
        ClassInfo<?> res = new ClassInfo<>(
                Modifier.PUBLIC,
                base.getName() + "$packageAccess$" + superClass.name().hashCode(),
                superClass
        );

        ClassInfo<?> baseC = ClassInfo.asType(base);

        for (Method method : base.getDeclaredMethods()) {
            if ((method.getModifiers() & PAC_PRI_FLAGS) == 0) {
                Methodc<?, ?> sup = baseC.getMethod(
                        ClassInfo.asType(method.getReturnType()),
                        method.getName(),
                        Arrays.stream(method.getParameterTypes()).map(ClassInfo::asType).toArray(ClassInfo[]::new)
                );

                CodeBlock<?> code = res.declareMethod(
                        Modifier.PROTECTED,
                        method.getName(),
                        ClassInfo.asType(method.getReturnType()),
                        Parameterf.asParameter(method.getParameters())
                );

                Localc<T> self = code.getThis();
                if (method.getReturnType() == void.class) {
                    code.invokeSuper(
                            self,
                            sup,
                            null,
                            code.getParamList().toArray(A)
                    );
                } else {
                    Localc ret = code.local(ClassInfo.asType(method.getReturnType()));

                    code.invokeSuper(
                            self,
                            sup,
                            ret,
                            code.getParamList().toArray(A)
                    );
                    code.returnValue(ret);
                }
            }
        }

        for (Constructor<?> constructor : superClass.getTypeClass().getDeclaredConstructors()) {
            if ((constructor.getModifiers() & PAC_PRI_FLAGS) == 0 && !(base.getPackage().equals(superClass.getTypeClass().getPackage()))) continue;
            if ((constructor.getModifiers() & (Modifier.PRIVATE | Modifier.FINAL)) != 0) continue;

            Methodc<?, ?> su = superClass.getConstructor(Arrays.stream(constructor.getParameterTypes()).map(ClassInfo::asType).toArray(Classc[]::new));

            CodeBlock<?> code = res.declareConstructor(Modifier.PUBLIC, Parameterf.asParameter(constructor.getParameters()));
            code.invoke(code.getThis(), su, null, code.getParamList().toArray(A));
        }

        return (ClassInfo<? extends T>) res;
    }

    /**
     * Load the target {@linkplain ClassInfo type information} as an object with the same protection domain as the base type,
     * and implement this method based on the target platform.
     */
    protected abstract <T> Class<? extends T> loadClass(ClassInfo<?> clazz, Class<T> baseClass);
}
