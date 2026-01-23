package endfield.graphics;

import mindustry.graphics.CacheLayer;
import mindustry.graphics.CacheLayer.ShaderLayer;
import org.jetbrains.annotations.ApiStatus.Internal;

/**
 * Defines the {@linkplain CacheLayer cache layer}s this mod offers.
 *
 * @author Eipusino
 */
public final class CacheLayer2 {
	public static ShaderLayer brine, glacium, crystalFluid, deepCrystalFluid, pit, waterPit;

	/** Don't let anyone instantiate this class. */
	private CacheLayer2() {}

	/** Loads the cache layers. */
	@Internal
	public static void load() {
		brine = new ShaderLayer(Shaders2.brine);
		glacium = new ShaderLayer(Shaders2.glacium);
		crystalFluid = new ShaderLayer(Shaders2.crystalFluid);
		deepCrystalFluid = new ShaderLayer(Shaders2.deepCrystalFluid);
		pit = new ShaderLayer(Shaders2.pit);
		waterPit = new ShaderLayer(Shaders2.waterPit);

		CacheLayer.add(brine, crystalFluid, deepCrystalFluid, pit, waterPit);
	}
}
