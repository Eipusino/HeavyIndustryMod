package heavyindustry.world.blocks.distribution

import arc.math.*
import heavyindustry.util.*
import mindustry.*
import mindustry.gen.*
import mindustry.world.blocks.distribution.*

open class InstantBridge(name: String) : ItemBridge(name) {
	open inner class InstantBridgeBuild : ItemBridgeBuild() {
		override fun updateTile() {
			if (timer(timerCheckMoved, 30f)) {
				wasMoved = moved
				moved = false
			}

			//smooth out animation, so it doesn't stop/start immediately
			timeSpeed = Mathf.approachDelta(timeSpeed, eq(wasMoved, 1f, 0f), 1f / 60f)

			time += (timeSpeed * edelta())

			checkIncoming()

			val other = Vars.world.tile(link)
			if (!linkValid(tile, other)) {
				doDump()
				warmup = 0f
			} else {
				ins(other.build) { it: ItemBridgeBuild ->
					val inc = it.incoming
					val pos = tile.pos()
					if (!inc.contains(pos)) {
						inc.add(pos)
					}

					warmup = Mathf.approachDelta(warmup, efficiency, 1f / 30f)
					updateTransport(other.build)
				}
			}
		}

		open fun updateItems(other: Building) {
			val item = items.take()
			if (item != null && other.acceptItem(this, item)) {
				other.handleItem(other, item)
				moved = true
			} else if (item != null) {
				items.add(item, 1)
				items.undoFlow(item)
			}
		}

		override fun updateTransport(other: Building) {
			if (efficiency < 1f) {
				transportCounter += edelta()
				while (transportCounter >= transportTime) {
					updateItems(other)
					transportCounter -= transportTime
				}
			} else {
				updateItems(other)
			}
		}
	}
}
