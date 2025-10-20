package heavyindustry.math;

import arc.func.FloatFloatf;
import arc.func.Floatc;
import arc.func.Floatc2;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mat;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Bezier;
import arc.math.geom.Mat3D;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.util.Time;
import heavyindustry.func.Floatc3;
import mindustry.gen.Posc;

/** @since 1.0.4 */
public final class Mathm {
	public static final double radiansToDegrees = 180.0 / Math.PI;

	static final int aSinBits = 14; //16KB. Adjust for accuracy.
	static final int aSinMask = ~(-1 << aSinBits);
	static final int aSinCount = aSinMask + 1;
	static final float[] aSinTable = new float[aSinCount];
	static final float sinToIndex = aSinCount / 2f;
	static final float radFull = Mathf.PI * 2f;
	static final Vec2 bezOut = new Vec2(), p1 = new Vec2(), p2 = new Vec2(), p3 = new Vec2(), p4 = new Vec2(), p5 = new Vec2(), tmp = new Vec2();
	static final Rand seedr = new Rand();

	static {
		for (int i = 0; i < aSinCount; i++) {
			aSinTable[i] = (float) (Math.asin((i + 0.5f) / aSinCount * 2 - 1) + radFull);
		}

		aSinTable[0] = radFull - Mathf.halfPi;
		aSinTable[aSinTable.length - 1] = radFull + Mathf.halfPi;
		aSinTable[index(1.5f)] = Mathf.halfPi + radFull;
		aSinTable[index(0.5f)] = Mathf.pi + Mathf.halfPi + radFull;
	}

	/** Don't let anyone instantiate this class. */
	private Mathm() {}

	/** @return whether x,y is inside the square with radius d centered at cx, cy. */
	public static boolean isInSquare(float cx, float cy, float d, float x, float y) {
		return x > cx - d && x < cx + d && y > cy - d && y < cy + d;
	}

	public static float cornerDst(float r) {
		return cornerDst(r, r);
	}

	public static float cornerDst(float w, float h) {
		return (float) Math.sqrt(w * h * 2f);
	}

	public static float cbrt(float x) {
		return (float) Math.cbrt(x);
	}

	public static Vec2 quad(float a, float b, float c) {
		Vec2 sol = null;
		if (Math.abs(a) < 1e-6) {
			if (Math.abs(b) < 1e-6) {
				sol = Math.abs(c) < 1e-6 ? tmp.set(0, 0) : null;
			} else {
				tmp.set(-c / b, -c / b);
			}
		} else {
			float disc = b * b - 4 * a * c;
			if (disc >= 0) {
				disc = Mathf.sqrt(disc);
				a = 2 * a;
				sol = tmp.set((-b - disc) / a, (-b + disc) / a);
			}
		}
		return sol;
	}

	/** @return Smallest positive nonzero solution of a quadratic. */
	public static float quadPos(float a, float b, float c) {
		Vec2 ts = quad(a, b, c);
		if (ts != null) {
			float t0 = ts.x, t1 = ts.y;
			float t = Math.min(t0, t1);
			if (t <= 0) t = Math.max(t0, t1);
			if (t > 0) {
				return t;
			}
		}
		return 0f;
	}

	public static float solve(float a, float b, float c) {
		if (a == 0) {
			return -c / b;
		} else {
			Vec2 t = quad(a, b, c);
			return Math.max(t.x, t.y);
		}
	}

	/** <a href="https://math.stackexchange.com/questions/785/is-there-a-general-formula-for-solving-quartic-degree-4-equations">...</a> */
	public static float[] quartic(float a, float b, float c, float d, float e) {
		float p1 = 2 * c * c * c - 9 * b * c * d + 27 * a * d * d + 27 * b * b * e - 72 * a * c * e;
		float p2 = c * c - 3 * b * d + 12 * a * e;
		float p3 = p1 + Mathf.sqrt(-4 * p2 * p2 * p2 + p1 * p1);
		float p4 = cbrt(p3 / 2);
		float p5 = p2 / (3 * a * p4) + (p4 / (3 * a));

		float p6 = Mathf.sqrt((b * b) / (4 * a * a) - (2 * c) / (3 * a) + p5);
		float p7 = (b * b) / (2 * a * a) - (4 * c) / (3 * a) - p5;
		float p8 = (-(b * b * b) / (a * a * a) + (4 * b * c) / (a * a) - (8 * d) / a) / (4 * p6);

		return new float[]{
				-(b / (4 * a)) - (p6 / 2) - (Mathf.sqrt(p7 - p8) / 2),
				-(b / (4 * a)) - (p6 / 2) + (Mathf.sqrt(p7 - p8) / 2),
				-(b / (4 * a)) - (p6 / 2) - (Mathf.sqrt(p7 + p8) / 2),
				-(b / (4 * a)) - (p6 / 2) + (Mathf.sqrt(p7 + p8) / 2)
		};
	}

	public static Vec2 randomCirclePoint(Vec2 v, float radius) {
		v.setToRandomDirection().setLength(radius * Mathf.sqrt(Mathf.random()));

		return v;
	}

	public static float circleStarPoint(float theta) {
		theta = Mathf.mod(theta, 90f);
		theta *= Mathf.degRad;
		float b = -2 * Mathf.sqrt2 * Mathf.cos(theta - Mathf.pi / 4f);
		return (-b - Mathf.sqrt(b * b - 4)) / 2;
	}

	/** Pulled out of {@link Angles#moveToward(float, float, float)}. */
	public static int angleMoveDirection(float from, float to) {
		from = Mathf.mod(from, 360f);
		to = Mathf.mod(to, 360f);

		if (from > to == Angles.backwardDistance(from, to) > Angles.forwardDistance(from, to)) {
			return -1;
		} else {
			return 1;
		}
	}

	/** Lerp from one angle to another. */
	public static float lerpAngle(float from, float to, float progress) {
		return Angles.moveToward(from, to, progress * Angles.angleDist(from, to));
	}

	public static Vec2 bezier(float t, float x1, float y1, float x2, float y2, Vec2 h1, Vec2 h2) {
		p1.set(x1, y1);
		p4.set(x2, y2);
		p2.set(p1).add(h1);
		p3.set(p4).add(h2);
		Bezier.cubic(bezOut, t, p1, p2, p3, p4, tmp);
		return bezOut;
	}

	public static Vec2 bezierDeriv(float t, float x1, float y1, float x2, float y2, Vec2 h1, Vec2 h2) {
		p1.set(x1, y1);
		p4.set(x2, y2);
		p2.set(p1).add(h1);
		p3.set(p4).add(h2);
		Bezier.cubicDerivative(bezOut, t, p1, p2, p3, p4, tmp);
		return bezOut;
	}

	public static int index(float sin) {
		return (int) ((sin + 1) * sinToIndex) & aSinMask;
	}

	public static float cosToSin(float x) {
		return x - (x * x * x) / 6 + (x * x * x * x * x) / 120; //Taylor
	}

	public static float acosRad(float cos) {
		return aSinTable[index((float) Math.sqrt(1 - cos * cos))];
	}

	public static float asinDeg(float sin) {
		return aSinTable[index(sin)] * Mathf.radiansToDegrees;
	}

	/**
	 * Calculate vector coordinates using Fourier series in polar coordinate form.
	 *
	 * @param time   Parameters (or interpolation) passed in for Fourier series.
	 * @param params Parameterf group, determine a sin function for every three data points, parameter format:{@code {Angular velocity, initial phase, extremum,...}}
	 * @return The vector calculated by the specified Fourier series under given interpolation is in the form of an orthogonal coordinate system.
	 */
	public static Vec2 fourierSeries(float time, float... params) {
		tmp.setZero();
		for (int i = 0; i < params.length; i += 3) {
			float w = params[i];
			float f = params[i + 1];
			float l = params[i + 2];

			tmp.add(Angles.trnsx(f + time * w, l), Angles.trnsy(f + time * w, l));
		}

		return tmp;
	}

	/**
	 * Use Fourier series in polar coordinate form to calculate vector coordinates, and call back the calculation result through a function.
	 *
	 * @param transRecall The callback function for calculating the result.
	 * @param time        Parameters (or interpolation) passed in for Fourier series.
	 * @param params      Parameterf group, determine a sin function for every three data points, parameter format:{@code {Angular velocity, initial phase, extremum, ...}}
	 */
	public static void fourierSeries(Floatc2 transRecall, float time, float... params) {
		Vec2 v = fourierSeries(time, params);
		transRecall.get(v.x, v.y);
	}

	public static float gradientRotate(float rad, float fine) {
		return gradientRotate(rad, fine, 0.25f, 4);
	}

	public static float gradientRotate(float rad, float fine, int sides) {
		return gradientRotate(rad, fine, 1f / sides, 4);
	}

	public static float gradientRotate(float rad, float fine, float off, int sides) {
		return rad - off * Mathf.sin(rad * sides + fine) + fine / sides;
	}

	public static float gradientRotateDeg(float deg, float fine) {
		return gradientRotate(deg * Mathf.degRad, fine * Mathf.degRad, 0.25f, 4) * Mathf.radDeg;
	}

	public static float gradientRotateDeg(float deg, float fine, int sides) {
		return gradientRotate(deg * Mathf.degRad, fine * Mathf.degRad, 1f / sides, 4) * Mathf.radDeg;
	}

	public static float gradientRotateDeg(float deg, float fine, float off, int sides) {
		return gradientRotate(deg * Mathf.degRad, fine * Mathf.degRad, off, sides) * Mathf.radDeg;
	}

	public static float innerAngle(float a, float b) {
		a %= 360;
		b %= 360;
		return b - a > 180 ? b - a - 360 : b - a < -180 ? b - a + 360 : b - a;
	}

	public static boolean confine(float d, float min, float max) {
		return d < min || d > max;
	}

	public static boolean confine(double d, double min, double max) {
		return d < min || d > max;
	}

	/** An asymptotic function that approximates from a specified starting point to a target point. */
	public static float lerp(float origin, float dest, float rate, float x) {
		float a = 1 - rate;
		float b = rate * dest;

		float powered = Mathf.pow(a, x - 1);

		return origin * powered + (b * powered - b / a) / (1 - 1 / a);
	}

	/** An asymptotic function that approximates from a specified starting point to a target point. */
	public static double lerp(double origin, double dest, double rate, double x) {
		double a = 1 - rate;
		double b = rate * dest;

		double powered = Math.pow(a, x - 1);

		return origin * powered + (b * powered - b / a) / (1 - 1 / a);
	}

	/** The function corresponding to an S-shaped curve. */
	public static float sCurve(float left, float right, float dx, float dy, float rate, float x) {
		float diff = right - left;
		float xValue = dx * rate;

		return diff / Mathf.pow(2, xValue - rate * x) + dy + left;
	}

	/** The function corresponding to an S-shaped curve. */
	public static double sCurve(double left, double right, double dx, double dy, double rate, double x) {
		double diff = right - left;
		double xValue = dx * rate;

		return diff / Math.pow(2, xValue - rate * x) + dy + left;
	}

	/**
	 * Increase and decrease the curve,reaching the highest point at the most suitable value,and decreasing on both sides.
	 * The parameters can control the left and right attenuation rates separately.
	 */
	public static float lerpIncrease(float lerpLeft, float lerpRight, float max, float optimal, float x) {
		if (x < 0) return 0;
		return x >= 0 && x < optimal ? -max * Mathf.pow(1 - x / optimal, lerpLeft) + max : -max * Mathf.pow(1 - optimal / x, lerpRight) + max;
	}

	/**
	 * Increase and decrease the curve,reaching the highest point at the most suitable value,and decreasing on both sides.
	 * The parameters can control the left and right attenuation rates separately.
	 */
	public static double lerpIncrease(double lerpLeft, double lerpRight, double max, double optimal, double x) {
		if (x < 0) return 0;
		return x >= 0 && x < optimal ? -max * Math.pow(1 - x / optimal, lerpLeft) + max : -max * Math.pow(1 - optimal / x, lerpRight) + max;
	}

	public static float hyperbolicLimit(float t) {
		return 1f - 1f / (t + 1);
	}

	public static void randLenVectors(long seed, int amount, float in, float inRandMin, float inRandMax, float lengthRand, FloatFloatf length, Floatc3 cons) {
		seedr.setSeed(seed);
		for (int i = 0; i < amount; i++) {
			float r = seedr.random(inRandMin, inRandMax);
			float offset = r > 0 ? seedr.nextFloat() * r : 0f;

			float fin = Mathf.curve(in, offset, (1f - r) + offset);
			float f = length.get(fin) * (lengthRand <= 0f ? 1f : seedr.random(1f - lengthRand, 1f));
			p5.trns(seedr.random(360f), f);
			cons.get(p5.x, p5.y, fin);
		}
	}

	public static Vec2 addLength(Vec2 vec, float add) {
		float len = vec.len();
		vec.x += add * (vec.x / len);
		vec.y += add * (vec.y / len);
		return vec;
	}

	public static float round(double value) {
		float scale = 0.00000001f;
		return ((float) Math.ceil(value / scale)) * scale;
	}

	public static float atan(float tan) {
		return round(radiansToDegrees * Math.atan(tan));
	}

	public static float bound(float in, float from, float to, float start, float end, Interp interpolation) {
		if (in < from || in > to) {
			return 0f;
		} else {
			float f = (in - from) / (to - from);
			return interpolation.apply(start, end, f);
		}
	}

	public static float curve(float in, float from, float to, float start, float end, Interp interpolation) {
		if (in < from) {
			return start;
		} else if (in > to) {
			return end;
		} else {
			float f = (in - from) / (to - from);
			return interpolation.apply(start, end, f);
		}
	}

	/** A parabolic curve with the opening facing downwards */
	public static float parabola(float x) {
		return (-Mathf.sqr(x) + x) * 4f;
	}

	/** Scale X magnification can also be seen as where the function returns to 0 */
	public static float parabola(float x, float scale) {
		if (x > scale) return 0f;
		x = x * scale;
		return (-Mathf.sqr(x) + x) * 4f;
	}

	/**
	 * Sets a 3x3 matrix to the top left 3x3 corner of the provided 4x4 matrix.
	 *
	 * @param from The 4x4 matrix. This matrix will not be modified.
	 * @param to   The output 3x3 matrix.
	 * @return The output 3x3 matrix, for chaining operations.
	 */
	public static Mat copyMatrix(Mat3D from, Mat to) {
		float[] in = from.val, out = to.val;
		System.arraycopy(in, Mat3D.M00, out, Mat.M00, 3);
		System.arraycopy(in, Mat3D.M01, out, Mat.M01, 3);
		System.arraycopy(in, Mat3D.M02, out, Mat.M02, 3);
		return to;
	}

	/**
	 * In between two lines.
	 *
	 * @param div   between two point, creates a new point.
	 * @param range random offset of two points, where the new point is created
	 * @return a Vec2 of the point between the two Vec2 point in a 2D plane.
	 **/
	public static Vec2 interpolate(Vec2 start, Vec2 end, float div, float range) {
		Vec2 between = ((end.sub(start).div(new Vec2(div, div))).add(start));
		return new Vec2(between.x + Mathf.range(range), between.y + Mathf.range(range));
	}

	public static Vec3 mul(Mat mat, float x, float y, float z, Vec3 out) {
		float[] m = mat.val;
		return out.set(
				m[Mat.M00] * x + m[Mat.M10] * y + m[Mat.M20] * z,
				m[Mat.M01] * x + m[Mat.M11] * y + m[Mat.M21] * z,
				m[Mat.M02] * x + m[Mat.M12] * y + m[Mat.M22] * z
		);
	}

	public static Vec3 normal(Vec3 out, Vec3 a, Vec3 b, Vec3 c) {
		return out.set(b).sub(a).crs(c.x - a.x, c.y - a.y, c.z - a.z).nor();
	}

	public static float angleDistSigned(float a, float b) {
		a = (a + 360f) % 360f;
		b = (b + 360f) % 360f;

		float d = Math.abs(a - b) % 360f;
		int sign = (a - b >= 0f && a - b <= 180f) || (a - b <= -180f && a - b >= -360f) ? 1 : -1;

		return (d > 180f ? 360f - d : d) * sign;
	}

	public static float angleDistSigned(float a, float b, float start) {
		float dst = angleDistSigned(a, b);
		if (Math.abs(dst) > start) {
			return dst > 0f ? dst - start : dst + start;
		}

		return 0f;
	}

	public static float angleDist(float a, float b) {
		float d = Math.abs(a - b) % 360f;
		return (d > 180f ? 360f - d : d);
	}

	public static void shotgun(int points, float spacing, float offset, Floatc cons) {
		for (int i = 0; i < points; i++) {
			cons.get(i * spacing - (points - 1) * spacing / 2f + offset);
		}
	}

	public static float clampedAngle(float angle, float relative, float limit) {
		if (limit >= 180f) return angle;
		if (limit <= 0f) return relative;
		float dst = angleDistSigned(angle, relative);
		if (Math.abs(dst) > limit) {
			float val = dst > 0f ? dst - limit : dst + limit;
			return (angle - val) % 360f;
		}
		return angle;
	}

	public static float dst(Posc a, Posc b) {
		return Mathf.dst(a.x(), a.y(), b.x(), b.y());
	}

	public static float dst(Point2 a, Point2 b) {
		return Mathf.dst(a.x, a.y, b.x, b.y);
	}

	public static float dst(Vec2 a, Vec2 b) {
		return Mathf.dst(a.x, a.y, b.x, b.y);
	}

	public static float absin(float rad) {
		return absin(rad, 1f, 1f);
	}

	public static float absin(float rad, float scl, float mag) {
		return (Mathf.sin(rad / scl) + 1f) / 2f * mag;
	}

	public static byte clamp(byte value, byte min, byte max) {
		return value > max ? max : value < min ? min : value;
	}

	public static byte max(byte a, byte b) {
		return (a >= b) ? a : b;
	}

	public static byte min(byte a, byte b) {
		return (a <= b) ? a : b;
	}

	public static short clamp(short value, short min, short max) {
		return value > max ? max : value < min ? min : value;
	}

	public static short max(short a, short b) {
		return (a >= b) ? a : b;
	}

	public static short min(short a, short b) {
		return (a <= b) ? a : b;
	}

	public static char max(char a, char b) {
		return (a >= b) ? a : b;
	}

	public static char min(char a, char b) {
		return (a <= b) ? a : b;
	}

	public static int clamp(int value, int min, int max) {
		return value > max ? max : value < min ? min : value;
	}

	public static long clamp(long value, long min, long max) {
		return value > max ? max : value < min ? min : value;
	}

	public static float clamp(float value, float min, float max) {
		return value > max ? max : value < min ? min : value;
	}

	public static double clamp(double value, double min, double max) {
		return value > max ? max : value < min ? min : value;
	}

	public static char clamp(char value, char min, char max) {
		return value > max ? max : value < min ? min : value;
	}

	public static boolean chanceDelta(float d) {
		return Mathf.rand.nextFloat() < d * Time.delta;
	}

	public static boolean isNaNInfinite(float value) {
		return Float.isNaN(value) || Float.isInfinite(value) || value >= Float.MAX_VALUE;
	}

	public static boolean isNaNInfinities(float... values) {
		for (float value : values) {
			if (Float.isNaN(value) || Float.isInfinite(value) || value >= Float.MAX_VALUE) return true;
		}
		return false;
	}
}
