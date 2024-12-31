package heavyindustry.world.blocks.storage

import heavyindustry.util.*
import mindustry.content.*
import mindustry.game.*
import mindustry.graphics.*
import mindustry.type.Item
import mindustry.world.Block
import mindustry.world.blocks.defense.turrets.*
import mindustry.world.blocks.payloads.*
import mindustry.world.blocks.storage.*

open class TurretCoreBlock(name: String) : CoreBlock(name) {
    @JvmField var turret: Block? = Blocks.duo
    @JvmField var ammo: Array<Item?>? = arrayOf(Items.copper)

    open inner class TurretCoreBuild : CoreBuild() {
        @JvmField val payload = BuildPayload(turret, Team.derelict)

        override fun update() {
            super.update()
            if (payload.build.team != team) {
                payload.build.team = team
            }
            payload.update(null, this)
            for (i in ammo!!) {
                if (team.core().items.get(i) >= 1) {
                    if (payload.build.acceptItem(this, i)) {
                        team.core().items.remove(i, 1)
                        payload.build.handleItem(this, i)
                    }
                    break
                }
            }
            payload.set(x, y, payload.build.payloadRotation)
        }

        override fun draw() {
            super.draw()
            payload.draw()
        }

        override fun drawSelect() {
            super.drawSelect()
            eqc(turret) { t: BaseTurret -> Drawf.dashCircle(x, y, t.range, Pal.accent) }
        }
    }
}