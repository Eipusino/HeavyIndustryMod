package heavyindustry.world.draw;

import arc.Core;
import arc.func.Floatf;
import arc.func.Func;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class DrawRegionDynamic extends DrawBlock {
	public Floatf<Building> rotation;
	public Floatf<Building> alpha;
	public Func<Building, Color> color;

	public TextureRegion region;
	public String suffix;

	public boolean spinSprite = false;
	public boolean drawPlan = false;
	public boolean planRotate = true;
	public float x, y;
	/** Any number <=0 disables layer changes. */
	public float layer = -1;

	public boolean makeIcon = false;

	public DrawRegionDynamic() {
		this(e -> 0, e -> 1, "");
	}

	public <E extends Building> DrawRegionDynamic(Floatf<E> rotations, Floatf<E> alphas, String fix) {
		this(rotations, alphas, null, fix);
	}

	@SuppressWarnings("unchecked")
	public <E extends Building> DrawRegionDynamic(Floatf<E> rotations, Floatf<E> alphas, Func<E, Color> colors, String name) {
		rotation = (Floatf<Building>) rotations;
		alpha = (Floatf<Building>) alphas;
		color = (Func<Building, Color>) colors;
		suffix = name;
	}

	@Override
	public void draw(Building build) {
		float alp = alpha.get(build);
		if (alp <= 0.01f) return;

		float z = Draw.z();

		if (layer > 0) Draw.z(layer);
		if (color != null) Draw.color(color.get(build));

		Draw.alpha(alp);

		if (spinSprite) {
			Drawf.spinSprite(region, build.x + x, build.y + y, rotation.get(build));
		} else {
			Draw.rect(region, build.x + x, build.y + y, rotation.get(build));
		}
		Draw.color();
		Draw.z(z);
	}

	@Override
	public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
		if (!drawPlan) return;
		Draw.rect(region, plan.drawx(), plan.drawy(), planRotate ? plan.rotation * 90 : 0);
	}

	@Override
	public TextureRegion[] icons(Block block) {
		return makeIcon ? new TextureRegion[]{region} : new TextureRegion[]{block.region};
	}

	@Override
	public void load(Block block) {
		region = Core.atlas.find(block.name + suffix);
	}
}
