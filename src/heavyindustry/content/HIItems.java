package heavyindustry.content;

import arc.graphics.*;
import heavyindustry.core.*;
import mindustry.type.*;

/**
 * Defines the {@linkplain Item item} this mod offers.
 *
 * @author Eipusino
 */
public final class HIItems {
    public static Item
            stone, salt, rareEarth,
            nanocore, nanocoreErekir,
            uranium, chromium, heavyAlloy;

    /** Don't let anyone instantiate this class. */
    private HIItems() {}

    /** Instantiates all contents. Called in the main thread in {@link HeavyIndustryMod#loadContent()}. */
    public static void load() {
        stone = new Item("stone", Color.valueOf("8a8a8a")) {{
            hardness = 1;
            cost = 0.4f;
            lowPriority = true;
        }};
        salt = new Item("salt", Color.white) {{
            cost = 1.1f;
            hardness = 2;
        }};
        rareEarth = new Item("rare-earth", Color.valueOf("b1bd99")) {{
            hardness = 1;
            radioactivity = 0.1f;
            buildable = false;
            lowPriority = true;
        }};
        nanocore = new Item("nanocore", Color.valueOf("6aa95e")) {{
            cost = -0.75f;
            hardness = 6;
        }};
        nanocoreErekir = new Item("nanocore-erekir", Color.valueOf("d06b53")) {{
            cost = -0.75f;
            hardness = 6;
        }};
        uranium = new Item("uranium", Color.valueOf("a5b2c2")) {{
            cost = 3;
            hardness = 7;
            healthScaling = 1.4f;
            radioactivity = 2f;
        }};
        chromium = new Item("chromium", Color.valueOf("8f94b3")) {{
            cost = 5;
            hardness = 8;
            healthScaling = 1.8f;
        }};
        heavyAlloy = new Item("heavy-alloy", Color.valueOf("686b7b")) {{
            cost = 4;
            hardness = 10;
            healthScaling = 2.2f;
            radioactivity = 0.1f;
        }};
    }
}