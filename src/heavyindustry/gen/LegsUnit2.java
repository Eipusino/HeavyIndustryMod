package heavyindustry.gen;

import heavyindustry.entities.abilities.ICollideBlockerAbility;
import heavyindustry.type.unit.UnitType2;
import mindustry.Vars;
import mindustry.entities.Damage;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Hitboxc;
import mindustry.gen.LegsUnit;

public class LegsUnit2 extends LegsUnit implements Unitc2 {
	@Override
	public int classId() {
		return Entitys.getId(LegsUnit2.class);
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
