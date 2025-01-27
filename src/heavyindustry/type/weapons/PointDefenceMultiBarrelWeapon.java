package heavyindustry.type.weapons;

import heavyindustry.util.*;
import mindustry.entities.units.*;
import mindustry.gen.*;

public class PointDefenceMultiBarrelWeapon extends MultiBarrelWeapon {
	protected static WeaponMount tmp;

	public PointDefenceMultiBarrelWeapon() {
		this("");
	}

	public PointDefenceMultiBarrelWeapon(String name) {
		super(name);
	}

	@Override
	public void update(Unit unit, WeaponMount mount) {
		tmp = mount;
		super.update(unit, mount);
	}

	@Override
	protected Teamc findTarget(Unit unit, float x, float y, float range, boolean air, boolean ground) {
		return Utils.nearestBullet(x, y, range, b -> b.team != unit.team && b.type.hittable && b.vel.len2() < 5f * 5f);
	}

	@Override
	protected boolean checkTarget(Unit unit, Teamc target, float x, float y, float range) {
		boolean bullet = (target instanceof Bullet b && (b.hitSize <= 0f || b.type == null));
		if (bullet) tmp.retarget = 5f;
		return super.checkTarget(unit, target, x, y, range) || bullet;
	}
}
