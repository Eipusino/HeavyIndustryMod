package dynamilize.classmaker.code;

import dynamilize.classmaker.*;

public interface PutFieldc<S, T extends S> extends Element {
    @Override
    default void accept(ElementVisitor visitor) {
        visitor.visitPutField(this);
    }

    @Override
    default ElementKind kind() {
        return ElementKind.FIELDSET;
    }

    Localc<?> inst();

    Localc<S> source();

    Fieldc<T> target();
}
