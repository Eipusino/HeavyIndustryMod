package heavyindustry.gen;

import arc.util.Time;
import heavyindustry.math.Mathm;
import heavyindustry.type.unit.UnitType2;
import mindustry.Vars;
import mindustry.entities.Damage;

public class DamageAbsorbMechUnit extends MechUnit2 implements DamageAbsorbc {
	@Override
	public int classId() {
		return Entitys.getId(DamageAbsorbMechUnit.class);
	}

	@Override
	public float realDamage(boolean isStatus, float amount) {
		return !isStatus && type instanceof UnitType2 fu ? amount * Mathm.clamp(1 - fu.absorption) : amount;
	}

	@Override
	public void damage(float amount) {
		damage(false, amount);
	}

	@Override
	public void damage(boolean isStatus, float amount) {
		rawDamage(Damage.applyArmor(realDamage(isStatus, amount), armor) / healthMultiplier / Vars.state.rules.unitHealthMultiplier);
	}

	@Override
	public void damagePierce(float amount, boolean withEffect) {
		damagePierce(false, amount, withEffect);
	}

	@Override
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

	@Override
	public void damageContinuous(boolean isStatus, float amount) {
		damage(realDamage(isStatus, amount) * Time.delta, hitTime <= -10 + hitDuration);
	}

	@Override
	public void damageContinuousPierce(float amount) {
		damageContinuousPierce(false, amount);
	}

	@Override
	public void damageContinuousPierce(boolean isStatus, float amount) {
		damagePierce(realDamage(isStatus, amount) * Time.delta, hitTime <= -20 + hitDuration);
	}
}
