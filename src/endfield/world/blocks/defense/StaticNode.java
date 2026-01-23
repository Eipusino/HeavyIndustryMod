package endfield.world.blocks.defense;

import arc.Core;
import arc.func.Boolf;
import arc.func.Cons;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Intersector;
import arc.math.geom.Point2;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Structs;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import endfield.entities.Damage2;
import endfield.world.meta.EStatValues;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.core.Renderer;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.type.StatusEffect;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Edges;
import mindustry.world.Tile;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import org.jetbrains.annotations.Nullable;

import static endfield.Vars2.MOD_NAME;

public class StaticNode extends Block {
	protected static BuildPlan otherReq;
	protected static int returnInt = 0;

	public final int shockTimer = timers++;

	public int laserRange = 20;
	public float damage, reload = 5f;
	public float powerPerLink;
	public int maxNodes = 2;
	public boolean hitAir = true, hitGround = true;
	public StatusEffect status = StatusEffects.shocked;
	public float statusDuration = 10f * 60f;
	public Effect lightningEffect = Fx.none;
	public Effect shockEffect = Fx.none;
	public float minValue = 0.75f;

	public TextureRegion laser, laserEnd;

	@SuppressWarnings("unchecked")
	public StaticNode(String name) {
		super(name);

		configurable = hasPower = consumesPower = true;
		outputsPower = false;
		canOverdrive = false;
		solid = true;
		update = true;
		drawDisabled = false;
		noUpdateDisabled = false;
		swapDiagonalPlacement = true;

		config(Integer.class, (StaticNodeBuild tile, Integer value) -> {
			IntSeq links = tile.links;
			Building other = Vars.world.build(value);
			boolean contains = links.contains(value);

			// (t) = target, (b) = base
			if (contains) {
				//unlink
				links.removeValue(value);
				if (other instanceof StaticNodeBuild node) node.links.removeValue(tile.pos());
			} else if (linkValid(tile, other) && other instanceof StaticNodeBuild node && links.size < maxNodes) {
				//add other to self
				if (!links.contains(node.pos())) {
					links.add(node.pos());
				}

				//add self to other
				if (node.team == tile.team) {
					if (!node.links.contains(tile.pos())) {
						node.links.add(tile.pos());
					}
				}
			}
		});

		config(Point2[].class, (StaticNodeBuild tile, Point2[] value) -> {
			tile.links.clear();

			IntSeq old = new IntSeq(tile.links);

			//clear old
			for (int i = 0; i < old.size; i++) {
				int cur = old.get(i);
				configurations.get(Integer.class).get(tile, cur);
			}

			//set new
			for (Point2 p : value) {
				int newPos = Point2.pack(p.x + tile.tileX(), p.y + tile.tileY());
				configurations.get(Integer.class).get(tile, newPos);
			}
		});
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.add(Stat.powerRange, laserRange, StatUnit.blocks);
		stats.add(Stat.powerConnections, maxNodes, StatUnit.none);

		stats.remove(Stat.powerUse);
		stats.add(Stat.powerUse, Core.bundle.format("stat.static-power", powerPerLink * 60f));

		stats.add(Stat.targetsAir, hitAir);
		stats.add(Stat.targetsGround, hitGround);

		stats.add(Stat.ammo, EStatValues.staticDamage(damage, reload, status));
	}

	@Override
	public void init() {
		consumePowerDynamic(b -> b instanceof StaticNodeBuild s ? s.powerUse() : 0f);
		clipSize = Math.max(clipSize, (laserRange + 1f) * Vars.tilesize * 2f);

		super.init();

		if (shockEffect == Fx.none)
			shockEffect = new Effect(10f, e -> {
				Draw.color(e.color);
				Lines.stroke(e.fout() * 1.5f);

				Angles.randLenVectors(e.id, 7, e.finpow() * 27f, e.rotation, 45f, (x, y) -> {
					float ang = Angles.angle(x, y);
					Lines.lineAngle(e.x + x, e.y + y, ang, e.fout() * 4f + 1f);
				});
			});
	}

	@Override
	public void load() {
		super.load();

		laser = Core.atlas.find(name + "-laser", MOD_NAME + "-static-laser");
		laserEnd = Core.atlas.find(name + "-laser-end", MOD_NAME + "-static-laser-end");
	}

	@Override
	public void setBars() {
		super.setBars();
		addBar("connections", (StaticNodeBuild tile) -> new Bar(
				() -> Core.bundle.format("bar.powerlines", tile.links.size, maxNodes),
				() -> tile.team.color,
				() -> (float) (tile.links.size / maxNodes)
		));
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		drawPotentialLinks(x, y);

		Tile tile = Vars.world.tile(x, y);

		if (tile == null) return;

		Lines.stroke(1f);
		Draw.color(Pal.placing);
		Drawf.circles(x * Vars.tilesize + offset, y * Vars.tilesize + offset, laserRange * Vars.tilesize);

		getPotentialLinks(tile, Vars.player.team(), other -> {
			Draw.color(Tmp.c1.set(Vars.player.team().color), Renderer.laserOpacity);
			staticLine(tile.team(), x * Vars.tilesize + offset, y * Vars.tilesize + offset, other.x, other.y, size, other.block.size, true, false);

			Drawf.square(other.x, other.y, other.block.size * Vars.tilesize / 2f + 2f, Pal.place);
		});

		Draw.reset();
	}

	@Override
	public void changePlacementPath(Seq<Point2> points, int rotation) {
		Placement.calculateNodes(points, this, rotation, (point, other) -> overlaps(Vars.world.tile(point.x, point.y), Vars.world.tile(other.x, other.y)));
	}

	public boolean staticLine(Team team, float x1, float y1, float x2, float y2, int size1, int size2, boolean drawLaser, boolean drawOther, boolean attack) {
		float angle1 = Angles.angle(x1, y1, x2, y2),
				len1 = size1 * Vars.tilesize / 2f - 1.5f, len2 = size2 * Vars.tilesize / 2f - 1.5f;
		Tmp.v1.trns(angle1, len1);
		Tmp.v2.trns(angle1, len2);

		float space = Math.min(size1, size2), scale = 0.25f; //In case I want to change these

		if (drawLaser) {
			Tmp.v3.trns(angle1 - 90f, space);
			Drawf.laser(laser, laserEnd,
					x1 + Tmp.v1.x + Tmp.v3.x, y1 + Tmp.v1.y + Tmp.v3.y,
					x2 - Tmp.v2.x + Tmp.v3.x, y2 - Tmp.v2.y + Tmp.v3.y,
					scale
			);

			if (drawOther) {
				Tmp.v3.trns(angle1 + 90f, space);
				Drawf.laser(laser, laserEnd,
						x2 - Tmp.v2.x + Tmp.v3.x, y2 - Tmp.v2.y + Tmp.v3.y,
						x1 + Tmp.v1.x + Tmp.v3.x, y1 + Tmp.v1.y + Tmp.v3.y,
						scale
				);
			}
		}

		if (attack) {
			Tmp.v3.trns(angle1 - 90f, space);
			float bx = x1 + Tmp.v1.x,
					by = y1 + Tmp.v1.y;
			float dst = Mathf.dst(bx + Tmp.v3.x, by + Tmp.v3.y, x2 - Tmp.v2.x + Tmp.v3.x, y2 - Tmp.v2.y + Tmp.v3.y);

			boolean hit = Damage2.collideLine(damage, team, shockEffect, status, statusDuration,
					bx + Tmp.v3.x, by + Tmp.v3.y, angle1, dst,
					hitGround, hitAir
			);
			if (hit) {
				Tmp.v4.trns(angle1, dst).add(bx, by);
				lightningEffect.at(bx + Tmp.v3.x, by + Tmp.v3.y, team.color);
			}

			return hit;
		}
		return false;
	}

	public boolean staticLine(Team team, float x1, float y1, float x2, float y2, int size1, int size2, boolean drawLaser, boolean attack) {
		return staticLine(team, x1, y1, x2, y2, size1, size2, drawLaser, true, attack);
	}

	protected boolean overlaps(float srcx, float srcy, Tile other, Block otherBlock, float range) {
		return Intersector.overlaps(Tmp.cr1.set(srcx, srcy, range), Tmp.r1.setCentered(other.worldx() + otherBlock.offset, other.worldy() + otherBlock.offset,
				otherBlock.size * Vars.tilesize, otherBlock.size * Vars.tilesize));
	}

	protected boolean overlaps(float srcx, float srcy, Tile other, float range) {
		return Intersector.overlaps(Tmp.cr1.set(srcx, srcy, range), other.getHitbox(Tmp.r1));
	}

	protected boolean overlaps(Building src, Building other, float range) {
		return overlaps(src.x, src.y, other.tile, range);
	}

	protected boolean overlaps(Tile src, Tile other, float range) {
		return overlaps(src.drawx(), src.drawy(), other, range);
	}

	public boolean overlaps(@Nullable Tile src, @Nullable Tile other) {
		if (src == null || other == null) return true;
		return Intersector.overlaps(Tmp.cr1.set(src.worldx() + offset, src.worldy() + offset, laserRange * Vars.tilesize), Tmp.r1.setSize(size * Vars.tilesize).setCenter(other.worldx() + offset, other.worldy() + offset));
	}

	protected void getPotentialLinks(Tile tile, Team team, Cons<Building> others) {
		Boolf<Building> valid = other -> other != null && other.tile != tile && other.block == this &&
				overlaps(tile.x * Vars.tilesize + offset, tile.y * Vars.tilesize + offset, other.tile, laserRange * Vars.tilesize) && other.team == team &&
				!(other instanceof StaticNodeBuild obuild && obuild.links.size >= ((StaticNode) obuild.block).maxNodes) &&
				!Structs.contains(Edges.getEdges(size), p -> { //do not link to adjacent buildings
					Tile t = Vars.world.tile(tile.x + p.x, tile.y + p.y);
					return t != null && t.build == other;
				});

		tempBuilds.clear();

		Geometry.circle(tile.x, tile.y, laserRange + 2, (x, y) -> {
			Building other = Vars.world.build(x, y);
			if (valid.get(other) && !tempBuilds.contains(other)) {
				tempBuilds.add(other);
			}
		});

		tempBuilds.sort((a, b) -> {
			int type = -Boolean.compare(a.block instanceof StaticNode, b.block instanceof StaticNode);
			if (type != 0) return type;
			return Float.compare(a.dst2(tile), b.dst2(tile));
		});

		returnInt = 0;

		for (Building t : tempBuilds) {
			if (valid.get(t) && returnInt++ < maxNodes) {
				others.get(t);
			}
		}
	}

	@Override
	public void drawPlanConfigTop(BuildPlan req, Eachable<BuildPlan> list) {
		if (req.config instanceof Point2[] ps) {
			Draw.color(Tmp.c1.set(Vars.player.team().color), Renderer.laserOpacity);
			for (Point2 point : ps) {
				int px = req.x + point.x, py = req.y + point.y;
				otherReq = null;
				list.each(other -> {
					if (other.block != null
							&& (px >= other.x - ((other.block.size - 1) / 2) && py >= other.y - ((other.block.size - 1) / 2) && px <= other.x + other.block.size / 2 && py <= other.y + other.block.size / 2)
							&& other != req && other.block.hasPower) {
						otherReq = other;
					}
				});

				if (otherReq == null || otherReq.block == null) continue;

				staticLine(Vars.player == null ? Team.sharded : Vars.player.team(), req.drawx(), req.drawy(), otherReq.drawx(), otherReq.drawy(), size, otherReq.block.size, true, false);
			}
			Draw.color();
		}
	}

	public boolean linkValid(Building tile, Building link) {
		return linkValid(tile, link, true);
	}

	public boolean linkValid(Building tile, Building link, boolean checkMaxNodes) {
		if (tile == link || link == null || !(link.block instanceof StaticNode node) || tile.team != link.team || tile.block != link.block)
			return false;

		if (overlaps(tile, link, laserRange * Vars.tilesize) || overlaps(link, tile, node.laserRange * Vars.tilesize)) {
			if (checkMaxNodes) {
				StaticNodeBuild n = (StaticNodeBuild) link;
				return n.links.size < node.maxNodes || n.links.contains(tile.pos());
			}
			return true;
		}
		return false;
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = StaticNodeBuild::new;
	}

	public class StaticNodeBuild extends Building {
		public IntSeq links = new IntSeq();
		public boolean active;

		@Override
		public void placed() {
			if (Vars.net.client()) return;

			getPotentialLinks(tile, team, other -> {
				if (!links.contains(other.pos())) {
					configureAny(other.pos());
				}
			});

			super.placed();
		}

		@Override
		public void dropped() {
			links.clear();
		}

		@Override
		public void onRemoved() {
			//Clear links
			while (links.size > 0) {
				configure(links.get(0));
			}

			super.onRemoved();
		}

		@Override
		public void updateTile() {
			super.updateTile();

			if (canConsume() && Groups.unit.contains(u -> u.team != team) && timer(shockTimer, reload / efficiency)) {
				for (int i : links.items) {
					Building link = Vars.world.build(i);

					if (linked(link)) {
						active = staticLine(team, x, y, link.x, link.y, size, link.block.size, false, true);
					}
				}
			}
		}

		public float powerUse() {
			if (!active || links.size <= 0) return 0;
			return powerPerLink * links.size;
		}

		@Override
		public boolean onConfigureBuildTapped(Building other) {
			if (linkValid(this, other)) {
				configure(other.pos());
				return false;
			}

			if (this == other) {
				if (((StaticNodeBuild) other).links.size == 0) {
					int[] total = {0};
					getPotentialLinks(tile, team, link -> {
						if (total[0]++ < maxNodes) {
							configure(link.pos());
						}
					});
				} else {
					while (links.size > 0) {
						configure(links.get(0));
					}
				}
				deselect();
				return false;
			}

			return true;
		}

		@Override
		public void drawSelect() {
			super.drawSelect();

			Lines.stroke(1f);

			Draw.color(team.color);
			Drawf.circles(x, y, laserRange * Vars.tilesize);
			Draw.reset();
		}

		@Override
		public void drawConfigure() {
			Drawf.circles(x, y, tile.block().size * Vars.tilesize / 2f + 1f + Mathf.absin(Time.time, 4f, 1f));
			Drawf.circles(x, y, laserRange * Vars.tilesize);

			Draw.color(team.color);

			for (int i : links.items) {
				Building link = Vars.world.build(i);

				if (link != this && linkValid(this, link, false) && linked(link)) {
					Drawf.square(link.x, link.y, link.block.size * Vars.tilesize / 2f + 1f, Pal.place);
				}
			}

			Draw.reset();
		}

		@Override
		public void draw() {
			super.draw();

			if (Mathf.zero(Renderer.laserOpacity)) return;

			Draw.z(Layer.power);

			for (int i : links.items) {
				Building link = Vars.world.build(i);

				if (!linkValid(this, link) || !linked(link)) continue;

				Draw.color(Tmp.c1.set(team.color).mul(minValue + efficiency * (1f - minValue)), Renderer.laserOpacity);

				staticLine(team, x, y, link.x, link.y, size, link.block.size, true, false, false);
			}

			Draw.reset();
		}

		protected boolean linked(Building other) {
			return other != null && links.contains(other.pos());
		}

		@Override
		public Object config() {
			Point2[] out = new Point2[links.size];
			for (int i = 0; i < out.length; i++) {
				out[i] = Point2.unpack(links.get(i)).sub(tile.x, tile.y);
			}
			return out;
		}

		@Override
		public void write(Writes write) {
			write.s(links.size);
			for (int i = 0; i < links.size; i++) {
				write.i(links.get(i));
			}
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			if (revision >= 1) {
				links.clear();
				short amount = read.s();
				for (int i = 0; i < amount; i++) {
					links.add(read.i());
				}
			}
		}

		@Override
		public byte version() {
			return 1;
		}
	}
}
