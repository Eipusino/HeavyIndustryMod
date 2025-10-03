package heavyindustry.entities.bullet;

import arc.Core;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import heavyindustry.HVars;
import heavyindustry.content.HFx;
import heavyindustry.entities.HEntity;
import heavyindustry.gen.HSounds;
import heavyindustry.graphics.HPal;
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

public class EndNukeBulletType extends BasicBulletType {
	public static int lastUnit, lastBuilding;

	public static volatile int lastMax;

	public EndNukeBulletType() {
		super(17f, 50000f, "missile-large");

		backColor = trailColor = hitColor = HPal.red;
		frontColor = HPal.red.cpy().mul(2f);

		shrinkY = 0f;
		width = 15f;
		height = 34f;

		trailLength = 5;
		trailWidth = 5f;

		lifetime = 60f;

		despawnHit = true;
		collidesTiles = false;
		scaleLife = true;

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
		float fall = Mathf.pow(Mathf.clamp(1f - HSounds.desNukeHit.calcFalloff(bx, by) * 1.1f), 1.5f);
		int sid2 = HSounds.desNukeHitFar.play(fall * 2f, 1f, HSounds.desNukeHit.calcPan(bx, by));
		Core.audio.protect(sid2, true);

		float[] arr = new float[360 * 3];
		HEntity.rayCastCircle(b.x, b.y, 480f, t -> t.build != null && t.build.team != team && !Mathf.within(b.x, b.y, t.worldx(), t.worldy(), 150f), t -> {
			float dst = 1f - Mathf.clamp(Mathf.dst(bx, by, t.x * Vars.tilesize, t.y * Vars.tilesize) / 480f);
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
			if (t instanceof Unit u) {
				//float damageScl = 1f;
				//if (u.isGrounded()) damageScl = HEntity.inRayCastCircle(bx, by, arr, u);
				float damageScl = HEntity.inRayCastCircle(bx, by, arr, u);

				if (damageScl > 0) {
					Tmp.v2.trns(Angles.angle(bx, by, u.x, u.y), (16f + 5f / u.mass()) * damageScl);
					u.vel.add(Tmp.v2);

					u.health -= (u.maxHealth / 10f + 10000f) * damageScl;

					if (lastUnit < lastMax && u.health <= 0f) {
						HVars.vaporBatch.discon = null;
						HVars.vaporBatch.switchBatch(u::draw, null, (d, w) -> {
							float with = HEntity.inRayCastCircle(bx, by, arr, d);
							if (with > 0.5f) {
								d.disintegrating = true;
								float dx = d.x - bx, dy = d.y - by;
								float len = Mathf.sqrt(dx * dx + dy * dy);
								float force = (6f / (1f + len / 90f) + (len / 480f) * 1.01f);

								Vec2 v = Tmp.v1.set(dx, dy).nor().setLength(force * Mathf.random(0.9f, 1f));

								d.lifetime = Mathf.random(60f, 90f) * Mathf.lerp(1f, 0.5f, Mathf.clamp(len / 480f));
								d.drag = -0.015f;

								d.vx = v.x;
								d.vy = v.y;
								d.vr = Mathf.range((force / 3f) * 5f);
								d.zOverride = Layer.flyingUnit;
							}
						});

						HFx.desNukeVaporize.at(u.x, u.y, u.angleTo(bx, by) + 180f, u.hitSize / 2f);

						lastUnit++;
					}
				}
			} else if (t instanceof Building bl) {
				float damageScl = HEntity.inRayCastCircle(bx, by, arr, bl);
				if (damageScl > 0) {
					bl.health -= (bl.maxHealth / 10f + 10000f) * damageScl;
					if (bl.health <= 0f) {
						if (lastBuilding < lastMax && t.within(bx, by, 150f + bl.hitSize() / 2f)) {
							HVars.vaporBatch.discon = null;
							HVars.vaporBatch.switchBatch(bl::draw, null, (d, w) -> {
								d.disintegrating = true;
								float dx = d.x - bx, dy = d.y - by;
								float len = Mathf.sqrt(dx * dx + dy * dy);
								//float force = Math.max(10f / (1f + len / 50f), (len / 150f) * 3f);
								float force = (3f / (1f + len / 50f) + (len / 150f) * 0.9f);
								//float force = (len / 150f) * 15f;

								Vec2 v = Tmp.v1.set(dx, dy).nor().setLength(force * Mathf.random(0.9f, 1f));

								d.lifetime = Mathf.random(60f, 90f) * Mathf.lerp(1f, 0.5f, Mathf.clamp(len / 150f));
								d.drag = -0.03f;

								d.vx = v.x;
								d.vy = v.y;
								d.vr = Mathf.range((force / 3f) * 5f);
								d.zOverride = Layer.turret + 1f;
							});
							//HFx.desNukeVaporize.at(u.x, u.y, u.angleTo(bx, by) + 180f, u.hitSize / 2f);
							HFx.desNukeVaporize.at(bl.x, bl.y, bl.angleTo(bx, by) + 180f, bl.hitSize() / 2f);

							lastBuilding++;
						}

						bl.kill();
					}
				}
			} else if (t instanceof Healthc h) {
				h.health(h.health() - (h.maxHealth() / 10f + 10000f));
			}
		});

		Effect.shake(60f, 120f, b.x, b.y);
		HFx.desNukeShockwave.at(b.x, b.y, 480f);
		HFx.desNuke.at(b.x, b.y, 479f, arr);

		lastBuilding = lastUnit = 0;

		/*HVars.listener.impactFrames(bx, by, b.rotation(), 23f, false, () -> {
			for (int i = 0; i < arr.length; i++) {
				float len1 = arr[i], len2 = arr[(i + 1) % arr.length];
				float ang1 = (i / (float)arr.length) * 360f;
				float ang2 = ((i + 1f) / arr.length) * 360f;

				float x1 = Mathf.cosDeg(ang1) * len1, y1 = Mathf.sinDeg(ang1) * len1;
				float x2 = Mathf.cosDeg(ang2) * len2, y2 = Mathf.sinDeg(ang2) * len2;

				Fill.tri(bx, by, bx + x1, by + y1, bx + x2, by + y2);
			}
		});*/
	}
}
