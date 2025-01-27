package heavyindustry.world.blocks.distribution

import arc.graphics.*
import arc.graphics.g2d.*
import arc.math.*
import arc.math.geom.*
import arc.util.*
import heavyindustry.*
import heavyindustry.util.*
import heavyindustry.world.meta.*
import mindustry.*
import mindustry.graphics.*
import mindustry.world.blocks.Autotiler.*
import mindustry.world.meta.*

open class OverchargeDuct(name: String) : TubeDuct(name) {
	@JvmField var glowRegions = Array(5) { HIVars.whiteRegion }

	@JvmField var glowAlpha = 1f
	@JvmField var glowColor = Pal.redLight

	@JvmField var baseEfficiency = 0f

	init {
		noUpdateDisabled = false
	}

	override fun setStats() {
		super.setStats()
		//stats.add(HIStat.itemsMovedBase, 60f / speed, StatUnit.itemsSecond)
		stats.add(HIStat.itemsMovedBoost, 60f / (speed / (1f + (baseEfficiency * 2f))), StatUnit.itemsSecond)
	}

	override fun load() {
		super.load()
		glowRegions = Utils.split("$name-glow", 32, 0)
	}

	open inner class OverchargeDuctBuild : TubeDuctBuild() {
		override fun draw() {
			val protege = rotdeg()

			//draw extra ducts facing this one for tiling purposes
			for (i in 0..3) {
				if ((blending and (1 shl i)) != 0) {
					val dir = rotation - i
					val rot = eq(i == 0, protege, dir * 90f)
					//var near = nearby(dir)
					drawAtWithGlow(x + Geometry.d4x(dir) * Vars.tilesize * 0.75f, y + Geometry.d4y(dir) * Vars.tilesize * 0.75f, 0, rot, eq(i != 0, SliceMode.bottom, SliceMode.top))
				}
			}

			//draw item
			if (current != null) {
				Draw.z(Layer.blockUnder + 0.1f)
				Tmp.v1.set(Geometry.d4x(recDir) * Vars.tilesize / 2f, Geometry.d4y(recDir) * Vars.tilesize / 2f)
					.lerp(
						Geometry.d4x(rotation) * Vars.tilesize / 2f, Geometry.d4y(rotation) * Vars.tilesize / 2f,
						Mathf.clamp((progress + 1f) / 2f)
					)

				Draw.rect(current.fullIcon, x + Tmp.v1.x, y + Tmp.v1.y, Vars.itemSize, Vars.itemSize)
			}

			Draw.scl(xscl.toFloat(), yscl.toFloat())

			//drawAt(x, y, blendbits, rotation, SliceMode.none)
			drawAtWithGlow(x, y, blendbits, protege, SliceMode.none)
			Draw.reset()
		}

		protected open fun drawAtWithGlow(x: Float, y: Float, bits: Int, rotation: Float, slice: SliceMode) {
			Draw.z(Layer.blockUnder)
			Draw.rect(sliced(botRegions[bits], slice), x, y, rotation)

			Draw.z(Layer.blockUnder + 0.2f)
			Draw.color(transparentColor)
			Draw.rect(sliced(botRegions[bits], slice), x, y, rotation)
			Draw.color()
			Draw.rect(sliced(topRegions[bits], slice), x, y, rotation)

			if (sliced(glowRegions[bits], slice).found() && power != null && power.status > 0f && slice == SliceMode.none) {
				Draw.z(Layer.blockAdditive)
				Draw.color(glowColor, glowAlpha * power.status)
				Draw.blend(Blending.additive)
				Draw.rect(sliced(glowRegions[bits], slice), x, y, rotation)
				Draw.blend()
				Draw.color()
			}
		}

		override fun updateTile() {
			val eff = eq(power.status > 0f, power.status + baseEfficiency, 1f)
			progress += delta() * eff / speed * 2f
			super.updateTile()
		}
	}
}