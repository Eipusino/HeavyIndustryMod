package heavyindustry.mod;

import arc.*;
import arc.files.*;
import arc.func.*;
import arc.struct.*;
import arc.util.serialization.*;
import mindustry.mod.*;

public final class ModGetter {
    /** Mod folder location. */
    public static final Fi modDirectory = Core.settings.getDataDirectory().child("mods");

    /** ModGetter should not be instantiated. */
    private ModGetter() {}

    /**
     * Pass in a file and check if it is a mod file. If it is, return the mod's meta file. If not, throw {@link IllegalStateException}
     *
     * @param modFile Check the file, which can be a directory
     * @return The main meta file of this mod
     * @throws IllegalStateException If this file is not a mod
     */
    public static Fi checkModFormat(Fi modFile) throws IllegalStateException {
        try {
            if (!(modFile instanceof ZipFi) && !modFile.isDirectory()) modFile = new ZipFi(modFile);
        } catch (Exception e) {
            throw new IllegalStateException("file was not a valid zipped file");
        }

        Fi meta = modFile.child("mod.json").exists() ? modFile.child("mod.json") :
                modFile.child("mod.hjson").exists() ? modFile.child("mod.hjson") :
                        modFile.child("plugin.json").exists() ? modFile.child("plugin.json") : modFile.child("plugin.hjson");

        if (!meta.exists()) throw new IllegalStateException("mod format error: mod meta was not found");

        return meta;
    }

    /**
     * Determine whether the incoming file is a mod.
     *
     * @param modFile Checked files
     * @return The result represented by Boolean value
     */
    public static boolean isMod(Fi modFile) {
        try {
            checkModFormat(modFile);
            return true;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public static Seq<ModInfo> getModsWithFilter(Boolf2<Fi, Jval> filter) {
        Seq<ModInfo> result = new Seq<>();

        for (Fi file : modDirectory.list()) {
            try {
                Jval info = Jval.read(checkModFormat(file).reader());
                if (filter.get(file, info)) {
                    result.add(new ModInfo(file));
                }
            } catch (IllegalStateException ignored) {}
        }

        return result;
    }

    public static Seq<ModInfo> getModsWithName(String name) {
        return getModsWithFilter((f, i) -> i.getString("name").equals(name));
    }

    public static Seq<ModInfo> getModsWithClass(Class<? extends Mod> mainClass) {
        return getModsWithFilter((f, i) -> i.getString("main").equals(mainClass.getCanonicalName()));
    }

    public static ModInfo getModWithName(String name) {
        Seq<ModInfo> seq = getModsWithName(name);

        return seq.isEmpty() ? null : seq.first();
    }

    public static ModInfo getModWithClass(Class<? extends Mod> mainClass) {
        Seq<ModInfo> seq = getModsWithClass(mainClass);

        return seq.isEmpty() ? null : seq.first();
    }
}
