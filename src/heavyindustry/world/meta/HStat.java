package heavyindustry.world.meta;

import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;

public final class HStat {
	public static final Stat minSpeed = new Stat("hi-min-speed");
	public static final Stat maxSpeed = new Stat("hi-max-speed");
	public static final Stat sentryLifetime = new Stat("hi-sentry-lifetime");
	public static final Stat fuel = new Stat("hi-fuel", StatCat.crafting);
	public static final Stat recipes = new Stat("hi-recipes", StatCat.crafting);
	public static final Stat producer = new Stat("hi-producer", StatCat.crafting);
	public static final Stat produce = new Stat("hi-produce", StatCat.crafting);
	public static final Stat baseHealChance = new Stat("hi-base-heal-chance");
	public static final Stat itemsMovedBoost = new Stat("hi-items-moved-boost", StatCat.optional);
	public static final Stat powerConsModifier = new Stat("hi-power-cons-modifier", StatCat.function);
	public static final Stat minerBoosModifier = new Stat("hi-miner-boost-modifier", StatCat.function);
	public static final Stat itemConvertList = new Stat("hi-item-convert-list", StatCat.function);
	public static final Stat maxBoostPercent = new Stat("hi-max-boost-percent", StatCat.function);
	public static final Stat damageReduction = new Stat("hi-damage-reduction", StatCat.general);
	public static final Stat fieldStrength = new Stat("hi-field-strength", StatCat.function);
	public static final Stat albedo = new Stat("hi-albedo", StatCat.function);
	public static final Stat contents = new Stat("hi-contents");
	public static final Stat healPercent = new Stat("hi-heal-percent", StatCat.general);
	public static final Stat produceChance = new Stat("hi-produce-chance", StatCat.crafting);
	//public static final Stat maxStructureSize = new Stat("hi-max-structure-size");

	/// Don't let anyone instantiate this class.
	private HStat() {}
}
