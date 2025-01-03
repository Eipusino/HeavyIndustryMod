package dynamilize.classmaker.code;

import dynamilize.classmaker.*;

import java.util.*;

public interface Switchc<T> extends Element {
    @Override
    default void accept(ElementVisitor visitor) {
        visitor.visitSwitch(this);
    }

    @Override
    default ElementKind kind() {
        return ElementKind.SWITCH;
    }

    boolean isTable();

    Label end();

    Localc<T> target();

    Map<T, Label> cases();

    void addCase(T caseKey, Label caseJump);
}
