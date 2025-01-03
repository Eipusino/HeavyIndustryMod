package dynamilize.classmaker.code;

import dynamilize.classmaker.*;

public interface Element {
    void accept(ElementVisitor visitor);

    ElementKind kind();
}
