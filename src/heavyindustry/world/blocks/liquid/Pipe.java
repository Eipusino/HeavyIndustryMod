package heavyindustry.world.blocks.liquid;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.util.Eachable;
import arc.util.Tmp;
import heavyindustry.util.Utils;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.Autotiler;

public class Pipe extends MergingLiquidBlock implements Autotiler {
	static final float rotatePad = 6;
	static final float rotateHpad = 0.75f;
	static final float[][] rotateOffsets = new float[][]{{0.75f, 0.75f}, {-0.75f, 0.75f}, {-0.75f, -0.75f}, {0.75f, -0.75f}};

	static final byte[][] blendIndices = {
			{0, 0}, {0, 0}, {0, 1}, {1, 3},
			{0, 0}, {0, 0}, {1, 2}, {2, 3},
			{0, 1}, {1, 0}, {0, 1}, {2, 0},
			{1, 1}, {2, 1}, {2, 2}, {3, 0}
	};

	public TextureRegion[] bottomRegions;
	public TextureRegion[][] regions;
	public TextureRegion[][][] rotateRegions;

	public Pipe(String name) {
		super(name);
		solid = false;
		conveyorPlacement = true;
	}

	@Override
	public void load() {
		super.load();

		regions = Utils.splitLayers(name + "-top", 32, 4);

		bottomRegions = Utils.split(name + "-bottom", size * 32, 0);

		// everything about this mod is terrible, and I hate it and i
		rotateRegions = new TextureRegion[4][2][Liquid.animationFrames];

		if (Vars.renderer != null) {
			float pad = rotatePad;
			TextureRegion[][] frames = Vars.renderer.getFluidFrames();

			for (int rot = 0; rot < 4; rot++) {
				for (int fluid = 0; fluid < 2; fluid++) {
					for (int frame = 0; frame < Liquid.animationFrames; frame++) {
						TextureRegion base = frames[fluid][frame];
						TextureRegion result = new TextureRegion();
						result.set(base);

						if (rot == 0) {
							result.setX(result.getX() + pad);
							result.setHeight(result.height - pad);
						} else if (rot == 1) {
							result.setWidth(result.width - pad);
							result.setHeight(result.height - pad);
						} else if (rot == 2) {
							result.setWidth(result.width - pad);
							result.setY(result.getY() + pad);
						} else {
							result.setX(result.getX() + pad);
							result.setY(result.getY() + pad);
						}

						rotateRegions[rot][fluid][frame] = result;
					}
				}
			}
		}
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		int[] bits = getTiling(plan, list);

		if (bits == null) return;

		byte[] blending = blendIndices[bits[3]];
		int index1 = blending[0];
		int index2 = blending[1];

		Draw.rect(bottomRegions[index1 == 1 ? 1 : 0], plan.drawx(), plan.drawy(), index2 * 90f);
		Draw.rect(regions[index1][index2], plan.drawx(), plan.drawy());
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{bottomRegions[0], regions[0][0]};
	}

	@Override
	public boolean blends(Tile tile, int rotation, int otherx, int othery, int otherrot, Block otherblock) {
		return otherblock.hasLiquids;
	}

	public void drawAt(float x, float y, int index1, int index2, Liquid liquid, float fullness) {
		Draw.rect(bottomRegions[index1 == 1 ? 1 : 0], x, y, index2 * 90f);

		int frame = liquid.getAnimationFrame();
		int gas = liquid.gas ? 1 : 0;
		float ox = 0f, oy = 0f;
		TextureRegion liquidr = index1 == 1 ? rotateRegions[index2][gas][frame] : Vars.renderer.fluidFrames[gas][frame];

		if (index1 == 1) {
			ox = rotateOffsets[index2][0];
			oy = rotateOffsets[index2][1];
		}

		Drawf.liquid(liquidr, x + ox, y + oy, fullness, liquid.color.write(Tmp.c1).a(1f));

		Draw.rect(regions[index1][index2], x, y);
	}

	public class PipeBuild extends MergingLiquidBuild {
		public int index1, index2, underBlending;

		@Override
		public void draw() {
			Draw.z(Layer.blockUnder);
			for (int i = 0; i < 4; i++) {
				if ((underBlending & (1 << i)) != 0) {
					int j = i % 2 == 0 ? i : i + 2;
					drawAt(
							x + Geometry.d4x(j) * Vars.tilesize,
							y + Geometry.d4y(j) * Vars.tilesize,
							0, i % 2,
							liquids.current(), liquids.currentAmount() / liquidCapacity
					);
				}
			}
			Draw.z(Layer.block);
			drawAt(x, y, index1, index2, liquids.current(), liquids.currentAmount() / liquidCapacity);
		}

		@Override
		public void onProximityUpdate() {
			super.onProximityUpdate();

			int[] bits = buildBlending(tile, 0, null, true);
			underBlending = bits[4];

			byte[] blending = blendIndices[bits[3]];
			index1 = blending[0];
			index2 = blending[1];
		}
	}
}