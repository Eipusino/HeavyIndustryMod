package heavyindustry.content;

import heavyindustry.core.HeavyIndustryMod;
import mindustry.content.Items;
import mindustry.content.Planets;
import mindustry.type.ItemStack;
import mindustry.type.SectorPreset;

/**
 * Defines the {@linkplain SectorPreset maps} this mod offers.
 *
 * @author Eipusino
 */
public final class HSectorPresets {
	public static SectorPreset
			//serpulo
			volcanicArchipelago, ironBridgeCoast, moltenRiftValley, frozenPlateau,
			//erekir

			//gliese
			gravelMountain;

	/** Don't let anyone instantiate this class. */
	private HSectorPresets() {}

	/** Instantiates all contents. Called in the main thread in {@link HeavyIndustryMod#loadContent()}. */
	public static void load() {
		//serpulo
		volcanicArchipelago = new SectorPreset("volcanicArchipelago", Planets.serpulo, 111) {{
			captureWave = 55;
			difficulty = 10f;
		}};
		ironBridgeCoast = new SectorPreset("ironBridgeCoast", Planets.serpulo, 243) {{
			captureWave = 65;
			difficulty = 11f;
		}};
		moltenRiftValley = new SectorPreset("moltenRiftValley", Planets.serpulo, 125) {{
			captureWave = 65;
			difficulty = 11f;
		}};
		frozenPlateau = new SectorPreset("frozenPlateau", Planets.serpulo, 132) {{
			captureWave = 75;
			difficulty = 12f;
		}};
		//
		gravelMountain = new SectorPreset("gravelMountain", HPlanets.gliese, 0) {{
			alwaysUnlocked = true;
			overrideLaunchDefaults = true;
			captureWave = 15;
			difficulty = 2f;
		}};
	}
}
