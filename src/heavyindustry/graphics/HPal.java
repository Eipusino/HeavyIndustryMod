package heavyindustry.graphics;

import arc.graphics.Color;
import mindustry.graphics.Pal;

import static mindustry.Vars.content;

public final class HPal {
	public static final Color[] spectrum = {Color.red, Color.coral, Color.yellow, Color.lime, Color.green, Color.teal, Color.blue, Color.purple, Color.magenta};
	public static Color[] itemColors = {}, liquidColors = {};

	/** Static read-only palettes that are used throughout the mod. */
	public static final Color
			miku = Color.valueOf("39c5bb"),
			carbideShot = Color.valueOf("ab8ec5"),
			goldYellow = Color.valueOf("f8df87"),
			originiumRed = Color.valueOf("fa8267"),
			originiumRedBright = Color.valueOf("ffbb93"),
			originiumRedDark = Color.valueOf("d54040"),
			uraniumGrey = Color.valueOf("a5b2c2"),
			chromiumGrey = Color.valueOf("8f94b3"),
			brightSteelBlue = Color.valueOf("b0c4de"),
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
			trail = Color.lightGray.cpy().lerp(Color.gray, 0.65f),
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
			heat = new Color(1f, 0.22f, 0.22f, 0.8f),
			strontiumLight = Color.valueOf("FFB0B0"),
			strontiumDark = Color.valueOf("D97C7C"),
			rubidium = Color.valueOf("D0BAE6"),
			ferium = Color.valueOf("DEDEDE"),
			sisteelDark = Color.valueOf("7595D2"),
			sisteelLight = Color.valueOf("B9C0EB"),
			clusRed = Color.valueOf("FE7777"),
			clusRedDark = Color.valueOf("FF5845"),

			discLight = Color.valueOf("ffd59e"),
			discDark = Color.valueOf("eec591"),
			tayrDark = Color.valueOf("25c9ab"),
			tayrLight = Color.valueOf("c4ffde"),
			chromiumDark = Color.valueOf("666484"),
			chromiumLight = Color.valueOf("929db5"),
			leipDark = Color.valueOf("5c5d79ff"),
			leipLight = Color.valueOf("9b9dCf"),
			missileGray = Color.valueOf("e3e3e3"),
			missileYellow = Color.valueOf("ffe176"),
			smoke = Color.valueOf("737373"),
			energyYellow = Color.valueOf("feebb3"),
			energySky = Color.valueOf("c0ecff"),
			energyGreen = Color.valueOf("f2ff9c"),
			enemyRedLight = Color.valueOf("ff6464"),
			enemyRedDark = Color.valueOf("c93b3b"),
			darkOutline = Color.valueOf("383848")
	;

	/** Don't let anyone instantiate this class. */
	private HPal() {}

	public static void init() {
		int items = content.items().size;
		itemColors = new Color[items + 1];
		for (int i = 0; i < items; i++) {
			itemColors[i] = content.item(i).color;
		}
		itemColors[items] = content.items().first().color;

		int liquids = content.liquids().size;
		liquidColors = new Color[liquids + 1];
		for (int i = 0; i < liquids; i++) {
			liquidColors[i] = content.liquid(i).color;
		}
		liquidColors[liquids] = content.liquids().first().color;
	}
}
