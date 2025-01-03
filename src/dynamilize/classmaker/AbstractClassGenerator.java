package dynamilize.classmaker;

import dynamilize.classmaker.code.*;

import java.util.*;

public abstract class AbstractClassGenerator implements ElementVisitor {
    protected Classc<?> currGenerating;
    protected Fieldc<?> currField;
    protected Methodc<?, ?> currMethod;
    protected CodeBlockc<?> currCodeBlock;

    protected Map<String, Localc<?>> localMap;

    @Override
    public void visitClass(Classc<?> clazz) {
        currGenerating = clazz;
        for (Element element: clazz.elements()) {
            element.accept(this);
        }
    }

    @Override
    public void visitCodeBlock(CodeBlockc<?> block) {
        currCodeBlock = block;
        for (Element element: block.codes()) {
            element.accept(this);
        }
    }

    @Override
    public void visitField(Fieldc<?> field) {
        currField = field;
    }

    @Override
    public void visitMethod(Methodc<?, ?> method) {
        currMethod = method;
        if (method.block() != null) method.block().accept(this);
    }

    @Override
    public void visitLocal(Localc<?> local) {
        localMap.put(local.name(), local);
    }

    public abstract byte[] genByteCode(ClassInfo<?> classInfo);

    protected abstract <T> Class<T> generateClass(ClassInfo<T> classInfo) throws ClassNotFoundException;
}
