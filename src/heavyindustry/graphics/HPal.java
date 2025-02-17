package heavyindustry.graphics;

import arc.graphics.*;
import mindustry.graphics.*;

public final class HPal {
	public static final Color[] spectrum = {Color.red, Color.coral, Color.yellow, Color.lime, Color.green, Color.teal, Color.blue, Color.purple, Color.magenta};

	/** Static read-only palettes that are used throughout the mod. */
	public static final Color
			miku = Color.valueOf("39c5bb"),
			carbideShot = Color.valueOf("ab8ec5"),
			nanoCoreRed = Color.valueOf("fa8267"),
			nanoCoreRedBright = Color.valueOf("ffbb93"),
			nanoCoreRedDark = Color.valueOf("d54040"),
			originiumBlack = Color.valueOf("3a2616"),
			activatedOriginiumBlack = Color.valueOf("c32900"),
			uraniumGrey = Color.valueOf("a5b2c2"),
			chromiumGrey = Color.valueOf("8f94b3"),
			brightSteelBlue = Color.valueOf("b0c4de"),
			lightGrey = Color.valueOf("e3e3e3"),
			darkGrey = Color.valueOf("737373"),
			lightYellow = Color.valueOf("ffe176"),
			canaryYellow = Color.valueOf("feebb3"),
			orangeBack = Color.valueOf("ff7f24"),
			regenerating = Color.valueOf("97ffa8"),
			surgeYellow = Color.valueOf("f3e979"),
			ancient = surgeYellow.cpy().lerp(Pal.accent, 0.115f),
			ancientHeat = Color.red.cpy().mul(1.075f),
			ancientLight = ancient.cpy().lerp(Color.white, 0.7f),
			ancientLightMid = ancient.cpy().lerp(Color.white, 0.4f),
			thurmixRed = Color.valueOf("#ff9492"),
			thurmixRedLight = Color.valueOf("#ffced0"),
			thurmixRedDark = thurmixRed.cpy().lerp(Color.black, 0.9f),
			rainBowRed = Color.valueOf("ff8787"),
			cold = Color.valueOf("6bc7ff"),
			fexCrystal = Color.valueOf("ff9584"),
			matrixNet = Color.valueOf("d3fdff"),
			matrixNetDark = Color.valueOf("9ecbcd"),
			ion = Color.valueOf("d1d19f"),
			dew = Color.valueOf("ff6214"),
			frost = Color.valueOf("aff7ff"),
			winter = Color.valueOf("6ca5ff"),
			monolithLight = Color.valueOf("c0ecff"),
			monolith = Color.valueOf("87ceeb"),
			monolithDark = Color.valueOf("6586b0"),
			monolithAtmosphere = Color.valueOf("001e6360"),
			coldcolor = Color.valueOf("6bc7ff"),
			heat = new Color(1f, 0.22f, 0.22f, 0.8f);

	/** Don't let anyone instantiate this class. */
	private HPal() {}
}
