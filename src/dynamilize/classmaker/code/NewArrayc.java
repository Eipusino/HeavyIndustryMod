package dynamilize.classmaker.code;

import dynamilize.classmaker.*;

import java.util.*;

public interface NewArrayc<T> extends Element {
    @Override
    default void accept(ElementVisitor visitor) {
        visitor.visitNewArray(this);
    }

    @Override
    default ElementKind kind() {
        return ElementKind.NEWARRAY;
    }

    Classc<T> arrayEleType();

    List<Localc<Integer>> arrayLength();

    Localc<?> resultTo();
}
