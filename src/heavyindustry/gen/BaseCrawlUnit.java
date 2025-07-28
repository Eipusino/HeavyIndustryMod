package heavyindustry.gen;

import heavyindustry.type.unit.BaseUnitType;
import mindustry.content.Fx;
import mindustry.gen.CrawlUnit;

public class BaseCrawlUnit extends CrawlUnit implements BaseUnitc {
	public BaseCrawlUnit() {}

	@Override
	public int classId() {
		return Entitys.getId(BaseCrawlUnit.class);
	}

	@Override
	public void rawDamage(float amount) {
		if (type instanceof BaseUnitType bt) {
			boolean hadShields = shield > 0.0001f;

			if (Float.isNaN(health)) health = 0f;

			if (hadShields) {
				shieldAlpha = 1f;
			}

			float a = amount * bt.damageMultiplier;

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
