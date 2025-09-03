package heavyindustry.util;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.AccessibleObject;

public interface ReflectImpl {
	void setOverride(AccessibleObject override);

	void setPublic(Class<?> type);

	Class<?> callerClass();

	Lookup lookup();
}
