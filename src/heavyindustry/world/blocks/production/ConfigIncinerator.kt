package heavyindustry.world.blocks.production

import arc.graphics.g2d.*
import arc.scene.style.*
import arc.scene.ui.layout.*
import arc.util.io.*
import heavyindustry.ui.*
import heavyindustry.util.*
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.type.*
import mindustry.world.blocks.production.*

open class ConfigIncinerator(name: String) : Incinerator(name) {
	@JvmField var choice = arrayOf<TextureRegion>()

	init {
		config(Int::class.javaObjectType) { tile: ConfigIncineratorBuild, value ->
			tile.configRecord = value
		}
	}

	override fun load() {
		super.load()
		choice = Utils.split("$name-choice", 32, 0)
	}

	open inner class ConfigIncineratorBuild : IncineratorBuild() {
		@JvmField var consItem = true
		@JvmField var consLiquid = true
		@JvmField var configRecord = 1

		override fun configured(builder: Unit?, value: Any?) {
			super.configured(builder, value)

			//I started with an array, but was unable to save it successfully.
			when (value) {
				1 -> {
					consLiquid = true
					consItem = true
				}

				2 -> {
					consLiquid = false
					consItem = true
				}

				3 -> {
					consItem = true
					consLiquid = false
				}

				4 -> {
					consItem = false
					consLiquid = true
				}
			}
		}

		override fun acceptItem(source: Building, item: Item): Boolean = heat > 0.5f && consItem

		override fun acceptLiquid(source: Building, liquid: Liquid): Boolean = heat > 0.5f && consLiquid

		open fun switchItem() {
			consItem = !consItem
			setConfigRecord()
		}

		open fun switchLiquid() {
			consLiquid = !consLiquid
			setConfigRecord()
		}

		open fun setConfigRecord() {
			deselect()
			configRecord = eq(consItem == consLiquid, eq(consItem, 1, 2), eq(consItem, 3, 4))
			configure(configRecord)
		}

		override fun buildConfiguration(table: Table) {
			table.button(TextureRegionDrawable(choice[eq(consItem, 1, 2)]), HStyles.clearToggle) { switchItem() }.size(40f).tooltip("switch mode")
			table.button(TextureRegionDrawable(choice[eq(consLiquid, 3, 4)]), HStyles.clearToggle) { switchLiquid() }.size(40f).tooltip("switch mode")
		}

		override fun config(): Int {
			setConfigRecord()
			return configRecord
		}

		override fun write(write: Writes) {
			super.write(write)
			write.bool(consItem)
			write.bool(consLiquid)
			write.i(configRecord)
		}

		override fun read(read: Reads, revision: Byte) {
			super.read(read, revision)
			consItem = read.bool()
			consLiquid = read.bool()
			configRecord = read.i()
		}
	}
}