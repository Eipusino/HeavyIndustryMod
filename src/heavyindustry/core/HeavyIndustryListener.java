package heavyindustry.core;

import arc.ApplicationCore;
import arc.ApplicationListener;
import arc.math.Mathf;
import arc.struct.Seq;
import heavyindustry.entities.HEntity;
import mindustry.Vars;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.pattern.ShootPattern;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

public class HeavyIndustryListener implements ApplicationListener {
	static float[] bulletDps, unitDps;
	static boolean[] powerful;

	HeavyIndustryListener() {
		if (Vars.platform instanceof ApplicationCore core) {
			core.add(this);
		}
	}

	@Override
	public void update() {
		HEntity.update();
	}

	public static float getUnitDps(UnitType unit) {
		if (unit.id >= unitDps.length) return 0f;
		return unitDps[unit.id];
	}

	public static float getBulletDps(BulletType bullet) {
		if (bullet.id >= bulletDps.length) return 0f;
		return bulletDps[bullet.id];
	}

	public static boolean getPowerful(UnitType unit) {
		if (unit.id >= powerful.length) return false;
		return powerful[unit.id];
	}

	public static void updateInit() {
		Seq<BulletType> bullets = Vars.content.bullets();
		Seq<UnitType> units = Vars.content.units();

		bulletDps = new float[bullets.size];
		unitDps = new float[units.size];

		powerful = new boolean[units.size];

		for (BulletType b : bullets) {
			updateBullet(b);
		}
		for (UnitType u : units) {
			updateUnit(u);
			updatePowerful(u);
		}
	}

	static float updateUnit(UnitType unit) {
		if (unitDps[unit.id] == 0f) {
			unitDps[unit.id] = 0.000001f;
			float damage = 0f;
			for (Weapon w : unit.weapons) {
				ShootPattern p = w.shoot;
				float d;
				if (!w.shootOnDeath && !w.bullet.killShooter) {
					d = (updateBullet(w.bullet) * p.shots * (w.continuous ? w.bullet.lifetime / 5f : 1f)) / w.reload;
				} else {
					d = updateBullet(w.bullet) * p.shots;
				}
				damage += d + (Mathf.pow(unit.hitSize, 0.75f) * unit.crashDamageMultiplier);
			}
			unitDps[unit.id] = damage;
		}
		return unitDps[unit.id];
	}

	static float updateBullet(BulletType type) {
		if (bulletDps[type.id] == 0f) {
			//recursion
			bulletDps[type.id] = type.damage;
			float damage = type.damage + type.splashDamage;

			if (type.fragBullet != null) damage += type.fragBullets * updateBullet(type.fragBullet);
			if (type.lightning > 0) {
				damage += type.lightning * Mathf.pow(type.lightningLength, 0.75f) * Math.max(type.lightningType != null ? updateBullet(type.lightningType) : 0f, type.lightningDamage);
			}
			if (type.intervalBullet != null) {
				damage += (updateBullet(type.intervalBullet) * type.intervalBullets) / type.bulletInterval;
			}
			//if (type.instantDisappear)
			if (type.spawnUnit != null) {
				damage += updateUnit(type.spawnUnit);
			}
			if (type.despawnUnit != null) {
				damage += updateUnit(type.despawnUnit) * type.despawnUnitCount;
			}
			bulletDps[type.id] = Math.max(0.00001f, damage);
		}
		return bulletDps[type.id];
	}

	static void updatePowerful(UnitType type) {
		switch (type.name) {
			case "extra-utilities-regency", "new-horizon-guardian", "new-horizon-pester", "new-horizon-nucleoid" -> powerful[type.id] = true;
			default -> powerful[type.id] = false;
		}
	}
}
