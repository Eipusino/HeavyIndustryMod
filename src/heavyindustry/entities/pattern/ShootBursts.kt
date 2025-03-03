package heavyindustry.entities.pattern

import arc.util.Nullable
import mindustry.entities.pattern.ShootPattern

open class ShootBursts : ShootPattern {
	@JvmField var shotsPerBurst = 1
	@JvmField var spread = 5f
	@JvmField var bursts = 3
	@JvmField var burstDelay = 30f

	constructor(shot: Int, bur: Int, spr: Float) {
		shotsPerBurst = shot
		bursts = bur
		spread = spr

		// This is so that stats work correctly
		shots = bur * shot
	}

	constructor()

	override fun shoot(totalShots: Int, handler: BulletHandler, @Nullable barrelIncrementer: Runnable) {
		for (i in 0..<bursts) {
			for (j in 0..<shotsPerBurst) {
				val angleOffset = j * spread - (shotsPerBurst - 1) * spread / 2f
				handler.shoot(0f, 0f, angleOffset, firstShotDelay + shotDelay * j + burstDelay * i)
			}
		}
	}
}
