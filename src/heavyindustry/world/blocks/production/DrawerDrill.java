package heavyindustry.world.blocks.production;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.world.blocks.production.Drill;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;

public class DrawerDrill extends Drill {
	public DrawBlock drawer = new DrawDefault();

	public DrawerDrill(String name) {
		super(name);
	}

	@Override
	public void load() {
		super.load();
		drawer.load(this);
	}

	@Override
	public TextureRegion[] icons() {
		return drawer.finalIcons(this);
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		drawer.drawPlan(this, plan, list);
	}

	public class DrawerDrillBuild extends DrillBuild {
		@Override
		public void draw() {
			drawer.draw(this);
			if (dominantItem != null && drawMineItem) {
				Draw.color(dominantItem.color);
				Draw.rect(itemRegion, x, y);
				Draw.color();
			}

			drawTeamTop();
		}

		@Override
		public float totalProgress() {
			return timeDrilled;
		}
	}
}
