package dynamilize;

import java.util.*;

/**
 * The encapsulation object of the actual parameter list records an actual parameter list and provides a
 * generic method for obtaining parameters to reduce redundant type conversions required when
 * referencing functions.
 * <p>The parameter table object is a reusable object. After referencing, please use {@link ArgumentList#recycle()} to recycle the
 * object to reduce the frequency of heap memory updates.
 */
@SuppressWarnings("unchecked")
public class ArgumentList {
    public static int MAX_INSTANCE_STACK = 2048;

    public static final Stack<Object[]>[] ARG_LEN_MAP = new Stack[64];
    public static final Object[] EMP_ARG = new Object[0];

    static {
        for (int i = 1; i < ARG_LEN_MAP.length; i++) {
            ARG_LEN_MAP[i] = new Stack<>();
        }
    }

    private static final Stack<ArgumentList> INSTANCES = new Stack<>();

    private Object[] args;
    private FunctionType type;

    /** Private constructor, not allowed for direct external use. */
    private ArgumentList() {}

    public static synchronized Object[] getList(int len) {
        if (len == 0) return EMP_ARG;

        Stack<Object[]> stack = ARG_LEN_MAP[len];
        return stack.isEmpty() ? new Object[len] : stack.pop();
    }

    public static void recycleList(Object[] list) {
        if (list.length == 0) return;

        Stack<Object[]> stack = ARG_LEN_MAP[list.length];
        if (stack.size() > MAX_INSTANCE_STACK) return;
        stack.push(list);
    }

    /**
     * Use a set of actual parameter lists to obtain an encapsulated parameter list, which is first popped out of the instance stack.
     * If there are no instances in the stack, a new one will be constructed.
     * If the parameter contains null, it is recommended to use {@link ArgumentList#asWithType(FunctionType, Object...)} to obtain the parameter list.
     * Execution with explicitly specified parameters can have higher efficiency.
     * @param args Actual parameter list.
     * @return Encapsulate parameter objects.
     */
    public static synchronized ArgumentList as(Object... args) {
        ArgumentList res = INSTANCES.isEmpty() ? new ArgumentList() : INSTANCES.pop();
        res.args = args;
        res.type = FunctionType.inst(args);

        return res;
    }

    public static synchronized ArgumentList asWithType(FunctionType type, Object... args) {
        ArgumentList res = INSTANCES.isEmpty() ? new ArgumentList() : INSTANCES.pop();
        res.args = args;
        res.type = type;

        return res;
    }

    /**
     * Recycle the instance and push it back onto the stack.
     * If the stack has reached its maximum capacity, it will not continue to be inserted into the instance stack.
     */
    public void recycle() {
        args = null;
        type = null;
        if (INSTANCES.size() >= MAX_INSTANCE_STACK) return;

        INSTANCES.add(this);
    }

    /**
     * Retrieve the actual parameter value at the given index, infer the type based on the reference, and throw a type conversion exception if the type is unavailable.
     * @param index The index position of the parameter in the list.
     * @param <T> Parameterf types can usually be inferred based on references.
     * @return The value of the actual parameter.
     * @throws ClassCastException If the recipient's required type and parameter type are not assignable.
     */
    public <T> T get(int index) {
        return (T) args[index];
    }

    /**
     * Get an array of actual parameter lists.
     * @return Array composed of actual parameters.
     */
    public Object[] args() {
        return args;
    }

    /**
     * Get formal parameter type encapsulated object.
     * @return The formal parameter type of this parameter list.
     */
    public FunctionType type() {
        return type;
    }

    @Override
    public String toString() {
        String arg = Arrays.toString(args);
        return "(" + arg.substring(1, arg.length() - 1) + ")";
    }
}
