package heavyindustry.content;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.scene.ui.layout.Table;
import arc.util.Interval;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.ai.CopterAI;
import heavyindustry.ai.DefenderHealAI;
import heavyindustry.ai.MinerDepotAI;
import heavyindustry.ai.MinerPointAI;
import heavyindustry.ai.NullAI;
import heavyindustry.ai.SurroundAI;
import heavyindustry.entities.abilities.BatteryAbility;
import heavyindustry.entities.abilities.InvincibleForceFieldAbility;
import heavyindustry.entities.abilities.JavelinAbility;
import heavyindustry.entities.abilities.MirrorArmorAbility;
import heavyindustry.entities.abilities.MirrorFieldAbility;
import heavyindustry.entities.abilities.RegenProjectorAbility;
import heavyindustry.entities.abilities.TerritoryFieldAbility;
import heavyindustry.entities.abilities.WitchServiceAbility;
import heavyindustry.entities.bullet.AccelBulletType;
import heavyindustry.entities.bullet.AntiBulletFlakBulletType;
import heavyindustry.entities.bullet.ArrowBulletType;
import heavyindustry.entities.bullet.CtrlMissileBulletType;
import heavyindustry.entities.bullet.EdgeFragBulletType;
import heavyindustry.entities.bullet.GuidedMissileBulletType;
import heavyindustry.entities.bullet.HealConeBulletType;
import heavyindustry.entities.bullet.HealingNukeBulletType;
import heavyindustry.entities.bullet.MultiTrailBulletType;
import heavyindustry.entities.bullet.ParticleFlameBulletType;
import heavyindustry.entities.bullet.TrailFadeBulletType;
import heavyindustry.entities.effect.WrapperEffect;
import heavyindustry.entities.part.AimPart;
import heavyindustry.entities.part.BowHalo;
import heavyindustry.entities.part.CustomPart;
import heavyindustry.entities.part.PartBow;
import heavyindustry.func.Floatff2;
import heavyindustry.func.Floatff3;
import heavyindustry.gen.BaseBuildingTetherLegsUnit;
import heavyindustry.gen.BaseBuildingTetherUnit;
import heavyindustry.gen.BaseMechUnit;
import heavyindustry.gen.BasePayloadUnit;
import heavyindustry.gen.BaseTankUnit;
import heavyindustry.gen.BaseUnit;
import heavyindustry.gen.BaseUnitWaterMove;
import heavyindustry.gen.CopterUnit;
import heavyindustry.gen.BaseLegsUnit;
import heavyindustry.gen.DPSMechUnit;
import heavyindustry.gen.DamageAbsorbMechUnit;
import heavyindustry.gen.HSounds;
import heavyindustry.gen.InvincibleShipUnit;
import heavyindustry.gen.NucleoidUnit;
import heavyindustry.gen.UltFire;
import heavyindustry.graphics.Drawn;
import heavyindustry.graphics.Draws;
import heavyindustry.graphics.HPal;
import heavyindustry.graphics.MathRenderer;
import heavyindustry.math.Mathm;
import heavyindustry.type.unit.BaseUnitType;
import heavyindustry.type.unit.CopterUnitType;
import heavyindustry.type.unit.NucleoidUnitType;
import heavyindustry.type.weapons.AcceleratingWeapon;
import heavyindustry.type.weapons.BoostWeapon;
import heavyindustry.type.weapons.DataWeapon;
import heavyindustry.type.weapons.EnergyChargeWeapon;
import heavyindustry.type.weapons.HealConeWeapon;
import heavyindustry.type.weapons.LimitedAngleWeapon;
import heavyindustry.type.weapons.PointDefenceMultiBarrelWeapon;
import heavyindustry.ui.Elements;
import heavyindustry.util.Utils;
import mindustry.Vars;
import mindustry.ai.UnitCommand;
import mindustry.ai.types.FlyingAI;
import mindustry.ai.types.FlyingFollowAI;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.StatusEffects;
import mindustry.entities.Effect;
import mindustry.entities.Mover;
import mindustry.entities.Units;
import mindustry.entities.abilities.EnergyFieldAbility;
import mindustry.entities.abilities.ForceFieldAbility;
import mindustry.entities.abilities.RegenAbility;
import mindustry.entities.abilities.RepairFieldAbility;
import mindustry.entities.abilities.ShieldRegenFieldAbility;
import mindustry.entities.abilities.StatusFieldAbility;
import mindustry.entities.abilities.SuppressionFieldAbility;
import mindustry.entities.bullet.ArtilleryBulletType;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.bullet.ContinuousLaserBulletType;
import mindustry.entities.bullet.EmpBulletType;
import mindustry.entities.bullet.FlakBulletType;
import mindustry.entities.bullet.LaserBulletType;
import mindustry.entities.bullet.LightningBulletType;
import mindustry.entities.bullet.MissileBulletType;
import mindustry.entities.bullet.PointBulletType;
import mindustry.entities.bullet.PointLaserBulletType;
import mindustry.entities.bullet.RailBulletType;
import mindustry.entities.bullet.ShrapnelBulletType;
import mindustry.entities.effect.ExplosionEffect;
import mindustry.entities.effect.MultiEffect;
import mindustry.entities.effect.ParticleEffect;
import mindustry.entities.effect.WaveEffect;
import mindustry.entities.part.RegionPart;
import mindustry.entities.part.ShapePart;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.entities.pattern.ShootBarrel;
import mindustry.entities.pattern.ShootPattern;
import mindustry.entities.pattern.ShootSine;
import mindustry.entities.pattern.ShootSpread;
import mindustry.entities.units.WeaponMount;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Call;
import mindustry.gen.Entityc;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;
import mindustry.gen.Shieldc;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Trail;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.type.ammo.ItemAmmoType;
import mindustry.type.ammo.PowerAmmoType;
import mindustry.type.weapons.PointDefenseWeapon;
import mindustry.type.weapons.RepairBeamWeapon;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Env;

import static heavyindustry.HVars.MOD_NAME;

/**
 * Defines the {@linkplain UnitType units} this mod offers.
 *
 * @author Eipusino
 */
public final class HUnitTypes {
	public static final String EPHEMERAS = "ephemeras";
	public static final String TIMER = "timer";
	public static final String STATUS = "status";
	public static final String PHASE = "phase";
	public static final String SHOOTERS = "shooters";

	public static BaseUnitType
			//vanilla-tank
			vanguard, striker, counterattack, crush, destruction, purgatory,
	//vanilla-copter
	caelifera, schistocerca, anthophila, vespula, lepidoptera, mantodea,
	//vanilla-tier6
	empire, supernova, cancer, aphelion, windstorm, poseidon, leviathan,
	//vanilla-tier6-erekir
	dominate, oracle, havoc,
	//miner-erekir
	miner, largeMiner, legsMiner,
	//other
	vulture, invincibleShip, dpsTesterLand,
	//elite
	tiger, thunder, eagle,
	//special
	eipusino;

	/** Don't let anyone instantiate this class. */
	private HUnitTypes() {}

	/** Instantiates all contents. Called in the main thread in {@code HeavyIndustryMod.loadContent()}. */
	public static void load() {
		//vanilla-tank
		vanguard = new BaseUnitType("vanguard") {{
			constructor = BaseTankUnit::new;
			squareShape = true;
			omniMovement = false;
			rotateMoveFirst = false;
			rotateSpeed = 3f;
			speed = 2.4f;
			hitSize = 9.5f;
			ammoCapacity = 300;
			health = 250f;
			armor = 5f;
			drag = 0.08f;
			accel = 0.1f;
			itemCapacity = 5;
			faceTarget = false;
			abilities.add(new StatusFieldAbility(StatusEffects.overclock, 250f, 300f, 30f) {{
				applyEffect = Fx.none;
				activeEffect = HFx.circle;
			}});
			weapons.add(new Weapon(name + "-weapon") {{
				reload = 7.6f;
				recoil = 0f;
				x = 0f;
				y = 0f;
				rotate = true;
				rotateSpeed = 15f;
				mirror = false;
				inaccuracy = 0.5f;
				ejectEffect = Fx.casing1;
				shootSound = Sounds.shoot;
				alternate = false;
				bullet = new BasicBulletType(9f, 10f) {{
					buildingDamageMultiplier = 0.8f;
					lifetime = 18f;
					width = 3f;
					height = 10f;
				}};
			}});
		}};
		striker = new BaseUnitType("striker") {{
			constructor = BaseTankUnit::new;
			squareShape = true;
			omniMovement = false;
			rotateMoveFirst = false;
			hovering = true;
			canDrown = false;
			speed = 1.8f;
			hitSize = 18f;
			ammoType = new ItemAmmoType(Items.graphite);
			ammoCapacity = 80;
			health = 660f;
			armor = 5f;
			drag = 0.08f;
			accel = 0.1f;
			rotateSpeed = 3f;
			itemCapacity = 0;
			faceTarget = false;
			weapons.add(new Weapon(name + "-weapon") {{
				reload = 120;
				x = 0f;
				y = -1f;
				rotate = true;
				rotateSpeed = 9f;
				mirror = false;
				inaccuracy = 0f;
				ejectEffect = Fx.casing2;
				shootSound = Sounds.artillery;
				alternate = false;
				bullet = new MissileBulletType(11f, 108f, MOD_NAME + "-rocket") {{
					hitSize = 40;
					splashDamageRadius = 46;
					splashDamage = 96;
					status = StatusEffects.blasted;
					statusDuration = 60;
					backColor = HPal.orangeBack;
					frontColor = HPal.missileGray;
					lifetime = 30;
					homingPower = 0.03f;
					homingRange = 80;
					knockback = 8;
					width = 12;
					height = 40;
					ammoMultiplier = 3;
					despawnEffect = Fx.none;
					shootEffect = Fx.shootPyraFlame;
					hitEffect = new MultiEffect(new ParticleEffect() {{
						particles = 8;
						sizeFrom = 6;
						sizeTo = 0;
						length = 25;
						baseLength = 23;
						lifetime = 35;
						colorFrom = colorTo = HPal.smoke;
					}}, new ParticleEffect() {{
						particles = 12;
						line = true;
						length = 43;
						baseLength = 3;
						lifetime = 22;
						colorFrom = Color.white;
						colorTo = HPal.missileYellow;
					}}, new WaveEffect() {{
						lifetime = 10;
						sizeFrom = 1;
						sizeTo = 48;
						strokeFrom = 2;
						strokeTo = 0;
						colorFrom = HPal.missileYellow;
						colorTo = Color.white;
					}});
				}};
			}});
		}};
		counterattack = new BaseUnitType("counterattack") {{
			constructor = BaseTankUnit::new;
			treadFrames = 8;
			treadPullOffset = 8;
			treadRects = new Rect[]{new Rect(-45f, -45f, 24f, 88f)};
			speed = 1.3f;
			hitSize = 20f;
			ammoType = new ItemAmmoType(Items.blastCompound);
			ammoCapacity = 80;
			squareShape = true;
			omniMovement = false;
			rotateMoveFirst = false;
			health = 1200f;
			armor = 13f;
			rotateSpeed = 2f;
			itemCapacity = 0;
			faceTarget = false;
			weapons.add(new Weapon(name + "-weapon") {{
				reload = 80f;
				x = 0f;
				y = 0f;
				rotate = true;
				mirror = false;
				alternate = false;
				rotateSpeed = 1.3f;
				parts.add(new RegionPart("-top") {{
					mirror = true;
					under = true;
					moveY = -4f;
					progress = PartProgress.warmup;
				}});
				shoot = new ShootAlternate(4f) {{
					shots = 3;
					shotDelay = 8f;
					barrels = 3;
				}};
				xRand = 4f;
				inaccuracy = 6f;
				shootSound = Sounds.missile;
				shootStatus = StatusEffects.slow;
				shootStatusDuration = reload + 1f;
				velocityRnd = 0.1f;
				bullet = new ArtilleryBulletType(12f, 10f, MOD_NAME + "-rocket") {{
					backColor = HPal.orangeBack;
					frontColor = trailColor = hitColor = HPal.missileGray;
					width = 8f;
					height = 45f;
					trailChance = 0f;
					trailInterval = 1f;
					trailEffect = new ParticleEffect() {{
						particles = 3;
						length = 30f;
						baseLength = 0f;
						sizeInterp = Interp.pow5In;
						lifetime = 10f;
						colorFrom = HPal.missileGray;
						colorTo = HPal.missileGray.cpy().a(0.45f);
						sizeFrom = 2.6f;
						sizeTo = 0f;
						cone = 8f;
					}};
					trailRotation = true;
					splashDamage = 55f;
					splashDamageRadius = 45f;
					buildingDamageMultiplier = 1.33f;
					collides = false;
					status = StatusEffects.blasted;
					shootEffect = Fx.shootSmallFlame;
					smokeEffect = new ParticleEffect() {{
						particles = 9;
						interp = Interp.pow10Out;
						sizeInterp = Interp.pow10In;
						sizeFrom = 6;
						sizeTo = 0;
						length = -58;
						baseLength = -20;
						lifetime = 42;
						colorFrom = colorTo = HPal.smoke.cpy().a(6f);
						cone = 40;
						layer = 49;
					}};
					lifetime = 41.6f;
					hitShake = 2f;
					hitSound = Sounds.explosion;
					hitEffect = new MultiEffect(new ParticleEffect() {{
						particles = 8;
						sizeFrom = 10;
						sizeTo = 0;
						length = 35;
						baseLength = 33;
						lifetime = 35;
						colorFrom = colorTo = HPal.smoke;
					}}, new ParticleEffect() {{
						particles = 12;
						line = true;
						strokeFrom = 2;
						strokeTo = 0;
						lenFrom = 16;
						lenTo = 8;
						length = 66;
						baseLength = 3;
						lifetime = 12;
						colorFrom = Color.white;
						colorTo = HPal.missileYellow;
					}}, new ParticleEffect() {{
						particles = 1;
						sizeFrom = 0;
						sizeTo = 45;
						length = 0;
						baseLength = 0;
						sizeInterp = Interp.pow5Out;
						lifetime = 12;
						layer = 50;
						colorFrom = Color.white.cpy().a(0.5f);
						colorTo = HPal.smoke.cpy().a(0f);
					}}, new WaveEffect() {{
						lifetime = 10;
						sizeFrom = 0;
						sizeTo = 48;
						interp = Interp.circleOut;
						strokeFrom = 15;
						strokeTo = 0;
						layer = 50;
						colorFrom = Color.white;
						colorTo = HPal.smoke.cpy().a(0.65f);
					}});
					despawnEffect = Fx.flakExplosionBig;
				}};
			}});
		}};
		crush = new BaseUnitType("crush") {{
			constructor = BaseTankUnit::new;
			squareShape = true;
			omniMovement = false;
			rotateMoveFirst = false;
			speed = 1.2f;
			hitSize = 28f;
			crushDamage = 2.33f;
			drownTimeMultiplier = 2f;
			treadPullOffset = 0;
			treadFrames = 8;
			treadRects = new Rect[]{new Rect(-67f, -84f, 39f, 167f)};
			ammoType = new ItemAmmoType(Items.surgeAlloy);
			ammoCapacity = 220;
			targetAir = true;
			health = 11000f;
			armor = 16f;
			rotateSpeed = 1.5f;
			itemCapacity = 0;
			faceTarget = false;
			immunities.addAll(StatusEffects.burning, StatusEffects.shocked);
			targetFlags = new BlockFlag[]{BlockFlag.repair, BlockFlag.turret};
			abilities.add(new StatusFieldAbility(StatusEffects.overclock, 1200f, 1200f, 45f) {{
				applyEffect = Fx.none;
				activeEffect = new WaveEffect() {{
					lifetime = 15;
					sizeFrom = 8;
					sizeTo = 45;
					strokeFrom = 2;
					strokeTo = 0;
					colorFrom = colorTo = HPal.energyYellow;
				}};
			}});
			weapons.add(new Weapon(name + "-weapon") {{
				reload = 90f;
				shootY = 19.2f;
				x = 0f;
				y = 0f;
				rotate = true;
				rotateSpeed = 1.9f;
				mirror = false;
				recoil = 4f;
				inaccuracy = 0f;
				shootSound = Sounds.laser;
				shake = 3f;
				alternate = false;
				bullet = new RailBulletType() {{
					damage = 180f;
					splashDamage = 170f;
					splashDamageRadius = 16f;
					buildingDamageMultiplier = 1.5f;
					speed = 20f;
					lifetime = 15f;
					hitSound = Sounds.lasercharge2;
					smokeEffect = Fx.bigShockwave;
					shootEffect = new ParticleEffect() {{
						particles = 1;
						sizeFrom = 5f;
						sizeTo = length = baseLength = 0f;
						lifetime = 11f;
						colorFrom = colorTo = HPal.energyYellow;
					}};
					hitEffect = new ParticleEffect() {{
						particles = 1;
						sizeFrom = 10f;
						sizeTo = length = baseLength = 0f;
						lifetime = 15f;
						colorFrom = colorTo = HPal.energyYellow;
					}};
					despawnEffect = Fx.bigShockwave;
					pointEffect = new ParticleEffect() {{
						particles = 1;
						length = 0f;
						baseLength = 1f;
						lifetime = 10f;
						line = true;
						randLength = false;
						lenFrom = 10f;
						lenTo = 10f;
						strokeFrom = 4f;
						strokeTo = 0f;
						colorFrom = colorTo = HPal.energyYellow;
						cone = 0f;
					}};
					fragLifeMin = 1f;
					fragVelocityMax = 0f;
					fragBullets = 1;
					fragBullet = new BasicBulletType() {{
						lifetime = 15f;
						height = 0f;
						width = 0f;
						collides = false;
						hittable = false;
						absorbable = false;
						buildingDamageMultiplier = 2f;
						splashDamageRadius = 66.4f;
						splashDamage = 136f;
						hitShake = 1f;
						hitSound = Sounds.plasmaboom;
						hitColor = HPal.energyYellow;
						hitEffect = new MultiEffect(new ParticleEffect() {{
							particles = 4;
							sizeFrom = 15f;
							sizeTo = 0f;
							length = 20f;
							baseLength = 48f;
							lifetime = 25f;
							colorFrom = HPal.energyYellow.cpy().a(0.45f);
							colorTo = HPal.energyYellow.cpy().a(0f);
						}}, new ParticleEffect() {{
							particles = 22;
							line = true;
							strokeFrom = 3f;
							strokeTo = 0f;
							lenFrom = 20f;
							lenTo = 0f;
							length = 63f;
							baseLength = 0f;
							lifetime = 20f;
							colorFrom = colorTo = HPal.energyYellow;
						}}, new WaveEffect() {{
							lifetime = 25f;
							sizeFrom = 0f;
							sizeTo = 66f;
							strokeFrom = 3f;
							strokeTo = 0f;
							colorFrom = colorTo = HPal.energyYellow;
						}});
						despawnEffect = Fx.none;
						fragBullets = 4;
						fragBullet = new PointBulletType() {{
							trailSpacing = 9f;
							trailEffect = new ParticleEffect() {{
								particles = 1;
								length = 0f;
								baseLength = 1f;
								lifetime = 6f;
								line = true;
								randLength = false;
								lenFrom = 10f;
								lenTo = 10f;
								strokeFrom = 2f;
								strokeTo = 0f;
								colorFrom = colorTo = HPal.energyYellow;
								cone = 0f;
							}};
							lifetime = 8f;
							speed = 15f;
							buildingDamageMultiplier = 2f;
							splashDamageRadius = 10f;
							splashDamage = 25f;
							hitShake = 1f;
							hitSound = Sounds.laser;
							hitColor = HPal.energyYellow;
							hitEffect = new ParticleEffect() {{
								particles = 1;
								sizeFrom = 5f;
								sizeTo = 0f;
								length = 0f;
								baseLength = 0f;
								lifetime = 11f;
								colorFrom = colorTo = HPal.energyYellow;
							}};
							despawnEffect = Fx.none;
						}};
					}};
				}};
			}});
		}};
		destruction = new BaseUnitType("destruction") {{
			constructor = BaseTankUnit::new;
			squareShape = true;
			omniMovement = false;
			rotateMoveFirst = false;
			speed = 1f;
			hitSize = 48f;
			drownTimeMultiplier = 2.6f;
			crushDamage = 6f;
			treadRects = new Rect[]{new Rect(-86f, -108f, 42f, 112f), new Rect(-72f, -124f, 21f, 16f), new Rect(-86f, 9f, 42f, 119f)};
			ammoType = new ItemAmmoType(Items.surgeAlloy);
			ammoCapacity = 200;
			targetAir = true;
			health = 28000f;
			armor = 28f;
			rotateSpeed = 1.22f;
			itemCapacity = 30;
			faceTarget = false;
			immunities.add(StatusEffects.burning);
			targetFlags = new BlockFlag[]{BlockFlag.repair, BlockFlag.turret};
			weapons.add(new Weapon(name + "-weapon") {{
				reload = 110f;
				x = 0f;
				y = -0.5f;
				shootY = 33f;
				cooldownTime = 100f;
				rotate = true;
				top = true;
				rotateSpeed = 1.6f;
				recoil = 5f;
				mirror = false;
				inaccuracy = 0f;
				shootSound = Sounds.mediumCannon;
				shake = 8f;
				bullet = new BasicBulletType(31f, 250f) {{
					splashDamage = 135f;
					splashDamageRadius = 55f;
					buildingDamageMultiplier = 2f;
					lifetime = 15f;
					lightning = 2;
					lightningDamage = 90f;
					lightningLength = 15;
					lightningColor = backColor = trailColor = hitColor = HPal.energyYellow;
					shrinkY = 0f;
					frontColor = Color.white;
					trailLength = 15;
					trailWidth = 2.2f;
					pierce = true;
					pierceCap = 4;
					knockback = 8f;
					hitEffect = new MultiEffect(new ParticleEffect() {{
						particles = 9;
						sizeFrom = 10f;
						sizeTo = 0f;
						length = 65f;
						baseLength = 0f;
						lifetime = 15f;
						colorFrom = HPal.energyYellow;
						colorTo = Color.white;
						cone = 40f;
					}}, new WaveEffect() {{
						lifetime = 10f;
						sizeFrom = 2f;
						sizeTo = 60f;
						strokeFrom = 10f;
						strokeTo = 0f;
						colorFrom = colorTo = HPal.energyYellow;
					}});
					shootEffect = new MultiEffect(new ParticleEffect() {{
						particles = 6;
						sizeFrom = 8;
						sizeTo = 0;
						length = 55;
						baseLength = 0;
						lifetime = 33;
						colorFrom = HPal.energyYellow;
						colorTo = Color.white;
						cone = 35;
					}}, new WaveEffect() {{
						lifetime = 10;
						sizeFrom = 0;
						sizeTo = 30;
						strokeFrom = 3;
						strokeTo = 0;
						colorFrom = HPal.energyYellow;
						colorTo = Color.white;
					}});
					smokeEffect = Fx.smokeCloud;
					width = 16f;
					height = 28f;
					hitSound = Sounds.plasmaboom;
					fragLifeMin = 0.1f;
					fragBullets = 3;
					fragRandomSpread = 60f;
					fragBullet = new PointBulletType() {{
						trailEffect = Fx.none;
						despawnEffect = Fx.none;
						status = StatusEffects.blasted;
						hitColor = HPal.energyYellow;
						hitEffect = new MultiEffect(new ParticleEffect() {{
							particles = 9;
							sizeFrom = 8f;
							sizeTo = 0f;
							length = 55f;
							baseLength = 0f;
							lifetime = 15f;
							colorFrom = colorTo = HPal.energyYellow;
						}}, new WaveEffect() {{
							lifetime = 15f;
							sizeFrom = 2f;
							sizeTo = 40f;
							strokeFrom = 6f;
							strokeTo = 0f;
							colorFrom = colorTo = HPal.energyYellow;
						}});
						hitSound = Sounds.laser;
						collides = false;
						damage = 250f;
						splashDamageRadius = 40f;
						splashDamage = 85f;
						buildingDamageMultiplier = 1.25f;
						lifetime = 10f;
						speed = 8f;
					}};
					despawnEffect = Fx.bigShockwave;
				}};
				parts.add(new RegionPart("-barrel") {{
					mirror = true;
					under = true;
					moveY = -4;
					heatProgress = PartProgress.recoil;
					progress = PartProgress.recoil;
				}});
			}});
		}};
		purgatory = new BaseUnitType("purgatory") {{
			constructor = BaseTankUnit::new;
			squareShape = true;
			omniMovement = false;
			rotateMoveFirst = false;
			drownTimeMultiplier = 5f;
			speed = 0.8f;
			crushDamage = 10f;
			treadRects = new Rect[]{new Rect(-115f, 118f, 52f, 48f), new Rect(-118f, -160f, 79f, 144f)};
			hitSize = 66f;
			immunities.add(StatusEffects.burning);
			ammoType = new ItemAmmoType(Items.surgeAlloy);
			ammoCapacity = 300;
			targetAir = true;
			health = 82000f;
			armor = 36f;
			drag = 0.3f;
			rotateSpeed = 1f;
			itemCapacity = 55;
			faceTarget = false;
			weapons.add(new Weapon(name + "-weapon") {{
				reload = 78f;
				x = 0f;
				y = 0f;
				shoot = new ShootBarrel() {{
					shots = 2;
					shotDelay = 8f;
					barrels = new float[]{
							-9f, 40f, 0f,
							9f, 40f, 0f};
				}};
				cooldownTime = 100f;
				rotate = true;
				rotateSpeed = 2f;
				recoil = 6f;
				mirror = false;
				inaccuracy = 0.5f;
				shootSound = Sounds.largeCannon;
				shake = 8f;
				bullet = new BasicBulletType(24f, 500f, "missile-large") {{
					splashDamage = 285f;
					splashDamageRadius = 80f;
					buildingDamageMultiplier = 1.5f;
					width = 10f;
					height = 26f;
					hitSize = 18f;
					lifetime = 17f;
					drag = -0.01f;
					absorbable = false;
					hittable = false;
					pierce = true;
					pierceArmor = true;
					pierceBuilding = true;
					pierceCap = 3;
					hitShake = 5f;
					status = StatusEffects.unmoving;
					statusDuration = 80f;
					frontColor = Color.white;
					backColor = trailColor = hitColor = HPal.energyYellow;
					trailLength = 8;
					trailWidth = 4f;
					trailRotation = true;
					trailChance = 1f;
					trailInterval = 33f;
					trailEffect = new ParticleEffect() {{
						particles = 3;
						sizeFrom = 3;
						sizeTo = 0;
						interp = Interp.circleOut;
						sizeInterp = Interp.pow3In;
						length = 10;
						baseLength = 0;
						lifetime = 8;
						colorFrom = colorTo = HPal.energyYellow;
					}};
					shrinkY = 0f;
					hitEffect = new MultiEffect(new ParticleEffect() {{
						particles = 9;
						sizeFrom = 10;
						sizeTo = 0;
						length = 90;
						baseLength = 8;
						lifetime = 35;
						colorFrom = colorTo = HPal.energyYellow;
					}}, new WaveEffect() {{
						lifetime = 10;
						sizeFrom = 2;
						sizeTo = 60;
						strokeFrom = 10;
						strokeTo = 0;
						colorFrom = colorTo = HPal.energyYellow;
					}});
					hitSound = Sounds.plasmaboom;
					shootEffect = new MultiEffect(new ParticleEffect() {{
						particles = 6;
						line = true;
						strokeFrom = 6f;
						strokeTo = 0f;
						lenFrom = 25f;
						lenTo = 0f;
						length = 50f;
						baseLength = 0f;
						lifetime = 11f;
						colorFrom = colorTo = HPal.energyYellow;
						cone = 15f;
					}}, new WaveEffect() {{
						lifetime = 10f;
						sizeFrom = 0f;
						sizeTo = 35f;
						strokeFrom = 4f;
						strokeTo = 0f;
						colorFrom = colorTo = HPal.energyYellow;
					}});
					smokeEffect = Fx.smokeCloud;
					despawnEffect = new ParticleEffect() {{
						particles = 1;
						sizeFrom = 10f;
						sizeTo = 0f;
						length = 0f;
						baseLength = 0f;
						lifetime = 65f;
						colorFrom = HPal.energyYellow;
						colorTo = Color.white;
					}};
				}};
			}});
		}};
		//vanilla-copter
		caelifera = new CopterUnitType("caelifera") {{
			constructor = CopterUnit::new;
			aiController = CopterAI::new;
			circleTarget = false;
			speed = 5f;
			drag = 0.08f;
			accel = 0.04f;
			fallSpeed = 0.005f;
			health = 75;
			armor = 1f;
			engineSize = 0f;
			flying = true;
			hitSize = 12f;
			range = 140f;
			weapons.add(new Weapon(name + "-gun") {{
				layerOffset = -0.01f;
				reload = 6f;
				x = 5.25f;
				y = 6.5f;
				shootY = 1.5f;
				shootSound = Sounds.pew;
				ejectEffect = Fx.casing1;
				bullet = new BasicBulletType(5f, 7f) {{
					lifetime = 30f;
					shrinkY = 0.2f;
				}};
			}}, new Weapon(name + "-launcher") {{
				layerOffset = -0.01f;
				reload = 30f;
				x = 4.5f;
				y = 0.5f;
				shootY = 2.25f;
				shootSound = Sounds.shootSnap;
				ejectEffect = Fx.casing2;
				bullet = new MissileBulletType(3f, 1f) {{
					speed = 3f;
					lifetime = 45f;
					splashDamage = 40f;
					splashDamageRadius = 8f;
					drag = -0.01f;
				}};
			}});
			rotors.add(new Rotor(name + "-rotor") {{
				x = 0f;
				y = 6f;
			}});
			hideDetails = false;
		}};
		schistocerca = new CopterUnitType("schistocerca") {{
			constructor = CopterUnit::new;
			aiController = CopterAI::new;
			circleTarget = false;
			speed = 4.5f;
			drag = 0.07f;
			accel = 0.03f;
			fallSpeed = 0.005f;
			health = 150;
			armor = 2f;
			engineSize = 0f;
			flying = true;
			hitSize = 13f;
			range = 165f;
			rotateSpeed = 4.6f;
			weapons.add(new Weapon(name + "-gun") {{
				layerOffset = -0.01f;
				top = false;
				x = 1.5f;
				y = 11f;
				shootX = -0.75f;
				shootY = 3f;
				shootSound = Sounds.pew;
				ejectEffect = Fx.casing1;
				reload = 8f;
				bullet = new BasicBulletType(4f, 5f) {{
					lifetime = 36;
					shrinkY = 0.2f;
				}};
			}}, new Weapon(name + "-gun") {{
				top = false;
				x = 4f;
				y = 8.75f;
				shootX = -0.75f;
				shootY = 3f;
				shootSound = Sounds.shootSnap;
				ejectEffect = Fx.casing1;
				reload = 12f;
				bullet = new BasicBulletType(4f, 8f) {{
					width = 7f;
					height = 9f;
					lifetime = 36f;
					shrinkY = 0.2f;
				}};
			}}, new Weapon(name + "-gun-big") {{
				top = false;
				x = 6.75f;
				y = 5.75f;
				shootX = -0.5f;
				shootY = 2f;
				shootSound = Sounds.shootSnap;
				ejectEffect = Fx.casing1;
				reload = 30f;
				bullet = new BasicBulletType(3.2f, 16, "bullet") {{
					width = 10f;
					height = 12f;
					frontColor = Pal.lightishOrange;
					backColor = Pal.lightOrange;
					status = StatusEffects.burning;
					hitEffect = new MultiEffect(Fx.hitBulletSmall, Fx.fireHit);
					ammoMultiplier = 5;
					splashDamage = 10f;
					splashDamageRadius = 22f;
					makeFire = true;
					lifetime = 60f;
				}};
			}});
			for (int i : Mathf.signs) {
				rotors.add(new Rotor(name + "-rotor") {{
					x = 0f;
					y = 6.5f;
					bladeCount = 3;
					ghostAlpha = 0.4f;
					shadowAlpha = 0.2f;
					shadeSpeed = 3f * i;
					speed = 29f * i;
				}});
			}
			hideDetails = false;
		}};
		anthophila = new CopterUnitType("anthophila") {{
			constructor = CopterUnit::new;
			aiController = CopterAI::new;
			circleTarget = false;
			speed = 4f;
			drag = 0.07f;
			accel = 0.03f;
			fallSpeed = 0.005f;
			health = 450;
			armor = 4f;
			engineSize = 0f;
			flying = true;
			hitSize = 15f;
			range = 165f;
			fallRotateSpeed = 2f;
			rotateSpeed = 3.8f;
			ammoType = new ItemAmmoType(Items.graphite);
			weapons.add(new Weapon(name + "-gun") {{
				layerOffset = -0.01f;
				x = 4.25f;
				y = 14f;
				shootX = -1f;
				shootY = 2.75f;
				reload = 15;
				shootSound = Sounds.shootBig;
				bullet = new BasicBulletType(6f, 60f) {{
					lifetime = 30f;
					width = 16f;
					height = 20f;
					shootEffect = Fx.shootBig;
					smokeEffect = Fx.shootBigSmoke;
				}};
			}}, new Weapon(name + "-tesla") {{
				x = 7.75f;
				y = 8.25f;
				shootY = 5.25f;
				reload = 30f;
				shoot.shots = 3;
				shootSound = Sounds.spark;
				bullet = new LightningBulletType() {{
					damage = 15f;
					lightningLength = 12;
					lightningColor = Pal.surge;
				}};
			}});
			for (int i : Mathf.signs) {
				rotors.add(new Rotor(name + "-rotor2") {{
					x = 0f;
					y = -13f;
					bladeCount = 2;
					ghostAlpha = 0.4f;
					shadowAlpha = 0.2f;
					shadeSpeed = 3f * i;
					speed = 29f * i;
				}});
			}
			rotors.add(new Rotor(name + "-rotor1") {{
				mirror = true;
				x = 13f;
				y = 3f;
				bladeCount = 3;
			}});
			hideDetails = false;
		}};
		vespula = new CopterUnitType("vespula") {{
			constructor = CopterUnit::new;
			aiController = CopterAI::new;
			circleTarget = false;
			speed = 3.5f;
			drag = 0.07f;
			accel = 0.03f;
			fallSpeed = 0.003f;
			health = 4000;
			armor = 10f;
			engineSize = 0f;
			flying = true;
			hitSize = 30f;
			range = 165f;
			lowAltitude = true;
			rotateSpeed = 3.5f;
			ammoType = new ItemAmmoType(Items.thorium);
			weapons.add(new Weapon(name + "-gun-big") {{
				layerOffset = -0.01f;
				x = 8.25f;
				y = 9.5f;
				shootX = -1f;
				shootY = 7.25f;
				reload = 12f;
				shootSound = Sounds.shootBig;
				bullet = new BasicBulletType(6f, 60f) {{
					lifetime = 30f;
					width = 16f;
					height = 20f;
					shootEffect = Fx.shootBig;
					smokeEffect = Fx.shootBigSmoke;
				}};
			}}, new Weapon(name + "-gun") {{
				layerOffset = -0.01f;
				x = 6.5f;
				y = 21.5f;
				shootX = -0.25f;
				shootY = 5.75f;
				reload = 20f;
				shoot.shots = 4;
				shoot.shotDelay = 2f;
				shootSound = Sounds.shootSnap;
				bullet = new BasicBulletType(4f, 29, "bullet") {{
					width = 10f;
					height = 13f;
					shootEffect = Fx.shootBig;
					smokeEffect = Fx.shootBigSmoke;
					ammoMultiplier = 4;
					lifetime = 60f;
				}};
			}}, new Weapon(name + "-laser-gun") {{
				x = 13.5f;
				y = 15.5f;
				shootY = 4.5f;
				reload = 60f;
				shootSound = Sounds.laser;
				bullet = new LaserBulletType(240f) {{
					sideAngle = 45f;
					length = 200f;
				}};
			}});
			for (int i : Mathf.signs) {
				rotors.add(new Rotor(name + "-rotor") {{
					mirror = true;
					x = 15f;
					y = 6.75f;
					speed = 29f * i;
					ghostAlpha = 0.4f;
					shadowAlpha = 0.2f;
					shadeSpeed = 3f * i;
				}});
			}
			hideDetails = false;
		}};
		lepidoptera = new CopterUnitType("lepidoptera") {{
			constructor = CopterUnit::new;
			aiController = CopterAI::new;
			circleTarget = false;
			speed = 3f;
			drag = 0.07f;
			accel = 0.03f;
			fallSpeed = 0.003f;
			health = 9500;
			armor = 14f;
			engineSize = 0f;
			flying = true;
			hitSize = 45f;
			range = 300f;
			lowAltitude = true;
			fallRotateSpeed = 0.8f;
			rotateSpeed = 2.7f;
			ammoType = new ItemAmmoType(Items.thorium);
			weapons.add(new Weapon(name + "-gun") {{
				layerOffset = -0.01f;
				x = 14f;
				y = 27f;
				shootY = 5.5f;
				shootSound = Sounds.shootBig;
				ejectEffect = Fx.casing3Double;
				reload = 10f;
				bullet = new BasicBulletType(7f, 80f) {{
					lifetime = 30f;
					width = 18f;
					height = 22f;
					shootEffect = Fx.shootBig;
					smokeEffect = Fx.shootBigSmoke;
				}};
			}}, new Weapon(name + "-launcher") {{
				x = 17f;
				y = 14f;
				shootY = 5.75f;
				shootSound = Sounds.shootSnap;
				ejectEffect = Fx.casing2;
				shoot = new ShootSpread(2, 2f);
				reload = 20f;
				bullet = new MissileBulletType(6f, 15f) {{
					width = 8f;
					height = 14f;
					trailColor = Pal.missileYellowBack;
					weaveScale = 2f;
					weaveMag = 2f;
					lifetime = 35f;
					drag = -0.01f;
					splashDamage = 48f;
					splashDamageRadius = 12f;
					frontColor = Pal.missileYellow;
					backColor = Pal.missileYellowBack;
				}};
			}}, new Weapon(name + "-gun-big") {{
				rotate = true;
				rotateSpeed = 3f;
				x = 8f;
				y = 3f;
				shootY = 6.75f;
				shootSound = Sounds.shotgun;
				ejectEffect = Fx.none;
				shoot = new ShootSpread(3, 15f);
				reload = 45f;
				bullet = new ShrapnelBulletType() {{
					toColor = Pal.accent;
					damage = 150f;
					keepVelocity = false;
					length = 150f;
				}};
			}});
			for (int i : Mathf.signs) {
				rotors.add(new Rotor(name + "-rotor1") {{
					mirror = true;
					x = 22.5f;
					y = 21.25f;
					bladeCount = 3;
					speed = 19f * i;
					ghostAlpha = 0.4f;
					shadowAlpha = 0.2f;
					shadeSpeed = 3f * i;
				}}, new Rotor(name + "-rotor2") {{
					mirror = true;
					x = 17.25f;
					y = 1f;
					bladeCount = 2;
					speed = 23f * i;
					ghostAlpha = 0.4f;
					shadowAlpha = 0.2f;
					shadeSpeed = 4f * i;
				}});
			}
			hideDetails = false;
		}};
		mantodea = new CopterUnitType("mantodea") {{
			constructor = CopterUnit::new;
			aiController = CopterAI::new;
			circleTarget = false;
			speed = 5f;
			drag = 0.1f;
			accel = 0.03f;
			fallSpeed = 0.0025f;
			armor = 22f;
			health = 25500f;
			engineSize = 0f;
			flying = true;
			hitSize = 45f;
			lowAltitude = true;
			fallRotateSpeed = 0.8f;
			rotateSpeed = 2.2f;
			ammoType = new ItemAmmoType(Items.surgeAlloy);
			Floatff3<Weapon> gun = (dx, dy, rel) -> new Weapon(name + "-gun") {{
				mirror = true;
				rotate = false;
				x = dx;
				y = dy;
				recoil = 2.5f;
				shootY = 10f;
				shootSound = Sounds.shootBig;
				shoot.shots = 3;
				shoot.shotDelay = 3f;
				reload = rel;
				bullet = new FlakBulletType(13f, 130f) {{
					width = 12f;
					height = 20f;
					pierce = true;
					pierceCap = 2;
					lifetime = 20f;
					collidesGround = true;
					lightning = 3;
					lightningLength = 4;
					lightningLengthRand = 2;
					lightningDamage = 25f;
					lightningColor = Pal.surge;
					shootEffect = Fx.shootBig;
					smokeEffect = Fx.shootBigSmoke;
				}};
			}};
			weapons.add(gun.get(14.25f, 26.5f, 25f), gun.get(26.25f, 19.5f, 15f));
			for (int i : Mathf.signs) {
				rotors.add(new Rotor(name + "-rotor2") {{
					y = -31.25f;
					bladeCount = 4;
					speed = 19f * i;
					ghostAlpha = 0.4f;
					shadowAlpha = 0.2f;
					shadeSpeed = 4f * i;
				}}, new Rotor(name + "-rotor3") {{
					mirror = true;
					x = 28.5f;
					y = -11.75f;
					bladeCount = 3;
					speed = 23f * i;
					ghostAlpha = 0.4f;
					shadowAlpha = 0.2f;
					shadeSpeed = 3f * i;
				}});
			}
			rotors.add(new Rotor(name + "-rotor1") {{
				y = 9.25f;
				bladeCount = 3;
				speed = 29f;
				shadeSpeed = 5f;
				bladeFade = 0.8f;
			}});
			hideDetails = false;
		}};
		//vanilla-tier6
		empire = new BaseUnitType("empire") {{
			constructor = BaseMechUnit::new;
			speed = 0.4f;
			hitSize = 40f;
			rotateSpeed = 1.65f;
			health = 63000f;
			armor = 40f;
			mechStepParticles = true;
			stepShake = 0.8f;
			canDrown = false;
			mechFrontSway = 2f;
			mechSideSway = 0.7f;
			mechStride = (4f + (hitSize - 8f) / 2.1f) / 1.25f;
			drownTimeMultiplier = 8f;
			shadowElevation = 0.1f;
			groundLayer = 74f;
			itemCapacity = 200;
			ammoType = new ItemAmmoType(Items.pyratite);
			ammoCapacity = 600;
			abilities.add(new TerritoryFieldAbility(20 * 8f, 90f, 210f) {{
				open = true;
			}});
			immunities.add(HStatusEffects.territoryFieldSuppress);
			weapons.add(new LimitedAngleWeapon(name + "-cannon") {{
				top = false;
				x = 31.5f;
				y = -6.25f;
				shootY = 30.25f;
				xRand = 4.5f;
				alternate = false;
				rotate = true;
				rotateSpeed = 1.2f;
				inaccuracy = 4f;
				reload = 3f;
				shoot.shots = 2;
				angleCone = 20f;
				angleOffset = -15f;
				shootCone = 20f;
				shootSound = Sounds.flame;
				cooldownTime = 180f;
				bullet = new ParticleFlameBulletType(6.6f, 105f) {{
					lifetime = 42f;
					pierceCap = 6;
					pierceBuilding = true;
					collidesAir = true;
					reflectable = false;
					incendChance = 0.2f;
					incendAmount = 1;
					particleAmount = 23;
					particleSizeScl = 8f;
					particleSpread = 11f;
					hitSize = 9f;
					layer = Layer.bullet - 0.001f;
					status = StatusEffects.melting;
					smokeColors = new Color[]{Pal.darkFlame, Color.darkGray, Color.gray};
					colors = new Color[]{Color.white, Color.valueOf("fff4ac"), Pal.lightFlame, Pal.darkFlame, Color.gray};
				}};
			}}, new LimitedAngleWeapon(name + "-mount") {{
				x = 17.75f;
				y = 11.25f;
				shootY = 5.5f;
				rotate = true;
				rotateSpeed = 7f;
				angleCone = 60f;
				reload = 60f;
				shootCone = 30f;
				shootSound = Sounds.missile;
				bullet = new MissileBulletType(5.5f, 22f) {{
					lifetime = 40f;
					drag = -0.005f;
					width = 14f;
					height = 15f;
					shrinkY = 0f;
					splashDamageRadius = 55f;
					splashDamage = 85f;
					homingRange = 90f;
					weaveMag = 2f;
					weaveScale = 8f;
					hitEffect = despawnEffect = HFx.hitExplosionLarge;
					status = StatusEffects.blasted;
					statusDuration = 60f;
					fragBullets = 5;
					fragLifeMin = 0.9f;
					fragLifeMax = 1.1f;
					fragBullet = new ShrapnelBulletType() {{
						damage = 200f;
						length = 60f;
						width = 12f;
						toColor = Pal.missileYellow;
						hitColor = Pal.bulletYellow;
						hitEffect = HFx.coloredHitSmall;
						serrationLenScl = 5f;
						serrationSpaceOffset = 45f;
						serrationSpacing = 5f;
					}};
				}};
			}});
		}};
		supernova = new BaseUnitType("supernova") {{
			constructor = BaseLegsUnit::new;
			hitSize = 37f;
			health = 59000f;
			armor = 32f;
			flying = false;
			mineSpeed = 7f;
			mineTier = 5;
			buildSpeed = 3f;
			stepShake = 1.8f;
			rotateSpeed = 1.8f;
			mechLandShake = 1.5f;
			drownTimeMultiplier = 8f;
			legCount = 6;
			legLength = 29f;
			legBaseOffset = 8f;
			legMoveSpace = 0.7f;
			legForwardScl = 0.6f;
			hovering = true;
			shadowElevation = 0.23f;
			allowLegStep = true;
			ammoCapacity = 500;
			ammoType = new PowerAmmoType(3500);
			speed = 0.3f;
			groundLayer = Layer.legUnit;
			immunities.addAll(StatusEffects.sapped, StatusEffects.wet, StatusEffects.electrified);
			drawShields = false;
			abilities.add(new EnergyFieldAbility(60, 90, 200) {{
				maxTargets = 25;
				healPercent = 6f;
				hitUnits = false;
				y = -20;
			}});
			weapons.add(new Weapon(name + "-laser") {{
				top = false;
				mirror = false;
				x = 0f;
				y = 0f;
				shootY = 23.75f;
				reload = 170f;
				shootSound = Sounds.beam;
				shoot.firstShotDelay = 59f;
				continuous = true;
				cooldownTime = 200f;
				chargeSound = Sounds.lasercharge2;
				recoil = 0f;
				bullet = new ContinuousLaserBulletType(110f) {{
					length = 340f;
					lifetime = 200f;
					despawnEffect = Fx.smokeCloud;
					smokeEffect = Fx.none;
					chargeEffect = Fx.greenLaserChargeSmall;
					collidesTeam = true;
					incendChance = 0.1f;
					incendSpread = 8f;
					incendAmount = 1;
					healPercent = 1.3f;
					splashDamage = 4f;
					splashDamageRadius = 25f;
					knockback = 3f;
					status = StatusEffects.electrified;
					statusDuration = 30f;
					colors = new Color[]{Pal.heal.cpy().a(.2f), Pal.heal.cpy().a(.5f), Pal.heal.cpy().mul(1.2f), Color.white};
				}
					@Override
					public void hitEntity(Bullet b, Hitboxc entity, float health) {
						if (entity instanceof Healthc h) {
							float damage = b.damage;
							float gain = 1f;
							float shield = 0f;
							if (entity instanceof Shieldc s) {
								gain += ((s.shield() / s.maxHealth()) * 2f);
								shield = Math.max(s.shield(), 0f);
							}
							health += shield;
							h.damage(damage * gain);
						}

						if (entity instanceof Unit unit) {
							Tmp.v3.set(unit).sub(b).nor().scl(knockback * 80f);
							unit.impulse(Tmp.v3);
							unit.apply(status, statusDuration);
						}

						handlePierce(b, health, entity.x(), entity.y());
					}
				};
			}}, new AcceleratingWeapon(name + "-mount") {{
				top = false;
				x = 28f;
				y = 0f;
				shootX = -3.5f;
				shootY = 12f;
				reload = 30f;
				accelCooldownWaitTime = 31f;
				minReload = 5f;
				accelPerShot = 0.5f;
				rotateSpeed = 5f;
				inaccuracy = 5f;
				rotate = true;
				rotationLimit = 20f;
				alternate = false;
				shoot.shots = 2;
				shootSound = Sounds.lasercharge2;
				bullet = new ArrowBulletType(7f, 25f) {{
					lifetime = 60f;
					pierce = true;
					pierceBuilding = true;
					pierceCap = 4;
					backColor = trailColor = hitColor = lightColor = lightningColor = Pal.heal;
					frontColor = Color.white;
					trailWidth = 4f;
					width = 9f;
					height = 15f;
					splashDamage = 15f;
					splashDamageRadius = 25f;
					healPercent = 3f;
					homingRange = 70f;
					homingPower = 0.05f;
				}};
			}}, new RepairBeamWeapon("repair-beam-weapon-center-large") {{
				x = 10.5f;
				y = -4.5f;
				shootY = 6f;
				beamWidth = 1f;
				repairSpeed = 4.2f;
				bullet = new BulletType() {{
					maxRange = 180f;
				}};
			}});
			hideDetails = false;
		}};
		cancer = new BaseUnitType("cancer") {{
			constructor = BaseLegsUnit::new;
			speed = 0.5f;
			hitSize = 33f;
			health = 54000f;
			armor = 38f;
			rotateSpeed = 1.9f;
			drownTimeMultiplier = 4f;
			legCount = 8;
			legMoveSpace = 0.76f;
			legPairOffset = 0.7f;
			legGroupSize = 2;
			legLength = 112f;
			legExtension = -8.25f;
			legBaseOffset = 8f;
			stepShake = 1f;
			mechLandShake = 1f;
			legLengthScl = 1f;
			rippleScale = 2f;
			legSpeed = 0.2f;
			legSplashDamage = 80f;
			legSplashRange = 40f;
			hovering = true;
			allowLegStep = true;
			shadowElevation = 0.95f;
			ammoType = new ItemAmmoType(Items.graphite, 8);
			groundLayer = Layer.legUnit;
			itemCapacity = 200;
			weapons.add(new LimitedAngleWeapon(name + "-launcher") {{
				layerOffset = -0.01f;
				x = 19.7f;
				y = 8.5f;
				shootY = 6.25f - 1f;
				reload = 7f;
				recoil = 1f;
				rotate = true;
				shootCone = 20f;
				angleCone = 60f;
				angleOffset = 45f;
				inaccuracy = 25f;
				xRand = 2.25f; //TODO use something else instead? -Anuke
				shoot.shots = 2;
				shootSound = Sounds.missile;
				bullet = new MissileBulletType(3.7f, 15f) {{
					width = 10f;
					height = 12f;
					shrinkY = 0f;
					drag = -0.01f;
					splashDamageRadius = 30f;
					splashDamage = 55f;
					ammoMultiplier = 5f;
					hitEffect = Fx.blastExplosion;
					despawnEffect = Fx.blastExplosion;
					backColor = trailColor = Pal.sapBulletBack;
					frontColor = lightningColor = lightColor = Pal.sapBullet;
					trailLength = 13;
					homingRange = 80f;
					weaveScale = 8f;
					weaveMag = 2f;
					lightning = 2;
					lightningLength = 2;
					lightningLengthRand = 1;
					lightningCone = 15f;
					status = StatusEffects.blasted;
					statusDuration = 60f;
				}};
			}}, new LimitedAngleWeapon(name + "-mount") {{
				x = 17.75f;
				y = 7.5f;
				shootY = 10.25f - 5f;
				reload = 120f;
				angleCone = 60f;
				rotate = true;
				continuous = true;
				alternate = false;
				rotateSpeed = 1.5f;
				recoil = 5f;
				shootSound = Sounds.laserbeam;
				bullet = HBullets.continuousSapLaser;
			}}, new Weapon(name + "-railgun") {{
				x = 14.5f;
				y = -10f;
				shootY = 20.5f - 4f;
				shootSound = Sounds.artillery;
				rotate = true;
				alternate = true;
				rotateSpeed = 0.9f;
				cooldownTime = 90f;
				reload = 90f;
				shake = 6f;
				recoil = 8f;
				bullet = new RailBulletType() {{
					length = 220f;
					speed = 15f;
					damage = 95f;
					lifetime = 23f;
					splashDamageRadius = 110f;
					splashDamage = 90f;
					hitEffect = Fx.sapExplosion;
					ammoMultiplier = 4f;
					lightning = 3;
					lightningLength = 20;
					smokeEffect = Fx.shootBigSmoke2;
					pointEffect = trailEffect = HFx.coloredRailgunSmallTrail;
					hitShake = 10f;
					lightRadius = 40f;
					trailColor = lightColor = Pal.sap;
					lightOpacity = 0.6f;
					collidesAir = false;
					scaleLife = true;
					pierceCap = 3;
					status = StatusEffects.sapped;
					statusDuration = 60f * 10;
					fragLifeMin = 0.3f;
					fragBullets = 4;
					fragBullet = HBullets.sapArtilleryFrag;
				}};
			}});
		}};
		aphelion = new BaseUnitType("aphelion") {{
			constructor = BaseUnit::new;
			aiController = FlyingAI::new;
			speed = 0.55f;
			accel = 0.04f;
			drag = 0.04f;
			rotateSpeed = 1f;
			baseRotateSpeed = 20f;
			engineOffset = 41f;
			engineSize = 11f;
			flying = true;
			lowAltitude = true;
			health = 60000f;
			hitSize = 62f;
			armor = 45f;
			targetFlags = new BlockFlag[]{BlockFlag.reactor, BlockFlag.battery, BlockFlag.core, null};
			ammoType = new ItemAmmoType(HItems.uranium);
			itemCapacity = 460;
			abilities.add(new EnergyFieldAbility(220f, 90f, 192f) {{
				color = Color.valueOf("ffa665");
				status = StatusEffects.burning;
				statusDuration = 180f;
				maxTargets = 30;
				healPercent = 0.8f;
			}});
			weapons.add(new Weapon() {{
				shake = 1f;
				shootY = 18f;
				x = 0f;
				y = 0f;
				rotateSpeed = 5f;
				reload = 120f;
				recoil = 4f;
				shootSound = Sounds.beam;
				continuous = true;
				cooldownTime = 120f;
				shadow = 20f;
				mirror = false;
				bullet = new ContinuousLaserBulletType(270f) {{
					width = 8f;
					length = 280f;
					drawSize = 200f;
					lifetime = 180f;
					shake = 1f;
					hitEffect = new Effect(21f, e -> {
						Draw.color(Color.white, e.color, e.fin());
						e.scaled(8f, s -> {
							Lines.stroke(0.5f + s.fout());
							Fill.circle(e.x, e.y, s.fin() * 11f);
						});

						Lines.stroke(0.5f + e.fout());
						Angles.randLenVectors(e.id, 6, e.fin() * 35f, e.rotation + 180f, 45f, (x, y) -> Lines.lineAngle(e.x + x, e.y + y, Mathf.angle(x, y), e.fout() * 7f + 1f));
					});
					shootEffect = Fx.shootHeal;
					smokeEffect = Fx.none;
					largeHit = false;
					incendChance = 0.03f;
					incendSpread = 5f;
					incendAmount = 1;
					collidesTeam = true;
					hitColor = Pal.meltdownHit;
				}};
			}}, new Weapon(name + "-mount") {{
				x = 19f;
				y = -18f;
				rotateSpeed = 2f;
				reload = 9f;
				shootSound = Sounds.shootBig;
				shadow = 7f;
				rotate = true;
				recoil = 0.5f;
				shootY = 8f;
				bullet = new FlakBulletType(8f, 35) {{
					shootEffect = Fx.shootTitan;
					ammoMultiplier = 4f;
					splashDamage = 75f;
					splashDamageRadius = 25f;
					collidesGround = true;
					lifetime = 32f;
					status = StatusEffects.blasted;
				}};
			}}, new Weapon(name + "-mount") {{
				x = 30.75f;
				y = -6.25f;
				reload = 8f;
				ejectEffect = Fx.casing1;
				rotateSpeed = 2f;
				shake = 1f;
				shootSound = Sounds.shootBig;
				rotate = true;
				shadow = 7f;
				shootY = 12f;
				bullet = new BasicBulletType(10f, 115f) {{
					width = 11f;
					height = 20f;
					shootEffect = Fx.shootTitan;
					ammoMultiplier = 2f;
					splashDamage = 25f;
					splashDamageRadius = 15f;
					lifetime = 30f;
					pierceArmor = true;
					pierce = true;
					pierceCap = 3;
					status = StatusEffects.melting;
					statusDuration = 330;
				}};
			}});
		}};
		windstorm = new BaseUnitType("windstorm") {{
			constructor = BasePayloadUnit::new;
			aiController = DefenderHealAI::new;
			armor = 41f;
			health = 61000f;
			speed = 0.65f;
			rotateSpeed = 1f;
			accel = 0.04f;
			drag = 0.018f;
			flying = true;
			engineOffset = 28f;
			engineSize = 9f;
			faceTarget = false;
			hitSize = 66f;
			payloadCapacity = (6.5f * 6.5f) * Vars.tilePayload;
			buildSpeed = 4f;
			drawShields = false;
			lowAltitude = true;
			buildBeamOffset = 43f;
			itemCapacity = 540;
			abilities.add(new ForceFieldAbility(180f, 6f, 12000f, 60f * 8, 6, 0f), new RepairFieldAbility(290f, 60f * 2, 160f));
			ammoType = new PowerAmmoType(2500);
			ammoCapacity = 100;
			weapons.add(new HealConeWeapon(name + "-heal-mount") {{
				x = 33.5f;
				y = -7.75f;
				bullet = new HealConeBulletType() {{
					lifetime = 240;
					healPercent = 8;
				}};
				reload = 180;
				rotate = true;
				rotateSpeed = 4;
				alternate = false;
				useAmmo = true;
				continuous = true;
				cooldownTime = 150;
				shootY = 8;
				recoil = 0;
				top = false;
			}}, new EnergyChargeWeapon() {{
				mirror = false;
				x = 0f;
				y = 0f;
				shootY = 0f;
				reload = 30f * 60f;
				shootCone = 360f;
				ignoreRotation = true;
				drawCharge = (unit, mount, charge) -> {
					float rotation = unit.rotation - 90f, wx = unit.x + Angles.trnsx(rotation, x, y), wy = unit.y + Angles.trnsy(rotation, x, y);
					Draw.color(Pal.heal);
					Drawn.shiningCircle(unit.id, Time.time, wx, wy, 13f * charge, 5, 70f, 15f, 6f * charge, 360f);
					Draw.color(Color.white);
					Drawn.shiningCircle(unit.id, Time.time, wx, wy, 6.5f * charge, 5, 70f, 15f, 4f * charge, 360f);
				};
				bullet = new HealingNukeBulletType() {{
					allyStatus = StatusEffects.overclock;
					allyStatusDuration = 15f * 60f;
					//status = HStatusEffects.disabled;
					statusDuration = 120f;
					healPercent = 20f;
				}};
			}});
		}};
		poseidon = new BaseUnitType("poseidon") {{
			constructor = BaseUnitWaterMove::new;
			trailLength = 70;
			waveTrailX = 25f;
			waveTrailY = -32f;
			trailScl = 3.5f;
			armor = 46f;
			drag = 0.2f;
			speed = 0.65f;
			accel = 0.2f;
			hitSize = 60f;
			rotateSpeed = 0.9f;
			health = 63000f;
			itemCapacity = 350;
			ammoType = new ItemAmmoType(HItems.uranium);
			abilities.add(new ShieldRegenFieldAbility(100f, 1500f, 60f * 4, 200f), new TerritoryFieldAbility(220, -1, 150) {{
				active = false;
			}});
			immunities.add(HStatusEffects.territoryFieldSuppress);
			weapons.addAll(new LimitedAngleWeapon(name + "-front-cannon") {{
				layerOffset = -0.01f;
				x = 22f;
				y = 26f;
				shootY = 9.5f;
				recoil = 5f;
				shoot.shots = 5;
				shoot.shotDelay = 3f;
				inaccuracy = 5f;
				shootCone = 15f;
				rotate = true;
				shootSound = Sounds.artillery;
				reload = 25f;
				bullet = new BasicBulletType(8f, 80, "bullet") {{
					hitSize = 5;
					width = 16f;
					height = 23f;
					shootEffect = Fx.shootBig;
					pierceCap = 2;
					pierceBuilding = true;
					knockback = 0.7f;
				}};
			}}, new LimitedAngleWeapon(name + "-side-silo") {{
				layerOffset = -0.01f;
				x = 24f;
				y = -13f;
				shootY = 7f;
				xRand = 9f; //TODO use something else instead? -Anuke
				defaultAngle = angleOffset = 90f;
				angleCone = 0f;
				shootCone = 125f;
				alternate = false;
				rotate = true;
				reload = 50f;
				shoot.shots = 12;
				shoot.shotDelay = 3f;
				inaccuracy = 5f;
				shootSound = Sounds.missile;
				bullet = new GuidedMissileBulletType(3f, 20f) {{
					homingPower = 0.09f;
					width = 8f;
					height = 8f;
					shrinkX = shrinkY = 0f;
					drag = -0.003f;
					keepVelocity = false;
					splashDamageRadius = 40f;
					splashDamage = 45f;
					lifetime = 65f;
					trailColor = Pal.missileYellowBack;
					hitEffect = Fx.blastExplosion;
					despawnEffect = Fx.blastExplosion;
				}};
			}
				@Override
				public void handleBullet(Unit unit, WeaponMount mount, Bullet b) {
					if (b.type instanceof GuidedMissileBulletType) {
						b.data = mount;
					}
				}
			}, new LimitedAngleWeapon(name + "-launcher") {{
				x = 0f;
				y = 21f;
				shootY = 8f;
				rotate = true;
				mirror = false;
				inaccuracy = 15f;
				reload = 7f;
				xRand = 2.25f; //TODO use something else instead? -Anuke
				shootSound = Sounds.missile;
				angleCone = 135f;
				bullet = HBullets.basicMissile;
			}}, new PointDefenceMultiBarrelWeapon(name + "-flak-turret") {{
				x = 23f;
				y = 15f;
				shootY = 15.75f;
				barrels = 2;
				barrelOffset = 5.25f;
				barrelSpacing = 6.5f;
				barrelRecoil = 4f;
				rotate = true;
				mirrorBarrels = true;
				alternate = false;
				reload = 6f;
				recoil = 0.5f;
				shootCone = 7f;
				shadow = 30f;
				targetInterval = 20f;
				autoTarget = true;
				controllable = false;
				bullet = new AntiBulletFlakBulletType(8f, 6f) {{
					lifetime = 45f;
					splashDamage = 12f;
					splashDamageRadius = 60f;
					bulletRadius = 60f;
					explodeRange = 45f;
					bulletDamage = 18f;
					width = 8f;
					height = 12f;
					scaleLife = true;
					collidesGround = false;
					status = StatusEffects.blasted;
					statusDuration = 60f;
				}};
			}}, new Weapon(name + "-railgun") {{
				x = 0f;
				y = 0f;
				shootY = 38.5f;
				mirror = false;
				rotate = true;
				rotateSpeed = 0.7f;
				shadow = 46f;
				reload = 60f * 2.5f;
				shootSound = Sounds.railgun;
				bullet = new RailBulletType() {{
					length = 800f;
					lifetime = 10f;
					speed = 70f;
					damage = 2100f;
					splashDamage = 50f;
					splashDamageRadius = 30f;
					pierceDamageFactor = 0.15f;
					pierceCap = -1;
					pierceEffect = Fx.railHit;
					hitEffect = Fx.massiveExplosion;
					smokeEffect = Fx.shootBig2;
					trailEffect = pointEffect = HFx.coloredArrowTrail;
					fragBullet = new BasicBulletType(3.5f, 18) {{
						width = 9f;
						height = 12f;
						reloadMultiplier = 0.6f;
						ammoMultiplier = 4;
						lifetime = 60f;
					}};
					fragBullets = 2;
					fragRandomSpread = 20f;
					fragLifeMin = 0.4f;
					fragLifeMax = 0.7f;
				}};
			}});
		}};
		leviathan = new BaseUnitType("leviathan") {{
			constructor = BaseUnitWaterMove::new;
			armor = 48f;
			drag = 0.2f;
			speed = 0.7f;
			accel = 0.2f;
			hitSize = 60;
			rotateSpeed = 1f;
			health = 62500f;
			itemCapacity = 800;
			ammoType = new PowerAmmoType(1800f);
			buildSpeed = 12;
			abilities.add(new SuppressionFieldAbility() {{
				orbRadius = 5f;
				particleSize = 3f;
				y = -16f;
				particles = 10;
				color = particleColor = effectColor = Pal.heal;
			}}, new BatteryAbility(80000f, 120f, 120f, 0f, -15f));
			weapons.addAll(new Weapon("emp-cannon-mount") {{
				rotate = true;
				x = 18f;
				y = 7f;
				reload = 65f;
				shake = 3f;
				rotateSpeed = 2f;
				shadow = 30f;
				shootY = 7f;
				recoil = 4f;
				cooldownTime = reload - 10f;
				shootSound = Sounds.laser;
				bullet = new EmpBulletType() {{
					float rad = 100f;
					scaleLife = true;
					lightOpacity = 0.7f;
					unitDamageScl = 0.8f;
					healPercent = 20f;
					timeIncrease = 3f;
					timeDuration = 60f * 20f;
					powerDamageScl = 3f;
					damage = 120;
					hitColor = lightColor = Pal.heal;
					lightRadius = 70f;
					shootEffect = Fx.hitEmpSpark;
					smokeEffect = Fx.shootBigSmoke2;
					lifetime = 80f;
					sprite = "circle-bullet";
					backColor = Pal.heal;
					frontColor = Color.white;
					width = height = 12f;
					shrinkY = 0f;
					speed = 5f;
					trailLength = 20;
					trailWidth = 6f;
					trailColor = Pal.heal;
					trailInterval = 3f;
					splashDamage = 140f;
					splashDamageRadius = rad;
					hitShake = 4f;
					trailRotation = true;
					status = StatusEffects.electrified;
					hitSound = Sounds.plasmaboom;
					clipSize = 250f;
					trailEffect = new Effect(16f, e -> {
						Draw.color(Pal.heal);
						for (int s : Mathf.signs) {
							Drawf.tri(e.x, e.y, 4f, 30f * e.fslope(), e.rotation + 90f * s);
						}
					});
					hitEffect = new Effect(50f, 100f, e -> {
						e.scaled(7f, b -> {
							Draw.color(Pal.heal, b.fout());
							Fill.circle(e.x, e.y, rad);
						});
						Draw.color(Pal.heal);
						Lines.stroke(e.fout() * 3f);
						Lines.circle(e.x, e.y, rad);
						int points = 10;
						float offset = Mathf.randomSeed(e.id, 360f);
						for (int i = 0; i < points; i++) {
							float angle = i * 360f / points + offset;
							Drawf.tri(e.x + Angles.trnsx(angle, rad), e.y + Angles.trnsy(angle, rad), 6f, 50f * e.fout(), angle);
						}
						Fill.circle(e.x, e.y, 12f * e.fout());
						Draw.color();
						Fill.circle(e.x, e.y, 6f * e.fout());
						Drawf.light(e.x, e.y, rad * 1.6f, Pal.heal, e.fout());
					});
				}};
			}}, new BoostWeapon() {{
				controllable = false;
				autoTarget = true;
				x = 0;
				y = -15.5f;
				mirror = false;
				rotate = false;
				baseRotation = 45;
				shootCone = 360;
				shootY = 0;
				shoot = new ShootBarrel() {{
					barrels = new float[]{
							0f, 8f, 0f,
							-8f, 0f, 45f,
							0f, -8f, 90f,
							8f, 0f, 135f
					};
					shots = 4;
					shotDelay = 12;
				}};
				reload = 72;
				inaccuracy = 0;
				shootSound = Sounds.missileSmall;
				bullet = new CtrlMissileBulletType("missile-large") {{
					width = 6f;
					height = 10f;
					status = StatusEffects.electrified;
					damage = 108;
					buildingDamageMultiplier = 1f;
					pierceArmor = true;
					autoHoming = true;
					homingPower = 7.5f;
					homingDelay = 18f;
					backColor = frontColor = lightColor = healColor = hitColor = trailColor = Pal.heal;
					trailLength = 15;
					trailWidth = 2.4f;
					speed = 4f;
					lifetime = 80f;
					hitEffect = despawnEffect = new Effect(30, e -> {
						e.scaled(12, b -> {
							Lines.stroke(2 * e.foutpow(), Color.lightGray);
							Lines.circle(e.x, e.y, 32 * e.finpow());
						});
						Draw.color(e.color);
						Angles.randLenVectors(e.id, 5, 32 * e.finpow(), e.rotation, 360, (x, y) -> Fill.poly(e.x + x, e.y + y, (int) Math.max(3, e.fout() * 12), 12 * e.fout(), Mathf.randomSeed(e.id, 360) + 360 * e.fout()));
					});
					shootEffect = new Effect(24, e -> {
						Draw.color(e.color);
						Angles.randLenVectors(e.id, 3, 16 * e.finpow(), e.rotation, 45, (x, y) -> Fill.poly(e.x + x, e.y + y, (int) Math.max(3, e.fout() * 8), 6.5f * e.fout(), Mathf.randomSeed(e.id, 360) + 180 * e.fout()));
					});
				}
					@Override
					public void update(Bullet b) {
						super.update(b);
						float startTime = homingDelay + 12;
						if (b.time < startTime) {
							float in = b.time / startTime;
							float out = 1 - in;
							out = Interp.fastSlow.apply(out);
							b.initVel(b.rotation(), speed * out + 1f);
						} else {
							float in = Math.min(1, (b.time - startTime) / 30);
							b.initVel(b.rotation(), speed * 2f * in + 1f);
						}
					}

					@Override
					public void hitEntity(Bullet b, Hitboxc entity, float health) {
						super.hitEntity(b, entity, health);

						if (entity instanceof Unit unit) {
							if (unit.shield > 0) {
								HFx.hitOut.at(unit.x, unit.y, b.rotation(), unit);
								unit.health -= damage;
							}
						}
					}

					@Override
					public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
						super.hitTile(b, build, x, y, initialHealth, direct);

						if (build == null || build.dead) return;
						if (build.timeScale() > 1) {
							HFx.hitOut.at(build.x, build.y, b.rotation(), build);
							build.health -= damage;
						}
						build.applySlowdown(0.6f, 30);
					}

					@Override
					public void draw(Bullet b) {
						drawTrail(b);
						drawParts(b);
						float z = Draw.z();
						Draw.z(Layer.effect);
						Draw.color(trailColor);
						Draw.rect(frontRegion, b.x, b.y, width * 1.5f, height * 1.5f, b.rotation() - 90);
						Draw.z(Layer.effect + 1);
						Draw.color(trailColor);
						Draw.rect(frontRegion, b.x, b.y, width * 0.9f, height * 0.9f, b.rotation() - 90);

						Draw.z(z);
						Draw.reset();
					}
				};
			}
				@Override
				public void addStats(UnitType u, Table t) {
					String text = Core.bundle.get("unit.heavy-industry-leviathan-weapon-1.description");
					Elements.collapseTextToTable(t, text);
					super.addStats(u, t);
				}
			});
		}};
		//vanilla-tier6-erekir
		dominate = new BaseUnitType("dominate") {{
			erekir();
			tank();
			constructor = BaseTankUnit::new;
			hitSize = 57f;
			treadPullOffset = 1;
			speed = 0.48f;
			health = 60000f;
			armor = 55f;
			crushDamage = 10f;
			rotateSpeed = 0.8f;
			treadRects = new Rect[]{new Rect(-113f, 34f, 70f, 100f), new Rect(-113f, -113f, 70f, 90f)};
			itemCapacity = 360;
			weapons.add(new Weapon(name + "-weapon") {{
				mirror = false;
				rotate = true;
				layerOffset = 0.1f;
				rotateSpeed = 0.9f;
				shootSound = Sounds.release;
				reload = 180f;
				recoil = 5.5f;
				shake = 5;
				x = 0;
				y = -1f;
				minWarmup = 0.9f;
				parts.addAll(new PartBow() {{
					color = Color.valueOf("feb380");
					turretTk = 6f;
					bowFY = -4f;
					bowMoveY = -33f - bowFY;
					bowTk = 6f;
					bowWidth = 28f;
					bowHeight = 12f;
				}}, new BowHalo() {{
					color = Color.valueOf("feb380");
					stroke = 3f;
					radius = 9f;
					w1 = 2.8f;
					h1 = 6f;
					w2 = 4f;
					h2 = 13f;
					y = -21f;
					sinWave = false;
				}}, new RegionPart("-glow") {{
					color = Color.valueOf("feb380");
					blending = Blending.additive;
					outline = mirror = false;
				}}, new ShapePart() {{
					progress = PartProgress.warmup.delay(0.5f);
					color = Color.valueOf("feb380");
					circle = true;
					hollow = true;
					stroke = 0f;
					strokeTo = 2f;
					radius = 14f;
					layer = Layer.effect;
					y = -21f;
				}}, new AimPart() {{
					layer = Layer.effect;
					y = 15f;
					width = 0.9f;
					length = 10f * 8;
					spacing = 10f;
					color = Color.valueOf("feb380");
				}});
				bullet = new BasicBulletType(10f, 360f) {{
					hitSound = despawnSound = Sounds.explosionbig;
					splashDamage = 860f;
					splashDamageRadius = 12f * 8;
					buildingDamageMultiplier = 0.8f;
					hitEffect = despawnEffect = new ExplosionEffect() {{
						lifetime = 30f;
						waveStroke = 5f;
						waveLife = 10f;
						waveRad = splashDamageRadius;
						waveColor = Color.valueOf("feb380");
						smokes = 7;
						smokeSize = 13f;
						smokeColor = Color.valueOf("feb380");
						smokeRad = splashDamageRadius;
						sparkColor = Color.valueOf("feb380");
						sparks = 14;
						sparkRad = splashDamageRadius;
						sparkLen = 6f;
						sparkStroke = 2f;
					}};
					pierce = true;
					pierceCap = 2;
					pierceBuilding = true;
					trailWidth = 7f;
					trailLength = 12;
					trailColor = Color.valueOf("feb380");
					healPercent = -1f;
					despawnHit = true;
					keepVelocity = false;
					reflectable = false;
				}
					@Override
					public void draw(Bullet b) {
						super.draw(b);
						Draw.color(Color.valueOf("feb380"));
						Drawf.tri(b.x, b.y, 13f, 12f, b.rotation());
					}
				};
			}});
			int i = 0;
			for (float f : new float[]{19f, -19f}) {
				int fi = i++;
				weapons.add(new Weapon(name + "-weapon-small") {{
					reload = 35f + fi * 5;
					x = 18f;
					y = f;
					shootY = 5.5f;
					recoil = 2f;
					rotate = true;
					rotateSpeed = 2f;
					shootCone = 2;
					shootSound = Sounds.dullExplosion;
					bullet = new BasicBulletType(9f, 90f) {{
						sprite = "missile-large";
						width = 8f;
						height = 14.5f;
						lifetime = 38f;
						hitSize = 6.5f;
						pierceCap = 2;
						pierce = true;
						pierceBuilding = true;
						hitColor = backColor = trailColor = Color.valueOf("feb380");
						frontColor = Color.white;
						trailWidth = 2.8f;
						trailLength = 8;
						hitEffect = despawnEffect = Fx.blastExplosion;
						shootEffect = Fx.shootTitan;
						smokeEffect = Fx.shootSmokeTitan;
						splashDamageRadius = 20f;
						splashDamage = 50f;
						trailEffect = Fx.hitSquaresColor;
						trailRotation = true;
						trailInterval = 3f;
						fragBullets = 4;
						fragBullet = new BasicBulletType(5f, 35f) {{
							sprite = "missile-large";
							width = 5f;
							height = 7f;
							lifetime = 15f;
							hitSize = 4f;
							pierceCap = 3;
							pierce = true;
							pierceBuilding = true;
							hitColor = backColor = trailColor = Color.valueOf("feb380");
							frontColor = Color.white;
							trailWidth = 1.7f;
							trailLength = 3;
							drag = 0.01f;
							despawnEffect = hitEffect = Fx.hitBulletColor;
						}};
					}};
				}});
			}
			parts.add(new RegionPart("-glow") {{
				color = Color.red;
				blending = Blending.additive;
				layer = -1f;
				outline = false;
			}});
			fogRadius = 44f;
			ammoCapacity = 500;
			ammoType = new ItemAmmoType(Items.beryllium);
		}};
		oracle = new BaseUnitType("oracle") {{
			erekir();
			constructor = BaseLegsUnit::new;
			drag = 0.1f;
			speed = 0.9f;
			hitSize = 50f;
			health = 47000f;
			armor = 30f;
			rotateSpeed = 1.6f;
			lockLegBase = true;
			legContinuousMove = true;
			legStraightness = 0.4f;
			baseLegStraightness = 1.2f;
			legCount = 8;
			legLength = 40f;
			legForwardScl = 2.4f;
			legMoveSpace = 1.1f;
			rippleScale = 1.2f;
			stepShake = 0.5f;
			legGroupSize = 2;
			legExtension = 2f;
			legBaseOffset = 12f;
			legStraightLength = 1.1f;
			legMaxLength = 1.2f;
			ammoType = new PowerAmmoType(2000);
			ammoCapacity = 500;
			legSplashDamage = 84;
			legSplashRange = 46;
			drownTimeMultiplier = 3f;
			hovering = true;
			shadowElevation = 0.4f;
			groundLayer = Layer.legUnit;
			targetAir = false;
			alwaysShootWhenMoving = true;
			itemCapacity = 340;
			weapons.add(new Weapon("collaris-weapon") {{
				shootSound = Sounds.pulseBlast;
				mirror = true;
				rotationLimit = 30f;
				rotateSpeed = 0.4f;
				rotate = true;
				x = 16.3f;
				y = -12f;
				shootY = 64f / 4f;
				recoil = 4f;
				reload = 130f;
				cooldownTime = reload * 1.2f;
				shake = 7f;
				layerOffset = 0.02f;
				shadow = 10f;
				shootStatus = StatusEffects.slow;
				shootStatusDuration = reload + 1f;
				shoot.shots = 1;
				heatColor = Color.red;
				for (int i = 0; i < 5; i++) {
					int j = i;
					parts.add(new RegionPart("-blade") {{
						under = true;
						layerOffset = -0.001f;
						heatColor = Pal.techBlue;
						heatProgress = PartProgress.heat.add(0.2f).min(PartProgress.warmup);
						progress = PartProgress.warmup.blend(PartProgress.reload, 0.1f);
						x = 13.5f / 4f;
						y = 10f / 4f - j * 2f;
						moveY = 1f - j * 1f;
						moveX = j * 0.3f;
						moveRot = -45f - j * 17f;
						moves.add(new PartMove(PartProgress.reload.inv().mul(1.8f).inv().curve(j / 5f, 0.2f), 0f, 0f, 36f));
					}});
				}
				bullet = new TrailFadeBulletType(10f, 360f) {{
					lifetime = 45f;
					trailLength = 90;
					trailWidth = 3.6f;
					tracers = 2;
					tracerFadeOffset = 20;
					keepVelocity = true;
					tracerSpacing = 10f;
					tracerUpdateSpacing *= 1.25f;
					removeAfterPierce = false;
					hitColor = backColor = lightColor = lightningColor = trailColor = frontColor = Pal.techBlue;
					width = 18f;
					height = 60f;
					homingPower = 0.01f;
					homingRange = 300f;
					homingDelay = 5f;
					hitSound = Sounds.plasmaboom;
					despawnShake = hitShake = 5f;
					pierce = pierceArmor = true;
					pierceCap = 2;
					lightning = 3;
					lightningLength = 6;
					lightningLengthRand = 18;
					lightningDamage = 40f;
					smokeEffect = WrapperEffect.wrap(HFx.hitSparkHuge, hitColor);
					shootEffect = HFx.instShoot(backColor, frontColor);
					despawnEffect = HFx.lightningHitLarge;
					hitEffect = new MultiEffect(HFx.hitSpark(backColor, 75f, 24, 90f, 2f, 12f), HFx.square45_6_45, HFx.lineCircleOut(backColor, 18f, 20, 2), HFx.sharpBlast(backColor, frontColor, 120f, 40f));
					fragBullets = 15;
					fragVelocityMin = 0.5f;
					fragRandomSpread = 130f;
					fragLifeMin = 0.3f;
					fragBullet = new BasicBulletType(5f, 70f) {{
						pierceCap = 2;
						pierce = pierceBuilding = true;
						homingPower = 0.09f;
						homingRange = 150f;
						lifetime = 60f;
						shootEffect = Fx.shootBigColor;
						smokeEffect = Fx.shootSmokeSquareBig;
						frontColor = Color.white;
						hitSound = Sounds.none;
						width = 15f;
						height = 25f;
						lightColor = trailColor = hitColor = backColor = Pal.techBlue;
						lightRadius = 40f;
						lightOpacity = 0.7f;
						trailWidth = 2.2f;
						trailLength = 7;
						trailChance = 0.1f;
						collidesAir = false;
						despawnEffect = Fx.none;
						splashDamage = 46f;
						splashDamageRadius = 30f;
						hitEffect = despawnEffect = new MultiEffect(new ExplosionEffect() {{
							lifetime = 30f;
							waveStroke = 2f;
							waveColor = sparkColor = trailColor;
							waveRad = 5f;
							smokeSize = 0f;
							smokeSizeBase = 0f;
							sparks = 5;
							sparkRad = 20f;
							sparkLen = 6f;
							sparkStroke = 2f;
						}}, Fx.blastExplosion);
					}};
				}};
			}}, new Weapon(name + "-weapon-small") {{
				shootSound = Sounds.malignShoot;
				mirror = true;
				rotate = true;
				rotateSpeed = 3;
				x = 18f;
				y = 13f;
				shootY = 47 / 4f;
				recoil = 3f;
				reload = 40f;
				shake = 3f;
				cooldownTime = 40f;
				shoot.shots = 3;
				inaccuracy = 3f;
				velocityRnd = 0.33f;
				heatColor = Color.red;
				bullet = new MissileBulletType(4.8f, 70f) {{
					homingPower = 0.2f;
					weaveMag = 4;
					weaveScale = 4;
					lifetime = 65f;
					shootEffect = Fx.shootBig2;
					smokeEffect = Fx.shootSmokeTitan;
					splashDamage = 80f;
					splashDamageRadius = 30f;
					frontColor = Color.white;
					hitSound = Sounds.none;
					width = height = 10f;
					lightColor = trailColor = backColor = Pal.techBlue;
					lightRadius = 40f;
					lightOpacity = 0.7f;
					trailWidth = 2.8f;
					trailLength = 20;
					trailChance = 0.1f;
					despawnSound = Sounds.dullExplosion;
					despawnEffect = Fx.none;
					hitEffect = new ExplosionEffect() {{
						lifetime = 20f;
						waveStroke = 2f;
						waveColor = sparkColor = trailColor;
						waveRad = 12f;
						smokeSize = 0f;
						smokeSizeBase = 0f;
						sparks = 10;
						sparkRad = 35f;
						sparkLen = 4f;
						sparkStroke = 1.5f;
					}};
				}};
			}}, new PointDefenseWeapon(name + "-point-defense") {{
				x = 11.2f;
				y = 25f;
				reload = 6f;
				targetInterval = 9f;
				targetSwitchInterval = 12f;
				recoil = 0.5f;
				bullet = new BulletType() {{
					shootSound = Sounds.lasershoot;
					shootEffect = Fx.sparkShoot;
					hitEffect = Fx.pointHit;
					maxRange = 200f;
					damage = 96f;
				}};
			}});
			fogRadius = 52f;
		}};
		havoc = new BaseUnitType("havoc") {{
			erekir();
			constructor = BasePayloadUnit::new;
			aiController = FlyingFollowAI::new;
			envDisabled = 0;
			lowAltitude = false;
			flying = true;
			drag = 0.07f;
			speed = 1f;
			rotateSpeed = 2f;
			accel = 0.1f;
			health = 28000f;
			armor = 27f;
			hitSize = 46f;
			payloadCapacity = Mathf.sqr(7f) * Vars.tilePayload;
			engineSize = 6f;
			engineOffset = 25.25f;
			itemCapacity = 360;
			float orbRad = 8f, partRad = 9f;
			int parts = 9;
			abilities.add(new SuppressionFieldAbility() {{
				orbRadius = orbRad;
				particleSize = partRad;
				y = 10f;
				particles = parts;
			}});
			for (float i : new float[]{14.2f, -14.2f}) {
				abilities.add(new SuppressionFieldAbility() {{
					orbRadius = orbRad;
					particleSize = partRad;
					y = -8f;
					x = i;
					particles = parts;
					display = active = false;
				}});
			}
			weapons.add(new Weapon("disrupt-weapon") {{
				shootSound = Sounds.missileLarge;
				x = 19.5f;
				y = -2.5f;
				mirror = true;
				rotate = true;
				rotateSpeed = 0.4f;
				reload = 70f;
				layerOffset = -20f;
				recoil = 1f;
				rotationLimit = 22f;
				minWarmup = 0.95f;
				shootWarmupSpeed = 0.1f;
				shootY = 2f;
				shootCone = 40f;
				shoot.shots = 3;
				shoot.shotDelay = 10f;
				inaccuracy = 28f;
				parts.add(new RegionPart("-blade") {{
					heatProgress = PartProgress.warmup;
					progress = PartProgress.warmup.blend(PartProgress.reload, 0.15f);
					heatColor = Color.valueOf("9c50ff");
					x = 5 / 4f;
					y = 0f;
					moveRot = -33f;
					moveY = -1f;
					moveX = -1f;
					under = true;
					mirror = true;
				}});
				bullet = new CtrlMissileBulletType(name + "-missile") {{
					shootEffect = Fx.sparkShoot;
					smokeEffect = Fx.shootSmokeTitan;
					hitColor = Pal.suppress;
					maxRange = 5f;
					speed = 5.4f;
					keepVelocity = false;
					homingDelay = 10f;
					trailColor = Pal.sapBulletBack;
					trailLength = 8;
					hitEffect = despawnEffect = new ExplosionEffect() {{
						lifetime = 50f;
						waveStroke = 5f;
						waveLife = 8f;
						waveColor = Color.white;
						sparkColor = smokeColor = Pal.suppress;
						waveRad = 40f;
						smokeSize = 4f;
						smokes = 7;
						smokeSizeBase = 0f;
						sparks = 10;
						sparkRad = 40f;
						sparkLen = 6f;
						sparkStroke = 2f;
					}};
					damage = 180f;
					splashDamage = 220f;
					splashDamageRadius = 30f;
					buildingDamageMultiplier = 0.5f;
					parts.add(new ShapePart() {{
						layer = Layer.effect;
						circle = true;
						y = -0.25f;
						radius = 1.5f;
						color = Pal.suppress;
						colorTo = Color.white;
						progress = PartProgress.life.curve(Interp.pow5In);
					}});
				}};
			}});
			fogRadius = 56f;
			setEnginesMirror(new UnitEngine(95f / 4f, -56f / 4, 5f, 330f), new UnitEngine(89f / 4, -95f / 4, 4f, 315f));
			ammoCapacity = 500;
			ammoType = new ItemAmmoType(Items.beryllium);
		}};
		//miner-erekir
		miner = new BaseUnitType("miner") {{
			erekir();
			constructor = BaseBuildingTetherUnit::new;
			defaultCommand = UnitCommand.mineCommand;
			controller = u -> new MinerPointAI();
			flying = true;
			drag = 0.06f;
			accel = 0.12f;
			speed = 1.5f;
			health = 100;
			engineSize = 1.8f;
			engineOffset = 5.7f;
			range = 50f;
			hitSize = 12f;
			itemCapacity = 20;
			isEnemy = false;
			payloadCapacity = 0;
			mineTier = 10;//The stronghold determines the tier.
			mineSpeed = 1.6f;
			mineWalls = true;
			mineFloor = true;
			useUnitCap = false;
			logicControllable = false;
			playerControllable = false;
			allowedInPayloads = false;
			createWreck = false;
			envEnabled = Env.any;
			envDisabled = Env.none;
			hidden = true;
			targetable = false;
			hittable = false;
			targetPriority = -2;
			setEnginesMirror(new UnitEngine(24 / 4f, -24 / 4f, 2.3f, 315f));
			ammoCapacity = 100;
			ammoType = new PowerAmmoType(600f);
		}};
		largeMiner = new BaseUnitType("large-miner") {{
			erekir();
			constructor = BaseBuildingTetherUnit::new;
			defaultCommand = UnitCommand.mineCommand;
			controller = u -> new MinerPointAI();
			flying = true;
			drag = 0.06f;
			accel = 0.12f;
			speed = 1.5f;
			health = 100;
			engineSize = 2.6f;
			engineOffset = 9.8f;
			range = 50f;
			mineRange = 100f;
			hitSize = 16f;
			itemCapacity = 50;
			isEnemy = false;
			payloadCapacity = 0;
			mineTier = 10;
			mineSpeed = 3.2f;
			mineWalls = true;
			mineFloor = true;
			useUnitCap = false;
			logicControllable = false;
			playerControllable = false;
			allowedInPayloads = false;
			createWreck = false;
			envEnabled = Env.any;
			envDisabled = Env.none;
			hidden = true;
			targetable = false;
			hittable = false;
			targetPriority = -2;
			setEnginesMirror(new UnitEngine(40 / 4f, -40 / 4f, 3f, 315f));
			ammoCapacity = 300;
			ammoType = new PowerAmmoType(1200f);
		}};
		legsMiner = new BaseUnitType("legs-miner") {{
			erekir();
			controller = u -> new MinerDepotAI();
			constructor = BaseBuildingTetherLegsUnit::new;
			isEnemy = false;
			allowedInPayloads = false;
			logicControllable = false;
			playerControllable = false;
			hidden = true;
			hideDetails = false;
			hitSize = 14f;
			speed = 1f;
			rotateSpeed = 2.5f;
			health = 1300;
			armor = 5f;
			omniMovement = false;
			rotateMoveFirst = true;
			itemOffsetY = 5f;
			itemCapacity = 50;
			mineTier = 5;
			mineSpeed = 6f;
			mineWalls = true;
			mineItems.remove(Items.thorium);
			mineItems.addAll(Items.beryllium, Items.graphite, Items.tungsten, HItems.stone, HItems.rareEarth, HItems.gold);
			allowLegStep = true;
			legCount = 6;
			legGroupSize = 3;
			legLength = 12f;
			lockLegBase = true;
			legContinuousMove = true;
			legExtension = -3f;
			legBaseOffset = 5f;
			legMaxLength = 1.1f;
			legMinLength = 0.2f;
			legForwardScl = 1f;
			legMoveSpace = 2.5f;
			hovering = true;
			weapons.add(new Weapon(name + "-weapon") {{
				x = 22f / 4f;
				y = -3f;
				shootX = -3f / 4f;
				shootY = 4.5f / 4f;
				rotate = true;
				rotateSpeed = 35f;
				reload = 35f;
				shootSound = Sounds.laser;
				bullet = new LaserBulletType() {{
					damage = 45f;
					sideAngle = 30f;
					sideWidth = 1f;
					sideLength = 5.25f * 8;
					length = 13.75f * 8f;
					colors = new Color[]{Pal.heal.cpy().a(0.4f), Pal.heal, Color.white};
				}};
			}});
			abilities.add(new RegenAbility() {{
				percentAmount = 1f / (90f * 60f) * 100f;
			}});
			ammoCapacity = 100;
			ammoType = new PowerAmmoType(800f);
		}};
		//other
		vulture = new BaseUnitType("vulture") {{
			constructor = BaseUnit::new;
			aiController = SurroundAI::new;
			weapons.add(new Weapon() {{
				top = false;
				rotate = true;
				alternate = true;
				mirror = false;
				x = 0f;
				y = -10f;
				reload = 25f;
				inaccuracy = 3f;
				ejectEffect = Fx.none;
				bullet = new AccelBulletType(6f, 50f, "missile-large") {{
					shrinkX = shrinkY = 0.35f;
					buildingDamageMultiplier = 1.5f;
					keepVelocity = false;
					velocityBegin = 0.5f;
					velocityIncrease = 3f;
					accelerateBegin = 0.01f;
					accelerateEnd = 0.9f;
					homingPower = 0f;
					hitColor = trailColor = lightningColor = backColor = lightColor = Pal.accent;
					frontColor = Pal.bulletYellow;
					splashDamageRadius = 20;
					splashDamage = damage * 0.3f;
					width = height = 8f;
					trailChance = 0.2f;
					trailParam = 1.75f;
					trailEffect = HFx.trailToGray;
					lifetime = 90f;
					collidesAir = false;
					hitSound = Sounds.explosion;
					hitEffect = HFx.square45_4_45;
					shootEffect = HFx.circleSplash;
					smokeEffect = Fx.shootBigSmoke;
					despawnEffect = HFx.crossBlast(hitColor, 50f);
				}};
				shootSound = HSounds.blaster;
			}});
			abilities.add(new JavelinAbility(20f, 5f, 29f) {{
				minDamage = 5f;
				minSpeed = 2f;
				maxSpeed = 4f;
				magX = 0.2f;
				magY = 0.1f;
			}});
			ammoCapacity = 100;
			ammoType = new PowerAmmoType(600f);
			targetAir = false;
			maxRange = 200;
			engineOffset = 14f;
			engineSize = 4f;
			speed = 5f;
			accel = 0.04f;
			drag = 0.0075f;
			circleTarget = true;
			hitSize = 14f;
			health = 1000f;
			baseRotateSpeed = 1.5f;
			rotateSpeed = 2.5f;
			armor = 10.5f;
			flying = true;
			hideDetails = false;
		}};
		invincibleShip = new BaseUnitType("invincible-ship") {{
			constructor = InvincibleShipUnit::new;
			abilities.add(new RepairFieldAbility(11451.4191981f, 60, 8 * 8), new InvincibleForceFieldAbility(60, 114.514191981f, 1145141919.81f, 300));
			aiController = NullAI::new;
			weapons.add(new Weapon(name + "-weapon") {{
				reload = 7;
				bullet = new BasicBulletType(24.1f, 114514.191981f) {{
					splashDamage = 114514.191981f;
					hitSize = 5f;
					width = 7f;
					height = 35f;
					lifetime = 10f;
					inaccuracy = 0f;
					despawnEffect = Fx.hitBulletSmall;
					keepVelocity = false;
				}
					@Override
					public void hitEntity(Bullet b, Hitboxc entity, float health) {
						super.hitEntity(b, entity, health);

						if (entity instanceof Healthc h && !h.dead()) {
							Call.unitDestroy(h.id());
						}
					}

					@Override
					public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
						super.hitTile(b, build, x, y, initialHealth, direct);

						if (build != null && build.team != b.team) {
							build.killed();
						}
					}
				};
				rotate = true;
				rotateSpeed = 20f;
				x = 3f;
				y = 2f;
			}});
			flying = true;
			speed = 120f;
			hitSize = 12f;
			accel = 0.01f;
			rotateSpeed = 20f;
			baseRotateSpeed = 20f;
			drag = 0.1f;
			health = 11451.4191981f;
			mineSpeed = 1145.14191981f;
			mineTier = 114514;
			buildSpeed = 114.514191981f;
			itemCapacity = 1145;
			canHeal = false;
			engineOffset = 5;
			engineSize = 3;
			payloadCapacity = (256 * 256) * Vars.tilePayload;
			ammoCapacity = 114514;
			coreUnitDock = true;
			mineWalls = true;
			envDisabled = 0;
			isEnemy = false;
		}};
		dpsTesterLand = new BaseUnitType("dps-tester-land") {{
			constructor = DPSMechUnit::new;
			aiController = NullAI::new;
			armor = 10f;
			health = 65535;
			speed = 0.4f;
			rotateSpeed = 2f;
			flying = false;
			hitSize = 25f;
			canDrown = false;
			mechFrontSway = 1f;
			mechStepParticles = true;
			stepShake = 0.15f;
			singleTarget = true;
			mineSpeed = 1145.14191981f;
			mineTier = 114514;
			buildSpeed = 114.514191981f;
			itemCapacity = 1145;
			canBoost = true;
			boostMultiplier = 5;
			mechLandShake = 4;
			engineOffset = 12;
			engineSize = 6;
			lowAltitude = true;
			mineWalls = true;
			envDisabled = 0;
			isEnemy = false;
			ammoCapacity = 114514;
			ammoType = new PowerAmmoType(114.514f);
		}};
		//elite
		tiger = new BaseUnitType("tiger") {{
			constructor = DamageAbsorbMechUnit::new;
			absorption = 0.2f;
			damageMultiplier = 0.9f;
			drawShields = false;
			engineOffset = 18f;
			engineSize = 9f;
			speed = 0.37f;
			hitSize = 48f;
			health = 52500f;
			buildSpeed = 4f;
			armor = 53f;
			envDisabled = Env.none;
			ammoType = new PowerAmmoType(3000f);
			ammoCapacity = 2400;
			weapons.add(new Weapon(name + "-cannon") {{
				top = false;
				rotate = true;
				rotationLimit = 13f;
				rotateSpeed = 0.75f;
				alternate = true;
				shake = 1.5f;
				shootY = 32f;
				x = 32f;
				y = -2f;
				recoil = 3.4f;
				predictTarget = true;
				shootCone = 30f;
				reload = 60f;
				parts.add(new RegionPart("-shooter") {{
					under = turretShading = true;
					outline = true;
					mirror = false;
					moveY = -8f;
					progress = PartProgress.recoil;
				}});
				shoot = new ShootPattern() {{
					shots = 3;
					shotDelay = 3.5f;
				}};
				velocityRnd = 0.075f;
				inaccuracy = 6f;
				ejectEffect = Fx.none;
				bullet = new BasicBulletType(8, 200f, MOD_NAME + "-strike") {{
					trailColor = lightningColor = backColor = lightColor = Pal.techBlue;
					frontColor = Pal.techBlue;
					lightning = 2;
					lightningCone = 360;
					lightningLengthRand = lightningLength = 8;
					homingPower = 0;
					scaleLife = true;
					collides = false;
					trailLength = 15;
					trailWidth = 3.5f;
					splashDamage = lightningDamage = damage;
					splashDamageRadius = 48f;
					lifetime = 95f;
					width = 22f;
					height = 35f;
					trailEffect = HFx.trailToGray;
					trailParam = 3f;
					trailChance = 0.35f;
					hitShake = 7f;
					hitSound = Sounds.explosion;
					hitEffect = HFx.hitSpark(backColor, 75f, 24, 95f, 2.8f, 16);
					smokeEffect = new MultiEffect(HFx.hugeSmokeGray, HFx.circleSplash(backColor, 60f, 8, 60f, 6));
					shootEffect = HFx.hitSpark(backColor, 30f, 15, 35f, 1.7f, 8);
					despawnEffect = HFx.blast(backColor, 60);
					fragBullet = HBullets.basicSkyFrag;
					fragBullets = 5;
					fragLifeMax = 0.6f;
					fragLifeMin = 0.2f;
					fragVelocityMax = 0.35f;
					fragVelocityMin = 0.074f;
				}
					@Override
					public void hitEntity(Bullet b, Hitboxc entity, float health) {
						if (entity instanceof Healthc h && !h.dead()) {
							if (h.health() <= damage) {
								h.kill();
							} else {
								h.health(h.health() - damage);
							}
						}

						if (entity instanceof Unit unit) {
							Tmp.v3.set(unit).sub(b).nor().scl(knockback * 80f);
							unit.impulse(Tmp.v3);
							unit.apply(status, statusDuration);
						}

						handlePierce(b, health, entity.x(), entity.y());
					}

					@Override
					public void hit(Bullet b, float x, float y) {
						super.hit(b, x, y);
						UltFire.createChance(b, splashDamageRadius, 0.4f);
					}
				};
				shootSound = Sounds.artillery;
			}
				@Override
				public void addStats(UnitType u, Table t) {
					String text = Core.bundle.get("unit.heavy-industry-tiger-weapon-0.description");
					Elements.collapseTextToTable(t, text);
					super.addStats(u, t);
				}
			}, new Weapon() {{
				mirror = false;
				rotate = true;
				rotateSpeed = 25f;
				x = 0;
				y = 12f;
				recoil = 2.7f;
				shootY = 7f;
				shootCone = 40f;
				velocityRnd = 0.075f;
				reload = 150f;
				xRand = 18f;
				shoot = new ShootSine() {{
					shots = 12;
					shotDelay = 4f;
				}};
				inaccuracy = 5f;
				ejectEffect = Fx.none;
				bullet = HBullets.annMissile;
				shootSound = HSounds.launch;
			}}, new Weapon() {{
				x = 26f;
				y = -12.5f;
				reload = 60f;
				shoot = new ShootPattern() {{
					shots = 3;
					shotDelay = 8f;
				}};
				shake = 3f;
				shootX = 2;
				xRand = 5;
				mirror = true;
				rotateSpeed = 2.5f;
				alternate = true;
				shootSound = HSounds.launch;
				shootCone = 30f;
				shootY = 5f;
				top = true;
				rotate = true;
				bullet = new BasicBulletType(5.25f, 150f, MOD_NAME + "-strike") {{
					lifetime = 60;
					knockback = 12f;
					width = 11f;
					height = 28f;
					trailWidth = 2.2f;
					trailLength = 20;
					drawSize = 300f;
					homingDelay = 5f;
					homingPower = 0.0075f;
					homingRange = 140f;
					splashDamageRadius = 16f;
					splashDamage = damage * 0.75f;
					backColor = lightColor = lightningColor = trailColor = hitColor = frontColor = Pal.techBlue;
					hitEffect = HFx.circleSplash(backColor, 40f, 4, 40f, 6f);
					despawnEffect = HFx.hitSparkLarge;
					shootEffect = HFx.shootCircleSmall(backColor);
					smokeEffect = Fx.shootBigSmoke2;
					trailChance = 0.6f;
					trailEffect = HFx.trailToGray;
					hitShake = 3f;
					hitSound = Sounds.plasmaboom;
				}};
			}});
			Floatff2<Weapon> cannon = (dx, dy) -> new PointDefenseWeapon(name + "-cannon-small") {{
				x = dx;
				y = dy;
				color = Pal.techBlue;
				mirror = top = alternate = true;
				reload = 6f;
				targetInterval = 6f;
				targetSwitchInterval = 6f;
				bullet = new BulletType() {{
					shootEffect = HFx.shootLineSmall(color);
					hitEffect = HFx.lightningHitSmall;
					hitColor = color;
					maxRange = 240f;
					damage = 150f;
				}};
			}};
			weapons.add(cannon.get(22f, 18f), cannon.get(25f, 2f));
			groundLayer = Layer.legUnit + 0.1f;
			mechLandShake = 12f;
			stepShake = 5f;
			rotateSpeed = 1f;
			fallSpeed = 0.03f;
			mechStepParticles = true;
			canDrown = false;
			mechFrontSway = 2.2f;
			mechSideSway = 0.8f;
			canBoost = true;
			boostMultiplier = 2.5f;
			abilities.add(new MirrorFieldAbility() {{
				strength = 350f;
				max = 22600f;
				regen = 10.5f;
				cooldown = 2200f;
				minAlbedo = 1f;
				maxAlbedo = 1f;
				rotation = false;
				shieldArmor = 22f;
				nearRadius = 160f;
				shapes.add(new ShieldShape(10, 0f, 0f, 0f, 48f) {{
					movement = new ShapeMove() {{
						rotateSpeed = -0.1f;
					}};
				}});
			}});
			fogRadius = 72f;
			immunities.addAll(StatusEffects.unmoving, StatusEffects.blasted, StatusEffects.corroded, StatusEffects.sporeSlowed, StatusEffects.disarmed, StatusEffects.electrified);
		}};
		thunder = new BaseUnitType("thunder") {{
			tank();
			drawShields = false;
			constructor = BaseTankUnit::new;
			damageMultiplier = 0.5f;
			health = 52500f;
			armor = 79f;
			rotateSpeed = 1f;
			speed = 0.66f;
			envDisabled = Env.none;
			hitSize = 47f;
			accel = 0.25f;
			float xo = 231f / 2f, yo = 231f / 2f;
			treadRects = new Rect[]{new Rect(27 - xo, 152 - yo, 56, 73), new Rect(24 - xo, 51 - 9 - yo, 29, 17), new Rect(59 - xo, 18 - 9 - yo, 39, 19)};
			hoverable = hovering = true;
			ammoType = new PowerAmmoType(3000);
			ammoCapacity = 2600;
			crushDamage = 20;
			weapons.add(new Weapon(name + "-weapon") {{
				x = 0f;
				y = -2f;
				rotate = true;
				rotateSpeed = 3.5f;
				mirror = alternate = false;
				layerOffset = 0.15f;
				shootWarmupSpeed /= 2f;
				minWarmup = 0.9f;
				recoil = 2.25f;
				shake = 8f;
				reload = 120f;
				shootY = 27.5f;
				cooldownTime = 45f;
				heatColor = HPal.ancientHeat;
				shoot = new ShootAlternate(12.3f) {{
					shots = 2;
					shotDelay = 0f;
				}};
				inaccuracy = 1.3f;
				shootSound = HSounds.flak;
				bullet = new AccelBulletType(1f, 400f, "missile-large") {{
					lightOpacity = 0.7f;
					healPercent = 20f;
					reflectable = false;
					knockback = 3f;
					impact = true;
					velocityBegin = 1f;
					velocityIncrease = 18f;
					accelerateBegin = 0.05f;
					accelerateEnd = 0.55f;
					pierce = pierceBuilding = true;
					pierceCap = 5;
					lightningColor = backColor = trailColor = hitColor = lightColor = HPal.ancient;
					lightRadius = 70f;
					shootEffect = new WrapperEffect(HFx.shootLine(33f, 32), backColor);
					smokeEffect = HFx.hugeSmokeLong;
					lifetime = 40f;
					frontColor = Color.white;
					lightning = 2;
					lightningDamage = damage / 6f + 10f;
					lightningLength = 7;
					lightningLengthRand = 16;
					splashDamageRadius = 36f;
					splashDamage = damage / 3f;
					width = 13f;
					height = 35f;
					speed = 8f;
					trailLength = 20;
					trailWidth = 2.3f;
					trailInterval = 1.76f;
					hitShake = 8f;
					trailRotation = true;
					keepVelocity = true;
					hitSound = Sounds.plasmaboom;
					trailEffect = new Effect(10f, e -> {
						Draw.color(trailColor, Color.white, e.fout() * 0.66f);
						for (int s : Mathf.signs) {
							Drawn.tri(e.x, e.y, 3f, 30f * Mathf.curve(e.fin(), 0, 0.1f) * e.fout(0.9f), e.rotation + 145f * s);
						}
					});
					hitEffect = new MultiEffect(HFx.square45_6_45, HFx.hitSparkLarge);
					despawnEffect = HFx.lightningHitLarge;
					fragBullet = new EdgeFragBulletType() {{
						hitColor = trailColor = HPal.ancient;
					}};
					fragBullets = 4;
					fragLifeMin = 0.7f;
					fragOnHit = true;
					fragOnAbsorb = true;
				}
					@Override
					public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
						super.hitTile(b, build, x, y, initialHealth, direct);
						if (build.block.armor > 10f || build.block.absorbLasers) b.time(b.lifetime());
					}
				};
				for (int i = 1; i <= 3; i++) {
					int j = i;
					parts.add(new RegionPart("-blade") {{
						progress = PartProgress.warmup.delay((3 - j) * 0.3f).blend(PartProgress.reload, 0.3f);
						heatProgress = PartProgress.heat.add(0.3f).min(PartProgress.warmup);
						mirror = true;
						under = true;
						moveRot = -40f * j;
						moveX = 3f;
						layerOffset = -0.002f;
						x = 11 / 4f;
					}});
				}
			}
				@Override
				public void addStats(UnitType u, Table t) {
					String text = Core.bundle.get("unit.heavy-industry-thunder-weapon-0.description");
					Elements.collapseTextToTable(t, text);
					super.addStats(u, t);
				}
			}, new Weapon(name + "-point-weapon") {{
				reload = 30f;
				x = y = 15f;
				shootY = 7f;
				recoil = 2f;
				mirror = rotate = true;
				rotateSpeed = 4f;
				autoTarget = true;
				controllable = alternate = false;
				shootSound = HSounds.gauss;
				shadow = 20f;
				bullet = new BasicBulletType(12f, 220f) {{
					width = 9f;
					height = 28f;
					trailWidth = 1.3f;
					trailLength = 7;
					lifetime = 39f;
					drag = 0.015f;
					trailColor = hitColor = backColor = lightColor = lightningColor = HPal.ancient;
					frontColor = Color.white;
					pierce = pierceArmor = true;
					pierceCap = 3;
					smokeEffect = Fx.shootSmallSmoke;
					shootEffect = HFx.shootCircleSmall(backColor);
					despawnEffect = HFx.square45_4_45;
					hitEffect = HFx.hitSpark;
					hittable = false;
				}
					public final float percent = 0.008f;

					@Override
					public void hitEntity(Bullet b, Hitboxc entity, float health) {
						if (entity instanceof Healthc h && !h.dead()) {
							float amount = h.health() - h.maxHealth() * percent;
							if (h.health() <= amount) {
								h.kill();
							} else {
								h.health(amount);
							}
						}

						if (entity instanceof Unit unit) {
							Tmp.v3.set(unit).sub(b).nor().scl(knockback * 80f);
							unit.impulse(Tmp.v3);
							unit.apply(status, statusDuration);
						}

						handlePierce(b, health, entity.x(), entity.y());
					}
				};
			}
				@Override
				public void addStats(UnitType u, Table t) {
					String text = Core.bundle.get("unit.heavy-industry-thunder-weapon-1.description");
					Elements.collapseTextToTable(t, text);
					super.addStats(u, t);
				}
			});
			abilities.addAll(new MirrorArmorAbility() {{
				strength = 240f;
				max = 16400f;
				regen = 5.5f;
				cooldown = 2250f;
				minAlbedo = 0.5f;
				maxAlbedo = 0.8f;
				shieldArmor = 26f;
			}});
			drownTimeMultiplier = 26f;
			fogRadius = 68f;
			immunities.addAll(StatusEffects.unmoving, StatusEffects.blasted, StatusEffects.corroded, StatusEffects.sporeSlowed, StatusEffects.disarmed, StatusEffects.electrified);
		}};
		eagle = new BaseUnitType("eagle") {{
			constructor = BaseUnit::new;
			health = 32500f;
			armor = 39f;
			envDisabled = Env.none;
			hitSize = 36f;
			speed = 1.1f;
			accel = 0.06f;
			drag = 0.04f;
			rotateSpeed = 1.5f;
			faceTarget = true;
			flying = true;
			lowAltitude = true;
			targetFlags = BlockFlag.all;
			drawShields = false;
			engineSize = 0f;
			ammoType = new PowerAmmoType(3000);
			ammoCapacity = 2200;
			abilities.addAll(new MirrorFieldAbility() {{
				strength = 350f;
				max = 15800f;
				regen = 8f;
				cooldown = 6500f;
				minAlbedo = 1f;
				maxAlbedo = 1f;
				rotation = false;
				shieldArmor = 22f;
				nearRadius = 160f;
				Floatff2<ShieldShape> alpha = (ofx, ofy) -> new ShieldShape(6, 0, 0, 0, 24) {{
					movement = new ShapeMove() {{
						x = ofx;
						y = ofy;
						rotateSpeed = 0.35f;
						childMoving = new ShapeMove() {{
							rotateSpeed = -0.2f;
						}};
					}};
				}};
				Floatff2<ShieldShape> beta = (ofx, ofy) -> new ShieldShape(5, 0, 0, 0, 24) {{
					movement = new ShapeMove() {{
						x = ofx;
						y = ofy;
						rotateSpeed = -0.25f;
						childMoving = new ShapeMove() {{
							rotateSpeed = 0.15f;
						}};
					}};
				}};
				shapes.addAll(new ShieldShape(10, 0, 0, 0, 56) {{
					movement = new ShapeMove() {{
						rotateSpeed = -0.1f;
					}};
				}}, alpha.get(45f, 0f), alpha.get(-45f, 0f), alpha.get(0f, 45f), alpha.get(0f, -45f), beta.get(50f, 0f), beta.get(-50f, 0f), beta.get(0f, 50f), beta.get(0f, -50f));
			}});
			Floatff2<Weapon> laser = (dx, dy) -> new Weapon(name + "-laser") {{
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
				bullet = new LaserBulletType() {{
					damage = 165f;
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
			weapons.addAll(laser.get(19.25f, 16f), laser.get(13.5f, 33.5f), new Weapon() {{
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
			}}, new PointDefenseWeapon(name + "-laser") {{
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
			}}, new DataWeapon() {{
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
									float angle = Mathf.clamp(Tmp.v2.angle() - u.rotation, -shootCone, shootCone);
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

				/*@Override
				public void addStats(UnitType u, Table t) {
					super.addStats(u, t);

					t.row();

					Table ic = new Table();
					HStatValues.ammo(ic, subBull);
					Collapser coll = new Collapser(ic, true);
					coll.setDuration(0.1f);

					t.table(it -> {
						it.left().defaults().left();

						it.add(Core.bundle.format("bullet.interval", 15));
						it.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
					});
					t.row();
					t.add(coll).padLeft(16);
				}*/

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
		}};
		eipusino = new NucleoidUnitType("eipusino") {{
			outlines = false;
			envDisabled = Env.none;
			constructor = NucleoidUnit::new;
			aiController = NullAI::new;
			drawArrow = false;
			createScorch = false;
			deathExplosionEffect = Fx.none;
			deathSound = HSounds.jumpIn;
			damageMultiplier = 0.01f;
			health = 999;
			engineSize = 0f;
			buildSpeed = 9f;
			engineOffset = 0f;
			itemCapacity = 999;
			armor = 9f;
			speed = 3.75f;
			drawShields = false;
			isEnemy = false;
			flying = true;
			killable = false;
			hittable = false;
			targetable = false;
			faceTarget = false;
			targetPriority = -9;
			mineWalls = true;
			mineFloor = true;
			mineHardnessScaling = false;
			mineSpeed = 9f;
			mineTier = 99;
			fallEffect = HFx.blast(HPal.thurmixRed, 120f);
			targetAir = targetGround = false;
			abilities.add(new WitchServiceAbility(), new RegenProjectorAbility());
			immunities.add(HStatusEffects.apoptosis);
			ammoCapacity = 99999;
			ammoType = new PowerAmmoType(9.9999f);
			weapons.add(new Weapon() {{
				x = 0f;
				y = 0f;
				top = false;
				rotate = true;
				mirror = false;
				shootSound = Sounds.none;
				bullet = new PointBulletType() {{
					trailEffect = shootEffect = despawnEffect = hitEffect = smokeEffect = Fx.none;
					hitSize *= 2f;
					lifetime *= 2.2f;
				}
					@Override
					public void hitEntity(Bullet b, Hitboxc entity, float health) {
						super.hitEntity(b, entity, health);

						if (entity instanceof Unit u && !u.dead()) {
							u.health = -1f;
							u.maxHealth = -1f;
						}
					}

					@Override
					public Bullet create(Entityc owner, Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY, Teamc target) {
						Bullet bullet = Bullet.create();

						return Utils.anyOtherCreate(bullet, this, shooter, null, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY, target);
					}
				};
			}});
		}};
	}

	public static void loadImmunities() {
		empire.immunities.addAll(Vars.content.statusEffects().copy().removeAll(s -> !empire.immunities.contains(s) && (s.reloadMultiplier >= 1 && !s.disarm)));
		poseidon.immunities.addAll(Vars.content.statusEffects().copy().removeAll(s -> s.reloadMultiplier >= 1 && !s.disarm));
		leviathan.immunities.addAll(Vars.content.statusEffects().copy().removeAll(s -> (s == StatusEffects.none || s.healthMultiplier > 1 || s.damage < 0 || s.reloadMultiplier > 1 || s.damageMultiplier > 1 || s.speedMultiplier > 1) && !s.disarm));
		eipusino.immunities.addAll(Vars.content.statusEffects().copy().removeAll(s -> s == StatusEffects.none || (s.healthMultiplier >= 1 && s.damage <= 0 && s.reloadMultiplier >= 1 && s.damageMultiplier >= 1 && s.speedMultiplier >= 1 && !s.disarm)));
	}

	public static class Shooter {
		public final Trail trail = new Trail(45);
		public final float[] param;

		public float x, y;

		public Shooter() {
			param = new float[9];

			for (int d = 0; d < 3; d++) {
				param[d * 3] = Mathf.random(0.5f, 3f) / (d + 1) * Mathf.randomSign();
				param[d * 3 + 1] = Mathf.random(0f, 360f);
				param[d * 3 + 2] = Mathf.random(18f, 48f) / ((d + 1) * (d + 1));
			}
		}
	}
}
