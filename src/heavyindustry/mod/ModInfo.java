package heavyindustry.mod;

import arc.files.*;
import arc.util.serialization.*;
import heavyindustry.util.*;

public class ModInfo {
    public final String name;
    public final String version;
    public final Fi file;

    public ModInfo(Fi modFile) {
        if (modFile instanceof ZipFi)
            throw new IllegalStateException("given file is a zip file object, please use file object!");
        Fi modMeta;
        try {
            modMeta = ModGetter.checkModFormat(modFile);
        } catch (IllegalStateException e) {
            throw new RuntimeException(e);
        }
        Jval info = Jval.read(modMeta.reader());
        file = modFile;
        name = info.get("name").asString();
        version = info.get("version").asString();
    }
}
