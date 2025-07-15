package heavyindustry.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.world.blocks.distribution.DuctRouter;

import static heavyindustry.struct.Collectionsf.arrayOf;

public class XFDirectionalRouter extends DuctRouter {
	public TextureRegion baseRegion, itemRegion;

	public XFDirectionalRouter(String name) {
		super(name);

		placeableLiquid = true;
		drawTeamOverlay = false;
	}

	@Override
	public void load() {
		super.load();
		baseRegion = Core.atlas.find(name + "-base");
		itemRegion = Core.atlas.find(name + "-item");
		topRegion = Core.atlas.find(name + "-overlay");
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		Draw.rect(baseRegion, plan.drawx(), plan.drawy());
		Draw.rect(topRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
	}

	public TextureRegion[] icons() {
		return arrayOf(region);
	}

	public class XFDirectionalRouterBuild extends DuctRouterBuild {
		@Override
		public void draw() {
			Draw.rect(baseRegion, x, y);
			if (sortItem != null) {
				Draw.color(sortItem.color);
				Draw.rect(itemRegion, x, y);
				Draw.color();
			}
			Draw.rect(topRegion, x, y, rotdeg());
		}
	}
}
