package heavyindustry.content;

import arc.graphics.Color;
import arc.util.Time;
import heavyindustry.core.HeavyIndustryMod;
import heavyindustry.entities.bullet.HailStoneBulletType;
import heavyindustry.gen.HSounds;
import heavyindustry.type.weather.EffectWeather;
import heavyindustry.type.weather.HailStormWeather;
import mindustry.content.Fx;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.type.Weather;
import mindustry.type.weather.ParticleWeather;
import mindustry.world.meta.Attribute;

import static heavyindustry.HVars.name;

/**
 * Defines the {@linkplain Weather weather} this mod offers.
 *
 * @author Eipusino
 */
public final class HWeathers {
	public static Weather wind, blizzard, hailStone, stoneStorm;

	/** Don't let anyone instantiate this class. */
	private HWeathers() {}

	/** Instantiates all contents. Called in the main thread in {@link HeavyIndustryMod#loadContent()}. */
	public static void load() {
		wind = new EffectWeather("wind") {{
			weatherFx = HFx.windTail;
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
			sound = HSounds.hailRain;
			setBullets(new HailStoneBulletType(name("hailstone-big"), 3) {{
				hitEffect = Fx.explosion.layer(Layer.power);
				hitSound = HSounds.bigHailstoneHit;
				hitSoundVolume = 0.2f;
				despawnEffect = HFx.staticStone;
				splashDamage = 95f;
				splashDamageRadius = 40f;
				canCollideFalling = pierce = true;
				fallingDamage = 120f;
				fallingRadius = 30f;
				minDistanceFallingCollide = 15f;
				hitFallingEffect = HFx.bigExplosionStone;
				hitFallingColor = Color.valueOf("5867ac");
			}}, 1 / 1600f, new HailStoneBulletType(name("hailstone-middle"), 2) {{
				hitEffect = Fx.dynamicWave.layer(Layer.power);
				despawnEffect = HFx.fellStone;
				splashDamage = 10f;
				splashDamageRadius = 25f;
				canCollideFalling = true;
				fallingDamage = 25f;
				fallingRadius = 15f;
				minDistanceFallingCollide = 5f;
				hitFallingEffect = HFx.explosionStone;
				hitFallingColor = Color.valueOf("5867ac");
			}}, 1 / 12f, new HailStoneBulletType(name("hailstone-small"), 5) {{
				hitEffect = Fx.none;
				despawnEffect = HFx.fellStone;
				splashDamage = 0f;
				splashDamageRadius = 0;
			}}, 1f);
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
			setBullets(new HailStoneBulletType(name("stone-storm-big"), 3) {{
				speed = 4f;
				hitEffect = Fx.explosion.layer(Layer.power);
				hitSound = HSounds.bigHailstoneHit;
				hitSoundVolume = 0.2f;
				despawnEffect = HFx.staticStone;
				damage = splashDamage = 95f;
				splashDamageRadius = 40f;
				canCollideFalling = pierce = true;
				immovable = false;
				fallingDamage = 120f;
				fallingRadius = 30f;
				minDistanceFallingCollide = 15f;
				hitFallingEffect = HFx.bigExplosionStone;
				hitFallingColor = Color.valueOf("5e9098");
			}}, 1 / 1600f, new HailStoneBulletType(name("stone-storm-middle"), 2) {{
				speed = 4f;
				hitEffect = Fx.none;
				despawnEffect = HFx.fellStoneAghanite;
				damage = splashDamage = 10f;
				splashDamageRadius = 25f;
				canCollideFalling = true;
				immovable = false;
				fallingDamage = 25f;
				fallingRadius = 15f;
				minDistanceFallingCollide = 5f;
				hitFallingEffect = HFx.explosionStone;
				hitFallingColor = Color.valueOf("5e9098");
			}}, 1 / 12f, new HailStoneBulletType(name("stone-storm-small"), 5) {{
				speed = 4f;
				immovable = false;
				hitEffect = Fx.none;
				despawnEffect = HFx.fellStoneAghanite;
				splashDamage = 0f;
				splashDamageRadius = 0;
			}}, 1f);
		}};
	}
}
