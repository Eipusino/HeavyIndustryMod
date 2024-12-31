package heavyindustry.entities.abilities

import arc.graphics.*
import arc.math.*
import arc.util.*
import mindustry.*
import mindustry.content.*
import mindustry.entities.abilities.*
import mindustry.gen.Unit
import mindustry.graphics.*

open class MendFieldAbility : Ability {
    @JvmField var baseColor: Color? = Color.valueOf("84f491")
    @JvmField var phaseColor: Color? = Color.valueOf("ffd59e")

    @JvmField var range = 180f
    @JvmField var reload = 60f
    @JvmField var healP = 10f

    @JvmField var timer = 0f

    constructor()

    constructor(range1: Float, reload1: Float, healP1: Float) {
        range = range1
        reload = reload1
        healP = healP1
    }

    override fun update(unit: Unit) {
        Vars.indexer.eachBlock(unit, range, { other -> other . damaged () },  { other ->
            timer += Time.delta
            if (timer >= reload) {
                timer = 0f
                other.heal((healP / 100) * other.block.health)
                Fx.healBlockFull.at(other.x, other.y, other.block.size.toFloat(), Tmp.c1.set(baseColor).lerp(phaseColor, 0.3f))
            }
        })
    }

    override fun draw(unit: Unit) {
        Vars.indexer.eachBlock(unit, range, { other -> other.damaged() },  { other ->
            val tmp = Tmp.c1.set(baseColor)
            tmp.a = Mathf.absin(4f, 1f)
            Drawf.selected(other, tmp)
        })
    }
}