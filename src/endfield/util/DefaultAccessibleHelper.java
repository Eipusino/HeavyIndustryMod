package endfield.util;

import java.lang.reflect.AccessibleObject;

public class DefaultAccessibleHelper implements AccessibleHelper {
	@Override
	public void makeAccessible(AccessibleObject object) {
		object.setAccessible(true);
	}

	@Override
	public void makeClassAccessible(Class<?> clazz) {
		//no action
	}
}
