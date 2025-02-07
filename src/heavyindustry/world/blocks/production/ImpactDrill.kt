package heavyindustry.world.blocks.production

import arc.*
import arc.graphics.*
import arc.graphics.g2d.*
import arc.math.*
import heavyindustry.util.*
import heavyindustry.world.meta.*
import mindustry.*
import mindustry.graphics.*
import mindustry.type.*
import mindustry.world.*
import mindustry.world.blocks.environment.*
import mindustry.world.blocks.production.*
import mindustry.world.meta.*

open class ImpactDrill(name: String) : Drill(name) {
	@JvmField var outputAmount = 5
	@JvmField var warmupTime = 60f

	init {
		itemCapacity = 20
		//does not drill in the traditional sense, so this is not even used
		hardnessDrillMultiplier = 0f
		//generally at center
		drillEffectRnd = 0f
	}

	override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
		super.drawPlace(x, y, rotation, valid)

		val tile = Vars.world.tile(x, y) ?: return

		countOre(tile)

		if (returnItem != null) {
			val width = drawPlaceText(Core.bundle.formatFloat("bar.drillspeed", getDrillTime() * returnCount, 2), x, y, valid)
			val dx = x * Vars.tilesize + offset - width / 2f - 4f
			val dy = y * Vars.tilesize + offset + size * Vars.tilesize / 2f + 5
			val s = Vars.iconSmall / 4f
			Draw.mixcol(Color.darkGray, 1f)
			Draw.rect(returnItem.fullIcon, dx, dy - 1, s, s)
			Draw.reset()
			Draw.rect(returnItem.fullIcon, dx, dy, s, s)

			if (drawMineItem) {
				Draw.color(returnItem.color)
				Draw.rect(itemRegion, tile.worldx() + offset, tile.worldy() + offset)
				Draw.color()
			}
		} else {
			val to = tile.getLinkedTilesAs(this, tempTiles).find { t: Tile -> t.drop() != null && (t.drop().hardness > tier || t.drop() === blockedItem) }
			val item = to?.drop()
			if (item != null) {
				drawPlaceText(Core.bundle.get("bar.drilltierreq"), x, y, valid)
			}
		}
	}

	override fun getDrillTime(item: Item): Float = getDrillTime()

	override fun setStats() {
		super.setStats()
		stats.remove(Stat.drillSpeed)
		stats.remove(Stat.drillTier)

		stats.add(Stat.drillTier, HIStatValues.drillAblesStack(getDrillTime(), outputAmount, drillMultipliers) { b: Block -> b is Floor && !b.wallOre && b.itemDrop != null && b.itemDrop.hardness <= tier && b.itemDrop !== blockedItem && (Vars.indexer.isBlockPresent(b) || Vars.state.isMenu) })

		stats.add(Stat.drillSpeed, getDrillTime(), StatUnit.itemsSecond)
	}

	open fun getDrillTime(): Float = ((outputAmount / (60 / drillTime)) / 2f) / 2f

	open inner class ImpactDrilllBuild : DrillBuild() {
		@JvmField var warmup = 0f

		override fun updateTile() {
			if (dominantItem == null) {
				return
			}
			if (timer(timerDump, dumpTime.toFloat())) {
				dump(eq(items.has(dominantItem), dominantItem, null))
			}

			if (items.total() <= itemCapacity - outputAmount && dominantItems > 0 && efficiency > 0) {
				warmup = Mathf.lerpDelta(warmup, warmupTime, efficiency)
				val wlD = (warmup / warmupTime)
				val speed = efficiency() * (wlD)
				timeDrilled += speed

				if (warmup >= warmupTime) {
					lastDrillSpeed = dominantItems / drillTime * speed
					progress += delta() * dominantItems * speed
				}
			} else {
				lastDrillSpeed = 0f
				return
			}

			if (dominantItems > 0 && progress >= drillTime && items.total() < itemCapacity) {
				for (i in 0..<outputAmount) {
					offload(dominantItem)
				}

				progress %= drillTime
				drillEffect.at(x + Mathf.range(drillEffectRnd), y + Mathf.range(drillEffectRnd), dominantItem.color)
			}
		}

		override fun draw() {
			val speedOffset = (warmup / (warmupTime / 2f))
			Draw.rect(region, x, y)
			drawDefaultCracks()

			//TODO change this draw bud
			if (dominantItem != null && drawMineItem) {
				Draw.color(dominantItem.color)
				Draw.rect(itemRegion, x, y)
				Draw.color()
			}

			Drawf.spinSprite(rotatorRegion, x, y, timeDrilled * (rotateSpeed + speedOffset))
			Draw.rect(topRegion, x, y)
		}
	}
}