package heavyindustry.world.blocks.defense

import arc.*
import arc.graphics.*
import arc.util.*
import arc.util.io.*
import mindustry.entities.*
import mindustry.ui.*
import mindustry.world.blocks.defense.*
import mindustry.world.meta.*

open class DamageBackWall(name: String) : Wall(name) {
    @JvmField var radius = 60f
    @JvmField var damageMulti = 1.5f
    @JvmField var max = 60f
    @JvmField var reload = 10f

    @JvmField var pierce = true

    @JvmField var air = true
    @JvmField var ground = true

    init {
        update = true
    }

    override fun setStats() {
        super.setStats()
        stats.add(Stat("danage-back-radius", StatCat.function), radius / 8, StatUnit.blocks)
        stats.add(Stat("damage-back-threshold", StatCat.function), max, StatUnit.none)
        stats.add(Stat("damage-back", StatCat.function), max * damageMulti, StatUnit.none)
        stats.add(Stat("damage-back-reload", StatCat.function), reload / 60, StatUnit.seconds)
        stats.add(Stat("damage-back-air", StatCat.function), "$air", StatUnit.none)
        stats.add(Stat("damage-back-ground", StatCat.function), "$ground", StatUnit.none)
    }

    override fun setBars() {
        super.setBars()
        addBar("damage") { e: DamageBackWallBuild -> Bar(
            { Core.bundle.format("bar.damage-progress", Strings.fixed(e.getProg() * 100, 0)) },
            { Color.valueOf("ff5845") },
            { e.getProg() }
        ) }
    }

    open inner class DamageBackWallBuild : WallBuild() {
        @JvmField var allDamage = 0f
        @JvmField var times = 0f

        open fun getProg(): Float {
            return allDamage / max
        }

        override fun update() {
            super.update()
            times += Time.delta
        }

        override fun handleDamage(amount: Float): Float {
            if (times >= reload) {
                allDamage = Math.min(allDamage + amount, max)
                if (allDamage >= max) {
                    Damage.damage(team, x, y, radius, max * damageMulti, pierce, air, ground)
                    allDamage = 0.0f
                    times = 0.0f
                }
            }
            return amount
        }

        override fun write(write: Writes) {
            super.write(write)
            write.f(allDamage)
            write.f(times)
        }

        override fun read(read: Reads, revision: Byte) {
            super.read(read, revision)
            allDamage = read.f()
            times = read.f()
        }
    }
}