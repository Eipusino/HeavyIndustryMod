package heavyindustry.gen;

import heavyindustry.entities.abilities.ICollideBlockerAbility;
import heavyindustry.type.unit.UnitType2;
import heavyindustry.util.CollectionObjectMap;
import mindustry.Vars;
import mindustry.entities.Damage;
import mindustry.entities.abilities.Ability;
import mindustry.gen.ElevationMoveUnit;
import mindustry.gen.Hitboxc;

import java.util.Map;

public class ElevationMoveUnit2 extends ElevationMoveUnit implements Unitc2 {
	public Map<String, Object> extraVar = new CollectionObjectMap<>(String.class, Object.class);

	@Override
	public int classId() {
		return Entitys.getId(ElevationMoveUnit2.class);
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

	@Override
	public Map<String, Object> extra() {
		return extraVar;
	}
}
