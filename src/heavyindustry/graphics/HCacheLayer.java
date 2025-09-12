package heavyindustry.graphics;

import mindustry.graphics.CacheLayer;
import mindustry.graphics.CacheLayer.ShaderLayer;

/**
 * Defines the {@linkplain CacheLayer cache layer}s this mod offers.
 *
 * @author Eipusino
 */
public final class HCacheLayer {
	public static ShaderLayer brine, crystalFluid, deepCrystalFluid, pit, waterPit;

	/** Don't let anyone instantiate this class. */
	private HCacheLayer() {}

	/** Loads the cache layers. */
	public static void onClient() {
		brine = new ShaderLayer(HShaders.brine);
		crystalFluid = new ShaderLayer(HShaders.crystalFluid);
		deepCrystalFluid = new ShaderLayer(HShaders.deepCrystalFluid);
		pit = new ShaderLayer(HShaders.pit);
		waterPit = new ShaderLayer(HShaders.waterPit);

		CacheLayer.add(brine, crystalFluid, deepCrystalFluid, pit, waterPit);
	}
}
