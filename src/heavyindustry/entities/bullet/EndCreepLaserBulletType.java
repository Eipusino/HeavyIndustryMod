package heavyindustry.entities.bullet;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Intersector;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.content.HFx;
import heavyindustry.entities.HDamage;
import heavyindustry.graphics.Drawn;
import heavyindustry.graphics.HPal;
import heavyindustry.math.Mathm;
import heavyindustry.type.weapons.LaserWeapon.LaserRange;
import heavyindustry.util.Constant;
import heavyindustry.util.Utils;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.entities.bullet.ContinuousBulletType;
import mindustry.game.Teams.TeamData;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

public class EndCreepLaserBulletType extends ContinuousBulletType implements LaserRange {
	public float creepTime = 140f;
	public float baseLength = 300f;
	public float width = 12f;

	public Color[] colors = {HPal.red.cpy().a(0.5f), HPal.red, HPal.red.cpy().mul(2f), Color.white};

	static Seq<Building> buildings = new Seq<>(Building.class);

	public EndCreepLaserBulletType() {
		super();

		damage = 9000f;
		length = 500 * 8f;
		hitColor = HPal.red;
		hitEffect = HFx.desCreepHit;
		shootEffect = Fx.none;
		smokeEffect = Fx.none;

		lifetime = 5f * 60f;
	}

	@Override
	protected float calculateRange() {
		return length / 2f;
	}

	@Override
	public void init(Bullet b) {
		super.init(b);
		b.fdata = baseLength;
		CreepLaserData data = new CreepLaserData();

		Vec2 v = Utils.v.trns(b.rotation(), baseLength).add(b.x, b.y);
		HFx.desGroundHitMain.at(v.x, v.y, b.rotation());

		data.lastX = v.x;
		data.lastY = v.y;
		b.data = data;
	}

	@Override
	public void update(Bullet b) {
		super.update(b);

		if (!(b.data instanceof CreepLaserData data)) return;

		//float llen = baseLength / Math.max(1f - Mathm.clamp((b.time - Time.delta) / creepTime), baseLength / length);
		//float len = baseLength / Math.max(1f - Mathm.clamp(b.time / creepTime), baseLength / length);
		float len = getLength(b.time);
		float grnd = getGroundScl(b);
		float scl = (1f + Mathm.clamp(1f - (b.time / 16f)) * 1.1f) * grnd;

		if (Mathf.chanceDelta(0.75f) && grnd > 0) {
			Vec2 v = Utils.v.trns(b.rotation(), len).add(b.x, b.y).lerp(data.lastX, data.lastY, Mathf.random());

			Tile ground = Vars.world.tileWorld(v.x, v.y);
			if (ground != null) {
				Floor floor = ground.floor();
				Color color = Tmp.c1.set(floor.mapColor).mul(1.2f);
				if (floor.isLiquid && floor.liquidDrop == Liquids.water) {
					color = Liquids.water.gasColor;
				}

				HFx.desGroundHit.at(v.x, v.y, scl, color);
			}
		}

		Vec2 v = Utils.v.trns(b.rotation(), len).add(b.x, b.y);

		if (scl > 0) {
			float dst = Mathf.dst(b.x, b.y, data.lastX, data.lastY);
			int melt = Math.min((int) ((len - dst) / 5f) + 1, 15);
			for (int i = 0; i < melt; i++) {
				float f = i / (float) melt;

				Vec2 m = Tmp.v1.set(v).lerp(data.lastX, data.lastY, f).add(Tmp.v2.rnd(Mathf.random(5f * scl)));
				Tile tile = Vars.world.tileWorld(v.x, v.y);

				if (tile != null && !tile.floor().isLiquid) {
					HFx.desGroundMelt.at(m.x, m.y, Mathf.random(12f, 24f) * scl);
				}
			}
		}
		data.lastX = v.x;
		data.lastY = v.y;
		data.hitTime -= Time.delta;
	}

	float getGroundScl(Bullet b) {
		return 1f - Mathm.clamp((b.time - creepTime / 1.4f) / (creepTime / 4f));
	}

	float getFlyingScl(Bullet b) {
		return Mathm.clamp((b.time - creepTime / 1.7f) / (creepTime / 3f));
	}

	float getLength(float time) {
		//float len = baseLength / Math.max(1f - Mathm.clamp(b.time / creepTime), baseLength / length);
		return baseLength / Math.max(1f - Interp.sineIn.apply(Mathm.clamp(time / creepTime)), baseLength / length);
	}

	@Override
	public float getLength(Bullet b) {
		return getLength(b.time);
	}

	@Override
	public void applyDamage(Bullet b) {
		if (!(b.data instanceof CreepLaserData data)) return;

		float len = getLength(b.time);
		Vec2 v = Utils.v.trns(b.rotation(), len).add(b.x, b.y), v2 = Utils.vv.trns(b.rotation(), length + 100f).add(b.x, b.y);
		float vx = v.x, vy = v.y;
		//float v2x = v2.x, v2y = v2.y;
		float bx = b.x, by = b.y;
		float ground = getGroundScl(b);
		float fly = getFlyingScl(b);
		//float rot = b.rotation();

		HDamage.hitLaser(b.team, 4f, b.x, b.y, v.x, v.y, null, Constant.BOOLF_HEALTHC_FALSE, (h, x, y) -> {
			//hit(b, x, y);

			boolean near = (data.hitTime <= 0f) && Mathf.within(x, y, vx, vy, 140f + (len - b.fdata));
			boolean groundHit = false;
			boolean hitt = false;

			//firstHit = true;

			if (h instanceof Unit unit) {
				//boolean near2 = Mathf.within(x, y, vx, vy, 200f + (len - b.fdata));
				boolean near2 = Intersector.distanceLinePoint(bx, by, vx, vy, unit.x, unit.y) < (unit.hitSize / 2f) / 2.5f;
				float dam = unit.isFlying() ? fly : ground;

				if (dam > 0) {
					unit.health -= (damage + unit.maxHealth / 390f) * dam;
					hitt = true;
					if (unit.isGrounded()) groundHit = true;
				}
			} else if (h instanceof Building build) {
				if (ground > 0) {
					build.health -= (damage + build.maxHealth / 390f) * ground;
					hitt = true;
					if ((build.health / build.maxHealth) > 0.68f) groundHit = true;
					if (build.health <= 0f) build.kill();
				}
			} else {
				h.health(h.health() - (damage + h.maxHealth() / 390f));
			}

			if (hitt) hit(b, x, y);

			if (near && groundHit) {
				hitEnd(b, x, y);
				data.hitTime = Mathf.random(10f, 18f) * (0.3f + ground * 0.7f);
			}
		});

		b.fdata = len;
	}

	public void hitEnd(Bullet b, float x, float y) {
		float scl = Mathf.random(0.75f, 1.3f) * Interp.pow2InInverse.apply(getGroundScl(b));
		float range = 230f * scl;
		HFx.desCreepHeavyHit.at(x, y, b.rotation(), scl);

		Rect rect = Utils.r.setCentered(x, y, range * 2f);

		buildings.clear();
		Groups.unit.intersect(rect.x, rect.y, rect.width, rect.height, unit -> {
			if (unit.team != b.team && Mathf.within(x, y, unit.x, unit.y, range + unit.hitSize / 2f) && unit.checkTarget(false, true)) {
				unit.health -= 12000f + unit.maxHealth / 50f;
			}
		});

		//Team team = b.team;

		for (TeamData data : Vars.state.teams.present) {
			if (data.team != b.team && data.buildingTree != null) {
				data.buildingTree.intersect(rect, bl -> {
					if (Mathf.within(x, y, bl.x, bl.y, range + bl.hitSize() / 2f)) {
						buildings.add(bl);
					}
				});
			}
		}

		for (Building build : buildings) {
			boolean lethal = Mathf.chance(0.3f);
			float d = lethal ? 12000f + build.maxHealth / 20f : build.health / 1.5f;
			build.health -= d;
			if (build.health <= 0f) build.kill();
		}
		buildings.clear();
	}

	@Override
	public void draw(Bullet b) {
		Rand rand = Utils.rand(b.id), rand2 = Utils.rand2;

		float len = getLength(b.time);
		float fade = Mathm.clamp(b.time / 15f) * Mathm.clamp((b.lifetime - b.time) / (2.5f * 60));

		float base = baseLength;
		float tipHeight = (width / 2f) * (1f + ((len - base) / (length - base)) * 10f);
		float flare = Mathm.clamp(b.time / 80f);
		float w2 = 8f * fade;
		float sclen = (len - base) / (length - base);

		for (int i = 0; i < colors.length; i++) {
			float f = ((float) (colors.length - i) / colors.length);
			float w = f * (width + Mathf.absin(Time.time + (i * 0.6f), 1.1f, width / 4)) * fade;

			Tmp.v2.trns(b.rotation(), len - tipHeight).add(b);
			Tmp.v1.trns(b.rotation(), tipHeight * 4f).add(Tmp.v2);

			Draw.color(colors[i]);

			if (flare < 1f) {
				for (int j = 0; j < 4; j++) {
					float r = j * 90f + Time.time * 2.5f;
					Drawf.tri(b.x, b.y, w2, (40f + w2 * 8f) * Interp.sine.apply(Mathf.slope(flare)), r);
				}
			}

			Fill.circle(b.x, b.y, w / 1.25f);
			Lines.stroke(w);
			Lines.line(b.x, b.y, Tmp.v2.x, Tmp.v2.y, false);
			for (int s : Mathf.signs) {
				Tmp.v3.trns(b.rotation(), w * -0.7f, w * s);
				Fill.tri(Tmp.v2.x, Tmp.v2.y, Tmp.v1.x, Tmp.v1.y, Tmp.v2.x + Tmp.v3.x, Tmp.v2.y + Tmp.v3.y);
			}
			w2 *= 0.666f;
		}
		for (int i = 0; i < 80; i++) {
			float dur = rand.random(8f, 12f);
			float time = (b.time + rand.random(dur)) / dur;
			float f = time % 1f;
			int seed = (int) (time) + b.id * 5231;
			rand2.setSeed(seed);

			float l = (len / (rand2.random(8f, 12f) * (1f + sclen * 2f))) * (f * 0.25f + 0.75f);
			float w = (width / 4f) * Utils.biasSlope(0.3f, f) * fade;
			float off = ((len - l * 2f) * f) + l;
			Vec2 v = Tmp.v2.trns(b.rotation(), off, rand2.range(width * (1 - 1f / 4f) * fade - w) / 2f).add(b.x, b.y);

			Draw.color(rand2.chance(0.5f) ? Tmp.c2.set(HPal.red).lerp(Color.white, Mathf.pow(rand2.nextFloat(), 2f)) : Color.black);
			Drawn.diamond(v.x, v.y, w, l, b.rotation());
		}

		Tmp.v2.trns(b.rotation(), b.fdata + tipHeight).add(b);
		//Drawf.light(b.team, b.x, b.y, Tmp.v2.x, Tmp.v2.y, width * 2f, colors[0], 0.5f);
		Drawf.light(b.x, b.y, Tmp.v2.x, Tmp.v2.y, width * 2f, colors[0], 0.5f);

		Draw.reset();
	}

	public static class CreepLaserData {
		public float lastX, lastY;
		public float hitTime;
	}
}
