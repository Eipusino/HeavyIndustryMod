package heavyindustry.type.weapons;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.type.Weapon;

public class AcceleratingWeapon extends Weapon {
	public float accelCooldownTime = 120f;
	public float accelCooldownWaitTime = 60f;
	public float accelPerShot = 1f;
	public float minReload = 5f;

	public AcceleratingWeapon(String name) {
		super(name);
		mountType = AcceleratingMount::new;
	}

	public AcceleratingWeapon() {
		this("");
	}

	@Override
	public void update(Unit unit, WeaponMount mount) {
		if (mount instanceof AcceleratingMount amo) updateAccelerating(unit, amo);

		super.update(unit, mount);
	}

	public void updateAccelerating(Unit unit, AcceleratingMount mount) {
		float r = ((mount.accel / reload) * unit.reloadMultiplier * Time.delta) * (reload - minReload);
		if (!alternate || otherSide == -1) {
			mount.reload -= r;
		} else {
			WeaponMount other = unit.mounts[otherSide];
			other.reload -= r / 2f;
			mount.reload -= r / 2f;
			if (other instanceof AcceleratingMount aMount) {
				float accel = unit.isShooting() && unit.canShoot() ? Math.max(aMount.accel, mount.accel) : Math.min(aMount.accel, mount.accel);
				float wTime = unit.isShooting() && unit.canShoot() ? Math.max(aMount.waitTime, mount.waitTime) : Math.min(aMount.waitTime, mount.waitTime);
				aMount.accel = accel;
				aMount.waitTime = wTime;
				mount.accel = accel;
				mount.waitTime = wTime;
			}
		}
		if (mount.waitTime <= 0f) {
			mount.accel = Math.max(0f, mount.accel - (minReload / accelCooldownTime) * Time.delta);
		} else {
			mount.waitTime -= Time.delta;
		}
	}

	@Override
	protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation) {
		AcceleratingMount aMount = (AcceleratingMount) mount;
		aMount.accel = Mathf.clamp(aMount.accel + accelPerShot, 0f, minReload);
		aMount.waitTime = accelCooldownWaitTime;
		super.shoot(unit, mount, shootX, shootY, rotation);
	}

	public static class AcceleratingMount extends WeaponMount {
		float accel = 0f;
		float waitTime = 0f;

		AcceleratingMount(Weapon weapon) {
			super(weapon);
		}
	}
}
