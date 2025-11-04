package heavyindustry.entities.bullet;

import arc.Core;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import heavyindustry.HVars;
import heavyindustry.audio.HSounds;
import heavyindustry.content.HFx;
import heavyindustry.entities.HEntity;
import heavyindustry.graphics.HPal;
import heavyindustry.math.Mathm;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Fires;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;

import static heavyindustry.HVars.MOD_NAME;

public class EndNukeBulletType extends BasicBulletType {
	public static int lastMax, lastUnit, lastBuilding;

	public EndNukeBulletType() {
		this(17f, 1000f, MOD_NAME + "-large-missile");
	}

	public EndNukeBulletType(float speed, float damage, String bulletSprite) {
		super(speed, damage, bulletSprite);

		splashDamage = 10000f;

		backColor = trailColor = hitColor = HPal.red;
		frontColor = HPal.red.cpy().mul(2f);

		shrinkY = 0f;
		width = 15f;
		height = 34f;

		trailLength = 5;
		trailWidth = 5f;

		lifetime = 180f;

		pierce = true;
		pierceArmor = true;

		despawnHit = true;
		collidesTiles = false;
		scaleLife = true;

		shieldDamageMultiplier += 1.75f;

		reflectable = false;
		hittable = false;
		absorbable = false;

		hitEffect = Fx.none;
		despawnEffect = Fx.none;

		shootEffect = HFx.desNukeShoot;
		smokeEffect = Fx.none;
	}

	@Override
	public void hit(Bullet b, float x, float y) {
		super.hit(b, x, y);

		float bx = b.x, by = b.y;
		Team team = b.team;

		int sid1 = HSounds.desNukeHit.at(bx, by, 1f, 2f);
		Core.audio.protect(sid1, true);
		float fall = Mathf.pow(Mathm.clamp(1f - HSounds.desNukeHit.calcFalloff(bx, by) * 1.1f), 1.5f);
		int sid2 = HSounds.desNukeHitFar.play(fall * 2f, 1f, HSounds.desNukeHit.calcPan(bx, by));
		Core.audio.protect(sid2, true);

		float[] arr = new float[360 * 3];
		HEntity.rayCastCircle(b.x, b.y, 480f, t -> t.build != null && t.build.team != team && !Mathf.within(b.x, b.y, t.worldx(), t.worldy(), 150f), t -> {
			float dst = 1f - Mathm.clamp(Mathf.dst(bx, by, t.x * Vars.tilesize, t.y * Vars.tilesize) / 480f);
			if (Mathf.chance(Mathf.pow(dst, 2f) * 0.75f)) Fires.create(t);
		}, t -> {
			float nx = t.x * Vars.tilesize, ny = t.y * Vars.tilesize;
			float ang = Angles.angle(bx, by, nx, ny);

			HFx.desNukeShockSmoke.at(nx, ny, ang);
		}, bl -> {
			//float d = lethal ? 12000f + bl.maxHealth / 20f : bl.health / 1.5f;
			float d = 21000f + bl.maxHealth / 5f;

			bl.health -= d;
			if (bl.health <= 0f) bl.kill();
		}, arr);

		lastMax = Vars.headless ? -1 : Core.settings.getInt("hi-vaporize-batch", 100);

		HEntity.scanEnemies(b.team, b.x, b.y, 480f, true, true, t -> {
			if (t instanceof Unit unit && unit.hittable()) {
				//float damageScl = 1f;
				//if (unit.isGrounded()) damageScl = HEntity.inRayCastCircle(bx, by, arr, unit);
				float damageScl = HEntity.inRayCastCircle(bx, by, arr, unit);

				if (damageScl > 0f) {
					Tmp.v2.trns(Angles.angle(bx, by, unit.x, unit.y), (16f + 5f / unit.mass()) * damageScl);
					unit.vel.add(Tmp.v2);

					unit.health -= (unit.maxHealth / 10f + splashDamage) * damageScl;

					if (lastUnit < lastMax && unit.health <= 0f) {
						HVars.vaporBatch.discon = null;
						HVars.vaporBatch.switchBatch(unit::draw, null, (d, w) -> {
							float with = HEntity.inRayCastCircle(bx, by, arr, d);
							if (with > 0.5f) {
								d.disintegrating = true;
								float dx = d.x - bx, dy = d.y - by;
								float len = Mathf.sqrt(dx * dx + dy * dy);
								float force = (6f / (1f + len / 90f) + (len / 480f) * 1.01f);

								Vec2 v = Tmp.v1.set(dx, dy).nor().setLength(force * Mathf.random(0.9f, 1f));

								d.lifetime = Mathf.random(60f, 90f) * Mathf.lerp(1f, 0.5f, Mathm.clamp(len / 480f));
								d.drag = -0.015f;

								d.vx = v.x;
								d.vy = v.y;
								d.vr = Mathf.range((force / 3f) * 5f);
								d.zOverride = Layer.flyingUnit;
							}
						});

						HFx.desNukeVaporize.at(unit.x, unit.y, unit.angleTo(bx, by) + 180f, unit.hitSize / 2f);

						lastUnit++;
					}
				}
			} else if (t instanceof Building build) {
				float damageScl = HEntity.inRayCastCircle(bx, by, arr, build);
				if (damageScl > 0) {
					build.health -= (build.maxHealth / 10f + splashDamage) * damageScl;
					if (build.health <= 0f) {
						if (lastBuilding < lastMax && t.within(bx, by, 150f + build.hitSize() / 2f)) {
							HVars.vaporBatch.discon = null;
							HVars.vaporBatch.switchBatch(build::draw, null, (d, w) -> {
								d.disintegrating = true;
								float dx = d.x - bx, dy = d.y - by;
								float len = Mathf.sqrt(dx * dx + dy * dy);
								//float force = Math.max(10f / (1f + len / 50f), (len / 150f) * 3f);
								float force = (3f / (1f + len / 50f) + (len / 150f) * 0.9f);
								//float force = (len / 150f) * 15f;

								Vec2 v = Tmp.v1.set(dx, dy).nor().setLength(force * Mathf.random(0.9f, 1f));

								d.lifetime = Mathf.random(60f, 90f) * Mathf.lerp(1f, 0.5f, Mathm.clamp(len / 150f));
								d.drag = -0.03f;

								d.vx = v.x;
								d.vy = v.y;
								d.vr = Mathf.range((force / 3f) * 5f);
								d.zOverride = Layer.turret + 1f;
							});
							HFx.desNukeVaporize.at(build.x, build.y, build.angleTo(bx, by) + 180f, build.hitSize() / 2f);

							lastBuilding++;
						}

						build.kill();
					}
				}
			} else if (t instanceof Healthc h) {
				h.health(h.health() - (h.maxHealth() / 10f + splashDamage));
			}
		});

		Effect.shake(60f, 120f, b.x, b.y);
		HFx.desNukeShockwave.at(b.x, b.y, 480f);
		HFx.desNuke.at(b.x, b.y, 479f, arr);

		lastBuilding = lastUnit = 0;
	}
}
