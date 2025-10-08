package heavyindustry.entities;

import arc.func.Boolf;
import arc.func.Cons;
import arc.func.Intc2;
import arc.graphics.Color;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Geometry;
import arc.math.geom.Intersector;
import arc.math.geom.Point2;
import arc.math.geom.Position;
import arc.math.geom.QuadTree;
import arc.math.geom.QuadTree.QuadTreeObject;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.IntIntMap;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pool.Poolable;
import arc.util.pooling.Pools;
import heavyindustry.content.HFx;
import heavyindustry.gen.Spawner;
import heavyindustry.util.CollectionList;
import heavyindustry.util.Reflects;
import mindustry.Vars;
import mindustry.audio.SoundLoop;
import mindustry.core.World;
import mindustry.entities.Effect;
import mindustry.entities.Sized;
import mindustry.entities.Units;
import mindustry.entities.units.WeaponMount;
import mindustry.game.Team;
import mindustry.game.Teams;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Drawc;
import mindustry.gen.EffectState;
import mindustry.gen.Entityc;
import mindustry.gen.Groups;
import mindustry.gen.Syncc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.gen.WaterMovec;
import mindustry.type.StatusEffect;
import mindustry.type.UnitType;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.turrets.Turret.TurretBuild;

import java.lang.reflect.Field;
import java.util.Arrays;

import static heavyindustry.entities.HDamage.tmpBuilding;

public final class HEntity {
	public static final Vec2 v1 = new Vec2(), v2 = new Vec2(), v3 = new Vec2(), v4 = new Vec2(), v11 = new Vec2(), v12 = new Vec2(), v13 = new Vec2();
	public static final Rect rect = new Rect(), hitRect = new Rect(), rect1 = new Rect(), rect2 = new Rect();
	public static final Rand rand = new Rand();

	static final CollectionList<Building> buildings = new CollectionList<>(Building.class);
	static final CollectionList<Unit> units = new CollectionList<>(Unit.class);

	static final IntSet collided = new IntSet(), collided2 = new IntSet();
	static final IntSet collidedBlocks = new IntSet();

	static Tile tileParma;

	static final CollectionList<Entityc> toRemove = new CollectionList<>(Entityc.class);

	static final IntSet exclude = new IntSet();
	static final CollectionList<Unit> excludeSeq = new CollectionList<>(Unit.class), queueExcludeRemoval = new CollectionList<>(Unit.class), excludeReAdd = new CollectionList<>(Unit.class);
	static final IntIntMap excludeTime = new IntIntMap();

	public static Unit eipusino;

	private HEntity() {}

	public static Position collideBuild(Team team, float x1, float y1, float x2, float y2, Boolf<Building> boolf) {
		tmpBuilding = null;

		boolean found = World.raycast(World.toTile(x1), World.toTile(y1), World.toTile(x2), World.toTile(y2),
				(x, y) -> (tmpBuilding = Vars.world.build(x, y)) != null && tmpBuilding.team != team && boolf.get(tmpBuilding));

		return found ? tmpBuilding : v1.set(x2, y2);
	}

	public static Position collideBuildOnLength(Team team, float x1, float y1, float length, float ang, Boolf<Building> boolf) {
		v2.trns(ang, length).add(x1, y1);
		return collideBuild(team, x1, y1, v2.x, v2.y, boolf);
	}

	public static float findLaserLength(Bullet b, float angle, float length) {
		Tmp.v1.trnsExact(angle, length);

		tileParma = null;

		boolean found = World.raycast(b.tileX(), b.tileY(), World.toTile(b.x + Tmp.v1.x), World.toTile(b.y + Tmp.v1.y),
				(x, y) -> (tileParma = Vars.world.tile(x, y)) != null && tileParma.team() != b.team && tileParma.block().absorbLasers);

		return found && tileParma != null ? Math.max(6f, b.dst(tileParma.worldx(), tileParma.worldy())) : length;
	}

	public static void collideLine(Bullet hitter, Team team, Effect effect, float x, float y, float angle, float length, boolean large, boolean laser) {
		if (laser) length = findLaserLength(hitter, angle, length);

		collidedBlocks.clear();
		v11.trnsExact(angle, length);

		Intc2 collider = (cx, cy) -> {
			Building tile = Vars.world.build(cx, cy);
			boolean collide = tile != null && collidedBlocks.add(tile.pos());

			if (hitter.damage > 0) {
				float health = !collide ? 0 : tile.health;

				if (collide && tile.team != team && tile.collide(hitter)) {
					tile.collision(hitter);
					hitter.type.hit(hitter, tile.x, tile.y);
				}

				//try to heal the tile
				if (collide && hitter.type.testCollision(hitter, tile)) {
					hitter.type.hitTile(hitter, tile, cx * Vars.tilesize, cy * Vars.tilesize, health, false);
				}
			}
		};

		if (hitter.type.collidesGround) {
			v12.set(x, y);
			v13.set(v12).add(v11);
			World.raycastEachWorld(x, y, v13.x, v13.y, (cx, cy) -> {
				collider.get(cx, cy);

				for (Point2 p : Geometry.d4) {
					Tile other = Vars.world.tile(p.x + cx, p.y + cy);
					if (other != null && (large || Intersector.intersectSegmentRectangle(v12, v13, other.getBounds(Tmp.r1)))) {
						collider.get(cx + p.x, cy + p.y);
					}
				}
				return false;
			});
		}

		rect.setPosition(x, y).setSize(v11.x, v11.y);
		float x2 = v11.x + x, y2 = v11.y + y;

		if (rect.width < 0) {
			rect.x += rect.width;
			rect.width *= -1;
		}

		if (rect.height < 0) {
			rect.y += rect.height;
			rect.height *= -1;
		}

		float expand = 3f;

		rect.y -= expand;
		rect.x -= expand;
		rect.width += expand * 2;
		rect.height += expand * 2;

		Cons<Unit> cons = unit -> {
			unit.hitbox(hitRect);

			Vec2 vec = Geometry.raycastRect(x, y, x2, y2, hitRect.grow(expand * 2));

			if (vec != null && hitter.damage > 0) {
				effect.at(vec.x, vec.y);
				unit.collision(hitter, vec.x, vec.y);
				hitter.collision(unit, vec.x, vec.y);
			}
		};

		units.clear();

		Units.nearbyEnemies(team, rect, u -> {
			if (u.checkTarget(hitter.type.collidesAir, hitter.type.collidesGround)) {
				units.add(u);
			}
		});

		units.sort(u -> u.dst2(hitter));
		units.each(cons);
	}

	public static void randFadeLightningEffect(float x, float y, float range, float lightningPieceLength, Color color, boolean in) {
		randFadeLightningEffectScl(x, y, range, 0.55f, 1.1f, lightningPieceLength, color, in);
	}

	public static void randFadeLightningEffectScl(float x, float y, float range, float sclMin, float sclMax, float lightningPieceLength, Color color, boolean in) {
		v1.rnd(range).scl(Mathf.random(sclMin, sclMax)).add(x, y);
		(in ? HFx.chainLightningFadeReversed : HFx.chainLightningFade).at(x, y, lightningPieceLength, color, v1.cpy());
	}

	public static float inRayCastCircle(float x, float y, float[] in, Sized target) {
		float amount = 0f;
		float hsize = target.hitSize() / 2f;
		int collision = 0;
		int isize = in.length;

		float dst = Mathf.dst(x, y, target.getX(), target.getY());
		float ang = Angles.angle(x, y, target.getX(), target.getY());
		float angSize = Mathf.angle(dst, hsize);

		int idx1 = (int) (((ang - angSize) / 360f) * isize + 0.5f);
		int idx2 = (int) (((ang + angSize) / 360f) * isize + 0.5f);

		for (int i = idx1; i <= idx2; i++) {
			int mi = Mathf.mod(i, isize);
			float range = in[mi];

			if ((dst - hsize) < range) {
				amount += Mathf.clamp((range - (dst - hsize)) / hsize);
			}
			collision++;
		}

		return collision > 0 ? (amount / collision) : 0f;
	}

	public static void rayCastCircle(float x, float y, float radius, Boolf<Tile> stop, Cons<Tile> ambient, Cons<Tile> edge, Cons<Building> hit, float[] out) {
		Arrays.fill(out, radius);

		int res = out.length;
		collided.clear();
		collided2.clear();
		buildings.clear();
		for (int i = 0; i < res; i++) {
			final int fi = i;
			float ang = (i / (float) res) * 360f;
			v2.trns(ang, radius).add(x, y);
			float vx = v2.x, vy = v2.y;
			int tx1 = (int) (x / Vars.tilesize), ty1 = (int) (y / Vars.tilesize);
			int tx2 = (int) (vx / Vars.tilesize), ty2 = (int) (vy / Vars.tilesize);

			World.raycastEach(tx1, ty1, tx2, ty2, (rx, ry) -> {
				Tile tile = Vars.world.tile(rx, ry);
				boolean collide = false;

				if (tile != null && !tile.block().isAir() && stop.get(tile)) {
					tile.getBounds(rect2);
					rect2.grow(0.1f);
					Vec2 inter = intersectRect(x, y, vx, vy, rect2);
					if (inter != null) {
						if (tile.build != null && collided.add(tile.build.id)) {
							buildings.add(tile.build);
						}

						float dst = Mathf.dst(x, y, inter.x, inter.y);
						out[fi] = dst;
						collide = true;
					} else {
						for (Point2 d : Geometry.d8) {
							Tile nt = Vars.world.tile(tile.x + d.x, tile.y + d.y);

							if (nt != null && !nt.block().isAir() && stop.get(nt)) {
								nt.getBounds(rect2);
								rect2.grow(0.1f);
								Vec2 inter2 = intersectRect(x, y, vx, vy, rect2);
								if (inter2 != null) {
									if (tile.build != null && collided.add(tile.build.id)) {
										buildings.add(tile.build);
									}

									float dst = Mathf.dst(x, y, inter2.x, inter2.y);
									out[fi] = dst;
									collide = true;
								}
							}
						}
					}
				}

				if (tile != null && collided2.add(tile.pos())) {
					ambient.get(tile);
					if (collide) {
						edge.get(tile);
					}
				}

				return collide;
			});
		}
		for (Building b : buildings) {
			hit.get(b);
		}
		buildings.clear();
	}

	public static void scanEnemies(Team team, float x, float y, float radius, boolean targetAir, boolean targetGround, Cons<Teamc> cons) {
		rect1.setCentered(x, y, radius * 2f);
		Groups.unit.intersect(rect1.x, rect1.y, rect1.width, rect1.height, u -> {
			if (u.team != team && Mathf.within(x, y, u.x, u.y, radius + u.hitSize / 2f) && u.checkTarget(targetAir, targetGround)) {
				cons.get(u);
			}
		});

		if (targetGround) {
			buildings.clear();
			for (Teams.TeamData data : Vars.state.teams.active) {
				if (data.team != team && data.buildingTree != null) {
					data.buildingTree.intersect(rect1, b -> {
						if (Mathf.within(x, y, b.x, b.y, radius + b.hitSize() / 2f)) {
							//cons.get(b);
							buildings.add(b);
						}
					});
				}
			}
			for (Building b : buildings) {
				cons.get(b);
			}

			buildings.clear();
		}
	}

	public static boolean circleContainsRect(float x, float y, float radius, Rect rect) {
		int count = 0;
		for (int i = 0; i < 4; i++) {
			int mod = i % 2;
			int i2 = i / 2;
			float rx1 = rect.x + rect.width * mod;
			float ry1 = rect.y + rect.height * i2;

			if (Mathf.within(x, y, rx1, ry1, radius)) {
				count++;
			}
		}

		return count == 4;
	}

	/** code taken from BadWrong_ on the gamemaker subreddit */
	@Nullable
	public static Vec2 intersectCircle(float x1, float y1, float x2, float y2, float cx, float cy, float cr) {
		if (!Intersector.nearestSegmentPoint(x1, y1, x2, y2, cx, cy, v4).within(cx, cy, cr)) return null;

		cx = x1 - cx;
		cy = y1 - cy;

		float vx = x2 - x1,
				vy = y2 - y1,
				a = vx * vx + vy * vy,
				b = 2 * (vx * cx + vy * cy),
				c = cx * cx + cy * cy - cr * cr,
				det = b * b - 4 * a * c;

		if (a <= Mathf.FLOAT_ROUNDING_ERROR || det < 0) {
			return null;
		} else if (det == 0f) {
			float t = -b / (2 * a);
			float ix = x1 + t * vx;
			float iy = y1 + t * vy;

			return v4.set(ix, iy);
		} else {
			det = Mathf.sqrt(det);
			float t1 = (-b - det) / (2 * a);

			return v4.set(x1 + t1 * vx, y1 + t1 * vy);
		}
	}

	public static Vec2 intersectRect(float x1, float y1, float x2, float y2, Rect rect) {
		boolean intersected = false;

		float nearX = 0f, nearY = 0f;
		float lastDst = 0f;

		for (int i = 0; i < 4; i++) {
			int mod = i % 2;
			float rx1 = i < 2 ? (rect.x + rect.width * mod) : rect.x;
			float rx2 = i < 2 ? (rect.x + rect.width * mod) : rect.x + rect.width;
			float ry1 = i < 2 ? rect.y : (rect.y + rect.height * mod);
			float ry2 = i < 2 ? rect.y + rect.height : (rect.y + rect.height * mod);

			if (Intersector.intersectSegments(x1, y1, x2, y2, rx1, ry1, rx2, ry2, v2)) {
				float dst = Mathf.dst2(x1, y1, v2.x, v2.y);
				if (!intersected || dst < lastDst) {
					nearX = v2.x;
					nearY = v2.y;
					lastDst = dst;
				}

				intersected = true;
			}
		}

		if (rect.contains(x1, y1)) {
			nearX = x1;
			nearY = y1;
			intersected = true;
		}

		return intersected ? v2.set(nearX, nearY) : null;
	}

	/** [0]For flying, [1] for navy, [2] for ground */
	public static Seq<Boolf<Tile>> formats() {
		Seq<Boolf<Tile>> seq = new Seq<>(true, 3, Boolf.class);

		seq.add(
				t -> Vars.world.getQuadBounds(Tmp.r1).contains(t.getBounds(Tmp.r2)),
				t -> t.floor().isLiquid && !t.cblock().solid && !t.floor().solid && !t.overlay().solid && !t.block().solidifes,
				t -> !t.floor().isDeep() && !t.cblock().solid && !t.floor().solid && !t.overlay().solid && !t.block().solidifes
		);

		return seq;
	}

	/**
	 * @param x	 the abscissa of search center.
	 * @param y	 the ordinate of search center.
	 * @param range the search range.
	 * @param bool  {@link Boolf} {@code lambda} to determine whether the condition is true.
	 * @return {@link Seq}{@code <Tile>} - which contains eligible {@link Tile} {@code tile}.
	 * @implNote Get all the {@link Tile} {@code tile} within a certain range at certain position.
	 */
	public static Seq<Tile> getAcceptableTiles(int x, int y, int range, Boolf<Tile> bool) {
		Seq<Tile> tiles = new Seq<>(true, (int) (Mathf.pow(range, 2) * Mathf.pi), Tile.class);
		Geometry.circle(x, y, range, (x1, y1) -> {
			if ((tileParma = Vars.world.tile(x1, y1)) != null && bool.get(tileParma)) {
				tiles.add(Vars.world.tile(x1, y1));
			}
		});
		return tiles;
	}

	public static Boolf<Tile> ableToSpawn(UnitType type) {
		Boolf<Tile> boolf;

		Seq<Boolf<Tile>> boolves = formats();

		if (type.flying) {
			boolf = boolves.get(0);
		} else if (WaterMovec.class.isAssignableFrom(type.constructor.get().getClass())) {
			boolf = boolves.get(1);
		} else {
			boolf = boolves.get(2);
		}

		return boolf;
	}

	public static Seq<Tile> ableToSpawn(UnitType type, float x, float y, float range) {
		Seq<Tile> tiles = new Seq<>(Tile.class);

		Boolf<Tile> boolf = ableToSpawn(type);

		return tiles.addAll(getAcceptableTiles(World.toTile(x), World.toTile(y), World.toTile(range), boolf));
	}

	public static boolean ableToSpawnPoints(Seq<Vec2> spawnPoints, UnitType type, float x, float y, float range, int num, long seed) {
		Seq<Tile> tiles = ableToSpawn(type, x, y, range);

		rand.setSeed(seed);
		for (int i = 0; i < num; i++) {
			Tile[] positions = tiles.shrink();
			if (positions.length < num) return false;
			spawnPoints.add(new Vec2().set(positions[rand.nextInt(positions.length)]));
		}

		return true;
	}

	public static boolean spawnUnit(Team team, float x, float y, float angle, float spawnRange, float spawnReloadTime, float spawnDelay, UnitType type, int spawnNum, Cons<Spawner> modifier) {
		if (type == null) return false;
		Seq<Vec2> vectorSeq = new Seq<>(Vec2.class);

		if (!ableToSpawnPoints(vectorSeq, type, x, y, spawnRange, spawnNum, rand.nextLong())) return false;

		int i = 0;
		for (Vec2 s : vectorSeq) {
			Spawner spawner = Pools.obtain(Spawner.class, Spawner::new);
			spawner.init(type, team, s, angle, spawnReloadTime + i * spawnDelay);
			modifier.get(spawner);
			if (!Vars.net.client()) spawner.add();
			i++;
		}
		return true;
	}

	public static boolean spawnUnit(Team team, float x, float y, float angle, float spawnRange, float spawnReloadTime, float spawnDelay, UnitType type, int spawnNum) {
		return spawnUnit(team, x, y, angle, spawnRange, spawnReloadTime, spawnDelay, type, spawnNum, t -> {
		});
	}

	public static boolean spawnUnit(Team team, float x, float y, float angle, float spawnRange, float spawnReloadTime, float spawnDelay, UnitType type, int spawnNum, StatusEffect statusEffect, float statusDuration) {
		return spawnUnit(team, x, y, angle, spawnRange, spawnReloadTime, spawnDelay, type, spawnNum, s -> {
			s.setStatus(statusEffect, statusDuration);
		});
	}

	public static boolean spawnUnit(Team team, float x, float y, float angle, float spawnRange, float spawnReloadTime, float spawnDelay, UnitType type, int spawnNum, StatusEffect statusEffect, float statusDuration, double frag) {
		return spawnUnit(team, x, y, angle, spawnRange, spawnReloadTime, spawnDelay, type, spawnNum, s -> {
			s.setStatus(statusEffect, statusDuration);
			s.flagToApply = frag;
		});
	}

	public static void spawnSingleUnit(Team team, float x, float y, float angle, float delay, UnitType type) {
		Spawner spawner = Pools.obtain(Spawner.class, Spawner::new);
		spawner.init(type, team, v1.set(x, y), angle, delay);
		if (!Vars.net.client()) spawner.add();
	}

	public static void spawnSingleUnit(Team team, float x, float y, float angle, float delay, UnitType type, Cons<Spawner> modifier) {
		Spawner spawner = Pools.obtain(Spawner.class, Spawner::new);
		spawner.init(type, team, v1.set(x, y), angle, delay);
		modifier.get(spawner);
		if (!Vars.net.client()) spawner.add();
	}

	//not support server
	public static void spawnSingleUnit(UnitType type, Team team, int spawnNum, float x, float y) {
		for (int spawned = 0; spawned < spawnNum; spawned++) {
			Time.run(spawned * Time.delta, () -> {
				Unit unit = type.create(team);
				if (unit != null) {
					unit.set(x, y);
					unit.add();
				}
			});
		}
	}

	public static <T extends QuadTreeObject> void scanQuadTree(QuadTree<T> tree, QuadTreeHandler within, Cons<T> cons) {
		if (within.get(tree.bounds, true)) {
			for (T t : tree.objects) {
				t.hitbox(rect2);
				if (within.get(rect2, false)) {
					cons.get(t);
				}
			}

			if (!tree.leaf) {
				scanQuadTree(tree.botLeft, within, cons);
				scanQuadTree(tree.botRight, within, cons);
				scanQuadTree(tree.topLeft, within, cons);
				scanQuadTree(tree.topRight, within, cons);
			}
		}
	}

	public static <T extends QuadTreeObject> void intersectLine(QuadTree<T> tree, float width, float x1, float y1, float x2, float y2, LineHitHandler<T> cons) {
		rect1.set(tree.bounds).grow(width);
		if (Intersector.intersectSegmentRectangle(x1, y1, x2, y2, rect1)) {
			for (T t : tree.objects) {
				t.hitbox(rect2);
				rect2.grow(width);
				float cx = rect2.x + rect2.width / 2f, cy = rect2.y + rect2.height / 2f;
				float cr = Math.max(rect2.width, rect2.height);

				Vec2 v = intersectCircle(x1, y1, x2, y2, cx, cy, cr / 2f);
				if (v != null) {
					float scl = (cr - width) / cr;
					v.sub(cx, cy).scl(scl).add(cx, cy);

					cons.get(t, v.x, v.y);
				}
			}

			if (!tree.leaf) {
				intersectLine(tree.botLeft, width, x1, y1, x2, y2, cons);
				intersectLine(tree.botRight, width, x1, y1, x2, y2, cons);
				intersectLine(tree.topLeft, width, x1, y1, x2, y2, cons);
				intersectLine(tree.topRight, width, x1, y1, x2, y2, cons);
			}
		}
	}

	public static <T extends QuadTreeObject> void scanCone(QuadTree<T> tree, float x, float y, float rotation, float length, float spread, Cons<T> cons) {
		scanCone(tree, x, y, rotation, length, spread, cons, true, false);
	}

	public static <T extends QuadTreeObject> void scanCone(QuadTree<T> tree, float x, float y, float rotation, float length, float spread, boolean accurate, Cons<T> cons) {
		scanCone(tree, x, y, rotation, length, spread, cons, true, accurate);
	}

	public static <T extends QuadTreeObject> void scanCone(QuadTree<T> tree, float x, float y, float rotation, float length, float spread, Cons<T> cons, boolean source, boolean accurate) {
		if (source) {
			v2.trns(rotation - spread, length).add(x, y);
			v3.trns(rotation + spread, length).add(x, y);
		}
		Rect r = tree.bounds;
		boolean valid = Intersector.intersectSegmentRectangle(x, y, v2.x, v2.y, r) || Intersector.intersectSegmentRectangle(x, y, v3.x, v3.y, r) || r.contains(x, y);
		float lenSqr = length * length;
		if (!valid) {
			for (int i = 0; i < 4; i++) {
				float mx = (r.x + r.width * (i % 2)) - x;
				float my = (r.y + (i >= 2 ? r.height : 0f)) - y;

				float dst2 = Mathf.dst2(mx, my);
				if (dst2 < lenSqr && Angles.within(rotation, Angles.angle(mx, my), spread)) {
					valid = true;
					break;
				}
			}
		}
		if (valid) {
			for (T t : tree.objects) {
				Rect rr = rect2;
				t.hitbox(rr);
				float mx = (rr.x + rr.width / 2) - x;
				float my = (rr.y + rr.height / 2) - y;
				float size = (Math.max(rr.width, rr.height) / 2f);
				float bounds = size + length;
				float at = accurate ? Mathf.angle(Mathf.sqrt(mx * mx + my * my), size) : 0f;
				if (mx * mx + my * my < (bounds * bounds) && Angles.within(rotation, Angles.angle(mx, my), spread + at)) {
					cons.get(t);
				}
			}
			if (!tree.leaf) {
				scanCone(tree.botLeft, x, y, rotation, length, spread, cons, false, accurate);
				scanCone(tree.botRight, x, y, rotation, length, spread, cons, false, accurate);
				scanCone(tree.topLeft, x, y, rotation, length, spread, cons, false, accurate);
				scanCone(tree.topRight, x, y, rotation, length, spread, cons, false, accurate);
			}
		}
	}

	public static <T extends QuadTreeObject> Seq<T> getObjects(QuadTree<T> tree, Class<T> arrayType) {
		Seq<T> seq = new Seq<>(arrayType);

		tree.getObjects(seq);

		return seq;
	}

	/** @deprecated Please use {@link #getObjects(QuadTree, Class)} */
	@Deprecated
	public static <T extends QuadTreeObject> Seq<T> getObjects(QuadTree<T> tree) {
		Seq<T> seq = new Seq<>();

		tree.getObjects(seq);

		return seq;
	}

	public static void update() {
		if (queueExcludeRemoval.any()) {
			for (Unit u : queueExcludeRemoval) {
				if (u == null) continue;

				exclude.remove(u.id);
				excludeSeq.remove(u);
				excludeTime.remove(u.id);
			}
			queueExcludeRemoval.clear();
		}
	}

	public static void reset() {
		tileParma = null;
		eipusino =  null;

		toRemove.clear();

		units.clear();
		exclude.clear();
		excludeSeq.clear();
		queueExcludeRemoval.clear();
		excludeReAdd.clear();
		excludeTime.clear();
	}

	public static void progressiveStar(float x, float y, float rad, float rot, int count, int stellation, float fin) {
		int c = Mathf.ceil(count * fin);
		for (int i = 0; i < c; i++) {
			//360f * (1f / count) * i * stellation + rotation;
			float r1 = 360f * (1f / count) * stellation * i + rot;
			float r2 = 360f * (1f / count) * stellation * (i + 1f) + rot;

			float rx1 = Mathf.cosDeg(r1) * rad + x, ry1 = Mathf.sinDeg(r1) * rad + y;
			float rx2 = Mathf.cosDeg(r2) * rad + x, ry2 = Mathf.sinDeg(r2) * rad + y;

			if (fin >= 1f || (i < (c - 1))) {
				Lines.line(rx1, ry1, rx2, ry2);
			} else {
				float f = fin < 1f ? (fin * count) % 1f : 1f;
				float lx = Mathf.lerp(rx1, rx2, f), ly = Mathf.lerp(ry1, ry2, f);
				Lines.line(rx1, ry1, lx, ly);
			}
		}
	}

	public static void progressiveCircle(float x, float y, float rad, float rot, float fin) {
		if (fin < 0.9999f) {
			if (fin < 0.001f) return;
			int r = Lines.circleVertices(rad * fin);
			//if (r <= 0) return;
			Lines.beginLine();
			for (int i = 0; i < r; i++) {
				float ang = (360f / (r - 1)) * fin * i + rot;
				float sx = Mathf.cosDeg(ang) * rad, sy = Mathf.sinDeg(ang) * rad;
				Lines.linePoint(sx + x, sy + y);
			}
			Lines.endLine();
		} else {
			Lines.circle(x, y, rad);
		}
	}

	public static void exclude(Unit unit) {
		if (exclude.add(unit.id)) {
			excludeSeq.add(unit);
			excludeTime.put(unit.id, (int) (Time.millis() / 1000));
		}
	}

	public static boolean removeExclude(Unit unit) {
		if (!queueExcludeRemoval.contains(unit) && ((int) (Time.millis() / 1000) - 30) > excludeTime.get(unit.id, 0xffffffff)) {
			queueExcludeRemoval.add(unit);
			return true;
		}
		return false;
	}

	public static boolean containsExclude(int id) {
		return exclude.contains(id);
	}

	public static void clearExclude() {
		exclude.clear();
		excludeSeq.clear();
		queueExcludeRemoval.clear();
		excludeReAdd.clear();
		excludeTime.clear();
	}

	public static void annihilate(Entityc entity, boolean setNaN) {
		Groups.all.remove(entity);

		if (entity instanceof Drawc d) Groups.draw.remove(d);
		if (entity instanceof Syncc s) Groups.sync.remove(s);

		if (entity instanceof Unit unit) {
			setAdded(unit, false);

			if (setNaN) {
				unit.x = unit.y = unit.rotation = Float.NaN;
				unit.vel.x = unit.vel.y = Float.NaN;
				for (WeaponMount mount : unit.mounts) {
					mount.reload = Float.NaN;
				}
			}

			if (Vars.net.client()) {
				Vars.netClient.addRemovedEntity(unit.id());
			}

			unit.team.data().updateCount(unit.type, -1);
			unit.controller().removed(unit);

			Groups.unit.remove(unit);

			for (WeaponMount mount : unit.mounts) {
				if (mount.bullet != null) {
					mount.bullet.time = mount.bullet.lifetime;
					mount.bullet = null;
				}
				if (mount.sound != null) {
					mount.sound.stop();
				}
			}

			unit.setIndex__all(-1);
			unit.setIndex__unit(-1);
			unit.setIndex__draw(-1);
			unit.setIndex__sync(-1);
		} else if (entity instanceof Building build) {
			Groups.build.remove(build);
			build.tile.remove();
			if (setNaN) {
				build.x = build.y = Float.NaN;
			}
			if (build instanceof TurretBuild tb) {
				tb.ammo.clear();
				if (setNaN) {
					tb.rotation = Float.NaN;
					tb.reloadCounter = Float.NaN;
				}
			}

			setAdded(build, false);

			//if (build.sound != null) build.sound.stop();
			findSound(build, sl -> {
				if (sl != null) {
					sl.stop();
				}
			});

			build.setIndex__all(-1);
			build.setIndex__build(-1);
		} else if (entity instanceof Bullet bullet) {
			Groups.bullet.remove(bullet);

			setAdded(bullet, false);

			bullet.setIndex__all(-1);
			bullet.setIndex__draw(-1);
			bullet.setIndex__bullet(-1);
		}
		if (entity instanceof Poolable pool) Groups.queueFree(pool);
	}

	public static void handleAdditions(int start, Entityc exclude, Entityc exclude2, CollectionList<Building> proxy) {
		toRemove.clear();

		int size = Groups.all.size();

		for (int i = start; i < size; i++) {
			Entityc e = Groups.all.index(i);
			if (e != exclude && e != exclude2 && (proxy == null || !proxy.contains(b -> e == b)) && !(e instanceof EffectState))
				toRemove.add(e);
		}

		toRemove.each(e -> annihilate(e, false));

		//Log.info("addition handled:" + toRemove.toString());
		toRemove.clear();
	}

	public static void setAdded(Entityc entity, boolean value) {
		Field field = Reflects.findClassField(entity.getClass(), "added");
		if (field != null) {
			try {
				field.setAccessible(true);
				field.setBoolean(entity, value);
			} catch (Exception e) {
				Log.err(e);
			}
		}
	}

	@Nullable
	public static void findSound(Building build, Cons<SoundLoop> cons) {
		Field field = Reflects.findClassField(build.getClass(), "sound");
		if (field != null) {
			try {
				field.setAccessible(true);
				cons.get((SoundLoop) field.get(build));
			} catch (Exception e) {
				Log.err(e);
			}
		}
	}

	public interface LineHitHandler<T> {
		void get(T t, float x, float y);
	}

	public interface QuadTreeHandler {
		boolean get(Rect rect, boolean tree);
	}
}
