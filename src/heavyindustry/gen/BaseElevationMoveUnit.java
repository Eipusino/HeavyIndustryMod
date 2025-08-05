package heavyindustry.gen;

import heavyindustry.type.unit.BaseUnitType;
import mindustry.content.Fx;
import mindustry.gen.ElevationMoveUnit;

public class BaseElevationMoveUnit extends ElevationMoveUnit implements BaseUnitc {
	@Override
	public int classId() {
		return Entitys.getId(BaseElevationMoveUnit.class);
	}

	@Override
	public void rawDamage(float amount) {
		if (type instanceof BaseUnitType but) {
			boolean hadShields = shield > 0.0001f;

			if (Float.isNaN(health)) health = 0f;

			if (hadShields) {
				shieldAlpha = 1f;
			}

			float damage = amount * but.damageMultiplier;

			float shieldDamage = Math.min(Math.max(shield, 0), damage);
			shield -= shieldDamage;
			hitTime = 1f;
			damage -= shieldDamage;

			if (damage > 0 && type.killable) {
				health -= damage;
				if (health <= 0 && !dead) {
					kill();
				}

				if (hadShields && shield <= 0.0001f) {
					Fx.unitShieldBreak.at(x, y, 0, type.shieldColor(this), this);
				}
			}
		}
	}
}
