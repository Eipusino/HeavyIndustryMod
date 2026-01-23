package endfield.world.blocks.defense;

import arc.func.Cons;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.world.blocks.defense.OverdriveProjector;

public class RectOverdriveProjector extends OverdriveProjector {
	public Cons<Building> selected = t -> Drawf.selected(t, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f)));

	public RectOverdriveProjector(String name) {
		super(name);
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		drawPotentialLinks(x, y);
		drawOverlay(x * Vars.tilesize + offset, y * Vars.tilesize + offset, rotation);

		x *= Vars.tilesize;
		y *= Vars.tilesize;
		x += offset;
		y += offset;

		Drawf.dashSquare(baseColor, x, y, range);
		Vars.indexer.eachBlock(Vars.player.team(), Tmp.r1.setCentered(x, y, range), b -> b.block.canOverdrive, selected);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = RectOverdriveBuild::new;
	}

	public class RectOverdriveBuild extends OverdriveBuild {
		@Override
		public void drawSelect() {
			drawOverlay(x, y, rotation);

			float realRange = range + phaseHeat * phaseRangeBoost;

			Vars.indexer.eachBlock(team, Tmp.r1.setCentered(x, y, realRange), other -> other.block.canOverdrive, selected);

			Drawf.dashSquare(baseColor, x, y, realRange);
		}

		@Override
		public void updateTile() {
			smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.08f);
			heat = Mathf.lerpDelta(heat, efficiency > 0 ? 1f : 0f, 0.08f);
			charge += heat * Time.delta;

			if (hasBoost) {
				phaseHeat = Mathf.lerpDelta(phaseHeat, optionalEfficiency, 0.1f);
			}

			if (charge >= reload) {
				float realRange = range + phaseHeat * phaseRangeBoost;

				charge = 0f;
				Vars.indexer.eachBlock(team, Tmp.r1.setCentered(x, y, realRange), other -> other.block.canOverdrive, other -> other.applyBoost(realBoost(), reload + 1f));
			}

			if (efficiency > 0) {
				useProgress += delta();
			}

			if (useProgress >= useTime) {
				consume();
				useProgress %= useTime;
			}
		}
	}
}
