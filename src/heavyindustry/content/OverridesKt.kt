package heavyindustry.content

import arc.struct.*
import heavyindustry.util.*
import mindustry.*
import mindustry.content.*
import mindustry.type.*
import mindustry.world.blocks.power.ConsumeGenerator
import mindustry.world.blocks.production.*

object OverridesKt {
	@JvmField val drillMultipliers = ObjectFloatMap<Item>()
	@JvmField val drillMultipliers_e = ObjectFloatMap<Item>()

	@JvmField val itemDurationMultipliers = ObjectFloatMap<Item>()

	@JvmStatic
	fun load() {
		itemDurationMultipliers.put(Items.phaseFabric, 15f)
		itemDurationMultipliers.put(Itemsf.uranium, 1.2f)
		itemDurationMultipliers.put(Itemsf.originium, 0.8f)
		itemDurationMultipliers.put(Itemsf.purifiedOriginium, 2.5f)

		for (i in Vars.content.items()) {
			drillMultipliers.put(i, itemHard(i))
		}
		//I am doing this to consider the compatibility of other mods.
		drillMultipliers_e.putAll(drillMultipliers)
		drillMultipliers_e.put(Items.thorium, 1f)
		drillMultipliers_e.put(Itemsf.uranium, 0.9f)
		drillMultipliers_e.put(Itemsf.chromium, 0.8f)

		(Blocks.rtgGenerator as ConsumeGenerator).itemDurationMultipliers.putAll(itemDurationMultipliers)

		(Blocks.impactDrill as BurstDrill).drillMultipliers.putAll(drillMultipliers_e)
		(Blocks.eruptionDrill as BurstDrill).drillMultipliers.putAll(drillMultipliers_e)
	}

	@JvmStatic
	fun itemHard(item: Item, bre: Item): Float = eq(item === bre, 1f, itemHard(item))

	@JvmStatic
	fun itemHard(item: Item, bres: Seq<Item>): Float = eq(!bres.contains(item), 1f, itemHard(item))

	@JvmStatic
	fun itemHard(item: Item): Float = when (item.hardness) {
		1 -> 3.5f
		2 -> 3f
		3 -> 2.5f
		4 -> 2f
		5 -> 1.5f
		6 -> 1f
		7 -> 0.75f
		8 -> 0.5f
		else -> 0.25f
	}
}