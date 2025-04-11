package heavyindustry.graphics;

import arc.Core;
import arc.graphics.Color;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.CacheLayer.ShaderLayer;

import static mindustry.Vars.renderer;

/**
 * Defines the {@linkplain CacheLayer cache layer}s this mod offers.
 *
 * @author Eipusino
 */
public final class HCacheLayer {
	public static ShaderLayer brine, nanoFluid, armor, pit, waterPit;

	/** Don't let anyone instantiate this class. */
	private HCacheLayer() {}

	/** Loads the cache layers. */
	public static void init() {
		brine = new ShaderLayer(HShaders.brine);
		nanoFluid = new ShaderLayer(HShaders.nanoFluid);
		pit = new ShaderLayer(HShaders.pit);
		waterPit = new ShaderLayer(HShaders.waterPit);

		armor = new ShaderLayer(HShaders.tiler) {
			@Override
			public void begin() {
				//renderer.blocks.floor.endc();
				renderer.effectBuffer.begin();
				Core.graphics.clear(Color.clear);
				renderer.blocks.floor.beginc();
			}

			@Override
			public void end() {
				//renderer.blocks.floor.endc();
				renderer.effectBuffer.end();

				HShaders.tiler.texture = HTextures.armor;
				renderer.effectBuffer.blit(shader);

				renderer.blocks.floor.beginc();
			}
		};

		CacheLayer.add(brine, nanoFluid, armor, pit, waterPit);
	}
}
