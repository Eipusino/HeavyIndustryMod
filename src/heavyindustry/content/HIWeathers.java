package heavyindustry.content;

import arc.graphics.*;
import arc.util.*;
import heavyindustry.core.*;
import heavyindustry.entities.bullet.*;
import heavyindustry.gen.*;
import heavyindustry.type.weather.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.type.weather.*;
import mindustry.world.meta.*;

import static heavyindustry.core.HeavyIndustryMod.*;

/**
 * Defines the {@linkplain Weather weather} this mod offers.
 *
 * @author Eipusino
 */
public final class HIWeathers {
    public static Weather wind, blizzard, hailStone, stoneStorm;

    /** Don't let anyone instantiate this class. */
    private HIWeathers() {}

    /** Instantiates all contents. Called in the main thread in {@link HeavyIndustryMod#loadContent()}. */
    public static void load() {
        wind = new EffectWeather("wind") {{
            weatherFx = HIFx.windTail;
            particleRegion = "particle";
            sizeMax = 5f;
            sizeMin = 1f;
            density = 1600;
            baseSpeed = 5.4f;
            minAlpha = 0.05f;
            maxAlpha = 0.18f;
            force = 0.1f;
            sound = Sounds.wind2;
            soundVol = 0.8f;
            maxSpawn = 2;
            duration = 8f * Time.toMinutes;
        }};
        blizzard = new ParticleWeather("blizzard") {{
            particleRegion = "particle";
            sizeMax = 14f;
            sizeMin = 3f;
            density = 600f;
            baseSpeed = 15f;
            yspeed = -2.5f;
            xspeed = 8f;
            minAlpha = 0.75f;
            maxAlpha = 0.9f;
            attrs.set(Attribute.light, -0.35f);
            sound = Sounds.windhowl;
            soundVol = 0.25f;
            soundVolOscMag = 1.5f;
            soundVolOscScl = 1100f;
            soundVolMin = 0.15f;
        }};
        hailStone = new HailStormWeather("hail-storm") {{
            attrs.set(Attribute.light, -0.5f);
            drawParticles = inBounceCam = drawNoise = false;
            duration = 15f * Time.toMinutes;
            bulletChange = 0.5f;
            soundVol = 0.05f;
            sound = HISounds.hailRain;
            setBullets(
                    new HailStoneBulletType(name("hailstone-big"), 3) {{
                        hitEffect = Fx.explosion.layer(Layer.power);
                        hitSound = HISounds.bigHailstoneHit;
                        hitSoundVolume = 0.2f;
                        despawnEffect = HIFx.staticStone;
                        splashDamage = 95f;
                        splashDamageRadius = 40f;
                        canCollideFalling = pierce = true;
                        fallingDamage = 120f;
                        fallingRadius = 30f;
                        minDistanceFallingCollide = 15f;
                        hitFallingEffect = HIFx.bigExplosionStone;
                        hitFallingColor = Color.valueOf("5867ac");
                    }}, 1 / 1600f,
                    new HailStoneBulletType(name("hailstone-middle"), 2) {{
                        hitEffect = Fx.dynamicWave.layer(Layer.power);
                        despawnEffect = HIFx.fellStone;
                        splashDamage = 10f;
                        splashDamageRadius = 25f;
                        canCollideFalling = true;
                        fallingDamage = 25f;
                        fallingRadius = 15f;
                        minDistanceFallingCollide = 5f;
                        hitFallingEffect = HIFx.explosionStone;
                        hitFallingColor = Color.valueOf("5867ac");
                    }}, 1 / 12f,
                    new HailStoneBulletType(name("hailstone-small"), 5) {{
                        hitEffect = Fx.none;
                        despawnEffect = HIFx.fellStone;
                        splashDamage = 0f;
                        splashDamageRadius = 0;
                    }}, 1f
            );
        }};
        stoneStorm = new HailStormWeather("stone-storm") {{
            attrs.set(Attribute.light, -0.5f);
            particleRegion = name("stone-storm-small-3");
            noisePath = "distortAlpha";
            inBounceCam = drawRain = false;
            useWindVector = drawNoise = true;
            noiseColor = Color.valueOf("8c8c8c");
            baseSpeed = 5.4f;
            duration = 15f * Time.toMinutes;
            bulletChange = 0.5f;
            soundVol = 0.05f;
            sound = Sounds.wind;
            setBullets(
                    new HailStoneBulletType(name("stone-storm-big"), 3) {{
                        speed = 4f;
                        hitEffect = Fx.explosion.layer(Layer.power);
                        hitSound = HISounds.bigHailstoneHit;
                        hitSoundVolume = 0.2f;
                        despawnEffect = HIFx.staticStone;
                        damage = splashDamage = 95f;
                        splashDamageRadius = 40f;
                        canCollideFalling = pierce = true;
                        immovable = false;
                        fallingDamage = 120f;
                        fallingRadius = 30f;
                        minDistanceFallingCollide = 15f;
                        hitFallingEffect = HIFx.bigExplosionStone;
                        hitFallingColor = Color.valueOf("5e9098");
                    }}, 1 / 1600f,
                    new HailStoneBulletType(name("stone-storm-middle"), 2) {{
                        speed = 4f;
                        hitEffect = Fx.none;
                        despawnEffect = HIFx.fellStoneAghanite;
                        damage = splashDamage = 10f;
                        splashDamageRadius = 25f;
                        canCollideFalling = true;
                        immovable = false;
                        fallingDamage = 25f;
                        fallingRadius = 15f;
                        minDistanceFallingCollide = 5f;
                        hitFallingEffect = HIFx.explosionStone;
                        hitFallingColor = Color.valueOf("5e9098");
                    }}, 1 / 12f,
                    new HailStoneBulletType(name("stone-storm-small"), 5) {{
                        speed = 4f;
                        immovable = false;
                        hitEffect = Fx.none;
                        despawnEffect = HIFx.fellStoneAghanite;
                        splashDamage = 0f;
                        splashDamageRadius = 0;
                    }}, 1f
            );
        }};
    }
}
