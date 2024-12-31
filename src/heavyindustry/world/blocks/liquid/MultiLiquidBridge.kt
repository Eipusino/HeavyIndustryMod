package heavyindustry.world.blocks.liquid

import arc.math.*
import arc.scene.ui.layout.*
import arc.util.io.*
import heavyindustry.util.Utils.*
import mindustry.*
import mindustry.gen.*
import mindustry.type.*
import mindustry.world.blocks.*
import mindustry.world.blocks.liquid.*

open class MultiLiquidBridge(name: String) : LiquidBridge(name) {
    init {
        instantTransfer = true
    }

    open inner class MultiLiquidBridgeBuild : LiquidBridgeBuild() {
        @JvmField var invert = false
        @JvmField var sortLiquid: Liquid? = null

        override fun acceptLiquid(source: Building?, liquid: Liquid?): Boolean {
            if (link == -1) {
                val to = getTileTarget(liquid, source, false)
                return to != null && to.acceptLiquid(this, liquid) && to.team == team
            } else return super.acceptLiquid(source, liquid)
        }

        override fun handleLiquid(source: Building?, liquid: Liquid?, amount: Float) {
            if (link == -1) getTileTarget(liquid, source, true)?.handleLiquid(this, liquid, amount)
            else super.handleLiquid(source, liquid, amount)
        }

        open fun isSame(other: Building?): Boolean {
            return other != null && other.block.instantTransfer
        }

        open fun getTileTarget(liquid: Liquid?, source: Building?, flip: Boolean): Building? {
            val dir = source!!.relativeTo(tile.x.toInt(), tile.y.toInt()).toInt()
            if (dir == -1) return null
            val to: Building?
            if (((liquid == sortLiquid) != invert) == enabled) {
                if (isSame(source) && isSame(nearby(dir))) return null
                to = nearby(dir)
            } else {
                val a = nearby(Mathf.mod(dir - 1, 4))
                val b = nearby(Mathf.mod(dir + 1, 4))
                val ac = a != null && !(a.block.instantTransfer && source.block.instantTransfer) && a.acceptLiquid(this, liquid)
                val bc = b != null && !(b.block.instantTransfer && source.block.instantTransfer) && b.acceptLiquid(this, liquid)
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
                ItemSelection.buildTable(table, Vars.content.liquids(), { sortLiquid }, { liquid -> sortLiquid = liquid }, block.selectionColumns)
            }
        }

        override fun write(write: Writes) {
            super.write(write)
            write.s((sortLiquid?.id ?: -1).toInt())
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            sortLiquid = Vars.content.liquid(read.s().toInt())
        }
    }
}