package heavyindustry.world.blocks.liquid

import arc.Core
import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import arc.math.Mathf
import arc.util.Time
import heavyindustry.util.eq
import mindustry.Vars
import mindustry.game.Team
import mindustry.logic.LAccess
import mindustry.type.Liquid
import mindustry.world.Tile
import mindustry.world.blocks.liquid.LiquidBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawMulti
import mindustry.world.draw.DrawPumpLiquid
import mindustry.world.meta.BlockGroup
import mindustry.world.meta.Env
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit

open class ThermalPump(name: String) : LiquidBlock(name) {
	/** Pump amount per tile.  */
	@JvmField var pumpAmount = 0.2f

	/** Interval in-between item consumptions, if applicable.  */
	@JvmField var consumeTime = 60f * 5f
	@JvmField var warmupSpeed = 0.019f

	/** Division of the Pump Amount.  */
	@JvmField var divisionMultiplierPump = 5f
	@JvmField var defaultTemperature = 0.5f
	@JvmField var drawer = DrawMulti(DrawDefault(), DrawPumpLiquid())

	init {
		group = BlockGroup.liquids
		floating = true
		envEnabled = Env.terrestrial
	}

	override fun setStats() {
		super.setStats()

		stats.add(Stat.output, 60f * pumpAmount * size * size, StatUnit.liquidSecond)
	}

	protected open fun canPump(tile: Tile?): Boolean = tile != null && tile.floor().liquidDrop != null

	override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
		super.drawPlace(x, y, rotation, valid)

		val tile = Vars.world.tile(x, y) ?: return

		var amount = 0f
		var totalAmount = 0f
		var liquidDrop: Liquid? = null

		for (other in tile.getLinkedTilesAs(this, tempTiles)) {
			totalAmount += 1f
			if (canPump(other)) {
				if (liquidDrop != null && other.floor().liquidDrop !== liquidDrop) {
					liquidDrop = null
					break
				}
				liquidDrop = other.floor().liquidDrop
				amount += other.floor().liquidMultiplier
			}
		}

		if (liquidDrop != null) {
			val tempBoost = ((liquidDrop.temperature - defaultTemperature) / divisionMultiplierPump)
			val efficiency = (((pumpAmount + tempBoost)) / (pumpAmount))
			val tileEfficiency = amount / totalAmount
			val width = drawPlaceText(Core.bundle.formatFloat("bar.pumpspeed", amount * (pumpAmount + tempBoost) * 60f, 0), x, y, valid)

			drawPlaceText(Core.bundle.formatFloat("bar.efficiency", (tileEfficiency * efficiency) * 100, 1), x, (y + 1), valid)

			val dx = x * Vars.tilesize + offset - width / 2f - 4f
			val dy = y * Vars.tilesize + offset + size * Vars.tilesize / 2f + 5
			val s = Vars.iconSmall / 4f
			val ratio = liquidDrop.fullIcon.width.toFloat() / liquidDrop.fullIcon.height

			Draw.mixcol(Color.darkGray, 1f)
			Draw.rect(liquidDrop.fullIcon, dx, dy - 1, s * ratio, s)
			Draw.reset()
			Draw.rect(liquidDrop.fullIcon, dx, dy, s * ratio, s)
		}
	}

	override fun load() {
		super.load()
		drawer.load(this)
	}

	override fun icons(): Array<TextureRegion> = drawer.finalIcons(this)

	override fun canPlaceOn(tile: Tile, team: Team, rotation: Int): Boolean {
		if (isMultiblock) {
			var last: Liquid? = null
			for (other in tile.getLinkedTilesAs(this, tempTiles)) {
				if (other.floor().liquidDrop == null) continue
				if (other.floor().liquidDrop !== last && last != null) return false
				last = other.floor().liquidDrop
			}
			return last != null
		} else {
			return canPump(tile)
		}
	}

	override fun setBars() {
		super.setBars()

		//replace dynamic output bar with own custom bar
		addLiquidBar { build: ThermalPumpBuild -> build.liquidDrop }
	}

	open inner class ThermalPumpBuild : LiquidBuild() {
		@JvmField var warmup = 0f
		@JvmField var totalProgress = 0f
		@JvmField var consTimer = 0f
		@JvmField var amount = 0f

		var liquidDrop: Liquid? = null

		override fun draw() {
			drawer.draw(this)
		}

		override fun drawLight() {
			super.drawLight()
			drawer.drawLight(this)
		}

		override fun pickedUp() {
			amount = 0f
		}

		override fun sense(sensor: LAccess): Double {
			if (sensor == LAccess.efficiency) return eq(shouldConsume(), efficiency, 0f).toDouble()
			if (sensor == LAccess.totalLiquids) return eq(liquidDrop == null, 0f, liquids.get(liquidDrop)).toDouble()
			return super.sense(sensor)
		}

		override fun onProximityUpdate() {
			super.onProximityUpdate()

			amount = 0f
			liquidDrop = null

			for (other in tile.getLinkedTiles(tempTiles)) {
				if (canPump(other)) {
					liquidDrop = other.floor().liquidDrop
					amount += other.floor().liquidMultiplier
				}
			}
		}

		override fun shouldConsume(): Boolean = liquidDrop != null && liquids.get(liquidDrop) < liquidCapacity - 0.01f && enabled

		override fun updateTile() {
			if (efficiency > 0 && liquidDrop != null) {
				val tempBoost = ((liquidDrop!!.temperature - defaultTemperature) / divisionMultiplierPump)
				val maxPump = Math.min(liquidCapacity - liquids.get(liquidDrop), amount * (pumpAmount + tempBoost) * edelta())
				liquids.add(liquidDrop, maxPump)

				//does nothing for most pumps, as those do not require items.
				if (delta().let { consTimer += it; consTimer } >= consumeTime) {
					consume()
					consTimer %= 1f
				}

				warmup = Mathf.approachDelta(warmup, if (maxPump > 0.001f) 1f else 0f, warmupSpeed)
			} else {
				warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed)
			}

			totalProgress += warmup * Time.delta

			if (liquidDrop != null) {
				dumpLiquid(liquidDrop)
			}
		}

		override fun warmup(): Float = warmup

		override fun progress(): Float = Mathf.clamp(consTimer / consumeTime)

		override fun totalProgress(): Float = totalProgress
	}
}
