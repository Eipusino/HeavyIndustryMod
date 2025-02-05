package heavyindustry.world.blocks.liquid;

import arc.graphics.g2d.*;
import arc.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.liquid.*;

import static heavyindustry.util.Utils.*;
import static mindustry.Vars.*;

public class BeltConduit extends Conduit {
	public boolean fireproof = false;
	public TextureRegion[] topParts, botParts;

	public BeltConduit(String name) {
		super(name);
	}

	@Override
	public void load() {
		super.load();
		topParts = split(name + "-top", 32, 0);
		botParts = split(name + "-bot", 32, 0);
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		int[] bits = getTiling(plan, list);

		if (bits == null) return;

		Draw.scl(bits[1], bits[2]);
		Draw.color(botColor);
		Draw.alpha(0.5f);
		Draw.rect(botParts[bits[0]], plan.drawx(), plan.drawy(), plan.rotation * 90);
		Draw.color();
		Draw.rect(topParts[bits[0]], plan.drawx(), plan.drawy(), plan.rotation * 90);
		Draw.scl();
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{region};
	}

	@Override
	public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock) {
		return noSideBlend ? (otherblock.outputsLiquid && blendsArmored(tile, rotation, otherx, othery, otherrot, otherblock)) || (lookingAt(tile, rotation, otherx, othery, otherblock) && otherblock.hasLiquids) || otherblock instanceof LiquidJunction : super.blends(tile, rotation, otherx, othery, otherrot, otherblock);
	}

	public class BeltConduitBuild extends ConduitBuild {
		@Override
		public boolean acceptLiquid(Building source, Liquid liquid) {
			return noSideBlend ? super.acceptLiquid(source, liquid) && (tile == null || source.block instanceof Conduit || source.block instanceof DirectionLiquidBridge || source.block instanceof LiquidJunction || source.tile.absoluteRelativeTo(tile.x, tile.y) == rotation || !source.proximity.contains(this)) : super.acceptLiquid(source, liquid);
		}

		@Override
		protected void drawAt(float x, float y, int bits, int rotation, SliceMode slice) {
			float angle = rotation * 90f;
			Draw.color(botColor);
			Draw.rect(sliced(botParts[bits], slice), x, y, angle);

			int offset = yscl == -1 ? 3 : 0;

			int frame = liquids.current().getAnimationFrame();
			int gas = liquids.current().gas ? 1 : 0;
			float ox = 0f, oy = 0f;
			int wrapRot = (rotation + offset) % 4;
			TextureRegion liquidr = bits == 1 && padCorners ? rotateRegions[wrapRot][gas][frame] : renderer.fluidFrames[gas][frame];

			if (bits == 1 && padCorners) {
				ox = rotateOffsets[wrapRot][0];
				oy = rotateOffsets[wrapRot][1];
			}

			//the drawing state machine sure was a great design choice with no downsides or hidden behavior!!!
			float xscl = Draw.xscl, yscl = Draw.yscl;
			Draw.scl(1f, 1f);
			Drawf.liquid(sliced(liquidr, slice), x + ox, y + oy, smoothLiquid, liquids.current().color.write(Tmp.c1).a(1f));
			Draw.scl(xscl, yscl);

			Draw.rect(sliced(topParts[bits], slice), x, y, angle);
		}
	}
}
