package heavyindustry.world.blocks.power

import arc.math.Mathf
import arc.struct.EnumSet
import heavyindustry.util.eq
import mindustry.Vars
import mindustry.world.blocks.power.PowerGenerator
import mindustry.world.meta.Attribute
import mindustry.world.meta.Env
import mindustry.world.meta.StatUnit

open class LunarGenerator(name: String) : PowerGenerator(name) {
	init {
		flags = EnumSet.of()
		envEnabled = Env.any
	}

	override fun setStats() {
		super.setStats()
		stats.remove(generationType)
		stats.add(generationType, powerProduction * 60.0f, StatUnit.powerSecond)
	}

	open inner class LunarGeneratorBuild : GeneratorBuild() {
		override fun updateTile() {
			productionEfficiency = eq(enabled, Mathf.maxZero(
				Attribute.light.env() +
						eq(Vars.state.rules.lighting, 1f + Vars.state.rules.ambientLight.a, 1f)
			), 0f)
		}
	}
}
