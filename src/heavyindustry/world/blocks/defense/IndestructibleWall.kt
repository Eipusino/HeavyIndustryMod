package heavyindustry.world.blocks.defense

import mindustry.content.*
import mindustry.gen.*
import mindustry.gen.Unit
import mindustry.world.blocks.*
import mindustry.world.blocks.defense.*

open class IndestructibleWall(name: String) : Wall(name) {
    init {
        health = 1
        instantDeconstruct = true
        placeableLiquid = instantDeconstruct
        absorbLasers = placeableLiquid
        chanceDeflect = 1f
    }

    open inner class IndestructibleWallBuild : WallBuild(), ControlBlock {
        @JvmField var unit: BlockUnitc? = null

        override fun damage(damage: Float) {}

        override fun handleDamage(amount: Float): Float {
            return 0f
        }

        override fun collision(bullet: Bullet?): Boolean {
            super.collision(bullet)
            return true
        }

        override fun unit(): Unit? {
            if (unit == null) {
                unit = UnitTypes.block!!.create(team) as BlockUnitc?
                unit!!.tile(this)
            }
            return unit as Unit?
        }

        override fun canControl(): Boolean {
            return true
        }
    }
}
