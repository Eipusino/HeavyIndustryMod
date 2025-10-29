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
	public static final Interp upThenFastDown = a -> 1.0115f * (1.833f * (0.9991f * a - 1.1f) + 0.2f / (0.9991f * a - 1.1f) + 2.2f);
	public static final Interp artillery = a -> 1 - 2 * (a - 0.5f) * (a - 0.5f);
	public static final Interp artilleryPlus = a -> 3 * a - 3 * a * a + 0.25f;
	public static final Interp artilleryPlusReversed = a -> -3 * a + 3 * a * a + 1;
	public static final Interp zero = a -> 0;
	public static final Interp inOut = a -> 2 * (0.9f * a + 0.31f) + 1f / (5f * (a + 0.1f)) - 1.6f;
	public static final Interp inOut2 = a -> 1.6243f * (0.9f * a + 0.46f) + 1 / (10 * (a + 0.1f)) - 1.3f;
	public static final Interp parabola4 = a -> 4 * (a - 0.5f) * (a - 0.5f);
	public static final Interp parabola4Reversed = a -> -4 * (a - 0.5f) * (a - 0.5f) + 1;
	public static final Interp parabola4ReversedX4 = a -> (-4 * (a - 0.5f) * (a - 0.5f) + 1) * 2.75f;
	public static final Interp laser = a -> Interp.pow10Out.apply(a * 1.5f) * Mathf.curve(1 - a, 0, 0.085f);

	public static final BounceOut bounce5Out = new BounceOut(5);

	public static final BounceIn bounce5In = new BounceIn(5);

	public static final Pow pow10 = new Pow(10);

	public static final PowIn pow1r5In = new PowIn(1.5f), pow6In = new PowIn(6);

	public static final PowOut pow25Out = new PowOut(25);

	public static final MultiInterp fastFastSlow = new MultiInterp(Interp.pow2In, Interp.pow2);

	/// Don't let anyone instantiate this class.
	private HInterps() {}
}