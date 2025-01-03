package heavyindustry.util;

import dynamilize.*;

import java.lang.reflect.*;

import static heavyindustry.core.HeavyIndustryMod.*;

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
    public Variablec genJavaVariableRef(Field field) {
        return new JavaFieldRef(field);
    }

    @Override
    public FunctionEntryc genJavaMethodRef(Method method) {
        return new JavaMethodRef(method);
    }
}
