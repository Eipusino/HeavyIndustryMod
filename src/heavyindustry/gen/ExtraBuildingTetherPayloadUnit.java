package heavyindustry.gen;

import heavyindustry.type.unit.ExtraUnitType;
import mindustry.content.Fx;
import mindustry.gen.BuildingTetherPayloadUnit;

public class ExtraBuildingTetherPayloadUnit extends BuildingTetherPayloadUnit implements ExtraUnitc {
	protected ExtraBuildingTetherPayloadUnit() {}

	@Override
	public int classId() {
		return EntityRegister.getId(ExtraBuildingTetherPayloadUnit.class);
	}

	@Override
	public void rawDamage(float amount) {
		if (type instanceof ExtraUnitType fType) {
			boolean hadShields = shield > 0.0001f;

			if (Float.isNaN(health)) health = 0f;

			if (hadShields) {
				shieldAlpha = 1f;
			}

			float a = amount * fType.damageMultiplier;

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

	public static ExtraBuildingTetherPayloadUnit create() {
		return new ExtraBuildingTetherPayloadUnit();
	}
}
