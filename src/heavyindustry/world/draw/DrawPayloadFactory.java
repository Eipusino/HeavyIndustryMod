package heavyindustry.world.draw;

import arc.Core;
import arc.func.Cons;
import arc.func.Intf;
import arc.graphics.Pixmap;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.PixmapRegion;
import arc.graphics.g2d.TextureRegion;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;

import static heavyindustry.graphics.HPixmaps.rotatePixmap90;

public class DrawPayloadFactory extends DrawDirSpliceBlock {
	public TextureRegion topRegion, outRegion;

	public Cons<Building> drawPayload;
	public String suffix = "";

	@SuppressWarnings("unchecked")
	public <E extends Building> DrawPayloadFactory(Intf<E> splice, Cons<E> draws) {
		super(splice);

		drawPayload = (Cons<Building>) draws;
	}

	public DrawPayloadFactory() {
		this(e -> 0, e -> {});
	}

	@Override
	public void load(Block block) {
		String name = block.name;
		int size = block.size;

		topRegion = Core.atlas.find(name + "-top", "factory-top-" + size + suffix);
		outRegion = Core.atlas.find(name + "-out", "factory-out-" + size + suffix);

		Pixmap[] splicers = new Pixmap[4];

		PixmapRegion region = Core.atlas.getPixmap(Core.atlas.find(name + "-in", "factory-in-" + size + suffix));
		Pixmap pixmap = region.crop();
		for (int i = 0; i < 4; i++) {
			Pixmap m = i == 1 || i == 2 ? rotatePixmap90(pixmap.flipY(), i) : rotatePixmap90(pixmap, i);
			splicers[i] = m;
		}

		for (int i = 0; i < regions.length; i++) {
			regions[i] = getSpliceRegion(splicers, i);
		}

		for (Pixmap p : splicers) {
			p.dispose();
		}
	}

	@Override
	public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
		Draw.rect(block.region, plan.drawx(), plan.drawy());
		super.drawPlan(block, plan, list);
		Draw.rect(outRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
		Draw.rect(topRegion, plan.drawx(), plan.drawy());
	}

	@Override
	public void draw(Building build) {
		Draw.rect(build.block.region, build.x, build.y);
		Draw.rect(regions[spliceBits.get(build)], build.x, build.y);
		Draw.rect(outRegion, build.x, build.y, build.rotdeg());

		drawPayload.get(build);

		Draw.rect(topRegion, build.x, build.y);
	}

	@Override
	public TextureRegion[] icons(Block block) {
		return new TextureRegion[]{block.region, outRegion, topRegion};
	}
}
