package heavyindustry.content;

import arc.graphics.*;
import heavyindustry.core.*;
import heavyindustry.graphics.*;
import mindustry.type.*;

/**
 * Defines the {@linkplain Item item} this mod offers.
 *
 * @author Eipusino
 */
public final class HIItems {
	public static Item
			stone, salt, rareEarth,
			nanoCore, nanoCoreErekir,
			originium, activatedOriginium,
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
		nanoCore = new Item("nano-core", HIPal.nanoCoreGreen) {{
			cost = -0.75f;
			hardness = 6;
		}};
		nanoCoreErekir = new Item("nano-core-erekir", HIPal.nanoCoreErekirOrange) {{
			cost = -0.75f;
			hardness = 6;
		}};
		originium = new Item("originium", HIPal.originiumBlack) {{
			cost = 1.25f;
			flammability = 0.2f;
			explosiveness = 0.3f;
			radioactivity = 0.15f;
			hardness = 6;
		}};
		activatedOriginium = new Item("activated-originium", HIPal.activatedOriginiumBlack) {{
			cost = 1.15f;
			flammability = 0.3f;
			explosiveness = 1.6f;
			radioactivity = 3f;
			hardness = 5;
		}};
		uranium = new Item("uranium", HIPal.uraniumGrey) {{
			cost = 3f;
			hardness = 7;
			healthScaling = 1.4f;
			radioactivity = 2f;
		}};
		chromium = new Item("chromium", HIPal.chromiumGrey) {{
			cost = 5f;
			hardness = 8;
			healthScaling = 1.8f;
		}};
		heavyAlloy = new Item("heavy-alloy", Color.valueOf("686b7b")) {{
			cost = 4f;
			hardness = 10;
			healthScaling = 2.2f;
			radioactivity = 0.1f;
		}};
	}
}