package heavyindustry.type.weapons;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import heavyindustry.entities.bullet.EndMissileBulletType;
import heavyindustry.util.ObjectFloatMap2;
import mindustry.entities.Effect;
import mindustry.entities.units.WeaponMount;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;
import mindustry.gen.Sounds;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.type.Weapon;

public class EndLauncherWeapon extends Weapon {
	//x, y, rotation
	public float[] targets = {
			-8f, 0f, -45f,
			12f, 10f, -35f,
			25f, 20f, -25f,
			28f, 30f, -15f
	};

	public EndLauncherWeapon(String name) {
		super(name);

		mirror = false;
		alternate = false;
		top = false;
		x = y = 0f;
		shootWarmupSpeed = 0.025f;
		shootCone = 360f;
		minWarmup = 0.99f;
		reload = 60f * 2.5f;

		//shootSound = Sounds.missileSmall;
		shootSound = Sounds.missile;
		mountType = EndLauncherMount::new;

		bullet = new EndMissileBulletType();
	}

	@Override
	public void update(Unit unit, WeaponMount mount) {
		if (mount instanceof EndLauncherMount lm) updateEndLauncher(unit, lm);
	}

	public void updateEndLauncher(Unit unit, EndLauncherMount mount) {
		boolean can = unit.canShoot();
		mount.reload = Math.max(mount.reload - Time.delta * unit.reloadMultiplier, 0);
		mount.recoil = Mathf.approachDelta(mount.recoil, 0, unit.reloadMultiplier / recoilTime);

		mount.smoothReload = Mathf.lerpDelta(mount.smoothReload, mount.reload / reload, smoothReloadSpeed);
		mount.charge = mount.charging && shoot.firstShotDelay > 0 ? Mathf.approachDelta(mount.charge, 1, 1 / shoot.firstShotDelay) : 0;

		float warmupTarget = (can && mount.shoot) || (mount.burstCount > 0) ? 1f : 0f;
		if (linearWarmup) {
			mount.warmup = Mathf.approachDelta(mount.warmup, warmupTarget, shootWarmupSpeed);
		} else {
			mount.warmup = Mathf.lerpDelta(mount.warmup, warmupTarget, shootWarmupSpeed);
		}

		mount.updateTarget();

		if (mount.shoot && mount.burstCount <= 0 && can && mount.warmup > minWarmup && mount.reload <= 0.0001f) {
			mount.reload = reload;
			mount.burstCount = 8 * 6;
			mount.burstTime = 0f;
		}

		if (mount.burstCount > 0) {
			mount.reload = reload;

			if (mount.burstTime <= 0f) {
				int mc = targets.length / 3;
				int hs = mount.totalShots / 2;
				boolean flip = mount.totalShots % 2 == 1;
				Vec3 pos = getShootPosition(unit, mount, hs % mc, flip);

				shootAlt(unit, mount, pos.x, pos.y, pos.z);

				mount.burstTime = 1f;
				mount.burstCount--;
				mount.totalShots++;
			}
			mount.burstTime -= Time.delta;
		}
	}

	protected void shootAlt(Unit unit, EndLauncherMount mount, float x, float y, float rot) {
		//mount.bullet = bullet.create(unit, shooter, unit.team, bulletX, bulletY, angle, -1f, (1f - velocityRnd) + Mathf.random(velocityRnd), lifeScl, null, mover, mount.aimX, mount.aimY);
		Bullet b = bullet.create(unit, unit.team, x, y, rot);
		b.aimX = mount.aimX;
		b.aimY = mount.aimY;
		EndLauncherData data = new EndLauncherData();
		data.mount = mount;
		data.ret = Mathf.random(5f);
		b.data = data;

		shootSound.at(x, y, Mathf.random(soundPitchMin, soundPitchMax));
		bullet.shootEffect.at(x, y, rot, bullet.hitColor, unit);
		Effect.shake(shake, shake, x, y);
	}

	@Override
	public void draw(Unit unit, WeaponMount mount) {}

	@Override
	public void drawOutline(Unit unit, WeaponMount mount) {
		drawBase(unit, mount);
	}

	public Vec3 getShootPosition(Unit unit, WeaponMount mount, int idx, boolean flip) {
		float x = unit.x, y = unit.y, rot = unit.rotation;
		float ang = -41f;
		float warm = mount.warmup;
		int i = idx * 3;
		int sign = flip ? -1 : 1;
		float tx = targets[i], ty = targets[i + 1], tr = targets[i + 2];
		float srot = rot + (ang + tr * warm) * sign;

		Vec2 v2 = Tmp.v2.trns(unit.rotation - 90f, (60f + tx * warm) * sign, -65f + ty * warm).add(x, y);
		Tmp.v3.trns(srot, 43f * 2f);
		v2.add(Tmp.v3);

		return Tmp.v31.set(v2.x, v2.y, srot);
	}

	public void drawBase(Unit unit, WeaponMount mount) {
		int count = targets.length;

		float x = unit.x, y = unit.y, rot = unit.rotation;
		float ang = -41f;
		float warm = mount.warmup;
		unit.type.applyColor(unit);
		for (int i = 0; i < count; i += 3) {
			float tx = targets[i], ty = targets[i + 1], tr = targets[i + 2];
			float lxs = Draw.xscl;
			for (int sign : Mathf.signs) {
				Vec2 v2 = Tmp.v2.trns(unit.rotation - 90f, (60f + tx * warm) * sign, -65f + ty * warm);

				Draw.xscl = sign;
				drawWeapon(x + v2.x, y + v2.y, rot + (ang + tr * warm) * sign);
			}
			Draw.xscl = lxs;
		}
		Draw.reset();
	}

	public void drawWeapon(float x, float y, float rotation) {
		Vec2 v = Tmp.v1.trns(rotation, 39f).add(x, y);
		TextureRegion reg = region;
		Draw.rect(reg, v.x, v.y, rotation - 90f);
	}

	public static class EndLauncherMount extends WeaponMount {
		public Seq<Teamc> targetSeq = new Seq<>(Teamc.class);
		public ObjectFloatMap2<Teamc> targets = new ObjectFloatMap2<>(Teamc.class);
		public int burstCount = 0;
		public float burstTime = 0f;

		public EndLauncherMount(Weapon weapon) {
			super(weapon);
		}

		public void updateTarget() {
			targetSeq.removeAll(t -> {
				float value = targets.increment(t, 0f, -Time.delta);
				boolean re = value <= 0f || (t instanceof Healthc h && !h.isValid());
				if (re) targets.remove(t);
				return re;
			});
		}

		public void removeTarget(Teamc target) {
			targets.remove(target);
			targetSeq.remove(target);
		}

		public void addTarget(Teamc target) {
			if (!targets.containsKey(target)) {
				targetSeq.add(target);
			}
			targets.put(target, 15f);
		}
	}

	public static class EndLauncherData {
		public EndLauncherMount mount;
		public Teamc current;
		public float ret;
	}
}
