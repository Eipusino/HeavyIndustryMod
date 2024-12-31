package heavyindustry.mod;

import arc.struct.*;
import arc.util.*;
import mindustry.*;

public final class ModJS {
    public static final Seq<Runnable> runs = new Seq<>();
    public static final Seq<String> names = new Seq<>();

    private ModJS() {}

    public static void loadMods() {
        try {
            run(runs, names);
            runs.clear();
            names.clear();
        } catch (Exception e) {
            Vars.ui.showException(e);
            Log.err(e);
        }
    }

    private static void run(Seq<Runnable> run, Seq<String> name) {
        if (name.any()) {
            for (int i = 0; i < name.size && i < run.size; i++) {
                Vars.content.setCurrentMod(Vars.mods.locateMod(name.get(i)));

                try {
                    require(name.get(i));
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    Log.err(e);
                }

                run.get(i).run();
            }

            Vars.content.setCurrentMod(null);

            try {
                require(null);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                Log.err(e);
            }
        }
    }

    private static void require(String modName) throws NoSuchFieldException, IllegalAccessException {
        var obj = Vars.mods.getScripts();
        var field = obj.getClass().getDeclaredField("currentMod");
        field.setAccessible(true);
        if (modName == null) {
            field.set(obj, null);
        } else {
            field.set(obj, Vars.mods.getMod(modName));
        }
    }
}
