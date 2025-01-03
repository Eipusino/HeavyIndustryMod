package dynamilize.classmaker.code;

import dynamilize.classmaker.*;

import java.util.*;

public interface NewInstancec<T> extends Element {
    @Override
    default void accept(ElementVisitor visitor) {
        visitor.visitNewInstance(this);
    }

    @Override
    default ElementKind kind() {
        return ElementKind.NEWINSTANCE;
    }

    Methodc<T, Void> constructor();

    Classc<T> type();

    Localc<? extends T> instanceTo();

    List<Localc<?>> params();
}
