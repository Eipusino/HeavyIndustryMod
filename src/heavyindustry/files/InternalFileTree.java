package heavyindustry.files;

import arc.files.*;
import arc.util.*;

/** Use for jar internal navigation. */
public class InternalFileTree {
    public final Class<?> anchorClass;

    public final ZipFi root;
    public final Fi path;

    /** @param owner navigation anchor */
    public InternalFileTree(Class<?> owner) {
        anchorClass = owner;

        String classPath = owner.getResource("").getFile().replaceAll("%20", " ");
        classPath = classPath.substring(classPath.indexOf(":") + 2);
        String jarPath = (OS.isLinux ? "/" : "") + classPath.substring(0, classPath.indexOf("!"));

        path = new Fi(jarPath);
        root = new ZipFi(path);
    }

    public Fi child(String name) {
        Fi out = root;
        for (String s : name.split("/")) {
            if (!s.isEmpty())
                out = out.child(s);
        }
        return out;
    }
}
