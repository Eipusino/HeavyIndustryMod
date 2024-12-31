package heavyindustry.world.blocks.power

import arc.math.*
import arc.util.*
import heavyindustry.util.Utils.*
import mindustry.entities.*
import mindustry.world.blocks.power.*
import mindustry.world.meta.*

open class ConsumeVariableReactor(name: String) : VariableReactor(name) {
    @JvmField var itemDuration = 120f

    init {
        hasItems = true
    }

    override fun setStats() {
        super.setStats()
        stats.add(Stat.productionTime, itemDuration / 60, StatUnit.seconds)
    }

    open inner class ConsumeVariableReactorBuild : VariableReactorBuild() {
        @JvmField var consumeItemTimer = 0f

        override fun update() {
            super.update()

            heat = calculateHeat(sideHeat)
            productionEfficiency = efficiency
            warmup = Mathf.lerpDelta(warmup, eq(productionEfficiency > 0, 1f, 0f), warmupSpeed)
            if (instability >= 1) kill()

            totalProgress += productionEfficiency * Time.delta
            if (Mathf.chanceDelta((effectChance * warmup).toDouble())) {
                effect.at(x, y, effectColor)
                Damage.damage(team, x, y, 40f, 100f, true, true, true, true, null)
            }

            consumeItemTimer += Time.delta * efficiency
            if (efficiency > 0 && consumeItemTimer >= itemDuration) {
                consumeItemTimer %= itemDuration
                consume()
            }
        }
    }
}
