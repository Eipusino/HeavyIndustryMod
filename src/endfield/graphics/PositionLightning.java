package endfield.graphics;

import arc.func.Cons;
import arc.func.Floatc2;
import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Geometry;
import arc.math.geom.Position;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.FloatSeq;
import arc.struct.Seq;
import endfield.content.Fx2;
import endfield.entities.bullet.EffectBulletType;
import endfield.util.Vec2Seq;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.core.World;
import mindustry.entities.Lightning;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Healthc;

import static mindustry.Vars.headless;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

/**
 * Provide methods that can generate Position to Position Lightningf.
 * <p>{@code Tmp} <b>var</b> is available.
 * <p>Completely independent class.
 *
 * @author Yuria
 * @implNote The method implements the generation of random lightning effect <b>from point to point</b> and complete certain action at <b>target point</b> through {@link Cons}.
 * @apiNote <li> {@code hitPointMovement} {@link Cons} used to run specific action at the target point.
 * <li> {@code WIDTH}: {@value WIDTH} used to control the stroke of the lightning.
 * <li> {@code RANGE_RAND}: {@value RANGE_RAND} used to control the base xRand range of every part of the lightning.
 * <li> {@code ROT_DST}: {@value ROT_DST} used to control the length of every part of the lightning.
 * @see Position
 * @see Vec2
 * @see Geometry
 * @see Cons
 */
public final class PositionLightning {
	public static final BulletType hitter = new EffectBulletType(5f) {{
		absorbable = true;
		collides = collidesAir = collidesGround = collidesTiles = true;
		status = StatusEffects.shocked;
		statusDuration = 10f;
		hittable = false;
	}};

	/** Spawns Nothing at the hit point. */
	public static final Cons<Position> none = p -> {};
	/** Lighting Effect Lifetime. */
	public static final float lifetime = 20f;
	/** Lighting Effect Default Width, apply it manually. */
	public static final float WIDTH = 2.5f;
	/** Lighting Effect X-Rand. */
	public static final float RANGE_RAND = 5f;
	/** Lighting Effect Length Between Nodes. */
	public static final float ROT_DST = tilesize * 0.6f;
	/** Used for range spawn, make the lightning more random and has smoother spacing. */
	public static float trueHitChance = 1;

	/** Don't let anyone instantiate this class. */
	private PositionLightning() {}

	/** [0, 1] */
	public static void setHitChance(float f) {
		trueHitChance = f;
	}

	/** Must Hit */
	public static void setHitChanceDef() {
		trueHitChance = 1;
	}

	/** Lightningf's randX. Modify it if needed. */
	static float getBoltRandomRange() {
		return Mathf.random(1f, 7f);
	}

	static Building furthest = null;

	static final Seq<Healthc> entities = new Seq<>(Healthc.class);

	static final Rect rect = new Rect();
	static final Rand rand = new Rand();
	static final FloatSeq floatSeq = new FloatSeq();
	static final Vec2 tmp1 = new Vec2(), tmp2 = new Vec2(), tmp3 = new Vec2();

	/**
	 * METHODS
	 * <p>create lightning to the enemies in range.
	 * <p>A radius create method that with a Bullet owner.
	 */
	public static void createRange(Bullet owner, float range, int maxHit, Color color, boolean createSubLightning, float width, int lightningNum, Cons<Position> hitPointMovement) {
		createRange(owner, owner, owner.team, range, maxHit, color, createSubLightning, 0, 0, width, lightningNum, hitPointMovement);
	}

	public static void createRange(Bullet owner, boolean hitAir, boolean hitGround, Position from, Team team, float range, int maxHit, Color color, boolean createSubLightning, float damage, int subLightningLength, float width, int lightningNum, Cons<Position> hitPointMovement) {
		entities.clear();

		whetherAdd(entities, team, rect.setSize(range * 2f).setCenter(from.getX(), from.getY()), maxHit, hitGround, hitAir);

		for (Healthc h : entities) {
			create(owner, team, from, h, color, createSubLightning, damage, subLightningLength, width, lightningNum, hitPointMovement);
		}
	}

	public static void createRange(Bullet owner, Position from, Team team, float range, int maxHit, Color color, boolean createSubLightning, float damage, int subLightningLength, float width, int lightningNum, Cons<Position> hitPointMovement) {
		createRange(owner, owner == null || owner.type.collidesAir, owner == null || owner.type.collidesGround, from, team, range, maxHit, color, createSubLightning, damage, subLightningLength, width, lightningNum, hitPointMovement);
	}

	public static void createLength(Bullet owner, Team team, Position from, float length, float angle, Color color, boolean createSubLightning, float damage, int subLightningLength, float width, int lightningNum, Cons<Position> hitPointMovement) {
		create(owner, team, from, tmp2.trns(angle, length).add(from), color, createSubLightning, damage, subLightningLength, width, lightningNum, hitPointMovement);
	}

	/** A create method that with a Bullet owner. */
	public static void create(Entityc owner, Team team, Position from, Position target, Color color, boolean createSubLightning, float damage, int subLightningLength, float lightningWidth, int lightningNum, Cons<Position> hitPointMovement) {
		if (!Mathf.chance(trueHitChance)) return;

		Position sureTarget = findInterceptedPoint(from, target, team);
		hitPointMovement.get(sureTarget);

		if (createSubLightning) {
			if (owner instanceof Bullet b) {
				for (int i = 0; i < b.type.lightning; i++) {
					Lightning.create(b, color, b.type.lightningDamage < 0f ? b.damage : b.type.lightningDamage, sureTarget.getX(), sureTarget.getY(), b.rotation() + Mathf.range(b.type.lightningCone / 2f) + b.type.lightningAngle, b.type.lightningLength + Mathf.random(b.type.lightningLengthRand));
				}
			} else for (int i = 0; i < 3; i++) {
				Lightning.create(team, color, damage <= 0f ? 1f : damage, sureTarget.getX(), sureTarget.getY(), Mathf.random(360f), subLightningLength);
			}
		}

		float realDamage = damage;

		if (realDamage <= 0) {
			if (owner instanceof Bullet b) {
				realDamage = b.damage > 0 ? b.damage : 1;
			} else {
				realDamage = 1;
			}
		}

		hitter.create(owner, team, sureTarget.getX(), sureTarget.getY(), 1).damage(realDamage);

		createEffect(from, sureTarget, color, lightningNum, lightningWidth);
	}

	public static void create(Entityc owner, Team team, float fromX, float fromY, float targetX, float targetY, Color color, boolean createSubLightning, float damage, int subLightningLength, float lightningWidth, int lightningNum, Floatc2 hitPointMovement) {
		if (!Mathf.chance(trueHitChance)) return;

		float sureTargetX, sureTargetY;

		furthest = null;

		if (Geometry.raycast(
				World.toTile(fromX),
				World.toTile(fromY),
				World.toTile(targetX),
				World.toTile(targetY),
				(x, y) -> (furthest = world.build(x, y)) != null && furthest.team() != team && furthest.block.insulated
		) && furthest != null) {
			sureTargetX = furthest.x;
			sureTargetY = furthest.y;
		} else {
			sureTargetX = targetX;
			sureTargetY = targetY;
		}

		hitPointMovement.get(sureTargetX, sureTargetY);

		if (createSubLightning) {
			if (owner instanceof Bullet b) {
				for (int i = 0; i < b.type.lightning; i++) {
					Lightning.create(b, color, b.type.lightningDamage < 0f ? b.damage : b.type.lightningDamage, sureTargetX, sureTargetY, b.rotation() + Mathf.range(b.type.lightningCone / 2f) + b.type.lightningAngle, b.type.lightningLength + Mathf.random(b.type.lightningLengthRand));
				}
			} else for (int i = 0; i < 3; i++) {
				Lightning.create(team, color, damage <= 0f ? 1f : damage, sureTargetX, sureTargetY, Mathf.random(360f), subLightningLength);
			}
		}

		float realDamage = damage;

		if (realDamage <= 0) {
			if (owner instanceof Bullet b) {
				realDamage = b.damage > 0 ? b.damage : 1;
			} else {
				realDamage = 1;
			}
		}

		hitter.create(owner, team, sureTargetX, sureTargetY, 1).damage(realDamage);

		createEffect(fromX, fromY, sureTargetX, sureTargetY, color, lightningNum, lightningWidth);
	}

	public static void createRandom(Bullet owner, Team team, Position from, float rand, Color color, boolean createSubLightning, float damage, int subLightningLength, float width, int lightningNum, Cons<Position> hitPointMovement) {
		create(owner, team, from, tmp2.rnd(rand).scl(Mathf.random(1f)).add(from), color, createSubLightning, damage, subLightningLength, width, lightningNum, hitPointMovement);
	}

	public static void createRandom(Team team, Position from, float rand, Color color, boolean createSubLightning, float damage, int subLightningLength, float width, int lightningNum, Cons<Position> hitPointMovement) {
		createRandom(null, team, from, rand, color, createSubLightning, damage, subLightningLength, width, lightningNum, hitPointMovement);
	}

	public static void createRandomRange(Team team, Position from, float rand, Color color, boolean createSubLightning, float damage, int subLightningLength, float width, int lightningNum, int generateNum, Cons<Position> hitPointMovement) {
		createRandomRange(null, team, from, rand, color, createSubLightning, damage, subLightningLength, width, lightningNum, generateNum, hitPointMovement);
	}

	public static void createRandomRange(Bullet owner, float rand, Color color, boolean createSubLightning, float damage, float width, int lightningNum, int generateNum, Cons<Position> hitPointMovement) {
		createRandomRange(owner, owner.team, owner, rand, color, createSubLightning, damage, owner.type.lightningLength + Mathf.random(owner.type.lightningLengthRand), width, lightningNum, generateNum, hitPointMovement);
	}

	public static void createRandomRange(Bullet owner, Team team, Position from, float rand, Color color, boolean createSubLightning, float damage, int subLightningLength, float width, int lightningNum, int generateNum, Cons<Position> hitPointMovement) {
		for (int i = 0; i < generateNum; i++) {
			createRandom(owner, team, from, rand, color, createSubLightning, damage, subLightningLength, width, lightningNum, hitPointMovement);
		}
	}

	public static void createEffect(Position from, float length, float angle, Color color, int lightningNum, float width) {
		if (headless) return;
		createEffect(from, tmp2.trns(angle, length).add(from), color, lightningNum, width);
	}

	public static void createEffect(float fromX, float fromY, float toX, float toY, Color color, int lightningNum, float width) {
		if (headless) return;

		if (lightningNum < 1) {
			Fx.chainLightning.at(fromX, fromY, 0f, color, new Vec2().set(toX, toY));
		} else {
			float dst = Mathf.dst(fromX, fromY, toX, toY);

			for (int i = 0; i < lightningNum; i++) {
				float len = getBoltRandomRange();
				float randRange = len * RANGE_RAND;

				floatSeq.clear();

				for (int num = 0; num < dst / (ROT_DST * len) + 1; num++) {
					floatSeq.add(Mathf.range(randRange) / (num * 0.025f + 1));
				}
				createBoltEffect(color, width, computeVectors(floatSeq, fromX, fromY, toX, toY));
			}
		}
	}

	public static void createEffect(Position from, Position to, Color color, int lightningNum, float width) {
		if (headless) return;

		if (lightningNum < 1) {
			Fx.chainLightning.at(from.getX(), from.getY(), 0f, color, new Vec2().set(to));
		} else {
			float dst = from.dst(to);

			for (int i = 0; i < lightningNum; i++) {
				float len = getBoltRandomRange();
				float randRange = len * RANGE_RAND;

				floatSeq.clear();

				for (int num = 0; num < dst / (ROT_DST * len) + 1; num++) {
					floatSeq.add(Mathf.range(randRange) / (num * 0.025f + 1));
				}
				createBoltEffect(color, width, computeVectors(floatSeq, from, to));
			}
		}
	}

	/** Compute the proper hit position. */
	public static Position findInterceptedPoint(Position from, Position target, Team fromTeam) {
		furthest = null;

		return Geometry.raycast(
				World.toTile(from.getX()),
				World.toTile(from.getY()),
				World.toTile(target.getX()),
				World.toTile(target.getY()),
				(x, y) -> (furthest = world.build(x, y)) != null && furthest.team() != fromTeam && furthest.block.insulated
		) && furthest != null ? furthest : target;
	}

	/** Add proper unit into the to hit Seq. */
	public static void whetherAdd(Seq<Healthc> points, Team team, Rect selectRect, int maxHit, boolean targetGround, boolean targetAir) {
		Units.nearbyEnemies(team, selectRect, unit -> {
			if (unit.checkTarget(targetAir, targetGround)) points.add(unit);
		});

		if (targetGround) {
			selectRect.getCenter(tmp3);
			Units.nearbyBuildings(tmp3.x, tmp3.y, selectRect.getHeight() / 2, b -> {
				if (b.team != team && b.isValid()) points.add(b);
			});
		}

		points.shuffle();
		points.truncate(maxHit);
	}

	/** create lightning effect. */
	public static void createBoltEffect(Color color, float width, Vec2Seq vets) {
		vets.each(((x, y) -> {
			if (Mathf.chance(0.0855)) Fx2.lightningSpark.at(x, y, rand.random(2f + width, 4f + width), color);
		}));
		Fx2.posLightning.at((vets.firstTmp().x + vets.peekTmp().x) / 2f, (vets.firstTmp().y + vets.peekTmp().y) / 2f, width, color, vets);
	}

	public static Vec2Seq computeVectors(FloatSeq randomVec, float fromX, float fromY, float toX, float toY) {
		int param = randomVec.size;
		float angle = Angles.angle(fromX, fromY, toX, toY);

		Vec2Seq lines = new Vec2Seq(param);
		tmp1.trns(angle, Mathf.dst(fromX, fromY, toX, toY) / (param - 1));

		lines.add(fromX, fromY);
		for (int i = 1; i < param - 2; i++) {
			lines.add(tmp3.trns(angle - 90, randomVec.get(i)).add(tmp1, i).add(fromX, fromY));
		}
		lines.add(toX, toY);

		return lines;
	}

	public static Vec2Seq computeVectors(FloatSeq randomVec, Position from, Position to) {
		int param = randomVec.size;
		float angle = from.angleTo(to);

		Vec2Seq lines = new Vec2Seq(param);
		tmp1.trns(angle, from.dst(to) / (param - 1));

		lines.add(from);
		for (int i = 1; i < param - 2; i++) {
			lines.add(tmp3.trns(angle - 90, randomVec.get(i)).add(tmp1, i).add(from.getX(), from.getY()));
		}
		lines.add(to);

		return lines;
	}

	public static void reset() {
		furthest = null;

		entities.clear();
	}
}