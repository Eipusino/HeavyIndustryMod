package dynamilize.classmaker.code;

import dynamilize.classmaker.*;

import java.util.*;

public interface Invokec<R> extends Element {
    @Override
    default void accept(ElementVisitor visitor) {
        visitor.visitInvoke(this);
    }

    @Override
    default ElementKind kind() {
        return ElementKind.INVOKE;
    }

    Localc<?> target();

    Methodc<?, R> method();

    List<Localc<?>> args();

    Localc<? super R> returnTo();

    boolean callSuper();
}
