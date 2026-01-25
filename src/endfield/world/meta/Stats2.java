package endfield.world.meta;

import mindustry.world.meta.Stat;
import mindustry.world.meta.StatCat;

public final class Stats2 {
	public static final Stat minSpeed = new Stat("min-speed");
	public static final Stat maxSpeed = new Stat("max-speed");
	public static final Stat sentryLifetime = new Stat("sentry-lifetime");
	public static final Stat fuel = new Stat("fuel", StatCat.crafting);
	public static final Stat recipes = new Stat("recipes", StatCat.crafting);
	public static final Stat producer = new Stat("producer", StatCat.crafting);
	public static final Stat produce = new Stat("produce", StatCat.crafting);
	public static final Stat baseHealChance = new Stat("base-heal-chance");
	public static final Stat itemsMovedBoost = new Stat("items-moved-boost", StatCat.optional);
	public static final Stat powerConsModifier = new Stat("power-cons-modifier", StatCat.function);
	public static final Stat minerBoosModifier = new Stat("miner-boost-modifier", StatCat.function);
	public static final Stat itemConvertList = new Stat("item-convert-list", StatCat.function);
	public static final Stat maxBoostPercent = new Stat("max-boost-percent", StatCat.function);
	public static final Stat damageReduction = new Stat("damage-reduction", StatCat.general);
	public static final Stat fieldStrength = new Stat("field-strength", StatCat.function);
	public static final Stat albedo = new Stat("albedo", StatCat.function);
	public static final Stat contents = new Stat("contents");
	public static final Stat healPercent = new Stat("heal-percent", StatCat.general);
	public static final Stat produceChance = new Stat("produce-chance", StatCat.crafting);
	//public static final Stat maxStructureSize = new Stat("max-structure-size");

	/** Don't let anyone instantiate this class. */
	private Stats2() {}
}
