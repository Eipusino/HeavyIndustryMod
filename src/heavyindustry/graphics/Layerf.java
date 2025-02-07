package heavyindustry.graphics;

import mindustry.graphics.*;

public final class Layerf {
	public static final float
			effectBottom = Layer.effect + 0.0001f,
			effectMask = Layer.bullet - 0.11f,
			//Layer the black hole renderer's frame buffer starts on
			begin = Layer.background - 0.1f,
			//Layer the black hole renderer's frame buffer ends
			end = Layer.max - 5,
			//Secondary bloom ranges from -1.02 to +1.02 around this
			skyBloom = 145;

	/** Don't let anyone instantiate this class. */
	private Layerf() {}
}
