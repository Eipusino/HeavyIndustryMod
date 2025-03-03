package heavyindustry.gen;

import arc.func.Prov;
import mindustry.gen.Entityc;
import mindustry.gen.Unitc;

public interface ExtraUnitc extends Unitc {
	default <T extends Entityc> Prov<T> provSelf() {
		return this::self;
	}
}
