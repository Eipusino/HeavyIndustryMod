package heavyindustry.ai

import arc.math.*
import arc.math.geom.*
import mindustry.*
import mindustry.ai.types.*
import mindustry.content.*
import mindustry.gen.*

/**
 * Around Rotating AI
 * @author Alon
 */
open class CircleAi : FlyingAI() {
    @JvmField var maxHealth = false

    /** Surrounding radius. */
    @JvmField var rand = 0f

    /** timer. */
    @JvmField var timer2 = 0
    @JvmField var reversal = false

    /** inversion time. */
    @JvmField var reversalTime = Rand().random(10, 20)

    override fun init() {
        rand = Rand().random(32f, unit.range())
    }

    override fun circle(target: Position?, circleLength: Float) {
        circle(target, circleLength, unit.speed())
    }

    override fun circle(target: Position?, circleLength: Float, speed: Float) {
        if (target == null) return
        timer2++
        if (timer2 / 60 >= reversalTime) {
            timer2 = 0
            reversal = !reversal
        }
        vec.set(target).sub(unit)
        if (vec.len() < circleLength) {
            vec.rotate((if (reversal) -1 else 1) * (circleLength - vec.len()) / circleLength * 180f)
        }
        vec.setLength(speed)
        unit.moveAt(vec)
    }

    override fun updateMovement() {
        unloadPayloads()
        if (maxHealth) {
            if (target == null) {
                //Find a target.
                target = Groups.unit.find { it.team != unit.team }
            } else if (unit.health <= unit.maxHealth * 0.2) {
                //Determine health value.
                maxHealth = false
            }
        } else {
            target = null
        }

        if (target != null && unit.hasWeapons()) {
            //Attack if there is a target.
            circle(target, rand)
        }
        //Return without a goal.
        if (target == null && Vars.state.rules.waves) {
            val find = Groups.build.find { it.block == Blocks.repairTurret || it.block == Blocks.repairPoint }
            val core = unit.team.core()
            if (unit.health == unit.maxHealth || find == null) {
                moveTo(core, Vars.state.rules.dropZoneRadius + 130f)
                maxHealth = true
            } else {
                moveTo(find, 32f)
            }
        }
    }
}