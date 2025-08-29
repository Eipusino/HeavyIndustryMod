package heavyindustry.util;

import dynamilize.IFunctionEntry;
import dynamilize.IVariable;
import dynamilize.JavaHandleHelper;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static heavyindustry.HVars.accessibleHelper;

public class DefaultHandleHelper implements JavaHandleHelper {
	@Override
	public void makeAccess(Object object) {
		if (object instanceof AccessibleObject obj) {
			accessibleHelper.makeAccessible(obj);
		} else if (object instanceof Class<?> clazz) {
			accessibleHelper.makeClassAccessible(clazz);
		} else throw new IllegalArgumentException("given obj unusable, it must be AccessibleObject or Class");
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
