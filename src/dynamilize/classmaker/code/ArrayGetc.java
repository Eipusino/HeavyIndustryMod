package dynamilize.classmaker.code;

import dynamilize.classmaker.*;

public interface ArrayGetc<T> extends Element {
    @Override
    default void accept(ElementVisitor visitor) {
        visitor.visitArrayGet(this);
    }

    @Override
    default ElementKind kind() {
        return ElementKind.ARRAYGET;
    }

    Localc<T[]> array();

    Localc<Integer> index();

    Localc<T> getTo();
}
