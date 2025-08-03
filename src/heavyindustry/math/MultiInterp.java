package heavyindustry.math;

import arc.math.Interp;

public class MultiInterp implements Interp {
	public static Interp fastFastSlow = new MultiInterp(Interp.pow2In, Interp.pow2);

	public final Interp[] interps;

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
