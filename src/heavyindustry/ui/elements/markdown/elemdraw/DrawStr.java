package heavyindustry.ui.elements.markdown.elemdraw;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.style.*;
import arc.util.*;
import arc.util.pooling.*;
import heavyindustry.ui.elements.markdown.*;
import heavyindustry.ui.elements.markdown.Markdown.*;

public class DrawStr extends DrawObj {
	String text;
	Font font;
	float scl;
	Color color;
	Drawable drawable;

	private FontCache cache;
	private GlyphLayout layout;

	//use get
	DrawStr() {}

	public static DrawStr get(Markdown owner, String str, Font font, Color color, float ox, float oy, float scl, Drawable background) {
		DrawStr res = Pools.obtain(DrawStr.class, DrawStr::new);
		res.parent = owner;
		res.text = str;
		res.font = font;
		res.offsetX = ox;
		res.offsetY = oy;
		res.scl = scl;
		res.color = color;
		res.drawable = background;

		Font.FontData data = font.getData();
		float lastScl = data.scaleX;
		data.setScale(scl);
		res.cache = font.newFontCache();
		res.layout = res.cache.setText(str,
				0, 0,
				0,
				Align.topLeft,
				false
		);
		data.setScale(lastScl);

		return res;
	}

	@Override
	protected void draw() {
		//Debugging Text Anchors
		//Fill.square(parent.x + offsetX, parent.y + parent.getHeight() + offsetY, 4, 45);
		cache.tint(tmp1.set(color).mul(Draw.getColor()));
		cache.setPosition(
				parent.x + offsetX,
				parent.y + parent.getHeight() + offsetY
		);

		if (drawable != null) {
			tmp2.set(Draw.getColor());

			drawable.draw(
					parent.x + offsetX - drawable.getLeftWidth(),
					parent.y + parent.getHeight() + offsetY - font.getLineHeight() - 2,
					layout.width + drawable.getLeftWidth() + drawable.getRightWidth(),
					font.getLineHeight() + 5
			);

			Draw.color(tmp2);
		}

		cache.draw();
	}

	@Override
	public void reset() {
		super.reset();
		text = null;
		font = null;
		scl = 0;
		color = null;
		drawable = null;
	}
}
