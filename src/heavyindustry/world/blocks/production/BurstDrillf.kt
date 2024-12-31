package heavyindustry.world.blocks.production

import arc.math.*
import heavyindustry.util.Utils.*
import mindustry.*
import mindustry.content.*
import mindustry.entities.*
import mindustry.world.blocks.environment.*
import mindustry.world.blocks.production.*
import mindustry.world.meta.*

open class BurstDrillf(name: String) : BurstDrill(name) {
    @JvmField var itemCountPerGrid = 3f

    override fun setStats() {
        super.setStats()
        stats.add(Stat.drillTier, StatValues.drillables(drillTime / itemCountPerGrid, hardnessDrillMultiplier, (size * size).toFloat(), drillMultipliers) { f -> f is Floor && !f.wallOre && f.itemDrop != null && f.itemDrop.hardness <= tier && f.itemDrop != blockedItem && (Vars.indexer.isBlockPresent(f) || Vars.state.isMenu()) })
        stats.add(Stat.drillSpeed, 60 / drillTime * size * size / itemCountPerGrid, StatUnit.itemsSecond)
    }

    open inner class BurstDrillBuildf : BurstDrillBuild() {
        override fun update() {
            if (dominantItem == null) return
            if (invertTime > 0) invertTime -= delta() / invertedTime
            if (timer.get(0, 5f)) dump(eq(items.has(dominantItem), dominantItem, null))

            val perGrid = Math.max((itemCountPerGrid - hardnessDrillMultiplier * dominantItem.hardness).toInt(), 0)
            val drillTime = getDrillTime(dominantItem)

            smoothProgress = Mathf.lerpDelta(smoothProgress, progress / (drillTime - 20), 0.1f)

            if (items.total() <= itemCapacity - dominantItems * perGrid && dominantItems > 0 && efficiency > 0) {
                warmup = Mathf.approachDelta(warmup, progress / drillTime, 0.01f)

                var speed = efficiency
                if (liquidBoostIntensity > 1 && liquids.get(Liquids.water) >= 0.01f) {
                    speed *= liquidBoostIntensity * liquidBoostIntensity
                }

                timeDrilled += speedCurve.apply(progress / drillTime) * speed

                lastDrillSpeed = perGrid / drillTime * speed * dominantItems
                progress += delta() * speed
            } else {
                warmup = Mathf.approachDelta(warmup, 0f, 0.01f)
                lastDrillSpeed = 0f
                return
            }

            if (dominantItems > 0 && progress >= drillTime && items.total() < itemCapacity) {
                for (i in 0..dominantItems * perGrid) {
                    offload(dominantItem)
                }
                invertTime = 1f
                progress %= drillTime

                if (wasVisible && Vars.headless) {
                    Effect.shake(shake, shake, this)
                    drillSound.at(x, y, 1 + Mathf.range(drillSoundPitchRand), drillSoundVolume)
                    drillEffect.at(x + Mathf.range(drillEffectRnd), y + Mathf.range(drillEffectRnd), dominantItem.color)
                }
            }
        }
    }
}
