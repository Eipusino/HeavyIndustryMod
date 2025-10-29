package heavyindustry.math;

import arc.math.Interp;

public class MultiInterp implements Interp {
	final Interp[] interps;

	public MultiInterp(Interp... interp) {
		interps = interp;
	}

	@Override
	public float apply(float a) {
		for (Interp interp : interps) {
			a = interp.apply(a);
		}

		return a;
	}
}
