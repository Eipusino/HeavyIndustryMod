package heavyindustry.world.draw;

import arc.Core;
import arc.func.Boolf2;
import arc.func.Intf;
import arc.graphics.Pixmap;
import arc.graphics.Pixmaps;
import arc.graphics.Texture;
import arc.graphics.Texture.TextureFilter;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Point2;
import arc.util.Eachable;
import heavyindustry.util.DirEdges;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

import static heavyindustry.util.Utils.split;

public class DrawAntiSpliceBlock extends DrawBlock {
	protected static final String[] splices = {"right", "right-top", "top", "left-top", "left", "left-bot", "bot", "right-bot"};

	public TextureRegion[] drawRegions = new TextureRegion[256];//Space for time, don't use this type too much.
	public Boolf2<BuildPlan, BuildPlan> planSplicer = (plan, other) -> false;
	public Intf<Building> splicer;

	public TextureRegion icon;

	public float layerOffset = 0.0001f;
	public boolean layerRec = true;
	public boolean split = false;

	public boolean interConner;

	@SuppressWarnings("unchecked")
	public <E extends Building> DrawAntiSpliceBlock(Intf<E> splicers) {
		splicer = (Intf<Building>) splicers;
	}

	public DrawAntiSpliceBlock() {
		this(e -> 0);
	}

	@Override
	public void load(Block block) {
		icon = Core.atlas.find(block.name + "-icon");

		if (split) {
			drawRegions = split(block.name + "-sheet", 32, 16, 16);
		} else {
			Pixmap[] regions = new Pixmap[8];
			Pixmap[] inner = new Pixmap[4];

			for (int i = 0; i < regions.length; i++) {
				regions[i] = Core.atlas.getPixmap(block.name + "-" + splices[i]).crop();
			}
			for (int i = 0; i < inner.length; i++) {
				inner[i] = Core.atlas.getPixmap(block.name + "-" + splices[i * 2 + 1] + "-inner").crop();
			}

			for (int i = 0; i < drawRegions.length; i++) {
				drawRegions[i] = getRegionWithBit(regions, inner, i);
			}
		}
	}

	@Override
	public TextureRegion[] icons(Block block) {
		return new TextureRegion[]{icon};
	}

	protected TextureRegion getRegionWithBit(Pixmap[] regions, Pixmap[] inner, int index) {
		Pixmap pixmap = new Pixmap(regions[0].width, regions[0].height);

		for (int i = 0; i < 8; i++) {
			if (i % 2 == 0 && (index & (1 << i)) == 0) {
				pixmap.draw(regions[i], true);
			}
		}
		for (int i = 0; i < 8; i++) {
			if ((i + 1) % 2 == 0) {
				int dirBit = 1 << (i + 1) % 8 | 1 << (i - 1);
				if ((index & dirBit) == 0) pixmap.draw(regions[i], true);
				else if ((index & dirBit) == dirBit && (interConner || (index & (1 << i)) == 0))
					pixmap.draw(inner[i / 2], true);
			}
		}

		Pixmaps.bleed(pixmap, 2);
		Texture texture = new Texture(pixmap);
		texture.setFilter(TextureFilter.linear);
		return new TextureRegion(texture);
	}

	@Override
	public void draw(Building build) {
		float z = Draw.z();
		Draw.z(z + layerOffset);
		Draw.rect(drawRegions[splicer.get(build)], build.x, build.y);
		if (layerRec) Draw.z(z);
	}

	@Override
	public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
		int data = 0;
		Block planBlock = plan.block;

		t:
		for (int i = 0; i < 8; i++) {
			Block other = null;
			for (Point2 p : DirEdges.get8(plan.block.size, i)) {
				int x = plan.x + p.x;
				int y = plan.y + p.y;
				BuildPlan[] target = {null};

				list.each(pl -> {
					if (target[0] != null) return;
					if (pl.x == x && pl.y == y) {
						target[0] = pl;
					}
				});

				if (target[0] == null) continue t;

				if (other == null) {
					if (planSplicer.get(plan, target[0])) {
						other = target[0].block;
					} else {
						continue t;
					}
				} else if (other != planBlock || !planSplicer.get(plan, target[0])) {
					continue t;
				}
			}
			data |= 1 << i;
		}

		Draw.rect(drawRegions[data], plan.drawx(), plan.drawy());
	}
}
