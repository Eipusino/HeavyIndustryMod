package endfield.util;

import dynamilize.Function;
import dynamilize.FunctionType;
import dynamilize.IFunctionEntry;
import endfield.util.handler.MethodHandler;

import java.lang.reflect.Method;

public class JavaMethodReference implements IFunctionEntry {
	final Method method;
	final String name;
	final Function<?, ?> defineFunction;
	final FunctionType type;

	public JavaMethodReference(Method method) {
		this.method = method;
		this.name = method.getName();
		this.type = FunctionType.inst(method);

		Reflects.setAccessible(method);

		this.defineFunction = (self, args) -> MethodHandler.invoke(self.objSelf(), method, false, args);
	}

	@Override
	public String getName() {
		return name;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <S, R> Function<S, R> getFunc() {
		return (Function<S, R>) defineFunction;
	}

	@Override
	public FunctionType getType() {
		return type;
	}
}
