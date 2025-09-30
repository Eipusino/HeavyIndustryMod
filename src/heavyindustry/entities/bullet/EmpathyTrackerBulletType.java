package heavyindustry.entities.bullet;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.math.geom.Vec2;
import arc.util.Tmp;
import heavyindustry.graphics.Drawn;
import heavyindustry.graphics.HPal;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Healthc;

public class EmpathyTrackerBulletType extends BulletType {
	public EmpathyTrackerBulletType() {
		super(4f, 200f);

		lifetime = 4f * 60f;
		pierce = pierceArmor = pierceBuilding = true;
		trailLength = 10;
		trailWidth = 3f;
		trailColor = HPal.empathy;
		drag = 0.005f;
		collides = collidesTiles = false;
		hitEffect = Fx.hitBulletColor;
		hitColor = HPal.empathy;
		keepVelocity = false;
	}

	@Override
	public void update(Bullet b) {
		super.update(b);
		Rect r = Tmp.r3;
		b.hitbox(r);
		float dam = 170f;

		if (b.timer(0, 15)) {
			b.collided.clear();
		}

		Groups.unit.intersect(r.x, r.y, r.width, r.height, u -> {
			if (u.team != b.team && !b.collided.contains(u.id)) {
				hit(b, b.x, b.y);

				float tdam = Math.max(dam, u.maxHealth / 1200f);
				u.health -= tdam;
				b.collided.add(u.id);
			}
		});
		Building build = Vars.world.buildWorld(b.x, b.y);
		if (build != null && build.team != b.team && !b.collided.contains(build.id)) {
			hit(b, b.x, b.y);

			float tdam = Math.max(dam, build.maxHealth / 1200f);
			build.health -= tdam;
			if (build.health <= 0f) build.kill();
			b.collided.add(build.id);
		}
	}

	@Override
	public void updateHoming(Bullet b) {
		if (b.data instanceof Healthc target) {
			float fin = Mathf.clamp((b.time - b.fdata) / 20f);
			float rotSlope = Mathf.slope(Mathf.clamp(((b.time - b.fdata) / 40f) % 4));
			Vec2 move = Tmp.v1.set(target.x(), target.y()).sub(b.x, b.y).scl(1f / 200f).add(b.vel.x / 4f, b.vel.y / 4f).limit(3f).scl(fin);
			b.vel.add(move).limit(15f);
			if (rotSlope > 0) {
				b.vel.setAngle(Mathf.slerpDelta(b.vel.angle(), b.angleTo(target), 0.3f * rotSlope));
			}
			if (!target.isValid()) {
				b.data = null;
			}
		}
	}

	@Override
	public void draw(Bullet b) {
		drawTrail(b);
		Draw.color(trailColor);
		Drawn.diamond(b.x, b.y, trailWidth, trailWidth * 3, b.rotation());
		Draw.color();
	}
}
