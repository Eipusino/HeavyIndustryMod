package heavyindustry.world.blocks.defense

import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import mindustry.gen.Building
import mindustry.gen.Unit
import mindustry.world.Block
import mindustry.world.meta.Stat
import mindustry.world.meta.StatUnit

/**
 * Causing damage to units walking on it.
 *
 * @author Eipusino
 */
open class Thorns(name: String) : Block(name) {
	@JvmField val timerDamage = timers++

	@JvmField var cooldown = 30f
	@JvmField var damage = 8f

	@JvmField var damaged = true
	@JvmField var damagedMultiplier = 0.3f

	override fun setStats() {
		super.setStats()
		stats.add(Stat.damage, 60f / cooldown * damage, StatUnit.perSecond)
	}

	open inner class ThornsBuild : Building() {
		override fun draw() {
			Draw.color(team.color)
			Draw.alpha(0.22f)
			Fill.rect(x, y, 2f, 2f)
			Draw.color()
		}

		override fun unitOn(unit: Unit) {
			if (timer[timerDamage, cooldown]) {
				unit.damage(damage)
				if (damaged) damage(damage * damagedMultiplier)
			}
		}
	}
}
