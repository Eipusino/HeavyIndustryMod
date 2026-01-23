package endfield.world.blocks.storage;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

public class FrontlineCoreBlock extends CoreBlock {
	public int max = 3;
	public boolean maxKill = false;

	public String showLabel = "oh no";
	public Color showColor = new Color(0xff5b5bff);

	public FrontlineCoreBlock(String name) {
		super(name);
	}

	@Override
	public boolean canBreak(Tile tile) {
		return Vars.state.teams.cores(tile.team()).size > 1;
	}

	@Override
	public boolean canReplace(Block other) {
		return other.alwaysReplace;
	}

	@Override
	public boolean canPlaceOn(Tile tile, Team team, int rotation) {
		return Vars.state.teams.cores(team).size < max;
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = FrontlineCoreBuild::new;
	}

	public class FrontlineCoreBuild extends CoreBuild {
		public boolean kill = false;

		public float num = 1;
		public float time = 60 * num;

		@Override
		public void updateTile() {
			super.updateTile();
			if (maxKill) {
				if (Vars.state.teams.cores(team).size > max + 3) kill = true;
				if (kill) {
					if (!Vars.headless) {
						Vars.ui.showLabel(showLabel, 0.015f, x, y);
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
				Draw.alpha(kill ? 1 : Vars.state.teams.cores(team).size > max + 2 ? 1 : 0);
				Lines.arc(x, y, 16f, time * (6 / num) / 360, 90f);
			}
		}
	}
}
