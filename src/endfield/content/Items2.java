package endfield.content;

import arc.graphics.Color;
import endfield.Vars2;
import endfield.graphics.Pal2;
import mindustry.type.Item;

/**
 * Defines the {@linkplain Item item} this mod offers.
 *
 * @author Eipusino
 */
public final class Items2 {
	public static Item stone, agglomerateSalt, rareEarth;
	public static Item galliumNitride, crystallineCircuit, crystallineElectronicUnit;
	public static Item crystal;
	public static Item gold, uranium, chromium, heavyAlloy;

	/** Don't let anyone instantiate this class. */
	private Items2() {}

	/** Instantiates all contents. Called in the main thread in {@code EndFieldMod.loadContent()}. */
	public static void load() {
		if (Vars2.isPlugin) return;

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
		crystallineCircuit = new Item("crystalline-circuit", Pal2.crystalAmmoBack) {{
			cost = -0.75f;
			hardness = 4;
		}};
		crystallineElectronicUnit = new Item("crystalline-electronic-unit", Pal2.crystalAmmoDark) {{
			cost = -1.75f;
			hardness = 7;
		}};
		crystal = new Item("crystal", Pal2.crystalAmmoBack) {{
			cost = 1.25f;
			flammability = 0.2f;
			explosiveness = 0.3f;
			radioactivity = 0.1f;
			hardness = 5;
		}};
		gold = new Item("gold", Pal2.goldAmmoBack) {{
			cost = 0.9f;
			hardness = 1;
		}};
		uranium = new Item("uranium", Pal2.uraniumAmmoBack) {{
			cost = 3f;
			hardness = 7;
			healthScaling = 1.4f;
			radioactivity = 2f;
		}};
		chromium = new Item("chromium", Pal2.chromiumAmmoBack) {{
			cost = 5f;
			hardness = 9;
			healthScaling = 1.8f;
		}};
		heavyAlloy = new Item("heavy-alloy", Pal2.heavyAlloyAmmoBack) {{
			cost = 4f;
			hardness = 10;
			healthScaling = 2.2f;
			radioactivity = 0.1f;
		}};
	}
}