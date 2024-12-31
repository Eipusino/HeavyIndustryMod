package heavyindustry.world.meta;

import mindustry.world.meta.*;

public final class HIStat {
    public static final Stat
            minSpeed = new Stat("hi-min-speed"),
            maxSpeed = new Stat("hi-max-speed"),
            sentryLifetime = new Stat("hi-sentry-lifetime"),
            fuel = new Stat("hi-fuel", StatCat.crafting),
            baseHealChance = new Stat("hi-base-heal-chance"),
            itemsMovedBoost = new Stat("hi-items-moved-boost", StatCat.optional),
            powerConsModifier = new Stat("hi-power-cons-modifier", StatCat.function),
            minerBoosModifier = new Stat("hi-miner-boost-modifier", StatCat.function),
            itemConvertList = new Stat("hi-item-convert-list", StatCat.function),
            maxBoostPercent = new Stat("hi-max-boost-percent", StatCat.function),
            damageReduction = new Stat("hi-damage-reduction", StatCat.general),
            fieldStrength = new Stat("hi-field-strength", StatCat.function),
            albedo = new Stat("hi-albedo", StatCat.function);

    /** HIStat should not be instantiated. */
    private HIStat() {}
}
