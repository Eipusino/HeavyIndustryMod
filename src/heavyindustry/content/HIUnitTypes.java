package heavyindustry.content;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import heavyindustry.ai.*;
import heavyindustry.core.*;
import heavyindustry.entities.abilities.*;
import heavyindustry.entities.bullet.*;
import heavyindustry.entities.effect.*;
import heavyindustry.entities.part.*;
import heavyindustry.gen.*;
import heavyindustry.graphics.*;
import heavyindustry.type.unit.*;
import heavyindustry.type.weapons.*;
import heavyindustry.ui.*;
import mindustry.ai.*;
import mindustry.ai.types.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.abilities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.effect.*;
import mindustry.entities.part.*;
import mindustry.entities.pattern.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.ammo.*;
import mindustry.type.unit.*;
import mindustry.type.weapons.*;
import mindustry.world.meta.*;

import static arc.Core.*;
import static heavyindustry.core.HeavyIndustryMod.*;
import static mindustry.Vars.*;

/**
 * Defines the {@linkplain UnitType units} this mod offers.
 *
 * @author Eipusino
 */
public final class HIUnitTypes {
    public static UnitType
            //vanilla-tank
            vanguard, striker, counterattack, crush, destruction, purgatory,
            //vanilla-copter
            caelifera, schistocerca, anthophila, vespula, lepidoptera, mantodea,
            //vanilla-tier6
            suzerain, supernova, cancer, sunlit, windstorm, mosasaur, killerWhale,
            //vanilla-tier6-erekir
            dominate, oracle, havoc,
            //miner-erekir
            miner, largeMiner, legsMiner,
            //other
            armoredCarrierVehicle, pioneer, vulture,
            burner, shadowBlade, artilleryFirePioneer,
            //elite
            tiger, thunder,
            //boss
            vast;

    /** Don't let anyone instantiate this class. */
    private HIUnitTypes() {}

    /** Instantiates all contents. Called in the main thread in {@link HeavyIndustryMod#loadContent()}. */
    public static void load() {
        //vanilla-tank
        vanguard = new UnitType("vanguard") {{
            constructor = TankUnit::create;
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
                activeEffect = HIFx.circle;
            }});
            weapons.add(new Weapon(name("vanguard-weapon")) {{
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
        striker = new UnitType("striker") {{
            constructor = TankUnit::create;
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
            weapons.add(new Weapon(name("striker-weapon")) {{
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
                bullet = Bullets.placeholder;
            }});
        }};
        counterattack = new UnitType("counterattack") {{
            constructor = TankUnit::create;
            treadFrames = 8;
            treadPullOffset = 8;
            treadRects = new Rect[]{new Rect(-45f, -45f, 24f, 88f)};
            speed = 1.3f;
            hitSize = 20f;
            ammoType = new ItemAmmoType(Items.blastCompound);
            ammoCapacity = 16;
            squareShape = true;
            omniMovement = false;
            rotateMoveFirst = false;
            health = 1200f;
            armor = 13f;
            rotateSpeed = 2f;
            itemCapacity = 0;
            faceTarget = false;
            weapons.add(new Weapon(name("counterattack-weapon")) {{
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
                bullet = Bullets.placeholder;
            }});
        }};
        crush = new UnitType("crush") {{
            constructor = TankUnit::create;
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
            ammoCapacity = 120;
            targetAir = true;
            health = 11000f;
            armor = 16f;
            rotateSpeed = 1.5f;
            itemCapacity = 0;
            faceTarget = false;
            immunities = ObjectSet.with(StatusEffects.burning, StatusEffects.shocked);
            targetFlags = new BlockFlag[]{BlockFlag.repair, BlockFlag.turret};
            abilities.add(new StatusFieldAbility(StatusEffects.overclock, 1200f, 1200f, 45f) {{
                applyEffect = Fx.none;
                activeEffect = HIFx.circle;
            }});
            weapons.add(new Weapon(name("crush-weapon")) {{
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
                bullet = Bullets.placeholder;
            }});
        }};
        destruction = new UnitType("destruction") {{
            constructor = TankUnit::create;
            squareShape = true;
            omniMovement = false;
            rotateMoveFirst = false;
            speed = 1f;
            hitSize = 48f;
            drownTimeMultiplier = 2.6f;
            crushDamage = 6f;
            treadRects = new Rect[]{new Rect(-86f, -108f, 42f, 112f), new Rect(-72f, -124f, 21f, 16f), new Rect(-86f, 9f, 42f, 119f)};
            ammoType = new ItemAmmoType(Items.surgeAlloy);
            ammoCapacity = 50;
            targetAir = true;
            health = 28000f;
            armor = 28f;
            rotateSpeed = 1.22f;
            itemCapacity = 30;
            faceTarget = false;
            immunities = ObjectSet.with(StatusEffects.burning);
            targetFlags = new BlockFlag[]{BlockFlag.repair, BlockFlag.turret};
            weapons.add(new Weapon(name("destruction-weapon")) {{
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
                shake = 8;
                bullet = Bullets.placeholder;
                parts.add(new RegionPart("-barrel") {{
                    mirror = true;
                    under = true;
                    moveY = -4;
                    heatProgress = PartProgress.recoil;
                    progress = PartProgress.recoil;
                }});
            }});
        }};
        purgatory = new UnitType("purgatory") {{
            constructor = TankUnit::create;
            squareShape = true;
            omniMovement = false;
            rotateMoveFirst = false;
            drownTimeMultiplier = 5f;
            speed = 0.8f;
            crushDamage = 10f;
            treadRects = new Rect[]{new Rect(-115f, 118f, 52f, 48f), new Rect(-118f, -160f, 79f, 144f)};
            hitSize = 66f;
            immunities = ObjectSet.with(StatusEffects.burning);
            ammoType = new ItemAmmoType(Items.surgeAlloy);
            ammoCapacity = 80;
            targetAir = true;
            health = 82000f;
            armor = 36f;
            drag = 0.3f;
            rotateSpeed = 1f;
            itemCapacity = 55;
            faceTarget = false;
            weapons.add(new Weapon(name("purgatory-weapon")) {{
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
                bullet = Bullets.placeholder;
            }});
        }};
        //vanilla-copter
        caelifera = new CopterUnitType("caelifera") {{
            constructor = CopterUnit::create;
            aiController = FlyingAI::new;
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
            weapons.add(new Weapon(name("caelifera-gun")) {{
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
            }}, new Weapon(name("caelifera-launcher")) {{
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
            rotors.add(new Rotor(name("caelifera-rotor")) {{
                x = 0f;
                y = 6f;
            }});
            hideDetails = false;
        }};
        schistocerca = new CopterUnitType("schistocerca") {{
            constructor = CopterUnit::create;
            aiController = FlyingAI::new;
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
            weapons.add(new Weapon(name("schistocerca-gun")) {{
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
            }}, new Weapon(name("schistocerca-gun")) {{
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
            }}, new Weapon(name("schistocerca-gun-big")) {{
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
                rotors.add(new Rotor(name("schistocerca-rotor")) {{
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
            constructor = CopterUnit::create;
            aiController = FlyingAI::new;
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
            weapons.add(new Weapon(name("anthophila-gun")) {{
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
            }}, new Weapon(name("anthophila-tesla")) {{
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
                rotors.add(new Rotor(name("anthophila-rotor2")) {{
                    x = 0f;
                    y = -13f;
                    bladeCount = 2;
                    ghostAlpha = 0.4f;
                    shadowAlpha = 0.2f;
                    shadeSpeed = 3f * i;
                    speed = 29f * i;
                }});
            }
            rotors.add(new Rotor(name("anthophila-rotor1")) {{
                mirror = true;
                x = 13f;
                y = 3f;
                bladeCount = 3;
            }});
            hideDetails = false;
        }};
        vespula = new CopterUnitType("vespula") {{
            constructor = CopterUnit::create;
            aiController = FlyingAI::new;
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
            weapons.add(new Weapon(name("vespula-gun-big")) {{
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
            }}, new Weapon(name("vespula-gun")) {{
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
            }}, new Weapon(name("vespula-laser-gun")) {{
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
                rotors.add(new Rotor(name("vespula-rotor")) {{
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
            constructor = CopterUnit::create;
            aiController = FlyingAI::new;
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
            weapons.add(new Weapon(name("lepidoptera-gun")) {{
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
            }}, new Weapon(name("lepidoptera-launcher")) {{
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
            }}, new Weapon(name("lepidoptera-gun-big")) {{
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
                rotors.add(new Rotor(name("lepidoptera-rotor1")) {{
                    mirror = true;
                    x = 22.5f;
                    y = 21.25f;
                    bladeCount = 3;
                    speed = 19f * i;
                    ghostAlpha = 0.4f;
                    shadowAlpha = 0.2f;
                    shadeSpeed = 3f * i;
                }}, new Rotor(name("lepidoptera-rotor2")) {{
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
            constructor = CopterUnit::create;
            aiController = FlyingAI::new;
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
            for (float[] i : new float[][]{{14.25f, 26.5f, 25f}, {26.25f, 19.5f, 15f}}) {
                weapons.add(new Weapon(name("mantodea-gun")) {{
                    mirror = true;
                    rotate = false;
                    x = i[0];
                    y = i[1];
                    recoil = 2.5f;
                    shootY = 10f;
                    shootSound = Sounds.shootBig;
                    shoot.shots = 3;
                    shoot.shotDelay = 3f;
                    reload = i[2];
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
                }});
            }
            for (int i : Mathf.signs) {
                rotors.add(new Rotor(name("mantodea-rotor2")) {{
                    y = -31.25f;
                    bladeCount = 4;
                    speed = 19f * i;
                    ghostAlpha = 0.4f;
                    shadowAlpha = 0.2f;
                    shadeSpeed = 4f * i;
                }}, new Rotor(name("mantodea-rotor3")) {{
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
            rotors.add(new Rotor(name("mantodea-rotor1")) {{
                y = 9.25f;
                bladeCount = 3;
                speed = 29f;
                shadeSpeed = 5f;
                bladeFade = 0.8f;
            }});
            hideDetails = false;
        }};
        //vanilla-tier6
        suzerain = new UnitType("suzerain") {{
            constructor = MechUnit::create;
            speed = 0.4f;
            hitSize = 40f;
            rotateSpeed = 1.65f;
            health = 63000f;
            armor = 40f;
            mechStepParticles = true;
            stepShake = 1f;
            drownTimeMultiplier = 8f;
            mechFrontSway = 1.9f;
            mechSideSway = 0.6f;
            shadowElevation = 0.1f;
            groundLayer = 74f;
            itemCapacity = 200;
            ammoType = new ItemAmmoType(HIItems.uranium);
            abilities.add(new TerritoryFieldAbility(20 * 8f, 90f, 210f) {{
                open = true;
            }});
            immunities = ObjectSet.with(HIStatusEffects.territoryFieldSuppress);
            weapons.add(new LimitedAngleWeapon(name("suzerain-mount")) {{
                x = 20.75f;
                y = 10f;
                shootY = 6.25f;
                rotate = true;
                rotateSpeed = 7f;
                angleCone = 60f;
                reload = 60f;
                shootCone = 30f;
                shootSound = Sounds.missile;
                bullet = new MissileBulletType(2.5f, 22f) {{
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
                    hitEffect = despawnEffect = HIFx.hitExplosionLarge;
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
                        hitEffect = HIFx.coloredHitSmall;
                        serrationLenScl = 5f;
                        serrationSpaceOffset = 45f;
                        serrationSpacing = 5f;
                    }};
                }};
            }}, new LimitedAngleWeapon(name("suzerain-cannon")) {{
                y = -1f;
                x = 28f;
                angleCone = 15f;
                shootY = 17f;
                reload = 36f;
                recoil = 5f;
                shake = 2f;
                shoot.shots = 3;
                shoot.shotDelay = 4f;
                ejectEffect = Fx.casing4;
                shootSound = Sounds.bang;
                bullet = new BasicBulletType(15f, 225f) {{
                    pierceArmor = true;
                    pierce = true;
                    pierceCap = 10;
                    width = 16f;
                    height = 37f;
                    lifetime = 22f;
                    shootEffect = Fx.shootBig;
                    fragVelocityMin = 0.4f;
                    hitEffect = Fx.blastExplosion;
                    splashDamage = 40f;
                    splashDamageRadius = 22f;
                    status = StatusEffects.melting;
                    statusDuration = 330f;
                    fragBullets = 1;
                    fragLifeMin = 0f;
                    fragRandomSpread = 30f;
                    fragBullet = new BasicBulletType(9f, 70f) {{
                        width = 10f;
                        height = 11f;
                        pierceArmor = true;
                        pierce = true;
                        pierceBuilding = true;
                        pierceCap = 3;
                        lifetime = 20f;
                        hitEffect = Fx.flakExplosion;
                        splashDamage = 40f;
                        splashDamageRadius = 12f;
                    }};
                }};
            }});
        }};
        supernova = new UnitType("supernova") {{
            constructor = LegsUnit::create;
            hitSize = 41f;
            health = 59000f;
            armor = 32f;
            flying = false;
            mineSpeed = 7f;
            mineTier = 5;
            buildSpeed = 3f;
            stepShake = 1.8f;
            rotateSpeed = 1.8f;
            drownTimeMultiplier = 8f;
            legCount = 6;
            legLength = 24f;
            legBaseOffset = 3f;
            legMoveSpace = 1.5f;
            legForwardScl = 0.58f;
            hovering = true;
            allowLegStep = true;
            shadowElevation = 0.2f;
            ammoType = new PowerAmmoType(3500);
            groundLayer = 75f;
            speed = 0.3f;
            immunities = ObjectSet.with(StatusEffects.sapped, StatusEffects.wet, StatusEffects.electrified);
            drawShields = false;
            abilities.add(new EnergyFieldAbility(60, 90, 200) {{
                maxTargets = 25;
                healPercent = 6f;
                hitUnits = false;
                y = -20;
            }});
            weapons.add(new Weapon() {{
                mirror = false;
                x = 0f;
                y = 0f;
                shootY = 16.75f;
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
            }}, new AcceleratingWeapon(name("supernova-mount")) {{
                x = 18.25f;
                y = -7.25f;
                shootY = 17f;
                reload = 30f;
                accelCooldownWaitTime = 31f;
                minReload = 5f;
                accelPerShot = 0.5f;
                rotateSpeed = 5f;
                inaccuracy = 5f;
                rotate = true;
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
        }};
        cancer = new UnitType("cancer") {{
            constructor = LegsUnitLegacyToxopid::create;
            speed = 0.5f;
            hitSize = 33f;
            health = 54000f;
            armor = 38f;
            rotateSpeed = 1.9f;
            drownTimeMultiplier = 4f;
            legCount = 8;
            legMoveSpace = 0.8f;
            legPairOffset = 3;
            legLength = 80f;
            legExtension = -22;
            legBaseOffset = 8f;
            stepShake = 1f;
            legLengthScl = 0.93f;
            rippleScale = 3.4f;
            legSpeed = 0.18f;
            ammoType = new ItemAmmoType(Items.graphite, 8);
            legSplashDamage = 100f;
            legSplashRange = 64f;
            hovering = true;
            shadowElevation = 0.95f;
            groundLayer = Layer.legUnit;
            itemCapacity = 200;
            weapons.add(new LimitedAngleWeapon(name("cancer-launcher")) {{
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
            }}, new LimitedAngleWeapon(name("cancer-mount")) {{
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
                bullet = HIBullets.continuousSapLaser;
            }}, new Weapon(name("cancer-railgun")) {{
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
                    speed = 15f;
                    damage = 95f;
                    lifetime = 23f;
                    splashDamageRadius = 110f;
                    splashDamage = 90f;
                    hitEffect = Fx.sapExplosion;
                    ammoMultiplier = 4f;
                    trailEffect = HIFx.coloredRailgunSmallTrail;
                    lightning = 3;
                    lightningLength = 20;
                    smokeEffect = Fx.shootBigSmoke2;
                    hitShake = 10f;
                    lightRadius = 40f;
                    lightColor = Pal.sap;
                    lightOpacity = 0.6f;
                    collidesAir = false;
                    scaleLife = true;
                    pierceCap = 3;
                    status = StatusEffects.sapped;
                    statusDuration = 60f * 10;
                    fragLifeMin = 0.3f;
                    fragBullets = 4;
                    fragBullet = HIBullets.sapArtilleryFrag;
                }};
            }});
        }};
        sunlit = new UnitType("sunlit") {{
            constructor = UnitEntity::create;
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
            ammoType = new ItemAmmoType(HIItems.uranium);
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
                x = 18f;
                y = -20f;
                rotateSpeed = 5f;
                reload = 120f;
                recoil = 4f;
                shootSound = Sounds.beam;
                continuous = true;
                cooldownTime = 120f;
                shadow = 20f;
                rotate = true;
                bullet = new ContinuousLaserBulletType() {{
                    damage = 72f;
                    width = 6f;
                    length = 300f;
                    drawSize = 200f;
                    lifetime = 180f;
                    shake = 1f;
                    hitEffect = Fx.hitMeltHeal;
                    shootEffect = Fx.shootHeal;
                    smokeEffect = Fx.none;
                    largeHit = false;
                    incendChance = 0.03f;
                    incendSpread = 5f;
                    incendAmount = 1;
                    collidesTeam = true;
                }};
            }}, new Weapon(name("sunlit-mount")) {{
                x = 16f;
                y = 18f;
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
            }}, new Weapon(name("sunlit-mount")) {{
                y = 32f;
                x = -16f;
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
        windstorm = new UnitType("windstorm") {{
            constructor = PayloadUnit::create;
            aiController = HealingDefenderAI::new;
            armor = 41f;
            health = 61000f;
            speed = 0.8f;
            rotateSpeed = 1f;
            accel = 0.04f;
            drag = 0.018f;
            flying = true;
            engineOffset = 28f;
            engineSize = 9f;
            faceTarget = false;
            hitSize = 66f;
            payloadCapacity = (6.5f * 6.5f) * tilePayload;
            buildSpeed = 4f;
            drawShields = false;
            lowAltitude = true;
            buildBeamOffset = 43f;
            itemCapacity = 540;
            abilities.add(new ForceFieldAbility(180f, 6f, 12000f, 60f * 8, 6, 0f), new RepairFieldAbility(290f, 60f * 2, 160f));
            ammoType = new PowerAmmoType(2500);
            weapons.add(new HealConeWeapon("windstorm-heal-mount") {{
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
                y = 10.75f;
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
                bullet = Bullets.placeholder;
            }});
        }};
        mosasaur = new UnitType("mosasaur") {{
            constructor = UnitWaterMove::create;
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
            ammoType = new ItemAmmoType(HIItems.uranium);
            abilities.add(new ShieldRegenFieldAbility(100f, 1500f, 60f * 4, 200f), new TerritoryFieldAbility(220, -1, 150) {{
                active = false;
            }});
            immunities = ObjectSet.with(HIStatusEffects.territoryFieldSuppress);
            weapons.addAll(new LimitedAngleWeapon(name("mosasaur-front-cannon")) {{
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
            }}, new LimitedAngleWeapon(name("mosasaur-side-silo")) {{
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
            }, new LimitedAngleWeapon(name("mosasaur-launcher")) {{
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
                bullet = HIBullets.basicMissile;
            }}, new PointDefenceMultiBarrelWeapon(name("mosasaur-flak-turret")) {{
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
            }}, new Weapon(name("mosasaur-railgun")) {{
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
                    lifetime = 10f;
                    speed = 70f;
                    damage = 2100f;
                    splashDamage = 50f;
                    splashDamageRadius = 30f;
                    pierceDamageFactor = 0.15f;
                    pierceCap = -1;
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
                    trailEffect = HIFx.coloredArrowTrail;
                }};
            }});
        }};
        killerWhale = new UnitType("killer-whale") {{
            constructor = UnitWaterMove::create;
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
                shootSound = Sounds.blaster;
                bullet = new CtrlMissileBulletType("missile-large", 6, 10) {{
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
                                HIFx.hitOut.at(unit.x, unit.y, b.rotation(), unit);
                                unit.health -= damage;
                            }
                        }
                    }

                    @Override
                    public void hitTile(Bullet b, Building build, float x, float y, float initialHealth, boolean direct) {
                        super.hitTile(b, build, x, y, initialHealth, direct);
                        if (build == null || build.dead) return;
                        if (build.timeScale() > 1) {
                            HIFx.hitOut.at(build.x, build.y, b.rotation(), build);
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
                    String text = bundle.get("unit.heavy-industry-killer-whale-weapon-1.description");
                    UIUtils.collapseTextToTable(t, text);
                    super.addStats(u, t);
                }
            });
        }};
        //vanilla-tier6-erekir
        dominate = new TankUnitType("dominate") {{
            constructor = TankUnit::create;
            hitSize = 57f;
            treadPullOffset = 1;
            speed = 0.48f;
            health = 60000f;
            armor = 55f;
            crushDamage = 10f;
            rotateSpeed = 0.8f;
            treadRects = new Rect[]{new Rect(-113f, 34f, 70f, 100f), new Rect(-113f, -113f, 70f, 90f)};
            itemCapacity = 360;
            weapons.add(new Weapon(name("dominate-weapon")) {{
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
                bullet = new BasicBulletType(10f, 660f) {{
                    hitSound = despawnSound = Sounds.explosionbig;
                    splashDamage = 960f;
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
                weapons.add(new Weapon(name("dominate-weapon-small")) {{
                    reload = 35f + fi * 5;
                    x = 18f;
                    y = f;
                    shootY = 5.5f;
                    recoil = 2f;
                    rotate = true;
                    rotateSpeed = 2f;
                    shootCone = 2;
                    shootSound = Sounds.dullExplosion;
                    bullet = new BasicBulletType(9f, 150) {{
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
                        fragBullet = new BasicBulletType(5f, 35) {{
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
        }};
        oracle = new ErekirUnitType("oracle") {{
            constructor = LegsUnit::create;
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
                    int fi = i;
                    parts.add(new RegionPart("-blade") {{
                        under = true;
                        layerOffset = -0.001f;
                        heatColor = Pal.techBlue;
                        heatProgress = PartProgress.heat.add(0.2f).min(PartProgress.warmup);
                        progress = PartProgress.warmup.blend(PartProgress.reload, 0.1f);
                        x = 13.5f / 4f;
                        y = 10f / 4f - fi * 2f;
                        moveY = 1f - fi * 1f;
                        moveX = fi * 0.3f;
                        moveRot = -45f - fi * 17f;
                        moves.add(new PartMove(PartProgress.reload.inv().mul(1.8f).inv().curve(fi / 5f, 0.2f), 0f, 0f, 36f));
                    }});
                }
                bullet = new TrailFadeBulletType(7f, 360f) {{
                    lifetime = 60f;
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
                    pierce = pierceArmor = pierceBuilding = true;
                    lightning = 3;
                    lightningLength = 6;
                    lightningLengthRand = 18;
                    lightningDamage = 40f;
                    smokeEffect = WrapperEffect.wrap(HIFx.hitSparkHuge, hitColor);
                    shootEffect = HIFx.instShoot(backColor, frontColor);
                    despawnEffect = HIFx.lightningHitLarge;
                    hitEffect = new MultiEffect(HIFx.hitSpark(backColor, 75f, 24, 90f, 2f, 12f), HIFx.square45_6_45, HIFx.lineCircleOut(backColor, 18f, 20, 2), HIFx.sharpBlast(backColor, frontColor, 120f, 40f));
                    fragBullets = 15;
                    fragVelocityMin = 0.5f;
                    fragRandomSpread = 130f;
                    fragLifeMin = 0.3f;
                    fragBullet = new BasicBulletType(5f, 70f) {{
                        pierceCap = 2;
                        pierceBuilding = true;
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
            }}, new Weapon(name("oracle-weapon-small")) {{
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
            }}, new PointDefenseWeapon(name("oracle-point-defense")) {{
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
        }};
        havoc = new ErekirUnitType("havoc") {{
            constructor = PayloadUnit::create;
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
            payloadCapacity = Mathf.sqr(7f) * tilePayload;
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
                shoot.shotDelay = 5f;
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
                bullet = new CtrlMissileBulletType(name("havoc-missile"), -1, -1) {{
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
                    damage = 160;
                    splashDamage = 220;
                    splashDamageRadius = 30;
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
            setEnginesMirror(new UnitEngine(95f / 4f, -56f / 4, 5f, 330f), new UnitEngine(89f / 4, -95f / 4, 4f, 315f));
        }};
        //miner-erekir
        miner = new ErekirUnitType("miner") {{
            constructor = BuildingTetherPayloadUnit::create;
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
        }};
        largeMiner = new ErekirUnitType("large-miner") {{
            constructor = BuildingTetherPayloadUnit::create;
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
        }};
        legsMiner = new ErekirUnitType("legs-miner") {{
            constructor = BuildingTetherPayloadLegsUnit::create;
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
            abilities.add(new RegenAbility() {{
                percentAmount = 1f / (90f * 60f) * 100f;
            }});
        }};
        //other
        armoredCarrierVehicle = new UnitType("armored-carrier-vehicle") {{
            constructor = TankUnit::create;
            healFlash = false;
            treadFrames = 16;
            treadPullOffset = 8;
            crushDamage = 2f;
            treadRects = new Rect[]{new Rect(-60f, -76f, 24f, 152f)};
            rotateSpeed = 0.7f;
            speed = 0.55f;
            accel = 0.08f;
            drag = 0.08f;
            hitSize = 32f;
            ammoType = new PowerAmmoType(10000f);
            ammoCapacity = 30;
            hovering = true;
            canDrown = false;
            health = 8700f;
            armor = 24f;
            itemCapacity = 2000;
            faceTarget = false;
            itemOffsetY = 15f;
            lightRadius = 60f;
            fogRadius = 30f;
            deathExplosionEffect = new MultiEffect(HIFx.explodeImpWave);
        }};
        pioneer = new UnitType("pioneer") {{
            constructor = PayloadLegsUnit::create;
            drag = 0.1f;
            speed = 0.62f;
            hitSize = 23f;
            health = 7200f;
            armor = 14f;
            rotateSpeed = 2.7f;
            legCount = 6;
            legMoveSpace = 1f;
            legPairOffset = 3f;
            legLength = 30f;
            legExtension = -15f;
            legBaseOffset = 10f;
            stepShake = 1f;
            legLengthScl = 0.96f;
            rippleScale = 2f;
            legSpeed = 0.2f;
            ammoType = new PowerAmmoType(2000);
            legSplashDamage = 32f;
            legSplashRange = 30f;
            drownTimeMultiplier = 2f;
            hovering = true;
            shadowElevation = 0.65f;
            groundLayer = Layer.legUnit;
            payloadCapacity = (3 * 3) * tilePayload;
            buildSpeed = 2.5f;
            buildBeamOffset = 2.3f;
            range = 140f;
            mineTier = 5;
            mineSpeed = 10.5f;
            weapons.add(new Weapon("avert-weapon") {{
                top = false;
                shake = 2f;
                shootY = 4f;
                x = 10.5f;
                reload = 55f;
                recoil = 4f;
                shootSound = Sounds.laser;
                bullet = new LaserBulletType() {{
                    damage = 45f;
                    recoil = 1f;
                    sideAngle = 45f;
                    sideWidth = 1f;
                    sideLength = 70f;
                    healPercent = 10f;
                    collidesTeam = true;
                    length = 135f;
                    colors = new Color[]{Pal.sapBulletBack.cpy().a(0.4f), Pal.sapBullet, Color.white};
                }};
            }});
        }};
        vulture = new UnitType("vulture") {{
            constructor = UnitEntity::create;
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
                    trailEffect = HIFx.trailToGray;
                    lifetime = 90f;
                    collidesAir = false;
                    hitSound = Sounds.explosion;
                    hitEffect = HIFx.square45_4_45;
                    shootEffect = HIFx.circleSplash;
                    smokeEffect = Fx.shootBigSmoke;
                    despawnEffect = HIFx.crossBlast(hitColor, 50f);
                }};
                shootSound = HISounds.blaster;
            }});
            abilities.add(new JavelinAbility(20f, 5f, 29f) {{
                minDamage = 5f;
                minSpeed = 2f;
                maxSpeed = 4f;
                magX = 0.2f;
                magY = 0.1f;
            }});
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
        }};
        burner = new UnitType("burner") {{
            constructor = MechUnit::create;
            speed = 0.36f;
            hitSize = 24f;
            rotateSpeed = 2.1f;
            health = 16700;
            armor = 32f;
            mechFrontSway = 1f;
            ammoType = new PowerAmmoType(500);
            mechStepParticles = true;
            stepShake = 0.15f;
            singleTarget = true;
            drownTimeMultiplier = 4f;
            range = 168f;
            weapons.add(new Weapon("scepter-weapon") {{
                mirror = top = false;
                y = 1f;
                x = 16f;
                shootY = 8f;
                reload = 6f;
                recoil = 2f;
                shake = 1f;
                shootSound = Sounds.flame;
                inaccuracy = 3f;
                shootCone = 8f;
                bullet = new FlameBulletType(Pal.techBlue, Pal.techBlue.cpy().lerp(Color.gray, 0.3f), Color.gray, range + 8f, 8, 72, 22f) {{
                    damage = 225f;
                    collidesAir = true;
                    status = HIStatusEffects.ultFireBurn;
                    statusDuration = 60f * 6;
                    ammoMultiplier = 4f;
                }
                    @Override
                    public void hit(Bullet b) {
                        if (absorbable && b.absorbed) return;
                        Units.nearbyEnemies(b.team, b.x, b.y, flameLength, unit -> {
                            if (Angles.within(b.rotation(), b.angleTo(unit), flameCone) && unit.checkTarget(collidesAir, collidesGround) && unit.hittable()) {
                                Fx.hitFlameSmall.at(unit);
                                unit.health(unit.health() - damage * damageBoost);
                                unit.apply(status, statusDuration);
                            }
                        });
                        indexer.allBuildings(b.x, b.y, flameLength, other -> {
                            if (other.team != b.team && Angles.within(b.rotation(), b.angleTo(other), flameCone)) {
                                Fx.hitFlameSmall.at(other);
                                other.health(other.health() - damage * buildingDamageMultiplier * damageBoost);
                            }
                        });
                    }
                };
            }
                @Override
                public float range() {
                    return range;
                }

                @Override
                public void addStats(UnitType u, Table t) {
                    String text = bundle.get("unit.heavy-industry-burner-weapon-0.description");
                    UIUtils.collapseTextToTable(t, text);
                    super.addStats(u, t);
                }
            });
        }};
        shadowBlade = new UnitType("shadow-blade") {{
            constructor = MechUnit::create;
            speed = 0.36f;
            hitSize = 24f;
            rotateSpeed = 2.1f;
            health = 11000;
            armor = 17f;
            mechFrontSway = 1f;
            ammoType = new PowerAmmoType(3000f);
            mechStepParticles = true;
            stepShake = 0.15f;
            singleTarget = true;
            drownTimeMultiplier = 4f;
            weapons.add(new Weapon(name("shadow-blade-weapon")) {{
                top = false;
                y = 1f;
                x = 16f;
                shootY = 8f;
                reload = 70f;
                recoil = 2f;
                shake = 1f;
                shootSound = Sounds.missileLarge;
                inaccuracy = 3f;
                shoot = new ShootSpread(3, 18f);
                shootCone = 35f;
                bullet = new CtrlMissileBulletType("missile-large", 6, 10) {{
                    damage = 165f;
                    buildingDamageMultiplier = 1.2f;
                    pierceArmor = true;
                    autoHoming = true;
                    homingPower = 7.5f;
                    homingRange = 100f;
                    homingDelay = 18f;
                    backColor = frontColor = lightColor = healColor = hitColor = trailColor = Pal.suppress;
                    trailLength = 15;
                    trailWidth = 2.4f;
                    speed = 5.3f;
                    lifetime = 100f;
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
                    parts.add(new ShapePart() {{
                        layer = Layer.effect;
                        circle = true;
                        y = -0.25f;
                        radius = 1.5f;
                        color = trailColor;
                        colorTo = Color.white;
                        progress = PartProgress.life.curve(Interp.pow5In);
                    }});
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
                        if (entity instanceof Unit unit && unit.type != null) {
                            if (unit.shield > 0) {
                                HIFx.hitOut.at(unit.x, unit.y, b.rotation(), unit);
                                unit.health -= damage;
                            }
                            unit.damagePierce(b.damage * (1 + Math.max(unit.type.armor, 0) / 10f));
                        } else if (entity instanceof Building build && build.block != null) {
                            build.damagePierce(b.damage * buildingDamageMultiplier * (1 + Math.max(build.block.armor, 0) / 10f));
                        } else super.hitEntity(b, entity, health);
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
                    String text = bundle.get("unit.heavy-industry-shadow-blade-weapon-0.description");
                    UIUtils.collapseTextToTable(t, text);
                    super.addStats(u, t);
                }
            });
        }};
        artilleryFirePioneer = new UnitType("artillery-fire-pioneer") {{
            constructor = UnitEntity::create;
            hitSize = 28f;
            speed = 1.1f;
            accel = 0.05f;
            drag = 0.05f;
            rotateSpeed = 0.1f;
            health = 6300f;
            armor = 39f;
            flying = true;
            lowAltitude = true;
            targetGround = true;
            targetAir = false;
            ammoType = new PowerAmmoType(22000f);
            targetFlags = new BlockFlag[]{BlockFlag.storage, BlockFlag.repair, BlockFlag.turret, null};
            weapons.add(new Weapon() {{
                mirror = false;
                rotate = true;
                rotateSpeed = 100f;
                reload = 150f;
                shootSound = Sounds.none;
                bullet = new BulletType(0f, 0f) {{
                    lifetime = 10f;
                    collides = collidesAir = collidesGround = collidesTiles = false;
                    despawnEffect = hitEffect = Fx.none;
                    fragBullets = 4;
                    fragBullet = new PointBulletType() {{
                        trailSpacing = 7f;
                        trailEffect = hitEffect = despawnEffect = Fx.none;
                        lifetime = 8f;
                        speed = 15f;
                        hitSound = Sounds.none;
                        fragBullets = 1;
                        fragLifeMin = 0.3f;
                        fragBullet = new ArtilleryBulletType(0.1f, 350f, "shell") {{
                            hitEffect = new MultiEffect(Fx.titanExplosion, Fx.titanSmoke);
                            despawnEffect = Fx.none;
                            knockback = 2f;
                            lifetime = 140f;
                            height = width = 0f;
                            splashDamageRadius = 65f;
                            splashDamage = 350f;
                            scaledSplashDamage = true;
                            backColor = hitColor = trailColor = Color.valueOf("ea8878").lerp(Pal.redLight, 0.5f);
                            frontColor = Color.white;
                            ammoMultiplier = 1f;
                            hitSound = Sounds.titanExplosion;
                            status = StatusEffects.blasted;
                            trailLength = 32;
                            trailWidth = 3.35f;
                            trailSinScl = 2.5f;
                            trailSinMag = 0.5f;
                            trailEffect = Fx.none;
                            despawnShake = 7f;
                            shootEffect = Fx.shootTitan;
                            smokeEffect = Fx.shootSmokeTitan;
                            trailInterp = v -> Math.max(Mathf.slope(v), 0.8f);
                            shrinkX = 0.2f;
                            shrinkY = 0.1f;
                            buildingDamageMultiplier = 0.7f;
                        }};
                    }};
                }};
            }
                public final float rangeWeapon = 720f;

                @Override
                protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation) {
                    shootSound.at(shootX, shootY, Mathf.random(soundPitchMin, soundPitchMax));

                    Tmp.v6.set(mount.aimX, mount.aimY).sub(unit);
                    Tmp.v1.set(mount.aimX, mount.aimY).sub(unit).nor().scl(Math.min(Tmp.v6.len(), rangeWeapon)).add(unit);

                    Bullet b = bullet.create(unit, unit.team, Tmp.v1.x, Tmp.v1.y, 0);
                    b.vel.setZero();
                    b.set(Tmp.v1);
                    unit.apply(shootStatus, shootStatusDuration);
                }

                @Override
                public float range() {
                    return rangeWeapon;
                }
            });
        }};
        //elite
        tiger = new UnitType("tiger") {{
            constructor = MechUnit::create;
            drawShields = false;
            engineOffset = 18f;
            engineSize = 9f;
            speed = 0.37f;
            hitSize = 48f;
            health = 62500f;
            buildSpeed = 4f;
            armor = 86f;
            envDisabled = Env.none;
            ammoType = new PowerAmmoType(3000f);
            weapons.add(new Weapon(name("tiger-cannon")) {{
                top = false;
                rotate = true;
                rotationLimit = 13f;
                rotateSpeed = 0.75f;
                alternate = true;
                shake = 3.5f;
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
                bullet = new BasicBulletType(8, 200f, name("strike")) {{
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
                    trailEffect = HIFx.trailToGray;
                    trailParam = 3f;
                    trailChance = 0.35f;
                    hitShake = 7f;
                    hitSound = Sounds.explosion;
                    hitEffect = HIFx.hitSpark(backColor, 75f, 24, 95f, 2.8f, 16);
                    smokeEffect = new MultiEffect(HIFx.hugeSmokeGray, HIFx.circleSplash(backColor, 60f, 8, 60f, 6));
                    shootEffect = HIFx.hitSpark(backColor, 30f, 15, 35f, 1.7f, 8);
                    despawnEffect = HIFx.blast(backColor, 60);
                    fragBullet = HIBullets.basicSkyFrag;
                    fragBullets = 5;
                    fragLifeMax = 0.6f;
                    fragLifeMin = 0.2f;
                    fragVelocityMax = 0.35f;
                    fragVelocityMin = 0.074f;
                }
                    @Override
                    public void hitEntity(Bullet b, Hitboxc entity, float health) {
                        if (entity instanceof Healthc h && !h.dead()) {
                            h.health(h.health() - damage);
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
                    String text = bundle.get("unit.heavy-industry-tiger-weapon-0.description");
                    UIUtils.collapseTextToTable(t, text);
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
                bullet = HIBullets.annMissile;
                shootSound = HISounds.launch;
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
                shootSound = HISounds.launch;
                shootCone = 30f;
                shootY = 5f;
                top = true;
                rotate = true;
                bullet = new BasicBulletType(5.25f, 150f, name("strike")) {{
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
                    hitEffect = HIFx.circleSplash(backColor, 40f, 4, 40f, 6f);
                    despawnEffect = HIFx.hitSparkLarge;
                    shootEffect = HIFx.shootCircleSmall(backColor);
                    smokeEffect = Fx.shootBigSmoke2;
                    trailChance = 0.6f;
                    trailEffect = HIFx.trailToGray;
                    hitShake = 3f;
                    hitSound = Sounds.plasmaboom;
                }};
            }});
            for (float[] i : new float[][]{{22f, 18f}, {25f, 2f}}) {
                weapons.add(new PointDefenseWeapon(name("tiger-cannon-small")) {{
                    x = i[0];
                    y = i[1];
                    color = Pal.techBlue;
                    mirror = top = alternate = true;
                    reload = 6f;
                    targetInterval = 6f;
                    targetSwitchInterval = 6f;
                    bullet = new BulletType() {{
                        shootEffect = HIFx.shootLineSmall(color);
                        hitEffect = HIFx.lightningHitSmall;
                        hitColor = color;
                        maxRange = 240f;
                        damage = 150f;
                    }};
                }});
            }
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
                maxShield = 22600f;
                recoverSpeed = 10.5f;
                cooldown = 6500f;
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
            hideDetails = false;
        }};
        thunder = new UnitType("thunder") {{
            constructor = TankUnit::create;
            health = 72500f;
            armor = 115f;
            rotateSpeed = 1f;
            speed = 0.66f;
            squareShape = true;
            omniMovement = false;
            rotateMoveFirst = true;
            envDisabled = Env.none;
            hitSize = 47f;
            accel = 0.25f;
            float xo = 231f / 2f, yo = 231f / 2f;
            treadRects = new Rect[]{new Rect(27 - xo, 152 - yo, 56, 73), new Rect(24 - xo, 51 - 9 - yo, 29, 17), new Rect(59 - xo, 18 - 9 - yo, 39, 19)};
            hoverable = hovering = true;
            ammoType = new PowerAmmoType(3000);
            crushDamage = 20;
            weapons.add(new Weapon(name("thunder-weapon")) {{
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
                heatColor = HIPal.ancientHeat;
                shoot = new ShootAlternate(12.3f) {{
                    shots = 2;
                    shotDelay = 0f;
                }};
                inaccuracy = 1.3f;
                shootSound = HISounds.flak;
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
                    lightningColor = backColor = trailColor = hitColor = lightColor = HIPal.ancient;
                    lightRadius = 70f;
                    shootEffect = new WrapperEffect(HIFx.shootLine(33f, 32), backColor);
                    smokeEffect = HIFx.hugeSmokeLong;
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
                    hitEffect = new MultiEffect(HIFx.square45_6_45, HIFx.hitSparkLarge);
                    despawnEffect = HIFx.lightningHitLarge;
                    fragBullet = new EdgeFragBulletType() {{
                        hitColor = trailColor = HIPal.ancient;
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
                    int fi = i;
                    parts.add(new RegionPart("-blade") {{
                        progress = PartProgress.warmup.delay((3 - fi) * 0.3f).blend(PartProgress.reload, 0.3f);
                        heatProgress = PartProgress.heat.add(0.3f).min(PartProgress.warmup);
                        mirror = true;
                        under = true;
                        moveRot = -40f * fi;
                        moveX = 3f;
                        layerOffset = -0.002f;
                        x = 11 / 4f;
                    }});
                }
            }
                @Override
                public void addStats(UnitType u, Table t) {
                    String text = bundle.get("unit.heavy-industry-thunder-weapon-0.description");
                    UIUtils.collapseTextToTable(t, text);
                    super.addStats(u, t);
                }
            }, new Weapon(name("thunder-point-weapon")) {{
                reload = 30f;
                x = y = 15f;
                shootY = 7f;
                recoil = 2f;
                mirror = rotate = true;
                rotateSpeed = 4f;
                autoTarget = true;
                controllable = alternate = false;
                shootSound = HISounds.gauss;
                shadow = 20f;
                bullet = new BasicBulletType(12f, 220f) {{
                    width = 9f;
                    height = 28f;
                    trailWidth = 1.3f;
                    trailLength = 7;
                    lifetime = 39f;
                    drag = 0.015f;
                    trailColor = hitColor = backColor = lightColor = lightningColor = HIPal.ancient;
                    frontColor = Color.white;
                    pierce = pierceArmor = true;
                    pierceCap = 3;
                    smokeEffect = Fx.shootSmallSmoke;
                    shootEffect = HIFx.shootCircleSmall(backColor);
                    despawnEffect = HIFx.square45_4_45;
                    hitEffect = HIFx.hitSpark;
                    hittable = false;
                }
                    public final float percent = 0.008f;

                    @Override
                    public void hitEntity(Bullet b, Hitboxc entity, float health) {
                        if (entity instanceof Healthc h && !h.dead()) {
                            h.health(h.health() - (float) Math.ceil(h.maxHealth() * percent));
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
                    String text = bundle.get("unit.heavy-industry-thunder-weapon-1.description");
                    UIUtils.collapseTextToTable(t, text);
                    super.addStats(u, t);
                }
            });
            abilities.addAll(new MirrorArmorAbility() {{
                strength = 240f;
                maxShield = 16400f;
                recoverSpeed = 5.5f;
                cooldown = 5500f;
                minAlbedo = 0.5f;
                maxAlbedo = 0.8f;
                shieldArmor = 26f;
            }});
            drownTimeMultiplier = 26f;
            hideDetails = false;
        }};
        vast = new EnergyUnitType("vast") {{
            constructor = EnergyUnit::create;
            damageMultiplier = 0.3f;
            clipSize = 260f;
            engineLayer = Layer.effect;
            engineOffset = 5f;
            deathExplosionEffect = Fx.none;
            deathSound = Sounds.plasmaboom;
            trailScl = 3f;
            Seq<StatusEffect> statusEffects = Seq.with(StatusEffects.none, StatusEffects.boss, StatusEffects.invincible);
            immunities = ObjectSet.with(content.statusEffects().select(s -> s != null && !statusEffects.contains(s)));
            armor = 8f;
            hitSize = 45;
            speed = 1.5f;
            accel = 0.07f;
            drag = 0.075f;
            health = 42500f;
            itemCapacity = 0;
            rotateSpeed = 6;
            engineSize = 8f;
            flying = true;
            trailLength = 70;
            buildSpeed = 10f;
            crashDamageMultiplier = Mathf.clamp(hitSize / 10f, 1, 10);
            buildBeamOffset = 0;
            aiController = SniperAI::new;
            targetFlags = new BlockFlag[]{BlockFlag.reactor, BlockFlag.generator, BlockFlag.turret, null};
            abilities.add(new DeathAbility());
            weapons.add(new Weapon() {{
                reload = 180f;
                x = y = shootX = shootY = 0;
                shootCone = 360;
                shootSound = HISounds.blaster;
                bullet = HIBullets.vastBulletAccel;
                shoot = new ShootPattern() {{
                    shots = 15;
                    shotDelay = 3f;
                }
                    @Override
                    public void shoot(int totalShots, BulletHandler handler) {
                        for (int i = 0; i < shots; i++) {
                            handler.shoot(0, 0, Mathf.random(360), firstShotDelay + shotDelay * i);
                        }
                    }
                };
            }}, new Weapon() {{
                reload = 120f;
                x = y = shootX = shootY = 0;
                rotateSpeed = 360;
                shootSound = HISounds.hugeShoot;
                velocityRnd = 0.015f;
                shoot = new ShootMulti(new ShootSummon(0, 0, 220, 0) {{
                    shots = 3;
                    shotDelay = 1f;
                }}, new ShootSpread() {{
                    shots = 2;
                    spread = 2f;
                    shotDelay = 25;
                }});
                bullet = HIBullets.vastBulletLightningBall;
            }}, new Weapon() {{
                shootCone = 30f;
                predictTarget = false;
                top = false;
                mirror = false;
                rotate = true;
                x = y = shootX = shootY = 0f;
                continuous = false;
                rotateSpeed = 100f;
                reload = 600f;
                shootStatus = StatusEffects.slow;
                shootStatusDuration = 180f;
                shoot = new ShootSummon(0, 0, 180, 0) {{
                    shots = 3;
                }};
                shake = 13f;
                shootSound = Sounds.none;
                bullet = HIBullets.vastBulletStrafeLaser;
            }
                @Override
                protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation) {
                    super.shoot(unit, mount, shootX, shootY, rotation);
                    HIFx.crossSpinBlast.at(unit.x, unit.y, unit.rotation, unit.team.color, unit);
                }
            });
            hideDetails = false;
        }};
    }
}
