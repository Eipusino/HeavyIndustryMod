package dynamilize.classmaker.code;

import dynamilize.classmaker.*;

public interface Returnc<T> extends Element {
    @Override
    default void accept(ElementVisitor visitor) {
        visitor.visitReturn(this);
    }

    @Override
    default ElementKind kind() {
        return ElementKind.RETURN;
    }

    Localc<T> returnValue();
}
