package heavyindustry.desktop;

import heavyindustry.util.*;

import java.lang.reflect.*;

public class DesktopAccessibleHelper implements AccessibleHelper {
    @Override
    public void makeAccessible(AccessibleObject object) {
        object.setAccessible(true);
    }

    @Override
    public void makeClassAccessible(Class<?> clazz) {
        //no action
    }
}
