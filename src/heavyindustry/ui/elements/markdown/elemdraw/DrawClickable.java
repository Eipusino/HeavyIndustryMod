package heavyindustry.ui.elements.markdown.elemdraw;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.*;
import arc.scene.ui.*;
import arc.util.pooling.*;
import heavyindustry.ui.elements.markdown.*;
import heavyindustry.ui.elements.markdown.Markdown.*;
import mindustry.ui.*;

public class DrawClickable extends DrawStr implements ActivityDrawer {
	TextButton openUrl;

	//use get
	DrawClickable() {}

	public static DrawClickable get(Markdown owner, String str, Font strFont, Runnable clicked, Tooltip tooltip, Color color, float ox, float oy, float scl) {
		DrawClickable res = Pools.obtain(DrawClickable.class, DrawClickable::new);
		res.parent = owner;
		res.text = str;
		res.openUrl = new TextButton(str, new TextButton.TextButtonStyle(Styles.nonet) {{
			fontColor = color;
			font = strFont;
		}}) {{
			clicked(clicked);
			label.setScale(scl);
			label.setWrap(false);

			if (tooltip != null) addListener(tooltip);
		}};
		res.offsetX = ox;
		res.offsetY = oy;
		res.scl = scl;
		res.color = color;

		return res;
	}

	@Override
	protected void draw() {
	}

	@Override
	public Element getElem() {
		return openUrl;
	}

	@Override
	public float width() {
		return openUrl.getLabel().getPrefWidth();
	}

	@Override
	public float height() {
		return openUrl.getLabel().getPrefHeight();
	}

	@Override
	public void reset() {
		super.reset();
		openUrl = null;
	}
}
