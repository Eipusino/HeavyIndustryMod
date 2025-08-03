package heavyindustry.world.blocks.units;

import arc.graphics.g2d.Draw;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.Env;

public class CapBlock extends Wall {
	public CapBlock(String name) {
		super(name);

		envDisabled = Env.any;
	}

	public class CapBuild extends WallBuild {
		@Override
		public void draw() {
			Draw.rect(region, x, y);

			drawTeamTop();
		}
	}
}
