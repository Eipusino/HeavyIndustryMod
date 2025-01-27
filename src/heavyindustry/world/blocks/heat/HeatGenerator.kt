package heavyindustry.world.blocks.heat

import arc.*
import arc.math.*
import arc.util.*
import arc.util.io.*
import heavyindustry.util.*
import mindustry.graphics.*
import mindustry.ui.*
import mindustry.world.blocks.heat.*
import mindustry.world.blocks.power.*
import mindustry.world.consumers.*
import mindustry.world.meta.*

open class HeatGenerator(name: String) : PowerGenerator(name) {
	@JvmField var maxHeat = 180f
	@JvmField var warmupSpeed = 0.4f

	override fun init() {
		removeConsumers { c -> c is ConsumePower }
		super.init()
	}

	override fun setBars() {
		super.setBars()
		addBar("heat") { tile: HeatGeneratorBuild -> Bar(
			{ Core.bundle.format("bar.heatpercent", Mathf.round(tile.heat()), Mathf.round(Mathf.clamp(tile.heat() / maxHeat) * 100)) },
			{ Pal.lightOrange },
			{ tile.heat() / maxHeat }
		) }
	}

	override fun setStats() {
		super.setStats()
		stats.add(Stat.input, maxHeat, StatUnit.heatUnits)
	}

	open inner class HeatGeneratorBuild : GeneratorBuild(), HeatConsumer {
		@JvmField var sideHeat = FloatArray(4)
		@JvmField var heat = 0f
		@JvmField var totalProgress = 0f
		@JvmField var warmup = 0f

		override fun updateTile() {
			heat = calculateHeat(sideHeat)
			warmup = Mathf.lerpDelta(warmup, eq(productionEfficiency > 0f, 1f, 0f), warmupSpeed)
			totalProgress += productionEfficiency * Time.delta
		}

		override fun shouldExplode(): Boolean = heat > 0f

		override fun totalProgress(): Float = totalProgress

		override fun warmup(): Float = warmup

		override fun updateEfficiencyMultiplier() {
			efficiency *= Mathf.clamp(heat)
			productionEfficiency = efficiency * Mathf.clamp(heat, 0f, maxHeat)
		}

		override fun sideHeat(): FloatArray = sideHeat

		override fun heatRequirement(): Float = 0f

		open fun heat(): Float = heat

		override fun write(write: Writes) {
			super.write(write)
			write.f(heat)
			write.f(warmup)
		}

		override fun read(read: Reads, revision: Byte) {
			super.read(read, revision)
			heat = read.f()
			warmup = read.f()
		}
	}
}
