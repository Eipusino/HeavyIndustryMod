package endfield.world.blocks.production;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.world.blocks.production.BurstDrill;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;

public class DrawerBurstDrill extends BurstDrill {
	public DrawBlock drawer = new DrawDefault();

	public DrawerBurstDrill(String name) {
		super(name);
	}

	@Override
	public void load() {
		super.load();
		drawer.load(this);
	}

	@Override
	public TextureRegion[] icons() {
		return drawer.icons(this);
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		drawer.drawPlan(this, plan, list);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = DrawerBurstDrillBuild::new;
	}

	public class DrawerBurstDrillBuild extends BurstDrillBuild {
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
	}
}
