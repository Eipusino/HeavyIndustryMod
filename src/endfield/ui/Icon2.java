package endfield.ui;

import arc.Core;
import arc.scene.style.TextureRegionDrawable;
import org.jetbrains.annotations.ApiStatus.Internal;

import static endfield.Vars2.MOD_PREFIX;

public final class Icon2 {
	public static TextureRegionDrawable keplerIcon, aboutIcon, artistIcon, configureIcon, contributeIcon, databaseIcon, debuggingIcon, defaultShowIcon, fullSwordIcon, gasesIcon;
	public static TextureRegionDrawable holdIcon, matrixIcon, nuclearIcon, programIcon, publicInfoIcon, reactionIcon, showInfosIcon, showRangeIcon, musicsIcon;
	public static TextureRegionDrawable soundsIcon, sounds2Icon, startIcon, telegramIcon, timeIcon, translateIcon, unShowInfosIcon;
	public static TextureRegionDrawable javaIcon, javaScriptIcon, networkErrorIcon, slotsBackIcon;
	//small
	public static TextureRegionDrawable resetIconSmall, timeIconSmall;
	//animdustry
	public static TextureRegionDrawable alphaChan, alphaChanHit, crawlerChan, crawlerChanHit, zenithChan, zenithChanHit, monoChan, monoChanHit, oxynoeChan, oxynoeChanHit, octChan, octChanHit;
	public static TextureRegionDrawable seiChan, seiChanHit, quadChan, quadChanHit, boulderChan, boulderChanHit;
	//other
	public static TextureRegionDrawable matrixArrow;

	/** Don't let anyone instantiate this class. */
	private Icon2() {}

	@Internal
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
		slotsBackIcon = texture("slots-back-icon");
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
		//other
		matrixArrow = texture("matrix-arrow");
	}

	public static TextureRegionDrawable texture(String name) {
		return new TextureRegionDrawable(Core.atlas.find(MOD_PREFIX + name));
	}
}
