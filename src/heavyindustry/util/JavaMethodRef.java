package heavyindustry.util;

import dynamilize.*;
import heavyindustry.util.handler.*;

import java.lang.reflect.*;

@SuppressWarnings("unchecked")
public class JavaMethodRef implements FunctionEntryc {
    private final String name;
    private final Function<?, ?> defFunc;
    private final FunctionType type;

    public JavaMethodRef(Method invokeMethod) {
        name = invokeMethod.getName();
        type = FunctionType.inst(invokeMethod);

        defFunc = (self, args) -> MethodHandler.invokeDefault(self.objSelf(), invokeMethod.getName(), args);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public <S, R> Function<S, R> getFunction() {
        return (Function<S, R>) defFunc;
    }

    @Override
    public FunctionType getType() {
        return type;
    }
}
