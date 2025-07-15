package heavyindustry.world.blocks.liquid;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Tile;

import static heavyindustry.struct.Collectionsf.arrayOf;

public class PipeBridge extends MergingLiquidBlock {
	public TextureRegion top1, top2, bridgeRegion1, bridgeRegion2;

	public int range = 6;
	public Pipe underPipe;

	public PipeBridge(String name) {
		super(name);
		rotate = true;
	}

	@Override
	public void load() {
		super.load();

		top1 = Core.atlas.find(name + "-top1");
		top2 = Core.atlas.find(name + "-top2");
		bridgeRegion1 = Core.atlas.find(name + "-bridge1");
		bridgeRegion2 = Core.atlas.find(name + "-bridge2");
	}

	@Override
	public TextureRegion[] icons() {
		return arrayOf(region, top1);
	}

	public class PipeBridgeBuild extends MergingLiquidBuild {
		public int underBlending;

		@Override
		public void moveLiquids() {
			dumpLiquid(liquids.current(), 2f, 2);
		}

		@Override
		public Seq<Building> chainTargets() {
			return Seq.with(back(), findLink());
		}

		public PipeBridgeBuild findLink() {
			for (int i = 1; i <= range; i++) {
				Tile other = tile.nearby(Geometry.d4x(rotation) * i, Geometry.d4y(rotation) * i);
				if (other != null && other.build instanceof PipeBridgeBuild build && build.block == PipeBridge.this && build.rotation == (rotation + 2) % 4 && build.team == team) {
					return build;
				}
			}
			return null;
		}

		@Override
		public void draw() {
			Draw.z(Layer.blockUnder);
			for (int i = 0; i < 4; i++) {
				if ((underBlending & (1 << i)) != 0 && underPipe != null) {
					int j = i % 2 == 0 ? i : i + 2;
					underPipe.drawAt(
							x + Geometry.d4x(j) * Vars.tilesize,
							y + Geometry.d4y(j) * Vars.tilesize,
							0, i % 2,
							liquids.current(), liquids.currentAmount() / liquidCapacity
					);
				}
			}
			Draw.z(Layer.block);

			Draw.rect(region, x, y);

			Draw.rect(rotation == 0 || rotation == 3 ? top1 : top2, x, y, rotation * 90f);
		}

		@Override
		public void onProximityUpdate() {
			super.onProximityUpdate();

			if (underPipe != null) {
				int[] bits = underPipe.buildBlending(tile, 0, null, true);
				underBlending = bits[4];
			}
		}
	}
}
