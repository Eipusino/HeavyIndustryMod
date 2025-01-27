package heavyindustry.world.blocks.distribution

import arc.graphics.g2d.*
import heavyindustry.util.*
import mindustry.world.blocks.distribution.*

open class TubeDuct(name: String) : Duct(name) {
	override fun load() {
		super.load()
		topRegions = Utils.split("$name-top", 32, 0)
		botRegions = Utils.split("$name-bot", 32, 0)
	}

	override fun icons(): Array<TextureRegion> = arrayOf(region)

	open inner class TubeDuctBuild : DuctBuild()//there's nothing to change...
}
