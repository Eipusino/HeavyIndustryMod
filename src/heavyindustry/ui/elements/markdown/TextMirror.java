package heavyindustry.ui.elements.markdown;

import arc.graphics.*;
import arc.graphics.g2d.*;

public class TextMirror {
	public final String text;
	public final Font font;
	public final Color fontColor;
	public final float offx, offy;
	public final float width, height;

	public TextMirror sub;

	TextMirror(String tex, Font fon, Color fonCol, float ofx, float ofy, float wid, float hei) {
		text = tex;
		font = fon;
		fontColor = fonCol;
		offx = ofx;
		offy = ofy;
		width = wid;
		height = hei;
	}
}
