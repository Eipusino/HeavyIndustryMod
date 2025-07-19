package heavyindustry.world.blocks.storage;

import arc.graphics.g2d.TextureRegion;
import mindustry.world.blocks.storage.Unloader;

/**
 * AN unloader that is not affected by game frame rates.
 *
 * @author Eipusino
 */
public class AdaptUnloader extends Unloader {
	public AdaptUnloader(String name) {
		super(name);
	}

	@Override
	protected TextureRegion[] icons() {
		return new TextureRegion[]{region};
	}

	public class AdaptUnloaderBuild extends UnloaderBuild {
		protected float counter;

		@Override
		public void updateTile() {
			counter += edelta();

			while (counter >= speed) {
				unloadTimer = speed;
				super.updateTile();
				counter -= speed;
			}
		}
	}
}
