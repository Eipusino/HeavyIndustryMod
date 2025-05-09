package heavyindustry.entities.bullet;

import heavyindustry.content.HFx;
import heavyindustry.graphics.PositionLightning;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;
import mindustry.gen.Sounds;

public class PositionLightningBulletType extends BulletType {
	public int boltNum = 2;
	public float hitEffectRotation = 12f;

	public PositionLightningBulletType() {
		this(1f);
	}

	public PositionLightningBulletType(float damage) {
		super(0.0001f, damage);
		scaleLife = true;
		hitShake = 2f;
		hitSound = Sounds.spark;
		absorbable = keepVelocity = false;
		instantDisappear = true;
		collides = false;
		collidesAir = collidesGround = true;
		lightning = 3;
		lightningDamage = damage;
		lightningLength = lightningLengthRand = 6;
		hitEffect = shootEffect = smokeEffect = HFx.boolSelector;
		despawnEffect = Fx.none;
	}

	@Override
	public void init() {
		super.init();
		drawSize = Math.max(drawSize, maxRange * 2);
		if (hitEffect == HFx.boolSelector) hitEffect = HFx.lightningHitLarge(lightningColor);
		if (smokeEffect == HFx.boolSelector) smokeEffect = Fx.shootBigSmoke;
		if (shootEffect == HFx.boolSelector) shootEffect = HFx.shootLineSmall(lightningColor);
	}

	public float range() {
		return maxRange;
	}

	@Override
	public void init(Bullet b) {
		float length = b.lifetime * range() / lifetime;

		Healthc target = Damage.linecast(b, b.x, b.y, b.rotation(), length + 4f);
		b.data = target;

		if (target instanceof Hitboxc hit) {
			hit.collision(b, hit.x(), hit.y());
			b.collision(hit, hit.x(), hit.y());
		} else if (target instanceof Building tile) {
			if (tile.collide(b)) {
				tile.collision(b);
				hit(b, tile.x, tile.y);
			}
		}

		PositionLightning.createLength(b, b.team, b, length, b.rotation(), lightningColor, true, 0, 0, PositionLightning.WIDTH, boltNum, p -> {
			hitEffect.at(p.getX(), p.getY(), hitEffectRotation, hitColor);
			Effect.shake(hitShake, hitShake, p);
		});
		super.init(b);
	}

	@Override
	public void despawned(Bullet b) {
		despawnEffect.at(b.x, b.y, b.rotation(), lightningColor);
	}

	@Override
	public void hit(Bullet b) {}

	@Override
	public void hit(Bullet b, float x, float y) {}

	@Override
	public void draw(Bullet b) {}

	@Override
	public void drawLight(Bullet b) {}
}
