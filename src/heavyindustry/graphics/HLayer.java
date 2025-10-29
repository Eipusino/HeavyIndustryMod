package heavyindustry.graphics;

import mindustry.graphics.Layer;

public final class HLayer {
	public static final float effectBottom = Layer.effect + 0.0001f;
	public static final float effectMask = Layer.bullet - 0.11f;
	//Layer the black hole renderer's frame buffer starts on
	public static final float begin = Layer.background - 0.1f;
	//Layer the black hole renderer's frame buffer ends
	public static final float end = Layer.max - 5;
	//Secondary bloom ranges from -1.02 to +1.02 around this
	public static final float skyBloom = 145;
	public static final float mirrorField = 135f;

	/// Don't let anyone instantiate this class.
	private HLayer() {}
}
