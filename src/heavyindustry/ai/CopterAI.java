package heavyindustry.ai;

import mindustry.ai.types.FlyingAI;

public class CopterAI extends FlyingAI {
	@Override
	public void circleAttack(float circleLength) {
		moveTo(target, unit.range() * 0.8f);
		unit.lookAt(target);
	}
}
