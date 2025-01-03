package dynamilize.classmaker.code;

import dynamilize.classmaker.*;

public interface InstanceOfc extends Element {
    @Override
    default void accept(ElementVisitor visitor) {
        visitor.visitInstanceOf(this);
    }

    @Override
    default ElementKind kind() {
        return ElementKind.INSTANCEOF;
    }

    Localc<?> target();

    Classc<?> type();

    Localc<Boolean> result();
}
