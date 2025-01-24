package heavyindustry.world.blocks.production

import arc.scene.style.*
import arc.scene.ui.layout.*
import arc.struct.*
import heavyindustry.util.*
import mindustry.*
import mindustry.gen.*
import mindustry.type.*
import mindustry.ui.*
import mindustry.world.blocks.production.*
import mindustry.world.meta.*

open class FilterCrafter(name: String) : GenericCrafter(name) {
    @JvmField val filterItemsBuilder = Seq<Item>()

    init {
        configurable = true
    }

    override fun setStats() {
        super.setStats()
        stats.remove(Stat.productionTime)
    }

    open inner class FilterCrafterBuild : GenericCrafterBuild() {
        @JvmField var filterItems = filterItemsBuilder.toArray<Item>(Item::class.java)
        @JvmField var shown = arrayOf(false, false, false, false)
        @JvmField var current = 0

        override fun acceptItem(source: Building, item: Item): Boolean {
            return filterItems.indexOf(item) < getMaximumAccepted(item)
        }

        override fun buildConfiguration(table: Table) {
            table.background(Tex.pane)
            table.pane { but ->
                but.collapser({ bs ->
                    Vars.content.items().each { item ->
                        bs.button(TextureRegionDrawable(item.uiIcon), Styles.flati, 24f) {
                            for (i in 0..3) {
                                if (shown[i]) {
                                    filterItems[i] = item
                                    shown[i] = false
                                }
                            }
                        }.size(30f)
                    }
                }, { shown[0] || shown[1] || shown[2] || shown[3] })
            }.size(120f, 30f).row()
            table.pane { but2 ->
                place(but2, 1)
                place(but2, 0)
                place(but2, 3)
                place(but2, 2)
            }.row()
            table.table { places ->
                places.defaults().size(30f)
                places.image(Icon.up)
                places.image(Icon.right)
                places.image(Icon.down)
                places.image(Icon.left)
            }
        }

        protected fun place(table: Table, index: Int) {
            table.button(Icon.add, Styles.flati, 24f) {
                shown[current] = false
                shown[index] = true
                current = index
            }.update { m ->
                m.style.imageUp = eq(filterItems[index].uiIcon == null, Icon.add, TextureRegionDrawable(filterItems[index].uiIcon))
            }.size(30f)
        }

        override fun updateTile() {
            for (i in 0..3) {
                val build = nearby(i)
                if (build != null && build.acceptItem(this, filterItems[i]) && items.has(filterItems[i])) {
                    build.handleItem(this, filterItems[i])
                    items.remove(filterItems[i], 1)
                }
            }
        }
    }
}
