package heavyindustry.world.blocks.distribution;

import arc.Core;
import arc.func.Boolf;
import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.struct.OrderedMap;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.core.Renderer;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.ItemBuffer;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

/**
 * A bridge with the same connection method as the power node.
 */
public class TubeItemBridge extends ItemBridge {
	public final int timerAccept;
	public Prov<Seq<Block>> connectBlocksGetter = () -> new Seq<>(Block.class);
	public byte maxConnections = 3;
	public int bufferCapacity;

	protected Seq<Block> connectibleBlocks = new Seq<>(Block.class);

	public Boolf<Building> connectFilter = building -> connectibleBlocks.contains(building.block);

	public TubeItemBridge(String name) {
		super(name);
		hasItems = true;
		timerAccept = timers++;
		bufferCapacity = 50;
		hasPower = false;
		canOverdrive = true;
		swapDiagonalPlacement = true;

		configClear((TubeItemBridgeBuild tile) -> tile.link = -1);
	}

	public TubeItemBridgeBuild cast(Building build) {
		return (TubeItemBridgeBuild) build;
	}

	@Override
	public void init() {
		super.init();
		Seq<Block> blocks = connectBlocksGetter.get();
		if (blocks == null) blocks = new Seq<>(Block.class);
		blocks.add(this);
		connectibleBlocks = blocks;
		maxConnections++;
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.add(Stat.range, range, StatUnit.blocks);
		stats.add(Stat.powerConnections, maxConnections - 1, StatUnit.none);
	}

	@Override
	public void setBars() {
		super.setBars();
		addBar("connections", tile -> new Bar(() ->
				Core.bundle.format("bar.powerlines", cast(tile).realConnections(), maxConnections - 1),
				() -> Pal.items,
				() -> (float) cast(tile).realConnections() / (float) (maxConnections - 1)
		));
	}

	@Override
	public void drawBridge(BuildPlan req, float ox, float oy, float flip) {
		drawBridge(bridgeRegion, endRegion, new Vec2(req.drawx(), req.drawy()), new Vec2(ox, oy));
		Draw.rect(arrowRegion,
				(req.drawx() + ox) / 2f,
				(req.drawy() + oy) / 2f,
				Angles.angle(req.drawx(), req.drawy(), ox, oy)
		);
	}

	public void drawBridge(TextureRegion bridgeRegion, TextureRegion endRegion, Vec2 pos1, Vec2 pos2) {
		float angle = pos1.angleTo(pos2) - 90;

		if (angle >= 0f && angle < 180f) Draw.yscl = -1f;

		Tmp.v1.set(pos2.x, pos2.y).sub(pos1.x, pos1.y).setLength(tilesize / 2f);

		Lines.stroke(8 * Draw.yscl);
		Lines.line(bridgeRegion, pos1.x + Tmp.v1.x, pos1.y + Tmp.v1.y, pos2.x - Tmp.v1.x, pos2.y - Tmp.v1.y, false);

		Draw.rect(endRegion, pos1.x, pos1.y, angle + 90f);
		Draw.xscl = -1f;
		Draw.rect(endRegion, pos2.x, pos2.y, angle + 90f);
		Draw.xscl = Draw.yscl = 1f;
	}

	public Tile findLink(int x, int y) {
		return findLinkTile(x, y, true);
	}

	public Tile findLinkTile(int x, int y, boolean checkBlock) {
		Tile tile = world.tile(x, y);
		if (tile != null && lastBuild != null && lastBuild.tile != tile) {
			boolean validLink = checkBlock ? linkValid(tile, lastBuild.tile) && lastBuild.link == -1 :
					linkValid(tile, lastBuild.tile, false, true);
			if (validLink) return lastBuild.tile;
		}
		return null;
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		Tile link = findLinkTile(x, y, false);
		Lines.stroke(1f);
		Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range * tilesize, Pal.placing);

		Draw.reset();
		Draw.color(Pal.placing);
		if (link != null && world.build(link.x, link.y) instanceof TubeItemBridgeBuild && Math.abs(link.x - x) + Math.abs(link.y - y) > 1) {
			Vec2 end = new Vec2(x, y), start = new Vec2(link.x, link.y);
			float angle = Tmp.v1.set(start).sub(end).angle() + 90;
			float layer = Draw.z();
			Draw.z(Layer.blockUnder - 0.3f);

			Lines.poly(new Vec2[]{
					start.cpy().add(Tmp.v1.trns(angle, -0.4f)),
					end.cpy().add(Tmp.v1.trns(angle, -0.4f)),
					end.cpy().add(Tmp.v1.trns(angle, 0.4f)),
					start.cpy().add(Tmp.v1.trns(angle, 0.4f)),
			}, 0, 0, 8);

			Tmp.v1.set(start).sub(end).setLength(4);
			Vec2 arrowOffset = Tmp.v1.cpy().setLength(1);
			Draw.rect("bridge-arrow", start.x * 8 - arrowOffset.x * 8, start.y * 8 - arrowOffset.y * 8, angle + 90);
			Draw.z(layer);
		}

		Draw.reset();
	}

	/** Change its connection method to range connection. */
	@Override
	public boolean linkValid(Tile tile, Tile other) {
		return linkValid(tile, other, true);
	}

	@Override
	public boolean linkValid(Tile tile, Tile other, boolean checkDouble) {
		return linkValid(tile, other, checkDouble, false);
	}

	public boolean linkValid(Tile tile, Tile other, boolean checkDouble, boolean old) {
		if (old) {
			if (other != null && tile != null && positionsValid(tile.x, tile.y, other.x, other.y)) {
				return (other.block() == tile.block() && tile.block() == this || !(tile.block() instanceof ItemBridge) && other.block() == this) && (other.team() == tile.team() || tile.block() != this) && (!checkDouble || ((ItemBridgeBuild) other.build).link != tile.pos());
			} else {
				return false;
			}
		} else {
			check: {
				if (!(other != null && tile != null) || other.build == null || tile.build == null) break check;
				other = other.build.tile;
				tile = tile.build.tile;
				int offset = other.block().isMultiblock() ? Mathf.floor(other.block().size / 2f) : 0;
				boolean b2 = tile.pos() != other.pos();
				if (tile.block() == this) {
					Vec2 offVec = Tmp.v1.trns(tile.angleTo(other) + 90f, offset, offset);
					if (!positionsValid(tile.x, tile.y, Mathf.ceil(other.x + offVec.x), Mathf.ceil(other.y + offVec.y)))
						break check;
					boolean connected = false;
					if (other.build instanceof ItemBridgeBuild bridge) {
						connected = bridge.link == tile.pos();
					}
					return ((tile.block() instanceof TubeItemBridge bridge && bridge.connectFilter.get(other.build)) || !(tile.block() instanceof ItemBridge) && other.block() == this) &&
							b2 &&
							(other.team() == tile.team() || other.block() != this) &&

							(!checkDouble || !connected);
				} else {
					if (!positionsValid(tile.x, tile.y, other.x, other.y)) break check;
					boolean b3 = other.team() == tile.team() || tile.block() != this;
					if (other.block() == this) {
						boolean b4 = !checkDouble || !(other.build instanceof ItemBridgeBuild b && b.link == tile.pos());
						return b2 && b3 && b4;
					} else {
						return (other.block() == tile.block() && tile.block() == this || !(tile.block() instanceof ItemBridge) && other.block() == this)
								&& b3 &&
								(!checkDouble || ((ItemBridgeBuild) other.build).link != tile.pos());
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean positionsValid(int x1, int y1, int x2, int y2) {
		return Mathf.within(x1, y1, x2, y2, range + 0.5f);
	}

	public boolean positionsValid(Point2 pos, Point2 other) {
		return positionsValid(pos.x, pos.y, other.x, other.y);
	}

	public void changePlacementPath(Seq<Point2> points, int rotation) {
		Placement.calculateNodes(points, this, rotation, this::positionsValid);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = TubeItemBridgeBuild::new;
	}

	public class TubeItemBridgeBuild extends ItemBridgeBuild {
		protected ItemBuffer buffer = new ItemBuffer(bufferCapacity);

		public void drawBase() {
			Draw.rect(block.region, x, y, block.rotate ? rotdeg() : 0f);
			drawTeamTop();
		}

		@Override
		public void checkIncoming() {
			Tile other;
			for (int i : incoming.toArray()) {
				other = world.tile(i);
				boolean valid = linkValid(tile, other, false) && (other.build instanceof ItemBridgeBuild ib && ib.link == tile.pos());
				if (!valid) {
					incoming.removeValue(i);
				}
			}
		}

		public int realConnections() {
			return incoming.size + (world.build(link) instanceof TubeItemBridgeBuild ? 1 : 0);
		}

		public boolean canLinked() {
			return (realConnections() < maxConnections);
		}

		public boolean canReLink() {
			return (realConnections() <= maxConnections && link != -1);
		}

		@Override
		public void updateTile() {
			Building other = world.build(link);
			if (other != null && !linkValid(tile, other.tile)) {
				link = -1;
			}
			super.updateTile();
		}

		@Override
		public void draw() {
			drawBase();

			Draw.z(Layer.power);
			Tile other = world.tile(link);
			Building build = world.build(link);
			if (build == this) build = null;
			if (build != null) other = build.tile;
			if (!linkValid(tile, other) || build == null || Mathf.zero(Renderer.bridgeOpacity)) return;
			Vec2 pos1 = new Vec2(x, y), pos2 = new Vec2(other.drawx(), other.drawy());

			if (pulse) Draw.color(Color.white, Color.black, Mathf.absin(Time.time, 6f, 0.07f));

			Draw.alpha(Renderer.bridgeOpacity);

			drawBridge(bridgeRegion, endRegion, pos1, pos2);

			Draw.color();
			int arrows = Mathf.round(pos1.dst(pos2) / arrowSpacing);
			float angle = pos1.angleTo(pos2);
			Tmp.v2.trns(angle - 45f, 1f, 1f);
			for (float a = 0; a < arrows - 2; ++a) {
				Draw.alpha(Mathf.absin(a - time / arrowTimeScl, arrowPeriod, 1f) * warmup * Renderer.bridgeOpacity);
				float arrowX, arrowY;
				arrowX = x - Tmp.v1.x + Tmp.v2.x * (tilesize / 2.5f + a * arrowSpacing + arrowOffset);
				arrowY = y - Tmp.v1.y + Tmp.v2.y * (tilesize / 2.5f + a * arrowSpacing + arrowOffset);
				Draw.rect(arrowRegion, arrowX, arrowY, angle);
			}
			Draw.reset();
		}

		@Override
		public void drawSelect() {
			if (linkValid(tile, world.tile(link))) {
				drawInput(world.tile(link));
			}

			for (int pos : incoming.items) {
				drawInput(world.tile(pos));
			}
			Draw.reset();
		}

		protected void drawInput(Tile other) {
			if (linkValid(tile, other, false)) {
				boolean linked = other.pos() == link;
				final float angle = tile.angleTo(other);
				Tmp.v2.trns(angle, 2f);
				float tx = tile.drawx();
				float ty = tile.drawy();
				float ox = other.drawx();
				float oy = other.drawy();
				float alpha = Math.abs((float) (linked ? 100 : 0) - Time.time * 2f % 100f) / 100f;
				float x = Mathf.lerp(ox, tx, alpha);
				float y = Mathf.lerp(oy, ty, alpha);
				Tile otherLink = linked ? other : tile;
				float rel = (linked ? tile : other).angleTo(otherLink);
				Draw.color(Pal.gray);
				Lines.stroke(2.5f);
				Lines.square(ox, oy, 2f, 45f);
				Lines.stroke(2.5f);
				Lines.line(tx + Tmp.v2.x, ty + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y);
				Draw.color(linked ? Pal.place : Pal.accent);
				Lines.stroke(1f);
				Lines.line(tx + Tmp.v2.x, ty + Tmp.v2.y, ox - Tmp.v2.x, oy - Tmp.v2.y);
				Lines.square(ox, oy, 2f, 45f);
				Draw.mixcol(Draw.getColor(), 1f);
				Draw.color();
				Draw.rect(arrowRegion, x, y, rel);
				Draw.mixcol();
			}
		}

		@Override
		public void drawConfigure() {
			Drawf.select(x, y, (float) (tile.block().size * 8) / 2f + 2f, Pal.accent);
			Drawf.dashCircle(x, y, (range) * 8f, Pal.accent);
			Draw.color();
			if (!canReLink() && !canLinked() && realConnections() >= maxConnections - 1) return;
			OrderedMap<Building, Boolean> orderedMap = new OrderedMap<>();
			for (int x = -range; x <= range; ++x) {
				for (int y = -range; y <= range; ++y) {
					Tile other = tile.nearby(x, y);
					if (linkValid(tile, other) && !(tile == other)) {
						if (!orderedMap.containsKey(other.build)) orderedMap.put(other.build, false);
					}
				}
			}
			Building linkBuilding = world.build(link);
			if (linkBuilding != null) {
				configure(linkBuilding.pos());
				orderedMap.remove(linkBuilding);
				orderedMap.put(linkBuilding, true);
			} else {
				configure(-1);
			}
			if (orderedMap.containsKey(this)) orderedMap.remove(this);
			for (var ord : orderedMap) {
				Drawf.select(ord.key.x, ord.key.y, (ord.key.block.size * 8f) / 2f + 2f + (ord.value ? 0f : Mathf.absin(Time.time, 4f, 1f)), ord.value ? Pal.place : Pal.breakInvalid);
			}
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			buffer.write(write);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			buffer.read(read);
		}
	}
}
