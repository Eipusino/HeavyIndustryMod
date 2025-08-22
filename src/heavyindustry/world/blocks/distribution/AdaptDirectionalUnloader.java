package heavyindustry.world.blocks.distribution;

import mindustry.world.blocks.distribution.DirectionalUnloader;

/**
 * A directional unloader that is not affected by game frame rates.
 *
 * @author Eipusino
 */
public class AdaptDirectionalUnloader extends DirectionalUnloader {
	public AdaptDirectionalUnloader(String name) {
		super(name);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = AdaptDirectionalUnloaderBuild::new;
	}

	public class AdaptDirectionalUnloaderBuild extends DirectionalUnloaderBuild {
		public float counter;

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
