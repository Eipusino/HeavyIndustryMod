package heavyindustry.util.handler;

import arc.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.ctype.*;

/** Static toolset for manipulating content. */
public final class ContentHandler {
    private static final FieldHandler<ContentLoader> handle = new FieldHandler<>(ContentLoader.class);
    private static final FieldHandler<MappableContent> contHandler = new FieldHandler<>(MappableContent.class);

    private static Seq<Content>[] contentMap;
    private static ObjectMap<String, MappableContent>[] contentNameMap;

    private ContentHandler() {}

    private static void updateContainer() {
        contentMap = handle.getValue(Vars.content, "contentMap");
        contentNameMap = handle.getValue(Vars.content, "contentNameMap");
    }

    public static void removeContent(Content content) {
        updateContainer();
        if (content.getContentType().ordinal() < contentMap.length) {
            Seq<Content> conts = contentMap[content.getContentType().ordinal()];
            if (conts != null) {
                conts.remove(content);
            }
        }

        if (content instanceof MappableContent) {
            if (content.getContentType().ordinal() < contentNameMap.length) {
                ObjectMap<String, MappableContent> map = contentNameMap[content.getContentType().ordinal()];
                if (map != null) {
                    map.remove(((MappableContent) content).name);
                }
            }
        }
    }

    /**
     * Overlay an existing content with new content and set the information for {@link UnlockableContent}.
     *
     * @param oldContent Content to be replaced
     * @param newContent Rewritten content
     * @throws RuntimeException When oldContent and newContent have different types (ContentType), throw them
     */
    public static void overrideContent(MappableContent oldContent, MappableContent newContent) {
        updateContainer();
        ContentType type = oldContent.getContentType();

        String oldName = oldContent.name;
        String newName = newContent.name;

        contHandler.setValue(newContent, "name", oldName);

        if (oldContent.getContentType() != newContent.getContentType())
            throw new RuntimeException("The old content cannot override by new content, because the content type are different");

        if (newContent instanceof UnlockableContent newC) {
            newC.localizedName = Core.bundle.get(type + "." + oldName + ".name", oldName);
            newC.description = Core.bundle.getOrNull(type + "." + oldName + ".description");
            newC.details = Core.bundle.getOrNull(type + "." + oldName + ".details");
            FieldHandler.setValueDefault(newC, "unlocked", Core.settings != null && Core.settings.getBool(oldName + "-unlocked", false));
        }

        if (contentNameMap != null) {
            contentNameMap[type.ordinal()].put(oldName, newContent);
            contentNameMap[type.ordinal()].remove(newName);
        }

        overrideContent(oldContent, (Content) newContent);
    }

    /**
     * Covering an existing content with new content
     *
     * @param oldContent Content to be replaced
     * @param newContent Rewritten content
     * @throws RuntimeException When oldContent and newContent have different types (ContentType), throw them
     */
    public static void overrideContent(Content oldContent, Content newContent) {
        updateContainer();
        short oldId = oldContent.id;
        short newId = newContent.id;

        if (oldContent.getContentType() != newContent.getContentType())
            throw new RuntimeException("The old content cannot override by new content, because the content type are different");

        ContentType type = oldContent.getContentType();
        try {
            newContent.id = oldId;

            if (contentMap != null) {
                contentMap[type.ordinal()].set(oldId, newContent);
                contentMap[type.ordinal()].remove(newId);
            }
        } catch (Throwable e) {
            Log.err(e);
        }
    }
}