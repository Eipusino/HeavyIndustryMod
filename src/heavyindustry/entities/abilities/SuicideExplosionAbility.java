package heavyindustry.entities.abilities;

import arc.math.Angles;
import arc.math.Mathf;
import mindustry.content.Bullets;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.abilities.Ability;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Unit;

public class SuicideExplosionAbility extends Ability {
	public float radius = 8;
	public float damage = 1;
	public int fragments = 0;

	public BulletType frag = Bullets.placeholder;

	public float fragVelocityMin = 0;
	public float fragVelocityMax = 1;
	public float fragLifeMin = 1;
	public float fragLifeMax = 1;

	public SuicideExplosionAbility() {}

	@Override
	public void death(Unit unit) {
		super.death(unit);
		Fx.dynamicExplosion.at(unit.x, unit.y, radius / 16f);
		Damage.damage(unit.team, unit.x, unit.y, damage, radius);
		if (fragments > 0) {
			for (int i = 0; i < fragments; i++) {
				float len = Mathf.random(1f, 7f);
				float a = Mathf.range(180);
				frag.create(unit, unit.team, unit.x + Angles.trnsx(a, len), unit.y + Angles.trnsy(a, len), a, Mathf.random(fragVelocityMin, fragVelocityMax), Mathf.random(fragLifeMin, fragLifeMax));
			}
		}
	}
}
