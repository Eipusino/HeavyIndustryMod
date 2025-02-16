package heavyindustry.math;

import arc.math.*;

/** @since 1.0.4 */
public final class Anglesf {
	/** Don't let anyone instantiate this class. */
	private Anglesf() {}

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
