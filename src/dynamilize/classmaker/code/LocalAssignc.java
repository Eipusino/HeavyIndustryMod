package dynamilize.classmaker.code;

import dynamilize.classmaker.*;

public interface LocalAssignc<S, T extends S> extends Element {
    @Override
    default void accept(ElementVisitor visitor) {
        visitor.visitLocalSet(this);
    }

    @Override
    default ElementKind kind() {
        return ElementKind.LOCALASSIGN;
    }

    Localc<S> source();

    Localc<T> target();
}
