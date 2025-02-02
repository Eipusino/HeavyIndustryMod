package heavyindustry.ui.elements.markdown.elemdraw;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.util.*;
import arc.util.pooling.*;
import heavyindustry.ui.elements.markdown.*;
import heavyindustry.ui.elements.markdown.Markdown.*;

public class DrawImg extends DrawObj {
	String title;
	Font titleFont;
	Color titleColor;
	TextureRegion region;

	//use get
	DrawImg() {}

	public static DrawImg get(Markdown owner, TextureRegion region, String title, float offY, Font titleFont, Color titleColor) {
		DrawImg res = Pools.obtain(DrawImg.class, DrawImg::new);
		res.parent = owner;
		res.title = title;
		res.region = region;
		res.offsetX = 0;
		res.offsetY = offY;
		res.titleFont = titleFont;
		res.titleColor = titleColor;

		return res;
	}

	@Override
	public void reset() {
		super.reset();
		title = null;
		titleFont = null;
		titleColor = null;
		region = null;
	}

	@Override
	protected void draw() {
		float w = Math.min(parent.getWidth(), region.width), h = region.height * (w / region.width);

		Draw.rect(region, parent.x + w / 2, parent.y + parent.getHeight() + offsetY - h / 2, w, h);
		if (title != null)
			titleFont.draw(title, parent.x + w / 2, parent.y + parent.getHeight() + offsetY - h - 4, titleColor, 1, true, Align.center);
	}
}
