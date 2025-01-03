package dynamilize.classmaker.code;

import dynamilize.classmaker.*;
import dynamilize.classmaker.code.annotation.*;

import java.lang.annotation.*;
import java.util.*;

public interface Classc<T> extends Element, AnnotatedElement {
    @Override
    default void accept(ElementVisitor visitor) {
        visitor.visitClass(this);
    }

    @Override
    default ElementKind kind() {
        return ElementKind.CLASS;
    }

    /**
     * Return the Java class marked by this type identifier.
     * @return The class marked with this type of tag.
     */
    Class<T> getTypeClass();

    /**
     * Is this type identifier an existing type identifier.
     * @return If the marked class has already been loaded by JVM.
     */
    boolean isExistedClass();

    boolean isPrimitive();

    Classc<T[]> asArray();

    <A extends Annotation> AnnotationType<A> asAnnotation(Map<String, Object> defaultAttributes);

    boolean isAnnotation();

    boolean isArray();

    Classc<?> componentType();

    String name();

    /**
     * Get the real name of this class in bytecode, for example: java.lang.Object -> Ljava/lang/Object;
     * <p>Specifically, for basic data types:
     * <pre>{@code
     * int     -> I
     * float   -> F
     * byte    -> B
     * short   -> S
     * long    -> J
     * double  -> D
     * char    -> C
     * boolean -> Z
     * void    -> V
     * }</pre>
     * @return The actual name of the class.
     */
    String realName();

    /**
     * Get the bytecode type identifier of this type, which is the real name without the first symbol L, for example, java.lang.Object -> java/lang/Object
     * @return The byte identifier name of the type.
     * @see ClassInfo#realName()
     */
    String internalName();

    int modifiers();

    Classc<? super T> superClass();

    List<Classc<?>> interfaces();

    List<Element> elements();

    <Type> Fieldc<Type> getField(Classc<Type> type, String name);

    <R> Methodc<T, R> getMethod(Classc<R> returnType, String name, Classc<?>... args);

    Methodc<T, Void> getConstructor(Classc<?>... args);

    boolean isAssignableFrom(Classc<?> target);
}
