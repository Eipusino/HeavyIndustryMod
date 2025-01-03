package heavyindustry.android.classes;

import com.android.dx.dex.file.*;
import com.android.dx.rop.code.*;
import com.android.dx.rop.cst.*;
import com.android.dx.rop.type.Type;
import com.android.dx.rop.type.*;
import dynamilize.classmaker.*;
import dynamilize.classmaker.code.*;
import heavyindustry.android.classes.dexmaker.*;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

public class DexGeneratorCoding extends AbstractClassGenerator {
    private final ByteClassLoader loader;

    private final HashMap<String, RegisterSpec> localMap = new HashMap<>();
    private final ArrayList<RegisterSpec> localList = new ArrayList<>();
    private final LinkedList<RegisterSpec> tmpDoubleRegister = new LinkedList<>();
    private final LinkedList<RegisterSpec> tmpIntLikeRegister = new LinkedList<>();
    private final LinkedList<RegisterSpec> tmpRefRegister = new LinkedList<>();

    DexFile dexFile;
    DexClassInfo currClass;
    DexFieldInfo currField;
    DexMethodInfo currMethod;
    Classc<?> generating;
    BlockHead currBlock;

    private SourcePosition position;
    private int regOff;

    public DexGeneratorCoding(ByteClassLoader classLoader) {
        loader = classLoader;
    }

    @Override
    public byte[] genByteCode(ClassInfo<?> classInfo) {
        dexFile.add(currClass.toItem());
        try {
            return dexFile.toDex(null, false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected <T> Class<T> generateClass(ClassInfo<T> classInfo) {
        try {
            return (Class<T>) loader.loadClass(classInfo.name(), false);
        } catch (ClassNotFoundException e) {
            loader.declareClass(classInfo.name(), genByteCode(classInfo));
            try {
                return (Class<T>) loader.loadClass(classInfo.name(), false);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void visitClass(Classc<?> clazz) {
        generating = clazz;
        currClass = new DexClassInfo(clazz);
        position = new SourcePosition(null, -1, -1);

        super.visitClass(clazz);
    }

    @Override
    public void visitMethod(Methodc<?, ?> method) {
        currClass.addMethod(currMethod = new DexMethodInfo(method));
        currBlock = new BlockHead();
        currMethod.addBlock(currBlock);

        super.visitMethod(method);
    }

    @Override
    public void visitLocal(Localc<?> local) {
        Type type = Type.intern(local.type().internalName());
        RegisterSpec spec;
        localMap.put(local.name(), spec = RegisterSpec.make(regOff, type));
        localList.add(spec);
        if (type.getCategory() == 2) localList.add(spec);
        regOff += type.getCategory();

        super.visitLocal(local);
    }

    @Override
    public void visitCodeBlock(CodeBlockc<?> block) {
        super.visitCodeBlock(block);
    }

    @Override
    public void visitField(Fieldc<?> field) {
        currClass.addField(currField = new DexFieldInfo(field));
    }

    @Override
    public void visitInvoke(Invokec<?> invoke) {
        Rop opcode;
        Prototype meth = Prototype.intern(invoke.method().typeDescription());

        if (Modifier.isStatic(invoke.method().modifiers())) {
            opcode = Rops.opInvokeStatic(meth);
        } else if (invoke.method().name().equals("<init>") || Modifier.isPrivate(invoke.method().modifiers())) {
            opcode = Rops.opInvokeDirect(meth);
        } else if (invoke.callSuper()) {
            opcode = Rops.opInvokeSuper(meth);
        } else if (Modifier.isInterface(invoke.method().owner().modifiers())) {
            opcode = Rops.opInvokeInterface(meth);
        } else opcode = Rops.opInvokeVirtual(meth);

        List<RegisterSpec> args = new ArrayList<>();
        if (!Modifier.isStatic(invoke.method().modifiers())) args.add(localMap.get(invoke.target().name()));
        args.addAll(Arrays.asList(invoke.args().stream().map(e -> localMap.get(e.name())).toArray(RegisterSpec[]::new)));

        if (args.size() >= 5) {
            int tempOff = 0;

            for (int i = 0; i < args.size(); i++) {
                currBlock.addInsn(new PlainInsn(
                        Rops.opMove(localList.get(tempOff).getType()),
                        position,
                        RegisterSpec.make(localList.size() + tempOff, localList.get(tempOff).getType()),
                        makeRegList(localList.get(tempOff))
                ));
            }

        }

        currBlock.addInsn(new ThrowingCstInsn(
                opcode,
                position,
                makeRegList(args.toArray(new RegisterSpec[0])),
                new StdTypeList(0),
                new CstMethodRef(
                        new CstType(Type.intern(invoke.method().owner().realName())),
                        new CstNat(
                                new CstString(invoke.method().name()),
                                new CstString(invoke.method().typeDescription())
                        )
                )
        ));
    }

    private RegisterSpecList makeRegList(RegisterSpec... regs) {
        RegisterSpecList list = new RegisterSpecList(regs.length);
        for (int i = 0; i < regs.length; i++) {
            list.set(i, regs[i]);
        }
        return list;
    }

    @Override
    public void visitGetField(GetFieldc<?, ?> getField) {

    }

    @Override
    public void visitPutField(PutFieldc<?, ?> putField) {

    }

    @Override
    public void visitLocalSet(LocalAssignc<?, ?> localSet) {

    }

    @Override
    public void visitOperate(Operatec<?> operate) {

    }

    @Override
    public void visitCast(Castc cast) {

    }

    @Override
    public void visitGoto(Gotoc iGoto) {

    }

    @Override
    public void visitLabel(MarkLabelc label) {

    }

    @Override
    public void visitCompare(Comparec<?> compare) {

    }

    @Override
    public void visitReturn(Returnc<?> iReturn) {

    }

    @Override
    public void visitInstanceOf(InstanceOfc instanceOf) {

    }

    @Override
    public void visitNewInstance(NewInstancec<?> newInstance) {

    }

    @Override
    public void visitOddOperate(OddOperatec<?> operate) {

    }

    @Override
    public void visitConstant(LoadConstantc<?> loadConstant) {

    }

    @Override
    public void visitNewArray(NewArrayc<?> newArray) {

    }

    @Override
    public void visitCondition(Conditionc condition) {

    }

    @Override
    public void visitArrayGet(ArrayGetc<?> arrayGet) {

    }

    @Override
    public void visitArrayPut(ArrayPutc<?> arrayPut) {

    }

    @Override
    public void visitSwitch(Switchc<?> zwitch) {

    }

    @Override
    public void visitThrow(Throwc<?> thr) {

    }
}
