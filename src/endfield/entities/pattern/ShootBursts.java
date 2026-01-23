package endfield.entities.pattern;

import mindustry.entities.pattern.ShootPattern;
import org.jetbrains.annotations.Nullable;

public class ShootBursts extends ShootPattern {
	public int shotsPerBurst = 1;
	public float spread = 5f;
	public int bursts = 3;
	public float burstDelay = 30f;

	public ShootBursts(int shot, int bur, float spr) {
		shotsPerBurst = shot;
		bursts = bur;
		spread = spr;

		// This is so that stats work correctly
		shots = bursts * shotsPerBurst;
	}

	public ShootBursts() {}

	@Override
	public void shoot(int totalShots, BulletHandler handler, @Nullable Runnable barrelIncrementer) {
		for (int i = 0; i < bursts; i++) {
			for (int j = 0; j < shotsPerBurst; j++) {
				float angleOffset = j * spread - (shotsPerBurst - 1) * spread / 2f;
				handler.shoot(0, 0, angleOffset, firstShotDelay + shotDelay * j + burstDelay * i);
			}
		}
	}
}
