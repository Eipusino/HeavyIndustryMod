package heavyindustry.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import arc.util.Eachable;
import mindustry.entities.units.BuildPlan;
import mindustry.graphics.Pal;
import mindustry.world.Block;

import static heavyindustry.struct.Collectionsf.arrayOf;

public class DuctNode extends TubeDuct {
	public int chainLimit = 2;
	public TextureRegion topRegion;

	public DuctNode(String name) {
		super(name);
	}

	@Override
	public void load() {
		super.load();
		topRegion = Core.atlas.find(name + "-top");
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		Draw.rect(region, plan.drawx(), plan.drawy());
		Draw.color(Pal.accent);
		Draw.rect(topRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
		Draw.color();
	}

	@Override
	public Block getReplacement(BuildPlan req, Seq<BuildPlan> plans) {
		return this;
	}

	@Override
	public TextureRegion[] icons() {
		return arrayOf(region);
	}

	public class DuctNodeBuild extends TubeDuctBuild {
		public int chainCount, lastChainCount;

		@Override
		public void draw() {
			Draw.rect(region, x, y);
			Draw.color(lastChainCount >= chainLimit ? Pal.remove : Pal.accent);
			Draw.rect(topRegion, x, y, rotdeg());
			Draw.color();
		}

		@Override
		protected void drawAt(float x, float y, int bits, float rotation, SliceMode slice) {}

		@Override
		public void update() {
			if (next != null && next instanceof DuctNodeBuild duct) {
				duct.chainCount = chainCount + 1;
			}

			if (chainCount < chainLimit) {
				super.update();
			}

			lastChainCount = chainCount;
			chainCount = 0;
		}
	}
}
