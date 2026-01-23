package endfield.world.blocks.storage;

import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import endfield.world.draw.DrawTeam;
import mindustry.entities.units.BuildPlan;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawMulti;

public class DrawerCoreBlock extends CoreBlock {
	public DrawBlock drawer = new DrawMulti(new DrawDefault(), new DrawTeam());

	public DrawerCoreBlock(String name) {
		super(name);
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		drawer.drawPlan(this, plan, list);
	}

	@Override
	protected TextureRegion[] icons() {
		return drawer.finalIcons(this);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = DrawerCoreBuild::new;
	}

	public class DrawerCoreBuild extends CoreBuild {
		@Override
		public void draw() {
			drawer.draw(this);
		}

		@Override
		public void drawLight() {
			drawer.drawLight(this);
		}
	}
}