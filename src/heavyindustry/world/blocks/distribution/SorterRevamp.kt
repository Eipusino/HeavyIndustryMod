package heavyindustry.world.blocks.distribution

import arc.*
import arc.graphics.g2d.*
import arc.math.*
import arc.scene.ui.layout.*
import arc.util.*
import arc.util.io.*
import heavyindustry.*
import heavyindustry.util.*
import mindustry.*
import mindustry.entities.units.*
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.type.*
import mindustry.world.*
import mindustry.world.blocks.*
import mindustry.world.meta.*

open class SorterRevamp(name: String) : Block(name) {
	@JvmField var cross = Varsf.whiteRegion
	@JvmField var source = Varsf.whiteRegion
	@JvmField var invert = false

	init {
		update = false
		destructible = true
		underBullets = true
		instantTransfer = false
		group = BlockGroup.transportation
		configurable = true
		unloadable = false
		saveConfig = true
		clearOnDoubleTap = true
		itemCapacity = 1

		config(Item::class.java) { tile: SorterRevampBuild, item: Item? -> tile.sortItem = item }
		configClear { tile: SorterRevampBuild -> tile.sortItem = null }

		region = Core.atlas.find(name)
	}

	override fun load() {
		super.load()
		cross = Core.atlas.find("$name-cross", "cross-full")
		source = Core.atlas.find("source-bottom")
	}

	override fun drawPlanConfig(plan: BuildPlan, list: Eachable<BuildPlan>) {
		drawPlanConfigCenter(plan, plan.config, "center", true)
	}

	override fun outputsItems(): Boolean = true

	override fun minimapColor(tile: Tile): Int = eqf(tile.build, { b: SorterRevampBuild -> nof(b.sortItem, { c -> c.color.rgba() }, 0) }, 0)

	override fun icons(): Array<TextureRegion> = arrayOf(source, region)

	open inner class SorterRevampBuild : Building() {
		@JvmField var sortItem: Item? = null

		@JvmField var r0: Boolean = false
		@JvmField var r1: Boolean = false

		override fun configured(player: Unit, value: Any) {
			super.configured(player, value)

			if (!Vars.headless) {
				Vars.renderer.minimap.update(tile)
			}
		}

		override fun draw() {
			Draw.rect(region, x, y)
			if (sortItem == null) {
				Draw.rect(cross, x, y)
			} else {
				Draw.color(sortItem!!.color)
				Fill.square(x, y, Vars.tilesize / 2f - 0.00001f)
				Draw.color()
			}

			//super.draw()
		}

		override fun acceptItem(source: Building, item: Item): Boolean {
			val to = getTargetTile(item, this, source, r0)

			r0 = !r0
			return to != null && to.acceptItem(this, item) && to.team === team
		}

		override fun handleItem(source: Building, item: Item) {
			val to = getTargetTile(item, this, source, r1)
			to?.handleItem(this, item)
			r1 = !r1
		}

		open fun getTargetTile(item: Item, fromBlock: Building, source: Building, flip: Boolean): Building? {
			val from = relativeToEdge(source.tile).toInt()
			var to = fromBlock.nearby(Mathf.mod(from + 2, 4))
			val canFow = to != null && to.acceptItem(fromBlock, item) && to.team === team && (((item === sortItem) != invert) == enabled)
			val inv = invert == enabled

			if (!canFow || inv) {
				if (!inv) to = null
				val offset = if (flip) -1 else 1
				val a = fromBlock.nearby(Mathf.mod(from + offset, 4))
				val ab = a != null && a.team === team && a.acceptItem(fromBlock, item)
				if (ab) {
					to = a
				}
			}

			return to
		}


		override fun buildConfiguration(table: Table) {
			ItemSelection.buildTable(block, table, Vars.content.items(), { sortItem }, { value: Item? -> configure(value) }, selectionRows, selectionColumns)
		}

		override fun config(): Item? {
			return sortItem
		}

		override fun version(): Byte {
			return 2
		}

		override fun write(write: Writes) {
			super.write(write)
			write.s(nof(sortItem, { it.id.toInt() }, -1))
		}

		override fun read(read: Reads, revision: Byte) {
			super.read(read, revision)
			sortItem = Vars.content.item(read.s().toInt())

			if (revision.toInt() == 1) {
				DirectionalItemBuffer(20).read(read)
			}
		}
	}
}

