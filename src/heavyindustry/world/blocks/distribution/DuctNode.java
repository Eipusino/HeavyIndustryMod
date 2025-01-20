package heavyindustry.world.blocks.distribution;

import arc.*;
import arc.graphics.g2d.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.graphics.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;

public class DuctNode extends Duct {
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
		return new TextureRegion[]{region};
	}

	public class DuctNodeBuild extends DuctBuild {
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
