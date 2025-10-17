package heavyindustry.entities.bullet;

import arc.graphics.Color;
import arc.math.Mathf;
import heavyindustry.content.HFx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;

import static mindustry.Vars.tilesize;

public class ShieldBreakerBulletType extends BasicBulletType {
	protected static BulletType breakType = new EffectBulletType(3f) {{
		absorbable = true;
		collides = false;
		lifetime = 8f;
		drawSize = 0;
		damage = 1;
	}
		@Override
		public void despawned(Bullet b) {
			if (b.absorbed && b.data instanceof Color color) {
				HFx.shuttle.at(b.x, b.y, Mathf.random(360f), color, b.damage / tilesize / 2f);
				Effect.shake(b.damage / 100, b.damage / 100, b);
				Sounds.plasmaboom.at(b);
			}
		}
	};

	public float fragSpawnSpacing = 5;
	public float maxShieldDamage;

	public ShieldBreakerBulletType(float speed, float damage, String bulletSprite, float shieldDamage) {
		super(speed, damage, bulletSprite);
		splashDamage = splashDamageRadius = -1f;
		maxShieldDamage = shieldDamage;
		absorbable = false;
	}

	public ShieldBreakerBulletType(float speed, float damage, float shieldDamage) {
		this(speed, damage, "bullet", shieldDamage);
	}

	public ShieldBreakerBulletType() {
		this(1f, 1f, "bullet", 500f);
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void update(Bullet b) {
		super.update(b);
		if (b.timer(5, fragSpawnSpacing)) breakType.create(b, b.team, b.x, b.y, 0, maxShieldDamage, 0, 1, backColor);
	}
}
