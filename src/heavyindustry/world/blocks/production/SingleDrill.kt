package heavyindustry.world.blocks.production

import arc.math.*
import heavyindustry.content.*
import mindustry.*
import mindustry.content.*
import mindustry.world.*
import mindustry.world.blocks.environment.*
import mindustry.world.blocks.production.*
import mindustry.world.meta.*

open class SingleDrill(name: String) : Drill(name) {
	/** Can only get that specific item.  */
	@JvmField var requiredItem = Items.copper

	init {
		drillEffect = Fxf.spark
		updateEffect = Fx.none
		hasLiquids = false
		drawRim = false
		drawMineItem = true
		drawSpinSprite = true
	}

	override fun init() {
		tier = requiredItem.hardness
		super.init()
	}

	override fun canMine(tile: Tile?): Boolean {
		if (tile == null || tile.block().isStatic) return false
		var mine = false

		val drops = tile.drop()
		if (drops != null) mine = drops.name == requiredItem.name
		return drops != null && mine && drops !== blockedItem
	}

	override fun setStats() {
		super.setStats()
		stats.remove(Stat.drillTier)

		stats.add(Stat.drillTier, StatValues.blocks { b: Block -> b is Floor && !b.wallOre && b.itemDrop != null && b.itemDrop !== blockedItem && b.itemDrop.name == requiredItem.name && (Vars.indexer.isBlockPresent(b) || Vars.state.isMenu) })
	}

	open inner class SingleDrillBuild : DrillBuild() {
		override fun updateTile() {
			if (timer(timerDump, dumpTime.toFloat())) {
				dump(if (dominantItem != null && items.has(dominantItem)) dominantItem else null)
			}

			if (dominantItem == null) {
				return
			}

			timeDrilled += warmup * delta()

			val delay = getDrillTime(dominantItem)

			if (items.total() < itemCapacity && dominantItems > 0 && efficiency > 0) {
				val speed = Mathf.lerp(1f, liquidBoostIntensity, optionalEfficiency) * efficiency

				lastDrillSpeed = (speed * dominantItems * warmup) / delay
				warmup = Mathf.approachDelta(warmup, speed, warmupSpeed)
				progress += delta() * dominantItems * speed * warmup
			} else {
				lastDrillSpeed = 0f
				warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed)
				return
			}

			if (dominantItems > 0 && progress >= delay && items.total() < itemCapacity) {
				offload(dominantItem)

				progress %= delay

				drillEffect.at(x, y)
			}
		}
	}
}
