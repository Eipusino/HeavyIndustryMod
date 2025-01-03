package dynamilize.classmaker;

import dynamilize.classmaker.code.*;

public class DefaultReadVisitor implements ElementVisitor {
    @Override
    public void visitClass(Classc<?> clazz) {
        for (Element element : clazz.elements()) {
            element.accept(this);
        }
    }

    @Override
    public void visitMethod(Methodc<?, ?> method) {
        method.block().accept(this);
    }

    @Override
    public void visitField(Fieldc<?> field) {}

    @Override
    public void visitLocal(Localc<?> local) {}

    @Override
    public void visitInvoke(Invokec<?> invoke) {}

    @Override
    public void visitGetField(GetFieldc<?, ?> getField) {}

    @Override
    public void visitPutField(PutFieldc<?, ?> putField) {}

    @Override
    public void visitLocalSet(LocalAssignc<?, ?> localSet) {}

    @Override
    public void visitOperate(Operatec<?> operate) {}

    @Override
    public void visitCast(Castc cast) {}

    @Override
    public void visitGoto(Gotoc iGoto) {}

    @Override
    public void visitLabel(MarkLabelc label) {}

    @Override
    public void visitCompare(Comparec<?> compare) {}

    @Override
    public void visitCodeBlock(CodeBlockc<?> codeBlock) {
        for (Element element : codeBlock.codes()) {
            element.accept(this);
        }
    }

    @Override
    public void visitReturn(Returnc<?> returnc) {}

    @Override
    public void visitInstanceOf(InstanceOfc instanceOf) {}

    @Override
    public void visitNewInstance(NewInstancec<?> newInstance) {}

    @Override
    public void visitOddOperate(OddOperatec<?> operate) {}

    @Override
    public void visitConstant(LoadConstantc<?> loadConstant) {}

    @Override
    public void visitNewArray(NewArrayc<?> newArray) {}

    @Override
    public void visitCondition(Conditionc condition) {}

    @Override
    public void visitArrayGet(ArrayGetc<?> arrayGet) {}

    @Override
    public void visitArrayPut(ArrayPutc<?> arrayPut) {}

    @Override
    public void visitSwitch(Switchc<?> zwitch) {}

    @Override
    public void visitThrow(Throwc<?> thr) {}
}
