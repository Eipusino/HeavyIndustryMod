package heavyindustry.entities.bullet;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Interp;
import arc.math.Mathf;
import heavyindustry.content.HBullets;
import heavyindustry.content.HFx;
import heavyindustry.gen.BlackHoleBullet;
import heavyindustry.util.Get;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.entities.Lightning;
import mindustry.entities.Mover;
import mindustry.entities.Sized;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;

public class BlackHoleBulletType extends EffectBulletType {
	public BlackHoleBulletType() {}

	public BlackHoleBulletType(float lifetime) {
		super(lifetime);
	}

	public BlackHoleBulletType(float lifetime, float damage, float splashDamage) {
		super(lifetime, damage, splashDamage);
	}

	@Override
	public void draw(Bullet b) {
		if (b instanceof BlackHoleBullet hb) {
			Draw.color(lightColor, Color.white, b.fin() * 0.7f);
			Draw.alpha(b.fin(Interp.pow3Out) * 1.1f);
			Lines.stroke(2 * b.fout());
			for (Sized sized : hb.sizeds) {
				if (sized instanceof Building) {
					Fill.square(sized.getX(), sized.getY(), sized.hitSize() / 2f);
				} else if (sized != null) {
					Lines.spikes(sized.getX(), sized.getY(), sized.hitSize() * (0.5f + b.fout() * 2f), sized.hitSize() / 2f * b.fslope() + 12f * b.fin(), 4, 45);
				}
			}

			Drawf.light(b.x, b.y, b.fdata, lightColor, 0.3f + b.fin() * 0.8f);
		}
	}

	public void hitTile(Entityc o, Team team, float x, float y) {
		for (int i = 0; i < lightning; i++)
			Lightning.create(team, lightColor, lightningDamage, x, y, Mathf.random(360), lightningLength + Mathf.random(lightningLengthRand));

		HBullets.hitter.create(o, team, x, y, 0, 3000, 1, 1, null);
	}

	public void hitTile(Sized target, Entityc o, Team team, float x, float y) {
		for (int i = 0; i < lightning; i++)
			Lightning.create(team, lightColor, lightningDamage, x, y, Mathf.random(360f), lightningLength + Mathf.random(lightningLengthRand));

		if (target instanceof Unit unit && unit.health > 1000f) HBullets.hitter.create(o, team, x, y, 0f);
	}

	@Override
	public void update(Bullet b) {
		super.update(b);

		if (b instanceof BlackHoleBullet hb) hb.sizeds.remove(d -> d == null || d instanceof Healthc h && !h.isValid());
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

		if (b instanceof BlackHoleBullet hb) {
			Entityc o = b.owner();

			for (Sized sized : hb.sizeds) {
				if (sized == null) continue;

				float size = Math.min(sized.hitSize(), 75);
				if (Mathf.chance(0.32) || hb.sizeds.size < 8) {
					float sd = Mathf.random(size * 3f, size * 12f);

					HFx.shuttleDark.at(sized.getX() + Mathf.range(size), sized.getY() + Mathf.range(size), 45, lightColor, sd);
				}
				hitTile(o, b.team, sized.getX(), sized.getY());
			}
		}

		createSplashDamage(b, b.x, b.y);
	}

	@Override
	public void init(Bullet b) {
		super.init(b);

		b.fdata = splashDamageRadius;

		if (b instanceof BlackHoleBullet hb) {
			Vars.indexer.eachBlock(null, b.x, b.y, b.fdata, bu -> bu.team != b.team, hb.sizeds::add);

			Groups.unit.intersect(b.x - b.fdata / 2, b.y - b.fdata / 2, b.fdata, b.fdata, u -> {
				if (u.team != b.team) hb.sizeds.add(u);
			});
		}
	}

	@Override
	public Bullet create(Entityc owner, Entityc shooter, Team team, float x, float y, float angle, float damage, float velocityScl, float lifetimeScl, Object data, Mover mover, float aimX, float aimY, Teamc target) {
		angle += angleOffset + Mathf.range(randomAngleOffset);

		if (!Mathf.chance(createChance)) return null;
		if (ignoreSpawnAngle) angle = 0;

		BlackHoleBullet bullet = BlackHoleBullet.createBlackHole();
		if (bullet.sizeds.size > 0) bullet.sizeds.clear();
		return Get.anyOtherCreate(bullet, this, shooter, owner, team, x, y, angle, damage, velocityScl, lifetimeScl, data, mover, aimX, aimY, target);
	}
}
