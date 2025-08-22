package heavyindustry.gen;

import heavyindustry.type.unit.BaseUnitType;
import mindustry.Vars;
import mindustry.entities.Damage;
import mindustry.gen.UnitEntity;

public class BaseUnit extends UnitEntity implements BaseUnitc {
	@Override
	public int classId() {
		return Entitys.getId(BaseUnit.class);
	}

	@Override
	public BaseUnitType checkType() {
		return (BaseUnitType) type;
	}

	@Override
	public void damage(float amount) {
		rawDamage(Damage.applyArmor(amount, armorOverride >= 0 ? armorOverride : armor) / healthMultiplier / Vars.state.rules.unitHealth(team) * checkType().damageMultiplier);
	}
}
