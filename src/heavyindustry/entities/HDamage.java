package heavyindustry.entities;

import arc.Core;
import arc.audio.Sound;
import arc.func.Boolf;
import arc.func.Cons;
import arc.func.Cons2;
import arc.func.Floatc;
import arc.func.Floatc2;
import arc.func.Floatf;
import arc.func.Intc2;
import arc.func.Prov;
import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Intersector;
import arc.math.geom.Point2;
import arc.math.geom.Position;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.FloatSeq;
import arc.struct.IntFloatMap;
import arc.struct.IntSeq;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pool;
import arc.util.pooling.Pool.Poolable;
import arc.util.pooling.Pools;
import heavyindustry.content.HFx;
import heavyindustry.entities.HEntity.LineHitHandler;
import heavyindustry.math.Mathm;
import heavyindustry.util.BoolGrid;
import heavyindustry.util.ValueMap;
import mindustry.Vars;
import mindustry.ai.types.MissileAI;
import mindustry.core.World;
import mindustry.entities.Damage;
import mindustry.entities.Damage.Collided;
import mindustry.entities.Effect;
import mindustry.entities.Sized;
import mindustry.entities.Units;
import mindustry.entities.Units.Sortf;
import mindustry.game.EventType.UnitDamageEvent;
import mindustry.game.Team;
import mindustry.game.Teams.TeamData;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;
import mindustry.gen.Posc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;
import mindustry.world.Tile;

public final class HDamage {
	public static final Seq<Unit> list = new Seq<>(Unit.class);

	static final UnitDamageEvent bulletDamageEvent = new UnitDamageEvent();
	static final Rect rect = new Rect(), rectAlt = new Rect(), hitrect = new Rect();
	static final Vec2 vec = new Vec2(), vec2 = new Vec2(), vec3 = new Vec2(), seg1 = new Vec2(), seg2 = new Vec2();
	static final Seq<Building> builds = new Seq<>(Building.class);
	static final Seq<Unit> units = new Seq<>(Unit.class);
	static final IntSet collidedBlocks = new IntSet();
	static final IntFloatMap damages = new IntFloatMap();
	static final Seq<Collided> collided = new Seq<>(Collided.class);
	static final Pool<Collided> collidePool = Pools.get(Collided.class, Collided::new);
	static final FloatSeq distances = new FloatSeq();
	static final BoolGrid collideLineCollided = new BoolGrid();
	static final IntSeq lineCast = new IntSeq(), lineCastNext = new IntSeq();
	static final Seq<Hit> hitEffects = new Seq<>(Hit.class);

	static Tile furthest;
	static Building tmpBuild;
	static Unit tmpUnit;
	static float tmpFloat;
	static boolean check;
	static Posc result;
	static float cdist;
	static int idx;
	static boolean hit, hit2;
	static final Seq<Hit> hseq = new Seq<>(Hit.class);
	static final BasicPool<Hit> hPool = new BasicPool<>(Hit::new);

	/// Don't let anyone instantiate this class.
	private HDamage() {}

	public static void chain(Position origin, @Nullable Position targetPos, Team team, Unit current, IntSeq collided, Sound hitSound, Effect hitEffect, float power, float initialPower, float width, float distanceDamageFalloff, float pierceDamageFactor, int branches, float segmentLength, float arc, Color color) {
		current.damage(power);
		hitSound.at(current.x, current.y, Mathf.random(0.8f, 1.1f));
		//Scales down width based on percent of power left
		float w = width * power / (initialPower);

		HFx.chainLightning.at(current.x, current.y, 0, color, new HFx.VisualLightningHolder() {
			@Override
			public Vec2 start() {
				return new Vec2(origin.getX(), origin.getY());
			}

			@Override
			public Vec2 end() {
				return new Vec2(current.x, current.y);
			}

			@Override
			public float width() {
				return w;
			}

			@Override
			public float segLength() {
				return segmentLength;
			}

			@Override
			public float arc() {
				return arc;
			}
		});
		hitEffect.at(current.x, current.y, 0, color);
		if (!current.dead) collided.add(current.id);

		float effectiveRange = power / distanceDamageFalloff;

		final float newPower = power * (pierceDamageFactor == 0 ? 1 : pierceDamageFactor);

		boolean derelict = team.id == Team.derelict.id;
		int teamID = team.id;

		Position tPos = targetPos == null ? current : targetPos;

		Time.run(15, () -> {
			Seq<Unit> units = Groups.unit.intersect(current.x - effectiveRange, current.y - effectiveRange, effectiveRange * 2, effectiveRange * 2);
			units.sort(u -> u.dst(tPos));
			if (units.contains(current)) units.remove(current);
			list.clear();
			for (int i = 0; i < Math.min(branches, units.size); i++) {
				Unit unit = units.get(i);
				if (collided.contains(unit.id) || !derelict && unit.team.id == teamID) continue;
				float dst = unit.dst(current);
				if (dst > effectiveRange) break;
				list.add(unit);
			}
			if (list.size == 0) return;
			float numberMultiplier = 1.0f / list.size;

			for (Unit u : list) {
				float newDamage = power - distanceDamageFalloff * current.dst(u);
				Log.info(Core.bundle.format("Old power was: {0}, while new one is {1}", power, newPower));
				if (newPower < 0) return;
				chain(current, u, collided, hitSound, hitEffect, newDamage * numberMultiplier, initialPower, width, distanceDamageFalloff, pierceDamageFactor, branches, segmentLength, arc, color);
			}
		});
	}

	public static void chain(Position origin, Unit current, IntSeq collided, Sound hitSound, Effect hitEffect, float power, float initialPower, float width, float distanceDamageFalloff, float pierceDamageFactor, int branches, float segmentLength, float arc, Color color) {
		chain(origin, null, Team.derelict, current, collided, hitSound, hitEffect, power, initialPower, width, distanceDamageFalloff, pierceDamageFactor, branches, segmentLength, arc, color);
	}

	public static void trueEachBlock(float wx, float wy, float range, Cons<Building> cons) {
		collidedBlocks.clear();
		int tx = World.toTile(wx);
		int ty = World.toTile(wy);

		int tileRange = Mathf.floorPositive(range / Vars.tilesize);

		for (int x = tx - tileRange - 2; x <= tx + tileRange + 2; x++) {
			for (int y = ty - tileRange - 2; y <= ty + tileRange + 2; y++) {
				if (Mathf.within(x * Vars.tilesize, y * Vars.tilesize, wx, wy, range)) {
					Building other = Vars.world.build(x, y);
					if (other != null && !collidedBlocks.contains(other.pos())) {
						cons.get(other);
						collidedBlocks.add(other.pos());
					}
				}
			}
		}
	}

	public static void trueEachTile(float wx, float wy, float range, Cons<Tile> cons) {
		collidedBlocks.clear();
		int tx = World.toTile(wx);
		int ty = World.toTile(wy);

		int tileRange = Mathf.floorPositive(range / Vars.tilesize);

		for (int x = tx - tileRange - 2; x <= tx + tileRange + 2; x++) {
			for (int y = ty - tileRange - 2; y <= ty + tileRange + 2; y++) {
				if (Mathf.within(x * Vars.tilesize, y * Vars.tilesize, wx, wy, range)) {
					Tile other = Vars.world.tile(x, y);
					if (other != null && !collidedBlocks.contains(other.pos())) {
						cons.get(other);
						collidedBlocks.add(other.pos());
					}
				}
			}
		}
	}

	public static void allNearbyEnemies(Team team, float x, float y, float radius, Cons<Healthc> cons) {
		Units.nearbyEnemies(team, x - radius, y - radius, radius * 2f, radius * 2f, unit -> {
			if (unit.within(x, y, radius + unit.hitSize / 2f) && !unit.dead) {
				cons.get(unit);
			}
		});

		trueEachBlock(x, y, radius, build -> {
			if (build.team != team && !build.dead && build.block != null) {
				cons.get(build);
			}
		});
	}

	public static boolean checkForTargets(Team team, float x, float y, float radius) {
		check = false;

		Units.nearbyEnemies(team, x - radius, y - radius, radius * 2f, radius * 2f, unit -> {
			if (unit.within(x, y, radius + unit.hitSize / 2f) && !unit.dead) {
				check = true;
			}
		});

		trueEachBlock(x, y, radius, build -> {
			if (build.team != team && !build.dead && build.block != null) {
				check = true;
			}
		});

		return check;
	}

	public static Teamc bestTarget(Team team, float cx, float cy, float x, float y, float range, Boolf<Unit> unitPred, Boolf<Building> tilePred, Sortf sort) {
		if (team == Team.derelict) return null;

		Unit unit = findEnemyUnit(team, cx, cy, x, y, range, unitPred, sort);
		if (unit != null) {
			return unit;
		} else {
			return findEnemyTile(team, cx, cy, x, y, range, tilePred);
		}
	}

	public static Unit findEnemyUnit(Team team, float cx, float cy, float x, float y, float range, Boolf<Unit> pred, Sortf unitSort) {
		tmpUnit = null;
		tmpFloat = Float.NEGATIVE_INFINITY;

		Units.nearbyEnemies(team, cx - range, cy - range, range * 2f, range * 2f, unit -> {
			float cost = unitSort.cost(unit, x, y);
			if (!unit.dead && tmpFloat < cost && unit.within(cx, cy, range + unit.hitSize / 2f) && pred.get(unit)) {
				tmpUnit = unit;
				tmpFloat = cost;
			}
		});

		return tmpUnit;
	}

	public static Building findEnemyTile(Team team, float cx, float cy, float x, float y, float range, Boolf<Building> pred) {
		tmpBuild = null;
		tmpFloat = 0;

		trueEachBlock(cx, cy, range, b -> {
			if (!(b.team() == team || (b.team() == Team.derelict && !Vars.state.rules.coreCapture)) && pred.get(b)) {
				//if a block has the same priority, the closer one should be targeted
				float dist = b.dst(x, y) - b.hitSize() / 2f;
				if (tmpBuild == null ||
						//if its closer and is at least equal priority
						(dist < tmpFloat && b.block.priority >= tmpBuild.block.priority) ||
						// block has higher priority (so range doesn't matter)
						(b.block.priority > tmpBuild.block.priority)) {
					tmpBuild = b;
					tmpFloat = dist;
				}
			}
		});

		return tmpBuild;
	}

	public static boolean collideLine(float damage, Team team, Effect effect, StatusEffect status, float statusDuration, float x, float y, float angle, float length, boolean ground, boolean air) {
		return collideLine(damage, team, effect, status, statusDuration, x, y, angle, length, ground, air, false);
	}

	/**
	 * Damage entities in a line.
	 * Only enemies of the specified team are damaged.
	 */
	public static boolean collideLine(float damage, Team team, Effect effect, StatusEffect status, float statusDuration, float x, float y, float angle, float length, boolean ground, boolean air, boolean buildings) {
		vec.trnsExact(angle, length);

		rect.setPosition(x, y).setSize(vec.x, vec.y);
		float x2 = x + vec.x, y2 = y + vec.y;

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

		check = false;

		Cons<Unit> cons = e -> {
			e.hitbox(hitrect);

			Vec2 vec = Geometry.raycastRect(x, y, x2, y2, hitrect.grow(expand * 2));

			if (vec != null && damage > 0) {
				effect.at(vec.x, vec.y, angle, team.color);
				e.damage(damage);
				e.apply(status, statusDuration);
				check = true;
			}
		};

		units.clear();

		Units.nearbyEnemies(team, rect, u -> {
			if (u.checkTarget(air, ground)) {
				units.add(u);
			}
		});

		units.sort(u -> u.dst2(x, y));
		units.each(cons);

		if (buildings) {
			collidedBlocks.clear();

			Intc2 collider = (cx, cy) -> {
				Building tile = Vars.world.build(cx, cy);
				boolean collide = tile != null && collidedBlocks.add(tile.pos());

				if (collide && damage > 0 && tile.team != team) {
					effect.at(tile.x, tile.y, angle, team.color);
					tile.damage(damage);
					check = true;
				}
			};

			seg1.set(x, y);
			seg2.set(seg1).add(vec);
			World.raycastEachWorld(x, y, seg2.x, seg2.y, (cx, cy) -> {
				collider.get(cx, cy);

				for (Point2 p : Geometry.d4) {
					Tile other = Vars.world.tile(p.x + cx, p.y + cy);
					if (other != null && Intersector.intersectSegmentRectangle(seg1, seg2, other.getBounds(Tmp.r1))) {
						collider.get(cx + p.x, cy + p.y);
					}
				}
				return false;
			});
		}

		return check;
	}

	/** {@link Damage#collideLine} but only hits missile units. */
	public static void missileCollideLine(Bullet hitter, Team team, Effect effect, float x, float y, float angle, float length, boolean large, boolean laser, int pierceCap) {
		if (pierceCap > 0) {
			length = findPierceLength(hitter, pierceCap, length);
		} else if (laser) {
			length = Damage.findLaserLength(hitter, length);
		}

		collidedBlocks.clear();
		vec.trnsExact(angle, length);

		float expand = 3f;

		rect.setPosition(x, y).setSize(vec.x, vec.y).normalize().grow(expand * 2f);
		float x2 = vec.x + x, y2 = vec.y + y;

		Units.nearbyEnemies(team, rect, u -> {
			if (u.checkTarget(hitter.type.collidesAir, hitter.type.collidesGround) && u.hittable() && u.controller() instanceof MissileAI) {
				u.hitbox(hitrect);

				Vec2 vec = Geometry.raycastRect(x, y, x2, y2, hitrect.grow(expand * 2));

				if (vec != null) {
					collided.add(collidePool.obtain().set(vec.x, vec.y, u));
				}
			}
		});

		int[] collideCount = {0};
		collided.sort(c -> hitter.dst2(c.x, c.y));
		collided.each(c -> {
			if (hitter.damage > 0 && (pierceCap <= 0 || collideCount[0] < pierceCap)) {
				if (c.target instanceof Unit u) {
					effect.at(c.x, c.y);
					u.collision(hitter, c.x, c.y);
					hitter.collision(u, c.x, c.y);
					collideCount[0]++;
				}
			}
		});

		collidePool.freeAll(collided);
		collided.clear();
	}

	/** Like {@link Damage#findPierceLength}, but uses an (x, y) coord instead of bullet position. */
	public static float findLaserLength(float x, float y, float angle, Team team, float length) {
		Tmp.v1.trns(angle, length);

		furthest = null;

		boolean found = World.raycast(World.toTile(x), World.toTile(y), World.toTile(x + Tmp.v1.x), World.toTile(y + Tmp.v1.y),
				(tx, ty) -> (furthest = Vars.world.tile(tx, ty)) != null && furthest.team() != team && furthest.block().absorbLasers);

		return found && furthest != null ? Math.max(6f, Mathf.dst(x, y, furthest.worldx(), furthest.worldy())) : length;
	}

	/** {@link Damage#findPierceLength} but it returns the distance to the point of contact, not the distance to the center of the target. */
	public static float findPierceLength(Bullet b, int pierceCap, float length) {
		vec.trnsExact(b.rotation(), length);
		rect.setPosition(b.x, b.y).setSize(vec.x, vec.y).normalize().grow(3f);

		//Max dist
		tmpFloat = Float.POSITIVE_INFINITY;

		distances.clear();

		World.raycast(b.tileX(), b.tileY(), World.toTile(b.x + vec.x), World.toTile(b.y + vec.y), (x, y) -> {
			//add distance to list so it can be processed
			Building build = Vars.world.build(x, y);

			if (build != null && build.team != b.team && build.collide(b) && b.checkUnderBuild(build, x * Vars.tilesize, y * Vars.tilesize)) {
				float dst = b.dst(x * Vars.tilesize, y * Vars.tilesize) - Vars.tilesize;
				distances.add(dst);

				if (b.type.laserAbsorb && build.absorbLasers()) {
					tmpFloat = Math.min(tmpFloat, dst);
					return true;
				}
			}

			return false;
		});

		Units.nearbyEnemies(b.team, rect, u -> {
			u.hitbox(hitrect);

			if (u.checkTarget(b.type.collidesAir, b.type.collidesGround) && u.hittable() && Intersector.intersectSegmentRectangle(b.x, b.y, b.x + vec.x, b.y + vec.y, hitrect)) {
				distances.add(b.dst(u) - u.hitSize());
			}
		});

		distances.sort();

		//return either the length when not enough things were pierced,
		//or the last pierced object if there were enough blockages
		return Math.min(distances.size < pierceCap || pierceCap < 0 ? length : Math.max(6f, distances.get(pierceCap - 1)), tmpFloat);
	}

	public static void completeDamage(Team team, float x, float y, float radius, float damage, float buildDmbMult, boolean air, boolean ground) {
		allNearbyEnemies(team, x, y, radius, t -> {
			if (t instanceof Unit u) {
				if (u.isFlying() && air || u.isGrounded() && ground) {
					u.damage(damage);
				}
			} else if (t instanceof Building b) {
				if (ground) {
					b.damage(team, damage * buildDmbMult);
				}
			}
		});
	}

	public static void completeDamage(Team team, float x, float y, float radius, float damage) {
		completeDamage(team, x, y, radius, damage, 1f, true, true);
	}

	/**
	 * Casts forward in a line.
	 *
	 * @return the collision point of the first encountered object.
	 */
	public static Vec2 linecast(boolean ground, boolean air, Team team, float x, float y, float angle, float length) {
		vec.trnsExact(angle, length);

		tmpBuild = null;

		if (ground) {
			seg1.set(x, y);
			seg2.set(seg1).add(vec);
			World.raycastEachWorld(x, y, seg2.x, seg2.y, (cx, cy) -> {
				Building tile = Vars.world.build(cx, cy);
				if (tile != null && tile.team != team) {
					tmpBuild = tile;
					Tmp.v1.set(cx * Vars.tilesize, cy * Vars.tilesize);
					return true;
				}
				return false;
			});
		}

		float expand = 3f;

		rect.setPosition(x, y).setSize(vec.x, vec.y).normalize().grow(expand * 2f);
		float x2 = vec.x + x, y2 = vec.y + y;

		tmpUnit = null;

		Units.nearbyEnemies(team, rect, e -> {
			if ((tmpUnit != null && e.dst2(x, y) > tmpUnit.dst2(x, y)) || !e.checkTarget(ground, air)) return;

			e.hitbox(hitrect);
			Vec2 vec = Geometry.raycastRect(x, y, x2, y2, hitrect.grow(expand * 2));

			if (vec != null) {
				tmpUnit = e;
				Tmp.v2.set(vec);
			}
		});

		if (tmpBuild != null && tmpUnit != null) {
			if (Mathf.dst2(x, y, Tmp.v1.x, Tmp.v1.y) <= Mathf.dst2(x, y, Tmp.v2.x, Tmp.v2.y)) {
				return Tmp.v1;
			}
		} else if (tmpBuild != null) {
			return Tmp.v1;
		} else if (tmpUnit != null) {
			return Tmp.v2;
		}

		return vec.add(x, y);
	}

	public static void collideLine(Bullet hitter, Team team, Effect effect, float x, float y, float angle, float length) {
		collideLine(hitter, team, effect, x, y, angle, length, false);
	}

	/**
	 * Damages entities in a line.
	 * Only enemies of the specified team are damaged.
	 */
	public static void collideLine(Bullet hitter, Team team, Effect effect, float x, float y, float angle, float length, boolean large) {
		collideLine(hitter, team, effect, x, y, angle, length, large, true);
	}

	/**
	 * Damages entities in a line.
	 * Only enemies of the specified team are damaged.
	 */
	public static void collideLine(Bullet hitter, Team team, Effect effect, float x, float y, float angle, float length, boolean large, boolean laser) {
		collideLine(hitter, team, effect, x, y, angle, length, large, laser, -1);
	}

	/**
	 * Damages entities in a line.
	 * Only enemies of the specified team are damaged.
	 */
	public static void collideLine(Bullet hitter, Team team, Effect effect, float x, float y, float angle, float length, boolean large, boolean laser, int pierceCap) {
		length = Damage.findLength(hitter, length, laser, pierceCap);
		hitter.fdata = length;

		collidedBlocks.clear();
		vec.trnsExact(angle, length);

		if (hitter.type.collidesGround && hitter.type.collidesTiles) {
			seg1.set(x, y);
			seg2.set(seg1).add(vec);
			World.raycastEachWorld(x, y, seg2.x, seg2.y, (cx, cy) -> {
				Building tile = Vars.world.build(cx, cy);
				boolean collide = tile != null && tile.collide(hitter) && hitter.checkUnderBuild(tile, cx * Vars.tilesize, cy * Vars.tilesize)
						&& ((tile.team != team && tile.collide(hitter)) || hitter.type.testCollision(hitter, tile)) && collidedBlocks.add(tile.pos());
				if (collide) {
					collided.add(collidePool.obtain().set(cx * Vars.tilesize, cy * Vars.tilesize, tile));

					for (Point2 p : Geometry.d4) {
						Tile other = Vars.world.tile(p.x + cx, p.y + cy);
						if (other != null && (large || Intersector.intersectSegmentRectangle(seg1, seg2, other.getBounds(Tmp.r1)))) {
							Building build = other.build;
							if (build != null && hitter.checkUnderBuild(build, cx * Vars.tilesize, cy * Vars.tilesize) && collidedBlocks.add(build.pos())) {
								collided.add(collidePool.obtain().set((p.x + cx * Vars.tilesize), (p.y + cy) * Vars.tilesize, build));
							}
						}
					}
				}
				return false;
			});
		}

		float expand = 3f;

		rect.setPosition(x, y).setSize(vec.x, vec.y).normalize().grow(expand * 2f);
		float x2 = vec.x + x, y2 = vec.y + y;

		Units.nearbyEnemies(team, rect, u -> {
			if (u.checkTarget(hitter.type.collidesAir, hitter.type.collidesGround) && u.hittable()) {
				u.hitbox(hitrect);

				Vec2 vec = Geometry.raycastRect(x, y, x2, y2, hitrect.grow(expand * 2));

				if (vec != null) {
					collided.add(collidePool.obtain().set(vec.x, vec.y, u));
				}
			}
		});

		int[] collideCount = {0};
		collided.sort(c -> hitter.dst2(c.x, c.y));
		collided.each(c -> {
			if (hitter.damage > 0 && (pierceCap <= 0 || collideCount[0] < pierceCap)) {
				if (c.target instanceof Unit u) {
					u.collision(hitter, c.x, c.y);
					hitter.collision(u, c.x, c.y);
					collideCount[0]++;
				} else if (c.target instanceof Building tile) {
					float health = tile.health;

					if (tile.team != team && tile.collide(hitter)) {
						tile.collision(hitter);
						hitter.type.hit(hitter, c.x, c.y);
						collideCount[0]++;
					}

					//try to heal the tile
					if (hitter.type.testCollision(hitter, tile)) {
						hitter.type.hitTile(hitter, tile, c.x, c.y, health, false);
					}
				}
			}
		});

		collidePool.freeAll(collided);
		collided.clear();
	}

	// -------------------- LEGACY ------------------------

	public static Bullet nearestBullet(float x, float y, float range, Boolf<Bullet> boolf) {
		result = null;
		cdist = range;
		Tmp.r1.setCentered(x, y, range * 2);
		Groups.bullet.intersect(Tmp.r1.x, Tmp.r1.y, Tmp.r1.width, Tmp.r1.height, b -> {
			float dst = b.dst(x, y);
			if (boolf.get(b) && b.within(x, y, range + b.hitSize) && (result == null || dst < cdist)) {
				result = b;
				cdist = dst;
			}
		});

		return (Bullet) result;
	}

	public static void shotgunRange(int points, float range, float angle, Floatc cons) {
		if (points <= 1) {
			cons.get(angle);
			return;
		}

		for (int i = 0; i < points; i++) {
			float in = Mathf.lerp(-range, range, i / (points - 1f));
			cons.get(in + angle);
		}
	}

	public static float[] castConeTile(float wx, float wy, float range, float angle, float cone, int rays, Cons2<Building, Tile> consBuilding, Boolf<Tile> insulator) {
		return castConeTile(wx, wy, range, angle, cone, consBuilding, insulator, new float[rays]);
	}

	public static float[] castConeTile(float wx, float wy, float range, float angle, float cone, Cons2<Building, Tile> consBuilding, Boolf<Tile> insulator, float[] ref) {
		collidedBlocks.clear();
		idx = 0;
		float expand = 3;
		rect.setCentered(wx, wy, expand);

		shotgunRange(3, cone, angle, con -> {
			vec2.trns(con, range).add(wx, wy);
			rectAlt.setCentered(vec2.x, vec2.y, expand);
			rect.merge(rectAlt);
		});

		if (insulator != null) {
			shotgunRange(ref.length, cone, angle, con -> {
				vec2.trns(con, range).add(wx, wy);
				ref[idx] = range * range;
				World.raycastEachWorld(wx, wy, vec2.x, vec2.y, (x, y) -> {
					Tile tile = Vars.world.tile(x, y);
					if (tile != null && insulator.get(tile)) {
						ref[idx] = Mathf.dst2(wx, wy, x * Vars.tilesize, y * Vars.tilesize);
						return true;
					}
					return false;
				});
				idx++;
			});
		}

		int tx = Mathf.round(rect.x / Vars.tilesize);
		int ty = Mathf.round(rect.y / Vars.tilesize);
		int tw = tx + Mathf.round(rect.width / Vars.tilesize);
		int th = ty + Mathf.round(rect.height / Vars.tilesize);
		for (int x = tx; x <= tw; x++) {
			for (int y = ty; y <= th; y++) {
				float ofX = (x * Vars.tilesize) - wx, ofY = (y * Vars.tilesize) - wy;
				int angIdx = Mathm.clamp(Mathf.round(((Mathm.angleDistSigned(Angles.angle(ofX, ofY), angle) + cone) / (cone * 2f)) * (ref.length - 1)), 0, ref.length - 1);
				float dst = ref[angIdx];
				float dst2 = Mathf.dst2(ofX, ofY);
				if (dst2 < dst && dst2 < range * range && Mathm.angleDist(Angles.angle(ofX, ofY), angle) < cone) {
					Tile tile = Vars.world.tile(x, y);
					Building build = null;
					if (tile != null) {
						Building b = Vars.world.build(x, y);
						if (b != null && !collidedBlocks.contains(b.id)) {
							build = b;
							collidedBlocks.add(b.id);
						}

						consBuilding.get(build, tile);
					}
				}
			}
		}

		collidedBlocks.clear();
		return ref;
	}

	public static void castCone(float wx, float wy, float range, float angle, float cone, BuildCone consTile) {
		castCone(wx, wy, range, angle, cone, consTile, null);
	}

	public static void castCone(float wx, float wy, float range, float angle, float cone, UnitCone consUnit) {
		castCone(wx, wy, range, angle, cone, null, consUnit);
	}

	public static void castCone(float wx, float wy, float range, float angle, float cone, BuildCone consTile, UnitCone consUnit) {
		collidedBlocks.clear();
		float expand = 3;
		float rangeSquare = range * range;
		if (consTile != null) {
			rect.setCentered(wx, wy, expand);
			for (int i = 0; i < 3; i++) {
				float angleC = (-1 + i) * cone + angle;
				vec2.trns(angleC, range).add(wx, wy);
				rectAlt.setCentered(vec2.x, vec2.y, expand);
				rect.merge(rectAlt);
			}

			int tx = Mathf.round(rect.x / Vars.tilesize);
			int ty = Mathf.round(rect.y / Vars.tilesize);
			int tw = tx + Mathf.round(rect.width / Vars.tilesize);
			int th = ty + Mathf.round(rect.height / Vars.tilesize);
			for (int x = tx; x <= tw; x++) {
				for (int y = ty; y <= th; y++) {
					float temp = Angles.angle(wx, wy, x * Vars.tilesize, y * Vars.tilesize);
					float tempDst = Mathf.dst(x * Vars.tilesize, y * Vars.tilesize, wx, wy);
					if (tempDst >= rangeSquare || !Angles.within(temp, angle, cone)) continue;

					Tile other = Vars.world.tile(x, y);
					if (other == null) continue;
					if (!collidedBlocks.contains(other.pos())) {
						float dst = 1f - tempDst / range;
						float anDst = 1f - Angles.angleDist(temp, angle) / cone;
						consTile.get(other, other.build, dst, anDst);
						collidedBlocks.add(other.pos());
					}
				}
			}
		}

		if (consUnit != null) {
			Groups.unit.intersect(wx - range, wy - range, range * 2f, range * 2f, e -> {
				float temp = Angles.angle(wx, wy, e.x, e.y);
				float tempDst = Mathf.dst(e.x, e.y, wx, wy);
				if (tempDst >= rangeSquare || !Angles.within(temp, angle, cone)) return;

				float dst = 1f - tempDst / range;
				float anDst = 1f - Angles.angleDist(temp, angle) / cone;
				consUnit.get(e, dst, anDst);
			});
		}
	}

	public static float[] castCircle(float wx, float wy, float range, int rays, Boolf<Building> filter, Cons<Building> cons, Boolf<Tile> insulator) {
		collidedBlocks.clear();
		float[] cast = new float[rays];

		for (int i = 0; i < cast.length; i++) {
			cast[i] = range;
			float ang = i * (360f / cast.length);
			vec2.trns(ang, range).add(wx, wy);
			int s = i;

			World.raycastEachWorld(wx, wy, vec2.x, vec2.y, (cx, cy) -> {
				Tile t = Vars.world.tile(cx, cy);
				if (t != null && t.block() != null && insulator.get(t)) {
					float dst = t.dst(wx, wy);
					cast[s] = dst;
					return true;
				}

				return false;
			});
		}

		Vars.indexer.allBuildings(wx, wy, range, build -> {
			if (!filter.get(build)) return;
			float ang = Angles.angle(wx, wy, build.x, build.y);
			float dst = build.dst2(wx, wy) - ((build.hitSize() * build.hitSize()) / 2f);

			float d = cast[Mathf.mod(Mathf.round((ang % 360f) / (360f / cast.length)), cast.length)];
			if (dst <= d * d) cons.get(build);
		});

		return cast;
	}

	public static void collideLineRawEnemyRatio(Team team, float x, float y, float x2, float y2, float width, BuildCollide buildingCons, UnitCollide unitCons, Floatc2 effectHandler) {
		float minRatio = 0.05f;
		collideLineRawEnemy(team, x, y, x2, y2, width, (building, direct) -> {
			float size = (building.block.size * Vars.tilesize / 2f);
			float dst = Mathm.clamp(1f - ((Intersector.distanceSegmentPoint(x, y, x2, y2, building.x, building.y) - width) / size), minRatio, 1f);
			return buildingCons.get(building, dst, direct);
		}, unit -> {
			float size = (unit.hitSize / 2f);
			float dst = Mathm.clamp(1f - ((Intersector.distanceSegmentPoint(x, y, x2, y2, unit.x, unit.y) - width) / size), minRatio, 1f);
			return unitCons.get(unit, dst);
		}, effectHandler, true);
	}

	public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, float width, boolean hitTiles, boolean hitUnits, boolean stopSort, HitHandler handler) {
		collideLineRaw(x, y, x2, y2, width, width, b -> b.team != team, u -> u.team != team, hitTiles, hitUnits, h -> h.dst2(x, y), handler, stopSort);
	}

	public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, BuildBoolf2 buildingCons, Cons<Unit> unitCons, Floatc2 effectHandler, boolean stopSort) {
		collideLineRaw(x, y, x2, y2, 3f, b -> b.team != team, u -> u.team != team, buildingCons, unitCons, healthc -> healthc.dst2(x, y), effectHandler, stopSort);
	}

	public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, float width, BuildBoolf2 buildingCons, Boolf<Unit> unitCons, Floatc2 effectHandler, boolean stopSort) {
		collideLineRaw(x, y, x2, y2, width, width, b -> b.team != team, u -> u.team != team, buildingCons, unitCons, healthc -> healthc.dst2(x, y), effectHandler, stopSort);
	}

	public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, float width, BuildBoolf2 buildingCons, Boolf<Unit> unitCons, Floatf<Healthc> sort, Floatc2 effectHandler, boolean stopSort) {
		collideLineRaw(x, y, x2, y2, width, width, b -> b.team != team, u -> u.team != team, buildingCons, unitCons, sort, effectHandler, stopSort);
	}

	public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, float unitWidth, float tileWidth, BuildBoolf2 buildingCons, Boolf<Unit> unitCons, Floatc2 effectHandler, boolean stopSort) {
		collideLineRaw(x, y, x2, y2, unitWidth, tileWidth, b -> b.team != team, u -> u.team != team, buildingCons, unitCons, healthc -> healthc.dst2(x, y), effectHandler, stopSort);
	}

	public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, BuildBoolf2 buildingCons, Boolf<Unit> unitCons, Floatc2 effectHandler, boolean stopSort) {
		collideLineRaw(x, y, x2, y2, 3f, b -> b.team != team, u -> u.team != team, buildingCons, unitCons, healthc -> healthc.dst2(x, y), effectHandler, stopSort);
	}

	public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, BuildBoolf2 buildingCons, Cons<Unit> unitCons, Floatf<Healthc> sort, Floatc2 effectHandler) {
		collideLineRaw(x, y, x2, y2, 3f, b -> b.team != team, u -> u.team != team, buildingCons, unitCons, sort, effectHandler);
	}

	public static void collideLineRawEnemy(Team team, float x, float y, float x2, float y2, float width, Boolf<Healthc> pred, BuildBoolf2 buildingCons, Boolf<Unit> unitCons, Floatc2 effectHandler, boolean stopSort) {
		collideLineRaw(x, y, x2, y2, width, width, b -> b.team != team && pred.get(b), u -> u.team != team && pred.get(u), buildingCons, unitCons, healthc -> healthc.dst2(x, y), effectHandler, stopSort);
	}

	public static void collideLineRaw(float x, float y, float x2, float y2, float unitWidth, Boolf<Building> buildingFilter, Boolf<Unit> unitFilter, BuildBoolf2 buildingCons, Cons<Unit> unitCons, Floatf<Healthc> sort, Floatc2 effectHandler) {
		collideLineRaw(x, y, x2, y2, unitWidth, buildingFilter, unitFilter, buildingCons, unitCons, sort, effectHandler, false);
	}

	public static void collideLineRaw(float x, float y, float x2, float y2, float unitWidth, Boolf<Building> buildingFilter, Boolf<Unit> unitFilter, BuildBoolf2 buildingCons, Cons<Unit> unitCons, Floatf<Healthc> sort, Floatc2 effectHandler, boolean stopSort) {
		collideLineRaw(x, y, x2, y2, unitWidth, buildingFilter, unitFilter, buildingCons, unitCons == null ? null : unit -> {
			unitCons.get(unit);
			return false;
		}, sort, effectHandler, stopSort);
	}

	public static void collideLineRaw(float x, float y, float x2, float y2, float unitWidth, Boolf<Building> buildingFilter, Boolf<Unit> unitFilter, BuildBoolf2 buildingCons, Boolf<Unit> unitCons, Floatf<Healthc> sort, Floatc2 effectHandler, boolean stopSort) {
		collideLineRaw(x, y, x2, y2, unitWidth, 0f, buildingFilter, unitFilter, buildingCons, unitCons, sort, effectHandler, stopSort);
	}

	public static void collideLineRaw(float x, float y, float x2, float y2, float unitWidth, float tileWidth, Boolf<Building> buildingFilter, Boolf<Unit> unitFilter, BuildBoolf2 buildingCons, Boolf<Unit> unitCons, Floatf<Healthc> sort, Floatc2 effectHandler, boolean stopSort) {
		collideLineRaw(x, y, x2, y2, unitWidth, tileWidth,
				buildingFilter, unitFilter, buildingCons != null, unitCons != null,
				sort, (ex, ey, ent, direct) -> {
					boolean hit = false;
					if (unitCons != null && direct && ent instanceof Unit) {
						hit = unitCons.get((Unit) ent);
					} else if (buildingCons != null && ent instanceof Building) {
						hit = buildingCons.get((Building) ent, direct);
					}

					if (effectHandler != null && direct) effectHandler.get(ex, ey);
					return hit;
				}, stopSort
		);
	}

	public static void collideLineRaw(float x, float y, float x2, float y2, float unitWidth, float tileWidth, Boolf<Building> buildingFilter, Boolf<Unit> unitFilter, boolean hitTile, boolean hitUnit, Floatf<Healthc> sort, HitHandler hitHandler, boolean stopSort) {
		hitEffects.clear();
		lineCast.clear();
		lineCastNext.clear();
		collidedBlocks.clear();

		vec2.set(x2, y2);
		if (hitTile) {
			collideLineCollided.clear();
			Runnable cast = () -> {
				hit2 = false;
				lineCast.each(i -> {
					int tx = Point2.x(i), ty = Point2.y(i);
					Building build = Vars.world.build(tx, ty);

					boolean hit = false;
					if (build != null && (buildingFilter == null || buildingFilter.get(build)) && collidedBlocks.add(build.pos())) {
						if (sort == null) {
							hit = hitHandler.get(tx * Vars.tilesize, ty * Vars.tilesize, build, true);
						} else {
							hit = hitHandler.get(tx * Vars.tilesize, ty * Vars.tilesize, build, false);
							Hit he = Pools.obtain(Hit.class, Hit::new);
							he.entity = build;
							he.x = tx * Vars.tilesize;
							he.y = ty * Vars.tilesize;

							hitEffects.add(he);
						}

						if (hit && !hit2) {
							vec2.trns(Angles.angle(x, y, x2, y2), Mathf.dst(x, y, build.x, build.y)).add(x, y);
							hit2 = true;
						}
					}

					Vec2 segment = Intersector.nearestSegmentPoint(x, y, vec2.x, vec2.y, tx * Vars.tilesize, ty * Vars.tilesize, vec3);
					if (!hit && tileWidth > 0f) {
						for (Point2 p : Geometry.d8) {
							int newX = (p.x + tx);
							int newY = (p.y + ty);
							boolean within = !hit2 || Mathf.within(x / Vars.tilesize, y / Vars.tilesize, newX, newY, vec2.dst(x, y) / Vars.tilesize);
							if (segment.within(newX * Vars.tilesize, newY * Vars.tilesize, tileWidth) && collideLineCollided.within(newX, newY) && !collideLineCollided.get(newX, newY) && within) {
								lineCastNext.add(Point2.pack(newX, newY));
								collideLineCollided.set(newX, newY, true);
							}
						}
					}
				});

				lineCast.clear();
				lineCast.addAll(lineCastNext);
				lineCastNext.clear();
			};

			World.raycastEachWorld(x, y, x2, y2, (cx, cy) -> {
				if (collideLineCollided.within(cx, cy) && !collideLineCollided.get(cx, cy)) {
					lineCast.add(Point2.pack(cx, cy));
					collideLineCollided.set(cx, cy, true);
				}

				cast.run();
				return hit2;
			});

			while (!lineCast.isEmpty()) cast.run();
		}

		if (hitUnit) {
			rect.setPosition(x, y).setSize(vec2.x - x, vec2.y - y);

			if (rect.width < 0) {
				rect.x += rect.width;
				rect.width *= -1;
			}

			if (rect.height < 0) {
				rect.y += rect.height;
				rect.height *= -1;
			}

			rect.grow(unitWidth * 2f);
			Groups.unit.intersect(rect.x, rect.y, rect.width, rect.height, unit -> {
				if (unitFilter == null || unitFilter.get(unit)) {
					unit.hitbox(hitrect);
					hitrect.grow(unitWidth * 2);

					Vec2 vec = Geometry.raycastRect(x, y, vec2.x, vec2.y, hitrect);

					if (vec != null) {
						float scl = (unit.hitSize - unitWidth) / unit.hitSize;
						vec.sub(unit).scl(scl).add(unit);
						if (sort == null) {
							hitHandler.get(vec.x, vec.y, unit, true);
						} else {
							Hit he = Pools.obtain(Hit.class, Hit::new);
							he.entity = unit;
							he.x = vec.x;
							he.y = vec.y;
							hitEffects.add(he);
						}
					}
				}
			});
		}

		if (sort != null) {
			hit = false;
			hitEffects.sort(he -> sort.get(he.entity)).each(he -> {
				if (!stopSort || !hit) {
					hit = hitHandler.get(he.x, he.y, he.entity, true);
				}

				Pools.free(he);
			});
		}

		hitEffects.clear();
	}

	/**
	 * Casts forward in a line.
	 *
	 * @return the first encountered model.
	 * There's an issue with the one in 126.2, which I fixed in a pr. This can be removed after the next Mindustry release.
	 */
	public static Healthc linecast(Bullet hitter, float x, float y, float angle, float length) {
		vec2.trns(angle, length);

		tmpBuild = null;

		if (hitter.type.collidesGround) {
			World.raycastEachWorld(x, y, x + vec2.x, y + vec2.y, (cx, cy) -> {
				Building tile = Vars.world.build(cx, cy);
				if (tile != null && tile.team != hitter.team) {
					tmpBuild = tile;
					return true;
				}
				return false;
			});
		}

		rect.setPosition(x, y).setSize(vec2.x, vec2.y);
		float x2 = vec2.x + x, y2 = vec2.y + y;

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

		tmpUnit = null;

		Units.nearbyEnemies(hitter.team, rect, e -> {
			if ((tmpUnit != null && e.dst2(x, y) > tmpUnit.dst2(x, y)) || !e.checkTarget(hitter.type.collidesAir, hitter.type.collidesGround))
				return;

			e.hitbox(hitrect);
			Rect other = hitrect;
			other.y -= expand;
			other.x -= expand;
			other.width += expand * 2;
			other.height += expand * 2;

			Vec2 vec = Geometry.raycastRect(x, y, x2, y2, other);

			if (vec != null) {
				tmpUnit = e;
			}
		});

		if (tmpBuild != null && tmpUnit != null) {
			if (Mathf.dst2(x, y, tmpBuild.getX(), tmpBuild.getY()) <= Mathf.dst2(x, y, tmpUnit.getX(), tmpUnit.getY())) {
				return tmpBuild;
			}
		} else if (tmpBuild != null) {
			return tmpBuild;
		}

		return tmpUnit;
	}

	public static float hitLaser(Team team, float width, float x1, float y1, float x2, float y2, Boolf<Healthc> within, Boolf<Healthc> stop, LineHitHandler<Healthc> cons) {
		hseq.removeAll(h -> {
			hPool.free(h);
			return true;
		});
		float ll = Mathf.dst(x1, y1, x2, y2);

		for (TeamData data : Vars.state.teams.present) {
			if (data.team != team) {
				if (data.unitTree != null) {
					HEntity.intersectLine(data.unitTree, width, x1, y1, x2, y2, (t, x, y) -> {
						if (within != null && !within.get(t)) return;
						Hit h = hPool.obtain();
						h.entity = t;
						h.x = x;
						h.y = y;
						hseq.add(h);
					});
				}
				if (data.buildingTree != null) {
					HEntity.intersectLine(data.buildingTree, width, x1, y1, x2, y2, (t, x, y) -> {
						if (within != null && !within.get(t)) return;
						Hit h = hPool.obtain();
						h.entity = t;
						h.x = x;
						h.y = y;
						hseq.add(h);
					});
				}
			}
		}
		hseq.sort(a -> a.entity.dst2(x1, y1));
		for (Hit hit : hseq) {
			Healthc t = hit.entity;

			cons.get(t, hit.x, hit.y);
			if (stop.get(t)) {
				ll = Mathf.dst(x1, y1, hit.x, hit.y) - (t instanceof Sized s ? s.hitSize() / 4f : 0f);
				break;
			}
		}
		return ll;
	}

	public static void add(ValueMap o, String key, float f) {
		if (!o.has(key)) {
			o.put(key, f);
			return;
		}
		o.put(key, o.getFloat(key) + f);
	}

	public static float getFloat(ValueMap o, String key, float defaultVal) {
		if (!o.has(key)) {
			return defaultVal;
		}
		return o.getFloat(key);
	}

	public interface BuildBoolf2 {
		boolean get(Building a, boolean b);
	}

	public interface BuildCollide {
		boolean get(Building build, float a, boolean b);
	}

	public interface UnitCollide {
		boolean get(Unit unit, float a);
	}

	public interface UnitCone {
		void get(Unit unit, float dst, float angDelta);
	}

	public interface BuildCone {
		void get(Tile tile, Building build, float dst, float angDamage);
	}

	public interface HitHandler {
		boolean get(float x, float y, Healthc ent, boolean direct);
	}

	public static class Hit implements Poolable {
		public Healthc entity;
		public float x, y;

		@Override
		public void reset() {
			entity = null;
			x = y = 0f;
		}
	}

	public static class BasicPool<T> extends Pool<T> {
		public final Prov<T> prov;

		public BasicPool(Prov<T> f) {
			prov = f;
		}

		@Override
		protected T newObject() {
			return prov.get();
		}
	}
}
