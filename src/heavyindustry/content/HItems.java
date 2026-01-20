package heavyindustry.content;

import arc.graphics.Color;
import heavyindustry.HVars;
import heavyindustry.graphics.HPal;
import mindustry.type.Item;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * Defines the {@linkplain Item item} this mod offers.
 *
 * @author Eipusino
 */
public final class HItems {
	public static Item stone, agglomerateSalt, rareEarth;
	public static Item galliumNitride, crystallineCircuit, crystallineElectronicUnit;
	public static Item crystal;
	public static Item gold, uranium, chromium, heavyAlloy;

	/** Don't let anyone instantiate this class. */
	private HItems() {}

	/** Instantiates all contents. Called in the main thread in {@code HeavyIndustryMod.loadContent()}. */
	@Internal
	public static void load() {
		if (HVars.isPlugin) return;

		stone = new Item("stone", new Color(0x8a8a8aff)) {{
			hardness = 1;
			cost = 0.4f;
			lowPriority = true;
		}};
		agglomerateSalt = new Item("agglomerate-salt", Color.white) {{
			cost = 1.1f;
			hardness = 2;
		}};
		rareEarth = new Item("rare-earth", new Color(0xb1bd99ff)) {{
			hardness = 1;
			radioactivity = 0.1f;
			buildable = false;
			lowPriority = true;
		}};
		galliumNitride = new Item("gallium-nitride", new Color(0xbff3ffff)) {{
			cost = 1.2f;
			hardness = 3;
		}};
		crystallineCircuit = new Item("crystalline-circuit", HPal.crystalAmmoBack) {{
			cost = -0.75f;
			hardness = 4;
		}};
		crystallineElectronicUnit = new Item("crystalline-electronic-unit", HPal.crystalAmmoDark) {{
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
		heavyAlloy = new Item("heavy-alloy", HPal.heavyAlloyAmmoBack) {{
			cost = 4f;
			hardness = 10;
			healthScaling = 2.2f;
			radioactivity = 0.1f;
		}};
	}
}