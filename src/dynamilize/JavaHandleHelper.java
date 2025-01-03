package dynamilize;

import java.lang.reflect.*;

/**
 * For the further upgrade of JDK16+in terms of modular management and cross-platform and cross version considerations,
 * a platform support tool for Java basic behavior is provided,
 * which includes some behavior interfaces related to version/platform.
 * <p>The implementation should be carried out according to the functional description of the method for the operating platform.
 */
public interface JavaHandleHelper {
    void makeAccess(Object object);

    Variablec genJavaVariableRef(Field field);

    FunctionEntryc genJavaMethodRef(Method method);
}
