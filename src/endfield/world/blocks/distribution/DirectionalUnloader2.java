package endfield.world.blocks.distribution;

import mindustry.world.blocks.distribution.DirectionalUnloader;

/**
 * A directional unloader that is not affected by game frame rates.
 *
 * @author Eipusino
 */
public class DirectionalUnloader2 extends DirectionalUnloader {
	public DirectionalUnloader2(String name) {
		super(name);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = DirectionalUnloaderBuild2::new;
	}

	public class DirectionalUnloaderBuild2 extends DirectionalUnloaderBuild {
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
