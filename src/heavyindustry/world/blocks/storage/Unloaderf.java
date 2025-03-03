package heavyindustry.world.blocks.storage;

import mindustry.world.blocks.storage.Unloader;

/**
 * AN unloader that is not affected by game frame rates.
 *
 * @author Eipusino
 */
public class Unloaderf extends Unloader {
	public Unloaderf(String name) {
		super(name);
	}

	public class UnloaderBuildf extends UnloaderBuild {
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
