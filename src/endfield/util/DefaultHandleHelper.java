package endfield.util;

import dynamilize.IFunctionEntry;
import dynamilize.IVariable;
import dynamilize.JavaHandleHelper;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static endfield.Vars2.accessibleHelper;

public class DefaultHandleHelper implements JavaHandleHelper {
	@Override
	public void makeAccess(Object object) {
		if (object instanceof AccessibleObject obj) {
			accessibleHelper.makeAccessible(obj);
		} else if (object instanceof Class<?> clazz) {
			accessibleHelper.makeClassAccessible(clazz);
		}
	}

	@Override
	public IVariable getJavaVariableReference(Field field) {
		return new JavaFieldReference(field);
	}

	@Override
	public IFunctionEntry getJavaMethodReference(Method method) {
		return new JavaMethodReference(method);
	}
}
