package endfield.entities.bullet;

import arc.math.geom.Vec2;
import endfield.content.Fx2;
import endfield.graphics.Draws;
import endfield.graphics.Pal2;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;

public class EdgeFragBulletType extends BulletType {
	public EdgeFragBulletType(float speed, float damage) {
		super(speed, damage);
		splashDamage = 40;
		splashDamageRadius = 24;
		hitSize = 3;
		lifetime = 120;
		despawnHit = true;
		hitEffect = Fx2.diamondSpark;
		hitColor = Pal2.matrixNet;

		collidesTiles = false;

		homingRange = 160;
		homingPower = 0.075f;

		trailColor = Pal2.matrixNet;
		trailLength = 25;
		trailWidth = 3f;
	}

	public EdgeFragBulletType() {
		this(4f, 80f);
	}

	@Override
	public void draw(Bullet b) {
		super.draw(b);
		Draws.drawDiamond(b.x, b.y, 10, 4, b.rotation());
	}

	@Override
	public void update(Bullet b) {
		super.update(b);

		b.vel.lerpDelta(Vec2.ZERO, 0.04f);
	}
}
