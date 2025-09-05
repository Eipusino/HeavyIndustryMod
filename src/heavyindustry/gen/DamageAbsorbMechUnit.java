package heavyindustry.gen;

import arc.math.Mathf;
import arc.util.Time;
import heavyindustry.type.unit.BaseUnitType;
import mindustry.Vars;
import mindustry.entities.Damage;

public class DamageAbsorbMechUnit extends BaseMechUnit implements DamageAbsorbc {
	@Override
	public int classId() {
		return Entitys.getId(DamageAbsorbMechUnit.class);
	}

	public float realDamage(boolean isStatus, float amount) {
		return !isStatus && type instanceof BaseUnitType fu ? amount * Mathf.clamp(1 - fu.absorption) : amount;
	}

	@Override
	public void damage(float amount) {
		damage(false, amount);
	}

	public void damage(boolean isStatus, float amount) {
		rawDamage(Damage.applyArmor(realDamage(isStatus, amount), armor) / healthMultiplier / Vars.state.rules.unitHealthMultiplier);
	}

	@Override
	public void damagePierce(float amount, boolean withEffect) {
		damagePierce(false, amount, withEffect);
	}

	public void damagePierce(boolean isStatus, float amount, boolean withEffect) {
		float pre = hitTime;
		rawDamage(realDamage(isStatus, amount) / healthMultiplier / Vars.state.rules.unitHealth(team));
		if (!withEffect) {
			hitTime = pre;
		}
	}

	@Override
	public void damageContinuous(float amount) {
		damageContinuous(false, amount);
	}

	public void damageContinuous(boolean isStatus, float amount) {
		damage(realDamage(isStatus, amount) * Time.delta, hitTime <= -10 + hitDuration);
	}

	@Override
	public void damageContinuousPierce(float amount) {
		damageContinuousPierce(false, amount);
	}

	public void damageContinuousPierce(boolean isStatus, float amount) {
		damagePierce(realDamage(isStatus, amount) * Time.delta, hitTime <= -20 + hitDuration);
	}
}
