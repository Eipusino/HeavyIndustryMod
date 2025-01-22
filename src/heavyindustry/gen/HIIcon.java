package heavyindustry.gen;

import arc.*;
import arc.scene.style.*;

import static heavyindustry.HIVars.*;

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
        keplerIcon = modDrawable("kepler-icon");
        aboutIcon = modDrawable("about-icon");
        artistIcon = modDrawable("artist-icon");
        configureIcon = modDrawable("configure-icon");
        contributeIcon = modDrawable("contribute-icon");
        databaseIcon = modDrawable("database-icon");
        debuggingIcon = modDrawable("debugging-icon");
        defaultShowIcon = modDrawable("default-show-icon");
        gasesIcon = modDrawable("gases-icon");
        holdIcon = modDrawable("hold-icon");
        matrixIcon = modDrawable("matrix-icon");
        nuclearIcon = modDrawable("nuclear-icon");
        programIcon = modDrawable("program-icon");
        publicInfoIcon = modDrawable("public-info-icon");
        reactionIcon = modDrawable("reaction-icon");
        showInfosIcon = modDrawable("show-infos-icon");
        showRangeIcon = modDrawable("show-range-icon");
        soundsIcon = modDrawable("sounds-icon");
        startIcon = modDrawable("start-icon");
        telegramIcon = modDrawable("telegram-icon");
        timeIcon = modDrawable("time-icon");
        translateIcon = modDrawable("translate-icon");
        unShowInfosIcon = modDrawable("un-show-infos-icon");
        //small
        resetIconSmall = modDrawable("reset-icon-small");
        timeIconSmall = modDrawable("time-icon-small");
        //animdustry
        alphaChan = modDrawable("alpha-chan");
        alphaChanHit = modDrawable("alpha-chan-hit");
        crawlerChan = modDrawable("crawler-chan");
        crawlerChanHit = modDrawable("crawler-chan-hit");
        zenithChan = modDrawable("zenith-chan");
        zenithChanHit = modDrawable("zenith-chan-hit");
        monoChan = modDrawable("mono-chan");
        monoChanHit = modDrawable("mono-chan-hit");
        oxynoeChan = modDrawable("oxynoe-chan");
        oxynoeChanHit = modDrawable("oxynoe-chan-hit");
        octChan = modDrawable("oct-chan");
        octChanHit = modDrawable("oct-chan-hit");
        seiChan = modDrawable("sei-chan");
        seiChanHit = modDrawable("sei-chan-hit");
        quadChan = modDrawable("quad-chan");
        quadChanHit = modDrawable("quad-chan-hit");
        boulderChan = modDrawable("boulder-chan");
        boulderChanHit = modDrawable("boulder-chan-hit");
    }

    public static <T extends Drawable> T getModDrawable(String name) {
        return Core.atlas.getDrawable(modName + "-" + name);
    }

    public static Drawable modDrawable(String name) {
        return Core.atlas.drawable(modName + "-" + name);
    }

    public static TextureRegionDrawable regionDrawable(String name) {
        return new TextureRegionDrawable(Core.atlas.find(name));
    }

    public static TextureRegionDrawable modRegionDrawable(String name) {
        return regionDrawable(modName + "-" + name);
    }
}
