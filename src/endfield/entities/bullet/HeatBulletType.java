package endfield.entities.bullet;

import mindustry.content.StatusEffects;
import mindustry.entities.Units;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Hitboxc;
import mindustry.gen.Unit;
import mindustry.type.StatusEffect;

public class HeatBulletType extends BulletType {
	public StatusEffect status2 = StatusEffects.none;

	public float melDamageScl = 0.4f;
	public float maxExDamage = -1;
	public float meltDownTime = 10;

	@Override
	public void hitEntity(Bullet b, Hitboxc entity, float health) {
		super.hitEntity(b, entity, health);

		if (entity instanceof Unit u) {
			float mel = u.getDuration(status2);

			u.damage(Math.min(mel * melDamageScl, maxExDamage < 0 ? damage : maxExDamage));

			u.apply(status2, mel + meltDownTime);
		}
	}

	@Override
	public void createSplashDamage(Bullet b, float x, float y) {
		super.createSplashDamage(b, x, y);

		Units.nearbyEnemies(b.team, x, y, splashDamageRadius, u -> {
			float mel = u.getDuration(status2);

			u.damage(Math.min(mel * melDamageScl, maxExDamage < 0 ? splashDamage : maxExDamage));

			u.apply(status2, mel + meltDownTime);
		});
	}
}
