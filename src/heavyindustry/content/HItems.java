package heavyindustry.content;

import arc.graphics.Color;
import heavyindustry.core.HeavyIndustryMod;
import heavyindustry.graphics.HPal;
import mindustry.type.Item;

/**
 * Defines the {@linkplain Item item} this mod offers.
 *
 * @author Eipusino
 */
public final class HItems {
	public static Item
			stone, salt, rareEarth,
			crystalCircuit,
			originium,
			uranium, chromium, heavyAlloy;

	/** Don't let anyone instantiate this class. */
	private HItems() {}

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
		crystalCircuit = new Item("crystal-circuit", HPal.originiumRed) {{
			cost = -0.75f;
			hardness = 6;
		}};
		originium = new Item("originium", HPal.originiumRed) {{
			cost = 1.25f;
			flammability = 0.2f;
			explosiveness = 0.3f;
			radioactivity = 0.1f;
			hardness = 5;
		}};
		uranium = new Item("uranium", HPal.uraniumGrey) {{
			cost = 3f;
			hardness = 7;
			healthScaling = 1.4f;
			radioactivity = 2f;
		}};
		chromium = new Item("chromium", HPal.chromiumGrey) {{
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