package heavyindustry.gen;

import heavyindustry.type.unit.BaseUnitType;
import mindustry.content.Fx;
import mindustry.gen.LegsUnit;

public class BaseLegsUnit extends LegsUnit implements BaseUnitc {
	public BaseLegsUnit() {}

	@Override
	public int classId() {
		return Entitys.getId(BaseLegsUnit.class);
	}

	@Override
	public void rawDamage(float amount) {
		if (type instanceof BaseUnitType et) {
			boolean hadShields = shield > 0.0001f;

			if (Float.isNaN(health)) health = 0f;

			if (hadShields) {
				shieldAlpha = 1f;
			}

			float a = amount * et.damageMultiplier;

			float shieldDamage = Math.min(Math.max(shield, 0), a);
			shield -= shieldDamage;
			hitTime = 1f;
			a -= shieldDamage;

			if (a > 0 && type.killable) {
				health -= a;
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
