package heavyindustry.world.blocks.storage;

import arc.graphics.*;
import arc.graphics.g2d.*;
import mindustry.game.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.storage.*;

import static mindustry.Vars.*;

public class FrontlineCoreBlock extends CoreBlock {
	public int max = 3;
	public boolean maxKill = false;

	public String showLabel = "oh no";
	public Color showColor = Color.valueOf("ff5b5b");

	public FrontlineCoreBlock(String name) {
		super(name);
	}

	@Override
	public boolean canBreak(Tile tile) {
		return state.teams.cores(tile.team()).size > 1;
	}

	@Override
	public boolean canReplace(Block other) {
		return other.alwaysReplace;
	}

	@Override
	public boolean canPlaceOn(Tile tile, Team team, int rotation) {
		return state.teams.cores(team).size < max;
	}

	public class FrontlineCoreBuild extends CoreBuild {
		public boolean kill = false;

		public float num = 1;
		public float time = 60 * num;

		@Override
		public void updateTile() {
			super.updateTile();
			if (maxKill) {
				if (state.teams.cores(team).size > max + 3) kill = true;
				if (kill) {
					if (!headless) {
						ui.showLabel(showLabel, 0.015f, x, y);
					}
					time--;
					if (time == 0) {
						kill();
					}
				}
			}
		}

		@Override
		public void draw() {
			super.draw();

			if (maxKill) {
				Draw.z(Layer.effect);
				Lines.stroke(2f, showColor);
				Draw.alpha(kill ? 1 : state.teams.cores(team).size > max + 2 ? 1 : 0);
				Lines.arc(x, y, 16f, time * (6 / num) / 360, 90f);
			}
		}
	}
}
