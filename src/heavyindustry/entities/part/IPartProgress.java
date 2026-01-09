package heavyindustry.entities.part;

import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.part.DrawPart.PartFunc;
import mindustry.entities.part.DrawPart.PartParams;
import mindustry.entities.part.DrawPart.PartProgress;

public interface IPartProgress extends PartProgress {
	@Override
	default float getClamp(PartParams p) {
		return getClamp(p, true);
	}

	@Override
	default float getClamp(PartParams p, boolean clamp) {
		return clamp ? Mathf.clamp(get(p)) : get(p);
	}

	@Override
	default PartProgress inv() {
		return p -> 1f - get(p);
	}

	@Override
	default PartProgress slope() {
		return p -> Mathf.slope(get(p));
	}

	@Override
	default PartProgress clamp() {
		return p -> Mathf.clamp(get(p));
	}

	@Override
	default PartProgress add(float amount) {
		return p -> get(p) + amount;
	}

	@Override
	default PartProgress add(PartProgress other) {
		return p -> get(p) + other.get(p);
	}

	@Override
	default PartProgress delay(float amount) {
		return p -> (get(p) - amount) / (1f - amount);
	}

	@Override
	default PartProgress curve(float offset, float duration) {
		return p -> (get(p) - offset) / duration;
	}

	@Override
	default PartProgress sustain(float offset, float grow, float sustain) {
		return p -> {
			float val = get(p) - offset;
			return Math.min(Math.max(val, 0f) / grow, (grow + sustain + grow - val) / grow);
		};
	}

	@Override
	default PartProgress shorten(float amount) {
		return p -> get(p) / (1f - amount);
	}

	@Override
	default PartProgress compress(float start, float end) {
		return p -> Mathf.curve(get(p), start, end);
	}

	@Override
	default PartProgress blend(PartProgress other, float amount) {
		return p -> Mathf.lerp(get(p), other.get(p), amount);
	}

	@Override
	default PartProgress mul(PartProgress other) {
		return p -> get(p) * other.get(p);
	}

	@Override
	default PartProgress mul(float amount) {
		return p -> get(p) * amount;
	}

	@Override
	default PartProgress min(PartProgress other) {
		return p -> Math.min(get(p), other.get(p));
	}

	@Override
	default PartProgress sin(float offset, float scl, float mag) {
		return p -> get(p) + Mathf.sin(Time.time + offset, scl, mag);
	}

	@Override
	default PartProgress sin(float scl, float mag) {
		return p -> get(p) + Mathf.sin(scl, mag);
	}

	@Override
	default PartProgress absin(float scl, float mag) {
		return p -> get(p) + Mathf.absin(scl, mag);
	}

	@Override
	default PartProgress mod(float amount) {
		return p -> Mathf.mod(get(p), amount);
	}

	@Override
	default PartProgress loop(float time) {
		return p -> Mathf.mod(get(p) / time, 1);
	}

	@Override
	default PartProgress apply(PartProgress other, PartFunc func) {
		return p -> func.get(get(p), other.get(p));
	}

	@Override
	default PartProgress curve(Interp interp) {
		return p -> interp.apply(get(p));
	}
}
