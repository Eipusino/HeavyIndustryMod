package heavyindustry.math;

import arc.math.Interp;
import arc.math.Interp.BounceIn;
import arc.math.Interp.BounceOut;
import arc.math.Interp.Pow;
import arc.math.Interp.PowIn;
import arc.math.Interp.PowOut;
import arc.math.Mathf;

/** @since 1.0.1 */
public final class HInterps {
	public static final Interp
			upThenFastDown = x -> 1.0115f * (1.833f * (0.9991f * x - 1.1f) + 0.2f / (0.9991f * x - 1.1f) + 2.2f),
			artillery = x -> 1 - 2 * (x - 0.5f) * (x - 0.5f),
			artilleryPlus = x -> 3 * x - 3 * x * x + 0.25f,
			artilleryPlusReversed = x -> -3 * x + 3 * x * x + 1,
			zero = a -> 0,
			inOut = a -> 2 * (0.9f * a + 0.31f) + 1f / (5f * (a + 0.1f)) - 1.6f,
			inOut2 = x -> 1.6243f * (0.9f * x + 0.46f) + 1 / (10 * (x + 0.1f)) - 1.3f,
			parabola4 = x -> 4 * (x - 0.5f) * (x - 0.5f),
			parabola4Reversed = x -> -4 * (x - 0.5f) * (x - 0.5f) + 1,
			parabola4ReversedX4 = x -> (-4 * (x - 0.5f) * (x - 0.5f) + 1) * 2.75f,
			laser = x -> Interp.pow10Out.apply(x * 1.5f) * Mathf.curve(1 - x, 0, 0.085f);

	public static final BounceOut bounce5Out = new BounceOut(5);

	public static final BounceIn bounce5In = new BounceIn(5);

	public static final Pow pow10 = new Pow(10);

	public static final PowIn pow1r5In = new PowIn(1.5f), pow6In = new PowIn(6);

	public static final PowOut pow25Out = new PowOut(25);

	public static final MultiInterp fastFastSlow = new MultiInterp(Interp.pow2In, Interp.pow2);

	/** Don't let anyone instantiate this class. */
	private HInterps() {}
}