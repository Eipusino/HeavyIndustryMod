package heavyindustry.world.blocks.production

import arc.*
import arc.graphics.*
import arc.graphics.g2d.*
import arc.math.*
import arc.math.geom.*
import arc.struct.*
import arc.util.*
import arc.util.io.*
import mindustry.*
import mindustry.content.*
import mindustry.entities.*
import mindustry.game.*
import mindustry.gen.*
import mindustry.graphics.*
import mindustry.type.*
import mindustry.world.*
import mindustry.world.meta.*

open class MultiDrill(name: String) : Block(name) {
    @JvmField val oreCount = ObjectIntMap<Item>()

    @JvmField var hardnessDrillMultiplier = 50f
    @JvmField var drillTime = 280f
    @JvmField var liquidBoostIntensity = 1.8f

    @JvmField var warmupSpeed = 0.01f
    @JvmField var rotateSpeed = 6f

    @JvmField var drillEffect: Effect = Fx.mineHuge
    @JvmField var updateEffect: Effect = Fx.pulverizeRed
    @JvmField var updateEffectChance = 0.03f

    @JvmField var heatColor: Color = Color.valueOf("ff5512")

    @JvmField var drawRim = false
    @JvmField var drawSpinSprite = true

    lateinit var rimRegion: TextureRegion
    lateinit var rotatorRegion: TextureRegion
    lateinit var topRegion: TextureRegion

    init {
        update = true
        solid = true
        group = BlockGroup.drills
        hasLiquids = true
        hasItems = true
        ambientSound = Sounds.drill
        ambientSoundVolume = 0.018f
    }

    override fun load() {
        super.load()
        rimRegion = Core.atlas.find("$name-rim")
        rotatorRegion = Core.atlas.find("$name-rotator")
        topRegion = Core.atlas.find("$name-top")
    }

    override fun icons(): Array<TextureRegion> {
        return arrayOf(region, rotatorRegion, topRegion)
    }

    override fun canPlaceOn(tile: Tile?, team: Team?, rotation: Int): Boolean {
        if (tile != null) {
            for (other in tile.getLinkedTilesAs(this, tempTiles)) {
                if (canMine(other)) {
                    return true
                }
            }
            for (edge in Edges.getInsideEdges(size + 2)) {
                val other = Vars.world.tile(tile.x + edge.x, tile.y + edge.y)
                if (canMine(other)) {
                    return true
                }
            }
        }
        return false
    }

    override fun drawPlace(x: Int, y: Int, rotation: Int, valid: Boolean) {
        super.drawPlace(x, y, rotation, valid)

        val tile = Vars.world.tile(x, y) ?: return
        countOre(tile)

        var off = 0
        for (ore in oreCount.keys()) {
            val dx: Float = x * Vars.tilesize + offset - 16
            val dy = y * Vars.tilesize + offset + size * Vars.tilesize / 2f
            Draw.mixcol(Color.darkGray, 1f)
            val itemRegion = ore.fullIcon
            Draw.rect(itemRegion, dx + off, dy - 1)
            Draw.reset()
            Draw.rect(itemRegion, dx + off, dy)
            off += 8
        }
        Draw.reset()

        Draw.color(Pal.placing)
        Lines.stroke(size.toFloat() / 2)
        Lines.square(x * Vars.tilesize + offset, y * Vars.tilesize + offset, (Vars.tilesize / 2f) * (size + 2).toFloat())
    }

    open fun countOre(tile: Tile) {
        oreCount.clear()

        for (other in tile.getLinkedTilesAs(this, tempTiles)) {
            if (canMine(other)) {
                oreCount.increment(other.drop(), 0, 1)
            }
        }

        val bot = (-((size - 1) / 2f)).toInt() - 1
        val top = ((size - 1) / 2f + 0.5f).toInt() + 1

        val edges = Edges.getEdges(size) + arrayOf(Point2(bot, bot), Point2(bot, top), Point2(top, top), Point2(top, bot))
        for (edge in edges) {
            val other = Vars.world.tile(tile.x + edge.x, tile.y + edge.y)
            if (canMine(other)) {
                oreCount.increment(other.drop(), 0, 1)
            }
        }
    }

    open fun canMine(tile: Tile?) = tile?.drop() != null

    override fun setStats() {
        super.setStats()
        stats.add(Stat.drillSpeed, 60f / drillTime * size * size, StatUnit.itemsSecond)
        stats.add(Stat.boostEffect, liquidBoostIntensity * liquidBoostIntensity, StatUnit.timesSpeed)
    }

    open inner class MultiDrillBuild : Building() {
        @JvmField val ores = ObjectIntMap<Item>()
        @JvmField val oreProgress = ObjectFloatMap<Item>()

        @JvmField var timeDrilled = 0f
        @JvmField var warmup = 0f

        override fun shouldActiveSound() = efficiency() > 0.01f

        override fun ambientVolume() = efficiency() * (size * size) / 4f

        override fun drawSelect() {
            var off = 0
            for (ore in ores.keys()) {
                val dx = x - size * Vars.tilesize / 2f
                val dy = y + size * Vars.tilesize / 2f
                Draw.mixcol(Color.darkGray, 1f)
                val itemRegion = ore.fullIcon
                Draw.rect(itemRegion, dx + off, dy - 1)
                Draw.reset()
                Draw.rect(itemRegion, dx + off, dy)
                off += 8
            }
            Draw.reset()
            Draw.color(Pal.placing)
            Lines.stroke(size.toFloat() / 2)
            Lines.square(tileX() * Vars.tilesize + offset, tileY() * Vars.tilesize + offset, (Vars.tilesize / 2f) * (size + 2).toFloat())
        }

        override fun drawCracks() {}

        override fun onProximityUpdate() {
            countOre(tile)
            ores.clear()
            oreProgress.clear()
            for (ore in oreCount) {
                ores.put(ore.key, ore.value)
            }
        }

        override fun updateTile() {
            if (ores.isEmpty) return

            if (timer(timerDump, dumpTime.toFloat())) {
                items.each { item, _ -> dump(item) }
            }

            timeDrilled += warmup * delta()

            if (items.total() < ores.size * itemCapacity && canConsume()) {
                val speed = Mathf.lerp(1f, liquidBoostIntensity, optionalEfficiency) * efficiency
                warmup = Mathf.approachDelta(warmup, speed, warmupSpeed)

                for (ore in ores) {
                    oreProgress.increment(ore.key, 0f, delta() * ore.value * speed * warmup)
                }

                if (Mathf.chanceDelta((updateEffectChance * warmup).toDouble())) {
                    updateEffect.at(x + Mathf.range(size * 2f), y + Mathf.range(size * 2f))
                }
            } else {
                warmup = Mathf.lerpDelta(warmup, 0f, warmupSpeed)
                return
            }

            for (ore in ores) {
                val delay = drillTime + hardnessDrillMultiplier * ore.key.hardness
                if (oreProgress.get(ore.key, 0f) >= delay && items.get(ore.key) < itemCapacity) {
                    offload(ore.key)
                    oreProgress.increment(ore.key, 0f, -delay)
                    drillEffect.at(x + Mathf.range(size), y + Mathf.range(size), ore.key.color)
                }
            }
        }

        override fun draw() {
            val s = 0.3f
            val ts = 0.6f

            Draw.rect(region, x, y)

            Draw.z(30.1f)
            drawCracks()
            Draw.z(30.2f)

            if (drawRim) {
                Draw.color(heatColor)
                Draw.alpha(warmup * ts * (1f - s + Mathf.absin(Time.time, 3f, s)))
                Draw.blend(Blending.additive)
                Draw.rect(rimRegion, x, y)
                Draw.blend()
                Draw.color()
            }

            if (drawSpinSprite) {
                Drawf.spinSprite(rotatorRegion, x, y, timeDrilled * rotateSpeed)
            } else {
                Draw.rect(rotatorRegion, x, y, timeDrilled * rotateSpeed)
            }

            Draw.rect(topRegion, x, y)
        }

        override fun write(write: Writes) {
            super.write(write)
            write.f(warmup)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            warmup = read.f()
        }
    }
}