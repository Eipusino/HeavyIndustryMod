package heavyindustry.entities.abilities

import arc.graphics.*
import arc.math.*
import arc.util.*
import mindustry.content.*
import mindustry.entities.*
import mindustry.entities.abilities.*
import mindustry.gen.Unit
import mindustry.type.*

open class ToxicAbility : Ability {
    @JvmField var damage = 1f
    @JvmField var reload = 60f
    @JvmField var range = 30f

    @JvmField var status: StatusEffect? = StatusEffects.disarmed

    @JvmField var i = 0f
    @JvmField var j = 60f

    constructor()

    constructor(damage1: Float, reload1: Float, range1: Float) {
        damage = damage1
        reload = reload1
        range = range1
    }

    override fun update(unit: Unit) {
        i += Time.delta
        j += Time.delta
        if (i >= reload) {
            Units.nearby(null, unit.x, unit.y, range) { other ->
                other.health -= damage
                other.apply(status, 60f * 15f)
            }
            Units.nearbyBuildings(unit.x, unit.y, range) { b ->
                b.health -= damage / 4f
                if (b.health <= 0f) {
                    b.kill()
                }
            }
            i = 0f
        }
        if (j >= 15f) {
            Fx.titanSmoke.at(
                unit.x + Mathf.range(range * 0.7071f),
                unit.y + Mathf.range(range * 0.7071f),
                Color.valueOf("92ab117f")
            )
            j -= 15f
        }
    }
}