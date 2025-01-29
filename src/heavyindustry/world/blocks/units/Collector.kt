package heavyindustry.world.blocks.units

import arc.*
import arc.math.*
import arc.math.geom.*
import arc.struct.*
import arc.util.io.*
import mindustry.Vars.*
import mindustry.content.*
import mindustry.game.EventType.*
import mindustry.gen.*
import mindustry.graphics.*
import mindustry.type.*
import mindustry.world.*

open class Collector(name: String) : Block(name) {
	@JvmField val existing = Seq<Building>()

	@JvmField var range = 120f

	init {
		solid = true
		update = true
		destructible = true

		Events.on(UnitDestroyEvent::class.java) { event ->
			val u = event.unit
			Geometry.findClosest(u.x, u.y, existing)?.let {
				if (u.within(it, range)) {
					val amount = Mathf.clamp(u.hitSize, 0f, it.getMaximumAccepted(Items.scrap).toFloat())
					Fx.itemTransfer.at(u.x, u.y, amount, Items.scrap.color, it)
					it.items.add(Items.scrap, Mathf.ceil(amount))
				}
			}
		}

		Events.on(ResetEvent::class.java) {
			existing.clear()
		}
	}

	override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
		Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.placing)
	}

	open inner class CollectorBuild : Building() {
		override fun updateTile() {
			dump(Items.scrap)
		}

		override fun created() {
			super.created()
			existing.add(this)
		}

		override fun onRemoved() {
			existing.remove(this)
			super.onRemoved()
		}

		override fun drawSelect() {
			Drawf.dashCircle(x, y, range, team.color)
		}

		override fun acceptItem(source: Building?, item: Item?) = false

		override fun read(read: Reads) {
			super.read(read)
			existing.add(this)
		}
	}
}