package heavyindustry.math;

import arc.math.Interp;

public class MultiInterp implements Interp {
	final Interp[] interps;

	public MultiInterp(Interp... interp) {
		interps = interp;
	}

	@Override
	public float apply(float value) {
		for (Interp interp : interps) {
			value = interp.apply(value);
		}

		return value;
	}
}
