package heavyindustry.world.blocks.production;

import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.world.blocks.production.Incinerator;
import mindustry.world.draw.DrawBlock;

public class DrawerIncinerator extends Incinerator {
	public DrawBlock drawer;

	public DrawerIncinerator(String name) {
		super(name);
	}

	@Override
	public void load() {
		super.load();
		drawer.load(this);
	}

	@Override
	protected TextureRegion[] icons() {
		return drawer.finalIcons(this);
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		drawer.drawPlan(this, plan, list);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = DrawerIncineratorBuild::new;
	}

	public class DrawerIncineratorBuild extends IncineratorBuild {
		@Override
		public void draw() {
			drawer.draw(this);
		}

		@Override
		public float warmup() {
			return heat;
		}
	}
}
