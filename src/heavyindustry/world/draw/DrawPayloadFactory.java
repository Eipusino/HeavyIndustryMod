package heavyindustry.world.draw;

import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.world.*;

import static arc.Core.*;
import static heavyindustry.util.Utils.*;

@SuppressWarnings("unchecked")
public class DrawPayloadFactory<E extends Building> extends DrawDirSpliceBlock<E> {
	public TextureRegion topRegion, outRegion;

	public Cons<E> drawPayload = e -> {};
	public String suffix = "";

	@Override
	public void load(Block block) {
		String name = block.name;
		int size = block.size;

		topRegion = atlas.find(name + "-top", "factory-top-" + size + suffix);
		outRegion = atlas.find(name + "-out", "factory-out-" + size + suffix);

		Pixmap[] splicers = new Pixmap[4];

		PixmapRegion region = atlas.getPixmap(atlas.find(name + "-in", "factory-in-" + size + suffix));
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
		Draw.rect(regions[spliceBits.get((E) build)], build.x, build.y);
		Draw.rect(outRegion, build.x, build.y, build.rotdeg());

		drawPayload.get((E) build);

		Draw.rect(topRegion, build.x, build.y);
	}

	@Override
	public TextureRegion[] icons(Block block) {
		return new TextureRegion[]{block.region, outRegion, topRegion};
	}
}
