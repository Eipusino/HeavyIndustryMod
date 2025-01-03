package dynamilize.classmaker.code;

import dynamilize.classmaker.*;

public interface Gotoc extends Element {
    @Override
    default void accept(ElementVisitor visitor) {
        visitor.visitGoto(this);
    }

    @Override
    default ElementKind kind() {
        return ElementKind.GOTO;
    }

    Label target();
}
