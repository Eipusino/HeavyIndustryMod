package heavyindustry.world.blocks.distribution;

import mindustry.world.blocks.distribution.*;

/**
 * A directional unloader that is not affected by game frame rates.
 *
 * @author Eipusino
 */
public class DirectionalUnloaderf extends DirectionalUnloader {
	public DirectionalUnloaderf(String name) {
		super(name);
	}

	public class DirectionalUnloaderBuildf extends DirectionalUnloaderBuild {
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
