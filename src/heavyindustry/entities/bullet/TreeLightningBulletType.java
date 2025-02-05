package heavyindustry.entities.bullet;

import arc.graphics.*;
import heavyindustry.graphics.*;
import mindustry.content.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;

public class TreeLightningBulletType extends BulletType {
	public float lightningRange = 120f;
	public int amount = 8;
	public Color lightningRoot = Color.pink;
	public Color lightningLeaf = Color.cyan;

	public TreeLightningBulletType(float damage, float speed) {
		super(damage, speed);
		lifetime = 1;
		despawnEffect = Fx.none;
		hitEffect = Fx.hitLancer;
		keepVelocity = false;
		hittable = false;
		status = StatusEffects.shocked;
	}

	public TreeLightningBulletType() {
		this(1f, 0f);
	}

	@Override
	protected float calculateRange() {
		return lightningRange;
	}

	@Override
	public float estimateDPS() {
		return damage * lightningRange / 10 * (amount + amount * 0.5f);
	}

	@Override
	public void draw(Bullet b) {}

	@Override
	public void init(Bullet b) {
		TreeLightning.create(b, lightningRoot, lightningLeaf, damage, amount, lightningRange);
	}
}
