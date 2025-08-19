package heavyindustry.world.blocks.liquid;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Eachable;
import heavyindustry.input.BeltPlacement;
import mindustry.Vars;
import mindustry.core.Renderer;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;

public class PipeBridge extends MergingLiquidBlock {
	private static BuildPlan otherReq;
	private int otherDst = 0;
	private boolean interrupted;

	public TextureRegion top1, top2, bridgeRegion1, bridgeRegion2, arrowRegion;

	public int range = 6;
	public Pipe underPipe;

	public PipeBridge(String name) {
		super(name);
		rotate = true;
		drawArrow = false;
		allowDiagonal = false;
	}

	@Override
	public void load() {
		super.load();

		top1 = Core.atlas.find(name + "-top1");
		top2 = Core.atlas.find(name + "-top2");
		bridgeRegion1 = Core.atlas.find(name + "-bridge1");
		bridgeRegion2 = Core.atlas.find(name + "-bridge2");
		arrowRegion = Core.atlas.find(name + "-arrow");
	}

	@Override
	public void init() {
		super.init();

		//if (underPipe == null) underPipe = (Pipe) HBlocks.pipe;
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.add(Stat.range, range, StatUnit.blocks);
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		Draw.rect(region, plan.drawx(), plan.drawy());
		Draw.rect(plan.rotation == 0 || plan.rotation == 3 ? top1 : top2, plan.drawx(), plan.drawy(), plan.rotation * 90f);
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		super.drawPlace(x, y, rotation, valid);

		drawPlace(x, y, rotation, valid, true);
	}

	public void drawPlace(int x, int y, int rotation, boolean valid, boolean line) {
		int length = range;
		Building found = null;
		int dx = Geometry.d4x(rotation), dy = Geometry.d4y(rotation);

		for (int i = 1; i <= range; i++) {
			Tile other = Vars.world.tile(x + dx * i, y + dy * i);

			if (other != null && other.build instanceof PipeBridgeBuild build && build.rotation == (rotation + 2) % 4 && build.block == this && build.team == Vars.player.team()) {
				length = i;
				found = other.build;
				break;
			}
		}

		if (line || found != null) {
			Drawf.dashLine(Pal.placing,
					x * tilesize + dx * (tilesize / 2f + 2),
					y * tilesize + dy * (tilesize / 2f + 2),
					x * tilesize + dx * (length) * tilesize,
					y * tilesize + dy * (length) * tilesize
			);
		}

		if (found != null) {
			if (line) {
				Drawf.square(found.x, found.y, found.block.size * tilesize / 2f + 2.5f, 0f);
			} else {
				Drawf.square(found.x, found.y, 2f);
			}
		}
	}

	public void drawBridge(int rotation, float x1, float y1, float x2, float y2) {
		Draw.alpha(Renderer.bridgeOpacity);
		float
				angle = Angles.angle(x1, y1, x2, y2),
				cx = (x1 + x2) / 2f,
				cy = (y1 + y2) / 2f,
				len = Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2)) - size * tilesize;

		TextureRegion bridgeRegion = rotation == 0 || rotation == 3 ? bridgeRegion1 : bridgeRegion2;

		Draw.rect(bridgeRegion, cx, cy, len, bridgeRegion.height * bridgeRegion.scl(), angle);

		for (float i = 6f; i <= len + size * tilesize - 5f; i += 5f) {
			Draw.rect(arrowRegion, x1 + Geometry.d4x(rotation) * i, y1 + Geometry.d4y(rotation) * i, angle);
		}

		Draw.reset();
	}

	@Override
	public void drawPlanConfigTop(BuildPlan plan, Eachable<BuildPlan> list) {
		otherReq = null;
		otherDst = range;
		interrupted = false;
		Point2 d = Geometry.d4(plan.rotation);
		list.each(other -> {
			if (!interrupted && other.block == this && plan != other && Mathf.clamp(other.x - plan.x, -1, 1) == d.x && Mathf.clamp(other.y - plan.y, -1, 1) == d.y) {
				if (other.rotation == plan.rotation) {
					interrupted = true;
					return;
				}

				int dst = Math.max(Math.abs(other.x - plan.x), Math.abs(other.y - plan.y));
				if (dst <= otherDst) {
					otherReq = other;
					otherDst = dst;
				}
			}
		});

		if (otherReq != null) {
			drawBridge(plan.rotation, plan.drawx(), plan.drawy(), otherReq.drawx(), otherReq.drawy());
		}
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{region, top1};
	}

	@Override
	public void changePlacementPath(Seq<Point2> points, int rotation) {
		BeltPlacement.calculateNodes(points, this, rotation, (point, other) -> Math.max(Math.abs(point.x - other.x), Math.abs(point.y - other.y)) <= range);
	}

	@Override
	public void handlePlacementLine(Seq<BuildPlan> plans) {
		boolean flip = false;
		for (BuildPlan plan : plans) {
			if (flip) {
				plan.rotation = (plan.rotation + 2) % 4;
			}
			flip = !flip;
		}
	}

	public boolean positionsValid(int x1, int y1, int x2, int y2) {
		if (x1 == x2) {
			return Math.abs(y1 - y2) <= range;
		} else if (y1 == y2) {
			return Math.abs(x1 - x2) <= range;
		} else {
			return false;
		}
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = PipeBridgeBuild::new;
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
				if ((underBlending & (1 << i)) != 0) {
					int j = i % 2 == 0 ? i : i + 2;
					underPipe.drawAt(
							x + Geometry.d4x(j) * tilesize,
							y + Geometry.d4y(j) * tilesize,
							0, i % 2,
							liquids.current(), liquids.currentAmount() / liquidCapacity
					);
				}
			}
			Draw.z(Layer.block);

			Draw.rect(region, x, y);
			Draw.rect(rotation == 0 || rotation == 3 ? top1 : top2, x, y, rotation * 90f);

			PipeBridgeBuild link = findLink();
			if (link != null) {
				Draw.z(Layer.power - 1);
				drawBridge(rotation, x, y, link.x, link.y);
			}
		}

		@Override
		public void drawSelect() {
			drawPlace(tile.x, tile.y, rotation, true, false);
		}

		@Override
		public void onProximityUpdate() {
			super.onProximityUpdate();

			int[] bits = underPipe.buildBlending(tile, 0, null, true);
			underBlending = bits[4];
		}
	}
}
