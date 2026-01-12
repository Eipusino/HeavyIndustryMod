package heavyindustry.entities.pattern;

import mindustry.entities.pattern.ShootPattern.BulletHandler;

@FunctionalInterface
public interface IBulletHandler extends BulletHandler {
	@Override
	default void shoot(float x, float y, float rotation, float delay) {
		shoot(x, y, rotation, delay, null);
	}
}
