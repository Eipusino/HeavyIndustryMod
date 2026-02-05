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
	public IVariable genJavaVariableRef(Field field) {
		return new JavaFieldRef(field);
	}

	@Override
	public IFunctionEntry genJavaMethodRef(Method method) {
		return new JavaMethodRef(method);
	}
}
