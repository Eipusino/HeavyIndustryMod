package heavyindustry.util.handler;

import arc.*;
import arc.scene.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.input.*;
import mindustry.type.*;
import mindustry.ui.*;

import java.util.*;

/** Tool for adding category entries to the selection bar in the bottom right corner of the square. */
public class CategoryHandler {
    protected static final Binding empBind;

    static {
        EnumHandler<Binding> handler = new EnumHandler<>(Binding.class);

        empBind = handler.addEnumItemTail("unBind", (KeyBinds.KeybindValue) null);
    }

    protected final ObjectMap<Category, Categoryf> newCats = new ObjectMap<>();
    protected boolean hasNew = false;

    public void handleBlockFrag() {
        if (!hasNew) return;
        Table catTable = FieldHandler.getValueDefault(Vars.ui.hudfrag.blockfrag, "blockCatTable");

        // frame.update(() -> {});
        Table blockSelect = (Table) catTable.getChildren().get(0);
        Table categories = (Table) catTable.getChildren().get(1);

        Cell<?> pane = blockSelect.getCells().get(0);
        pane.height(240f);

        Seq<Element> catButtons = new Seq<>(categories.getChildren());
        catButtons.remove(0);

        for (Categoryf cat : newCats.values()) {
            ImageButton button = ((ImageButton) catButtons.find(e -> ("category-" + cat.cat.name()).equals(e.name)));
            if (button == null) continue;
            button.getStyle().imageUp = new TextureRegionDrawable(Core.atlas.find(cat.icon));
            button.resizeImage(32);
        }

        categories.clearChildren();
        categories.pane(t -> {
            t.defaults().size(50);
            int count = 0;
            for (Element element : catButtons) {
                if (count++ % 2 == 0 && count != 0) t.row();
                t.add(element);
            }

            if (catButtons.size % 2 != 0) t.image(Styles.black6);
        }).size(catButtons.size > 12 ? 125 : 100, 300);
    }

    /**
     * Add a new building type to the list, which will be displayed in the block selection bar in the game.
     *
     * @param name     Internal name of category
     * @param ordinal  The ordinal number of the display position of this category in the selection bar
     * @param iconName The resource file name of the icon in this category
     */
    public Category add(String name, int ordinal, String iconName) {
        return add(name, ordinal, null, iconName);
    }

    /**
     * Add a new building type to the list, which will be displayed in the block selection bar in the game.
     *
     * @param name     Internal name of category
     * @param iconName The resource file name of the icon in this category
     */
    public Category add(String name, String iconName) {
        return add(name, null, iconName);
    }

    /**
     * Add a new building type to the list, which will be displayed in the block selection bar in the game.
     *
     * @param name     Internal name of category
     * @param bind     The target key bound to this category
     * @param iconName The resource file name of the icon in this category
     */
    public Category add(String name, Binding bind, String iconName) {
        return add(name, Category.values().length, bind, iconName);
    }

    /**
     * Add a new building type to the list, which will be displayed in the block selection bar in the game.
     *
     * @param name     Internal name of category
     * @param ordinal  The ordinal number of the display position of this category in the selection bar
     * @param bind     The target key bound to this category
     * @param iconName The resource file name of the icon in this category
     */
    public Category add(String name, int ordinal, Binding bind, String iconName) {
        hasNew = true;
        Categoryf category = new Categoryf(name, ordinal, bind, iconName);
        newCats.put(category.cat, category);

        return category.cat;
    }

    public void init() {
        Binding[] arr = FieldHandler.getValueDefault(Vars.ui.hudfrag.blockfrag, "blockSelect");
        if (arr.length < Category.all.length) {
            arr = Arrays.copyOf(arr, Category.all.length);
            for (int i = 0; i < arr.length; i++) {
                Categoryf cat = newCats.get(Category.all[i]);
                if (arr[i] == null) {
                    arr[i] = cat != null ? cat.bind : empBind;
                }
            }
        }

        FieldHandler.setValueDefault(Vars.ui.hudfrag.blockfrag, "blockSelect", arr);
    }

    protected static class Categoryf {
        private static final EnumHandler<Category> handler = new EnumHandler<>(Category.class);

        final Category cat;
        final @Nullable Binding bind;
        final String icon;
        int ordinal;

        Categoryf(Category cat, Binding bind, String icon) {
            this.cat = cat;
            this.icon = icon;
            ordinal = cat.ordinal();
            this.bind = bind;
        }

        Categoryf(String name, int ordinal, Binding bind, String icon) {
            this(handler.addEnumItem(name, ordinal), bind, icon);
            FieldHandler.setValueDefault(Category.class, "all", Category.values());
            this.ordinal = ordinal;
        }
    }
}
