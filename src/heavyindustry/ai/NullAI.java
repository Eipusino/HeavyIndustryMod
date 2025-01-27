package heavyindustry.ai;

import mindustry.entities.units.*;
import mindustry.gen.*;

public class NullAI implements UnitController {
	Unit unit;

	@Override
	public void unit(Unit u) {
		unit = u;
	}

	@Override
	public Unit unit() {
		return unit;
	}
}
