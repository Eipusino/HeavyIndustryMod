package heavyindustry.content;

import heavyindustry.core.*;
import mindustry.type.*;

import static mindustry.content.Planets.*;

/**
 * Defines the {@linkplain SectorPreset maps} this mod offers.
 *
 * @author Eipusino
 */
public final class HISectorPresets {
    public static SectorPreset
            //serpulo
            volcanicArchipelago, ironBridgeCoast, moltenRiftValley, frozenPlateau
            //erekir
            ;

    /** HISectorPresets should not be instantiated. */
    private HISectorPresets() {}

    /**
     * Instantiates all contents. Called in the main thread in {@link HeavyIndustryMod#loadContent()}.
     * <p>Remember not to execute it a second time, I did not take any precautionary measures.
     */
    public static void load() {
        //serpulo
        volcanicArchipelago = new SectorPreset("volcanicArchipelago", serpulo, 111) {{
            captureWave = 55;
            difficulty = 10f;
        }};
        ironBridgeCoast = new SectorPreset("ironBridgeCoast", serpulo, 243) {{
            captureWave = 65;
            difficulty = 11f;
        }};
        moltenRiftValley = new SectorPreset("moltenRiftValley", serpulo, 125) {{
            captureWave = 65;
            difficulty = 11f;
        }};
        frozenPlateau = new SectorPreset("frozenPlateau", serpulo, 132) {{
            captureWave = 75;
            difficulty = 12f;
        }};
    }
}
