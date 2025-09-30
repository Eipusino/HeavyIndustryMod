package heavyindustry.entities.bullet;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import heavyindustry.entities.HDamage;
import heavyindustry.graphics.Drawn;
import heavyindustry.graphics.HPal;
import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;

import static heavyindustry.HVars.MOD_NAME;

public class EmpathyPinBulletType extends BulletType {
	public TextureRegion region;

	public EmpathyPinBulletType() {
		super(2f, 800f);
		lifetime = 120f;
		collides = collidesTiles = false;
		pierce = true;
		absorbable = false;
		hittable = false;
		keepVelocity = false;
		despawnEffect = Fx.none;
		shootEffect = Fx.none;
		hitEffect = Fx.hitBulletColor;
		hitColor = HPal.empathy;
		drawSize = 40f * 1.5f;
	}

	@Override
	public void load() {
		region = Core.atlas.find(MOD_NAME + "-heart-pin");
	}

	@Override
	public void update(Bullet b) {
		b.vel.setLength(Mathf.lerp(2f, 16f, Mathf.clamp(b.time / 60f)));
		if (b.timer(0, 5f)) {
			float length = 40 / 2f;
			float dam = 700f;
			Vec2 v = Tmp.v1.trns(b.rotation(), length);
			HDamage.hitLaser(b.team, 2f, -v.x + b.x, -v.y + b.y, v.x + b.x, v.y + b.y, null, h -> false, (h, x, y) -> {
				hit(b, x, y);
				float tdam = Math.max(dam, h.maxHealth() / 500f);
				h.health(h.health() - tdam);

				if (h instanceof Building bl && bl.health <= 0f) bl.kill();
			});
		}
	}

	@Override
	public void draw(Bullet b) {
		float out = Mathf.clamp((b.lifetime - b.time) / 16f);
		float out2 = 1 - Mathf.clamp(b.time / 6f);
		Draw.color(HPal.empathyAdd);
		Draw.alpha(out);
		Draw.blend(Blending.additive);
		if (out2 > 0.001f) {
			Drawn.diamond(b.x, b.y, 5f * out2, 40f, b.rotation());
		}
		Draw.rect(region, b.x, b.y, b.rotation() - 90f);
		Draw.blend();
	}

	@Override
	public void drawLight(Bullet b) {

	}
}
