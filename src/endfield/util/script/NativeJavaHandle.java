package endfield.util.script;

import rhino.BaseFunction;
import rhino.Context;
import rhino.Scriptable;

import java.lang.invoke.MethodHandle;

public class NativeJavaHandle extends BaseFunction {
	protected final MethodHandle handle;

	public NativeJavaHandle(Scriptable scope, MethodHandle handle) {
		super(scope, null);
		this.handle = handle;
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
		try {
			return context.getWrapFactory().wrap(context, scope, Scripts2.invokeForHandle(handle, args), handle.type().returnType());
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
