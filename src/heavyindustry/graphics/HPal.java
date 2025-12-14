package heavyindustry.graphics;

import arc.graphics.Color;
import mindustry.graphics.Pal;

import static mindustry.Vars.content;

public final class HPal {
	public static final Color[] spectrum = {Color.red, Color.coral, Color.yellow, Color.lime, Color.green, Color.teal, Color.blue, Color.purple, Color.magenta};
	public static Color[] itemColors = {}, liquidColors = {};

	/** Static read-only palettes that are used throughout the mod. */
	public static final Color miku = new Color(0x39c5bbff);
	public static final Color carbideAmmoBack = new Color(0xab8ec5ff);
	public static final Color goldAmmoBack = new Color(0xf8df87ff);
	public static final Color crystalAmmoBack = new Color(0x7fd489ff);
	public static final Color crystalAmmoBright = new Color(0x96e6a0ff);
	public static final Color crystalAmmoDark = new Color(0x62ae7fff);
	public static final Color uraniumAmmoBack = new Color(0xa5b2c2ff);
	public static final Color uraniumAmmoFront = new Color(0xebf4ffff);
	public static final Color chromiumAmmoBack = new Color(0x8f94b3ff);
	public static final Color heavyAlloyAmmoFront = new Color(0x9b9daaff);
	public static final Color heavyAlloyAmmoBack = new Color(0x686b7bff);
	public static final Color orangeBack = new Color(0xff7f24ff);
	public static final Color regenerating = new Color(0x97ffa8ff);
	public static final Color surgeYellow = new Color(0xf3e979ff);
	public static final Color ancient = surgeYellow.cpy().lerp(Pal.accent, 0.115f);
	public static final Color ancientHeat = Color.red.cpy().mul(1.075f);
	public static final Color ancientLight = ancient.cpy().lerp(Color.white, 0.7f);
	public static final Color ancientLightMid = ancient.cpy().lerp(Color.white, 0.4f);
	public static final Color thurmixRed = new Color(0xff9492ff);
	public static final Color thurmixRedLight = new Color(0xffced0ff);
	public static final Color thurmixRedDark = thurmixRed.cpy().lerp(Color.black, 0.9f);
	public static final Color trail = Color.lightGray.cpy().lerp(Color.gray, 0.65f);
	public static final Color rainBowRed = new Color(0xff8787ff);
	public static final Color cold = new Color(0x6bc7ffff);
	public static final Color fexCrystal = new Color(0xff9584ff);
	public static final Color matrixNet = new Color(0xd3fdffff);
	public static final Color matrixNetDark = new Color(0x9ecbcdff);
	public static final Color ion = new Color(0xd1d19fff);
	public static final Color dew = new Color(0xff6214ff);
	public static final Color frost = new Color(0xaff7ffff);
	public static final Color winter = new Color(0x6ca5ffff);
	public static final Color monolithLighter = new Color(0x9cd4f8ff);
	public static final Color monolithLight = new Color(0xc0ecffff);
	public static final Color monolithMid = new Color(0x5379b7ff);
	public static final Color monolithDark = new Color(0x354d97ff);
	public static final Color monolithDarker = new Color(0x253080ff);
	public static final Color monolith = new Color(0x87ceebff);
	public static final Color monolithAtmosphere = new Color(0x001e6360);
	public static final Color coldcolor = new Color(0x6bc7ffff);
	public static final Color heat = new Color(1f, 0.22f, 0.22f, 0.8f);
	public static final Color strontiumLight = new Color(0xffb0b0ff);
	public static final Color strontiumDark = new Color(0xd97c7cff);
	public static final Color rubidium = new Color(0xd0bae6ff);
	public static final Color ferium = new Color(0xdededeff);
	public static final Color sisteelDark = new Color(0x7595d2ff);
	public static final Color sisteelLight = new Color(0xb9c0ebff);
	public static final Color clusRed = new Color(0xfe7777ff);
	public static final Color clusRedDark = new Color(0xff5845ff);

	public static final Color leadAmmoBack = new Color(0x8c7fa9ff);
	public static final Color leadAmmoFront = Color.white.cpy();
	public static final Color titaniumAmmoBack = new Color(0x8da1e3ff);
	public static final Color titaniumAmmoFront = Color.white.cpy();

	public static final Color discLight = new Color(0xffd59eff);
	public static final Color discDark = new Color(0xeec591ff);
	public static final Color tayrDark = new Color(0x25c9abff);
	public static final Color tayrLight = new Color(0xc4ffdeff);
	public static final Color chromiumDark = new Color(0x666484ff);
	public static final Color chromiumLight = new Color(0x929db5ff);
	public static final Color leipDark = new Color(0x5c5d79ff);
	public static final Color leipLight = new Color(0x9b9dCfff);
	public static final Color missileGray = new Color(0xe3e3e3ff);
	public static final Color missileYellow = new Color(0xffe176ff);
	public static final Color missileYellowBack = new Color(0xffb90fff);
	public static final Color smoke = new Color(0x737373ff);
	public static final Color energyYellow = new Color(0xfeebb3ff);
	public static final Color energySky = new Color(0xc0ecffff);
	public static final Color energyGreen = new Color(0xf2ff9cff);
	public static final Color enemyRedLight = new Color(0xff6464ff);
	public static final Color enemyRedDark = new Color(0xc93b3bff);
	public static final Color darkOutline = new Color(0x383848ff);

	public static final Color primary = new Color(0x9a75ffff);
	public static final Color blood = new Color(0.5f, 0.1f, 0.1f);
	public static final Color paleYellow = new Color(1f, 1f, 0.5f);
	public static final Color empathy = new Color(0xffcae9ff);
	public static final Color empathyAdd = new Color(0xff7dbcff);
	public static final Color empathyDark = new Color(0xff2e93ff);
	public static final Color red = new Color(0xf53036ff);
	public static final Color redLight = red.cpy().mul(2f);
	public static final Color darkRed = new Color(0.5f, 0f, 0f);
	public static final Color melt = new Color(0xffa20aff);

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
