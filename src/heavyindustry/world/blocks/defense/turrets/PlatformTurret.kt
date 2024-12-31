package heavyindustry.world.blocks.defense.turrets

import arc.struct.*
import mindustry.content.*
import mindustry.entities.bullet.*
import mindustry.gen.*
import mindustry.world.blocks.defense.turrets.*
import mindustry.world.meta.*

/**
 * Suitable for sandbox turrets without any consumption.
 *
 * @author Eipusino
 */
open class PlatformTurret(name: String) : Turret(name) {
    @JvmField var shootType: BulletType? = Bullets.placeholder
    @JvmField var destructible2: Boolean = true

    override fun setStats() {
        super.setStats()
        stats.add(Stat.ammo, StatValues.ammo(ObjectMap.of(this, shootType)))
    }

    open inner class PlatformTurretBuild : TurretBuild() {
        override fun damage(damage: Float) {
            if (destructible2) super.damage(damage)
        }

        override fun handleDamage(amount: Float): Float {
            return if (destructible2) super.handleDamage(amount) else 0f
        }

        override fun collision(bullet: Bullet?): Boolean {
            val collision = super.collision(bullet)
            return if (destructible2) collision else true
        }

        override fun useAmmo(): BulletType? {
            return shootType
        }

        override fun hasAmmo(): Boolean {
            return true
        }

        override fun peekAmmo(): BulletType? {
            return shootType
        }
    }
}
