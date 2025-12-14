package heavyindustry.graphics;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.util.Tmp;

public class FillStripDrawable extends BaseStripDrawable {
	public Color color, innerColor;

	public FillStripDrawable(Color col) {
		this(col, col);
	}

	public FillStripDrawable(Color col, Color innCol) {
		color = col;
		innerColor = innCol;
	}

	@Override
	public void draw(float originX, float originY, float angle, float distance, float angleDelta, float stripWidth) {
		Drawh.circleStrip(originX, originY, distance, distance + stripWidth, angleDelta, angle, Tmp.c1.set(innerColor).mul(Draw.getColor()), Tmp.c2.set(color).mul(Draw.getColor()), 72);
	}

	public FillStripDrawable tint(Color color, Color innerColor) {
		return new FillStripDrawable(color, innerColor);
	}
}
