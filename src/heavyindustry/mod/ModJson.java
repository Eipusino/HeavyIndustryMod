package heavyindustry.mod;

import arc.files.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.ctype.*;
import mindustry.mod.Mods.*;
import mindustry.type.*;

import java.util.*;

import static mindustry.Vars.*;

public final class ModJson {
    private static final ContentParserf parser = new ContentParserf();

    /** Don't let anyone instantiate this class. */
    private ModJson() {}

    /** Load JSON content from other mods. */
    public static void loadContent() {
        content.setCurrentMod(null);

        class LoadRun implements Comparable<LoadRun> {
            final ContentType type;
            final Fi file;
            final LoadedMod mod;

            public LoadRun(ContentType content, Fi fi, LoadedMod loaded) {
                type = content;
                file = fi;
                mod = loaded;
            }

            @Override
            public int compareTo(LoadRun l) {
                int compare = mod.name.compareTo(l.mod.name);
                if (compare != 0) return compare;
                return file.name().compareTo(l.file.name());
            }
        }

        Seq<LoadRun> runs = new Seq<>();

        for (LoadedMod mod : Vars.mods.orderedMods()) {
            if (mod.root.child("h_content").exists()) {
                Fi contentRoot = mod.root.child("h_content");
                for (ContentType type : ContentType.all) {
                    String lower = type.name().toLowerCase(Locale.ROOT);

                    Fi folder = contentRoot.child(lower + (lower.endsWith("s") ? "" : "s"));//units,items....

                    if (folder.exists()) {
                        for (Fi file : folder.findAll(f -> f.extension().equals("json") || f.extension().equals("hjson"))) {
                            runs.add(new LoadRun(type, file, mod));
                        }
                    }
                }
            }
        }

        //ensure that mod content is in the appropriate order
        runs.sort();
        for (LoadRun l : runs) {
            Content current = content.getLastAdded();
            try {
                //this binds the content but does not load it entirely
                Content loaded = parser.parse(l.mod, l.file.nameWithoutExtension(),
                        l.file.readString("UTF-8")
                        , l.file, l.type);
                Log.debug("[@] Loaded '@'.", l.mod.meta.name, (loaded instanceof UnlockableContent u ? u.localizedName : loaded));
            } catch (Throwable e) {
                if (current != content.getLastAdded() && content.getLastAdded() != null) {
                    parser.markError(content.getLastAdded(), l.mod, l.file, e);
                } else {
                    ErrorContent error = new ErrorContent();
                    parser.markError(error, l.mod, l.file, e);
                }
            }
        }

        //this finishes parsing content fields
        parser.finishParsing();
    }
}
