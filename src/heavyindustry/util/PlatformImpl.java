package heavyindustry.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface PlatformImpl {
	void setOverride(AccessibleObject override);

	void setPublic(Class<?> type);

	/** @return The caller class of the current method. */
	Class<?> callerClass();

	Field[] getFields(Class<?> cls);

	Method[] getMethods(Class<?> cls);

	Constructor<?>[] getConstructors(Class<?> cls);
}
