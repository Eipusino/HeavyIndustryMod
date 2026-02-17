package endfield.world.blocks.power;

import arc.Events;
import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.struct.EnumSet;
import arc.util.Time;
import arc.util.Tmp;
import endfield.audio.Sounds2;
import endfield.content.Bullets2;
import endfield.content.Fx2;
import endfield.entities.bullet.EffectBulletType;
import endfield.graphics.Drawn;
import endfield.graphics.PositionLightning;
import endfield.util.Constant;
import mindustry.Vars;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.game.EventType.Trigger;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.blocks.power.ImpactReactor;
import mindustry.world.meta.BlockFlag;

/**
 * After being destroyed, it will produce complex explosive effects and other split bullets.
 */
public class HyperGenerator extends ImpactReactor {
	public float destroyedExplodeLimit = 0.5f;

	public int updateLightning;
	public int updateLightningRand;
	public float lightningRange = 160f;
	public int lightningLen = 4;
	public int lightningLenRand = 8;
	public float lightningDamage = 120f;
	public int subNum = 1;
	public int subNumRand = 1;

	public float statusDuration = 15 * 60;

	public Cons<HyperGeneratorBuild> explodeAction = Constant.cons();
	public Cons<Position> explodeSub = Constant.cons();

	public float maxVelScl = 1.25f, minVelScl = 0.75f;
	public float maxTimeScl = 1.25f, minTimeScl = 0.75f;

	public float triWidth = 6f;
	public float triLength = 100f;
	public Color effectColor = Pal.techBlue;

	public BulletType destroyed, blast, blastLinker;
	public float attract = 8f;

	public HyperGenerator(String name) {
		super(name);

		explosionRadius = 220;
		explosionDamage = 14000;
		baseExplosiveness = 1000f;

		flags = EnumSet.of(BlockFlag.reactor, BlockFlag.generator);
	}

	@Override
	public void init() {
		super.init();

		if (consPower == null) consumePower(0f);

		if (blast == null) blast = Bullets2.hyperBlast;
		if (blastLinker == null) blastLinker = Bullets2.hyperBlastLinker;

		if (destroyed == null) destroyed = new EffectBulletType(600f) {{
			absorbable = hittable = false;
			speed = 0;
			lightningLen = lightningLenRand = 4;
			lightningDamage = HyperGenerator.this.lightningDamage / 2;
			lightning = 3;
			damage = splashDamage = lightningDamage / 1.5f;
			splashDamageRadius = 38f;

			hitColor = lightColor = lightningColor = effectColor;

			despawnEffect = Fx2.circleOut(hitColor, lightningRange * 1.5f);
			hitEffect = Fx2.collapserBulletExplode;

			hitShake = despawnShake = 80f;
			despawnSound = Sounds2.hugeBlast;
		}
			public final Effect updateEffect1 = Fx2.circleOut(effectColor, lightningRange * 0.75f), updateEffect2 = Fx2.blast(hitColor, lightningRange / 2f);

			@Override
			public void init(Bullet b) {
				super.init(b);

				Units.nearby(Tmp.r1.setCenter(b.x, b.y).setSize(lightningRange * 4), unit -> {
					unit.impulse(Tmp.v3.set(unit).sub(b.x, b.y).nor().scl(b.dst(unit) * unit.mass() / 160f));
				});
			}

			@Override
			public void draw(Bullet b) {
				super.draw(b);

				float f = Mathf.curve(b.fout(), 0, 0.15f);
				float f2 = Mathf.curve(b.fin(), 0, 0.1f);
				Draw.color(effectColor);
				Fill.circle(b.x, b.y, size * Vars.tilesize / 3f * f);

				for (int i : Mathf.signs) {
					Drawf.tri(b.x, b.y, triWidth * f2 * f, triLength * 1.3f * f2 * f, (i + 1) * 90 + Time.time * 2);
					Drawf.tri(b.x, b.y, triWidth * f2 * f, triLength * 1.3f * f2 * f, (i + 1) * 90 - Time.time * 2 + 90);
				}

				Draw.color(Color.black);
				Draw.z(Layer.effect + 0.01f);
				Fill.circle(b.x, b.y, size * Vars.tilesize / 5f * f);
				Draw.z(Layer.bullet);

				Drawf.light(b, lightningRange * 4f * b.fout(Interp.pow2Out), effectColor, 0.75f);
			}

			@Override
			public void update(Bullet b) {
				super.update(b);

				Units.nearby(Tmp.r1.setCenter(b.x, b.y).setSize(lightningRange * 3f), unit -> {
					unit.impulse(Tmp.v3.set(unit).sub(b.x, b.y).nor().scl(-attract * 100f));
				});

				if (!Vars.headless) {
					if (Mathf.chanceDelta((b.fin() * 3 + 1) / 4f * 0.65f)) {
						Drawn.randFadeLightningEffect(b.x, b.y, lightningRange * 1.5f, Mathf.random(12f, 20f), lightningColor, Mathf.chance(0.5));
					}

					if (Mathf.chanceDelta(0.2)) {
						updateEffect2.at(b.x + Mathf.range(size * Vars.tilesize * 0.75f), b.y + Mathf.range(size * Vars.tilesize * 0.75f));
					}

					if (Mathf.chanceDelta(0.075)) {
						updateEffect1.at(b.x + Mathf.range(size * Vars.tilesize), b.y + Mathf.range(size * Vars.tilesize));
					}

					Effect.shake(10f, 30f, b);
				}

				if (b.timer(3, 8)) {
					PositionLightning.createRange(b, b, Team.derelict, lightningRange * 2f, 255, effectColor, true, lightningDamage, subNum + Mathf.random(subNumRand), PositionLightning.WIDTH, updateLightning + Mathf.random(updateLightningRand), point -> {
						if (!Vars.headless) {
							Fx2.lightningHitSmall.at(point);
						}
						Damage.damage(point.getX(), point.getY(), splashDamageRadius, splashDamage);
					});
				}

				if (blast == null || blastLinker == null) return;

				if (b.timer(4, 5)) {
					if (!Vars.headless) {
						float range = size * Vars.tilesize / 1.5f;
						Fx2.hyperExplode.at(b.x + Mathf.range(range), b.y + Mathf.range(range), effectColor);
						Sounds.explosionReactor2.at(b);
					}
					blast.create(b, Team.derelict, b.x, b.y, Mathf.random(360), blast.damage * baseExplosiveness, Mathf.random(minVelScl, maxVelScl), Mathf.random(minTimeScl, maxTimeScl), new Object());
				}

				if (b.timer(5, 8)) {
					if (!Vars.headless) {
						float range = size * Vars.tilesize / 1.5f;
						Fx2.hitSparkLarge.at(b.x + Mathf.range(range), b.y + Mathf.range(range), effectColor);
					}
					blastLinker.create(b, Team.derelict, b.x, b.y, Mathf.random(360), blast.damage * baseExplosiveness, Mathf.random(minVelScl, maxVelScl), Mathf.random(minTimeScl, maxTimeScl), new Object());
				}
			}

			@Override
			public void despawned(Bullet b) {
				super.despawned(b);

				Units.nearby(Tmp.r1.setCenter(b.x, b.y).setSize(lightningRange * 4), unit -> {
					if (unit.hittable()) {
						unit.vel.set(Tmp.v1.set(unit).sub(b).nor().scl(6));
						unit.kill();
					}
				});

				for (int i = 0; i < 7; ++i) {
					Time.run(Mathf.random(80), () -> {
						Fx2.hyperExplode.at(b.x + Mathf.range(size * Vars.tilesize), b.y + Mathf.range(size * Vars.tilesize), effectColor);
						Fx2.hyperCloud.at(b.x + Mathf.range(size * Vars.tilesize), b.y + Mathf.range(size * Vars.tilesize), effectColor);
						Fx2.circle.at(b.x + Mathf.range(size * Vars.tilesize), b.y + Mathf.range(size * Vars.tilesize), explosionRadius, effectColor);
					});
				}
			}
		};
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = HyperGeneratorBuild::new;
	}

	public class HyperGeneratorBuild extends ImpactReactorBuild {
		@Override
		public void updateTile() {
			if (efficiency >= 0.9999f && power.status >= 0.99f) {
				boolean prevOut = getPowerProduction() <= consPower.requestedPower(this);

				warmup = Mathf.lerpDelta(warmup, 1f, warmupSpeed * timeScale);
				if (Mathf.equal(warmup, 1f, 0.001f)) {
					warmup = 1f;
				}

				if (!prevOut && (getPowerProduction() > consPower.requestedPower(this))) {
					Events.fire(Trigger.impactPower);
				}

				if (timer(timerUse, itemDuration / timeScale)) {
					consume();
				}
			} else {
				warmup = Mathf.lerpDelta(warmup, 0f, 0.001f);
			}

			totalProgress += warmup * Time.delta;

			productionEfficiency = Mathf.pow(warmup, 5f);
		}

		@Override
		public void onDestroyed() {
			super.onDestroyed();

			if (warmup < destroyedExplodeLimit) return;

			explodeAction.get(this);

			int i;

			destroyed.create(this, Team.derelict, x, y, 0);

			for (i = 0; i < 30; i++) {
				Time.run(Mathf.random(80f), () -> {
					explodeSub.get(this);
					Sounds.shootArtillerySmall.at(this);
					Sounds.explosionReactor2.at(this);
				});
			}

			for (i = 0; i < 10; i++) {
				Time.run(i * (3 + Mathf.random(2f)), () -> {
					explodeSub.get(this);
					Sounds.explosionReactor2.at(this);
					PositionLightning.createRandomRange(Team.derelict, this, lightningRange * 3f, effectColor, true, lightningDamage, lightningLen + Mathf.random(lightningLenRand), PositionLightning.WIDTH, subNum + Mathf.random(subNumRand), updateLightning + Mathf.random(updateLightningRand), point -> {
						Fx2.lightningHitLarge.at(point.getX(), point.getY(), effectColor);
					});
				});
			}

			Sounds.explosionReactor2.at(this);
			Effect.shake(6f, 16f, x, y);

			for (i = 0; i < 7; ++i) {
				Time.run((float) Mathf.random(80), () -> {
					Fx2.hyperExplode.at(x + Mathf.range(size * Vars.tilesize), y + Mathf.range(size * Vars.tilesize), effectColor);
					Fx2.hyperCloud.at(x + Mathf.range(size * Vars.tilesize), y + Mathf.range(size * Vars.tilesize), effectColor);
					Fx2.circle.at(x + Mathf.range(size * Vars.tilesize), y + Mathf.range(size * Vars.tilesize), explosionRadius, effectColor);
				});
			}

			Damage.damage(x, y, explosionRadius, explosionDamage);
		}

		@Override
		public void createExplosion() {
			//The original explosion is no longer in use.
		}
	}
}