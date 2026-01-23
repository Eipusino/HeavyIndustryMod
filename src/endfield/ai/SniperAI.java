package endfield.ai;

import mindustry.Vars;
import mindustry.ai.types.FlyingAI;

public class SniperAI extends FlyingAI {
	public static final float APPROACHING_DST = 48f;

	@Override
	public void updateMovement() {
		unloadPayloads();

		if (target != null && unit.hasWeapons()) {
			if (unit.type.circleTarget) {
				circleAttack(120f);
			} else {
				moveTo(target, unit.type.maxRange - APPROACHING_DST);
				if (unit.type.faceTarget) {
					unit.lookAt(target);
					unit.lookAt(target);
				} else if (!unit.isShooting()) {
					unit.lookAt(target);
				}
			}
		}

		if (target == null && Vars.state.rules.waves && unit.team == Vars.state.rules.defaultTeam) {
			moveTo(getClosestSpawner(), Vars.state.rules.dropZoneRadius + 130f);
		}
	}
}
