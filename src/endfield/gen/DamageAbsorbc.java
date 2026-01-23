package endfield.gen;

import arc.util.Time;
import endfield.math.Mathm;
import endfield.type.unit.UnitType2;
import mindustry.Vars;
import mindustry.entities.Damage;
import mindustry.gen.MechUnit;
import mindustry.gen.Unitc;

public interface DamageAbsorbc extends Unitc {
	default float realDamage(boolean isStatus, float amount) {
		return !isStatus && type() instanceof UnitType2 bu ? amount * Mathm.clamp(1 - bu.absorption) : amount;
	}

	@Override
	default void damage(float amount) {
		damage(false, amount);
	}

	default void damage(boolean isStatus, float amount) {
		rawDamage(Damage.applyArmor(realDamage(isStatus, amount), armor()) / healthMultiplier() / Vars.state.rules.unitHealthMultiplier);
	}

	default void damagePierce(boolean isStatus, float amount, boolean withEffect) {
		float pre = hitTime();
		rawDamage(realDamage(isStatus, amount) / healthMultiplier() / Vars.state.rules.unitHealth(team()));
		if (!withEffect) {
			hitTime(pre);
		}
	}

	@Override
	default void damageContinuous(float amount) {
		damageContinuous(false, amount);
	}

	default void damageContinuous(boolean isStatus, float amount) {
		damage(realDamage(isStatus, amount) * Time.delta, hitTime() <= -10 + MechUnit.hitDuration);
	}

	@Override
	default void damageContinuousPierce(float amount) {
		damageContinuousPierce(false, amount);
	}

	default void damageContinuousPierce(boolean isStatus, float amount) {
		damagePierce(realDamage(isStatus, amount) * Time.delta, hitTime() <= -20 + MechUnit.hitDuration);
	}
}
