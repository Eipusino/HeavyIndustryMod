package heavyindustry.entities.bullet;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import heavyindustry.entities.HDamage;
import heavyindustry.graphics.HPal;
import heavyindustry.util.Constant;
import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;

import static heavyindustry.HVars.MOD_NAME;

public class EmpathySwordBulletType extends BulletType {
	public TextureRegion sword, swordSide;

	public EmpathySwordBulletType() {
		super(18f, 400f);
		lifetime = 1.1f * 60f;
		pierce = pierceArmor = pierceBuilding = true;
		trailColor = HPal.empathy;
		drag = 0.005f;
		collides = collidesTiles = false;
		hittable = false;
		hitEffect = Fx.hitBulletColor;
		hitColor = HPal.empathy;
		keepVelocity = false;
	}

	@Override
	public void load() {
		super.load();

		sword = Core.atlas.find(MOD_NAME + "-sword");
		swordSide = Core.atlas.find(MOD_NAME + "-sword-side");
	}

	@Override
	public void update(Bullet b) {
		super.update(b);

		if (b.timer(0, 5f)) {
			float length = 40 / 2f;
			float dam = 370f;
			Vec2 v = Tmp.v1.trns(b.rotation(), length);
			HDamage.hitLaser(b.team, 2f, -v.x + b.x, -v.y + b.y, v.x + b.x, v.y + b.y, null, Constant.BOOLF_HEALTHC_FALSE, (h, x, y) -> {
				hit(b, x, y);
				float tdam = Math.max(dam, h.maxHealth() / 700f);
				h.health(h.health() - tdam);

				if (h instanceof Building bl && bl.health <= 0f) bl.kill();
			});
		}
	}

	@Override
	public void draw(Bullet b) {
		//float fin = b.fin();
		float fin = Mathf.clamp(b.time / ((sword.width * Draw.scl * 0.5f) / speed));
		float rot = b.time * 3f + Mathf.randomSeedRange(b.id, 180f);
		float w = Mathf.cosDeg(rot);
		float h = Mathf.sinDeg(rot);

		TextureRegion t1 = Tmp.tr1, t2 = Tmp.tr2;
		t1.set(sword);
		t2.set(swordSide);
		float u1 = Mathf.lerp(sword.u2, sword.u, fin);
		float u2 = Mathf.lerp(swordSide.u2, swordSide.u, fin);
		t1.setU(u1);
		t2.setU(u2);

		Draw.color();
		Draw.rect(t1, b.x, b.y, t1.width * Draw.scl * 0.5f, t1.height * Draw.scl * 0.5f * w, b.rotation());
		Draw.rect(t2, b.x, b.y, t2.width * Draw.scl * 0.5f, t2.height * Draw.scl * 0.5f * h, b.rotation());
	}

	@Override
	public void drawLight(Bullet b) {}
}
