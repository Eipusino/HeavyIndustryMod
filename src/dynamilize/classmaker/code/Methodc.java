package dynamilize.classmaker.code;

import dynamilize.classmaker.*;
import dynamilize.classmaker.code.annotation.*;

import java.util.*;

public interface Methodc<S, R> extends Element, AnnotatedElement {
    @Override
    default void accept(ElementVisitor visitor) {
        visitor.visitMethod(this);
    }

    @Override
    default ElementKind kind() {
        return ElementKind.METHOD;
    }

    String name();

    int modifiers();

    String typeDescription();

    List<Parameterf<?>> parameters();

    List<Classc<? extends Throwable>> throwTypes();

    Classc<S> owner();

    Classc<R> returnType();

    CodeBlockc<R> block();
}
