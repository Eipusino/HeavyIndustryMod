package heavyindustry.ai

import arc.util.*
import mindustry.*
import mindustry.content.*
import mindustry.entities.*
import mindustry.entities.units.*
import mindustry.gen.*

open class FirefighterAI : AIController {
    @JvmField var retreatDelay = 0f
    @JvmField var fleeRange = 0f
    @JvmField var retreatDst = 0f

    @JvmField var avoid: Teamc? = null
    @JvmField var retreatTimer = 0f

    constructor()

    constructor(retreatDelay: Float, fleeRange: Float, retreatDst: Float) {
        this.retreatDelay = retreatDelay
        this.fleeRange = fleeRange
        this.retreatDst = retreatDst
    }

    override fun updateTargeting() {
        val result: Bullet?
        val realRange = Math.max(unit.type.range, 800f)
        result = Groups.bullet.intersect(unit.x - realRange, unit.y - realRange, realRange * 2, realRange * 2).min({ b: Bullet -> b.type == Bullets.fireball }, { b: Bullet -> b.dst2(unit.x, unit.y) })
        if (result != null) {
            var enemyBuild: Building? = null
            Vars.indexer.allBuildings(result.x, result.y, 64f) { other ->
                if (other.team != unit.team) {
                    enemyBuild = other
                }
            }
            if (enemyBuild == null) target = result
        } else {
            super.updateTargeting()
        }
    }

    override fun updateMovement() {
        if (target != null) {
            var shoot = false
            if (target.within(unit, Math.max(unit.type.range, 80f))) {
                unit.aim(target)
                shoot = true
            }
            unit.controlWeapons(shoot)
        } else if (target == null) {
            unit.controlWeapons(false)
        }
        if (target != null) {
            if (!target.within(unit, unit.type.range)) {
                moveTo(target, unit.type.range)
            }
            unit.lookAt(target)
        }
        if (target == null) {
            if (timer.get(timerTarget4, 40f)) {
                avoid = Units.closestTarget(unit.team, unit.x, unit.y, fleeRange, { u -> u.checkTarget(true, true) }, { true })
            }
            if ((Time.delta.let { retreatTimer += it; retreatTimer }) >= retreatDelay) {
                if (avoid != null) {
                    val core = unit.closestCore()
                    if (core != null && !unit.within(core, retreatDst)) {
                        moveTo(core, retreatDst)
                    }
                }
            }
        } else {
            retreatTimer = 0f
        }
    }
}