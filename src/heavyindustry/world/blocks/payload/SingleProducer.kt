package heavyindustry.world.blocks.payload

import arc.math.Mathf
import heavyindustry.util.nof
import mindustry.Vars
import mindustry.gen.Building
import mindustry.type.Item
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.blocks.payloads.BlockProducer
import mindustry.world.blocks.payloads.BuildPayload
import mindustry.world.consumers.ConsumeItemDynamic

open class SingleProducer(name: String) : BlockProducer(name) {
	@JvmField var produce: Block? = null

	init {
		consumeBuilder.add(ConsumeItemDynamic { tile: SingleProducerBuild -> nof(tile.tehRecipe, { it.requirements }, ItemStack.empty) })
	}

	open inner class SingleProducerBuild : BlockProducerBuild() {
		@JvmField var tehRecipe: Block? = null

		override fun recipe(): Block? = tehRecipe

		override fun acceptItem(source: Building, item: Item): Boolean = items[item] < getMaximumAccepted(item)

		override fun getMaximumAccepted(item: Item): Int {
			if (tehRecipe == null) return 0
			for (stack in tehRecipe!!.requirements) {
				if (stack.item === item) return stack.amount * 2
			}
			return 0
		}

		override fun shouldConsume(): Boolean = super.shouldConsume() && tehRecipe != null

		override fun updateTile() {
			if (tehRecipe == null && produce != null) tehRecipe = produce
			//super.updateTile()
			val recipe = tehRecipe
			val produce = recipe != null && efficiency > 0 && payload == null

			if (produce) {
				progress += buildSpeed * edelta()

				if (progress >= recipe!!.buildTime) {
					consume()
					payload = BuildPayload(recipe, team)
					payload.block().placeEffect.at(x, y, payload.size() / Vars.tilesize)
					payVector.setZero()
					progress %= 1f
				}
			}

			heat = Mathf.lerpDelta(heat, Mathf.num(produce).toFloat(), 0.15f)
			time += heat * delta()

			moveOutPayload()

			if (payload != null) {
				payload.update(null, this)
			}
		}
	}
}
