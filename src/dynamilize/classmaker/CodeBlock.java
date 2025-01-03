package dynamilize.classmaker;

import dynamilize.*;
import dynamilize.classmaker.code.*;

import java.lang.reflect.*;
import java.util.*;

import static dynamilize.classmaker.ClassInfo.*;

/**
 * A code block object that describes the specific behavior of a method,
 * which is decomposed into a linear structure of {@linkplain Element basic behavior} concatenated,
 * providing a fast way to add behavior.
 */
@SuppressWarnings("unchecked")
public class CodeBlock<R> implements CodeBlockc<R> {
    public static class StackElem<T> implements Localc<T> {
        private static final HashMap<Classc<?>, StackElem<?>> caching = new HashMap<>();

        private final Classc<T> type;

        private StackElem(Classc<T> type) {
            this.type = type;
        }

        public static <T> StackElem<T> get(Classc<T> type) {
            return (StackElem<T>) caching.computeIfAbsent(type, StackElem::new);
        }

        @Override
        public String name() {
            return "<stack>";
        }

        @Override
        public int modifiers() {
            return 0;
        }

        @Override
        public Classc<T> type() {
            return type;
        }

        @Override
        public Object initial() {
            return null;
        }
    }

    public static <T> Localc<T> stack(Classc<T> type) {
        return StackElem.get(type);
    }

    protected final ArrayList<Element> statements = new ArrayList<>();

    protected final ArrayList<Localc<?>> parameter = new ArrayList<>();
    Local<?> selfPointer;

    final List<Label> labelList = new ArrayList<>();
    final Methodc<?, R> method;

    public CodeBlock(Methodc<?, R> method) {
        this.method = method;
    }

    protected void initParams(Classc<?> self, List<Parameterf<?>> params) {
        if (!Modifier.isStatic(method.modifiers())) {
            selfPointer = new Local<>("this", Modifier.FINAL, self);
            parameter.add(selfPointer);
        }

        for (Parameterf<?> param: params) {
            parameter.add(
                    new Local<>(param.name(), param.modifiers(), param.getType())
            );
        }
    }

    public Methodc<?, R> owner() {
        return method;
    }

    @Override
    public List<Element> codes() {
        return statements;
    }

    @Override
    public List<Localc<?>> getParamList() {
        return selfPointer == null ? new ArrayList<>(parameter) : parameter.subList(1, parameter.size());
    }

    public List<Localc<?>> getParamAll() {
        return new ArrayList<>(parameter);
    }

    @Override
    public int modifiers() {
        return 0;
    }

    //*=============*//
    //* utilMethods *//
    //*=============*//
    private static final String VAR_DEFAULT = "var&";

    private int defVarCount = 0;

    public <T> Localc<T> local(Classc<T> type, String name, int flags) {
        Localc<T> res = new Local<>(name, flags, type);
        codes().add(res);
        return res;
    }

    public <T> Localc<T> local(Classc<T> type, int flags) {
        return local(type, VAR_DEFAULT + defVarCount++, flags);
    }

    public <T> Localc<T> local(Classc<T> type) {
        return local(type, VAR_DEFAULT + defVarCount++, 0);
    }

    /**
     * Get the local variable of the method's parameters,
     * index the position of this parameterf in the parameterf list,
     * and for non-static methods, index 0 is the 'this' pointer.
     * @param index The position of this parameterf in the formal parameterf list is the 'this' pointer at the 0 index of the non static method.
     * @param <T> Type of parameterf.
     */
    public <T> Localc<T> getParam(int index) {
        return (Localc<T>) parameter.get(index);
    }

    /**
     * Get the local variable of the method's parameters,
     * index the position of this parameterf in the parameterf list, different from {@link CodeBlock#getParam(int)},
     * where index 0 is the first formal parameterf instead of this.
     * @param index The position of this parameterf in the formal parameterf list.
     * @param <T> Type of parameterf.
     */
    public <T> Localc<T> getRealParam(int index) {
        return (Localc<T>) parameter.get(selfPointer != null? index + 1: index);
    }

    @Override
    public List<Label> labelList() {
        return labelList;
    }

    public <T> Localc<T> getThis() {
        if (selfPointer == null)
            throw new IllegalHandleException("static method no \"this\" pointer");

        return (Localc<T>) selfPointer;
    }

    public final <S, T extends S> void assignField(Localc<?> target, Fieldc<S> src, Fieldc<T> tar) {
        Localc<S> srcTemp = local(src.type());
        assign(target, src, srcTemp);
        assign(target, srcTemp, tar);
    }

    public final <S, T extends S> void assign(Localc<S> src, Localc<T> tar) {
        codes().add(new LocalAssign<>(src, tar));
    }

    public final <S, T extends S> void assign(Localc<?> tar, Fieldc<S> src, Localc<T> to) {
        codes().add(new GetField<>(tar, src, to));
    }

    /**
     * Assign the specified property of the target object to the value of the given local variable.
     * @param tar Save the local variables of the target object for the field to be set.
     * @param src Set the source local variable of the value.
     * @param to Fields that need to be written with values.
     */
    public final <S, T extends S> void assign(Localc<?> tar, Localc<S> src, Fieldc<T> to) {
        codes().add(new PutField<>(tar, src, to));
    }

    public final <S, T extends S> void assign(Localc<?> tar, String src, Localc<T> to) {
        codes().add(new GetField<>(tar, tar.type().getField(to.type(), src), to));
    }

    public final <S> void assign(Localc<?> tar, Localc<S> src, String to) {
        codes().add(new PutField<>(tar, src, tar.type().getField(src.type(), to)));
    }

    public final <S, T extends S> void assignStatic(Classc<?> clazz, String src, Localc<T> to) {
        codes().add(new GetField<>(null, clazz.getField(to.type(), src), to));
    }

    public final <S, T extends S> void assignStatic(Fieldc<S> src, Localc<T> to) {
        codes().add(new GetField<>(null, src, to));
    }

    public final <S> void assignStatic(Classc<?> clazz, Localc<S> src, String to) {
        codes().add(new PutField<>(null, src, clazz.getField(src.type(), to)));
    }

    public final <S> void assignStatic(Localc<S> src, Fieldc<? extends S> to) {
        codes().add(new PutField<>(null, src, to));
    }

    public final <U> void invoke(Localc<?> target, Methodc<?, U> method, Localc<U> returnTo, Localc<?>... args) {
        codes().add(new Invoke<>(target, false, method, returnTo, args));
    }

    public final <U> void invoke(Localc<?> target, String method, Localc<U> returnTo, Localc<?>... args) {
        codes().add(new Invoke<>(target, false, target.type().getMethod(returnTo.type(), method, Arrays.stream(args).map(Localc::type).toArray(Classc<?>[]::new)), returnTo, args));
    }

    public final <U> void invokeStatic(Methodc<?, U> method, Localc<? super U> returnTo, Localc<?>... args) {
        codes().add(new Invoke<>(null, false, method, returnTo, args));
    }

    public final <U> void invokeStatic(Classc<?> target, String method, Localc<U> returnTo, Localc<?>... args) {
        codes().add(new Invoke<>(null, false, target.getMethod(returnTo.type(), method, Arrays.stream(args).map(Localc::type).toArray(Classc<?>[]::new)), returnTo, args));
    }

    public final <U> void invokeSuper(Localc<?> target, Methodc<?, U> method, Localc<? super U> returnTo, Localc<?>... args) {
        codes().add(new Invoke<>(target, true, method, returnTo, args));
    }

    public final <U> void invokeSuper(Localc<?> target, String method, Localc<U> returnTo, Localc<?>... args) {
        codes().add(new Invoke<>(target, true, target.type().getMethod(returnTo.type(), method, Arrays.stream(args).map(Localc::type).toArray(Classc<?>[]::new)), returnTo, args));
    }

    public final <T extends R> void returnValue(Localc<T> local) {
        codes().add(new Return<>(local));
    }

    public final void returnVoid() {
        codes().add(new Return<>(null));
    }

    public final <T> void operate(OddOperatec.OddOperator opCode, Localc<T> opNumb, Localc<T> to) {
        codes().add(new OddOperate<>(opCode, opNumb, to));
    }

    public final <T> void operate(String symbol, Localc<T> opNumb, Localc<T> to) {
        codes().add(new OddOperate<>(OddOperatec.OddOperator.as(symbol), opNumb, to));
    }

    public final <T> void operate(Localc<T> lefOP, Operatec.OPCode opCode, Localc<T> rigOP, Localc<?> to) {
        codes().add(new Operate<>(opCode, lefOP, rigOP, to));
    }

    public final <T> void operate(Localc<T> lefOP, String symbol, Localc<T> rigOP, Localc<?> to) {
        codes().add(new Operate<>(Operatec.OPCode.as(symbol), lefOP, rigOP, to));
    }

    public final Label label() {
        Label l = new Label();
        labelList.add(l);

        return l;
    }

    public final void markLabel(Label label) {
        codes().add(new MarkLabel(label));
    }

    public final void jump(Label label) {
        codes().add(new Goto(label));
    }

    public final <T> void compare(Localc<T> lef, Comparec.Comparison opc, Localc<T> rig, Label ifJump) {
        codes().add(new Compare<>(lef, rig, ifJump, opc));
    }

    public final <T> void compare(Localc<T> lef, String symbol, Localc<T> rig, Label ifJump) {
        compare(lef, Comparec.Comparison.as(symbol), rig, ifJump);
    }

    public final <T> void condition(Localc<T> target, String symbol, Label ifJump) {
        condition(target, Conditionc.CondCode.as(symbol), ifJump);
    }

    public final <T> void condition(Localc<T> target, Conditionc.CondCode condCode, Label ifJump) {
        codes().add(new Condition(target, condCode, ifJump));
    }

    public final void cast(Localc<?> source, Localc<?> target) {
        codes().add(new Cast(source, target));
    }

    public final void instanceOf(Localc<?> target, Classc<?> type, Localc<Boolean> result) {
        codes().add(new InstanceOf(target, type, result));
    }

    public final  <T> void newInstance(Methodc<T, Void> constructor, Localc<? extends T> resultTo, Localc<?>... params) {
        codes().add(new NewInstance<>(constructor, resultTo, params));
    }

    @SafeVarargs
    public final <T> void newArray(Classc<T> arrElemType, Localc<?> storeTo, Localc<Integer>... length) {
        codes().add(
                new NewArray<>(arrElemType, Arrays.asList(length), storeTo)
        );
    }

    public final <T> void arrayPut(Localc<T[]> array, Localc<Integer> index, Localc<T> value) {
        codes().add(new ArrayPut<>(array, index, value));
    }

    public final <T> void arrayGet(Localc<T[]> array, Localc<Integer> index, Localc<T> getTo) {
        codes().add(new ArrayGet<>(array, index, getTo));
    }

    public final <T> void loadConstant(Localc<T> tar, T constant) {
        codes().add(
                new LoadConstant<>(constant, tar)
        );
    }

    public final <T extends Comparable<?>> Switchc<T> switchCase(Localc<T> target, Label end, Object... casePairs) {
        Switch<T> swi;
        codes().add(swi = new Switch<>(target, end, casePairs));

        return swi;
    }

    public final <T extends Comparable<?>> Switchc<T> switchDef(Localc<T> target, Label end) {
        Switch<T> swi;
        codes().add(swi = new Switch<>(target, end));

        return swi;
    }

    public final <T extends Throwable> void thr(Localc<T> throwable) {
        codes().add(new Throw<>(throwable));
    }

    //*===============*//
    //*memberCodeTypes*//
    //*===============*//
    protected static class Local<T> implements Localc<T> {
        final String name;
        final int modifiers;
        final Classc<T> type;

        Object initial;

        public Local(String name, int modifiers, Classc<T> type) {
            this.name = name;
            this.modifiers = modifiers;
            this.type = type;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public int modifiers() {
            return modifiers;
        }

        @Override
        public Classc<T> type() {
            return type;
        }

        @Override
        public Object initial() {
            return initial;
        }
    }

    protected static class Operate<T> implements Operatec<T> {
        final OPCode opc;

        final Localc<T> leftOP;
        final Localc<T> rightOP;
        final Localc<?> result;

        public Operate(OPCode opc, Localc<T> leftOP, Localc<T> rightOP, Localc<?> result) {
            checkStack(this, leftOP, rightOP);

            this.opc = opc;
            this.leftOP = leftOP;
            this.rightOP = rightOP;
            this.result = result;
        }

        @Override
        public OPCode opCode() {
            return opc;
        }

        @Override
        public Localc<?> resultTo() {
            return result;
        }

        @Override
        public Localc<T> leftOpNumber() {
            return leftOP;
        }

        @Override
        public Localc<T> rightOpNumber() {
            return rightOP;
        }
    }

    protected static class OddOperate<T> implements OddOperatec<T> {
        final Localc<T> opNumb;
        final Localc<T> retTo;

        final OddOperator opCode;

        public OddOperate(OddOperator opCode, Localc<T> opNumb, Localc<T> retTo) {
            this.opNumb = opNumb;
            this.retTo = retTo;
            this.opCode = opCode;
        }

        @Override
        public Localc<T> operateNumber() {
            return opNumb;
        }

        @Override
        public Localc<T> resultTo() {
            return retTo;
        }

        @Override
        public OddOperator opCode() {
            return opCode;
        }
    }

    protected static class LocalAssign<S, T extends S> implements LocalAssignc<S, T> {
        final Localc<S> src;
        final Localc<T> tar;

        public LocalAssign(Localc<S> src, Localc<T> tar) {
            this.src = src;
            this.tar = tar;
        }

        @Override
        public Localc<S> source() {
            return src;
        }

        @Override
        public Localc<T> target() {
            return tar;
        }
    }

    protected static class PutField<S, T extends S> implements PutFieldc<S, T> {
        final Localc<?> inst;
        final Localc<S> source;
        final Fieldc<T> target;

        public PutField(Localc<?> inst, Localc<S> source, Fieldc<T> target) {
            checkStack(this, inst, source);

            this.inst = inst;
            this.source = source;
            this.target = target;
        }

        @Override
        public Localc<?> inst() {
            return inst;
        }

        @Override
        public Localc<S> source() {
            return source;
        }

        @Override
        public Fieldc<T> target() {
            return target;
        }
    }

    protected static class GetField<S, T extends S> implements GetFieldc<S, T> {
        final Localc<?> inst;
        final Fieldc<S> source;
        final Localc<T> target;

        public GetField(Localc<?> inst, Fieldc<S> source, Localc<T> target) {
            this.inst = inst;
            this.source = source;
            this.target = target;
        }

        @Override
        public Localc<?> inst() {
            return inst;
        }

        @Override
        public Fieldc<S> source() {
            return source;
        }

        @Override
        public Localc<T> target() {
            return target;
        }
    }

    protected static class Goto implements Gotoc {
        final Label label;

        public Goto(Label label) {
            this.label = label;
        }

        @Override
        public Label target() {
            return label;
        }
    }

    protected static class Invoke<R> implements Invokec<R> {
        final Methodc<?, R> method;
        final Localc<? super R> returnTo;
        final List<Localc<?>> args;
        final Localc<?> target;

        final boolean callSuper;

        public Invoke(Localc<?> target, boolean callSuper, Methodc<?, R> method, Localc<? super R> returnTo, Localc<?>... args) {
            checkStack(this, args);
            if (args.length >= 1) checkStack(this, target, args[0]);

            this.method = method;
            this.returnTo = method.returnType() != VOID_TYPE ? returnTo: null;
            this.target = target;
            this.args = Arrays.asList(args);
            this.callSuper = callSuper;

            if (callSuper && !method.owner().isAssignableFrom(target.type()))
                throw new IllegalHandleException("cannot call super method in non-super class");
        }

        @Override
        public Localc<?> target() {
            return target;
        }

        @Override
        public Methodc<?, R> method() {
            return method;
        }

        @Override
        public List<Localc<?>> args() {
            return args;
        }

        @Override
        public Localc<? super R> returnTo() {
            return returnTo;
        }

        @Override
        public boolean callSuper() {
            return callSuper;
        }
    }

    protected static class Compare<T> implements Comparec<T> {
        final Localc<T> left;
        final Localc<T> right;

        final Label jumpTo;

        final Comparison comparison;

        public Compare(Localc<T> left, Localc<T> right, Label jumpTo, Comparison comparison) {
            checkStack(this, left, right);

            this.left = left;
            this.right = right;
            this.jumpTo = jumpTo;
            this.comparison = comparison;
        }

        @Override
        public Localc<T> leftNumber() {
            return left;
        }

        @Override
        public Localc<T> rightNumber() {
            return right;
        }

        @Override
        public Label ifJump() {
            return jumpTo;
        }

        @Override
        public Comparison comparison() {
            return comparison;
        }
    }

    protected static class Cast implements Castc {
        final Localc<?> src;
        final Localc<?> tar;

        public Cast(Localc<?> src, Localc<?> tar) {
            this.src = src;
            this.tar = tar;
        }

        @Override
        public Localc<?> source() {
            return src;
        }

        @Override
        public Localc<?> target() {
            return tar;
        }
    }

    protected static class Return<R> implements Returnc<R> {
        final Localc<R> local;

        public Return(Localc<R> local) {
            this.local = local;
        }

        @Override
        public Localc<R> returnValue() {
            return local;
        }
    }

    protected static class InstanceOf implements InstanceOfc {
        final Localc<?> target;
        final Classc<?> type;
        final Localc<Boolean> result;

        public InstanceOf(Localc<?> target, Classc<?> type, Localc<Boolean> result) {
            this.target = target;
            this.type = type;
            this.result = result;
        }

        @Override
        public Localc<?> target() {
            return null;
        }

        @Override
        public Classc<?> type() {
            return null;
        }

        @Override
        public Localc<Boolean> result() {
            return null;
        }
    }

    protected static class NewInstance<T> implements NewInstancec<T> {
        final Methodc<T, Void> constructor;
        final Localc<? extends T> resultTo;

        final Classc<T> type;

        final List<Localc<?>> params;

        public NewInstance(Methodc<T, Void> constructor, Localc<? extends T> resultTo, Localc<?>... params) {
            checkStack(this, params);

            this.constructor = constructor;
            this.type = constructor.owner();
            this.resultTo = resultTo;
            this.params = Arrays.asList(params);
        }

        @Override
        public Methodc<T, Void> constructor() {
            return constructor;
        }

        @Override
        public Classc<T> type() {
            return type;
        }

        @Override
        public Localc<? extends T> instanceTo() {
            return resultTo;
        }

        @Override
        public List<Localc<?>> params() {
            return params;
        }
    }

    protected static class NewArray<T> implements NewArrayc<T> {
        final Classc<T> arrCompType;
        final List<Localc<Integer>> arrayLength;
        final Localc<?> retTo;

        public NewArray(Classc<T> arrCompType, List<Localc<Integer>> arrayLength, Localc<?> retTo) {
            checkStack(this, arrayLength.toArray(new Localc[0]));

            this.arrCompType = arrCompType;
            this.arrayLength = arrayLength;
            this.retTo = retTo;
        }

        @Override
        public Classc<T> arrayEleType() {
            return arrCompType;
        }

        @Override
        public List<Localc<Integer>> arrayLength() {
            return arrayLength;
        }

        @Override
        public Localc<?> resultTo() {
            return retTo;
        }
    }

    public static class ArrayPut<T> implements ArrayPutc<T> {
        final Localc<T[]> array;
        final Localc<Integer> index;
        final Localc<T> value;

        public ArrayPut(Localc<T[]> array, Localc<Integer> index, Localc<T> value) {
            checkStack(array, index, value);

            this.array = array;
            this.index = index;
            this.value = value;
        }

        @Override
        public Localc<T[]> array() {
            return array;
        }

        @Override
        public Localc<Integer> index() {
            return index;
        }

        @Override
        public Localc<T> value() {
            return value;
        }
    }

    public static class ArrayGet<T> implements ArrayGetc<T> {
        final Localc<T[]> array;
        final Localc<Integer> index;
        final Localc<T> getTo;

        public ArrayGet(Localc<T[]> array, Localc<Integer> index, Localc<T> getTo) {
            checkStack(array, index);

            this.array = array;
            this.index = index;
            this.getTo = getTo;
        }

        @Override
        public Localc<T[]> array() {
            return array;
        }

        @Override
        public Localc<Integer> index() {
            return index;
        }

        @Override
        public Localc<T> getTo() {
            return getTo;
        }
    }

    protected static class LoadConstant<T> implements LoadConstantc<T> {
        final T constant;
        final Localc<T> resTo;

        public LoadConstant(T constant, Localc<T> resTo) {
            this.constant = constant;
            this.resTo = resTo;
        }

        @Override
        public T constant() {
            return constant;
        }

        @Override
        public Localc<T> constTo() {
            return resTo;
        }
    }

    protected static class MarkLabel implements MarkLabelc {
        final Label label;

        public MarkLabel(Label label) {
            this.label = label;
        }

        @Override
        public Label label() {
            return label;
        }
    }

    protected static class Condition implements Conditionc {
        final Localc<?> condition;
        final CondCode condCode;
        final Label ifJump;

        public Condition(Localc<?> condition, CondCode condCode, Label ifJump) {
            this.condition = condition;
            this.condCode = condCode;
            this.ifJump = ifJump;
        }

        @Override
        public CondCode condCode() {
            return condCode;
        }

        @Override
        public Localc<?> condition() {
            return condition;
        }

        @Override
        public Label ifJump() {
            return ifJump;
        }
    }

    protected static class Switch<T> implements Switchc<T> {
        final Localc<T> target;
        final Map<T, Label> casesMap;

        final Label end;

        boolean isTable;

        public Switch(Localc<T> target, Label end, Object... pairs) {
            this.target = target;
            this.casesMap = new TreeMap<>((a, b) -> a.hashCode() - b.hashCode());

            this.end = end;

            for (int i = 0; i < pairs.length; i += 2) {
                casesMap.put((T) pairs[i], (Label) pairs[i + 1]);
            }

            checkTable();
        }

        public Switch(Localc<T> target, Label end) {
            this.target = target;
            this.casesMap = new TreeMap<>();
            this.end = end;
        }

        @Override
        public boolean isTable() {
            return isTable;
        }

        @Override
        public Label end() {
            return end;
        }

        @Override
        public Localc<T> target() {
            return target;
        }

        @Override
        public Map<T, Label> cases() {
            return casesMap;
        }

        @Override
        public void addCase(T caseKey, Label caseJump) {
            casesMap.put(caseKey, caseJump);

            checkTable();
        }

        protected void checkTable() {
            if (target.type() == BOOLEAN_TYPE || (!target.type().isPrimitive()
                    && !asType(Enum.class).isAssignableFrom(target.type())
                    && target.type() != STRING_TYPE))
                throw new IllegalHandleException("unsupported type error");

            if (target.type() == LONG_TYPE || target.type() == INT_TYPE
                    || target.type() == SHORT_TYPE || target.type() == BYTE_TYPE
                    || target.type() == CHAR_TYPE || target.type() instanceof Enum<?>) {
                int labelsNumber = casesMap.size();

                int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
                for (T t: casesMap.keySet()) {
                    if (t instanceof Number n) {
                        max = Math.max(max, n.intValue());
                        min = Math.min(min, n.intValue());
                    } else if (t instanceof Character c) {
                        max = Math.max(max, c);
                        min = Math.min(min, c);
                    } else if (t instanceof Enum<?> e) {
                        max = Math.max(max, e.ordinal());
                        min = Math.min(min, e.ordinal());
                    }
                }

                int tableSpaceCost = 4 + max - min + 1;
                int tableTimeCost = 3;
                int lookupSpaceCost = 3 + 2*labelsNumber;

                isTable = labelsNumber > 0 && tableSpaceCost + 3*tableTimeCost <= lookupSpaceCost + 3 * labelsNumber;
            } else isTable = false;
        }
    }

    protected static class Throw<T extends Throwable> implements Throwc<T> {
        final Localc<T> thr;

        public Throw(Localc<T> thr) {
            this.thr = thr;
        }

        @Override
        public Localc<T> thr() {
            return thr;
        }
    }

    private static void checkStack(Element elem, Localc<?>... accesses) {
        boolean accessLocal = false;
        for (Localc<?> access : accesses) {
            if (access == null) continue;

            if (access instanceof StackElem) {
                if (accessLocal) throw new IllegalHandleException("Bad stack access, element: " + elem);
            } else accessLocal = true;
        }
    }
}
