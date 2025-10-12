package heavyindustry.gen;

import heavyindustry.entities.abilities.ICollideBlockerAbility;
import heavyindustry.type.unit.UnitType2;
import mindustry.Vars;
import mindustry.entities.Damage;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Hitboxc;
import mindustry.gen.UnitEntity;

public class Unit2 extends UnitEntity implements Unitc2 {
	@Override
	public int classId() {
		return Entitys.getId(Unit2.class);
	}

	@Override
	public UnitType2 checkType() {
		return (UnitType2) type;
	}

	@Override
	public boolean collides(Hitboxc other) {
		for (Ability ability : abilities) {
			if (ability instanceof ICollideBlockerAbility blocker && blocker.blockedCollides(this, other)) return false;
		}

		return super.collides(other);
	}

	@Override
	public void damage(float amount) {
		rawDamage(Damage.applyArmor(amount, armorOverride >= 0 ? armorOverride : armor) / healthMultiplier / Vars.state.rules.unitHealth(team) * checkType().damageMultiplier);
	}
}
