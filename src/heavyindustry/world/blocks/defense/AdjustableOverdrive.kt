package heavyindustry.world.blocks.defense

import arc.Core.*
import arc.graphics.*
import arc.graphics.g2d.*
import arc.math.*
import arc.util.*
import arc.util.io.*
import mindustry.Vars.*
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.graphics.*
import mindustry.world.*

open class AdjustableOverdrive(name: String) : Block(name) {
    companion object {
        @JvmField val max = 100000f
        @JvmField val min = 1f

        @JvmField val commandMap = floatArrayOf(1f, 10f, 100f, 1000f)

        @JvmField val initMask = 1000000f
    }

    @JvmField var topRegion: TextureRegion? = null

    @JvmField var lastNumber = 200f

    @JvmField var range = 120f
    @JvmField var reload = 30f
    @JvmField var baseColor: Color? = Color.valueOf("feb380")
    @JvmField var phaseColor: Color? = Color.valueOf("ff9ed5")

    init {
        update = true
        solid = true
        configurable = true
        canOverdrive = false
    }

    override fun load() {
        super.load()
        topRegion = atlas.find("$name-top")
    }

    override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.accent)
    }

    open inner class AdjustableOverdriveBuild : Building() {
        @JvmField var heat = 0f
        @JvmField var phaseHeat = 0f

        @JvmField var speedTo = 200f

        override fun playerPlaced(config: Any?) {
            app.post { configure(lastNumber + initMask) }
        }

        override fun configured(builder: Unit?, value: Any?) {
            if (value is Number) {
                if (value.toFloat() > initMask) {
                    speedTo = value.toFloat() - initMask
                } else if (value.toInt() >= 100) {
                    val commandVal = commandMap[value.toInt() - 100]
                    val result = Math.max(min, speedTo - commandVal)
                    speedTo = result
                    lastNumber = speedTo
                } else {
                    val commandVal = commandMap[value.toInt()]
                    val result = Math.min(max, speedTo + commandVal)
                    speedTo = result
                    lastNumber = speedTo
                }
            }
        }

        override fun drawLight() {
            Drawf.light(x, y, 50f * efficiency(), baseColor, 0.7f * efficiency())
        }

        override fun drawSelect() {
            val realRange = range
            indexer.eachBlock(this, realRange, { other -> other.block.canOverdrive }, { other ->
                val tmp = Tmp.c1.set(baseColor)
                tmp.a = Mathf.absin(4f, 1f)
                Drawf.selected(other, tmp)
            })
            Drawf.dashCircle(x, y, realRange, baseColor)
        }

        override fun draw() {
            super.draw()
            val f = 1 - (Time.time / 100) % 1
            Draw.color(baseColor, phaseColor, phaseHeat)
            Draw.alpha(heat * Mathf.absin(Time.time, 10f, 1f) * 0.5f)
            Draw.rect(topRegion, tile.drawx(), tile.drawy())
            Draw.alpha(1f)
            Lines.stroke((2 * f + 0.2f) * heat)
            Lines.square(tile.drawx(), tile.drawy(), (1 - f) * 8)
            Draw.reset()
        }

        override fun updateTile() {
            val speedBoost = 1f
            val duration = reload + 1f
            indexer.eachBlock(this, range, { true }, { other ->
                other.applySlowdown(speedBoost, duration)
            })
        }

        override fun write(write: Writes) {
            super.write(write)
            write.f(heat)
            write.f(phaseHeat)
            write.f(speedTo)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            heat = read.f()
            phaseHeat = read.f()
            speedTo = read.f()
        }
    }
}