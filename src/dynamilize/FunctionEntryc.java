package dynamilize;

public interface FunctionEntryc {
    default String signature() {
        return FunctionType.signature(getName(), getType());
    }

    /** Get the name of the entrance. */
    String getName();

    /** Get the reference anonymous function defined by the entrance of this method. */
    <S, R> Function<S, R> getFunction();

    /** Get the formal parameter table type of this method. */
    FunctionType getType();
}
