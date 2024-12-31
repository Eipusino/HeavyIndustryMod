package heavyindustry.world.blocks.distribution

import arc.math.*
import arc.scene.ui.layout.*
import arc.util.io.*
import heavyindustry.util.Utils.*
import mindustry.*
import mindustry.gen.*
import mindustry.type.*
import mindustry.world.blocks.*
import mindustry.world.blocks.distribution.*

open class MultiItemBridge(name: String) : ItemBridge(name) {
    init {
        instantTransfer = true
    }

    open inner class MultiItemBridgeBuild : ItemBridgeBuild() {
        @JvmField var invert = false
        @JvmField var sortItem: Item? = null

        override fun acceptItem(source: Building?, item: Item?): Boolean {
            if (link == -1) {
                val to = getTileTarget(item, source, false)
                return to != null && to.acceptItem(this, item) && to.team == team
            } else return super.acceptItem(source, item)
        }

        override fun handleItem(source: Building?, item: Item?) {
            if (link == -1) getTileTarget(item, source, true)?.handleItem(this, item)
            else super.handleItem(source, item)
        }

        open fun isSame(other: Building?): Boolean {
            return other != null && other.block.instantTransfer
        }

        open fun getTileTarget(item: Item?, source: Building?, flip: Boolean): Building? {
            val dir = source!!.relativeTo(tile.x.toInt(), tile.y.toInt()).toInt()
            if (dir == -1) return null
            val to: Building?
            if (((item == sortItem) != invert) == enabled) {
                if (isSame(source) && isSame(nearby(dir))) return null
                to = nearby(dir)
            } else {
                val a = nearby(Mathf.mod(dir - 1, 4))
                val b = nearby(Mathf.mod(dir + 1, 4))
                val ac = a != null && !(a.block.instantTransfer && source.block.instantTransfer) && a.acceptItem(this, item)
                val bc = b != null && !(b.block.instantTransfer && source.block.instantTransfer) && b.acceptItem(this, item)
                if (ac && !bc) {
                    to = a
                } else if (bc && !ac) {
                    to = b
                } else if (!bc) {
                    return null
                } else {
                    to = eq((rotation and (1 shl dir)) == 0, a, b)
                    if (flip) rotation = rotation xor (1 shl dir)
                }
            }
            return to
        }

        override fun buildConfiguration(table: Table?) {
            if (link == -1) {
                ItemSelection.buildTable(table, Vars.content.items(), { sortItem }, { item -> sortItem = item }, block.selectionColumns)
            }
        }

        override fun write(write: Writes) {
            super.write(write)
            write.s((sortItem?.id ?: -1).toInt())
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            sortItem = Vars.content.item(read.s().toInt())
        }
    }
}