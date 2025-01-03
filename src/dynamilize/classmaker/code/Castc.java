package dynamilize.classmaker.code;

import dynamilize.classmaker.*;

public interface Castc extends Element {
    @Override
    default void accept(ElementVisitor visitor) {
        visitor.visitCast(this);
    }

    @Override
    default ElementKind kind() {
        return ElementKind.CAST;
    }

    Localc<?> source();

    Localc<?> target();
}
