package heavyindustry.math;

import arc.math.Interp;

public abstract class AbstractInterp implements Interp {
	@Override
	public float apply(float start, float end, float a) {
		return start + (end - start) * apply(a);
	}
}
