package dynamilize.classmaker.code;

import dynamilize.classmaker.*;

public interface ArrayPutc<T> extends Element {
    @Override
    default void accept(ElementVisitor visitor) {
        visitor.visitArrayPut(this);
    }

    @Override
    default ElementKind kind() {
        return ElementKind.ARRAYPUT;
    }

    Localc<T[]> array();

    Localc<Integer> index();

    Localc<T> value();
}
