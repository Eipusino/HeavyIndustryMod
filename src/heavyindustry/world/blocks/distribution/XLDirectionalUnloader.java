package heavyindustry.world.blocks.distribution;

import mindustry.world.blocks.distribution.DirectionalUnloader;

/**
 * A directional unloader that is not affected by game frame rates.
 *
 * @author Eipusino
 */
public class XLDirectionalUnloader extends DirectionalUnloader {
	public XLDirectionalUnloader(String name) {
		super(name);
	}

	public class XLDirectionalUnloaderBuild extends DirectionalUnloaderBuild {
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
