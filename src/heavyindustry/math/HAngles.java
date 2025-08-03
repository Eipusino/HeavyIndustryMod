package heavyindustry.math;

import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import mindustry.gen.Posc;

import static heavyindustry.math.Mathm.p1;
import static heavyindustry.math.Mathm.p2;
import static heavyindustry.math.Mathm.p3;

/** @since 1.0.4 */
public final class HAngles {
	/** Don't let anyone instantiate this class. */
	private HAngles() {}

	public static float angle(Posc start, Posc end) {
		return Angles.angle(start.x(), start.y(), end.x(), end.y());
	}

	public static float angle(Point2 start, Point2 end) {
		return Angles.angle(start.x, start.y, end.x, end.y);
	}

	public static float angle(Vec2 start, Vec2 end) {
		return Angles.angle(start.x, start.y, end.x, end.y);
	}

	public static float angleBisector(float a, float b) {
		a = Mathf.mod(a, 360f);
		b = Mathf.mod(b, 360f);

		float delta = Math.abs(a - b);

		return (delta > 180 ? (a + b) / 2f + 180 : (a + b) / 2f) % 360;
	}

	public static float angelDistance(float start, float end) {
		start = Mathf.mod(start, 360f);
		end = Mathf.mod(end, 360f);

		return (end + 360 - start) % 360;
	}

	/**
	 * Angel move from start to end, the distance is in 180 degrees
	 */
	public static float angleRot(float start, float end, float progress) {
		p1.trns(start, 1);
		p2.trns(end, 1);
		return p3.set(p1).lerp(p2, progress).angle();
	}

	public static float moveLerpToward(float angle, float to, float speed) {
		if (Math.abs(Angles.angleDist(angle, to)) < speed || angle == to) {
			return to;
		} else {
			angle = Mathf.mod(angle, 360f);
			to = Mathf.mod(to, 360f);
			if (angle == to) return to;
			if (angle > to == Angles.backwardDistance(angle, to) > Angles.forwardDistance(angle, to)) {
				angle -= speed;
			} else {
				angle += speed;
			}

			return angle;
		}
	}
}
