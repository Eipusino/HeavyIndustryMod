package heavyindustry.graphics;

import arc.*;
import arc.graphics.*;
import mindustry.graphics.*;
import mindustry.graphics.CacheLayer.*;

import static mindustry.Vars.*;

/**
 * Defines the {@linkplain CacheLayer cache layer}s this mod offers.
 *
 * @author Eipusino
 */
public final class HICacheLayer {
	public static ShaderLayer brine, nanoFluid, armor, pit, waterPit;

	/** Don't let anyone instantiate this class. */
	private HICacheLayer() {}

	/** Loads the cache layers. */
	public static void init() {
		brine = new ShaderLayer(HIShaders.brine);
		nanoFluid = new ShaderLayer(HIShaders.nanoFluid);
		pit = new ShaderLayer(HIShaders.pit);
		waterPit = new ShaderLayer(HIShaders.waterPit);

		armor = new ShaderLayer(HIShaders.tiler) {
			@Override
			public void begin() {
				renderer.blocks.floor.endc();
				renderer.effectBuffer.begin();
				Core.graphics.clear(Color.clear);
				renderer.blocks.floor.beginc();
			}

			@Override
			public void end() {
				renderer.blocks.floor.endc();
				renderer.effectBuffer.end();

				HIShaders.tiler.texture = HITextures.armor;
				renderer.effectBuffer.blit(shader);

				renderer.blocks.floor.beginc();
			}
		};

		CacheLayer.add(brine, nanoFluid, armor, pit, waterPit);
	}
}
