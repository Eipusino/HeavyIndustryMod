package heavyindustry.ui;

import arc.scene.ui.layout.*;
import arc.util.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.meta.*;

/**
 * An item image with text.
 */
public class ItemDisplay extends Table {
    public final Item item;
    public final int amount;

    public ItemDisplay(Item item) {
        this(item, 0);
    }

    public ItemDisplay(Item it, int am, boolean sh) {
        add(new ItemImage(new ItemStack(it, am)));
        if (sh) add(it.localizedName).padLeft(4 + am > 99 ? 4 : 0);

        item = it;
        amount = am;
    }

    public ItemDisplay(Item item, int amount) {
        this(item, amount, true);
    }

    /** Displays the item with a "/sec" qualifier based on the time period, in ticks. */
    public ItemDisplay(Item it, int am, float ti, boolean sh) {
        add(new ItemImage(it.uiIcon, am));
        add((sh ? it.localizedName + "\n" : "") + "[lightgray]" + Strings.autoFixed(am / (ti / 60f), 2) + StatUnit.perSecond.localized()).padLeft(2).padRight(5).style(Styles.outlineLabel);

        item = it;
        amount = am;
    }
}