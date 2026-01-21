package heavyindustry.math;

import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Position;

public interface IPosition extends Position {
	@Override
	default float angleTo(Position other) {
		return Angles.angle(getX(), getY(), other.getX(), other.getY());
	}

	@Override
	default float angleTo(float x, float y) {
		return Angles.angle(getX(), getY(), x, y);
	}

	@Override
	default float dst2(Position other) {
		return dst2(other.getX(), other.getY());
	}

	@Override
	default float dst(Position other) {
		return dst(other.getX(), other.getY());
	}

	@Override
	default float dst(float x, float y) {
		final float xd = getX() - x;
		final float yd = getY() - y;
		return Mathf.sqrt(xd * xd + yd * yd);
	}

	@Override
	default float dst2(float x, float y) {
		final float xd = getX() - x;
		final float yd = getY() - y;
		return (xd * xd + yd * yd);
	}

	@Override
	default boolean within(Position other, float dst) {
		return within(other.getX(), other.getY(), dst);
	}

	@Override
	default boolean within(float x, float y, float dst) {
		return Mathf.dst2(getX(), getY(), x, y) < dst * dst;
	}
}
