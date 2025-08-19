package heavyindustry.world.blocks.liquid;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.util.Tmp;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.blocks.liquid.Conduit;

import static heavyindustry.util.Utils.splitLayers;
import static mindustry.Vars.renderer;

public class TubeConduit extends Conduit {
	static final float[][] rotateOffsets = new float[][]{{0.75f, 0.75f}, {-0.75f, 0.75f}, {-0.75f, -0.75f}, {0.75f, -0.75f}};
	static final byte[][] ductArrows = {
			{1, 1, 1, 0, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 2, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 0, 1, 1, 1},
			{1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 2, 1, 1, 1}
	};

	public TextureRegion[][] regions;

	public TubeConduit(String name) {
		super(name);
	}

	@Override
	public void load() {
		super.load();
		regions = splitLayers(name + "-sheet", 32, 2);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = TubeConduitBuild::new;
	}

	public class TubeConduitBuild extends ConduitBuild {
		public int tiling = 0;

		@Override
		public void draw() {
			Draw.z(Layer.blockUnder);
			Draw.rect(regions[1][0], x, y, 0f);

			int frame = liquids.current().getAnimationFrame();
			int gas = liquids.current().gas ? 1 : 0;
			float ox = 0f, oy = 0f;
			int wrapRot = (int) (rotation + offset) % 4;
			TextureRegion liquidr = blendbits == 1 ? rotateRegions[wrapRot][gas][frame] : renderer.fluidFrames[gas][frame];

			if (blendbits == 1) {
				ox = rotateOffsets[wrapRot][0];
				oy = rotateOffsets[wrapRot][1];
			}

			float xscl = Draw.xscl, yscl = Draw.yscl; //todo FUCK
			Draw.scl(1f, 1f);
			Drawf.liquid(sliced(liquidr, SliceMode.none), x + ox, y + oy, smoothLiquid, liquids.current().color.write(Tmp.c1).a(1f));
			Draw.scl(xscl, yscl);

			Draw.z(Layer.blockUnder + 0.2f);
			Draw.rect(regions[0][tiling], x, y, 0f);
			Draw.rect(regions[1][ductArrows[rotation][tiling] + 1], x, y, rotdeg());
		}

		@Override
		public void onProximityUpdate() {
			super.onProximityUpdate();

			tiling = 0;

			for (int i = 0; i < 4; i++) {
				Building b = nearby(Geometry.d4(i).x, Geometry.d4(i).y);
				if (i == rotation || b != null && (b instanceof TubeConduitBuild ? (b.front() != null && b.front() == this) : b.block.outputsLiquid)) {
					tiling |= (1 << i);
				}
			}
		}
	}
}
