package dynamilize.classmaker.code;

import dynamilize.classmaker.*;

public interface LoadConstantc<T> extends Element {
    @Override
    default void accept(ElementVisitor visitor) {
        visitor.visitConstant(this);
    }

    @Override
    default ElementKind kind() {
        return ElementKind.LOADCONSTANT;
    }

    T constant();

    Localc<T> constTo();
}
