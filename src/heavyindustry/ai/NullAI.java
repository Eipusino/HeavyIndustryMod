package heavyindustry.ai;

import mindustry.entities.units.UnitController;
import mindustry.gen.Unit;

public class NullAI implements UnitController {
	protected Unit unit;

	@Override
	public void unit(Unit u) {
		if (unit != u) unit = u;
	}

	@Override
	public Unit unit() {
		return unit;
	}
}
