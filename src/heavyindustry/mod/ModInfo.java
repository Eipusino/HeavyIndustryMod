package heavyindustry.mod;

import arc.files.*;
import arc.util.serialization.*;

public class ModInfo {
    public final String name;
    public final String version;
    public final boolean plugin;

    public final Fi file;
    public final Jval info;

    public ModInfo(Fi modFile) {
        if (modFile instanceof ZipFi)
            throw new IllegalStateException("given file is a zip file object, please use file object!");
        Fi modMeta;
        try {
            modMeta = ModGetter.checkModFormat(modFile);
        } catch (IllegalStateException e) {
            throw new RuntimeException(e);
        }
        info = Jval.read(modMeta.reader());
        file = modFile;
        name = info.get("name").asString();
        version = info.get("version").asString();

        boolean plu = false;
        if (info.has("plugin")) {
            plu = info.get("plugin").asBool();
        }
        plugin = plu;
    }

    @Override
    public String toString() {
        return "ModInfo{" +
                "name='" + name + '\'' +
                ", version='" + version + '\'' +
                ", plugin=" + plugin +
                '}';
    }
}
