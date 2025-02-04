package heavyindustry.math;

import arc.math.*;

public class MultiInterp implements Interp {
	public static Interp fastFastSlow = new MultiInterp(Interp.pow2In, Interp.pow2);

	Interp[] interps;

	public MultiInterp(Interp... ints) {
		interps = ints;
	}

	@Override
	public float apply(float value) {
		for (Interp interp : interps) {
			value = interp.apply(value);
		}

		return value;
	}
}
