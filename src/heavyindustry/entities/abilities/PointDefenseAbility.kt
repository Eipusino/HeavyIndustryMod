package heavyindustry.entities.abilities

import arc.*
import arc.graphics.*
import arc.graphics.g2d.*
import arc.math.*
import arc.util.*
import mindustry.content.*
import mindustry.entities.abilities.*
import mindustry.gen.*
import mindustry.gen.Unit

open class PointDefenseAbility : Ability {
    @JvmField var px = 0f
    @JvmField var py = 0f
    @JvmField var reload = 60f
    @JvmField var range = 180f
    @JvmField var bulletDamage = 1f
    @JvmField var suffix = "-full"

    @JvmField var color: Color? = Color.white
    @JvmField var target: Bullet? = null
    @JvmField var rotation = 90f
    @JvmField var timer = 90f
    @JvmField var reloadTimer = 60f

    constructor()

    constructor(px1: Float, py1: Float, reloadTimer1: Float, range1: Float, bulletDamage1: Float, sprite1: String) {
        px = px1
        py = py1
        reloadTimer = reloadTimer1
        range = range1
        bulletDamage = bulletDamage1
        suffix = sprite1
    }

    override fun localized(): String {
        return Core.bundle.get("ability.point-defense")
    }

    override fun update(unit: Unit) {
        val x = unit.x + Angles.trnsx(unit.rotation, py, px)
        val y = unit.y + Angles.trnsy(unit.rotation, py, px)
        target = Groups.bullet.intersect(unit.x - range, unit.y - range, range * 2, range * 2).min({ b -> b.team != unit.team && b.type.hittable }, { b -> b.dst2(unit) })

        if (target != null && !target!!.isAdded()) {
            target = null
        }
        if (target == null) {
            if (timer >= 90) {
                rotation = Angles.moveToward(rotation, unit.rotation, 3f)
            } else {
                timer += Time.delta
            }
        }
        if (target != null && target!!.within(unit, range) && target!!.team != unit.team && target!!.type != null && target!!.type.hittable) {
            timer = 0f
            reload += Time.delta
            //val dest = unit!!.angleTo(target)
            val dest = target!!.angleTo(x, y) - 180
            rotation = Angles.moveToward(rotation, dest, 20f)
            if (Angles.within(rotation, dest, 3f) && reload >= reloadTimer) {
                if (target!!.damage > bulletDamage) {
                    target!!.damage -= bulletDamage
                } else {
                    target!!.remove()
                }
                Tmp.v1.trns(rotation, 6f)
                Fx.pointBeam.at(x + Tmp.v1.x, y + Tmp.v1.y, rotation, color, target)
                Fx.sparkShoot.at(x + Tmp.v1.x, y + Tmp.v1.y, rotation, color)
                Fx.pointHit.at(target!!.x, target!!.y, color)
                Sounds.lasershoot.at(x, y, Mathf.random(0.9f, 1.1f))
                reload = 0f
            }
        }
    }

    override fun draw(unit: Unit) {
        val x = unit.x + Angles.trnsx(unit.rotation, py, px)
        val y = unit.y + Angles.trnsy(unit.rotation, py, px)
        val region = Core.atlas.find("creators$suffix")
        Draw.rect(region, x, y, rotation - 90f)
    }
}