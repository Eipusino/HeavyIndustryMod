package heavyindustry.world.blocks.defense.turrets;

import arc.math.*;
import arc.struct.*;
import arc.util.*;
import mindustry.entities.*;
import mindustry.entities.bullet.*;
import mindustry.entities.pattern.*;
import mindustry.type.*;
import mindustry.world.blocks.defense.turrets.*;

/** Shoot Match Turret */
public class ShootMatchTurret extends ItemTurret {
	public float lifeRnd = 0;
	public IntMap<ShootPattern> shooterMap = new IntMap<>();

	public ShootMatchTurret(String name) {
		super(name);
	}

	public void shooter(Object... objects) {
		ObjectMap<Item, ShootPattern> mapper = ObjectMap.of(objects);

		for (ObjectMap.Entry<Item, BulletType> entry : ammoTypes.entries()) {
			shooterMap.put(entry.value.id, mapper.get(entry.key, shoot));
		}
	}

	public class ShootMatchTurretBuild extends ItemTurretBuild {
		public ShootPattern getShooter(BulletType type) {
			ShootPattern s = shooterMap.get(type.id);
			return s == null ? shoot : s;
		}

		@Override
		protected void shoot(BulletType type) {
			float
					bulletX = x + Angles.trnsx(rotation - 90, shootX, shootY),
					bulletY = y + Angles.trnsy(rotation - 90, shootX, shootY);

			ShootPattern shoot = getShooter(type);

			if (shoot.firstShotDelay > 0) {
				chargeSound.at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax));
				type.chargeEffect.at(bulletX, bulletY, rotation);
			}

			shoot.shoot(barrelCounter, (xOffset, yOffset, angle, delay, mover) -> {
				queuedBullets++;
				if (delay > 0f) {
					Time.run(delay, () -> bullet(type, xOffset, yOffset, angle, mover));
				} else {
					bullet(type, xOffset, yOffset, angle, mover);
				}
			}, () -> barrelCounter++);

			if (consumeAmmoOnce) {
				useAmmo();
			}
		}

		@Override
		protected void bullet(BulletType type, float xOffset, float yOffset, float angleOffset, Mover mover) {
			queuedBullets--;

			if (dead || (!consumeAmmoOnce && !hasAmmo())) return;

			float
					xSpread = Mathf.range(xRand),
					bulletX = x + Angles.trnsx(rotation - 90, shootX + xOffset + xSpread, shootY + yOffset),
					bulletY = y + Angles.trnsy(rotation - 90, shootX + xOffset + xSpread, shootY + yOffset),
					shootAngle = rotation + angleOffset + Mathf.range(inaccuracy + type.inaccuracy);

			float lifeScl = type.scaleLife ? Mathf.clamp(Mathf.dst(bulletX, bulletY, targetPos.x, targetPos.y) / type.range, minRange / type.range, range() / type.range) : 1f;

			if (lifeRnd > 0) lifeScl += Mathf.range(lifeRnd);

			//TODO aimX / aimY for multi shot turrets?
			handleBullet(type.create(this, team, bulletX, bulletY, shootAngle, -1f, (1f - velocityRnd) + Mathf.random(velocityRnd), lifeScl, null, mover, targetPos.x, targetPos.y), xOffset, yOffset, shootAngle - rotation);

			(shootEffect == null ? type.shootEffect : shootEffect).at(bulletX, bulletY, rotation + angleOffset, type.hitColor);
			(smokeEffect == null ? type.smokeEffect : smokeEffect).at(bulletX, bulletY, rotation + angleOffset, type.hitColor);
			shootSound.at(bulletX, bulletY, Mathf.random(soundPitchMin, soundPitchMax));

			ammoUseEffect.at(x - Angles.trnsx(rotation, ammoEjectBack), y - Angles.trnsy(rotation, ammoEjectBack), rotation * Mathf.sign(xOffset));

			if (shake > 0) {
				Effect.shake(shake, shake, this);
			}

			curRecoil = 1f;
			if (recoils > 0) {
				curRecoils[barrelCounter % recoils] = 1f;
			}
			heat = 1f;
			totalShots++;

			if (!consumeAmmoOnce) {
				useAmmo();
			}
		}
	}
}
