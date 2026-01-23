package endfield.gen;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import endfield.type.unit.SentryUnitType;
import mindustry.Vars;
import mindustry.type.UnitType;

public class SentryUnit extends TimedKillUnit2 implements Sentryc {
	public Vec2 anchorVel = new Vec2();
	public float anchorX, anchorY, anchorRot;
	public float anchorDrag;

	@Override
	public int classId() {
		return Entitys.getId(SentryUnit.class);
	}

	@Override
	public void setType(UnitType type) {
		super.setType(type);
		if (type instanceof SentryUnitType sType) {
			anchorDrag = sType.anchorDrag;
		}
	}

	@Override
	public void update() {
		super.update();
		if (type instanceof SentryUnitType sType) {
			if (!Vars.net.client() || isLocal()) {
				float offset = anchorX;
				float range = anchorY;
				anchorX += anchorVel.x * Time.delta; //I'm sure letting the anchors overlap won't be problematic in anyway.
				anchorY += anchorVel.y * Time.delta;
				if (Mathf.equal(offset, anchorX)) {
					anchorVel.x = 0f;
				}
				if (Mathf.equal(range, anchorY)) {
					anchorVel.y = 0f;
				}

				anchorVel.scl(Math.max(1f - anchorDrag * Time.delta, 0f));
			}

			anchorDrag = sType.anchorDrag * (isGrounded() ? floorOn().dragMultiplier : 1f) * dragMultiplier * Vars.state.rules.dragMultiplier;

			// Pull unit to anchor.
			// Similar to impulseNet, does not factor in mass
			Tmp.v1.set(anchorX, anchorY).sub(x, y).limit(dst(anchorX, anchorY) * sType.pullScale * Time.delta);
			vel.add(Tmp.v1);

			// Manually move units to simulate velocity for remote players
			if (isRemote()) move(Tmp.v1);

			// Pull anchor to unit
			Tmp.v1.set(x, y).sub(anchorX, anchorY).limit(dst(anchorX, anchorY) * sType.anchorPullScale * Time.delta);
			anchorVel.add(Tmp.v1);

			if (isRemote()) {
				anchorX += Tmp.v1.x;
				anchorY += Tmp.v1.y;
			}
		}
	}

	@Override
	public void set(float x, float y) {
		super.set(x, y);
		anchorX = x;
		anchorY = y;
	}

	@Override
	public void rotation(float rotation) {
		super.rotation(rotation); //Improperly replaces rotation. Glenn will fix later.
		anchorRot = rotation;
	}

	@Override
	public float prefRotation() {
		return rotation;
	}

	@Override
	public void wobble() {
		anchorX += Mathf.sin(Time.time + (float) (id % 10 * 12), 25f, 0.05f) * Time.delta * elevation;
		anchorY += Mathf.cos(Time.time + (float) (id % 10 * 12), 25f, 0.05f) * Time.delta * elevation;
	}

	@Override
	public Vec2 anchorVel() {
		return anchorVel;
	}

	@Override
	public float anchorDrag() {
		return anchorDrag;
	}

	@Override
	public float anchorRot() {
		return anchorRot;
	}

	@Override
	public float anchorX() {
		return anchorX;
	}

	@Override
	public float anchorY() {
		return anchorY;
	}

	@Override
	public void anchorVel(Vec2 value) {
		anchorVel = value;
	}

	@Override
	public void anchorDrag(float value) {
		anchorDrag = value;
	}

	@Override
	public void anchorRot(float value) {
		anchorRot = value;
	}

	@Override
	public void anchorX(float value) {
		anchorX = value;
	}

	@Override
	public void anchorY(float value) {
		anchorY = value;
	}
}
