package heavyindustry.world.blocks.defense

import mindustry.entities.*
import mindustry.world.blocks.defense.Wall
import mindustry.world.meta.*

open class ArmorupWall(name: String) : Wall(name) {
    @JvmField var percent = 0.5f
    @JvmField var armorup = 12f

    override fun setStats() {
        super.setStats()
        stats.add(Stat("armorup-percent", StatCat.function), percent * 100, StatUnit.percent);
        stats.add(Stat("armorup", StatCat.function), armorup, StatUnit.none);
    }

    open inner class ArmorupWallBuild : WallBuild() {
        override fun handleDamage(amount: Float): Float {
            if (health / block.health <= percent) {
                return Damage.applyArmor(amount, armorup)
            }
            return amount
        }
    }
}