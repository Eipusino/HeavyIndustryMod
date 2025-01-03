package dynamilize;

/** The packaged delegation call function interface, unlike the {@linkplain Function function} function, does not accept this pointer in the delegation. */
public interface Delegate<R> {
    R invoke(ArgumentList args);

    default R invoke(Object... args) {
        ArgumentList lis = ArgumentList.as(args);
        R r = invoke(lis);
        lis.type().recycle();
        lis.recycle();
        return r;
    }

    default R invoke(FunctionType type, Object... args) {
        ArgumentList lis = ArgumentList.asWithType(type, args);
        R r = invoke(lis);
        lis.recycle();
        return r;
    }
}
