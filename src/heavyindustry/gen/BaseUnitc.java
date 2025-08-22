package heavyindustry.gen;

import arc.func.Prov;
import heavyindustry.type.unit.BaseUnitType;
import mindustry.gen.Entityc;
import mindustry.gen.Unitc;
import mindustry.type.UnitType;

public interface BaseUnitc extends Unitc {
	default BaseUnitType checkType(UnitType value) {
		return (BaseUnitType) value;
	}

	default BaseUnitType checkType() {
		return (BaseUnitType) type();
	}

	default <T extends Entityc> Prov<T> provSelf() {
		return this::self;
	}
}
