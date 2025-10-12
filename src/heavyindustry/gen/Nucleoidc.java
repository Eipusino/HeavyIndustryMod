package heavyindustry.gen;

import heavyindustry.type.unit.NucleoidUnitType;
import mindustry.type.UnitType;

public interface Nucleoidc extends Unitc2 {
	@Override
	default NucleoidUnitType checkType(UnitType value) {
		if (value instanceof NucleoidUnitType nu) {
			return nu;
		}

		throw new ClassCastException("Unit's type must be NucleoidUnitType!");
	}

	float recentDamage();

	float reinforcementsReload();

	void recentDamage(float value);

	void reinforcementsReload(float value);
}
