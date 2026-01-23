package endfield.gen;

import arc.util.io.Reads;
import arc.util.io.Writes;
import endfield.entities.abilities.ICollideBlockerAbility;
import endfield.type.unit.UnitType2;
import endfield.util.CollectionObjectMap;
import mindustry.Vars;
import mindustry.entities.Damage;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Hitboxc;
import mindustry.gen.UnitWaterMove;

import java.util.Map;

public class UnitWaterMove2 extends UnitWaterMove implements Unitc2 {
	public Map<String, Object> extraVar = new CollectionObjectMap<>(String.class, Object.class);

	@Override
	public int classId() {
		return Entitys.getId(UnitWaterMove2.class);
	}

	@Override
	public UnitType2 asType() {
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

		asType().init(this);
	}

	@Override
	public void write(Writes write) {
		super.write(write);

		write.i(asType().version());
		asType().write(this, write);
	}

	@Override
	public void read(Reads read) {
		super.read(read);

		asType().read(this, read, read.i());
	}

	@Override
	public void damage(float amount) {
		rawDamage(Damage.applyArmor(amount, armorOverride >= 0 ? armorOverride : armor) / healthMultiplier / Vars.state.rules.unitHealth(team) * asType().damageMultiplier);
	}

	@Override
	public Map<String, Object> extra() {
		return extraVar;
	}
}
