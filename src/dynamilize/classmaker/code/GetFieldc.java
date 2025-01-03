package dynamilize.classmaker.code;

import dynamilize.classmaker.*;

public interface GetFieldc<S, T extends S> extends Element {
    @Override
    default void accept(ElementVisitor visitor) {
        visitor.visitGetField(this);
    }

    @Override
    default ElementKind kind() {
        return ElementKind.FIELDGET;
    }

    Localc<?> inst();

    Fieldc<S> source();

    Localc<T> target();
}
