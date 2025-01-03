package dynamilize.classmaker.code;

import dynamilize.classmaker.*;

public interface Localc<T> extends Element {
    @Override
    default void accept(ElementVisitor visitor) {
        visitor.visitLocal(this);
    }

    @Override
    default ElementKind kind() {
        return ElementKind.LOCAL;
    }

    String name();

    int modifiers();

    Classc<T> type();

    Object initial();
}
