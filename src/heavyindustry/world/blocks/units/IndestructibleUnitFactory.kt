package heavyindustry.world.blocks.units

import arc.graphics.g2d.Draw
import arc.scene.style.TextureRegionDrawable
import arc.scene.ui.ButtonGroup
import arc.scene.ui.ImageButton
import arc.scene.ui.ScrollPane
import arc.scene.ui.layout.Scl
import arc.scene.ui.layout.Table
import arc.struct.IntSeq
import arc.util.io.Reads
import arc.util.io.Writes
import heavyindustry.util.Utils
import heavyindustry.util.eq
import heavyindustry.util.ins
import mindustry.Vars
import mindustry.content.Items
import mindustry.ctype.ContentType
import mindustry.game.Team
import mindustry.gen.Building
import mindustry.gen.Tex
import mindustry.type.ItemStack
import mindustry.type.UnitType
import mindustry.ui.Styles
import mindustry.world.Tile
import mindustry.world.blocks.units.UnitFactory

open class IndestructibleUnitFactory(name: String) : UnitFactory(name) {
	@JvmField var consItems = ItemStack.empty

	init {
		hasPower = false
		targetable = false

		config(Int::class.javaObjectType) { tile: IndestructibleUnitFactoryBuild, index ->
			tile.currentPlan = eq(index < 0 || index >= plans.size, -1, index)
			tile.progress = 0f
			tile.payload = null
		}
		config(UnitType::class.java) { tile: IndestructibleUnitFactoryBuild, index ->
			tile.currentPlan = plans.indexOf { p -> p.unit == index }
			tile.progress = 0f
			tile.payload = null
		}
		config(IntSeq::class.java) { tile: IndestructibleUnitFactoryBuild, index ->
			val get = index.get(0)
			tile.currentPlan = eq(get < 0 || get >= plans.size, -1, get)
			tile.progress = 0f
			tile.targetTeam = Team.get(index.get(1))
			tile.payload = null
		}
	}

	override fun init() {
		plans = Vars.content.getBy<UnitType>(ContentType.unit)
			.map { unit -> UnitPlan(unit, 1f, consItems) }
			.retainAll { plan -> !plan.unit.isHidden }
		super.init()
		itemCapacity = 1
		capacities[Items.copper.id.toInt()] = 1
	}

	open inner class IndestructibleUnitFactoryBuild : UnitFactoryBuild() {
		var targetTeam = Team.sharded

		override fun init(tile: Tile, team: Team, shouldAdd: Boolean, rotation: Int): Building {
			super.init(tile, team, shouldAdd, rotation)
			targetTeam = team
			return this
		}

		override fun buildConfiguration(table: Table) {
			val group = ButtonGroup<ImageButton>()
			val cont = Table()
			cont.defaults().size(40f)
			var i = 0

			for (t in Utils.baseTeams) {
				val button = cont.button(Tex.whiteui, Styles.clearTogglei, 24f, {
				}).group(group).get()
				button.changed { targetTeam = eq(button.isChecked, t, null) }
				ins(Tex.whiteui) { w: TextureRegionDrawable ->
					button.style.imageUp = w.tint(t.color.r, t.color.g, t.color.b, t.color.a)
				}
				button.update { button.isChecked = targetTeam == t }

				if (i++ % 4 == 3) {
					cont.row()
				}
			}
			if (i % 4 != 0) {
				val remaining = 4 - (i % 4)
				for (j in 0..remaining) {
					cont.image(Styles.black6)
				}
			}
			val pane = ScrollPane(cont, Styles.smallPane)
			pane.setScrollingDisabled(true, false)
			pane.setOverscroll(false, false)
			table.add(pane).maxHeight(Scl.scl(40f * 2)).left()
			table.row()
			super.buildConfiguration(table)
		}

		override fun draw() {
			super.draw()
			Draw.color(targetTeam.color)
			Draw.rect(teamRegion, x, y)
		}

		override fun drawPayload() {
			payload?.unit?.team = targetTeam
			super.drawPayload()
		}

		override fun config(): IntSeq = IntSeq.with(currentPlan, targetTeam.id)

		override fun write(write: Writes) {
			super.write(write)
			write.i(targetTeam.id)
		}

		override fun read(read: Reads) {
			super.read(read)
			targetTeam = Team.get(read.i())
		}
	}
}