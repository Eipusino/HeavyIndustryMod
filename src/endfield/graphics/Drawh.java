package endfield.graphics;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import kotlin.Pair;
import org.jetbrains.annotations.Contract;

import static endfield.graphics.Drawn.v1;
import static endfield.graphics.Drawn.v2;
import static endfield.graphics.Drawn.v3;
import static endfield.graphics.Drawn.v4;
import static endfield.graphics.Drawn.v5;
import static endfield.util.Arrays2.arrayOf;

public final class Drawh {
	static float[] circleOffset24;
	static float[] circleOffset36;
	static float[] circleOffset60;

	static float[] circleVertices24;
	static float[] circleVertices36;
	static float[] circleVertices60;

	static Pair<float[], float[]>[] verts;

	static {
		circleOffset24 = prepareCircleOffset(24);
		circleOffset36 = prepareCircleOffset(36);
		circleOffset60 = prepareCircleOffset(60);

		circleVertices24 = prepareCircleVertices(24);
		circleVertices36 = prepareCircleVertices(36);
		circleVertices60 = prepareCircleVertices(60);

		verts = arrayOf(new Pair<>(circleOffset24, circleVertices24), new Pair<>(circleOffset36, circleVertices36), new Pair<>(circleOffset60, circleVertices60));
	}

	private Drawh() {}

	@Contract(value = "_ -> new", pure = true)
	static float[] prepareCircleOffset(int sides) {
		float[] vertices = new float[sides * 4];
		float step = 360f / sides;

		for (int i = 0; i < sides; i++) {
			float angle = i * step;
			float angle1 = (i + 1) * step;
			vertices[i * 4] = Mathf.cosDeg(angle);
			vertices[i * 4 + 1] = Mathf.sinDeg(angle);
			vertices[i * 4 + 2] = Mathf.cosDeg(angle1);
			vertices[i * 4 + 3] = Mathf.sinDeg(angle1);
		}
		return vertices;
	}

	@Contract(value = "_ -> new", pure = true)
	static float[] prepareCircleVertices(int sides) {
		float[] vertices = new float[sides * 24];
		TextureRegion region = Core.atlas.white();
		float mcolor = Color.clearFloatBits;
		float u = region.u;
		float v = region.v;

		for (int i = 0; i < sides; i++) {
			int off = i * 24;
			vertices[off + 3] = u;
			vertices[off + 4] = v;
			vertices[off + 5] = mcolor;
			vertices[off + 9] = u;
			vertices[off + 10] = v;
			vertices[off + 11] = mcolor;
			vertices[off + 15] = u;
			vertices[off + 16] = v;
			vertices[off + 17] = mcolor;
			vertices[off + 21] = u;
			vertices[off + 22] = v;
			vertices[off + 23] = mcolor;
		}

		return vertices;
	}

	public static void fillCircle(float x, float y, float radius) {
		fillCircle(x, y, radius, 2);
	}

	public static void fillCircle(float x, float y, float radius, int level) {
		float color = Draw.getColorPacked();

		Pair<float[], float[]> vert = verts[level & 3];

		float[] offset = vert.getFirst();
		float[] vertices = vert.getSecond();
		int sides = offset.length / 4;

		for (int i = 0; i < sides; i++) {
			int idx = i * 24;
			float offX1 = offset[i * 4];
			float offY1 = offset[i * 4 + 1];
			float offX2 = offset[i * 4 + 2];
			float offY2 = offset[i * 4 + 3];
			float dxi1 = x + offX1 * radius;
			float dyi1 = y + offY1 * radius;
			float dxi2 = x + offX2 * radius;
			float dyi2 = y + offY2 * radius;

			vertices[idx] = dxi1;
			vertices[idx + 1] = dyi1;
			vertices[idx + 2] = color;

			vertices[idx + 6] = x;
			vertices[idx + 7] = y;
			vertices[idx + 8] = color;

			vertices[idx + 12] = x;
			vertices[idx + 13] = y;
			vertices[idx + 14] = color;

			vertices[idx + 18] = dxi2;
			vertices[idx + 19] = dyi2;
			vertices[idx + 20] = color;
		}

		Draw.vert(Core.atlas.white().texture, vertices, 0, vertices.length);
	}

	public static void innerCircle(float x, float y, float innerRadius, float radius, Color innerColor, Color color) {
		innerCircle(x, y, innerRadius, radius, innerColor, color, 2);
	}

	public static void innerCircle(float x, float y, float innerRadius, float radius, Color innerColor, Color color, int level) {
		float c1 = innerColor.toFloatBits();
		float c2 = color.toFloatBits();

		Pair<float[], float[]> vert = verts[level & 3];

		float[] offset = vert.getFirst();
		float[] vertices = vert.getSecond();

		int sides = offset.length / 4;

		for (int i = 0; i < sides; i++) {
			int idx = i * 24;
			float offX1 = offset[i * 4];
			float offY1 = offset[i * 4 + 1];
			float offX2 = offset[i * 4 + 2];
			float offY2 = offset[i * 4 + 3];
			float dxi1 = x + offX1 * innerRadius;
			float dyi1 = y + offY1 * innerRadius;
			float dxo1 = x + offX1 * radius;
			float dyo1 = y + offY1 * radius;
			float dxi2 = x + offX2 * innerRadius;
			float dyi2 = y + offY2 * innerRadius;
			float dxo2 = x + offX2 * radius;
			float dyo2 = y + offY2 * radius;

			vertices[idx] = dxi1;
			vertices[idx + 1] = dyi1;
			vertices[idx + 2] = c1;

			vertices[idx + 6] = dxo1;
			vertices[idx + 7] = dyo1;
			vertices[idx + 8] = c2;

			vertices[idx + 12] = dxo2;
			vertices[idx + 13] = dyo2;
			vertices[idx + 14] = c2;

			vertices[idx + 18] = dxi2;
			vertices[idx + 19] = dyi2;
			vertices[idx + 20] = c1;
		}

		Draw.vert(Core.atlas.white().texture, vertices, 0, vertices.length);
	}

	public static void lineCircle(float x, float y, float radius) {
		lineCircle(x, y, radius, 2);
	}

	public static void lineCircle(float x, float y, float radius, int level) {
		float stroke = Lines.getStroke();
		float color = Draw.getColorPacked();

		Pair<float[], float[]> vert = verts[level & 3];

		float[] offset = vert.getFirst();
		float[] vertices = vert.getSecond();

		int sides = offset.length / 4;

		for (int i = 0; i < sides; i++) {
			int idx = i * 24;
			float offX1 = offset[i * 4];
			float offY1 = offset[i * 4 + 1];
			float offX2 = offset[i * 4 + 2];
			float offY2 = offset[i * 4 + 3];
			float dxi1 = x + offX1 * (radius + stroke);
			float dyi1 = y + offY1 * (radius + stroke);
			float dxo1 = x + offX1 * (radius - stroke);
			float dyo1 = y + offY1 * (radius - stroke);
			float dxi2 = x + offX2 * (radius + stroke);
			float dyi2 = y + offY2 * (radius + stroke);
			float dxo2 = x + offX2 * (radius - stroke);
			float dyo2 = y + offY2 * (radius - stroke);

			vertices[idx] = dxi1;
			vertices[idx + 1] = dyi1;
			vertices[idx + 2] = color;

			vertices[idx + 6] = dxo1;
			vertices[idx + 7] = dyo1;
			vertices[idx + 8] = color;

			vertices[idx + 12] = dxo2;
			vertices[idx + 13] = dyo2;
			vertices[idx + 14] = color;

			vertices[idx + 18] = dxi2;
			vertices[idx + 19] = dyi2;
			vertices[idx + 20] = color;
		}

		Draw.vert(Core.atlas.white().texture, vertices, 0, vertices.length);
	}

	public static void arc(float x, float y, float radius, float innerAngel, float rotate) {
		arc(x, y, radius, innerAngel, rotate, 0.8f);
	}

	public static void arc(float x, float y, float radius, float innerAngel, float rotate, float scaleFactor) {
		int sides = 40 + (int) (radius * scaleFactor);

		float step = 360f / sides;
		int sing = innerAngel > 0 ? 1 : -1;
		float inner = Math.min(Math.abs(innerAngel), 360f);

		int n = (int) (inner / step);
		float rem = inner - n * step;
		Lines.beginLine();

		v1.set(radius, 0f).setAngle(rotate);
		Lines.linePoint(x + v1.x, y + v1.y);

		for (int i = 0; i < n; i++) {
			v1.set(radius, 0f).setAngle((i + 1) * step * sing + rotate);
			Lines.linePoint(x + v1.x, y + v1.y);
		}

		if (rem > 0.1f) {
			v1.set(radius, 0f).setAngle(inner * sing + rotate);
			Lines.linePoint(x + v1.x, y + v1.y);
		}

		Lines.endLine(inner >= 360f - 0.01f);
	}

	public static void dashCircle(float x, float y, float radius) {
		dashCircle(x, y, radius, 8, 180f, 0f);
	}

	public static void dashCircle(float x, float y, float radius, int dashes, float totalDashDeg, float rotate) {
		if (Mathf.equal(totalDashDeg, 0f)) return;

		float totalTransDeg = 360f - totalDashDeg;
		float dashDeg = totalDashDeg / dashes;
		float transDeg = totalTransDeg / dashes;
		float step = dashDeg + transDeg;
		int sides = (int) (360 / dashDeg) * 2;

		for (int i = 0; i < dashes; i++) {
			Lines.arc(x, y, radius, dashDeg / 360f, rotate + i * step, sides);
		}
	}

	public static void circleFan(float x, float y, float radius, float angle) {
		circleFan(x, y, radius, angle, 0f, 72);
	}

	public static void circleFan(float x, float y, float radius, float angle, float rotate, int sides) {
		float step = 360f / sides;
		int s = (int) (angle / 360 * sides);

		float rem = angle - s * step;

		for (int i = 0; i < s; i++) {
			float offX1 = Mathf.cosDeg(rotate + i * step);
			float offY1 = Mathf.sinDeg(rotate + i * step);
			float offX2 = Mathf.cosDeg(rotate + (i + 1) * step);
			float offY2 = Mathf.sinDeg(rotate + (i + 1) * step);

			v1.set(offX1, offY1).scl(radius).add(x, y);
			v2.set(offX2, offY2).scl(radius).add(x, y);

			Fill.quad(x, y, v1.x, v1.y, v2.x, v2.y, x, y);
		}

		if (rem > 0) {
			float offX1 = Mathf.cosDeg(rotate + s * step);
			float offY1 = Mathf.sinDeg(rotate + s * step);
			float offX2 = Mathf.cosDeg(rotate + angle);
			float offY2 = Mathf.sinDeg(rotate + angle);

			v1.set(offX1, offY1).scl(radius).add(x, y);
			v2.set(offX2, offY2).scl(radius).add(x, y);

			Fill.quad(x, y, v1.x, v1.y, v2.x, v2.y, x, y);
		}
	}

	public static void circleStrip(float x, float y, float innerRadius, float radius, float angle) {
		Color color = Draw.getColor();
		circleStrip(x, y, innerRadius, radius, angle, 0f, color, color, 72);
	}

	public static void circleStrip(float x, float y, float innerRadius, float radius, float angle, float rotate, Color innerColor, Color outerColor, int sides) {
		float step = 360f / sides;
		int s = (int) (angle / 360 * sides);
		float innerC = innerColor.toFloatBits();
		float outerC = outerColor.toFloatBits();

		float rem = angle - s * step;

		for (int i = 0; i < s; i++) {
			float offX1 = Mathf.cosDeg(rotate + i * step);
			float offY1 = Mathf.sinDeg(rotate + i * step);
			float offX2 = Mathf.cosDeg(rotate + (i + 1) * step);
			float offY2 = Mathf.sinDeg(rotate + (i + 1) * step);

			Vec2 inner1 = v1.set(offX1, offY1).scl(innerRadius).add(x, y);
			Vec2 inner2 = v2.set(offX2, offY2).scl(innerRadius).add(x, y);
			Vec2 out1 = v3.set(offX1, offY1).scl(radius).add(x, y);
			Vec2 out2 = v4.set(offX2, offY2).scl(radius).add(x, y);

			Fill.quad(inner1.x, inner1.y, innerC, inner2.x, inner2.y, innerC, out2.x, out2.y, outerC, out1.x, out1.y, outerC);
		}

		if (rem > 0) {
			float offX1 = Mathf.cosDeg(rotate + s * step);
			float offY1 = Mathf.sinDeg(rotate + s * step);
			float offX2 = Mathf.cosDeg(rotate + angle);
			float offY2 = Mathf.sinDeg(rotate + angle);

			Vec2 inner1 = v1.set(offX1, offY1).scl(innerRadius).add(x, y);
			Vec2 inner2 = v2.set(offX2, offY2).scl(innerRadius).add(x, y);
			Vec2 out1 = v3.set(offX1, offY1).scl(radius).add(x, y);
			Vec2 out2 = v4.set(offX2, offY2).scl(radius).add(x, y);

			Fill.quad(inner1.x, inner1.y, innerC, inner2.x, inner2.y, innerC, out2.x, out2.y, outerC, out1.x, out1.y, outerC);
		}
	}

	public static void circleFrame(float x, float y, float innerRadius, float radius, float angle) {
		circleFrame(x, y, innerRadius, radius, angle, 0f, 72, false);
	}

	public static void circleFrame(float x, float y, float innerRadius, float radius, float angle, float rotate, int sides, boolean padCap) {
		float offX1 = Mathf.cosDeg(rotate);
		float offY1 = Mathf.sinDeg(rotate);
		float offX2 = Mathf.cosDeg(rotate + angle);
		float offY2 = Mathf.sinDeg(rotate + angle);

		Vec2 inner1;
		Vec2 inner2;
		Vec2 out1;
		Vec2 out2;

		if (padCap) {
			float off = Lines.getStroke() / 2f;
			v5.set(off, off).rotate(rotate);
			inner1 = v1.set(offX1, offY1).scl(innerRadius).add(v5).add(x, y);
			v5.set(-off, off).rotate(rotate);
			out1 = v3.set(offX1, offY1).scl(radius).add(v5).add(x, y);
			v5.set(off, -off).rotate(rotate + angle);
			inner2 = v2.set(offX2, offY2).scl(innerRadius).add(v5).add(x, y);
			v5.set(-off, -off).rotate(rotate + angle);
			out2 = v4.set(offX2, offY2).scl(radius).add(v5).add(x, y);

			Lines.arc(x, y, innerRadius + off, angle / 360f, rotate, sides);
			Lines.arc(x, y, radius - off, angle / 360f, rotate, sides);
		} else {
			inner1 = v1.set(offX1, offY1).scl(innerRadius).add(x, y);
			inner2 = v2.set(offX2, offY2).scl(innerRadius).add(x, y);
			out1 = v3.set(offX1, offY1).scl(radius).add(x, y);
			out2 = v4.set(offX2, offY2).scl(radius).add(x, y);

			Lines.arc(x, y, innerRadius, angle / 360f, rotate, sides);
			Lines.arc(x, y, radius, angle / 360f, rotate, sides);
		}

		Lines.line(inner1.x, inner1.y, out1.x, out1.y);
		Lines.line(inner2.x, inner2.y, out2.x, out2.y);
	}

	public static void drawLinesRadio(float centerX, float centerY, float innerRadius, float radius, int lines) {
		drawLinesRadio(centerX, centerY, innerRadius, radius, lines, 0f, 360f, false);
	}

	public static void drawLinesRadio(float centerX, float centerY, float innerRadius, float radius, int lines, float rotate, float totalDeg, boolean cap) {
		float angleStep = totalDeg / lines;

		for (int i = 0; i < (cap ? lines + 1 : lines); i++) {
			float angle = i * angleStep + rotate;

			float cos = Mathf.cosDeg(angle);
			float sin = Mathf.sinDeg(angle);
			float innerOffX = innerRadius * cos;
			float innerOffY = innerRadius * sin;
			float outerOffX = radius * cos;
			float outerOffY = radius * sin;

			Lines.line(centerX + innerOffX, centerY + innerOffY, centerX + outerOffX, centerY + outerOffY);
		}
	}
}
