package heavyindustry.gen;

import arc.scene.style.*;

import static arc.Core.*;
import static heavyindustry.core.HeavyIndustryMod.*;

public final class HIIcon {
    public static Drawable
            keplerIcon, aboutIcon, artistIcon, configureIcon, contributeIcon, databaseIcon, debuggingIcon, defaultShowIcon, gasesIcon,
            holdIcon, matrixIcon, nuclearIcon, programIcon, publicInfoIcon, reactionIcon, showInfosIcon, showRangeIcon, soundsIcon,
            startIcon, telegramIcon, timeIcon, translateIcon, unShowInfosIcon,
            //small
            resetIconSmall, timeIconSmall,
            //animdustry
            alphaChan, alphaChanHit, crawlerChan, crawlerChanHit, zenithChan, zenithChanHit, monoChan, monoChanHit, oxynoeChan, oxynoeChanHit, octChan, octChanHit,
            seiChan, seiChanHit, quadChan, quadChanHit, boulderChan, boulderChanHit;

    /** Don't let anyone instantiate this class. */
    private HIIcon() {}

    public static void load() {
        keplerIcon = getModDrawable("kepler-icon");
        aboutIcon = getModDrawable("about-icon");
        artistIcon = getModDrawable("artist-icon");
        configureIcon = getModDrawable("configure-icon");
        contributeIcon = getModDrawable("contribute-icon");
        databaseIcon = getModDrawable("database-icon");
        debuggingIcon = getModDrawable("debugging-icon");
        defaultShowIcon = getModDrawable("default-show-icon");
        gasesIcon = getModDrawable("gases-icon");
        holdIcon = getModDrawable("hold-icon");
        matrixIcon = getModDrawable("matrix-icon");
        nuclearIcon = getModDrawable("nuclear-icon");
        programIcon = getModDrawable("program-icon");
        publicInfoIcon = getModDrawable("public-info-icon");
        reactionIcon = getModDrawable("reaction-icon");
        showInfosIcon = getModDrawable("show-infos-icon");
        showRangeIcon = getModDrawable("show-range-icon");
        soundsIcon = getModDrawable("sounds-icon");
        startIcon = getModDrawable("start-icon");
        telegramIcon = getModDrawable("telegram-icon");
        timeIcon = getModDrawable("time-icon");
        translateIcon = getModDrawable("translate-icon");
        unShowInfosIcon = getModDrawable("un-show-infos-icon");
        //small
        resetIconSmall = getModDrawable("reset-icon-small");
        timeIconSmall = getModDrawable("time-icon-small");
        //animdustry
        alphaChan = getModDrawable("alpha-chan");
        alphaChanHit = getModDrawable("alpha-chan-hit");
        crawlerChan = getModDrawable("crawler-chan");
        crawlerChanHit = getModDrawable("crawler-chan-hit");
        zenithChan = getModDrawable("zenith-chan");
        zenithChanHit = getModDrawable("zenith-chan-hit");
        monoChan = getModDrawable("mono-chan");
        monoChanHit = getModDrawable("mono-chan-hit");
        oxynoeChan = getModDrawable("oxynoe-chan");
        oxynoeChanHit = getModDrawable("oxynoe-chan-hit");
        octChan = getModDrawable("oct-chan");
        octChanHit = getModDrawable("oct-chan-hit");
        seiChan = getModDrawable("sei-chan");
        seiChanHit = getModDrawable("sei-chan-hit");
        quadChan = getModDrawable("quad-chan");
        quadChanHit = getModDrawable("quad-chan-hit");
        boulderChan = getModDrawable("boulder-chan");
        boulderChanHit = getModDrawable("boulder-chan-hit");
    }

    public static <T extends Drawable> T getModDrawable(String name) {
        return atlas.getDrawable(modName + "-" + name);
    }
}
