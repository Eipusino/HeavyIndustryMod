package heavyindustry.type.unit;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Interval;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.pooling.Pool.Poolable;
import arc.util.pooling.Pools;
import heavyindustry.content.HFx;
import heavyindustry.entities.abilities.MirrorFieldAbility;
import heavyindustry.entities.bullet.EdgeFragBulletType;
import heavyindustry.entities.bullet.LightLaserBulletType;
import heavyindustry.entities.bullet.MultiTrailBulletType;
import heavyindustry.func.Floatt2;
import heavyindustry.gen.AirSeaAmphibiousUnit;
import heavyindustry.graphics.Draws;
import heavyindustry.graphics.HPal;
import heavyindustry.type.lightnings.LightningContainer;
import heavyindustry.type.lightnings.TrailMoveLightning;
import heavyindustry.type.weapons.RelatedWeapon;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.ContinuousBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Bullet;
import mindustry.gen.Posc;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Trail;
import mindustry.type.Weapon;
import mindustry.world.meta.BlockFlag;

import java.util.Iterator;

import static heavyindustry.HVars.MOD_NAME;
import static heavyindustry.content.HUnitTypes.EPHEMERAS;
import static heavyindustry.content.HUnitTypes.TIMER;

public class MornstarType extends AirSeaAmphibiousUnitType {
	private static final Rand rand = new Rand();

	public MornstarType(String name) {
		super(name);

		constructor = AirSeaAmphibiousUnit::new;

		armor = 19;
		speed = 0.84f;
		accel = 0.065f;
		drag = 0.03f;
		rotateSpeed = 1.8f;
		riseSpeed = 0.02f;
		boostMultiplier = 1.25f;
		faceTarget = true;
		health = 42500;
		lowAltitude = true;
		hitSize = 64;
		targetFlags = BlockFlag.allLogic;

		drawShields = false;

		engineOffset = 0;
		engineSize = 0;

		abilities.addAll(new MirrorFieldAbility() {{
			strength = 340;
			max = 12500;
			regen = 6f;
			cooldown = 2680;
			minAlbedo = 0.7f;
			maxAlbedo = 1f;
			rotation = false;

			shieldArmor = 10;

			nearRadius = 156;

			Floatt2<ShieldShape> a = (ofx, ofy) -> new ShieldShape(6, 0, 0, 0, 48) {{
				movement = new ShapeMove() {{
					x = ofx;
					y = ofy;
					rotateSpeed = 0.35f;

					childMoving = new ShapeMove() {{
						rotateSpeed = -0.2f;
					}};
				}};
			}};

			shapes.addAll(new ShieldShape(8, 0, 0, 0, 102) {{
				movement = new ShapeMove() {{
					rotateSpeed = -0.1f;
				}};
			}}, a.get(90f, 0f), a.get(-90f, 0f), a.get(0f, 90f), a.get(0f, -90f));
		}});

		setEnginesMirror(new UnitEngine() {{
			x = 16f;
			y = -44f;
			radius = 10;
			rotation = 45;
		}}, new UnitEngine() {{
			x = 24f;
			y = -52f;
			radius = 6;
			rotation = 45;
		}}, new UnitEngine() {{
			x = 34f;
			y = -52f;
			radius = 8;
			rotation = -45;
		}});

		weapons.addAll(new Weapon(this.name + "-cannon") {{
			recoil = 0;
			recoilTime = 120;
			cooldownTime = 120;

			reload = 90;
			rotate = true;
			mirror = false;

			rotateSpeed = 2.5f;

			layerOffset = 1;

			x = 0;
			y = 4;
			shootY = 25;

			shoot = new ShootBarrel() {{
				barrels = new float[]{
						5.75f, 0, 0,
						-5.75f, 0, 0
				};
				shots = 2;
				shotDelay = 0;
			}};

			bullet = new MultiTrailBulletType() {{
				hitColor = trailColor = HPal.matrixNet;
				trailLength = 22;
				trailWidth = 2f;
				trailEffect = new MultiEffect(
						HFx.trailLineLong,
						HFx.railShootRecoil,
						HFx.movingCrystalFrag
				);
				trailRotation = true;
				trailChance = 1;

				lightColor = HPal.matrixNet;
				lightRadius = 120;
				lightOpacity = 0.8f;

				shootEffect = new MultiEffect(
						HFx.shootRecoilWave,
						HFx.shootRail
				);
				hitEffect = HFx.lightConeHit;
				despawnEffect = new MultiEffect(
						HFx.impactWaveSmall,
						HFx.spreadSparkLarge,
						HFx.diamondSparkLarge
				);
				smokeEffect = Fx.shootSmokeSmite;

				shootSound = Sounds.shootSmite;

				damage = 500;
				//empDamage = 100;
				lifetime = 45;
				speed = 8;
				pierceCap = 4;
				hittable = false;

				fragBullet = new EdgeFragBulletType();
				fragOnHit = true;
				fragBullets = 3;
				fragRandomSpread = 115;
				fragLifeMin = 0.7f;
			}
				@Override
				public void draw(Bullet b) {
					super.draw(b);
					Draw.color(HPal.matrixNet);
					Draws.gapTri(b.x, b.y, 18, 28, 16, b.rotation());
					Draws.drawTransform(b.x, b.y, -6, 0, b.rotation(), (x, y, r) -> {
						Draws.drawDiamond(x, y, 16, 8, r);
					});
				}

				@Override
				public void hit(Bullet b) {
					super.hit(b);
					b.damage -= 125;
				}

				@Override
				public void init(Bullet b) {
					super.init(b);
					b.data = Pools.obtain(TrailMoveLightning.class, TrailMoveLightning::new);
				}

				@Override
				public void despawned(Bullet b) {
					super.despawned(b);
					if (b.data instanceof TrailMoveLightning l) Pools.free(l);
				}

				@Override
				public void updateTrail(Bullet b) {
					if (!Vars.headless && trailLength > 0) {
						if (b.trail == null) {
							b.trail = new Trail(trailLength);
						}
						b.trail.length = trailLength;

						if (!(b.data instanceof TrailMoveLightning m)) return;
						m.update();
						Draws.drawTransform(b.x, b.y, 0, m.off, b.rotation(), (x, y, r) -> b.trail.update(x, y));
					}
				}
			};

			parts.addAll(new RegionPart("_blade") {{
				under = true;
				progress = PartProgress.recoil;
				moveY = -3;
				heatColor = Pal.turretHeat;
				heatProgress = PartProgress.heat;
			}}, new RegionPart("_body") {{
				under = true;
			}});
		}}, new Weapon(this.name + "-turret") {{
			x = 26;
			y = -28;
			shootY = 0;
			recoil = 5;
			recoilTime = 60;
			reload = 4;

			rotate = true;
			rotateSpeed = 8;

			/*customDisplay = (b, t) -> {
				t.row();
				t.add(Core.bundle.get("infos.damageAttenuationWithDist")).color(Pal.accent);
			};*/

			bullet = new BulletType() {{
				speed = 12;
				lifetime = 30;
				damage = 180;

				lightColor = HPal.matrixNet;
				lightRadius = 58;
				lightOpacity = 0.6f;

				pierceCap = 1;

				despawnHit = true;

				shootSound = Sounds.shootLocus;

				hitColor = trailColor = HPal.matrixNet;
				hitEffect = new MultiEffect(
						HFx.spreadDiamondSmall,
						HFx.movingCrystalFrag
				);
				smokeEffect = Fx.colorSpark;
				shootEffect = new MultiEffect(
						HFx.railShootRecoil,
						HFx.crossLightMini
				);
				trailWidth = 3;
				trailLength = 8;

				trailEffect = HFx.glowParticle;
				trailChance = 0.12f;
			}
				@Override
				public void draw(Bullet b) {
					super.draw(b);
					Draw.color(HPal.matrixNet);
					Tmp.v1.set(1, 0).setAngle(b.rotation());
					Draws.gapTri(b.x + Tmp.v1.x * 3 * b.fout(), b.y + Tmp.v1.y * 3 * b.fout(), 15, 22, 14, b.rotation());
					Draws.gapTri(b.x - Tmp.v1.x * 3 * b.fout(), b.y - Tmp.v1.y * 3 * b.fout(), 12, 18, 10, b.rotation());
					Draws.gapTri(b.x - Tmp.v1.x * 5 * b.fout(), b.y - Tmp.v1.y * 5 * b.fout(), 9, 12, 8, b.rotation());
					Draws.drawDiamond(b.x, b.y, 18, 6, b.rotation());
				}

				@Override
				public void update(Bullet b) {
					super.update(b);
					b.damage = (b.type.damage + b.type.damage*b.fout())*0.5f;
				}
			};
		}}, new RelatedWeapon(MOD_NAME + "-lightedge") {{
			useAlternative = isFlying;
			mirror = false;
			x = 0;
			y = -25;
			shootCone = 180;

			recoil = 0;
			recoilTime = 1;

			alternate = false;

			linearWarmup = false;
			shootWarmupSpeed = 0.025f;
			minWarmup = 0.9f;

			reload = 60;

			bullet = new ContinuousBulletType() {{
				speed = 0;
				lifetime = 180;
				length = 420;

				damage = 80;

				lightColor = HPal.matrixNet;
				lightRadius = 96;
				lightOpacity = 1;

				hitEffect = HFx.railShootRecoil;

				trailColor = hitColor = HPal.matrixNet;
				trailEffect = HFx.movingCrystalFrag;
				trailInterval = 4;

				shootEffect = new MultiEffect(
						HFx.shootCrossLight,
						HFx.explodeImpWaveSmall
				);
			}
				@Override
				public void init(Bullet b) {
					super.init(b);

					Sounds.shootCorvus.at(b.x, b.y, 1.25f);

					if (b.owner instanceof Unit u) {
						b.rotation(b.angleTo(u.aimX, u.aimY));
					}
				}

				@Override
				public void update(Bullet b) {
					super.update(b);

					Effect.shake(4, 4, b.x, b.y);
					updateTrailEffects(b);
					if (b.owner instanceof Unit u) {
						b.rotation(Angles.moveToward(b.rotation(), b.angleTo(u.aimX, u.aimY), 8 * Time.delta));
					}
				}

				@Override
				public void applyDamage(Bullet b) {
					Damage.collideLaser(b, length, largeHit, laserAbsorb, pierceCap);
				}

				@Override
				public void draw(Bullet b) {
					super.draw(b);

					float realLen = b.fdata;
					float lerp = Mathf.clamp(b.time / 40);
					float out = Mathf.clamp((b.type.lifetime - b.time) / 30);
					lerp *= out;

					lerp = 1 - Mathf.pow(1 - lerp, 3);

					Draw.color(HPal.matrixNet);
					Drawf.tri(b.x, b.y, 12 * lerp, realLen, b.rotation());
					Draw.color(Color.black);
					Drawf.tri(b.x, b.y, 5 * lerp, realLen * 0.8f, b.rotation());
					Draw.color(HPal.matrixNet);
					Fill.circle(b.x, b.y, 4 * out + 3 * lerp);
					Draws.drawDiamond(b.x, b.y, 24, 10 * lerp, Time.time);
					Draws.drawDiamond(b.x, b.y, 28, 12 * lerp, -Time.time * 1.2f);
					Lines.stroke(0.8f);
					Lines.circle(b.x, b.y, 6);
					Draw.color(Color.black);
					Fill.circle(b.x, b.y, 4f * lerp);

					Drawf.light(b.x, b.y,
							b.x + Angles.trnsx(b.rotation(), realLen), b.y + Angles.trnsy(b.rotation(), realLen),
							46 * lerp, lightColor, lightOpacity);

					Draw.color(HPal.matrixNet);
					Draws.gapTri(b.x + Angles.trnsx(Time.time, 8, 0), b.y + Angles.trnsy(Time.time, 8, 0), 8 * lerp, 12 + 14 * lerp, 8, Time.time);
					Draws.gapTri(b.x + Angles.trnsx(Time.time + 180, 8, 0), b.y + Angles.trnsy(Time.time + 180, 8, 0), 8 * lerp, 12 + 14 * lerp, 8, Time.time + 180);
					Draws.drawDiamond(b.x + Angles.trnsx(-Time.time * 1.2f, 12, 0), b.y + Angles.trnsy(-Time.time * 1.2f, 12, 0), 16, 5 * lerp, -Time.time * 1.2f);
					Draws.drawDiamond(b.x + Angles.trnsx(-Time.time * 1.2f + 180, 12, 0), b.y + Angles.trnsy(-Time.time * 1.2f + 180, 12, 0), 16, 5 * lerp, -Time.time * 1.2f + 180);

					float out2 = Mathf.pow(1 - out, 3);
					Tmp.v1.set(35 + 30 * out2, 0).setAngle(b.rotation());
					Tmp.v2.set(Tmp.v1).setLength(8 + 10 * lerp).rotate90(1);

					float len = 100 + out2 * 80;
					float an = Mathf.atan2(len / 2, 8 * lerp) * Mathf.radDeg;

					Drawf.tri(b.x + Tmp.v1.x + Tmp.v2.x, b.y + Tmp.v1.y + Tmp.v2.y, len, 8 * lerp, b.rotation() - 90 - an);
					Drawf.tri(b.x + Tmp.v1.x - Tmp.v2.x, b.y + Tmp.v1.y - Tmp.v2.y, len, 8 * lerp, b.rotation() + 90 + an);
				}
			};

			alternativeBullet = new BulletType() {{
				splashDamage = 380;
				splashDamageRadius = 32;

				speed = 8;
				lifetime = 360;
				rangeOverride = 360;
				homingDelay = 60;

				homingPower = 0.03f;
				homingRange = 360;

				lightColor = HPal.matrixNet;
				lightRadius = 75;
				lightOpacity = 0.8f;

				despawnShake = 6;

				collides = false;
				absorbable = false;
				hittable = false;

				keepVelocity = false;

				trailColor = hitColor = HPal.matrixNet;
				trailLength = 34;
				trailWidth = 4.5f;

				despawnEffect = new MultiEffect(
						HFx.explodeImpWave,
						HFx.crossLightSmall,
						HFx.diamondSparkLarge
				);

				trailEffect = HFx.movingCrystalFrag;
				trailInterval = 4;

				fragBullet = new BulletType() {{
					damage = 60;
					splashDamage = 80;
					splashDamageRadius = 24;
					speed = 4;
					hitSize = 3;
					lifetime = 120;
					despawnHit = true;
					hitEffect = HFx.diamondSpark;
					hitColor = HPal.matrixNet;

					collidesTiles = false;

					homingRange = 240;
					homingPower = 0.035f;

					trailColor = HPal.matrixNet;
					trailLength = 25;
					trailWidth = 3f;
					trailEffect = HFx.movingCrystalFrag;
					trailInterval = 5;
				}
					@Override
					public void draw(Bullet b) {
						drawTrail(b);
						Draw.color(hitColor);
						Fill.circle(b.x, b.y, 4);
						Draw.color(Color.black);
						Fill.circle(b.x, b.y, 2.5f);
					}

					@Override
					public void updateHoming(Bullet b) {
						Posc target = Units.closestTarget(b.team, b.x, b.y, homingRange,
								e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
								t -> t != null && collidesGround && !b.hasCollided(t.id));

						if (target == null) {
							b.vel.lerpDelta(Vec2.ZERO, homingPower);
						} else {
							b.vel.lerpDelta(Tmp.v1.set(target.x() - b.x, target.y() - b.y).setLength(speed * 0.5f), homingPower);
						}
					}
				};
				fragBullets = 5;
				fragLifeMin = 0.7f;

				intervalBullet = new LightLaserBulletType() {{
					damage = 150;
					//empDamage = 20;
				}
					@Override
					public void init(Bullet b, LightningContainer c) {
						Teamc target = Units.closestTarget(b.team, b.x, b.y, range,
								e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
								t -> t != null && collidesGround && !b.hasCollided(t.id));

						if (target != null) {
							b.rotation(b.angleTo(target));
						}

						super.init(b, c);
					}
				};
				bulletInterval = 15f;
			}
				@Override
				public void draw(Bullet b) {
					super.draw(b);
					Draw.color(HPal.matrixNet);

					float lerp = Mathf.clamp(b.time / homingDelay);
					lerp = 1 - Mathf.pow(1 - lerp, 2);

					Fill.circle(b.x, b.y, 4 + 2 * lerp);
					Draws.drawDiamond(b.x, b.y, 22, 10 * lerp, Time.time);
					Lines.stroke(0.8f);
					Lines.circle(b.x, b.y, 6);
					Draw.color(Color.black);
					Fill.circle(b.x, b.y, 3.75f * lerp);
					Draw.color(HPal.matrixNet);

					rand.setSeed(b.id);
					for (int i = 0; i < 7; i++) {
						float w = rand.random(1f, 2.5f) * (rand.random(1f) > 0.5 ? 1 : -1);
						float f = rand.random(360f);
						float r = rand.random(12f, 28f);
						float size = rand.random(18f, 26f) * lerp;

						float a = f + Time.time * w;
						Tmp.v1.set(r, 0).setAngle(a);

						Draws.drawHaloPart(b.x + Tmp.v1.x, b.y + Tmp.v1.y, size, size * 0.5f, a);
					}
				}

				@Override
				public void despawned(Bullet b) {
					super.despawned(b);
					Sounds.shootMalign.at(b, 2);
				}

				@Override
				public void update(Bullet b) {
					super.update(b);

					if (b.timer(4, 18f)) {
						Teamc target = Units.closestTarget(b.team, b.x, b.y, range,
								e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
								t -> t != null && collidesGround && !b.hasCollided(t.id));

						fragBullet.create(b, b.x, b.y, target != null ? b.angleTo(target) : Mathf.random(0, 360));
					}
				}

				@Override
				public void updateHoming(Bullet b) {
					if (homingPower > 0.0001f && b.time >= homingDelay) {
						float realAimX = b.aimX < 0 ? b.x : b.aimX;
						float realAimY = b.aimY < 0 ? b.y : b.aimY;

						Posc target;
						if (b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && collidesGround && !b.hasCollided(b.aimTile.build.id)) {
							target = b.aimTile.build;
						} else {
							target = Units.closestTarget(b.team, realAimX, realAimY, homingRange,
									e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
									t -> t != null && collidesGround && !b.hasCollided(t.id));
						}

						if (target != null) {
							float dst = target.dst(b);
							float v = Mathf.lerpDelta(b.vel.len(), speed * (dst / homingRange), 0.05f);
							b.vel.setLength(v);

							float degA = b.rotation();
							float degB = b.angleTo(target);

							if (degA - degB > 180) {
								degB += 360;
							} else if (degA - degB < -180) {
								degB -= 360;
							}

							b.vel.setAngle(Mathf.lerpDelta(degA, degB, homingPower));
						} else {
							b.vel.lerpDelta(0, 0, 0.03f);
						}
					}
				}
			};
		}
			@Override
			public void init(Unit unit, DataWeaponMount mount) {
				super.init(unit, mount);
				mount.setVar(EPHEMERAS, new Seq<>(Ephemera.class));
				mount.setVar(TIMER, new Interval(3));
			}

			@Override
			protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation) {
				DataWeaponMount m = (DataWeaponMount) mount;
				Seq<Ephemera> seq = m.getVar(EPHEMERAS);
				for (Ephemera ephemera : seq) {
					if (ephemera.alpha > 0.9f) {
						ephemera.shoot(unit, useAlternative.alt(unit) ? alternativeBullet : bullet);
						ephemera.removed = true;
						break;
					}
				}
			}

			@Override
			public void draw(Unit unit, DataWeaponMount mount) {
				super.draw(unit, mount);
				Draw.z(Layer.effect);
				Draws.drawTransform(unit.x, unit.y, mount.weapon.x, mount.weapon.y, unit.rotation - 90, (x, y, r) -> {
					Draw.color(HPal.matrixNet);
					Fill.circle(x, y, 6);
					Lines.stroke(0.8f);
					Draws.dashCircle(x, y, 8, 4, 180, Time.time);
					Lines.stroke(0.5f);
					Lines.circle(x, y, 10);

					Draw.alpha(1);
					Draws.drawDiamond(x, y, 20 + 14 * mount.warmup, 2 + 3 * mount.warmup, Time.time * 1.2f);
					Draws.drawDiamond(x, y, 26 + 14 * mount.warmup, 3 + 4 * mount.warmup, -Time.time * 1.2f);

					for (Ephemera ephemera : mount.<Seq<Ephemera>>getVar(EPHEMERAS)) {
						Draw.color(HPal.matrixNet);

						if (!ephemera.removed) {
							Fill.circle(ephemera.x, ephemera.y, 4);
							Lines.stroke(0.8f);
							Lines.circle(ephemera.x, ephemera.y, 6);
						}

						Drawf.light(ephemera.x, ephemera.y, 60, lightColor, 0.45f);

						for (int i = 0; i < 3; i++) {
							Tmp.v1.set(16, 0).setAngle(Time.time + i * 120);
							float sin = Mathf.absin((Time.time * 4 + i * 120) * Mathf.degRad, 0.5f, 1);
							sin = Math.max(sin, mount.warmup) * ephemera.alpha;
							float w = sin * sin * sin * 4;
							Draws.drawDiamond(ephemera.x + Tmp.v1.x, ephemera.y + Tmp.v1.y, 20, w, Time.time + i * 120);
							Draws.drawDiamond(ephemera.x - Tmp.v1.x, ephemera.y - Tmp.v1.y, 20, w, Time.time + i * 120);
						}

						ephemera.trail.draw(HPal.matrixNet, 4f * ephemera.alpha);
					}
				});
			}

			@Override
			public void update(Unit unit, DataWeaponMount mount) {
				Tmp.v1.set(mount.weapon.x, mount.weapon.y).rotate(unit.rotation - 90);
				float mx = unit.x + Tmp.v1.x;
				float my = unit.y + Tmp.v1.y;
				Seq<Ephemera> seq = mount.getVar(EPHEMERAS);

				if (seq.size < 4) {
					if (mount.<Interval>getVar(TIMER).get(0, 240)) {
						mount.totalShots++;

						Ephemera ephemera = Pools.obtain(Ephemera.class, Ephemera::new);
						ephemera.x = mx;
						ephemera.y = my;
						ephemera.move = Mathf.random(0.02f, 0.04f);
						ephemera.angelOff = Mathf.random(15, 45) * (mount.totalShots % 2 == 0 ? 1 : -1);
						ephemera.bestDst = Mathf.random(18, 36);
						ephemera.vel.rnd(Mathf.random(0.6f, 2));

						seq.add(ephemera);
					}
				}

				if (seq.isEmpty()) {
					mount.reload = mount.weapon.reload;
				}

				for (Iterator<Ephemera> iterator = seq.iterator(); iterator.hasNext(); ) {
					Ephemera ephemera = iterator.next();
					ephemera.alpha = Mathf.lerpDelta(ephemera.alpha, ephemera.removed ? 0 : 1, 0.015f);
					ephemera.trail.update(ephemera.x, ephemera.y);
					if (ephemera.removed) {
						if (ephemera.alpha <= 0.05f) {
							iterator.remove();
							Pools.free(ephemera);
						}
						continue;
					}

					ephemera.x += ephemera.vel.x * Time.delta;
					ephemera.y += ephemera.vel.y * Time.delta;

					float dst = (ephemera.bestDst + ephemera.bestDst * mount.warmup) - Mathf.dst(ephemera.x - mx, ephemera.y - my);
					float speed = dst / 30;

					ephemera.vel.lerpDelta(Tmp.v1.set(ephemera.x - unit.x, ephemera.y - unit.y).setLength2(1).scl(speed).rotate(ephemera.angelOff), 0.15f);
					Tmp.v1.set(ephemera.move + ephemera.move * mount.warmup, 0).rotate(Time.time);
					ephemera.vel.add(Tmp.v1);
				}
			}
		});
	}

	public static class Ephemera implements Poolable {
		public float x, y, angelOff, move;
		public float bestDst, alpha;
		public boolean removed;
		public final Vec2 vel = new Vec2();
		public final Trail trail = new Trail(60);

		@Override
		public void reset() {
			x = y = 0;
			alpha = 0;
			removed = false;
			vel.setZero();
			trail.clear();
			bestDst = 0;
			move = 0;
			angelOff = 0;
		}

		public void shoot(Unit u, BulletType bullet) {
			Bullet b = bullet.create(u, x, y, vel.angle());
			if (b.type.speed > 0.01f) b.vel.set(vel);
			b.set(x, y);
			bullet.shootEffect.at(b.x, b.y, vel.angle(), b.type.hitColor);
			bullet.smokeEffect.at(b.x, b.y, vel.angle(), b.type.hitColor);
		}
	}
}
