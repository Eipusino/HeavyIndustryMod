package heavyindustry.world.draw;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import arc.util.Tmp;
import heavyindustry.graphics.Drawn;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

import static mindustry.Vars.tilesize;

public class DrawStrobePower extends DrawBlock {
	public TextureRegion colorRegion, previewRegion;
	public String suffix = "-power";
	public float minValue = 0.75f;
	public boolean reverse = true;

	public boolean drawPlan = true;

	/** Any number <=0 disables layer changes. */
	public float layer = -1;

	public DrawStrobePower() {
	}

	public DrawStrobePower(String suffix) {
		this.suffix = suffix;
	}

	@Override
	public void draw(Building build) {
		float z = Draw.z();
		if (layer > 0) Draw.z(layer);
		float status = reverse ? 1f - build.power.status : build.power.status;
		Tmp.c1.set(Color.red).value(minValue + (status * (1f - minValue)));
		Draw.color(Drawn.applyStrobeHue(Tmp.c1));
		if (colorRegion.found()) {
			Draw.rect(colorRegion, build.x, build.y);
		} else {
			Fill.square(build.x, build.y, (tilesize * build.block.size / 2f - 1) * Draw.xscl);
		}
		Draw.color();
		Draw.z(z);
	}

	@Override
	public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
		if (!drawPlan || !previewRegion.found()) return;
		Draw.rect(previewRegion, plan.drawx(), plan.drawy());
	}

	@Override
	public TextureRegion[] icons(Block block) {
		return previewRegion.found() ? new TextureRegion[]{previewRegion} : new TextureRegion[]{};
	}

	@Override
	public void load(Block block) {
		colorRegion = Core.atlas.find(block.name + suffix);
		previewRegion = Core.atlas.find(block.name + suffix + "-preview");
	}
}
