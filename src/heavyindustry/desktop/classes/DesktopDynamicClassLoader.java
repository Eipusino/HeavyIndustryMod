package heavyindustry.desktop.classes;

import arc.*;
import arc.files.*;
import heavyindustry.util.classes.*;

import java.util.*;

public class DesktopDynamicClassLoader extends AbstractDynamicClassLoader {
    public static Fi jarFileCache = Core.files.cache("tempGenerate.jar");

    //private static final Object unsafe;
    //private static final Method defineClass;

    private final HashMap<String, byte[]> classes = new HashMap<>();
    private final HashMap<String, Class<?>> loadedClass = new HashMap<>();

    public DesktopDynamicClassLoader(ClassLoader parent) {
        super(jarFileCache.file(), parent);
        reset();
    }

    @Override
    public void reset() {

    }

    @Override
    public void declareClass(String name, byte[] byteCode) {

    }

    @Override
    public Class<?> loadClass(String name, Class<?> accessor, boolean resolve) throws ClassNotFoundException {
        return null;
    }
}
