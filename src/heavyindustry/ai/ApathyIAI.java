package heavyindustry.ai;

import arc.Core;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.util.Interval;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.content.HBullets;
import heavyindustry.content.HFx;
import heavyindustry.entities.HEntity;
import heavyindustry.entities.bullet.ApathySmallLaserBulletType;
import heavyindustry.entities.bullet.ApathySmallLaserBulletType.ApathyData;
import heavyindustry.gen.ApathyIUnit;
import heavyindustry.gen.HSounds;
import heavyindustry.type.unit.ApathyUnitType;
import heavyindustry.util.Utils;
import mindustry.Vars;
import mindustry.ai.types.MissileAI;
import mindustry.audio.SoundLoop;
import mindustry.entities.Sized;
import mindustry.entities.Units;
import mindustry.game.Teams.TeamData;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;
import mindustry.gen.Posc;
import mindustry.gen.Teamc;
import mindustry.gen.TimedKillUnit;
import mindustry.gen.Unit;
import mindustry.world.blocks.defense.turrets.PowerTurret;
import mindustry.world.blocks.defense.turrets.Turret.AmmoEntry;
import mindustry.world.blocks.defense.turrets.Turret.TurretBuild;
import mindustry.world.blocks.storage.CoreBlock.CoreBuild;

import java.util.Arrays;

public class ApathyIAI extends NullAI {
	protected static final Vec2 vec2 = new Vec2();

	public ApathyIUnit apathy;
	public ApathyUnitType apathyType;
	public Interval scanTimer = new Interval(2);

	public int currentTransformation = 0;

	//IntSeq nextTransformations = new IntSeq();
	public float transformationTime = 2f * 60f;
	public float transformationSpeed = 1f;

	public float critDamage = 0f;
	public float critStun = 0f;
	public float maxCritDamage = 0f;

	public float strongLaserScore = 0f;

	public float[] directionalBias = new float[32];
	public float[] vision = new float[40];

	public float[] shiftScore = new float[5];

	public int[] shiftUses = new int[5];
	public float[] shiftUseTimes = new float[5];

	public float bulletCount = 0f;
	public float flyingCount = 0f;
	public float groundCount = 0f;

	public boolean init = false;
	public boolean unchanged = false;

	public float strongestValue = 0f;
	public Teamc strongest, nearest;
	public Teamc nearestCore;

	public float noEnemyTime = 0f;

	public Bullet laser;
	//protected SoundLoop sound;
	public SoundLoop[] sounds;
	public boolean[] soundPlaying;
	public float data1 = 0f, data2 = 0f;

	public float stressScaled = 0f;

	public float laserX = Float.NaN;
	public float laserY = Float.NaN;

	public float distance = 0f;

	public static float scanRange = 1100f;
	public static float shiftUseTime = 15f * 60f;
	public static Rect tr = new Rect();
	public static boolean updateScore = true;

	public static Vision vis = new Vision();

	public static Teamc st = null, nt = null;
	public static float sts = 0f, nts = 0f;
	public static boolean found = false;

	public ApathyIAI() {
		initSounds();
	}

	protected void initSounds() {
		sounds = new SoundLoop[]{new SoundLoop(HSounds.laserSmall, 2f), new SoundLoop(HSounds.laserBig, 2.5f)};
		soundPlaying = new boolean[2];
	}

	@Override
	public void updateUnit() {
		if (apathy.dead) {
			if (laser != null) {
				laser.time = Math.max(laser.lifetime - 48f, laser.time);
			}
			for (SoundLoop s : sounds) {
				s.stop();
			}
			return;
		}
		stressScaled = apathy.getStressScaled();

		for (int i = 0; i < shiftUseTimes.length; i++) {
			shiftUseTimes[i] = Math.max(0, shiftUseTimes[i] - Time.delta);
			if (shiftUses[i] > 0 && shiftUseTimes[i] <= 0f) {
				shiftUses[i] = 0;
			}
		}

		if (scanTimer.get(0, 5f)) {
			updateAI();

			//shiftScore[0] = 100f;
			//shiftScore[4] = 100f;
			//shiftScore[1] = 100f;
		}

		critDamage = Math.max(0, critDamage - (maxCritDamage / 180) * Time.delta);
		if (critDamage <= 0f) {
			maxCritDamage = 0f;
		}

		Arrays.fill(soundPlaying, false);

		if (!apathy.shifting) {
			boolean f = true;
			if (transformationTime <= 0f) {
				float lc = currentTransformation;
				updateSwitchForm();
				//critDamage = 0f;
				strongLaserScore = 0f;

				f = currentTransformation == lc;
			}
			if (transformationTime > 0f && f) {
				float ttspeed = apathyType.handlers.get(currentTransformation).stressAffected ? (1 + stressScaled / 2f) : 1f;
				transformationTime -= Time.delta * ttspeed * transformationSpeed;
				transformationSpeed = 1f;

				if (init) {
					init = false;
					switch (currentTransformation) {
						case 1 -> initSmallLaser();
						case 2 -> initAoE();
						case 3 -> initSweep();
						case 4 -> initLargeLaser();
					}
				}
				switch (currentTransformation) {
					case 0 -> updateBase();
					case 1 -> updateSmallLaser();
					case 2 -> updateAoE();
					case 3 -> updateSweep();
					case 4 -> updateLargeLaser();
				}
			}
		}

		for (int i = 0; i < sounds.length; i++) {
			if (!soundPlaying[i]) {
				if (Float.isNaN(laserX) || Float.isNaN(laserY)) {
					sounds[i].update(apathy.x, apathy.y, false);
				} else {
					sounds[i].update(laserX, laserY, false);
				}
			}
		}

		if (critStun > 0) critStun -= Time.delta;

		if (currentTransformation == 0) {
			distance += apathy.deltaLen();
			if (distance >= 128f) {
				HSounds.idle.at(apathy.x, apathy.y, 1f);
				distance = 0f;
			}
		}

		//if (currentTransformation == 0) moveTo();
	}

	public void updateBase() {
		moveTo();
	}

	public void initLargeLaser() {
		laser = null;
		data1 = 0f;
		data2 = 0f;
	}

	public void updateLargeLaser() {
		if (strongest != null) {
			float ato = apathy.angleTo(strongest);
			//float adst = laser == null ? Math.max(Angles.angleDist(unit.rotation, ato) / 5f, 3f) : 0.05f;
			float adst = laser == null ? Math.max(Angles.angleDist(apathy.rotation, ato) / 5f, 3f) : (laser.time < 140f ? 0.05f : 0.005f);

			apathy.rotation = Angles.moveToward(apathy.rotation, ato, adst * Time.delta * (1 + stressScaled / 3f));

			if (Angles.within(apathy.rotation, ato, 0.1f) && data2 <= 0f && laser == null) {
				data2 = HFx.bigLaserCharge.lifetime;
				HSounds.bigCharge.at(apathy.x, apathy.y, 1f);
				transformationTime = Math.max(transformationTime, data2 * 3f);
				HFx.bigLaserCharge.at(apathy.x, apathy.y, apathy.rotation, apathy);
			}
		}
		if (transformationTime <= 0f) {
			if (laser != null && laser.type != null && laser.time < laser.type.lifetime)
				laser.time = laser.type.lifetime - 80;
		}
		if (laser != null) {
			if (!laser.isAdded() || laser.owner != apathy || laser.type != HBullets.bigLaser) {
				laser = null;
				transformationTime = 0f;
			} else {
				Vec2 v = Utils.v.trns(apathy.rotation, 40f).add(apathy);
				laser.set(v.x, v.y);
				laser.rotation(apathy.rotation);
				laser.damage = laser.type.damage * (1 + stressScaled / 4f);
				transformationSpeed = 0f;

				if (!Vars.headless) {
					Vec2 cpos = Core.camera.position;
					Vec2 v2 = Tmp.v1.trns(laser.rotation(), 1800f);
					Vec2 iv = Intersector.nearestSegmentPoint(v.x, v.y, v.x + v2.x, v.y + v2.y, cpos.x, cpos.y, Tmp.v2);
					int idx = laser.time < 140 ? 0 : 1;

					laserX = iv.x;
					laserY = iv.y;

					sounds[idx].update(laserX, laserY, true);
					soundPlaying[idx] = true;

					if (data1 <= 0f && laser.time >= 140) {
						data1 = 1f;
						HSounds.laserCharge.at(laserX, laserY);
					}
				}
			}
		}
		if (data2 > 0 && laser == null) {
			data2 -= Time.delta;
			if (data2 <= 0f) {
				Vec2 v = Utils.v.trns(apathy.rotation, 40f).add(apathy);
				laser = HBullets.bigLaser.create(apathy, apathy.team, v.x, v.y, apathy.rotation);
				HFx.bigLaserFlash.at(apathy.x, apathy.y, apathy.rotation);
				HSounds.laserCharge.at(apathy.x, apathy.y, 2f);
			}
		}
	}

	public void initSmallLaser() {
		if (unchanged) return;
		//Vec2 v = Utils.v.trns(unit.rotation, 40f).add(unit);
		//laser = HBullets.smallLaser.create(unit, unit.team, v.x, v.y, unit.rotation);
		laser = null;
		data1 = 20f;
		data2 = 0f;
	}

	public void updateSmallLaser() {
		if (strongest != null) {
			float ato = apathy.angleTo(strongest);
			float adst = laser == null ? Math.max(Angles.angleDist(apathy.rotation, ato) / 5f, 3f) : 0.25f;
			apathy.rotation = Angles.moveToward(apathy.rotation, ato, adst * Time.delta * (1 + stressScaled / 3f));
			if (Angles.within(apathy.rotation, ato, 0.1f) && data2 <= 0f && laser == null) {
				data2 = 20f;
			}
			if (Angles.within(apathy.rotation, ato, 40f)) {
				data1 = 30f;
			}
		} else if (data1 <= 0f) {
			transformationTime = 0f;
		}
		if (laser != null) {
			if (laser.type == HBullets.smallLaser) {
				Vec2 v = Utils.v.trns(apathy.rotation, 40f).add(apathy);
				laser.set(v.x, v.y);
				laser.rotation(apathy.rotation);
				laser.damage = laser.type.damage * (1 + stressScaled / 4f);

				if (Units.invalidateTarget(strongest, apathy.team, apathy.x, apathy.y) || data1 >= 10f) {
					laser.time = Math.min(laser.time, ApathySmallLaserBulletType.inEnd);
					transformationSpeed = 0.5f;
				}

				if (!Vars.headless) {
					Vec2 cpos = Core.camera.position;
					Vec2 v2 = Tmp.v1.trns(laser.rotation(), 1300f);
					Vec2 iv = Intersector.nearestSegmentPoint(v.x, v.y, v.x + v2.x, v.y + v2.y, cpos.x, cpos.y, Tmp.v2);

					laserX = iv.x;
					laserY = iv.y;

					sounds[0].update(laserX, laserY, true);
					soundPlaying[0] = true;
				}
			}

			if (!laser.isAdded() || laser.owner != apathy || laser.type != HBullets.smallLaser) {
				laser = null;
				transformationTime = 0f;
			}
		}
		if (laser == null && data2 > 0f) {
			data2 -= Time.delta;
			if (data2 <= 0f) {
				Vec2 v = Utils.v.trns(apathy.rotation, 40f).add(apathy);
				laser = HBullets.smallLaser.create(apathy, apathy.team, v.x, v.y, apathy.rotation);
				ApathyData d = new ApathyData();
				d.ai = this;
				laser.data = d;
				//HSounds.laserCharge.at(v.x, v.y);
				HSounds.laserCharge.play(1f, 1f, HSounds.laserCharge.calcPan(v.x, v.y));
				HFx.bigLaserFlash.at(v.x, v.y, apathy.rotation);
			}
		}
		if (data1 >= 0f) data1 -= Time.delta;
	}

	public void initSweep() {
		if (laser != null && unchanged) return;
		data1 = Mathf.random(360f);
		//data1 = unit.angleTo(strongest);
		data2 = 0f;

		laser = HBullets.sweep.create(apathy, apathy.team, apathy.x, apathy.y, data1);
		HSounds.laserCharge.play(0.5f, 1.5f, HSounds.laserCharge.calcPan(apathy.x, apathy.y));
		//laser.lifetime = HBullets.sweep.lifetime / (1 + stressScaled / 3f);
		laser.fdata = data1;
	}

	public void updateSweep() {
		//
		if (laser != null) {
			if (laser.type == HBullets.sweep) {
				float rotation = data1 + (360f + 45f) * Interp.pow2.apply(Interp.pow2In.apply(data2 / laser.lifetime));
				laser.set(apathy);
				laser.rotation(rotation);
				laser.team = apathy.team;

				data2 = Math.min(data2 + Time.delta * (1 + stressScaled / 3f), laser.lifetime);
				transformationSpeed = 0f;

				if (!Vars.headless) {
					Vec2 cpos = Core.camera.position;
					Vec2 v2 = Tmp.v1.trns(laser.rotation(), 1300f);
					Vec2 iv = Intersector.nearestSegmentPoint(apathy.x, apathy.y, apathy.x + v2.x, apathy.y + v2.y, cpos.x, cpos.y, Tmp.v2);

					laserX = iv.x;
					laserY = iv.y;

					sounds[0].update(laserX, laserY, true);
					soundPlaying[0] = true;
				}
			}

			if (!laser.isAdded() || laser.type != HBullets.sweep) {
				laser = null;
				transformationTime = 0f;
			}
		} else {
			transformationTime = 0f;
		}
	}

	public void initAoE() {
		if (unchanged) return;
		data1 = 0f;
		data2 = 0f;
		vec2.set(0f, 0f);
	}

	public void updateAoE() {
		Vision t = getVisionAngle();

		if (nearest != null) {
			float dst = apathy.dst(nearest);
			if (dst < 250f) {
				//
				Vec2 vec = Tmp.v1;
				vec.set(nearest).sub(apathy);

				float length = Mathf.clamp((dst - 250f) / 100f, -1f, 0f);

				vec.setLength(apathy.speed() * (1.25f + stressScaled / 4f));
				vec.scl(length);

				if (!(vec.isNaN() || vec.isInfinite() || vec.isZero())) {
					apathy.movePref(vec);
				}
			}
		}

		if ((t.score > 10f || data2 <= 0f) && t.idx != -1) {
			float angle = t.angle;
			if (scanTimer.get(1, 5f)) {
				vec2.set(0f, 0f);
				Vec2 v = Utils.v.trns(angle, scanRange).add(apathy);

				found = false;
				for (TeamData data : Vars.state.teams.present) {
					if (data.team != apathy.team) {
						//found = false;
						if (data.unitTree != null) {
							HEntity.intersectLine(data.unitTree, 250f, apathy.x, apathy.y, v.x, v.y, (u, x, y) -> {
								if (u.isGrounded()) {
									vec2.x += (u.x - apathy.x);
									vec2.y += (u.y - apathy.y);
									found = true;
								}
							});
						}
						if (data.turretTree != null) {
							HEntity.intersectLine(data.turretTree, 250f, apathy.x, apathy.y, v.x, v.y, (tr, x, y) -> {
								vec2.x += (tr.x - apathy.x);
								vec2.y += (tr.y - apathy.y);
								found = true;
							});
						}
						if (!found && data.buildingTree != null) {
							HEntity.intersectLine(data.buildingTree, 250f, apathy.x, apathy.y, v.x, v.y, (b, x, y) -> {
								vec2.x += (b.x - apathy.x) / 25f;
								vec2.y += (b.y - apathy.y) / 25f;
							});
						}
					}
				}
			}
			if (!vec2.isZero()) {
				apathy.rotation = Angles.moveToward(apathy.rotation, vec2.angle(), 15f * Time.delta * (1 + stressScaled));
				//
				if (Angles.within(apathy.rotation, vec2.angle(), 0.25f) && data1 <= 0f) {
					//
					Vec2 v = Utils.v.trns(vec2.angle(), 50f).add(apathy);
					HBullets.aoe.create(apathy, v.x, v.y, vec2.angle());
					HSounds.aoeShoot.play(1f, Mathf.random(0.9f, 1.1f), HSounds.apathyDeath.calcPan(v.x, v.y));
					data1 = 90f / (1f + stressScaled / 1.5f);
					data2++;

					vision[t.idx] = 0f;

					transformationTime = Math.max(transformationTime, 2.666f * 60f);
				}
			}
			if (data1 > 0) data1 -= Time.delta;
		} else {
			transformationTime = 0f;
		}
	}

	public void updateSwitchForm() {
		int maxIdx = -1;
		float maxScore = -99999f;
		laser = null;

		float bias = 0f;

		for (float v : shiftScore) {
			bias = Math.max(v, bias);
		}
		for (int i = 0; i < shiftScore.length; i++) {
			if ((shiftScore[i] > maxScore) && shiftScore[i] > bias / 10f && i != currentTransformation && (shiftUses[i] < 2)) {
				maxIdx = i;
				maxScore = shiftScore[i];
			}
		}
		int lc = currentTransformation;
		if (maxIdx != -1) {
			transformationTime = maxIdx != 0 ? 5f * 60f : 2f * 50f;
			currentTransformation = maxIdx;

			if (maxIdx != 0 && maxIdx != 4) {
				shiftUses[maxIdx]++;
				shiftUseTimes[maxIdx] = shiftUseTime;
			}
			if (lc != 0 && maxIdx == 0) {
				//Arrays.fill(shiftUses, 0);
				//Arrays.fill(shiftUseTimes, 0);
				for (int i = 0; i < shiftUses.length; i++) {
					//shiftUses[i] /= 2;
					shiftUseTimes[i] /= 2;
				}
			}

			apathy.switchShift(apathyType.handlers.get(currentTransformation));
			//init = true;
			//data1 = Mathf.random(360f);
		} else {
			transformationTime = 2f * 60f;
		}
		unchanged = lc == currentTransformation;
		init = true;
	}

	public void updateAI() {
		bulletCount = flyingCount = groundCount = 0f;
		Arrays.fill(directionalBias, 0);
		Arrays.fill(vision, 0);

		float srad = apathy.getShieldRadius();
		int dbl = directionalBias.length;
		int vl = vision.length;
		Rect r = tr.setCentered(apathy.x, apathy.y, scanRange * 2f);

		Groups.bullet.intersect(r.x, r.y, r.width, r.height, b -> {
			float dst = apathy.dst(b);
			if (dst < scanRange && b.team != apathy.team) {
				float dps = b.type.estimateDPS();

				int angIdx = (int) Mathf.mod(apathy.angleTo(b) / (360f / dbl), dbl);
				directionalBias[angIdx] += dps;

				bulletCount += dps;

				if (apathy.shieldStun <= 0f && dst < srad && !b.vel.isZero()) {
					//
					if (b.vel.len() < 8f) {
						b.hit = true;
						b.remove();
					} else {
						float angC = (((apathy.angleTo(b) + 90f) * 2f) - b.rotation()) + Mathf.range(5f);
						b.rotation(angC);
						b.vel.scl(0.75f);
						b.team = apathy.team;

						if (b.owner instanceof Sized s && b.owner instanceof Posc p) {
							b.aimX = p.x() + Mathf.range(s.hitSize() / 4f);
							b.aimY = p.y() + Mathf.range(s.hitSize() / 4f);
						}
					}
					HFx.shield.at(b.x, b.y, b.angleTo(apathy));

					apathy.shieldHealth -= Math.min(dps / 2f, 500f);
					if (apathy.shieldHealth <= 0f) {
						apathy.shieldStun = 4f * 60f;
					}

					apathy.stress += dps / 750f;
				} else {
					apathy.stress += dps * b.vel.len() / 1000f;
				}
			}
		});
		st = null;
		sts = 0f;

		nt = null;
		nts = 0f;

		for (TeamData td : Vars.state.teams.present) {
			if (td.team != apathy.team) {
				if (td.unitTree != null) {
					td.unitTree.intersect(r.x, r.y, r.width, r.height, e -> {
						float dst = apathy.dst(e);
						if (dst - e.hitSize / 2 < scanRange) {
							float dps = e.type.estimateDps();
							float angleTo = apathy.angleTo(e);

							int angIdx = (int) Mathf.mod(angleTo / (360f / dbl), dbl);
							directionalBias[angIdx] += dps;

							if (!(e instanceof TimedKillUnit || e.controller() instanceof MissileAI)) {
								//if (!(e instanceof TimedKillUnit || (dst < srad && ((e.type.weapons.size > 0 && (e.type.weapons.get(0).shootOnDeath && (e.type.speed <= 0.001f || e.type.speed > 5f))) || e.controller() instanceof MissileAI))))
								dps += e.maxHealth / 500f;
								noEnemyTime = 0f;

								float sscr = dps - dst / 1500f;

								if (st == null || sscr > sts) {
									st = e;
									sts = sscr;
								}
								if (nt == null || dst < nts) {
									nt = e;
									nts = dst;
								}

								if (e.isGrounded()) {
									groundCount += dps;
									//handle groups
									int vidx = (int) Mathf.mod(angleTo / (360f / vl), vl);
									vision[vidx] += dps - dst / 2000f;
								} else {
									flyingCount += dps;
								}
								apathy.stress += dps / 1100f;
							} else {
								bulletCount += dps;
								if (apathy.shieldStun <= 0f && dst < srad) {
									//
									if (e.vel.len() < 6f) {
										//e.hit = false;
										e.type.deathExplosionEffect.at(e.x, e.y);
										e.type.deathSound.at(e.x, e.y);
										e.remove();
									} else {
										float angC = (((apathy.angleTo(e) + 90f) * 2f) - e.rotation()) + Mathf.range(5f);
										e.rotation(angC);
										e.vel.scl(0.75f);
										e.team = apathy.team;
									}
									HFx.shield.at(e.x, e.y, e.angleTo(apathy));

									apathy.shieldHealth -= Math.min(dps / 2f, 500f);

									if (apathy.shieldHealth <= 0f) {
										apathy.shieldStun = 4f * 60f;
									}

									apathy.stress += dps / 750f;
								}
							}
						}
					});
				}
				if (td.turretTree != null) {
					td.turretTree.intersect(r.x, r.y, r.width, r.height, e -> {
						if (!(e instanceof TurretBuild t)) return;
						float dst = apathy.dst(e);
						if (dst - e.hitSize() / 2 < scanRange) {
							noEnemyTime = 0f;
							float dps = 0;
							for (AmmoEntry ammo : t.ammo) {
								//
								dps = Math.max(ammo.type().estimateDPS(), dps);
							}
							if (e.block instanceof PowerTurret pt) {
								dps = Math.max(pt.shootType.estimateDPS(), dps);
							}

							dps += e.health / 500f;
							groundCount += dps;

							float angleTo = apathy.angleTo(e);

							int angIdx = (int) Mathf.mod(angleTo / (360f / dbl), dbl);
							directionalBias[angIdx] += dps;

							int vidx = (int) Mathf.mod(angleTo / (360f / vl), vl);
							vision[vidx] += dps - dst / 2000f;

							float sscr = dps - dst / 1500f;

							if (st == null || sscr > sts) {
								st = e;
								sts = sscr;
							}
							if (nt == null || dst < nts) {
								nt = e;
								nts = dst;
							}
						}
					});
				}
			}

			/*nextTransformation = 0;
			if (isSurrounded()) {
				if (Math.max(bulletCount, flyingCount) > groundCount) {
					nextTransformation = 3;
				} else {
					nextTransformation = 2;
				}
			} else {
				if (unit.health > unit.maxHealth / 3f) {
					nextTransformation = 1;
				} else {
					nextTransformation = 4;
				}
			}*/
		}
		noEnemyTime += Time.delta;
		if (noEnemyTime >= 2.75f * 60f && strongest == null) {
			//st = null;
			//sts = 0f;

			for (TeamData td : Vars.state.teams.active) {
				if (td.team != apathy.team) {
					for (Unit u : td.units) {
						if (u.dead) continue;
						float dst = apathy.dst(u);
						float dps = u.type.estimateDps() + u.health / 500f;
						float sscr = dps - dst / 1500f;

						if (st == null || sscr > sts) {
							st = u;
							sts = sscr;
						}
					}
				}
				if (td.buildings.size > 30) {
					int size = Math.max(20, td.buildings.size / 8);
					int count = 0;
					for (int i = 0; i < size; i++) {
						int idx = Mathf.random(td.buildings.size - 1);
						Building b = td.buildings.get(idx);

						if (b instanceof TurretBuild) {
							float scr = (b.health / 500f) - (apathy.dst(b) / 1500f);

							if (st == null || scr > sts) {
								st = b;
								sts = scr;
							}
							count++;
							if (count > 200) {
								break;
							}
						}
					}
				} else {
					for (Building b : td.buildings) {
						if (b instanceof TurretBuild) {
							float scr = (b.health / 500f) - (apathy.dst(b) / 1500f);

							if (st == null || scr > sts) {
								st = b;
								sts = scr;
							}
						}
					}
				}
			}
		}
		if (bulletCount > 0 || flyingCount > 0 || groundCount > 0) {
			apathy.conflict = 60f * 5f;
		}

		if (strongest != null && (!strongest.isAdded() || (strongest instanceof Healthc hh && hh.dead()))) {
			strongestValue = 0f;
			strongest = null;
		}
		if (st != null) {
			strongestValue = sts;
			strongest = st;
		}
		if (nearest != null && (!nearest.isAdded() || (nearest instanceof Healthc hh && hh.dead()))) {
			nearest = null;
		}
		if (nt != null) {
			nearest = nt;
		}

		if (updateScore) {
			Arrays.fill(shiftScore, 0f);
			//shiftScore[0] = Math.max(Math.max(100f, flyingCount), critDamage);
			shiftScore[0] = Math.max(20f, critDamage);

			/*if (isSurrounded()) {
				shiftScore[2] = groundCount + bulletCount / 100f;
				shiftScore[3] = Math.max(bulletCount, flyingCount + bulletCount / 100f);

				shiftScore[1] = Math.max(groundCount, flyingCount) / 10f;
			} else {
				float v = Math.max(200f, Math.max(groundCount, flyingCount));
				if (unit.health > unit.maxHealth / 3f && (Math.max(groundCount, flyingCount)) < 1000000f) {
					shiftScore[1] = v;
					shiftScore[4] = v * 0.333f + strongLaserScore;
				} else {
					shiftScore[4] = v;
					shiftScore[1] = v * 0.333f;
				}
			}*/
			if (critStun > 0) return;

			Vec2 surSrc = surroundScore();
			float conc = surSrc.x;
			float surr = surSrc.y;

			shiftScore[2] = (groundCount + bulletCount / 100f) * surr;
			shiftScore[3] = (Math.max(bulletCount, flyingCount + bulletCount / 100f)) * surr;

			float v = Math.max(groundCount, flyingCount) * conc;

			if (apathy.health > apathy.maxHealth / 3f && (Math.max(groundCount, flyingCount)) < 1000000f) {
				shiftScore[1] = v;
				shiftScore[4] = v * 0.1f + strongLaserScore;
			} else {
				shiftScore[4] = v;
				shiftScore[1] = v * 0.25f;
			}
		}

		//transformationTime += Time.delta;
	}

	public void criticalHit(float damage) {
		if (damage > 500000f && (currentTransformation == 1 || currentTransformation == 4)) {
			if (currentTransformation == 4 && laser != null && laser.type != null)
				laser.time = laser.type.lifetime - 80;
			transformationTime = 0f;
			apathy.extraShiftSpeed = 2f;
			critStun = 4f * 60f;
		}
		critDamage += damage / 2f;
		maxCritDamage = Math.max(critDamage, maxCritDamage);
	}

	public Vision getVisionAngle() {
		float max = 0f;
		float angle = 0f;
		int idx = -1;
		for (int i = 0; i < vision.length; i++) {
			//
			float a = (i / (float) vision.length) * 360f;
			if (vision[i] > max) {
				max = vision[i];
				angle = a;
				idx = i;
			}
		}
		//
		vis.angle = angle;
		vis.score = max;
		vis.idx = idx;
		return vis;
	}

	public Vec2 surroundScore() {
		Vec2 vec = Tmp.v1;

		float max = 0f;
		int maxIdx = -1;
		int dbl = directionalBias.length;
		float total = 0f;
		float total2 = 0f;

		for (int i = 0; i < dbl; i++) {
			float v = directionalBias[i];

			if (v > max) maxIdx = i;
			max = Math.max(max, v);
			total += v;
		}
		if (maxIdx < 0) return vec.set(1f, 1f);

		int hvl = dbl / 2 + 1;
		//let fadeSize = Math.max(2, Math.floor(vl * (20 / 180)));
		int fadeSize = Math.max(2, (int) (dbl * (17f / 180f)));
		for (int i = 0; i < hvl; i++) {
			int lastIdx = -1;
			float fade = Mathf.clamp(i / (float) fadeSize);
			for (int s = 0; s < 2; s++) {
				int si = s == 0 ? -1 : 1;
				int mi = Mathf.mod(i * si + maxIdx, dbl);
				if (mi != lastIdx) {
					total2 += directionalBias[mi] * fade;
					lastIdx = mi;
				}
			}
		}

		//vec.x = max / l;
		//vec.y = total - max;
		vec.x = max - total2 / dbl;
		vec.y = Math.max(0f, (total * 5f + total2) / 6f - max);
		float n = vec.x + vec.y;
		vec.x /= n;
		vec.y /= n;

		return vec;
	}

	public void moveTo() {
		if (strongest != null && (strongestValue > 1000f || nearestCore == null)) {
			float dst = apathy.dst(strongest);
			if (dst > scanRange - 100f) {
				Vec2 vec = Tmp.v1;

				vec.set(strongest).sub(apathy);
				vec.setLength(apathy.speed());

				apathy.movePref(vec);
				apathy.lookAt(apathy.angleTo(strongest));
			}
			return;
		}

		if (nearestCore == null || !nearestCore.isAdded()) {
			float dst = 0;
			Teamc core = null;

			for (TeamData td : Vars.state.teams.active) {
				if (td.team != apathy.team) {
					for (CoreBuild cores : td.cores) {
						float cd = apathy.dst2(cores);
						if (core == null || cd < dst) {
							core = cores;
							dst = cd;
						}
					}
				}
			}
			nearestCore = core;
		}
		if (nearestCore != null) {
			Vec2 vec = Tmp.v1;

			vec.set(nearestCore).sub(apathy);

			float length = Mathf.clamp((apathy.dst(nearestCore) - 220f) / 100f, -1f, 1f);

			vec.setLength(apathy.speed());
			vec.scl(length);

			if (vec.isNaN() || vec.isInfinite() || vec.isZero()) return;

			apathy.movePref(vec);
			apathy.lookAt(apathy.angleTo(nearestCore));
		}
	}

	@Override
	public void removed(Unit unit) {
		if (laser != null) {
			laser.time = Math.max(laser.lifetime - 48f, laser.time);
		}
		for (SoundLoop sl : sounds) {
			sl.stop();
		}

		apathy.switchShift(apathyType.handlers.get(0));
	}

	@Override
	public void unit(Unit unit) {
		super.unit(unit);

		if (apathy != unit) apathy = (ApathyIUnit) unit;
		if (apathyType != unit.type) apathyType = (ApathyUnitType) unit.type;
	}

	@Override
	public Unit unit() {
		return apathy;
	}

	public static class Vision {
		public float angle;
		public float score;
		public int idx;
	}
}
