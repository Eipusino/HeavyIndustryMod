package heavyindustry.content;

import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.audio.HSounds;
import heavyindustry.entities.HEntity;
import heavyindustry.entities.bullet.AccelBulletType;
import heavyindustry.entities.bullet.BlackHoleBulletType;
import heavyindustry.entities.bullet.BoidBulletType;
import heavyindustry.entities.bullet.EffectBulletType;
import heavyindustry.entities.bullet.LightningLinkerBulletType;
import heavyindustry.entities.bullet.ShieldBreakerType;
import heavyindustry.entities.bullet.StrafeLaserBulletType;
import heavyindustry.entities.bullet.TrailFadeBulletType;
import heavyindustry.entities.effect.WrapperEffect;
import heavyindustry.gen.BlackHoleBullet;
import heavyindustry.gen.UltFire;
import heavyindustry.graphics.Drawn;
import heavyindustry.graphics.HLayer;
import heavyindustry.graphics.HPal;
import heavyindustry.graphics.PositionLightning;
import heavyindustry.math.HInterps;
import heavyindustry.util.Constant;
import heavyindustry.util.Utils;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Lightning;
import mindustry.entities.Sized;
import mindustry.entities.Units;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.entities.bullet.FireBulletType;
import mindustry.entities.bullet.FlakBulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.entities.effect.MultiEffect;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;

import static heavyindustry.HVars.MOD_NAME;

/**
 * Some preset bullets. Perhaps it will be used multiple times.
 *
 * @author Eipusino
 */
public final class HBullets {
	public static BulletType
			basicMissile, boidMissile, sapArtilleryFrag, continuousSapLaser,
			heavyArtilleryProjectile,
			ancientBall, ancientStd,
			hitter, ncBlackHole, nuBlackHole, executor,
			ultFireball, basicSkyFrag, annMissile, singularityBulletStrafeLaser, singularityBulletAccel, singularityBulletLightningBall,
			hyperBlast, hyperBlastLinker,
			arc9000frag, arc9000, arc9000hyper,
			collapseFrag, collapse;

	/** Don't let anyone instantiate this class. */
	private HBullets() {}

	/** Instantiates all contents. Called in the main thread in {@code HeavyIndustryMod.loadContent()}. */
	public static void load() {
		basicMissile = new MissileBulletType(4.2f, 15f) {{
			homingPower = 0.12f;
			width = 8f;
			height = 8f;
			shrinkX = shrinkY = 0f;
			drag = -0.003f;
			homingRange = 80f;
			keepVelocity = false;
			splashDamageRadius = 35f;
			splashDamage = 30f;
			lifetime = 62f;
			trailColor = Pal.missileYellowBack;
			hitEffect = Fx.blastExplosion;
			despawnEffect = Fx.blastExplosion;
			weaveScale = 8f;
			weaveMag = 2f;
		}};
		sapArtilleryFrag = new ArtilleryBulletType(2.3f, 30f) {{
			hitEffect = Fx.sapExplosion;
			knockback = 0.8f;
			lifetime = 70f;
			width = height = 20f;
			collidesTiles = false;
			splashDamageRadius = 70f;
			splashDamage = 60f;
			backColor = Pal.sapBulletBack;
			frontColor = lightningColor = Pal.sapBullet;
			lightning = 2;
			lightningLength = 5;
			smokeEffect = Fx.shootBigSmoke2;
			hitShake = 5f;
			lightRadius = 30f;
			lightColor = Pal.sap;
			lightOpacity = 0.5f;
			status = StatusEffects.sapped;
			statusDuration = 60f * 10;
		}};
		boidMissile = new BoidBulletType(2.7f, 30f) {{
			damage = 50;
			homingPower = 0.02f;
			lifetime = 500f;
			keepVelocity = false;
			shootEffect = Fx.shootHeal;
			smokeEffect = Fx.hitLaser;
			hitEffect = despawnEffect = Fx.hitLaser;
			hitSound = Sounds.none;
			healPercent = 5.5f;
			collidesTeam = true;
			trailColor = Pal.heal;
			backColor = Pal.heal;
		}};
		continuousSapLaser = new ContinuousLaserBulletType(60f) {{
			colors = new Color[]{Pal.sapBulletBack.cpy().a(0.3f), Pal.sapBullet.cpy().a(0.6f), Pal.sapBullet, Color.white};
			length = 190f;
			width = 5f;
			shootEffect = HFx.sapPlasmaShoot;
			hitColor = lightColor = lightningColor = Pal.sapBullet;
			hitEffect = HFx.coloredHitSmall;
			status = StatusEffects.sapped;
			statusDuration = 80f;
			lifetime = 180f;
			incendChance = 0f;
			largeHit = false;
		}
			@Override
			public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
				super.hitTile(b, build, x, y, initialHealth, direct);
				if (b.owner instanceof Healthc owner) {
					owner.heal(Math.max(initialHealth - build.health(), 0f) * 0.2f);
				}
			}

			@Override
			public void hitEntity(Bullet b, Hitboxc entity, float health) {
				super.hitEntity(b, entity, health);
				if (entity instanceof Healthc h && b.owner instanceof Healthc owner) {
					owner.heal(Math.max(health - h.health(), 0f) * 0.2f);
				}
			}
		};
		heavyArtilleryProjectile = new ShieldBreakerType(7f, 6000f, "missile-large", 7000f) {{
			backColor = trailColor = lightColor = lightningColor = hitColor = HPal.ancientLightMid;
			frontColor = HPal.ancientLight;
			trailEffect = HFx.hugeTrail;
			trailParam = 6f;
			trailChance = 0.2f;
			trailInterval = 3;
			lifetime = 200f;
			scaleLife = true;
			trailWidth = 5f;
			trailLength = 55;
			trailInterp = Interp.slope;
			lightning = 6;
			lightningLength = lightningLengthRand = 22;
			splashDamage = damage;
			lightningDamage = damage / 15;
			splashDamageRadius = 120;
			scaledSplashDamage = true;
			despawnHit = true;
			collides = false;
			reflectable = false;
			shrinkY = shrinkX = 0.33f;
			width = 17f;
			height = 55f;
			despawnShake = hitShake = 12f;
			hitEffect = new MultiEffect(HFx.square(hitColor, 200f, 20, splashDamageRadius + 80, 10), HFx.lightningHitLarge, HFx.hitSpark(hitColor, 130, 85, splashDamageRadius * 1.5f, 2.2f, 10f), HFx.subEffect(140, splashDamageRadius + 12, 33, 34f, Interp.pow2Out, ((i, x, y, rot, fin) -> {
				float fout = Interp.pow2Out.apply(1 - fin);
				for (int s : Mathf.signs) {
					Drawf.tri(x, y, 12 * fout, 45 * Mathf.curve(fin, 0, 0.1f) * HFx.fout(fin, 0.25f), rot + s * 90);
				}
			})));
			despawnEffect = HFx.circleOut(145f, splashDamageRadius + 15f, 3f);
			shootEffect = WrapperEffect.wrap(HFx.missileShoot, hitColor);//NHFx.blast(hitColor, 45f);
			smokeEffect = HFx.instShoot(hitColor, frontColor);
			despawnSound = hitSound = Sounds.largeExplosion;
			fragBullets = 22;
			fragBullet = new BasicBulletType(2f, 300f, MOD_NAME + "-circle-bolt") {{
				width = height = 10f;
				shrinkY = shrinkX = 0.7f;
				backColor = trailColor = lightColor = lightningColor = hitColor = HPal.ancientLightMid;
				frontColor = HPal.ancientLight;
				trailEffect = Fx.missileTrail;
				trailParam = 3.5f;
				splashDamage = 80f;
				splashDamageRadius = 40f;
				lifetime = 18f;
				lightning = 2;
				lightningLength = lightningLengthRand = 4;
				lightningDamage = 30f;
				hitSoundVolume /= 2.2f;
				despawnShake = hitShake = 4f;
				despawnSound = hitSound = Sounds.dullExplosion;
				trailWidth = 5f;
				trailLength = 35;
				trailInterp = Interp.slope;
				despawnEffect = HFx.blast(hitColor, 40f);
				hitEffect = HFx.hitSparkHuge;
			}};
			fragLifeMax = 5f;
			fragLifeMin = 1.5f;
			fragVelocityMax = 2f;
			fragVelocityMin = 0.35f;
		}};
		ancientBall = new AccelBulletType(2.85f, 240f, "mine-bullet") {{
			frontColor = Color.white;
			backColor = lightningColor = trailColor = hitColor = lightColor = HPal.ancient;
			lifetime = 95f;
			spin = 3f;
			statusDuration = 300f;
			accelerateBegin = 0.15f;
			accelerateEnd = 0.95f;
			despawnSound = hitSound = Sounds.titanExplosion;
			velocityBegin = 8f;
			velocityIncrease = -7.5f;
			collides = false;
			scaleLife = scaledSplashDamage = true;
			despawnHit = true;
			hitShake = despawnShake = 18f;
			lightning = 4;
			lightningCone = 360;
			lightningLengthRand = 12;
			lightningLength = 10;
			width = height = 30;
			shrinkX = shrinkY = 0;
			splashDamageRadius = 120f;
			splashDamage = 800f;
			lightningDamage = damage * 0.85f;
			hitEffect = HFx.hitSparkLarge;
			despawnEffect = HFx.square45_6_45;
			trailEffect = HFx.trailToGray;
			trailLength = 15;
			trailWidth = 5f;
			drawSize = 300f;
			shootEffect = HFx.instShoot(backColor, frontColor);
			smokeEffect = HFx.lightningHitLarge;
			hitEffect = new Effect(90, e -> {
				Draw.color(backColor, frontColor, e.fout() * 0.7f);
				Fill.circle(e.x, e.y, e.fout() * height / 1.25f);
				Lines.stroke(e.fout() * 3f);
				Lines.circle(e.x, e.y, e.fin() * 80);
				Lines.stroke(e.fout() * 2f);
				Lines.circle(e.x, e.y, e.fin() * 50);
				Angles.randLenVectors(e.id, 35, 18 + 100 * e.fin(), (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 12 + 4));

				Draw.color(frontColor);
				Fill.circle(e.x, e.y, e.fout() * height / 1.75f);
			});
			despawnEffect = new MultiEffect(HFx.hitSparkHuge, HFx.instHit(backColor, 3, 120f));
			fragBullets = 3;
			fragBullet = new LaserBulletType(4060f) {{
				length = 460f;
				width = 45f;
				statusDuration = 120f;
				lifetime = 65f;
				splashDamage = 800;
				splashDamageRadius = 120;
				hitShake = 18f;
				lightningSpacing = 35f;
				lightningLength = 8;
				lightningDelay = 1.1f;
				lightningLengthRand = 15;
				lightningDamage = 450;
				lightningAngleRand = 40f;
				scaledSplashDamage = largeHit = true;
				lightningColor = trailColor = hitColor = lightColor = Pal.surge.cpy().lerp(Pal.accent, 0.055f);
				despawnHit = false;
				hitEffect = new Effect(90, 500, e -> {
					Draw.color(backColor, frontColor, e.fout() * 0.7f);
					Fill.circle(e.x, e.y, e.fout() * height / 1.55f);
					Lines.stroke(e.fout() * 3f);
					Lines.circle(e.x, e.y, e.fin(Interp.pow3Out) * 80);
					Angles.randLenVectors(e.id, 18, 18 + 100 * e.fin(), (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 7f));

					Draw.color(frontColor);
					Fill.circle(e.x, e.y, e.fout() * height / 2f);
				});
				sideAngle = 15f;
				sideWidth = 0f;
				sideLength = 0f;
				colors = new Color[]{hitColor.cpy().a(0.2f), hitColor, Color.white};
			}
				@Override
				public void despawned(Bullet b) {}

				@Override
				public void init(Bullet b) {
					Vec2 p = new Vec2().set(HEntity.collideBuildOnLength(b.team, b.x, b.y, length, b.rotation(), Constant.BOOLF_BUILDING_TRUE));

					float resultLength = b.dst(p), rot = b.rotation();

					b.fdata = resultLength;
					laserEffect.at(b.x, b.y, rot, resultLength * 0.75f);

					if (lightningSpacing > 0) {
						int idx = 0;
						for (float i = 0; i <= resultLength; i += lightningSpacing) {
							float cx = b.x + Angles.trnsx(rot, i), cy = b.y + Angles.trnsy(rot, i);

							int f = idx++;

							for (int s : Mathf.signs) {
								Time.run(f * lightningDelay, () -> {
									if (b.isAdded() && b.type == this) {
										Lightning.create(b, lightningColor,
												lightningDamage < 0f ? damage : lightningDamage,
												cx, cy, rot + 90 * s + Mathf.range(lightningAngleRand),
												lightningLength + Mathf.random(lightningLengthRand));
									}
								});
							}
						}
					}
				}

				@Override
				public void draw(Bullet b) {
					float realLength = b.fdata;

					float f = Mathf.curve(b.fin(), 0f, 0.2f);
					float baseLen = realLength * f;
					float cwidth = width;
					//float compound = 1f;

					Tmp.v1.trns(b.rotation(), baseLen);

					for (Color color : colors) {
						Draw.color(color);
						Lines.stroke((cwidth *= lengthFalloff) * b.fout());
						Lines.lineAngle(b.x, b.y, b.rotation(), baseLen, false);

						Fill.circle(Tmp.v1.x + b.x, Tmp.v1.y + b.y, Lines.getStroke() * 2.2f);
						Fill.circle(b.x, b.y, 1f * cwidth * b.fout());
						//compound *= lengthFalloff;
					}
					Draw.reset();
					Drawf.light(b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, width * 1.4f * b.fout(), colors[0], 0.6f);
				}
			};
		}};
		ancientStd = new AccelBulletType(2.85f, 120f) {{
			frontColor = HPal.ancientLight;
			backColor = lightningColor = hitColor = lightColor = HPal.ancient;
			trailColor = HPal.ancientLightMid;
			lifetime = 126f;
			knockback = 2f;
			ammoMultiplier = 8f;
			accelerateBegin = 0.1f;
			accelerateEnd = 0.85f;
			statusDuration = 30f;
			despawnSound = hitSound = Sounds.dullExplosion;
			hitSoundVolume /= 4f;
			velocityBegin = 8f;
			velocityIncrease = -5f;
			homingDelay = 20f;
			homingPower = 0.05f;
			homingRange = 120f;
			despawnHit = pierceBuilding = true;
			hitShake = despawnShake = 5f;
			lightning = 1;
			lightningCone = 360;
			lightningLengthRand = 12;
			lightningLength = 4;
			width = 10f;
			height = 35f;
			pierceCap = 8;
			shrinkX = shrinkY = 0f;
			lightningDamage = damage * 0.85f;
			hitEffect = HFx.hitSparkLarge;
			despawnEffect = HFx.square45_6_45;
			shootEffect = HFx.shootCircleSmall(backColor);
			smokeEffect = HFx.hugeSmokeGray;
			trailEffect = HFx.trailToGray;
			trailLength = 15;
			trailWidth = 2f;
			drawSize = 300f;
		}};
		hitter = new EffectBulletType(15f, 500f, 600f) {{
			speed = 0.001f;
			hittable = false;
			scaledSplashDamage = true;
			collidesTiles = collidesGround = collides = collidesAir = true;
			lightningDamage = 200f;
			lightColor = lightningColor = trailColor = hitColor = HPal.ancient;
			lightning = 5;
			lightningLength = 12;
			lightningLengthRand = 16;
			splashDamageRadius = 60f;
			hitShake = despawnShake = 20f;
			hitSound = despawnSound = Sounds.explosionbig;
			hitEffect = despawnEffect = new MultiEffect(HFx.square45_8_45, HFx.hitSparkHuge, HFx.crossBlast_45);
		}
			@Override
			public void despawned(Bullet b) {
				if (despawnHit) {
					hit(b);
				} else {
					createUnits(b, b.x, b.y);
				}

				if (!fragOnHit) {
					createFrags(b, b.x, b.y);
				}

				despawnEffect.at(b.x, b.y, b.rotation(), lightColor);
				despawnSound.at(b);

				Effect.shake(despawnShake, despawnShake, b);
			}

			@Override
			public void hit(Bullet b, float x, float y) {
				hitEffect.at(x, y, b.rotation(), lightColor);
				hitSound.at(x, y, hitSoundPitch, hitSoundVolume);

				Effect.shake(hitShake, hitShake, b);

				if (fragOnHit) {
					createFrags(b, x, y);
				}
				createPuddles(b, x, y);
				createIncend(b, x, y);
				createUnits(b, x, y);

				if (suppressionRange > 0) {
					//bullets are pooled, require separate Vec2 instance
					Damage.applySuppression(b.team, b.x, b.y, suppressionRange, suppressionDuration, 0f, suppressionEffectChance, new Vec2(b.x, b.y));
				}

				createSplashDamage(b, x, y);

				for (int i = 0; i < lightning; i++) {
					Lightning.create(b, lightColor, lightningDamage < 0 ? damage : lightningDamage, b.x, b.y, b.rotation() + Mathf.range(lightningCone / 2) + lightningAngle, lightningLength + Mathf.random(lightningLengthRand));
				}
			}
		};
		ncBlackHole = new BlackHoleBulletType(120f, 10000f, 3800f) {{
			despawnHit = true;
			splashDamageRadius = 240f;
			hittable = false;
			lightColor = HPal.ancient;
			lightningDamage = 2000f;
			lightning = 2;
			lightningLength = 4;
			lightningLengthRand = 8;
			scaledSplashDamage = true;
			collidesAir = collidesGround = collidesTiles = true;
		}
			@Override
			public void despawned(Bullet b) {
				if (despawnHit) {
					hit(b);
				} else {
					createUnits(b, b.x, b.y);
				}

				if (!fragOnHit) {
					createFrags(b, b.x, b.y);
				}

				despawnEffect.at(b.x, b.y, b.rotation(), hitColor);
				despawnSound.at(b);

				Effect.shake(despawnShake, despawnShake, b);

				float rad = 33f;

				Vec2 v = new Vec2().set(b);

				for (int i = 0; i < 5; i++) {
					Time.run(i * 0.35f + Mathf.random(2), () -> {
						Tmp.v1.rnd(rad / 3f).scl(Mathf.random());
						HFx.shuttle.at(v.x + Tmp.v1.x, v.y + Tmp.v1.y, Tmp.v1.angle(), lightColor, Mathf.random(rad * 3f, rad * 12f));
					});
				}

				if (b instanceof BlackHoleBullet hb) {
					Entityc o = b.owner();

					for (Sized s : hb.sizeds) {
						if (s == null) continue;

						float size = Math.min(s.hitSize(), 85);
						Time.run(Mathf.random(44), () -> {
							if (Mathf.chance(0.32) || hb.sizeds.size < 8)
								HFx.shuttle.at(s.getX(), s.getY(), 45, lightColor, Mathf.random(size * 3f, size * 12f));
							hitTile(s, o, b.team, s.getX(), s.getY());
						});
					}
				}

				createSplashDamage(b, b.x, b.y);
			}

			@Override
			public void init(Bullet b) {
				super.init(b);

				HFx.circleOut.at(b.x, b.y, b.fdata * 1.25f, lightColor);
			}
		};
		nuBlackHole = new BlackHoleBulletType(20f, 10000f, 0f) {{
			despawnHit = true;
			splashDamageRadius = 36f;
			hittable = false;
			lightColor = hitColor = HPal.ancient;
			lightningDamage = 2000f;
			lightning = 2;
			lightningLength = 4;
			lightningLengthRand = 8;
			scaledSplashDamage = true;
			collidesAir = collidesGround = collidesTiles = true;
		}};
		ultFireball = new FireBulletType(1f, 10) {{
			colorFrom = colorMid = Pal.techBlue;
			lifetime = 12f;
			radius = 4f;
			trailEffect = HFx.ultFireBurn;
		}
			@Override
			public void draw(Bullet b) {
				Draw.color(colorFrom, colorMid, colorTo, b.fin());
				Fill.square(b.x, b.y, radius * b.fout(), 45);
				Draw.reset();
			}

			@Override
			public void update(Bullet b) {
				if (Mathf.chanceDelta(fireTrailChance)) {
					UltFire.create(b.tileOn());
				}

				if (Mathf.chanceDelta(fireEffectChance)) {
					trailEffect.at(b.x, b.y);
				}

				if (Mathf.chanceDelta(fireEffectChance2)) {
					trailEffect2.at(b.x, b.y);
				}
			}
		};
		executor = new TrailFadeBulletType(28f, 1800f) {{
			lifetime = 40f;
			trailLength = 90;
			trailWidth = 3.6F;
			tracers = 2;
			tracerFadeOffset = 20;
			keepVelocity = true;
			tracerSpacing = 10f;
			tracerUpdateSpacing *= 1.25f;
			removeAfterPierce = false;
			hitColor = backColor = lightColor = lightningColor = HPal.ancient;
			trailColor = HPal.ancientLightMid;
			frontColor = HPal.ancientLight;
			width = 18f;
			height = 60f;
			homingPower = 0.01f;
			homingRange = 300f;
			homingDelay = 5f;
			hitSound = Sounds.plasmaboom;
			despawnShake = hitShake = 18f;
			statusDuration = 1200f;
			pierce = pierceArmor = pierceBuilding = true;
			lightning = 3;
			lightningLength = 6;
			lightningLengthRand = 18;
			lightningDamage = 400;
			smokeEffect = WrapperEffect.wrap(HFx.hitSparkHuge, hitColor);
			shootEffect = HFx.instShoot(backColor, frontColor);
			despawnEffect = HFx.lightningHitLarge;
			hitEffect = new MultiEffect(HFx.hitSpark(backColor, 75f, 24, 90f, 2f, 12f), HFx.square45_6_45, HFx.lineCircleOut(backColor, 18f, 20, 2), HFx.sharpBlast(backColor, frontColor, 120f, 40f));
			hittable = false;
			absorbable = false;
			reflectable = false;
		}
			@Override
			public void createFrags(Bullet b, float x, float y) {
				super.createFrags(b, x, y);
				HBullets.nuBlackHole.create(b, x, y, 0);
			}
		};
		basicSkyFrag = new BasicBulletType(3.8f, 50) {{
			speed = 6f;
			trailLength = 12;
			trailWidth = 2f;
			lifetime = 60;
			despawnEffect = HFx.square45_4_45;
			knockback = 4f;
			width = 15f;
			height = 37f;
			lightningDamage = damage * 0.65f;
			backColor = lightColor = lightningColor = trailColor = hitColor = frontColor = Pal.techBlue;
			lightning = 2;
			lightningLength = lightningLengthRand = 3;
			smokeEffect = Fx.shootBigSmoke2;
			trailChance = 0.2f;
			trailEffect = HFx.skyTrail;
			drag = 0.015f;
			hitShake = 2f;
			hitSound = Sounds.explosion;
			hitEffect = new Effect(45f, e -> {
				Fx.rand.setSeed(e.id);
				Draw.color(lightColor, e.fin());
				Lines.stroke(1.75f * e.fout());
				Lines.spikes(e.x, e.y, Fx.rand.random(14, 28) * e.finpow(), Fx.rand.random(1, 5) * e.fout() + Fx.rand.random(5, 8) * e.fin(HInterps.parabola4Reversed), 4, 45);
				Lines.square(e.x, e.y, Fx.rand.random(4, 14) * e.fin(Interp.pow3Out), 45);
			});
		}
			@Override
			public void hit(Bullet b) {
				super.hit(b);
				UltFire.createChance(b, 12, 0.0075f);
			}
		};
		annMissile = new BasicBulletType(5.6f, 80f, MOD_NAME + "-strike") {{
			trailColor = lightningColor = backColor = lightColor = frontColor = Pal.techBlue;
			lightning = 3;
			lightningCone = 360;
			lightningLengthRand = lightningLength = 9;
			splashDamageRadius = 60;
			splashDamage = lightningDamage = damage * 0.7f;
			range = 320f;
			scaleLife = true;
			width = 12f;
			height = 30f;
			trailLength = 15;
			drawSize = 250f;
			trailParam = 1.4f;
			trailChance = 0.35f;
			lifetime = 50f;
			homingDelay = 10f;
			homingPower = 0.05f;
			homingRange = 150f;
			hitEffect = HFx.lightningHitLarge(lightColor);
			shootEffect = HFx.hugeSmokeGray;
			smokeEffect = new Effect(45f, e -> {
				Draw.color(lightColor, Color.white, e.fout() * 0.7f);
				Angles.randLenVectors(e.id, 8, 5 + 55 * e.fin(), e.rotation, 45, (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 3f));
			});
			despawnEffect = new Effect(32f, e -> {
				Draw.color(Color.gray);
				Angles.randLenVectors(e.id + 1, 8, 2f + 30f * e.finpow(), (x, y) -> Fill.circle(e.x + x, e.y + y, e.fout() * 4f + 0.5f));
				Draw.color(lightColor, Color.white, e.fin());
				Lines.stroke(e.fout() * 2);
				Fill.circle(e.x, e.y, e.fout() * e.fout() * 13);
				Angles.randLenVectors(e.id, 4, 7 + 40 * e.fin(), (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fslope() * 8 + 3));
			});
		}};
		singularityBulletAccel = new AccelBulletType(2f, 180f) {{
			width = 22f;
			height = 40f;
			velocityBegin = 1f;
			velocityIncrease = 11f;
			accelInterp = HInterps.inOut;
			accelerateBegin = 0.045f;
			accelerateEnd = 0.675f;
			pierceCap = 3;
			splashDamage = damage / 4f;
			splashDamageRadius = 24f;
			trailLength = 30;
			trailWidth = 3f;
			lifetime = 160f;
			trailEffect = HFx.trailFromWhite;
			pierceArmor = true;
			trailRotation = false;
			trailChance = 0.35f;
			trailParam = 4f;
			homingRange = 640f;
			homingPower = 0.075f;
			homingDelay = 5f;
			lightning = 3;
			lightningLengthRand = 10;
			lightningLength = 5;
			lightningDamage = damage / 4f;
			shootEffect = smokeEffect = Fx.none;
			hitEffect = despawnEffect = new MultiEffect(new Effect(65f, b -> {
				Draw.color(b.color);
				Fill.circle(b.x, b.y, 6f * b.fout(Interp.pow3Out));
				Angles.randLenVectors(b.id, 6, 35f * b.fin() + 5f, (x, y) -> Fill.circle(b.x + x, b.y + y, 4f * b.fout(Interp.pow2Out)));
			}), HFx.hitSparkLarge);
			despawnHit = false;
			rangeOverride = 480f;
		}
			@Override
			public void updateTrailEffects(Bullet b) {
				if (trailChance > 0f) {
					if (Mathf.chanceDelta(trailChance)) {
						trailEffect.at(b.x, b.y, trailRotation ? b.rotation() : trailParam, b.team.color);
					}
				}

				if (trailInterval > 0f) {
					if (b.timer(0, trailInterval)) {
						trailEffect.at(b.x, b.y, trailRotation ? b.rotation() : trailParam, b.team.color);
					}
				}
			}

			@Override
			public void hit(Bullet b, float x, float y) {
				b.hit = true;
				hitEffect.at(x, y, b.rotation(), b.team.color);
				hitSound.at(x, y, hitSoundPitch, hitSoundVolume);

				Effect.shake(hitShake, hitShake, b);

				if (splashDamageRadius > 0f && !b.absorbed) {
					Damage.damage(b.team, x, y, splashDamageRadius, splashDamage * b.damageMultiplier(), collidesAir, collidesGround);

					if (status != StatusEffects.none) {
						Damage.status(b.team, x, y, splashDamageRadius, status, statusDuration, collidesAir, collidesGround);
					}
				}

				for (int i = 0; i < lightning; i++) Lightning.create(b, b.team.color, lightningDamage < 0f ? damage : lightningDamage, b.x, b.y, b.rotation() + Mathf.range(lightningCone/2) + lightningAngle, lightningLength + Mathf.random(lightningLengthRand));

				if (!(b.owner instanceof Unit from)) return;
				if (from.dead || !from.isAdded() || from.healthf() > 0.99f) return;
				HFx.chainLightningFade.at(b.x, b.y, Mathf.random(12, 20), b.team.color, from);
				from.heal(damage / 8f);
			}

			@Override
			public void despawned(Bullet b) {
				despawnEffect.at(b.x, b.y, b.rotation(), b.team.color);
				Effect.shake(despawnShake, despawnShake, b);
			}

			@Override
			public void removed(Bullet b) {
				if (trailLength > 0 && b.trail != null && b.trail.size() > 0) {
					Fx.trailFade.at(b.x, b.y, trailWidth, b.team.color, b.trail.copy());
				}
			}

			@Override
			public void init(Bullet b) {
				super.init(b);

				b.vel.rotate(Utils.rand(b.id).random(360));
			}

			@Override
			public void draw(Bullet b) {
				Tmp.c1.set(b.team.color).lerp(Color.white, Mathf.absin(4f, 0.3f));

				if (trailLength > 0 && b.trail != null) {
					float z = Draw.z();
					Draw.z(z - 0.01f);
					b.trail.draw(Tmp.c1, trailWidth);
					Draw.z(z);
				}

				Draw.color(b.team.color, Color.white, 0.35f);
				Drawn.arrow(b.x, b.y, 5f, 35f, -6f, b.rotation());
				Draw.color(Tmp.c1);
				Drawn.arrow(b.x, b.y, 5f, 35f, 12f, b.rotation());

				Draw.reset();
			}
		};
		singularityBulletLightningBall = new LightningLinkerBulletType(3f, 120f) {{
			lifetime = 120f;
			keepVelocity = false;
			lightningDamage = damage = splashDamage = 80f;
			splashDamageRadius = 50f;
			homingDelay = 20f;
			homingRange = 300f;
			homingPower = 0.025f;
			smokeEffect = shootEffect = Fx.none;
			effectLingtning = 0;
			maxHit = 6;
			hitShake = despawnShake = 5f;
			hitSound = despawnSound = Sounds.plasmaboom;
			size = 7.2f;
			trailWidth = 3f;
			trailLength = 16;
			linkRange = 80f;
			scaleLife = false;
			despawnHit = false;
			collidesAir = collidesGround = true;
			despawnEffect = hitEffect = new MultiEffect(HFx.lightningHitLarge, HFx.hitSparkHuge);
			trailEffect = slopeEffect = HFx.trailFromWhite;
			spreadEffect = new Effect(32f, e -> Angles.randLenVectors(e.id, 2, 6 + 45 * e.fin(), (x, y) -> {
				Draw.color(e.color);
				Fill.circle(e.x + x, e.y + y, e.fout() * size / 2f);
				Draw.color(Color.black);
				Fill.circle(e.x + x, e.y + y, e.fout() * (size / 3f - 1f));
			})).layer(Layer.effect + 0.00001f);
		}
			public final Effect RshootEffect = new Effect(24f, e -> {
				e.scaled(10f, (b) -> {
					Draw.color(e.color);
					Lines.stroke(b.fout() * 3f + 0.2f);
					Lines.circle(b.x, b.y, b.fin() * 70f);
				});
				Draw.color(e.color);

				for (int i : Mathf.signs) {
					Drawn.tri(e.x, e.y, 8f * e.fout(), 85f, e.rotation + 90f * i);
				}

				Draw.color(Color.black);

				for (int i : Mathf.signs) {
					Drawn.tri(e.x, e.y, 3f * e.fout(), 38f, e.rotation + 90f * i);
				}
			});

			public final Effect RsmokeEffect = HFx.hitSparkLarge;

			public Color getColor(Bullet b) {
				return Tmp.c1.set(b.team.color).lerp(Color.white, 0.1f + Mathf.absin(4f, 0.15f));
			}

			@Override
			public void update(Bullet b) {
				updateTrail(b);
				updateHoming(b);
				updateWeaving(b);
				updateBulletInterval(b);

				Effect.shake(hitShake, hitShake, b);
				if (b.timer(5, hitSpacing)) {
					slopeEffect.at(b.x + Mathf.range(size / 4f), b.y + Mathf.range(size / 4f), Mathf.random(2f, 4f), b.team.color);
					spreadEffect.at(b.x, b.y, b.team.color);
					PositionLightning.createRange(b, collidesAir, collidesGround, b, b.team, linkRange, maxHit, b.team.color, Mathf.chanceDelta(randomLightningChance), lightningDamage, lightningLength, PositionLightning.WIDTH, boltNum, p -> liHitEffect.at(p.getX(), p.getY(), b.team.color));
				}

				if (Mathf.chanceDelta(0.1)) {
					slopeEffect.at(b.x + Mathf.range(size / 4f), b.y + Mathf.range(size / 4f), Mathf.random(2f, 4f), b.team.color);
					spreadEffect.at(b.x, b.y, b.team.color);
				}

				if (randomGenerateRange > 0f && Mathf.chance(Time.delta * randomGenerateChance) && b.lifetime - b.time > PositionLightning.lifetime) PositionLightning.createRandomRange(b, b.team, b, randomGenerateRange, backColor, Mathf.chanceDelta(randomLightningChance), 0, 0, boltWidth, boltNum, randomLightningNum, hitPos -> {
					randomGenerateSound.at(hitPos, Mathf.random(0.9f, 1.1f));
					Damage.damage(b.team, hitPos.getX(), hitPos.getY(), splashDamageRadius / 8, splashDamage * b.damageMultiplier() / 8, collidesAir, collidesGround);
					HFx.lightningHitLarge.at(hitPos.getX(), hitPos.getY(), b.team.color);

					hitModifier.get(hitPos);
				});

				if (Mathf.chanceDelta(effectLightningChance) && b.lifetime - b.time > Fx.chainLightning.lifetime) {
					for (int i = 0; i < effectLingtning; i++) {
						Vec2 v = randVec.rnd(effectLightningLength + Mathf.random(effectLightningLengthRand)).add(b).add(Tmp.v1.set(b.vel).scl(Fx.chainLightning.lifetime / 2));
						Fx.chainLightning.at(b.x, b.y, 12f, b.team.color, v.cpy());
						HFx.lightningHitSmall.at(v.x, v.y, 20f, b.team.color);
					}
				}
			}

			@Override
			public void init(Bullet b) {
				super.init(b);

				b.lifetime *= Mathf.randomSeed(b.id, 0.875f , 1.125f);

				RsmokeEffect.at(b.x, b.y, b.team.color);
				RshootEffect.at(b.x, b.y, b.rotation(), b.team.color);
			}

			@Override
			public void drawTrail(Bullet b) {
				if (trailLength > 0 && b.trail != null) {
					float z = Draw.z();
					Draw.z(z - 0.0001f);
					b.trail.draw(getColor(b), trailWidth);
					Draw.z(z);
				}
			}

			@Override
			public void draw(Bullet b) {
				drawTrail(b);

				Draw.color(Tmp.c1);
				Fill.circle(b.x, b.y, size);

				float[] param = {
						9f, 28f, 1f,
						9f, 22f, -1.25f,
						12f, 16f, -0.45f,
				};

				for (int i = 0; i < param.length / 3; i++) {
					for (int j : Mathf.signs) {
						Drawf.tri(b.x, b.y, param[i * 3] * b.fout(), param[i * 3 + 1] * b.fout(), b.rotation() + 90f * j + param[i * 3 + 2] * Time.time);
					}
				}

				Draw.color(Color.black);

				Fill.circle(b.x, b.y, size / 6.125f + size / 3 * Mathf.curve(b.fout(), 0.1f, 0.35f));

				Drawf.light(b.x, b.y, size * 6.85f, b.team.color, 0.7f);
			}

			@Override
			public void despawned(Bullet b) {
				PositionLightning.createRandomRange(b, b.team, b, randomGenerateRange, b.team.color, Mathf.chanceDelta(randomLightningChance), 0, 0, boltWidth, boltNum, randomLightningNum, hitPos -> {
					Damage.damage(b.team, hitPos.getX(), hitPos.getY(), splashDamageRadius, splashDamage * b.damageMultiplier(), collidesAir, collidesGround);
					HFx.lightningHitLarge.at(hitPos.getX(), hitPos.getY(), b.team.color);
					liHitEffect.at(hitPos);
					for (int j = 0; j < lightning; j++) {
						Lightning.create(b, b.team.color, lightningDamage < 0f ? damage : lightningDamage, b.x, b.y, b.rotation() + Mathf.range(lightningCone / 2f) + lightningAngle, lightningLength + Mathf.random(lightningLengthRand));
					}
					hitSound.at(hitPos, Mathf.random(0.9f, 1.1f));

					hitModifier.get(hitPos);
				});

				if (despawnHit) {
					hit(b);
				} else {
					createUnits(b, b.x, b.y);
				}

				if (!fragOnHit) {
					createFrags(b, b.x, b.y);
				}

				despawnEffect.at(b.x, b.y, b.rotation(), b.team.color);
				despawnSound.at(b);

				Effect.shake(despawnShake, despawnShake, b);
			}

			@Override
			public void hit(Bullet b, float x, float y) {
				hitEffect.at(x, y, b.rotation(), b.team.color);
				hitSound.at(x, y, hitSoundPitch, hitSoundVolume);

				Effect.shake(hitShake, hitShake, b);

				if (fragOnHit) {
					createFrags(b, x, y);
				}
				createPuddles(b, x, y);
				createIncend(b, x, y);
				createUnits(b, x, y);

				if (suppressionRange > 0) {
					//bullets are pooled, require separate Vec2 instance
					Damage.applySuppression(b.team, b.x, b.y, suppressionRange, suppressionDuration, 0f, suppressionEffectChance, new Vec2(b.x, b.y));
				}

				createSplashDamage(b, x, y);

				for (int i = 0; i < lightning; i++) {
					Lightning.create(b, b.team.color, lightningDamage < 0 ? damage : lightningDamage, b.x, b.y, b.rotation() + Mathf.range(lightningCone/2) + lightningAngle, lightningLength + Mathf.random(lightningLengthRand));
				}
			}

			@Override
			public void removed(Bullet b) {
				if (trailLength > 0 && b.trail != null && b.trail.size() > 0) {
					Fx.trailFade.at(b.x, b.y, trailWidth, b.team.color, b.trail.copy());
				}
			}
		};
		singularityBulletStrafeLaser = new StrafeLaserBulletType(0f, 300f) {{
			strafeAngle = 0;
		}
			public void init(Bullet b) {
				super.init(b);
				Sounds.laserblast.at(b);
			}

			public void hit(Bullet b, float x, float y) {
				super.hit(b, x, y);
				if (b.owner instanceof Unit from) {
					if (from.dead || !from.isAdded() || from.healthf() > 0.99f) return;

					from.heal(damage / 20f);
					if (Vars.headless) return;

					PositionLightning.createEffect(b, from, b.team.color, 2, Mathf.random(1.5f, 3f));
				}
			}

			public void draw(Bullet b) {
				Tmp.c1.set(b.team.color).lerp(Color.white, Mathf.absin(4f, 0.1f));
				super.draw(b);
				Draw.z(110f);
				float fout = b.fout(0.25f) * Mathf.curve(b.fin(), 0f, 0.125f);
				Draw.color(Tmp.c1);
				Fill.circle(b.x, b.y, width / 1.225f * fout);
				if (b.owner instanceof Unit unit) {
					if (!unit.dead) {
						Draw.z(100f);
						Lines.stroke((width / 3f + Mathf.absin(Time.time, 4f, 0.8f)) * fout);
						Lines.line(b.x, b.y, unit.x, unit.y, false);
					}
				}

				for (int i : Mathf.signs) {
					Drawn.tri(b.x, b.y, 6f * fout, 10f + 50f * fout, Time.time * 1.5f + (float) (90 * i));
					Drawn.tri(b.x, b.y, 6f * fout, 20f + 60f * fout, Time.time * -1f + (float) (90 * i));
				}

				Draw.z(110.001f);
				Draw.color(b.team.color, Color.white, 0.25f);
				Fill.circle(b.x, b.y, width / 1.85f * fout);
				Draw.color(Color.black);
				Fill.circle(b.x, b.y, width / 2.155f * fout);
				Draw.z(100f);
				Draw.reset();
				float rotation = dataRot ? b.fdata : b.rotation() + getRotation(b);
				float maxRangeFout = maxRange * fout;
				float realLength = HEntity.findLaserLength(b, rotation, maxRangeFout);
				Tmp.v1.trns(rotation, realLength);
				Tmp.v2.trns(rotation, 0f, width / 2f * fout);
				Tmp.v3.setZero();
				if (realLength < maxRangeFout) {
					Tmp.v3.set(Tmp.v2).scl((maxRangeFout - realLength) / maxRangeFout);
				}

				Draw.color(Tmp.c1);
				Tmp.v2.scl(0.9f);
				Tmp.v3.scl(0.9f);
				Fill.quad(b.x - Tmp.v2.x, b.y - Tmp.v2.y, b.x + Tmp.v2.x, b.y + Tmp.v2.y, b.x + Tmp.v1.x + Tmp.v3.x, b.y + Tmp.v1.y + Tmp.v3.y, b.x + Tmp.v1.x - Tmp.v3.x, b.y + Tmp.v1.y - Tmp.v3.y);
				if (realLength < maxRangeFout) {
					Fill.circle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, Tmp.v3.len());
				}

				Tmp.v2.scl(1.2f);
				Tmp.v3.scl(1.2f);
				Draw.alpha(0.5f);
				Fill.quad(b.x - Tmp.v2.x, b.y - Tmp.v2.y, b.x + Tmp.v2.x, b.y + Tmp.v2.y, b.x + Tmp.v1.x + Tmp.v3.x, b.y + Tmp.v1.y + Tmp.v3.y, b.x + Tmp.v1.x - Tmp.v3.x, b.y + Tmp.v1.y - Tmp.v3.y);
				if (realLength < maxRangeFout) {
					Fill.circle(b.x + Tmp.v1.x, b.y + Tmp.v1.y, Tmp.v3.len());
				}

				Draw.alpha(1f);
				Draw.color(Color.black);
				Draw.z(Draw.z() + 0.01f);
				Tmp.v2.scl(0.5f);
				Fill.quad(b.x - Tmp.v2.x, b.y - Tmp.v2.y, b.x + Tmp.v2.x, b.y + Tmp.v2.y, b.x + (Tmp.v1.x + Tmp.v3.x) / 3f, b.y + (Tmp.v1.y + Tmp.v3.y) / 3f, b.x + (Tmp.v1.x - Tmp.v3.x) / 3f, b.y + (Tmp.v1.y - Tmp.v3.y) / 3f);
				Drawf.light(b.x, b.y, b.x + Tmp.v1.x, b.y + Tmp.v1.y, width * 1.5f, getColor(b), 0.7f);
				Draw.reset();
				Draw.z(Draw.z() - 0.01f);
			}
		};
		hyperBlast = new BasicBulletType(3.3f, 400) {{
			lifetime = 60;
			trailLength = 15;
			drawSize = 250f;
			drag = 0.0075f;
			despawnEffect = hitEffect = HFx.lightningHitLarge(Pal.techBlue);
			knockback = 12f;
			width = 15f;
			height = 37f;
			splashDamageRadius = 40f;
			splashDamage = lightningDamage = damage * 0.75f;
			backColor = lightColor = lightningColor = trailColor = Pal.techBlue;
			frontColor = Color.white;
			lightning = 3;
			lightningLength = 8;
			smokeEffect = Fx.shootBigSmoke2;
			trailChance = 0.6f;
			trailEffect = HFx.trailToGray;
			hitShake = 3f;
			hitSound = Sounds.plasmaboom;
		}};
		hyperBlastLinker = new LightningLinkerBulletType(5f, 220f) {{
			effectLightningChance = 0.15f;
			backColor = trailColor = lightColor = lightningColor = hitColor = Pal.techBlue;
			size = 8f;
			frontColor = Pal.techBlue.cpy().lerp(Color.white, 0.25f);
			range = 200f;
			trailWidth = 8f;
			trailLength = 20;
			linkRange = 280f;
			maxHit = 8;
			drag = 0.085f;
			hitSound = Sounds.explosionbig;
			splashDamageRadius = 120f;
			splashDamage = lightningDamage = damage / 4f;
			lifetime = 50f;
			scaleLife = false;
			hittable = false;
			absorbable = false;
			despawnEffect = HFx.lightningHitLarge(hitColor);
			hitEffect = new MultiEffect(HFx.hitSpark(backColor, 65f, 22, splashDamageRadius, 4, 16), HFx.blast(backColor, splashDamageRadius / 2f));
			shootEffect = HFx.hitSpark(backColor, 45f, 12, 60, 3, 8);
			smokeEffect = HFx.hugeSmokeGray;
		}};
		arc9000frag = new FlakBulletType(3.75f, 200) {{
			trailColor = lightColor = lightningColor = backColor = frontColor = Pal.techBlue;
			trailLength = 14;
			trailWidth = 2.7f;
			trailRotation = true;
			trailInterval = 3;
			trailEffect = HFx.polyTrail(backColor, frontColor, 4.65f, 22f);
			trailChance = 0f;
			hitColor = Pal.techBlue;
			despawnEffect = hitEffect = HFx.techBlueExplosion;
			knockback = 12f;
			lifetime = 90f;
			width = 17f;
			height = 42f;
			reflectable = false;
			hittable = false;
			absorbable = false;
			collidesTiles = false;
			splashDamageRadius = 60f;
			splashDamage = damage * 0.6f;
			lightning = 3;
			lightningLength = 8;
			smokeEffect = Fx.shootBigSmoke2;
			hitShake = 8f;
			hitSound = Sounds.plasmaboom;
			status = StatusEffects.shocked;
		}};
		arc9000 = new LightningLinkerBulletType(2.75f, 200) {{
			trailWidth = 4.5f;
			trailLength = 66;
			chargeEffect = new MultiEffect(HFx.techBlueCharge, HFx.techBlueChargeBegin);
			spreadEffect = slopeEffect = Fx.none;
			trailEffect = HFx.hitSparkHuge;
			trailInterval = 5;
			backColor = trailColor = hitColor = lightColor = lightningColor = frontColor = Pal.techBlue;
			randomGenerateRange = 340f;
			randomLightningNum = 3;
			linkRange = 280f;
			range = 800f;
			drawSize = 500f;
			drag = 0.0035f;
			fragLifeMin = 0.3f;
			fragLifeMax = 1f;
			fragVelocityMin = 0.3f;
			fragVelocityMax = 1.25f;
			fragBullets = 14;
			intervalBullets = 2;
			intervalBullet = fragBullet = arc9000frag;
			hitSound = Sounds.explosionbig;
			splashDamageRadius = 120f;
			splashDamage = 1000;
			lightningDamage = 375f;
			hittable = false;
			collidesTiles = true;
			reflectable = false;
			pierce = false;
			collides = false;
			absorbable = false;
			ammoMultiplier = 1f;
			lifetime = 300;
			despawnEffect = HFx.circleOut(hitColor, splashDamageRadius * 1.5f);
			hitEffect = HFx.largeTechBlueHit;
			shootEffect = HFx.techBlueShootBig;
			smokeEffect = HFx.techBlueSmokeBig;
			hitSpacing = 3;
		}
			@Override
			public void update(Bullet b) {
				super.update(b);

				if (b.timer(1, 6)) for (int j = 0; j < 2; j++) {
					Drawn.randFadeLightningEffect(b.x, b.y, Mathf.random(360), Mathf.random(7, 12), backColor, Mathf.chance(0.5));
				}
			}

			@Override
			public void draw(Bullet b) {
				Draw.color(backColor);
				Drawn.surround(b.id, b.x, b.y, size * 1.45f, 14, 7, 11, (b.fin(HInterps.parabola4Reversed) + 1f) / 2 * b.fout(0.1f));

				drawTrail(b);

				Draw.color(backColor);
				Fill.circle(b.x, b.y, size);

				Draw.z(HLayer.effectMask);
				Draw.color(frontColor);
				Fill.circle(b.x, b.y, size * 0.62f);
				Draw.z(HLayer.effectBottom);
				Draw.color(frontColor);
				Fill.circle(b.x, b.y, size * 0.66f);
				Draw.z(Layer.bullet);

				Drawf.light(b.x, b.y, size * 1.85f, backColor, 0.7f);
			}
		};
		arc9000hyper = new AccelBulletType(10f, 1000f) {{
			drawSize = 1200f;
			width = height = shrinkX = shrinkY = 0;
			collides = false;
			despawnHit = false;
			reflectable = false;
			collidesAir = collidesGround = collidesTiles = true;
			splashDamage = 4000f;
			velocityBegin = 6f;
			velocityIncrease = -5.9f;
			accelerateEnd = 0.75f;
			accelerateBegin = 0.1f;
			accelInterp = Interp.pow2;
			trailInterp = Interp.pow10Out;
			despawnSound = Sounds.plasmaboom;
			hitSound = Sounds.explosionbig;
			hitShake = 60;
			despawnShake = 100;
			lightning = 12;
			lightningDamage = 2000f;
			lightningLength = 50;
			lightningLengthRand = 80;
			fragBullets = 1;
			fragBullet = arc9000;
			fragVelocityMin = 0.4f;
			fragVelocityMax = 0.6f;
			fragLifeMin = 0.5f;
			fragLifeMax = 0.7f;
			trailWidth = 12F;
			trailLength = 120;
			ammoMultiplier = 1;
			hittable = false;
			absorbable = false;
			scaleLife = true;
			splashDamageRadius = 400f;
			hitColor = lightColor = lightningColor = trailColor = Pal.techBlue;
			Effect effect = HFx.crossBlast(hitColor, 420f, 45);
			effect.lifetime += 180f;
			despawnEffect = HFx.circleOut(hitColor, splashDamageRadius);
			hitEffect = new MultiEffect(HFx.blast(hitColor, 200f), new Effect(180f, 600f, e -> {
				float rad = 120f;

				float f = (e.fin(Interp.pow10Out) + 8) / 9 * Mathf.curve(Interp.slowFast.apply(e.fout(0.75f)), 0f, 0.85f);

				Draw.alpha(0.9f * e.foutpowdown());
				Draw.color(Color.white, e.color, e.fin() + 0.6f);
				Fill.circle(e.x, e.y, rad * f);

				e.scaled(45f, i -> {
					Lines.stroke(7f * i.fout());
					Lines.circle(i.x, i.y, rad * 3f * i.finpowdown());
					Lines.circle(i.x, i.y, rad * 2f * i.finpowdown());
				});

				Draw.color(Color.white);
				Fill.circle(e.x, e.y, rad * f * 0.75f);

				Drawf.light(e.x, e.y, rad * f * 2f, Draw.getColor(), 0.7f);
			}).layer(Layer.effect + 0.001f), effect, new Effect(260f, 460f, e -> {
				Draw.blend(Blending.additive);
				Draw.z(Layer.flyingUnit - 0.8f);
				float radius = e.fin(Interp.pow3Out) * 230f;
				Fill.light(e.x, e.y, Lines.circleVertices(radius), radius, Color.clear, Tmp.c1.set(Pal.techBlue).a(e.fout(Interp.pow10Out)));
				Draw.blend();
			}));
		}
			@Override
			public void draw(Bullet b) {
				super.draw(b);

				Draw.color(Pal.techBlue, Color.white, b.fout() * 0.25f);

				float extend = Mathf.curve(b.fin(Interp.pow10Out), 0.075f, 1f);

				float chargeCircleFrontRad = 20;
				float width = chargeCircleFrontRad * 1.2f;
				Fill.circle(b.x, b.y, width * (b.fout() + 4) / 3.5f);

				float rotAngle = b.fdata;

				for (int i : Mathf.signs) {
					Drawn.tri(b.x, b.y, width * b.foutpowdown(), 200 + 570 * extend, rotAngle + 90 * i - 45);
				}

				for (int i : Mathf.signs) {
					Drawn.tri(b.x, b.y, width * b.foutpowdown(), 200 + 570 * extend, rotAngle + 90 * i + 45);
				}

				float cameraFin = (1 + 2 * Drawn.cameraDstScl(b.x, b.y, Vars.mobile ? 200 : 320)) / 3f;
				float triWidth = b.fout() * chargeCircleFrontRad * cameraFin;

				for (int i : Mathf.signs) {
					Fill.tri(b.x, b.y + triWidth, b.x, b.y - triWidth, b.x + i * cameraFin * chargeCircleFrontRad * (23 + Mathf.absin(10f, 0.75f)) * (b.fout() * 1.25f + 1f), b.y);
				}

				float rad = splashDamageRadius * b.fin(Interp.pow5Out) * Interp.circleOut.apply(b.fout(0.15f));
				Lines.stroke(8f * b.fin(Interp.pow2Out));
				Lines.circle(b.x, b.y, rad);

				Draw.color(Color.white);
				Fill.circle(b.x, b.y, width * (b.fout() + 4) / 5.5f);

				Drawf.light(b.x, b.y, rad, hitColor, 0.5f);
			}

			@Override
			public void init(Bullet b) {
				super.init(b);
				b.fdata = Mathf.randomSeed(b.id, 90);
			}

			@Override
			public void update(Bullet b) {
				super.update(b);
				b.fdata += b.vel.len() / 3f;
			}

			@Override
			public void despawned(Bullet b) {
				super.despawned(b);

				Angles.randLenVectors(b.id, 8, splashDamageRadius / 1.25f, ((x, y) -> {
					float nowX = b.x + x;
					float nowY = b.y + y;

					Vec2 vec2 = new Vec2(nowX, nowY);
					Team team = b.team;
					float mul = b.damageMultiplier();
					Time.run(Mathf.random(6f, 24f) + Mathf.sqrt(x * x + y * y) / splashDamageRadius * 3f, () -> {
						if (Mathf.chanceDelta(0.4f)) hitSound.at(vec2.x, vec2.y, hitSoundPitch, hitSoundVolume);
						despawnSound.at(vec2);
						Effect.shake(hitShake, hitShake, vec2);

						for (int i = 0; i < lightning / 2; i++) {
							Lightning.create(team, lightningColor, lightningDamage, vec2.x, vec2.y, Mathf.random(360f), lightningLength + Mathf.random(lightningLengthRand));
						}

						hitEffect.at(vec2.x, vec2.y, 0, hitColor);
						hitSound.at(vec2.x, vec2.y, hitSoundPitch, hitSoundVolume);

						if (fragBullet != null) {
							for (int i = 0; i < fragBullets; i++) {
								fragBullet.create(team.cores().firstOpt(), team, vec2.x, vec2.y, Mathf.random(360), Mathf.random(fragVelocityMin, fragVelocityMax), Mathf.random(fragLifeMin, fragLifeMax));
							}
						}

						if (splashDamageRadius > 0 && !b.absorbed) {
							Damage.damage(team, vec2.x, vec2.y, splashDamageRadius, splashDamage * mul, collidesAir, collidesGround);

							if (status != StatusEffects.none) {
								Damage.status(team, vec2.x, vec2.y, splashDamageRadius, status, statusDuration, collidesAir, collidesGround);
							}
						}
					});
				}));
			}
		};
		collapseFrag = new LightningLinkerBulletType() {{
			effectLightningChance = 0.15f;
			damage = 200;
			backColor = trailColor = lightColor = lightningColor = hitColor = HPal.thurmixRed;
			size = 10f;
			frontColor = HPal.thurmixRedLight;
			range = 600f;
			spreadEffect = Fx.none;
			trailWidth = 8f;
			trailLength = 20;
			speed = 6f;
			linkRange = 280f;
			maxHit = 12;
			drag = 0.0065f;
			hitSound = Sounds.explosionbig;
			splashDamageRadius = 60f;
			splashDamage = lightningDamage = damage / 3f;
			lifetime = 130f;
			despawnEffect = HFx.lightningHitLarge(hitColor);
			hitEffect = HFx.sharpBlast(hitColor, frontColor, 35, splashDamageRadius * 1.25f);
			shootEffect = HFx.hitSpark(backColor, 45f, 12, 60, 3, 8);
			smokeEffect = HFx.hugeSmoke;
		}};
		collapse = new EffectBulletType(480f) {{
			hittable = false;
			absorbable = false;
			collides = false;
			collidesTiles = collidesAir = collidesGround = true;
			speed = 0.1f;
			despawnHit = true;
			keepVelocity = false;
			splashDamageRadius = 800f;
			splashDamage = 800f;
			lightningDamage = 200f;
			lightning = 36;
			lightningLength = 60;
			lightningLengthRand = 60;
			hitShake = despawnShake = 40f;
			drawSize = 800f;
			hitColor = lightColor = trailColor = lightningColor = HPal.thurmixRed;
			fragBullets = 22;
			fragBullet = collapseFrag;
			hitSound = HSounds.hugeBlast;
			hitSoundVolume = 4f;
			fragLifeMax = 1.1f;
			fragLifeMin = 0.7f;
			fragVelocityMax = 0.6f;
			fragVelocityMin = 0.2f;
			status = StatusEffects.shocked;
			shootEffect = HFx.lightningHitLarge(hitColor);
			hitEffect = HFx.hitSpark(hitColor, 240f, 220, 900, 8, 27);
			despawnEffect = HFx.collapserBulletExplode;
		}
			@Override
			public void despawned(Bullet b) {
				super.despawned(b);

				Vec2 vec = new Vec2().set(b);

				float damageMulti = b.damageMultiplier();
				Team team = b.team;
				for (int i = 0; i < splashDamageRadius / (Vars.tilesize * 3.5f); i++) {
					int j = i;
					Time.run(i * despawnEffect.lifetime / (splashDamageRadius / (Vars.tilesize * 2)), () -> Damage.damage(team, vec.x, vec.y, Vars.tilesize * (j + 6), splashDamage * damageMulti, true));
				}

				float rad = 120;
				float spacing = 2.5f;

				for (int k = 0; k < (despawnEffect.lifetime - HFx.chainLightningFadeReversed.lifetime) / spacing; k++) {
					Time.run(k * spacing, () -> {
						for (int j : Mathf.signs) {
							Vec2 v = Tmp.v6.rnd(rad * 2 + Mathf.random(rad * 4)).add(vec);
							(j > 0 ? HFx.chainLightningFade : HFx.chainLightningFadeReversed).at(v.x, v.y, 12f, hitColor, vec);
						}
					});
				}
			}

			@Override
			public void update(Bullet b) {
				float rad = 120;

				Effect.shake(8 * b.fin(), 6, b);

				if (b.timer(1, 12)) {
					Seq<Teamc> entites = new Seq<>(Teamc.class);

					Units.nearbyEnemies(b.team, b.x, b.y, rad * 2.5f * (1 + b.fin()) / 2, entites::add);

					Units.nearbyBuildings(b.x, b.y, rad * 2.5f * (1 + b.fin()) / 2, e -> {
						if (e.team != b.team) entites.add(e);
					});

					entites.shuffle();
					entites.truncate(15);

					for (Teamc e : entites) {
						PositionLightning.create(b, b.team, b, e, lightningColor, false, lightningDamage, 5 + Mathf.random(5), PositionLightning.WIDTH, 1, p -> HFx.lightningHitSmall.at(p.getX(), p.getY(), 0, lightningColor));
					}
				}

				if (b.lifetime() - b.time() > HFx.chainLightningFadeReversed.lifetime)
					for (int i = 0; i < 2; i++) {
						if (Mathf.chanceDelta(0.2 * Mathf.curve(b.fin(), 0, 0.8f))) {
							for (int j : Mathf.signs) {
								Sounds.spark.at(b.x, b.y, 1f, 0.3f);
								Vec2 v = Tmp.v6.rnd(rad / 2 + Mathf.random(rad * 2) * (1 + Mathf.curve(b.fin(), 0, 0.9f)) / 1.5f).add(b);
								(j > 0 ? HFx.chainLightningFade : HFx.chainLightningFadeReversed).at(v.x, v.y, 12f, hitColor, b);
							}
						}
					}

				if (b.fin() > 0.05f && Mathf.chanceDelta(b.fin() * 0.3f + 0.02f)) {
					HSounds.blaster.at(b.x, b.y, 1f, 0.3f);
					Tmp.v1.rnd(rad / 4 * b.fin());
					HFx.shuttleLerp.at(b.x + Tmp.v1.x, b.y + Tmp.v1.y, Tmp.v1.angle(), hitColor, Mathf.random(rad, rad * 3f) * (Mathf.curve(b.fin(Interp.pow2In), 0, 0.7f) + 2) / 3);
				}
			}

			@Override
			public void draw(Bullet b) {
				float fin = Mathf.curve(b.fin(), 0f, 0.02f);
				float f = fin * Mathf.curve(b.fout(), 0f, 0.1f);
				float rad = 120f;

				float z = Draw.z();

				float circleFout = (b.fout(Interp.pow2In) + 1) / 2;

				Draw.color(hitColor);
				Lines.stroke(rad / 20 * b.fin());
				Lines.circle(b.x, b.y, rad * b.fout(Interp.pow3In));
				Lines.circle(b.x, b.y, b.fin(Interp.circleOut) * rad * 3f * Mathf.curve(b.fout(), 0, 0.05f));

				Rand rand = HFx.rand0;
				rand.setSeed(b.id);
				for (int i = 0; i < (int) (rad / 3); i++) {
					Tmp.v1.trns(rand.random(360f) + rand.range(1f) * rad / 5 * b.fin(Interp.pow2Out), rad / 2.05f * circleFout + rand.random(rad * (1 + b.fin(Interp.circleOut)) / 1.8f));
					float angle = Tmp.v1.angle();
					Drawn.tri(b.x + Tmp.v1.x, b.y + Tmp.v1.y, (b.fin() + 1) / 2 * 28 + rand.random(0, 8), rad / 16 * (b.fin(Interp.exp5In) + 0.25f), angle);
					Drawn.tri(b.x + Tmp.v1.x, b.y + Tmp.v1.y, (b.fin() + 1) / 2 * 12 + rand.random(0, 2), rad / 12 * (b.fin(Interp.exp5In) + 0.5f) / 1.2f, angle - 180);
				}

				Angles.randLenVectors(b.id + 1, (int) (rad / 3), rad / 4 * circleFout, rad * (1 + b.fin(Interp.pow3Out)) / 3, (x, y) -> {
					float angle = Mathf.angle(x, y);
					Drawn.tri(b.x + x, b.y + y, rad / 8 * (1 + b.fout()) / 2.2f, (b.fout() * 3 + 1) / 3 * 25 + rand.random(4, 12) * (b.fout(Interp.circleOut) + 1) / 2, angle);
					Drawn.tri(b.x + x, b.y + y, rad / 8 * (1 + b.fout()) / 2.2f, (b.fout() * 3 + 1) / 3 * 9 + rand.random(0, 2) * (b.fin() + 1) / 2, angle - 180);
				});

				Drawf.light(b.x, b.y, rad * f * (b.fin() + 1) * 2, Draw.getColor(), 0.7f);

				Draw.z(Layer.effect + 0.001f);
				Draw.color(hitColor);
				Fill.circle(b.x, b.y, rad * fin * circleFout / 2f);
				Draw.color(HPal.thurmixRedDark);
				Fill.circle(b.x, b.y, rad * fin * circleFout * 0.75f / 2f);
				Draw.z(Layer.bullet - 0.1f);
				Draw.color(HPal.thurmixRedDark);
				Fill.circle(b.x, b.y, rad * fin * circleFout * 0.8f / 2f);
				Draw.z(z);
			}
		};
	}
}
