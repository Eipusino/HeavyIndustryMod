package heavyindustry.util;

import java.lang.reflect.*;

public interface AccessibleHelper {
    void makeAccessible(AccessibleObject object);

    void makeClassAccessible(Class<?> clazz);
}
