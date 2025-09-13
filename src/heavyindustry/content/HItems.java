package heavyindustry.content;

import arc.graphics.Color;
import heavyindustry.graphics.HPal;
import mindustry.type.Item;

/**
 * Defines the {@linkplain Item item} this mod offers.
 *
 * @author Eipusino
 */
public final class HItems {
	public static Item
			stone, agglomerateSalt, rareEarth,
			galliumNitride, crystallineCircuit, crystallineElectronicUnit,
			crystal,
			gold, uranium, chromium, heavyAlloy;

	/** Don't let anyone instantiate this class. */
	private HItems() {}

	/** Instantiates all contents. Called in the main thread in {@code HeavyIndustryMod.loadContent()}. */
	public static void load() {
		stone = new Item("stone", Color.valueOf("8a8a8a")) {{
			hardness = 1;
			cost = 0.4f;
			lowPriority = true;
		}};
		agglomerateSalt = new Item("agglomerate-salt", Color.white) {{
			cost = 1.1f;
			hardness = 2;
		}};
		rareEarth = new Item("rare-earth", Color.valueOf("b1bd99")) {{
			hardness = 1;
			radioactivity = 0.1f;
			buildable = false;
			lowPriority = true;
		}};
		galliumNitride = new Item("gallium-nitride", Color.valueOf("bff3ff")) {{
			cost = 1.2f;
			hardness = 3;
		}};
		crystallineCircuit = new Item("crystalline-circuit", HPal.crystalAmmoBack) {{
			cost = -0.75f;
			hardness = 4;
		}};
		crystallineElectronicUnit = new Item("crystalline-electronic-unit") {{
			cost = -1.75f;
			hardness = 7;
		}};
		crystal = new Item("crystal", HPal.crystalAmmoBack) {{
			cost = 1.25f;
			flammability = 0.2f;
			explosiveness = 0.3f;
			radioactivity = 0.1f;
			hardness = 5;
		}};
		gold = new Item("gold", HPal.goldAmmoBack) {{
			cost = 0.9f;
			hardness = 1;
		}};
		uranium = new Item("uranium", HPal.uraniumAmmoBack) {{
			cost = 3f;
			hardness = 7;
			healthScaling = 1.4f;
			radioactivity = 2f;
		}};
		chromium = new Item("chromium", HPal.chromiumAmmoBack) {{
			cost = 5f;
			hardness = 9;
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