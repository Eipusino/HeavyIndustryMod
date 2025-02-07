package heavyindustry.content;

import heavyindustry.core.*;
import mindustry.content.*;
import mindustry.type.*;

/**
 * Defines the {@linkplain SectorPreset maps} this mod offers.
 *
 * @author Eipusino
 */
public final class SectorPresetsf {
	public static SectorPreset
			//serpulo
			volcanicArchipelago, ironBridgeCoast, moltenRiftValley, frozenPlateau
			//erekir
			;

	/** Don't let anyone instantiate this class. */
	private SectorPresetsf() {}

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
	}
}
