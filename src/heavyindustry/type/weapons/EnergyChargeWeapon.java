package heavyindustry.type.weapons;

import arc.func.Cons2;
import arc.func.Cons3;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.audio.SoundLoop;
import mindustry.entities.Predict;
import mindustry.entities.Sized;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.type.Weapon;

public class EnergyChargeWeapon extends Weapon {
	public Cons3<Unit, WeaponMount, Float> drawCharge = (unit, mount, charge) -> {};
	/** Uses reload as charge. */
	public Cons2<Unit, WeaponMount> chargeCondition;
	public boolean drawTop = true, startUncharged = true, drawRegion = true;
	private int sequenceNum;

	public EnergyChargeWeapon() {
		this("");
	}

	public EnergyChargeWeapon(String name) {
		super(name);
		mountType = w -> {
			WeaponMount m = new ChargeMount(w);
			m.reload = startUncharged ? reload : 0f;
			return m;
		};
	}

	@Override
	public void drawOutline(Unit unit, WeaponMount mount) {
		if (drawRegion) super.drawOutline(unit, mount);
	}

	@Override
	public void draw(Unit unit, WeaponMount mount) {
		float tmp = mount.reload;
		mount.reload = Mathf.clamp(mount.reload, 0f, reload);
		if (!drawTop) drawCharge.get(unit, mount, 1f - Mathf.clamp(mount.reload / reload));
		if (drawRegion) super.draw(unit, mount);
		mount.reload = tmp;
		if (drawTop) drawCharge.get(unit, mount, 1f - Mathf.clamp(mount.reload / reload));
	}

	@Override
	public void update(Unit unit, WeaponMount mount) {
		if (chargeCondition == null) {
			super.update(unit, mount);
		} else {
			boolean can = unit.canShoot();
			chargeCondition.get(unit, mount);

			float
					weaponRotation = unit.rotation - 90 + (rotate ? mount.rotation : 0),
					mountX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
					mountY = unit.y + Angles.trnsy(unit.rotation - 90, x, y),
					bulletX = mountX + Angles.trnsx(weaponRotation, this.shootX, this.shootY),
					bulletY = mountY + Angles.trnsy(weaponRotation, this.shootX, this.shootY),
					shootAngle = rotate ? weaponRotation + 90 : Angles.angle(bulletX, bulletY, mount.aimX, mount.aimY) + (unit.rotation - unit.angleTo(mount.aimX, mount.aimY));

			if (!controllable && autoTarget) {
				if ((mount.retarget -= Time.delta) <= 0f) {
					mount.target = findTarget(unit, mountX, mountY, bullet.range, bullet.collidesAir, bullet.collidesGround);
					mount.retarget = mount.target == null ? targetInterval : targetSwitchInterval;
				}

				if (mount.target != null && checkTarget(unit, mount.target, mountX, mountY, bullet.range)) {
					mount.target = null;
				}

				boolean shoot = false;

				if (mount.target != null) {
					shoot = mount.target.within(mountX, mountY, bullet.range + Math.abs(shootY) + (mount.target instanceof Sized s ? s.hitSize() / 2f : 0f)) && can;

					if (predictTarget) {
						Vec2 to = Predict.intercept(unit, mount.target, bullet.speed);
						mount.aimX = to.x;
						mount.aimY = to.y;
					} else {
						mount.aimX = mount.target.x();
						mount.aimY = mount.target.y();
					}
				}

				mount.shoot = mount.rotate = shoot;
			}

			if (continuous && mount.bullet != null) {
				if (!mount.bullet.isAdded() || mount.bullet.time >= mount.bullet.lifetime || mount.bullet.type != bullet) {
					mount.bullet = null;
				} else {
					mount.bullet.rotation(weaponRotation + 90);
					mount.bullet.set(bulletX, bulletY);
					mount.reload = reload;
					unit.vel.add(Tmp.v1.trns(unit.rotation + 180f, mount.bullet.type.recoil));
					if (shootSound != Sounds.none && !Vars.headless) {
						if (mount.sound == null) mount.sound = new SoundLoop(shootSound, 1f);
						mount.sound.update(bulletX, bulletY, true);
					}
				}
			} else {
				mount.heat = Math.max(mount.heat - Time.delta * unit.reloadMultiplier / mount.weapon.cooldownTime, 0);

				if (mount.sound != null) {
					mount.sound.update(bulletX, bulletY, false);
				}
			}

			if (otherSide != -1 && alternate && mount.side == flipSprite &&
					mount.reload + Time.delta * unit.reloadMultiplier > reload / 2f && mount.reload <= reload / 2f) {
				unit.mounts[otherSide].side = !unit.mounts[otherSide].side;
				mount.side = !mount.side;
			}

			if (rotate && (mount.rotate || mount.shoot) && can) {
				float axisX = unit.x + Angles.trnsx(unit.rotation - 90, x, y),
						axisY = unit.y + Angles.trnsy(unit.rotation - 90, x, y);

				mount.targetRotation = Angles.angle(axisX, axisY, mount.aimX, mount.aimY) - unit.rotation;
				mount.rotation = Angles.moveToward(mount.rotation, mount.targetRotation, rotateSpeed * Time.delta);
			} else if (!rotate) {
				mount.rotation = 0;
				mount.targetRotation = unit.angleTo(mount.aimX, mount.aimY);
			}

			if (mount.shoot && can &&
					(!useAmmo || unit.ammo > 0 || !Vars.state.rules.unitAmmo || unit.team.rules().infiniteAmmo) &&
					(!alternate || mount.side == flipSprite) &&
					unit.vel.len() >= mount.weapon.minShootVelocity &&
					mount.reload <= 0.0001f &&
					Angles.within(rotate ? mount.rotation : unit.rotation, mount.targetRotation, mount.weapon.shootCone)) {
				shoot(unit, mount, bulletX, bulletY, shootAngle);

				mount.reload = reload;

				if (useAmmo) {
					unit.ammo--;
					if (unit.ammo < 0) unit.ammo = 0;
				}
			}
		}
	}

	@Override
	protected void shoot(Unit unit, WeaponMount mount, float shootX, float shootY, float rotation) {
		if (chargeCondition == null) {
			super.shoot(unit, mount, shootX, shootY, rotation);
		}
	}

	protected Bullet bulletC(Unit unit, float shootX, float shootY, float angle, float lifescl, float charge) {
		return bullet.create(unit, unit.team,
				shootX,
				shootY,
				angle, bullet.damage + charge, (1f - velocityRnd) + Mathf.random(velocityRnd), lifescl, null);
	}

	public static class ChargeMount extends WeaponMount {
		public float timer = 0f, charge = 0f;

		public ChargeMount(Weapon weapon) {
			super(weapon);
		}
	}
}
