package heavyindustry.content;

import heavyindustry.HVars;
import mindustry.content.Planets;
import mindustry.type.SectorPreset;

/**
 * Defines the {@linkplain SectorPreset maps} this mod offers.
 *
 * @author Eipusino
 */
public final class HSectorPresets {
	//serpulo
	public static SectorPreset volcanicArchipelago, ironBridgeCoast, moltenRiftValley, frozenPlateau;
	//erekir
	//public static SectorPreset no;

	/** Don't let anyone instantiate this class. */
	private HSectorPresets() {}

	/** Instantiates all contents. Called in the main thread in {@code HeavyIndustryMod.loadContent()}. */
	public static void load() {
		if (HVars.isPlugin) return;

		//serpulo
		volcanicArchipelago = new SectorPreset("volcanicArchipelago", Planets.serpulo, 14) {{
			captureWave = 55;
			difficulty = 10f;
		}};
		ironBridgeCoast = new SectorPreset("ironBridgeCoast", Planets.serpulo, 37) {{
			captureWave = 65;
			difficulty = 11f;
		}};
		moltenRiftValley = new SectorPreset("moltenRiftValley", Planets.serpulo, 270) {{
			captureWave = 65;
			difficulty = 11f;
		}};
		frozenPlateau = new SectorPreset("frozenPlateau", Planets.serpulo, 267) {{
			captureWave = 75;
			difficulty = 12f;
		}};
		//erekir
	}
}
