package heavyindustry.gen;

import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.entities.abilities.ICollideBlockerAbility;
import heavyindustry.type.unit.UnitType2;
import heavyindustry.util.CollectionObjectMap;
import mindustry.Vars;
import mindustry.entities.Damage;
import mindustry.entities.abilities.Ability;
import mindustry.gen.BuildingTetherPayloadUnit;
import mindustry.gen.Hitboxc;

import java.util.Map;

public class BuildingTetherPayloadUnit2 extends BuildingTetherPayloadUnit implements Unitc2 {
	public Map<String, Object> extraVar = new CollectionObjectMap<>(String.class, Object.class);

	@Override
	public int classId() {
		return Entitys.getId(BuildingTetherPayloadUnit2.class);
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
	public void add() {
		super.add();

		checkType().init(this);
	}

	@Override
	public void write(Writes write) {
		super.write(write);

		write.i(checkType().version());
		checkType().write(this, write);
	}

	@Override
	public void read(Reads read) {
		super.read(read);

		checkType().read(this, read, read.i());
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
