package heavyindustry.math;

import arc.Core;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.gen.Hitboxc;

import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;

/** @since 1.0.5 */
public final class Math3d {
	public static final float horiToVerti = 1f / 48f;

	private static final Vec2 vec = new Vec2(), vresult = new Vec2();
	private static final Vec3 axis = new Vec3(), rim = new Vec3();

	/** Don't let anyone instantiate this class. */
	private Math3d() {}

	/**
	 * Properly rotates and tilts up a 3D vector.
	 *
	 * @param vec3	  Vec3 to write output to.
	 * @param length	Length of the vector.
	 * @param yaw	   Angle of the main angle.
	 * @param yawOffset Rotational offset from the main angle.
	 * @param pitch	 Pitch. Tilts around the axis 90* of the main angle.
	 */
	public static Vec3 rotate(Vec3 vec3, float length, float yaw, float yawOffset, float pitch) {
		return vec3.set(Angles.trnsx(yawOffset, length), Angles.trnsy(yawOffset, length), 0f)
				.rotate(Vec3.Y, pitch).rotate(Vec3.Z, -yaw);
	}

	public static int linePointCounts(float x1, float y1, float z1, float x2, float y2, float z2) {
		return (int) (dst(x1, y1, z1, x2, y2, z2) / tilesize / tilesize);
	}

	public static float[] linePoints(float x1, float y1, float z1, float x2, float y2, float z2, int pointCount) {
		float[] points = new float[pointCount * 3];
		float px = (x2 - x1) / (pointCount - 1);
		float py = (y2 - y1) / (pointCount - 1);
		float pz = (z2 - z1) / (pointCount - 1);

		for (int i = 0; i < pointCount; i++) {
			points[i * 3] = x1 + px * i;
			points[i * 3 + 1] = y1 + py * i;
			points[i * 3 + 2] = z1 + pz * i;
		}

		return points;
	}

	public static float[] diskVertices(float x, float y, float z, float rotation, float startAngle, float tilt, float rad, int verts) {
		float[] diskVerts = new float[(verts + 1) * 3];
		float space = 360f / verts;
		axis.set(Vec3.Z).rotate(Vec3.Y, tilt).rotate(Vec3.Z, -rotation);
		rim.set(rad, 0, 0).rotate(Vec3.Y, tilt).rotate(Vec3.Z, -rotation);
		rim.rotate(axis, rotation - startAngle);

		for (int i = 0; i <= verts; i++) {
			diskVerts[i * 3] = x + rim.x;
			diskVerts[i * 3 + 1] = y + rim.y;
			diskVerts[i * 3 + 2] = z + rim.z;
			rim.rotate(axis, space);
		}
		return diskVerts;
	}

	public static float[] castVertices(float x, float y, float rotation, float startAngle, float tilt, float rad, int verts) {
		float[] castVerts = new float[verts * 2];
		float space = 360f / (verts - 1f);
		float scl = 1f + Mathf.sinDeg(tilt);

		for (int i = 0; i < verts; i++) {
			float angle = startAngle + space * i - rotation;
			vec.trns(rotation, Mathf.cosDeg(angle) * rad * scl, Mathf.sinDeg(angle) * rad);
			castVerts[i * 2] = x + vec.x;
			castVerts[i * 2 + 1] = y + vec.y;
		}
		return castVerts;
	}

	/**
	 * Calculates of intercept of a stationary and moving target. Do not call from multiple threads!
	 *
	 * @param srcx  X of shooter
	 * @param srcy  Y of shooter
	 * @param dstx  X of target
	 * @param dsty  Y of target
	 * @param dstvx X velocity of target (subtract shooter X velocity if needed)
	 * @param dstvy Y velocity of target (subtract shooter Y velocity if needed)
	 * @param ba	constant acceleration of the bullet
	 * @param bv	initial velocity of the bullet
	 * @return the intercept location
	 */
	public static Vec2 intercept(float srcx, float srcy, float dstx, float dsty, float dstvx, float dstvy, float ba, float bv) {
		dstvx /= Time.delta;
		dstvy /= Time.delta;
		float dx = dstx - srcx,
				dy = dsty - srcy;
		float uv = dstvx * dstvx + dstvy * dstvy,
				ud = dx * dstvx + dy * dstvy;

		// Get quartic comp
		float a = -(ba * ba) / 4;
		float b = -ba * bv;
		float c = uv - (bv * bv);
		float d = 2 * ud;
		float e = dx * dx + dy * dy;

		// Solve
		float[] ts = Mathm.quartic(a, b, c, d, e);

		// Find smallest positive solution
		Vec2 sol = vresult.set(dstx, dsty);
		float min = Float.MAX_VALUE;
		for (float t : ts) {
			if (t >= 0 && t < min) min = t;
		}
		if (min < Float.MAX_VALUE) sol.set(dstx + dstvx * min, dsty + dstvy * min);

		return sol;
	}

	public static Vec2 intercept(Position src, Position dst, float ba, float bv) {
		float ddx = 0, ddy = 0;
		if (dst instanceof Hitboxc h) {
			ddx += h.deltaX();
			ddy += h.deltaY();
		}
		if (src instanceof Hitboxc h) {
			ddx -= h.deltaX();
			ddy -= h.deltaY();
		}
		if (ddx == 0 && ddy == 0)
			return vresult.set(dst); //Don't bother performing unnecessary math if no prediction is needed.
		return intercept(src.getX(), src.getY(), dst.getX(), dst.getY(), ddx, ddy, ba, bv);
	}

	public static Vec2 inaccuracy(float inaccuracy) {
		return Mathm.randomCirclePoint(vec, inaccuracy);
	}

	//See my notebook for half the calculation. Oh wait, you don't have access to it because I physically hold it.
	public static float homingPitch(float x1, float y1, float z1, float x2, float y2, float v2, float a, float g) {
		float dst = Mathf.dst(x1, y1, x2, y2);

		float p1 = 2 * g * z1;
		float p2 = 2 * a * dst;
		float p3 = -a * z1 - g * dst;
		float p4 = (p3 * p3 - p1 * p2) / v2; //v2 = v * v

		//Also thanks to asimplebeginner for helping with the second half of the calculation.
		float A = p2 - p1;
		float C = p4 - p1;

		return (float) (-Math.asin((C - 0.5f * A) / ((Mathf.sign(p3) * Mathf.sqrt(0.25f * A * A + p3 * p3)))) / 2 - Math.atan(0.5f * A / p3)) / 2f;
	}

	public static float dst(float x, float y, float z) {
		return Mathf.sqrt(x * x + y * y + z * z);
	}

	public static float dst(float x1, float y1, float z1, float x2, float y2, float z2) {
		float xd = x2 - x1;
		float yd = y2 - y1;
		float zd = z2 - z1;
		return Mathf.sqrt(xd * xd + yd * yd + zd * zd);
	}

	public static float tubeStartAngle(float x1, float y1, float x2, float y2, float rad1, float rad2) {
		if (x1 == x2 && y1 == y2) return 0f;

		float d = Mathf.dst(x2 - x1, y2 - y1);
		float f = Mathf.sqrt(d * d - Mathf.sqr(rad2 - rad1));
		float a = rad1 > rad2 ? Mathf.atan2(rad1 - rad2, f) : (rad1 < rad2 ? Mathf.pi - Mathf.atan2(rad2 - rad1, f) : Mathf.halfPi);
		Tmp.v1.set(x2 - x1, y2 - y1).scl(1f / d); //normal
		Tmp.v2.set(Tmp.v1).rotateRad(Mathf.pi - a).scl(-rad2).add(x2, y2); //tangent

		return Angles.angle(x2, y2, Tmp.v2.x, Tmp.v2.y);
	}

	public static float xOffset(float x, float height) {
		return (x - Core.camera.position.x) * hMul(height);
	}

	public static float yOffset(float y, float height) {
		return (y - Core.camera.position.y) * hMul(height);
	}

	public static float hMul(float height) {
		return height(height) * renderer.getDisplayScale();
	}

	public static float height(float height) {
		return height * horiToVerti;
	}

	public static float layerOffset(float x, float y) {
		float max = Math.max(Core.camera.width, Core.camera.height);
		return -Mathf.dst(x, y, Core.camera.position.x, Core.camera.position.y) / max / 1000f;
	}
}
