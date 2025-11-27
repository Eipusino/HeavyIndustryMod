package heavyindustry.type.unit;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Collapser;
import arc.scene.ui.layout.Table;
import arc.util.Interval;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.content.HFx;
import heavyindustry.entities.abilities.MirrorFieldAbility;
import heavyindustry.entities.bullet.EdgeFragBulletType;
import heavyindustry.entities.bullet.MultiTrailBulletType;
import heavyindustry.entities.part.CustomPart;
import heavyindustry.func.Floatt2;
import heavyindustry.gen.Unit2;
import heavyindustry.graphics.Draws;
import heavyindustry.graphics.HPal;
import heavyindustry.graphics.MathRenderer;
import heavyindustry.math.Mathm;
import heavyindustry.type.weapons.DataWeapon;
import heavyindustry.world.meta.HStatValues;
import mindustry.content.Fx;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.entities.bullet.PointLaserBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Bullet;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Trail;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.ammo.PowerAmmoType;
import mindustry.type.weapons.PointDefenseWeapon;
import mindustry.ui.Styles;
import mindustry.world.meta.BlockFlag;

import static heavyindustry.HVars.MOD_NAME;
import static heavyindustry.content.HUnitTypes.SHOOTERS;
import static heavyindustry.content.HUnitTypes.TIMER;

public class KaguyaUnitType extends UnitType2 {
	public KaguyaUnitType(String name) {
		super(name);

		constructor = Unit2::new;
		armor = 20f;
		speed = 1.1f;
		accel = 0.06f;
		drag = 0.04f;
		rotateSpeed = 1.5f;
		faceTarget = true;
		flying = true;
		health = 45000f;
		lowAltitude = true;
		//canBoost = true;
		//boostMultiplier = 2.5f;
		hitSize = 70f;
		targetFlags = BlockFlag.all;
		drawShields = false;
		engineSize = 0;
		ammoType = new PowerAmmoType(3000);
		ammoCapacity = 2200;
		abilities.add(new MirrorFieldAbility() {{
			strength = 350f;
			max = 15800f;
			regen = 8f;
			cooldown = 6500f;
			minAlbedo = 1f;
			maxAlbedo = 1f;
			rotation = false;
			shieldArmor = 22f;
			nearRadius = 160f;
			Floatt2<ShieldShape> alpha = (ofx, ofy) -> new ShieldShape(6, 0, 0, 0, 48f) {{
				movement = new ShapeMove() {{
					x = ofx;
					y = ofy;
					rotateSpeed = 0.35f;
					childMoving = new ShapeMove() {{
						rotateSpeed = -0.2f;
					}};
				}};
			}};
			Floatt2<ShieldShape> beta = (ofx, ofy) -> new ShieldShape(5, 0f, 0f, 0f, 48f) {{
				movement = new ShapeMove() {{
					x = ofx;
					y = ofy;
					rotateSpeed = -0.25f;
					childMoving = new ShapeMove() {{
						rotateSpeed = 0.15f;
					}};
				}};
			}};
			shapes.addAll(new ShieldShape(10, 0f, 0f, 0f, 112f) {{
				movement = new ShapeMove() {{
					rotateSpeed = -0.1f;
				}};
			}}, alpha.get(90f, 0f), alpha.get(-90f, 0f), alpha.get(0f, 90f), alpha.get(0f, -90f), beta.get(100f, 0f), beta.get(-100f, 0f), beta.get(0f, 100f), beta.get(0f, -100f));
		}});
		Floatt2<Weapon> laser = (dx, dy) -> new Weapon(this.name + "-laser") {{
			x = dx;
			y = dy;
			mirror = true;
			reload = 30;
			recoil = 4;
			recoilTime = 30;
			shadow = 4;
			rotate = true;
			layerOffset = 0.1f;
			shootSound = Sounds.laser;
			shake = 3;
			bullet = new LaserBulletType(165f) {{
				lifetime = 20;
				sideAngle = 90f;
				sideWidth = 1.25f;
				sideLength = 15f;
				width = 16f;
				length = 450f;
				hitEffect = Fx.circleColorSpark;
				shootEffect = Fx.colorSparkBig;
				colors = new Color[]{HPal.matrixNetDark, HPal.matrixNet, Color.white};
				hitColor = colors[0];
			}};
		}};
		weapons.add(laser.get(19.25f, 16f), laser.get(13.5f, 33.5f));
		weapons.add(new Weapon(this.name + "-cannon") {{
			x = 30.5f;
			y = -3.5f;
			mirror = true;
			cooldownTime = 120;
			recoil = 0;
			recoilTime = 120;
			reload = 90;
			shootX = 2;
			shootY = 22;
			rotate = true;
			rotationLimit = 30;
			rotateSpeed = 10;
			shake = 1.5f;
			layerOffset = 0.1f;
			shootSound = Sounds.shockBlast;
			shoot.shots = 3;
			shoot.shotDelay = 10;
			bullet = new MultiTrailBulletType() {{
				speed = 6;
				lifetime = 75;
				damage = 180;
				splashDamage = 240;
				splashDamageRadius = 36;
				hitEffect = new MultiEffect(Fx.shockwave, Fx.bigShockwave, HFx.impactWaveSmall, HFx.spreadSparkLarge, HFx.diamondSparkLarge);
				despawnHit = true;
				smokeEffect = Fx.shootSmokeSmite;
				shootEffect = HFx.railShootRecoil;
				hitColor = HPal.matrixNet;
				trailColor = HPal.matrixNet;
				hitSize = 8;
				trailLength = 36;
				trailWidth = 4;
				hitShake = 4;
				hitSound = Sounds.dullExplosion;
				hitSoundVolume = 3.5f;
				trailEffect = HFx.trailParticle;
				trailChance = 0.5f;
				fragBullet = new EdgeFragBulletType();
				fragBullets = 4;
				fragLifeMin = 0.7f;
				fragOnHit = true;
				fragOnAbsorb = true;
			}
				@Override
				public void draw(Bullet b) {
					super.draw(b);

					Drawf.tri(b.x, b.y, 12, 30, b.rotation());
					Drawf.tri(b.x, b.y, 12, 12, b.rotation() + 180);
				}
			};
		}}, new PointDefenseWeapon(this.name + "-point-laser") {{
			x = 30.5f;
			y = -3.5f;
			mirror = true;
			recoil = 0;
			reload = 12;
			targetInterval = 0;
			targetSwitchInterval = 0;
			layerOffset = 0.2f;
			bullet = new BulletType();
			bullet.damage = 62;
			bullet.rangeOverride = 420;
		}}, new DataWeapon(MOD_NAME + "-lightedge") {{
			x = 0f;
			y = -14f;
			minWarmup = 0.98f;
			shootWarmupSpeed = 0.02f;
			linearWarmup = false;
			rotate = false;
			shootCone = 10;
			rotateSpeed = 10;
			shootY = 80;
			reload = 30;
			recoilTime = 60;
			recoil = 2;
			recoilPow = 0;
			targetSwitchInterval = 300;
			targetInterval = 0;
			mirror = false;
			continuous = true;
			alwaysContinuous = true;
			Weapon weapon = this;
			bullet = new PointLaserBulletType() {{
				damage = 240f;
				damageInterval = 5f;
				rangeOverride = 450f;
				shootEffect = HFx.railShootRecoil;
				hitColor = HPal.matrixNet;
				hitEffect = HFx.diamondSparkLarge;
				shake = 1.5f;
			}
				public final Rand rand = new Rand();

				@Override
				public float continuousDamage() {
					return damage * (60 / damageInterval);
				}

				@Override
				public void update(Bullet b) {
					super.update(b);

					if (b.owner instanceof Unit u) {
						for (WeaponMount mount : u.mounts) {
							if (mount.weapon == weapon) {
								float bulletX = u.x + Angles.trnsx(u.rotation - 90, x + shootX, y + shootY),
										bulletY = u.y + Angles.trnsy(u.rotation - 90, x + shootX, y + shootY);

								b.set(bulletX, bulletY);
								Tmp.v2.set(mount.aimX - bulletX, mount.aimY - bulletY);
								float angle = Mathm.clamp(Tmp.v2.angle() - u.rotation, -shootCone, shootCone);
								Tmp.v2.setAngle(u.rotation).rotate(angle);

								Tmp.v1.set(b.aimX - bulletX, b.aimY - bulletY).lerpDelta(Tmp.v2, 0.1f).clampLength(80, range);

								b.aimX = bulletX + Tmp.v1.x;
								b.aimY = bulletY + Tmp.v1.y;

								shootEffect.at(bulletX, bulletY, Tmp.v1.angle(), hitColor);
							}
						}
					}
				}

				@Override
				public void draw(Bullet b) {
					super.draw(b);

					Draw.draw(Draw.z(), () -> {
						Draw.color(hitColor);
						MathRenderer.setDispersion(0.1f);
						MathRenderer.setThreshold(0.4f, 0.6f);

						rand.setSeed(b.id);

						for (int i = 0; i < 3; i++) {
							MathRenderer.drawSin(b.x, b.y, b.aimX, b.aimY,
									rand.random(4f, 6f) * b.fslope(),
									rand.random(360f, 720f),
									rand.random(360f) - Time.time * rand.random(4f, 7f)
							);
						}
					});
				}
			};
			parts.addAll(new CustomPart() {{
				layer = Layer.effect;
				progress = PartProgress.warmup;
				draw = (x, y, r, p) -> {
					Draw.color(HPal.matrixNet);
					Fill.circle(x, y, 8);
					Lines.stroke(1.4f);
					Draws.dashCircle(x, y, 12, Time.time);

					Draw.draw(Draw.z(), () -> {
						MathRenderer.setThreshold(0.65f, 0.8f);
						MathRenderer.setDispersion(1f);
						MathRenderer.drawCurveCircle(x, y, 15, 2, 6, Time.time);
						MathRenderer.setDispersion(0.6f);
						MathRenderer.drawCurveCircle(x, y, 16, 3, 6, -Time.time);
					});

					Draw.alpha(0.65f);
					Draws.gradientCircle(x, y, 20, 12, 0);

					Draw.alpha(1);
					Draws.drawDiamond(x, y, 24 + 18 * p, 3 + 3 * p, Time.time * 1.2f);
					Draws.drawDiamond(x, y, 30 + 18 * p, 4 + 4 * p, -Time.time * 1.2f);
				};
			}});
		}
			public final BulletType subBull = new BulletType() {{
				damage = 62;
				speed = 5;
				homingDelay = 30;
				homingPower = 0.1f;
				homingRange = 460;
				lifetime = 150;
				hitSize = 2;
				keepVelocity = false;
				pierceArmor = true;
				hitColor = trailColor = HPal.matrixNet;
				trailWidth = 1;
				trailLength = 38;
				hitEffect = HFx.neutronWeaveMicro;
				despawnEffect = HFx.constructSpark;
			}
				@Override
				public void draw(Bullet b) {
					super.draw(b);

					Draw.color(hitColor);
					Fill.circle(b.x, b.y, hitSize / 2);
				}

				@Override
				public void updateHoming(Bullet b) {
					if (b.time > homingDelay) {
						float realAimX = b.aimX < 0 ? b.x : b.aimX;
						float realAimY = b.aimY < 0 ? b.y : b.aimY;

						Teamc target;

						if (b.aimTile != null && b.aimTile.build != null && b.aimTile.build.team != b.team && collidesGround && !b.hasCollided(b.aimTile.build.id)) {
							target = b.aimTile.build;
						} else {
							target = Units.closestTarget(b.team, realAimX, realAimY, homingRange,
									e -> e != null && e.checkTarget(collidesAir, collidesGround) && !b.hasCollided(e.id),
									t -> t != null && collidesGround && !b.hasCollided(t.id));
						}

						if (target != null) {
							Tmp.v1.set(target).sub(b).setLength(homingPower).scl(Time.delta);
							if (b.vel.len() < speed) b.vel.add(Tmp.v1);
							b.vel.setAngle(Angles.moveToward(b.rotation(), b.angleTo(target), homingPower * Time.delta * 50f));
						}
					}
				}
			};

			@Override
			public void init(Unit unit, DataWeaponMount mount) {
				Shooter[] shooters = new Shooter[3];

				for (int i = 0; i < shooters.length; i++) {
					shooters[i] = new Shooter();
				}
				mount.setVar(SHOOTERS, shooters);
				mount.setVar(TIMER, new Interval());
			}

			@Override
			public void update(Unit unit, DataWeaponMount mount) {
				Shooter[] shooters = mount.getVar(SHOOTERS);
				Interval timer = mount.getVar(TIMER);

				for (Shooter shooter : shooters) {
					Vec2 v = Mathm.fourierSeries(Time.time, shooter.param).scl(mount.warmup);
					Tmp.v1.set(mount.weapon.x, mount.weapon.y).rotate(unit.rotation - 90);
					shooter.x = Tmp.v1.x + v.x;
					shooter.y = Tmp.v1.y + v.y;
					shooter.trail.update(unit.x + shooter.x, unit.y + shooter.y);
				}

				if (mount.warmup > 0.8f && timer.get(120)) {
					for (int i = 0; i < shooters.length; i++) {
						Shooter shooter = shooters[i];
						Time.run(i * 40, () -> {
							HFx.explodeImpWaveMini.at(unit.x + shooter.x, unit.y + shooter.y, HPal.matrixNet);
							for (int l = 0; l < 10; l++) {
								Time.run(l * 4, () -> {
									Vec2 v = Mathm.fourierSeries(Time.time, shooter.param).scl(mount.warmup);
									subBull.create(unit, unit.team, unit.x + shooter.x, unit.y + shooter.y, Angles.angle(v.x, v.y), 0.2f);
								});
							}
						});
					}
				}
			}

			@Override
			public void addStats(UnitType u, Table t) {
				super.addStats(u, t);

				t.row();

				Table ic = new Table();

				HStatValues.ammo3(ic, subBull);

				Collapser coll = new Collapser(ic, true);
				coll.setDuration(0.1f);

				t.table(it -> {
					it.left().defaults().left();

					it.add(Core.bundle.format("bullet.interval", 15));
					it.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
				});
				t.row();
				t.add(coll).padLeft(16);
			}

			@Override
			protected void shoot(Unit unit, DataWeaponMount mount, float shootX, float shootY, float rotation) {
				float mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
						mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y);

				HFx.shootRecoilWave.at(shootX, shootY, rotation, HPal.matrixNet);
				HFx.impactWave.at(shootX, shootY, HPal.matrixNet);

				HFx.impactWave.at(mountX, mountY, HPal.matrixNet);
				HFx.crossLight.at(mountX, mountY, HPal.matrixNet);
				Shooter[] shooters = mount.getVar(SHOOTERS);

				for (Shooter shooter : shooters) {
					HFx.impactWaveSmall.at(mountX + shooter.x, mountY + shooter.y);
				}
			}

			@Override
			public void draw(Unit unit, DataWeaponMount mount) {
				Shooter[] shooters = mount.getVar(SHOOTERS);
				Draw.z(Layer.effect);

				float mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
						mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y);

				float bulletX = mountX + Angles.trnsx(unit.rotation - 90, shootX, shootY),
						bulletY = mountY + Angles.trnsy(unit.rotation - 90, shootX, shootY);

				Draw.color(HPal.matrixNet);
				Draws.drawDiamond(bulletX, bulletY, mount.recoil * 18 + 10, mount.recoil * 4, Time.time * 1.2f);
				Draws.drawDiamond(bulletX, bulletY, mount.recoil * 24 + 12, mount.recoil * 6, -Time.time);

				float lerp = Math.max(mount.recoil, 0.5f * mount.warmup);
				Fill.circle(bulletX, bulletY, 6 * lerp);
				Draw.color(Color.white);
				Fill.circle(bulletX, bulletY, 3 * lerp);

				for (Shooter shooter : shooters) {
					Draw.color(HPal.matrixNet);
					shooter.trail.draw(HPal.matrixNet, 3 * mount.warmup);

					float drawx = unit.x + shooter.x, drawy = unit.y + shooter.y;
					Fill.circle(drawx, drawy, 4 * mount.warmup);
					Lines.stroke(0.65f * mount.warmup);
					Draws.dashCircle(drawx, drawy, 6f * mount.warmup, 4, 180, Time.time);
					Draws.drawDiamond(drawx, drawy, 4 + 8 * mount.warmup, 3 * mount.warmup, Time.time * 1.45f);
					Draws.drawDiamond(drawx, drawy, 8 + 10 * mount.warmup, 3.6f * mount.warmup, -Time.time * 1.45f);

					Lines.stroke(3 * lerp, HPal.matrixNet);
					Lines.line(drawx, drawy, bulletX, bulletY);
					Lines.stroke(1.75f * lerp, Color.white);
					Lines.line(drawx, drawy, bulletX, bulletY);

					Draw.alpha(0.5f);
					Lines.line(mountX, mountY, drawx, drawy);
				}
			}
		});
	}

	public static class Shooter {
		public final Trail trail = new Trail(45);
		public final float[] param = new float[9];

		public float x, y;

		public Shooter() {
			for (int d = 0; d < 3; d++) {
				param[d * 3] = Mathf.random(0.5f, 3f) / (d + 1) * Mathf.randomSign();
				param[d * 3 + 1] = Mathf.random(0f, 360f);
				param[d * 3 + 2] = Mathf.random(18f, 48f) / ((d + 1) * (d + 1));
			}
		}
	}
}
