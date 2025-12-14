package heavyindustry.entities.pattern;

import arc.math.Mathf;
import mindustry.entities.pattern.ShootHelix;
import org.jetbrains.annotations.Nullable;

public class FlipShootHelix extends ShootHelix {
	public boolean flip = false;
	public float rotSpeedOffset = 0;
	public float rotSpeedBegin = 1;

	@Override
	public void shoot(int totalShots, BulletHandler handler, @Nullable Runnable barrelIncrementer) {
		for (int i = 0; i < shots; i++) {
			if (flip) {
				for (int sign : Mathf.signs) {
					int j = i;
					handler.shoot(0, 0, 0, firstShotDelay + shotDelay * i, b -> b.moveRelative(0f, Mathf.sin(b.time * (rotSpeedOffset * j + rotSpeedBegin) + offset * ((float) j / shots), scl, mag * sign)));
				}
			} else {
				int j = i;
				handler.shoot(0, 0, 0, firstShotDelay + shotDelay * i, b -> b.moveRelative(0f, Mathf.sin(b.time * (rotSpeedOffset * j + rotSpeedBegin) + offset * ((float) j / shots), scl, mag)));
			}
		}
	}
}
