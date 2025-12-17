package heavyindustry.graphics;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Tmp;
import org.jetbrains.annotations.Contract;

public class EdgeLineStripDrawable extends FillStripDrawable {
	public float stroke;
	public Color lineColor;

	public EdgeLineStripDrawable(float str, Color lineCol) {
		this(str, lineCol, Color.clear);
	}

	public EdgeLineStripDrawable(float str, Color lineCol, Color col) {
		this(str, lineCol, col, col);
	}

	public EdgeLineStripDrawable(float str, Color lineCol, Color col, Color innCol) {
		super(col, innCol);

		stroke = str;
		lineColor = lineCol;
	}

	@Override
	public void draw(float originX, float originY, float angle, float distance, float angleDelta, float stripWidth) {
		super.draw(originX, originY, angle, distance, angleDelta, stripWidth);

		Draw.color(Tmp.c1.set(lineColor).mul(Draw.getColor()).toFloatBits());
		Lines.stroke(stroke);

		if (stripWidth <= 0) {
			Lines.arc(originX, originY, distance, angleDelta / 360f, angle);
		} else if (angleDelta <= 0) {
			float cos = Mathf.cosDeg(angle);
			float sin = Mathf.sinDeg(angle);
			float inOffX = distance * cos;
			float inOffY = distance * sin;
			float outOffX = (distance + stripWidth) * cos;
			float outOffY = (distance + stripWidth) * sin;

			Lines.line(originX + inOffX, originY + inOffY, originX + outOffX, originY + outOffY);
		} else {
			Drawh.circleFrame(originX, originY, distance, distance + stripWidth, angleDelta, angle, 72, true);
		}
	}

	@Contract(value = "_, _, _, _ -> new", pure = true)
	public EdgeLineStripDrawable tint(float str, Color lineCol, Color color, Color innerColor) {
		return new EdgeLineStripDrawable(str, lineCol, color, innerColor);
	}

	@Override
	@Contract(value = "_, _ -> new", pure = true)
	public EdgeLineStripDrawable tint(Color color, Color innerColor) {
		return new EdgeLineStripDrawable(stroke, lineColor, color, innerColor);
	}
}
