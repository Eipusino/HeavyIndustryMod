package heavyindustry.desktop.classes;

import arc.*;
import arc.files.*;
import heavyindustry.mod.*;
import heavyindustry.util.classes.*;

import java.util.*;

public class DesktopGeneratedClassLoader extends AbstractGeneratedClassLoader {
    private static final Fi jarFileCache = Core.settings.getDataDirectory().child("heavyindustry").child("cache");
    //private static final Object unsafe;
    //private static final Method defineClass;

    public static final Fi TMP_FILE = jarFileCache.child("temp_file.jar");

    private final HashMap<String, Class<?>> classMap = new HashMap<>();

    protected ModInfo mod;

    private ZipFi zip;

    public DesktopGeneratedClassLoader(ModInfo modInfo, ClassLoader parent) {
        super(JarList.inst().loadCacheFile(modInfo).file(), parent);
        mod = modInfo;
    }

    @Override
    public void declareClass(String name, byte[] byteCode) {

    }

    @Override
    public Class<?> loadClass(String name, Class<?> accessor, boolean resolve) throws ClassNotFoundException {
        return null;
    }
}
