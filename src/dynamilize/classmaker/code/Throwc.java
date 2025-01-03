package dynamilize.classmaker.code;

import dynamilize.classmaker.*;

public interface Throwc<T extends Throwable> extends Element {
    @Override
    default void accept(ElementVisitor visitor) {
        visitor.visitThrow(this);
    }

    @Override
    default ElementKind kind() {
        return ElementKind.THROW;
    }

    Localc<T> thr();
}
