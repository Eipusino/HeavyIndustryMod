package heavyindustry.content;

import arc.graphics.Color;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import heavyindustry.entities.bullet.ChainBulletType;
import heavyindustry.entities.bullet.DelayedPointBulletType;
import heavyindustry.entities.bullet.TrailFadeBulletType;
import heavyindustry.entities.effect.WrapperEffect;
import heavyindustry.entities.pattern.FlipShootHelix;
import heavyindustry.graphics.HPal;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.part.RegionPart;
import mindustry.entities.pattern.ShootPattern;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.type.Weapon;

import static heavyindustry.HVars.name;

public final class HWeapons {
	public static Weapon
			ancientLightningBallTurret, ancientCannon, ancientPrism;

	private HWeapons() {}

	public static void load() {
		ancientLightningBallTurret = new Weapon(name("ancient-ball-cannon")) {{
			x = 56;
			y = -48;
			shootY = 18f;
			rotate = true;
			rotateSpeed = 1.4f;
			shoot = new ShootPattern();
			shootSound = Sounds.missileSmall;
			recoil = 3f;
			reload = 90f;
			bullet = HBullets.ancientBall;
		}};
		ancientCannon = new Weapon(name("ancient-cannon")) {{
			mirror = false;
			layerOffset = 0.01f;
			rotate = true;
			shootSound = Sounds.shootSmite;
			reload = 90f;
			rotateSpeed = 1.55f;
			parts.add(new RegionPart("-barrel") {{
				under = true;
				layerOffset = -0.00175f;
				moveY = -4f;
				progress = PartProgress.recoil;
			}});
			shoot = new FlipShootHelix() {{
				shots = 3;
				mag = 1.15f;
				scl = 6f;
				shotDelay = 15f;
				offset = Mathf.PI2 * 12;
			}};
			inaccuracy = 1.5f;
			velocityRnd = 0.075f;
			bullet = new TrailFadeBulletType(19f, 400f) {{
				lifetime = 50f;
				trailLength = 14;
				trailWidth = 1.6F;
				tracerStroke -= 0.3f;
				tracers = 1;
				keepVelocity = true;
				tracerSpacing = 10f;
				tracerUpdateSpacing *= 1.25f;
				hitColor = backColor = lightColor = lightningColor = HPal.ancient;
				trailColor = HPal.ancientLightMid;
				frontColor = HPal.ancientLight;
				width = 9f;
				height = 30f;
				hitSound = Sounds.plasmaboom;
				despawnShake = hitShake = 18f;
				pierceArmor = true;
				pierceCap = 4;
				smokeEffect = HFx.hugeSmokeGray;
				shootEffect = WrapperEffect.wrap(HFx.shootLine(33, 28), hitColor);
				despawnEffect = HFx.square45_6_45;
				hitEffect = new MultiEffect(HFx.hitSpark, HFx.square45_4_45);
			}};
		}};
		ancientPrism = new Weapon(name("ancient-prism")) {{
			shootSound = Sounds.laser;
			x = y = 0;
			shootY = 3;
			layerOffset = 0.005f;
			shootCone = 5f;
			rotateSpeed = 2.5f;
			rotate = true;
			mirror = false;
			predictTarget = false;
			reload = 240f;
			inaccuracy = 0;
			recoil = 1.25f;
			shootWarmupSpeed /= 2f;
			minWarmup = 0.935f;
			cooldownTime = reload - 30f;
			parts.add(new RegionPart("-barrel") {{
				outline = true;
				moveY = -4;
				heatLayerOffset = 0;
				heatLightOpacity = 0.4f;
				progress = PartProgress.recoil;
				heatColor = HPal.ancientHeat;
			}}, new RegionPart("-back") {{
				outline = true;
				heatLayerOffset = 0;
				heatLightOpacity = 0.4f;
				heatColor = HPal.ancientHeat;
			}});
			bullet = new DelayedPointBulletType() {{
				width = 10f;
				damage = 230;
				hitColor = HPal.ancientLightMid;
				lightColor = lightningColor = trailColor = HPal.ancientLightMid;
				rangeOverride = 320;
				trailEffect = Fx.none;
				hitEffect = HFx.hitSparkLarge;
				despawnEffect = HFx.square45_6_45;
				shootStatusDuration = 0;
				status = StatusEffects.melting;
				statusDuration = 600f;
				despawnShake = hitShake = 2f;
				collidesAir = collidesGround = true;
				fragBullets = 1;
				fragBullet = new ChainBulletType(80) {{
					length = 0;
					collidesAir = collidesGround = true;
					quietShoot = true;
					hitColor = HPal.ancientLightMid;
					lightColor = lightningColor = trailColor = Pal.techBlue;
					thick = 7f;
					maxHit = 3;
					hitEffect = HFx.lightningHitSmall;
					effectController = (t, f) -> {
						DelayedPointBulletType.laser.at(f.getX(), f.getY(), thick, hitColor, new Vec2().set(t));
					};
				}};
				shootEffect = WrapperEffect.wrap(HFx.shootSquare(45, 8, 4, 45, 40, 25), hitColor);
				smokeEffect = WrapperEffect.wrap(HFx.hugeSmokeLong, Color.gray);
			}};
		}};
	}
}
