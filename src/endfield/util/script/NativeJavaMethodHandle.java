package endfield.util.script;

import rhino.BaseFunction;
import rhino.Context;
import rhino.Scriptable;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

import static endfield.Vars2.platformImpl;

/** @see rhino.NativeJavaMethod */
public class NativeJavaMethodHandle extends BaseFunction {
	protected final MethodHandle handle;

	public NativeJavaMethodHandle(Scriptable scope, Method method) throws IllegalAccessException {
		this(scope, platformImpl.lookup(method.getDeclaringClass()).unreflect(method));
	}

	public NativeJavaMethodHandle(Scriptable scope, MethodHandle method) {
		super(scope, null);
		handle = method;
	}

	public static NativeJavaMethodHandle unreflect(Scriptable scope, Method method) {
		try {
			return new NativeJavaMethodHandle(scope, method);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
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
