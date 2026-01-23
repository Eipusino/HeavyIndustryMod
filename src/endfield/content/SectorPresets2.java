package endfield.content;

import endfield.Vars2;
import mindustry.content.Planets;
import mindustry.type.SectorPreset;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * Defines the {@linkplain SectorPreset maps} this mod offers.
 *
 * @author Eipusino
 */
public final class SectorPresets2 {
	//serpulo
	public static SectorPreset volcanicArchipelago, ironBridgeCoast, moltenRiftValley, frozenPlateau;
	//erekir
	//public static SectorPreset no;

	/** Don't let anyone instantiate this class. */
	private SectorPresets2() {}

	/** Instantiates all contents. Called in the main thread in {@code EndFieldMod.loadContent()}. */
	@Internal
	public static void load() {
		if (Vars2.isPlugin) return;

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
