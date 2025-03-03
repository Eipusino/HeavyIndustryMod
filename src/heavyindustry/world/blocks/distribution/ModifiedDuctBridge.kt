package heavyindustry.world.blocks.distribution

import arc.graphics.g2d.Draw
import arc.math.geom.Geometry
import arc.util.Eachable
import mindustry.Vars
import mindustry.entities.units.BuildPlan
import mindustry.gen.Building
import mindustry.graphics.Drawf
import mindustry.graphics.Pal
import mindustry.type.Item
import mindustry.world.blocks.distribution.DirectionBridge

open class ModifiedDuctBridge(name: String) : DirectionBridge(name) {
	@JvmField var speed = 5f
	@JvmField var dirFlip = false

	init {
		itemCapacity = 4
		hasItems = true
		underBullets = true
		isDuct = true
	}

	override fun drawPlanRegion(plan: BuildPlan, list: Eachable<BuildPlan>) {
		if (!dirFlip) {
			Draw.rect(region, plan.drawx(), plan.drawy(), plan.rotation * 90f)
		} else {
			Draw.rect(region, plan.drawx(), plan.drawy(), plan.rotation * -90f)
		}
	}

	override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean, line: Boolean) {
		var length = range
		var found: Building? = null
		val dx = Geometry.d4x(rotation)
		val dy = Geometry.d4y(rotation)

		//find the link
		for (i in 1..range) {
			val other = Vars.world.tile(x + dx * i, y + dy * i)

			if (other != null && other.build is DirectionBridgeBuild && other.build.block == this && other.build.team === Vars.player.team()) {
				length = i
				found = other.build
				dirFlip = true
				break
			} else {
				dirFlip = false
			}
		}

		if (line || found != null) {
			Drawf.dashLine(
				Pal.placing,
				x * Vars.tilesize + dx * (Vars.tilesize / 2f + 2),
				y * Vars.tilesize + dy * (Vars.tilesize / 2f + 2),
				(x * Vars.tilesize + dx * length * Vars.tilesize).toFloat(),
				(y * Vars.tilesize + dy * length * Vars.tilesize).toFloat()
			)
		}

		if (found != null) {
			if (line) {
				Drawf.square(found.x, found.y, found.block.size * Vars.tilesize / 2f + 2.5f, 0f)
			} else {
				Drawf.square(found.x, found.y, 2f)
			}
		}
	}

	open inner class ModifiedDuctBridgeBuild : DirectionBridgeBuild() {
		@JvmField var progress = 0f
		@JvmField var transporter = false

		override fun updateTile() {
			val link = findLink()
			if (link != null) {
				transporter = (link.occupied.isNotEmpty())
				link.occupied[rotation % 4] = this
				if (items.any() && link.items.total() < link.block.itemCapacity) {
					progress += edelta()
					while (progress > speed) {
						val next = items.take()
						if (next != null && link.items.total() < link.block.itemCapacity) {
							link.handleItem(this, next)
						}
						progress -= speed
					}
				}
			} else if (items.any()) {
				transporter = false
				val next = items.first()
				if (moveForward(next)) {
					items.remove(next, 1)
				}
			}

			for (i in 0..3) {
				if (occupied[i] == null || occupied[i].rotation != i || !occupied[i].isValid) {
					occupied[i] = null
				}
			}
		}

		override fun acceptItem(source: Building, item: Item): Boolean {
			//only accept if there's an output point.
			if (findLink() == null) return false

			val rel = relativeToEdge(source.tile).toInt()
			return items.total() < itemCapacity && rel != rotation && occupied[(rel + 2) % 4] == null
		}

		override fun draw() {
			if (!transporter) {
				Draw.rect(name, x, y, rotdeg())
			} else {
				Draw.rect(name, x, y, rotdeg() - 180f)
			}
		}
	}
}
