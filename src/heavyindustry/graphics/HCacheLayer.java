package heavyindustry.graphics;

import arc.Core;
import arc.graphics.Color;
import mindustry.Vars;
import mindustry.graphics.CacheLayer;
import mindustry.graphics.CacheLayer.ShaderLayer;

/**
 * Defines the {@linkplain CacheLayer cache layer}s this mod offers.
 *
 * @author Eipusino
 */
public final class HCacheLayer {
	public static ShaderLayer brine, originiumFluid, armor, pit, waterPit;

	/** Don't let anyone instantiate this class. */
	private HCacheLayer() {}

	/** Loads the cache layers. */
	public static void init() {
		brine = new ShaderLayer(HShaders.brine);
		originiumFluid = new ShaderLayer(HShaders.originiumFluid);
		pit = new ShaderLayer(HShaders.pit);
		waterPit = new ShaderLayer(HShaders.waterPit);

		armor = new ShaderLayer(HShaders.tiler) {
			@Override
			public void begin() {
				//Vars.renderer.blocks.floor.endc();
				Vars.renderer.effectBuffer.begin();
				Core.graphics.clear(Color.clear);
				Vars.renderer.blocks.floor.beginDraw();
			}

			@Override
			public void end() {
				//Vars.renderer.blocks.floor.endc();
				Vars.renderer.effectBuffer.end();

				HShaders.tiler.texture = HTextures.armor;
				Vars.renderer.effectBuffer.blit(shader);

				Vars.renderer.blocks.floor.beginDraw();
			}
		};

		CacheLayer.add(brine, originiumFluid, armor, pit, waterPit);
	}
}
