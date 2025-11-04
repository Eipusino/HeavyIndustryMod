package heavyindustry.gen;

import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.math.Mathm;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.entities.EntityCollisions.SolidPred;
import mindustry.world.blocks.environment.Floor;

public class FloatMechCoreUnit extends Unit2 implements Corec, FloatMechc {
	public float baseRotation;
	public transient float walkTime, walkExtension;
	public transient boolean walked;

	@Override
	public int classId() {
		return Entitys.getId(FloatMechCoreUnit.class);
	}

	@Override
	public void update() {
		super.update();

		elevation = Mathf.approachDelta(elevation, onSolid() ? 1f : 0f, type.riseSpeed);

		//trigger animation only when walking manually
		if (walked || Vars.net.client()) {
			float len = deltaLen();
			baseRotation = Angles.moveToward(baseRotation, deltaAngle(), type().baseRotateSpeed * Mathm.clamp(len / type().speed / Time.delta) * Time.delta);
			walkTime += len;
			walked = false;
		}

		//update mech effects
		float extend = walkExtend(false);
		float base = walkExtend(true);
		float extendScl = base % 1f;

		float lastExtend = walkExtension;

		if (!Vars.headless && extendScl < lastExtend && base % 2f > 1f && !isFlying() && !inFogTo(Vars.player.team())) {
			int side = -Mathf.sign(extend);
			float width = hitSize / 2f * side, length = type.mechStride * 1.35f;

			float cx = x + Angles.trnsx(baseRotation, length, width),
					cy = y + Angles.trnsy(baseRotation, length, width);

			if (type.stepShake > 0) {
				Effect.shake(type.stepShake, type.stepShake, cx, cy);
			}

			if (type.mechStepParticles) {
				Effect.floorDust(cx, cy, hitSize / 8f);
			}
		}

		walkExtension = extendScl;
	}

	@Override
	public Floor drownFloor() {
		//large mechs can only drown when all the nearby floors are deep
		if (hitSize >= 12 && canDrown()) {
			for (Point2 p : Geometry.d8) {
				Floor f = Vars.world.floorWorld(x + p.x * Vars.tilesize, y + p.y * Vars.tilesize);
				if (!f.isDeep()) {
					return null;
				}
			}
		}
		return canDrown() ? floorOn() : null;
	}

	@Override
	public float baseRotation() {
		return baseRotation;
	}

	@Override
	public float walkExtend(boolean scaled) {
		//now ranges from -maxExtension to maxExtension*3
		float raw = walkTime % (type.mechStride * 4);

		if (scaled) return raw / type.mechStride;

		if (raw > type.mechStride * 3) raw = raw - type.mechStride * 4;
		else if (raw > type.mechStride * 2) raw = type.mechStride * 2 - raw;
		else if (raw > type.mechStride) raw = type.mechStride * 2 - raw;

		return raw;
	}

	@Override
	public float walkExtension() {
		return walkExtension;
	}

	@Override
	public float walkTime() {
		return walkTime;
	}

	@Override
	public void baseRotation(float value) {
		baseRotation = value;
	}

	@Override
	public void walkExtension(float value) {
		walkExtension = value;
	}

	@Override
	public void walkTime(float value) {
		walkTime = value;
	}

	@Override
	public void rotateMove(Vec2 vec) {
		//mechs use baseRotation to rotate, not rotation.
		moveAt(Tmp.v2.trns(baseRotation, vec.len()));

		if (!vec.isZero()) {
			baseRotation = Angles.moveToward(baseRotation, vec.angle(), type.rotateSpeed * Math.max(Time.delta, 1));
		}
	}

	@Override
	public void moveAt(Vec2 vector, float acceleration) {
		//mark walking state when moving in a controlled manner
		if (!vector.isZero()) {
			walked = true;
		}

		super.moveAt(vector, acceleration);
	}

	@Override
	public void approach(Vec2 vector) {
		super.approach(vector);

		//mark walking state when moving in a controlled manner
		if (!vector.isZero(0.001f)) {
			walked = true;
		}
	}

	@Override
	public SolidPred solidity() {
		return null;
	}
}
