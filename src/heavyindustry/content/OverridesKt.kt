package heavyindustry.content

import arc.struct.*
import heavyindustry.util.*
import mindustry.*
import mindustry.content.*
import mindustry.type.*
import mindustry.world.blocks.production.*

object OverridesKt {
	@JvmField val drillMultipliers = ObjectFloatMap<Item>()
	@JvmField val erekirDrillMultipliers = ObjectFloatMap<Item>()

	@JvmStatic
	fun load() {
		for (i in Vars.content.items()) {
			drillMultipliers.put(i, itemHard(i))
		}
		//I am doing this to consider the compatibility of other mods.
		erekirDrillMultipliers.putAll(drillMultipliers)
		erekirDrillMultipliers.put(Items.thorium, 1f)
		erekirDrillMultipliers.put(Itemsf.uranium, 0.9f)
		erekirDrillMultipliers.put(Itemsf.chromium, 0.8f)

		(Blocks.impactDrill as BurstDrill).drillMultipliers.putAll(erekirDrillMultipliers)
		(Blocks.eruptionDrill as BurstDrill).drillMultipliers.putAll(erekirDrillMultipliers)
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