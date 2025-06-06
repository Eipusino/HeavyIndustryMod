package heavyindustry.ai;

import mindustry.entities.Predict;
import mindustry.entities.Units;
import mindustry.entities.units.AIController;

public class SentryAI extends AIController {
	@Override
	public void updateMovement() {
		if (!Units.invalidateTarget(target, unit, unit.range()) && unit.type.faceTarget && unit.type.hasWeapons()) {
			unit.lookAt(Predict.intercept(unit, target, unit.type.weapons.first().bullet.speed));
		}
	}

	@Override
	public boolean retarget() {
		return timer.get(timerTarget, target == null ? 10f : 20f);
	}
}
