package heavyindustry.world.blocks.storage

import arc.graphics.*
import arc.graphics.g2d.*
import mindustry.Vars.*
import mindustry.game.*
import mindustry.graphics.*
import mindustry.world.*
import mindustry.world.blocks.storage.*

open class FrontlineCoreBlock(name: String) : CoreBlock(name) {
    @JvmField var max = 3
    @JvmField var maxKill = false

    @JvmField var showLabel: String? = "oh no"
    @JvmField var showColor: Color? = Color.valueOf("ff5b5b")

    override fun canBreak(tile: Tile?): Boolean {
        return state.teams.cores(tile!!.team()).size > 1
    }

    override fun canReplace(other: Block?): Boolean {
        return other!!.alwaysReplace
    }

    override fun canPlaceOn(tile: Tile?, team: Team?, rotation: Int): Boolean {
        return state.teams.cores(team).size < max
    }

    open inner class FrontlineCoreBuild : CoreBuild() {
        @JvmField var kill = false
        @JvmField var num = 1
        @JvmField var time = 60 * num

        override fun update() {
            super.update()
            if (maxKill) {
                if (state.teams.cores(team).size > max + 3) kill = true
                if (kill) {
                    if (!headless) {
                        ui.showLabel(showLabel, 0.015f, x, y)
                    }
                    time--
                    if (time == 0) {
                        kill()
                    }
                }
            }
        }

        override fun draw() {
            super.draw()
            if (maxKill) {
                Draw.z(Layer.effect)
                Lines.stroke(2f, showColor)
                Draw.alpha((if (kill) 1 else if (state.teams.cores(team).size > max + 2) 1 else 0).toFloat())
                Lines.arc(x, y, 16f, (time * (6 / num) / 360).toFloat(), 90f)
            }
        }
    }
}