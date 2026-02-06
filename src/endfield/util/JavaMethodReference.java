package endfield.util;

import dynamilize.Function;
import dynamilize.FunctionType;
import dynamilize.IFunctionEntry;
import endfield.util.handler.MethodHandler;

import java.lang.reflect.Method;

public class JavaMethodReference implements IFunctionEntry {
	final String name;
	final Function<?, ?> defineFunction;
	final FunctionType type;

	public JavaMethodReference(Method method) {
		name = method.getName();
		type = FunctionType.inst(method);

		defineFunction = (self, args) -> MethodHandler.invokeDefault(self.objSelf(), method.getName(), args);
	}

	@Override
	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S, R> Function<S, R> getFunction() {
		return (Function<S, R>) defineFunction;
	}

	@Override
	public FunctionType getType() {
		return type;
	}
}
