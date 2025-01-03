package heavyindustry.android.classes;

import com.android.dx.cf.direct.*;
import com.android.dx.command.dexer.*;
import com.android.dx.dex.*;
import com.android.dx.dex.cf.*;
import com.android.dx.dex.file.*;
import dynamilize.classmaker.*;
import dynamilize.classmaker.code.*;
import heavyindustry.util.handler.*;
import org.objectweb.asm.*;

import static dynamilize.classmaker.ClassInfo.*;

public class DexGenerator extends ASMGenerator {
    private static final ClassInfo<StringBuilder> BUILDER_TYPE = ClassInfo.asType(StringBuilder.class);
    private static final Methodc<StringBuilder, String> TO_STRING = BUILDER_TYPE.getMethod(STRING_TYPE, "toString");

    public DexGenerator(ByteClassLoader classLoader) {
        super(classLoader, Opcodes.V1_8);
    }

    @Override
    public byte[] genByteCode(ClassInfo<?> classInfo) {
        byte[] byteCode = super.genByteCode(classInfo);

        DexOptions dexOptions = new DexOptions();
        DexFile dexFile = new DexFile(dexOptions);
        DirectClassFile classFile = MethodHandler.newInstanceDefault(
                DirectClassFile.class,
                byteCode,
                classInfo.internalName() + ".class"
        );
        MethodHandler.invokeDefault(classFile, "setAttributeFactory");
        classFile.getMagic();
        DxContext context = new DxContext();

        dexFile.add(MethodHandler.invokeDefault(CfTranslator.class, "translate",
                context,
                classFile,
                new CfOptions(),
                dexOptions,
                dexFile)
        );

        return MethodHandler.invokeDefault(dexFile, "toDex");
    }

    @Override
    public void visitOperate(Operatec<?> operate) {
        if (operate.leftOpNumber().type() == STRING_TYPE || operate.rightOpNumber().type() == STRING_TYPE) {
            int leftInd = operate.leftOpNumber() instanceof CodeBlock.StackElem ?
                    localIndex.computeIfAbsent("$leftCache$", e -> localIndex.size()) :
                    localIndex.get(operate.leftOpNumber().name());

            int rightInd = operate.rightOpNumber() instanceof CodeBlock.StackElem ?
                    localIndex.computeIfAbsent("$rightCache$", e -> localIndex.size()) :
                    localIndex.get(operate.rightOpNumber().name());

            if (operate.leftOpNumber() instanceof CodeBlock.StackElem<?>) {
                methodVisitor.visitVarInsn(
                        getStoreType(STRING_TYPE),
                        leftInd
                );
            }
            if (operate.rightOpNumber() instanceof CodeBlock.StackElem<?>) {
                methodVisitor.visitVarInsn(
                        getStoreType(STRING_TYPE),
                        rightInd
                );
            }

            Methodc<StringBuilder, StringBuilder> left, right;
            left = BUILDER_TYPE.getMethod(BUILDER_TYPE, "append", operate.leftOpNumber().type());
            right = BUILDER_TYPE.getMethod(BUILDER_TYPE, "append", operate.rightOpNumber().type());

            methodVisitor.visitTypeInsn(NEW, BUILDER_TYPE.internalName());
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitMethodInsn(
                    INVOKESPECIAL,
                    BUILDER_TYPE.internalName(),
                    "<init>",
                    BUILDER_TYPE.getConstructor().typeDescription(),
                    false
            );

            methodVisitor.visitVarInsn(
                    getLoadType(operate.leftOpNumber().type()),
                    leftInd
            );
            methodVisitor.visitMethodInsn(
                    INVOKEVIRTUAL,
                    BUILDER_TYPE.internalName(),
                    "append",
                    left.typeDescription(),
                    false
            );
            methodVisitor.visitVarInsn(
                    getLoadType(operate.rightOpNumber().type()),
                    rightInd
            );
            methodVisitor.visitMethodInsn(
                    INVOKEVIRTUAL,
                    BUILDER_TYPE.internalName(),
                    "append",
                    right.typeDescription(),
                    false
            );
            methodVisitor.visitMethodInsn(
                    INVOKEVIRTUAL,
                    BUILDER_TYPE.internalName(),
                    "toString",
                    TO_STRING.typeDescription(),
                    false
            );
            methodVisitor.visitVarInsn(ASTORE, localIndex.get(operate.resultTo().name()));
        } else super.visitOperate(operate);
    }
}
