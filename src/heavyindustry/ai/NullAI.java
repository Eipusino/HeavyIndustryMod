package heavyindustry.ai;

import arc.math.geom.Position;
import arc.math.geom.Vec2;
import mindustry.entities.units.AIController;

public class NullAI extends AIController {
	@Override
	public void updateUnit() {}

	@Override
	public void updateVisuals() {}

	@Override
	public void updateTargeting() {}

	@Override
	public void faceTarget() {}

	@Override
	public void faceMovement() {}

	@Override
	public void pathfind(int pathTarget, boolean stopAtTargetTile, boolean avoidance) {}

	@Override
	public void updateWeapons() {}

	@Override
	public void unloadPayloads() {}

	@Override
	public void circleAttack(float circleLength) {}

	@Override
	public void circle(Position target, float circleLength, float speed) {}

	@Override
	public void moveTo(Position target, float circleLength, float smooth, boolean keepDistance, Vec2 offset, boolean arrive) {}
}
