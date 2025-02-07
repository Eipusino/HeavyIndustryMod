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
public final class CacheLayerf {
	public static ShaderLayer brine, nanoFluid, armor, pit, waterPit;

	/** Don't let anyone instantiate this class. */
	private CacheLayerf() {}

	/** Loads the cache layers. */
	public static void init() {
		brine = new ShaderLayer(Shadersf.brine);
		nanoFluid = new ShaderLayer(Shadersf.nanoFluid);
		pit = new ShaderLayer(Shadersf.pit);
		waterPit = new ShaderLayer(Shadersf.waterPit);

		armor = new ShaderLayer(Shadersf.tiler) {
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

				Shadersf.tiler.texture = Textures.armor;
				renderer.effectBuffer.blit(shader);

				renderer.blocks.floor.beginc();
			}
		};

		CacheLayer.add(brine, nanoFluid, armor, pit, waterPit);
	}
}
