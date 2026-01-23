package endfield.world.blocks.defense;

import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.world.Tile;

import static mindustry.Vars.world;

public class CliffExplosive extends Explosive {
	public CliffExplosive(String name) {
		super(name);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = CliffExplosiveBuild::new;
	}

	public class CliffExplosiveBuild extends ExplosiveBuild {
		@Override
		public void detonate() {
			for (float i = -range; i <= range; i += 8) {
				for (float j = -range; j <= range; j += 8) {
					Tile tile = world.tileWorld(x + i, y + j);

					if (tile == null || tile.block().hasBuilding() || tile.block() == Blocks.cliff) continue;

					if (tile.block() != Blocks.air) Fx.blockExplosionSmoke.at(x + i, y + j);
					tile.setBlock(Blocks.air);
				}
			}

			super.detonate();
		}
	}
}
