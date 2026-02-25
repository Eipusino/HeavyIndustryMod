package endfield.gen;

import arc.func.Prov;
import endfield.type.unit.UnitType2;
import endfield.util.ExtraVariable;
import mindustry.gen.Entityc;
import mindustry.gen.Unitc;
import mindustry.type.UnitType;

public interface Unitc2 extends Unitc, ExtraVariable {
	default UnitType2 asType() {
		return (UnitType2) type();
	}

	default UnitType2 asType(UnitType type) {
		return (UnitType2) type;
	}

	default <T extends Entityc> Prov<T> provSelf() {
		return this::self;
	}

	default <T> Prov<T> provAs() {
		return this::as;
	}
}
