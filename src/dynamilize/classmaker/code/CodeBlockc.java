package dynamilize.classmaker.code;

import dynamilize.classmaker.*;

import java.util.*;

public interface CodeBlockc<R> extends Element{
    @Override
    default void accept(ElementVisitor visitor) {
        visitor.visitCodeBlock(this);
    }

    @Override
    default ElementKind kind() {
        return ElementKind.BLOCK;
    }

    Methodc<?, R> owner();

    List<Element> codes();

    List<Localc<?>> getParamList();

    List<Localc<?>> getParamAll();

    List<Label> labelList();

    int modifiers();
}
