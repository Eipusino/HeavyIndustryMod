package dynamilize.classmaker;

import dynamilize.classmaker.code.*;

public interface ElementVisitor {
    void visitClass(Classc<?> clazz);

    void visitMethod(Methodc<?, ?> method);

    void visitField(Fieldc<?> field);

    void visitLocal(Localc<?> local);

    void visitInvoke(Invokec<?> invoke);

    void visitGetField(GetFieldc<?, ?> getField);

    void visitPutField(PutFieldc<?, ?> putField);

    void visitLocalSet(LocalAssignc<?, ?> localSet);

    void visitOperate(Operatec<?> operate);

    void visitCast(Castc cast);

    void visitGoto(Gotoc iGoto);

    void visitLabel(MarkLabelc label);

    void visitCompare(Comparec<?> compare);

    void visitCodeBlock(CodeBlockc<?> codeBlock);

    void visitReturn(Returnc<?> returnc);

    void visitInstanceOf(InstanceOfc instanceOf);

    void visitNewInstance(NewInstancec<?> newInstance);

    void visitOddOperate(OddOperatec<?> operate);

    void visitConstant(LoadConstantc<?> loadConstant);

    void visitNewArray(NewArrayc<?> newArray);

    void visitCondition(Conditionc condition);

    void visitArrayGet(ArrayGetc<?> arrayGet);

    void visitArrayPut(ArrayPutc<?> arrayPut);

    void visitSwitch(Switchc<?> zwitch);

    void visitThrow(Throwc<?> thr);
}
