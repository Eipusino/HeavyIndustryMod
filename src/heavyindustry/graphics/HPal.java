package heavyindustry.graphics;

import arc.graphics.Color;
import mindustry.graphics.Pal;

import static mindustry.Vars.content;

public final class HPal {
	public static final Color[] spectrum = {Color.red, Color.coral, Color.yellow, Color.lime, Color.green, Color.teal, Color.blue, Color.purple, Color.magenta};
	public static Color[] itemColors = {}, liquidColors = {};

	/** Static read-only palettes that are used throughout the mod. */
	public static final Color
			miku = new Color(0x39c5bbff),
			carbideAmmoBack = new Color(0xab8ec5ff),
			goldAmmoBack = new Color(0xf8df87ff),
			crystalAmmoBack = new Color(0x7fd489ff),
			crystalAmmoBright = new Color(0x96e6a0ff),
			crystalAmmoDark = new Color(0x62ae7fff),
			uraniumAmmoBack = new Color(0xa5b2c2ff),
			uraniumAmmoFront = new Color(0xebf4ffff),
			chromiumAmmoBack = new Color(0x8f94b3ff),
			heavyAlloyAmmoFront = new Color(0x9b9daaff),
			heavyAlloyAmmoBack = new Color(0x686b7bff),
			orangeBack = new Color(0xff7f24ff),
			regenerating = new Color(0x97ffa8ff),
			surgeYellow = new Color(0xf3e979ff),
			ancient = surgeYellow.cpy().lerp(Pal.accent, 0.115f),
			ancientHeat = Color.red.cpy().mul(1.075f),
			ancientLight = ancient.cpy().lerp(Color.white, 0.7f),
			ancientLightMid = ancient.cpy().lerp(Color.white, 0.4f),
			thurmixRed = new Color(0xff9492ff),
			thurmixRedLight = new Color(0xffced0ff),
			thurmixRedDark = thurmixRed.cpy().lerp(Color.black, 0.9f),
			trail = Color.lightGray.cpy().lerp(Color.gray, 0.65f),
			rainBowRed = new Color(0xff8787ff),
			cold = new Color(0x6bc7ffff),
			fexCrystal = new Color(0xff9584ff),
			matrixNet = new Color(0xd3fdffff),
			matrixNetDark = new Color(0x9ecbcdff),
			ion = new Color(0xd1d19fff),
			dew = new Color(0xff6214ff),
			frost = new Color(0xaff7ffff),
			winter = new Color(0x6ca5ffff),
			monolithLighter = new Color(0x9cd4f8ff),
			monolithLight = new Color(0xc0ecffff),
			monolithMid = new Color(0x5379b7ff),
			monolithDark = new Color(0x354d97ff),
			monolithDarker = new Color(0x253080ff),
			monolith = new Color(0x87ceebff),
			monolithAtmosphere = new Color(0x001e6360),
			coldcolor = new Color(0x6bc7ffff),
			heat = new Color(1f, 0.22f, 0.22f, 0.8f),
			strontiumLight = new Color(0xffb0b0ff),
			strontiumDark = new Color(0xd97c7cff),
			rubidium = new Color(0xd0bae6ff),
			ferium = new Color(0xdededeff),
			sisteelDark = new Color(0x7595d2ff),
			sisteelLight = new Color(0xb9c0ebff),
			clusRed = new Color(0xfe7777ff),
			clusRedDark = new Color(0xff5845ff),

			leadAmmoBack = new Color(0x8c7fa9ff),
			leadAmmoFront = Color.white.cpy(),
			titaniumAmmoBack = new Color(0x8da1e3ff),
			titaniumAmmoFront = Color.white.cpy(),

			discLight = new Color(0xffd59eff),
			discDark = new Color(0xeec591ff),
			tayrDark = new Color(0x25c9abff),
			tayrLight = new Color(0xc4ffdeff),
			chromiumDark = new Color(0x666484ff),
			chromiumLight = new Color(0x929db5ff),
			leipDark = new Color(0x5c5d79ff),
			leipLight = new Color(0x9b9dCfff),
			missileGray = new Color(0xe3e3e3ff),
			missileYellow = new Color(0xffe176ff),
			missileYellowBack = new Color(0xffb90fff),
			smoke = new Color(0x737373ff),
			energyYellow = new Color(0xfeebb3ff),
			energySky = new Color(0xc0ecffff),
			energyGreen = new Color(0xf2ff9cff),
			enemyRedLight = new Color(0xff6464ff),
			enemyRedDark = new Color(0xc93b3bff),
			darkOutline = new Color(0x383848ff),

			primary = new Color(0x9a75ffff),
			blood = new Color(0.5f, 0.1f, 0.1f),
			paleYellow = new Color(1f, 1f, 0.5f),
			empathy = new Color(0xffcae9ff),
			empathyAdd = new Color(0xff7dbcff),
			empathyDark = new Color(0xff2e93ff),
			red = new Color(0xf53036ff),
			redLight = red.cpy().mul(2f),
			darkRed = new Color(0.5f, 0f, 0f),
			melt = new Color(0xffa20aff);

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
