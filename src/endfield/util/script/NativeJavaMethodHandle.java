package endfield.util.script;

import rhino.BaseFunction;
import rhino.Context;
import rhino.Scriptable;

import java.lang.invoke.MethodHandle;

/** @see rhino.NativeJavaMethod */
public class NativeJavaMethodHandle extends BaseFunction {
	protected final MethodHandle handle;

	public NativeJavaMethodHandle(Scriptable scope, MethodHandle method) {
		super(scope, null);
		handle = method;
	}

	@Override
	public String toString() {
		return handle.toString();
	}

	@Override
	public Object get(Object key) {
		if ("__javaObject__".equals(key)) return handle;
		return super.get(key);
	}

	@Override
	public Object call(Context context, Scriptable scope, Scriptable scriptable, Object[] args) {
		return context.getWrapFactory().wrap(context, scope, Scripts2.invokeForHandle(handle, args), handle.type().returnType());
	}
}
