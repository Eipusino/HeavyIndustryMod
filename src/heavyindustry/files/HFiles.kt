package heavyindustry.files

import arc.Core
import arc.Graphics
import arc.files.Fi
import arc.graphics.Texture
import arc.graphics.g2d.NinePatch
import arc.graphics.g2d.PixmapRegion
import arc.graphics.g2d.TextureAtlas.AtlasRegion
import arc.graphics.g2d.TextureRegion
import arc.scene.style.Drawable
import arc.scene.style.ScaledNinePatchDrawable
import arc.util.Log
import heavyindustry.HVars.internalTree
import heavyindustry.core.HeavyIndustryMod.MOD_NAME

object HFiles {
	@JvmStatic private val filter = arrayOf("heavyindustry", "kotlin", "org", "META-INF", "classes.dex")
	@JvmStatic private val rootDirectory = HashMap<String, Fi>().apply {
		internalTree.root.list().forEach {
			if (!filter.contains(it.name()) && it.isDirectory) {
				put(it.name(), it)
			}
		}
	}
	@JvmStatic private val spritesIce = HashMap<String, Fi>().apply {
		rootDirectory["sprites-ice"]?.findAll {
			it.extension().equals("png")
		}?.forEach {
			val key = it.name()
			if (contains(key)) {
				Log.warn("Included files: ${get(it.name())?.path()},Not included:${it.path()}")
			} else {
				put(key, it)
			}
		}
	}
	@JvmStatic private val sprites = HashMap<String, Fi>().apply {
		rootDirectory["sprites"]?.findAll {
			it.extension().equals("png")
		}?.forEach {
			put(it.name(), it)
		}
	}
	@JvmStatic private val musics = HashMap<String, Fi>().apply {
		rootDirectory["music"]?.findAll { it.extension().equals("ogg") }?.forEach {
			put(it.name(), it)
		}
	}
	@JvmStatic private val sounds = HashMap<String, Fi>().apply {
		rootDirectory["sounds"]?.findAll { it.extension().equals("ogg") }?.forEach {
			put(it.name(), it)
		}
	}
	@JvmStatic private val shaders = HashMap<String, Fi>().apply {
		rootDirectory["shaders"]?.list()?.forEach {
			put(it.name(), it)
		}
	}

	@JvmStatic fun findSound(name: String) = sounds[name] ?: throw Exception("File not found: $name")
	@JvmStatic fun findMusics(name: String) = musics[name] ?: throw Exception("File not found: $name")
	@JvmStatic fun findShader(name: String) = shaders[name] ?: throw Exception("File not found: $name")

	@JvmStatic fun findDirectory(name: String) = rootDirectory[name]
	@JvmStatic fun findModRegion(name: String): AtlasRegion {
		val file = spritesIce["$name.png"] ?: throw Exception("File not found: $name")
		val texture = TextureRegion(Texture(file))
		val atlasRegion = AtlasRegion(texture)
		atlasRegion.offsetX = 0f
		atlasRegion.offsetY = 0f
		atlasRegion.x = 0
		atlasRegion.y = 0
		atlasRegion.name = "$MOD_NAME-$name"
		atlasRegion.packedWidth = texture.width
		atlasRegion.packedHeight = texture.height
		atlasRegion.originalWidth = texture.width
		atlasRegion.originalHeight = texture.height
		return atlasRegion
	}

	@JvmStatic fun findRegion(name: String): AtlasRegion {
		val file = sprites["$name.png"] ?: throw Exception("File not found: $name")
		val texture = TextureRegion(Texture(file))
		val atlasRegion = AtlasRegion(texture)
		atlasRegion.offsetX = 0f
		atlasRegion.offsetY = 0f
		atlasRegion.x = 0
		atlasRegion.y = 0
		atlasRegion.name = "$MOD_NAME-$name"
		atlasRegion.packedWidth = texture.width
		atlasRegion.packedHeight = texture.height
		atlasRegion.originalWidth = texture.width
		atlasRegion.originalHeight = texture.height
		return atlasRegion
	}

	/** Split the Nine Palaces Diagram  */
	@JvmStatic
	fun createNinePatch(name: String): Drawable {
		val texture = findModRegion("$name.9")
		val pixmapRegion = PixmapRegion(texture.texture.textureData.pixmap, 0, 0, texture.width, texture.height)
		val splits = getSplits(pixmapRegion)
		texture.set(1, 1, texture.width - 2, texture.height - 2)
		val copy = getScaledNinePatchDrawable(texture, splits!!)
		copy.minWidth = 0f
		copy.minHeight = 0f
		copy.topHeight = 0f
		copy.rightWidth = 0f
		copy.bottomHeight = 0f
		copy.leftWidth = 0f
		return copy
	}

	@JvmStatic
	fun newCursor(filename: String): Graphics.Cursor {
		val p = findModRegion(filename).texture.textureData.pixmap
		val result = Core.graphics.newCursor(p, p.width / 2, p.height / 2)
		p.dispose()
		return result
	}

	@JvmStatic
	private fun getScaledNinePatchDrawable(region: TextureRegion, ints: IntArray): ScaledNinePatchDrawable {
		return object : ScaledNinePatchDrawable(NinePatch(region, ints[0], ints[1], ints[2], ints[3])) {
			override fun getLeftWidth(): Float {
				return 0f
			}

			override fun getRightWidth(): Float {
				return 0f
			}

			override fun getTopHeight(): Float {
				return 0f
			}

			override fun getBottomHeight(): Float {
				return 0f
			}
		}
	}

	@JvmStatic
	private fun getSplits(raster: PixmapRegion): IntArray? {
		var startX = getSplitPoint(raster, 1, 0, startPoint = true, true)
		var endX = getSplitPoint(raster, startX, 0, startPoint = false, true)
		var startY = getSplitPoint(raster, 0, 1, startPoint = true, false)
		var endY = getSplitPoint(raster, 0, startY, startPoint = false, false)
		// Ensure pixels after the end are not invalid.
		getSplitPoint(raster, endX + 1, 0, startPoint = true, true)
		getSplitPoint(raster, 0, endY + 1, startPoint = true, false)
		// No splits, or all splits.
		if (startX == 0 && endX == 0 && startY == 0 && endY == 0) return null
		// Subtraction here is because the coordinates were computed before the 1px border was stripped.
		if (startX != 0) {
			startX--
			endX = raster.width - 2 - (endX - 1)
		} else {
			// If no start point was ever found, we assume full stretch.
			endX = raster.width - 2
		}
		if (startY != 0) {
			startY--
			endY = raster.height - 2 - (endY - 1)
		} else {
			// If no start point was ever found, we assume full stretch.
			endY = raster.height - 2
		}

		return intArrayOf(startX, endX, startY, endY)
	}

	@JvmStatic
	private fun getSplitPoint(raster: PixmapRegion, startX: Int, startY: Int, startPoint: Boolean, xAxis: Boolean, ): Int {
		var next = if (xAxis) startX else startY
		val end = if (xAxis) raster.width else raster.height
		val breakA = if (startPoint) 255 else 0
		var x = startX
		var y = startY
		while (next != end) {
			if (xAxis) x = next
			else y = next
			val a = raster.getA(x, y)
			if (a == breakA) return next

			next++
		}

		return 0
	}
}