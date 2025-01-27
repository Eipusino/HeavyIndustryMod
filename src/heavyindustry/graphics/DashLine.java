package heavyindustry.graphics;

import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.pooling.*;
import arc.util.pooling.Pool.*;

public final class DashLine {
	private static final Vec2 tmp1 = new Vec2(), tmp2 = new Vec2(), tmp3 = new Vec2(), tmp4 = new Vec2();
	private static final Vec2 vector = new Vec2(), u = new Vec2(), v = new Vec2(), inner = new Vec2(), outer = new Vec2();
	private static final Pool<PointList> pointListPool = Pools.get(PointList.class, PointList::new);
	private static final FloatSeq floats = new FloatSeq(20);

	/** Don't let anyone instantiate this class. */
	private DashLine() {}

	public static void dashPoly(float... cords) {
		dashPolyWithLength(10, cords);
	}

	public static void brokenLine(float... cords) {
		final int length = cords.length - cords.length % 2;
		final int realSize = length / 2;
		if (realSize < 2) return;
		PointList pointList = pointListPool.obtain().set(cords, length);

		try {
			float stroke = Lines.getStroke();
			floats.clear();

			float x11 = pointList.x(0), v11 = pointList.y(0);
			float x12 = pointList.x(1), y12 = pointList.y(1);
			float jstroke = stroke / 2f;

			float len1 = Mathf.len(x12 - x11, y12 - v11);
			float diff1x = (x12 - x11) / len1 * jstroke, diff1y = (y12 - v11) / len1 * jstroke;
			floats.add(x11 - diff1x - diff1y, v11 - diff1y + diff1x, x11 - diff1x + diff1y, v11 - diff1y - diff1x);

			for (int i = 0; i < realSize - 2; i++) {
				int i1 = i + 1;
				int i2 = i + 2;
				float x0 = pointList.x(i);
				float y0 = pointList.y(i);
				float x1 = pointList.x(i1);
				float y1 = pointList.y(i1);
				float x2 = pointList.x(i2);
				float y2 = pointList.y(i2);
				float ang0 = Angles.angle(x0, y0, x1, y1), ang1 = Angles.angle(x1, y1, x2, y2);
				float beta = Mathf.sinDeg(ang1 - ang0);

				u.set(x0, y0).sub(x1, y1).setLength(stroke).scl(1f / beta).scl(0.5f);
				v.set(x2, y2).sub(x1, y1).setLength(stroke).scl(1f / beta).scl(0.5f);
				if (beta == 0) {
					v.setZero();
					float hstroke = stroke / 2f;
					tmp1.set(x2, y2).sub(x1, y1);
					tmp2.set(x0, y0).sub(x1, y1);
					float angleDiff = Mathf.mod(tmp1.angle() - tmp2.angle(), 360);
					float len = hstroke / Mathf.sinDeg(angleDiff / 2f);

					tmp1.setLength(len).rotate(90);
					tmp2.setLength(len).rotate(90);
					floats.add(tmp1.x + x1, tmp1.y + y1, tmp2.x + x1, tmp2.y + y1);
					continue;
				}

				inner.set(x1, y1).add(u).add(v);
				outer.set(x1, y1).sub(u).sub(v);

				floats.add(inner.x, inner.y, outer.x, outer.y);
			}

			float x21 = pointList.x(-2), y21 = pointList.y(-2);
			float x22 = pointList.x(-1), y22 = pointList.y(-1);
			float kstroke = stroke / 2f;

			float len2 = Mathf.len(x22 - x21, y22 - y21);
			float diff2x = (x22 - x21) / len2 * kstroke, diff2y = (y22 - y21) / len2 * kstroke;
			floats.add(x22 + diff2x - diff2y, y22 + diff2y + diff2x, x22 + diff2x + diff2y, y22 + diff2y - diff2x);

			for (int i = 0; i < floats.size / 2 - 2; i += 2) {
				float x1 = xfloats(i);
				float y1 = yfloats(i);
				float x2 = xfloats(i + 1);
				float y2 = yfloats(i + 1);

				float x3 = xfloats(i + 2);
				float y3 = yfloats(i + 2);
				float x4 = xfloats(i + 3);
				float y4 = yfloats(i + 3);

				if (floats.size / 2 == 4) {
					Fill.quad(x1, y1, x3, y3, x4, y4, x2, y2);
				} else {
					Fill.quad(x1, y1, x2, y2, x4, y4, x3, y3);
				}
			}
		} finally {
			pointList.free();
		}
	}

	private static float xfloats(int x) {
		return floats.items[Mathf.mod(x * 2, floats.size)];
	}

	private static float yfloats(int y) {
		return floats.items[Mathf.mod(y * 2 + 1, floats.size)];
	}

	public static void dashPolyWithLength(final float middleLength, float... cords) {
		if (cords.length % 2 != 0) return;

		PointList pointList = pointListPool.obtain().set(cords, cords.length);
		try {
			float perimeter = 0;
			for (int currentIndex = 0; currentIndex < cords.length / 2; currentIndex++) {
				perimeter += Mathf.dst(pointList.x(currentIndex), pointList.y(currentIndex), pointList.x(currentIndex + 1), pointList.y(currentIndex + 1));
			}
			int k = (int) (perimeter / middleLength) / 2;
			int amount = k * 2;
			final float len = perimeter / amount;
			final float len2 = len * len;
			boolean line = true;
			float cornerPercent = -1;

			for (int currentIndex = 0; currentIndex < cords.length / 2; currentIndex++) {
				Vec2 cur = Tmp.v1.set(pointList.x(currentIndex), pointList.y(currentIndex));
				Vec2 next = Tmp.v2.set(pointList.x(currentIndex + 1), pointList.y(currentIndex + 1));
				Vec2 position = Tmp.v3.set(cur);
				vector.set(next).sub(cur).limit2(len2);
				if (cornerPercent != -1) {
					Vec2 set = Tmp.v4.set(vector.x * cornerPercent, vector.y * cornerPercent);
					position.add(set);
					cornerPercent = -1;
				}
				while (!position.within(next, len / 10000f)) {
					if (position.dst2(next) > len2) {
						if (line) {
							Lines.line(position.x, position.y, position.x + vector.x, position.y + vector.y);
						}
						line = !line;
						position.add(vector);
					} else {
						cornerPercent = 1f - position.dst(next) / len;

						Vec2 nextPosition;
						int nextIndex = currentIndex + 2;
						FloatSeq floats = new FloatSeq();
						floats.add(position.x, position.y);
						floats.add(next.x, next.y);
						do {
							if (nextIndex % cords.length == 0) {
								return;
							}
							float next2x = pointList.x(nextIndex);
							float next2y = pointList.y(nextIndex);
							float currentx = pointList.x(nextIndex - 1);
							float currenty = pointList.y(nextIndex - 1);
							nextPosition = Tmp.v4.set(next2x, next2y).sub(currentx, currenty);
							float perfectLen = len * cornerPercent;
							float nextLen = nextPosition.len();
							if (nextLen > perfectLen) {
								nextPosition.setLength(perfectLen);
								nextLen = perfectLen;
							}
							floats.add(currentx + nextPosition.x, currenty + nextPosition.y);
							if (nextLen < perfectLen && cornerPercent > 0.0000001f) {
								cornerPercent = (perfectLen - nextLen) / len;
							} else {
								break;
							}
							nextIndex++;
						} while (true);
						currentIndex = nextIndex - 2;
						if (line) {
							brokenLine(floats.toArray());
						}
						line = !line;
						break;
					}
				}
			}
		} finally {
			pointList.free();
		}
	}

	static class PointList implements Poolable {
		public float[] cords;
		public int len;

		public PointList set(float[] cords, int len) {
			this.cords = cords;
			this.len = len;
			return this;
		}

		public float x(int i) {
			return cords[Mathf.mod(i * 2, len)];
		}

		public float y(int i) {
			return cords[Mathf.mod(i * 2 + 1, len)];
		}

		public void free() {
			pointListPool.free(this);
		}

		@Override
		public void reset() {
			cords = null;
			len = -1;
		}
	}
}
