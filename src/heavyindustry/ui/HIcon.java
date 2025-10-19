package heavyindustry.ui;

import arc.Core;
import arc.graphics.g2d.NinePatch;
import arc.graphics.g2d.TextureAtlas.AtlasRegion;
import arc.scene.style.Drawable;
import arc.scene.style.ScaledNinePatchDrawable;
import arc.scene.style.TextureRegionDrawable;

import static heavyindustry.HVars.MOD_PREFIX;

public final class HIcon {
	public static TextureRegionDrawable
			keplerIcon, aboutIcon, artistIcon, configureIcon, contributeIcon, databaseIcon, debuggingIcon, defaultShowIcon, fullSwordIcon, gasesIcon,
			holdIcon, matrixIcon, nuclearIcon, programIcon, publicInfoIcon, reactionIcon, showInfosIcon, showRangeIcon, musicsIcon,
			soundsIcon, sounds2Icon, startIcon, telegramIcon, timeIcon, translateIcon, unShowInfosIcon,
			javaIcon, javaScriptIcon, networkErrorIcon,
			//small
			resetIconSmall, timeIconSmall,
			//
			//animdustry
			alphaChan, alphaChanHit, crawlerChan, crawlerChanHit, zenithChan, zenithChanHit, monoChan, monoChanHit, oxynoeChan, oxynoeChanHit, octChan, octChanHit,
			seiChan, seiChanHit, quadChan, quadChanHit, boulderChan, boulderChanHit;

	/** Don't let anyone instantiate this class. */
	private HIcon() {}

	public static void load() {
		keplerIcon = texture("kepler-icon");
		aboutIcon = texture("about-icon");
		artistIcon = texture("artist-icon");
		configureIcon = texture("configure-icon");
		contributeIcon = texture("contribute-icon");
		databaseIcon = texture("database-icon");
		debuggingIcon = texture("debugging-icon");
		defaultShowIcon = texture("default-show-icon");
		fullSwordIcon = texture("full-sword-icon");
		gasesIcon = texture("gases-icon");
		holdIcon = texture("hold-icon");
		matrixIcon = texture("matrix-icon");
		nuclearIcon = texture("nuclear-icon");
		programIcon = texture("program-icon");
		publicInfoIcon = texture("public-info-icon");
		reactionIcon = texture("reaction-icon");
		showInfosIcon = texture("show-infos-icon");
		showRangeIcon = texture("show-range-icon");
		musicsIcon = texture("musics-icon");
		soundsIcon = texture("sounds-icon");
		sounds2Icon = texture("sounds-2-icon");
		startIcon = texture("start-icon");
		telegramIcon = texture("telegram-icon");
		timeIcon = texture("time-icon");
		translateIcon = texture("translate-icon");
		unShowInfosIcon = texture("un-show-infos-icon");
		javaIcon = texture("java-icon");
		javaScriptIcon = texture("java-script-icon");
		networkErrorIcon = texture("network-error-icon");
		//small
		resetIconSmall = texture("reset-icon-small");
		timeIconSmall = texture("time-icon-small");
		//animdustry
		alphaChan = texture("alpha-chan");
		alphaChanHit = texture("alpha-chan-hit");
		crawlerChan = texture("crawler-chan");
		crawlerChanHit = texture("crawler-chan-hit");
		zenithChan = texture("zenith-chan");
		zenithChanHit = texture("zenith-chan-hit");
		monoChan = texture("mono-chan");
		monoChanHit = texture("mono-chan-hit");
		oxynoeChan = texture("oxynoe-chan");
		oxynoeChanHit = texture("oxynoe-chan-hit");
		octChan = texture("oct-chan");
		octChanHit = texture("oct-chan-hit");
		seiChan = texture("sei-chan");
		seiChanHit = texture("sei-chan-hit");
		quadChan = texture("quad-chan");
		quadChanHit = texture("quad-chan-hit");
		boulderChan = texture("boulder-chan");
		boulderChanHit = texture("boulder-chan-hit");
	}

	public static Drawable drawable(String name) {
		AtlasRegion region = Core.atlas.find(MOD_PREFIX + name);

		if (region.splits != null) {
			int[] splits = region.splits;
			NinePatch patch = new NinePatch(region, splits[0], splits[1], splits[2], splits[3]);
			int[] pads = region.pads;
			if (pads != null) patch.setPadding(pads[0], pads[1], pads[2], pads[3]);
			return new ScaledNinePatchDrawable(patch);
		} else {
			return new TextureRegionDrawable(region);
		}
	}

	public static TextureRegionDrawable texture(String name) {
		return new TextureRegionDrawable(Core.atlas.find(MOD_PREFIX + name));
	}
}
