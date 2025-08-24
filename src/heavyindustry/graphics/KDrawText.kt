@file:JvmName("KDrawText")

package heavyindustry.graphics

import arc.graphics.Color
import arc.graphics.g2d.Draw
import arc.graphics.g2d.Fill
import arc.graphics.g2d.Font
import arc.graphics.g2d.GlyphLayout
import arc.math.geom.Position
import arc.scene.ui.layout.Scl
import arc.util.Align
import arc.util.Tmp
import arc.util.pooling.Pools
import mindustry.graphics.Drawf
import mindustry.ui.Fonts

/**
 * @param fontScl Font size, relative to world size, approximately one square. If you want to draw
 *                according to the UI, use [Scl.scl].
 */
@JvmOverloads
fun drawText(
	pos: Position, text: String,
	fontScl: Float = 1f, color: Color = Color.white, anchor: Int = Align.center,
	font: Font = Fonts.outline, background: Boolean = false,
) {
	val p = Tmp.v1.set(pos)
	// Reference source mindustry.gen.WorldLabel.drawAt
	val z = Drawf.text()
	val ints = font.usesIntegerPositions()
	font.setUseIntegerPositions(false)
	font.data.setScale(0.25f / Scl.scl() * fontScl)
	font.color = color

	Pools.obtain(GlyphLayout::class.java, ::GlyphLayout).apply {
		setText(font, text)

		if (Align.isCenterVertical(anchor)) p.y += height / 2
		else if (Align.isBottom(anchor)) p.y += height
		var centerX = p.x
		if (Align.isLeft(anchor)) centerX += width / 2
		else if (Align.isRight(anchor)) centerX -= width / 2
		if (background) {
			Draw.color(Color.black, 0.3f)
			Fill.rect(centerX, p.y - height / 2, width + 2, height + 3)
			Draw.color()
		}
	}.let(Pools::free)
	val align = if (Align.isCenterHorizontal(anchor)) anchor or Align.center else anchor
	// This time x is the top of the drawing area
	font.draw(text, p.x, p.y, 0f, align, false)
	Draw.reset()

	font.color.set(Color.white)
	font.data.setScale(1f)
	font.setUseIntegerPositions(ints)
	Draw.z(z)
}
