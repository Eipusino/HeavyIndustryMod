package heavyindustry.entities.bullet;

import arc.audio.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.struct.*;
import heavyindustry.content.*;
import heavyindustry.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.graphics.*;

import static mindustry.Vars.*;

public class ElectricStormBulletType extends BulletType {
	public Color color;
	public int maxTarget;
	public Sound sound = Sounds.spark;

	public ElectricStormBulletType(float dmg, Color col, int maxTar) {
		damage = dmg;
		splashDamage = dmg * 10;
		color = col;
		maxTarget = maxTar;
		keepVelocity = absorbable = hittable = collides = collidesAir = collidesGround = false;
		despawnEffect = hitEffect = Fx.none;
		despawnSound = Sounds.explosionbig;
		speed = 0;
	}

	@Override
	public void update(Bullet b) {
		if (b.time >= b.lifetime - HFx.chainLightningFade.lifetime) return;
		float baseRange = splashDamageRadius * 0.1f + splashDamageRadius * 0.9f * b.finpow();
		if (b.timer.get(lifetime / 15f)) {
			Seq<Healthc> t = new Seq<>();
			indexer.allBuildings(b.x, b.y, baseRange, build -> {
				if (!build.block.privileged && build.team != b.team && Mathf.chance(0.5f) && t.size < maxTarget) t.addUnique(build);
			});
			Units.nearbyEnemies(b.team, b.x, b.y, baseRange, unit -> {
				if (Mathf.chance(0.5f) && t.size < maxTarget) t.addUnique(unit);
			});
			t.removeAll(hc -> hc == null || hc.dead());
			if (t.any()) sound.at(b);
			for (int i = 0; i < t.size; i++) {
				Healthc hc = t.get(i);
				if (hc != null) {
					HFx.chainLightningFade.at(b.x, b.y, 4, color.cpy().a(0.3f), hc);
					Fx.randLifeSpark.at(hc.getX(), hc.getY(), b.angleTo(hc), color);
					if (hc instanceof Building bd) {
						bd.applySlowdown(0, 300);
					}
					if (hc instanceof Unit uc) {
						uc.apply(StatusEffects.disarmed, 300);
					}

					hc.damage(damage * state.rules.unitCrashDamage(b.team));
				}
			}
		}
		if (!state.isPaused()) Effect.shake(2, 2, b);
	}

	@Override
	public void draw(Bullet b) {
		float baseRange = splashDamageRadius * 0.1f + splashDamageRadius * 0.9f * b.finpow();

		Draw.color(color);
		Draw.z(Layer.flyingUnitLow - 0.1f);
		Draw.alpha(0.3f);
		Fill.circle(b.x, b.y, baseRange);

		Draw.z(Layer.bullet);
		Lines.stroke(splashDamageRadius / 12 * b.finpow(), color);
		Lines.circle(b.x, b.y, baseRange);

		Draw.color(color);
		for (int i = 0; i < 2; i++) {
			Drawf.tri(b.x, b.y, 12 * b.finpow(), baseRange * 1.5f, i * 180);
			Drawf.tri(b.x, b.y, 6 * b.finpow(), baseRange / 3, i * 180 + 90);
		}

		if (b.timer.get(1, 27 * b.foutpow() + 3)) {
			if (b.time >= b.lifetime - HFx.chainLightningFade.lifetime) return;
			for (int i = 0; i < 3; i++) {
				float a = Mathf.random(360);
				float x = Utils.dx(b.x, baseRange, a);
				float y = Utils.dy(b.y, baseRange, a);
				HFx.chainLightningFade.at(x, y, 8, color, b);
			}
		}
	}
}
