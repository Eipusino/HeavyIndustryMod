package endfield.world.blocks.storage;

import arc.graphics.g2d.TextureRegion;
import mindustry.world.blocks.storage.Unloader;

/**
 * AN unloader that is not affected by game frame rates.
 *
 * @author Eipusino
 */
public class Unloader2 extends Unloader {
	public Unloader2(String name) {
		super(name);
	}

	@Override
	protected TextureRegion[] icons() {
		return new TextureRegion[]{region};
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = UnloaderBuild2::new;
	}

	public class UnloaderBuild2 extends UnloaderBuild {
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
