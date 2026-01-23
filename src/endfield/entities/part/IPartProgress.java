package endfield.entities.part;

import arc.math.Interp;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.part.DrawPart.PartFunc;
import mindustry.entities.part.DrawPart.PartParams;
import mindustry.entities.part.DrawPart.PartProgress;

@FunctionalInterface
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
		return (IPartProgress) p -> 1f - get(p);
	}

	@Override
	default PartProgress slope() {
		return (IPartProgress) p -> Mathf.slope(get(p));
	}

	@Override
	default PartProgress clamp() {
		return (IPartProgress) p -> Mathf.clamp(get(p));
	}

	@Override
	default PartProgress add(float amount) {
		return (IPartProgress) p -> get(p) + amount;
	}

	@Override
	default PartProgress add(PartProgress other) {
		return (IPartProgress) p -> get(p) + other.get(p);
	}

	@Override
	default PartProgress delay(float amount) {
		return (IPartProgress) p -> (get(p) - amount) / (1f - amount);
	}

	@Override
	default PartProgress curve(float offset, float duration) {
		return (IPartProgress) p -> (get(p) - offset) / duration;
	}

	@Override
	default PartProgress sustain(float offset, float grow, float sustain) {
		return (IPartProgress) p -> {
			float val = get(p) - offset;
			return Math.min(Math.max(val, 0f) / grow, (grow + sustain + grow - val) / grow);
		};
	}

	@Override
	default PartProgress shorten(float amount) {
		return (IPartProgress) p -> get(p) / (1f - amount);
	}

	@Override
	default PartProgress compress(float start, float end) {
		return (IPartProgress) p -> Mathf.curve(get(p), start, end);
	}

	@Override
	default PartProgress blend(PartProgress other, float amount) {
		return (IPartProgress) p -> Mathf.lerp(get(p), other.get(p), amount);
	}

	@Override
	default PartProgress mul(PartProgress other) {
		return (IPartProgress) p -> get(p) * other.get(p);
	}

	@Override
	default PartProgress mul(float amount) {
		return (IPartProgress) p -> get(p) * amount;
	}

	@Override
	default PartProgress min(PartProgress other) {
		return (IPartProgress) p -> Math.min(get(p), other.get(p));
	}

	@Override
	default PartProgress sin(float offset, float scl, float mag) {
		return (IPartProgress) p -> get(p) + Mathf.sin(Time.time + offset, scl, mag);
	}

	@Override
	default PartProgress sin(float scl, float mag) {
		return (IPartProgress) p -> get(p) + Mathf.sin(scl, mag);
	}

	@Override
	default PartProgress absin(float scl, float mag) {
		return (IPartProgress) p -> get(p) + Mathf.absin(scl, mag);
	}

	@Override
	default PartProgress mod(float amount) {
		return (IPartProgress) p -> Mathf.mod(get(p), amount);
	}

	@Override
	default PartProgress loop(float time) {
		return (IPartProgress) p -> Mathf.mod(get(p) / time, 1);
	}

	@Override
	default PartProgress apply(PartProgress other, PartFunc func) {
		return (IPartProgress) p -> func.get(get(p), other.get(p));
	}

	@Override
	default PartProgress curve(Interp interp) {
		return (IPartProgress) p -> interp.apply(get(p));
	}
}
