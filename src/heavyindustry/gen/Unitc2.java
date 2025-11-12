package heavyindustry.gen;

import arc.func.Prov;
import heavyindustry.type.unit.UnitType2;
import mindustry.gen.Entityc;
import mindustry.gen.Unitc;

public interface Unitc2 extends Unitc {
	default UnitType2 checkType() {
		return (UnitType2) type();
	}

	default <T extends Entityc> Prov<T> provSelf() {
		return this::self;
	}

	default <T> Prov<T> provAs() {
		return this::as;
	}
}
