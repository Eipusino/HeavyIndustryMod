package heavyindustry.gen;

import arc.func.Prov;
import heavyindustry.type.unit.BaseUnitType;
import mindustry.gen.Entityc;
import mindustry.gen.Unitc;
import mindustry.type.UnitType;

public interface BaseUnitc extends Unitc {
	default BaseUnitType checkType(UnitType def) {
		if (def instanceof BaseUnitType bu) {
			return bu;
		}

		throw new ClassCastException("Unit's type must be BaseUnitType!");
	}

	default <T extends Entityc> Prov<T> provSelf() {
		return this::self;
	}
}
