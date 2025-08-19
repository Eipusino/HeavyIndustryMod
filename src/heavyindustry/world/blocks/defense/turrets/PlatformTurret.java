package heavyindustry.world.blocks.defense.turrets;

import arc.struct.ObjectMap;
import mindustry.content.Bullets;
import mindustry.entities.bullet.BulletType;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

public class PlatformTurret extends Turret {
	public BulletType shootType = Bullets.placeholder;

	public PlatformTurret(String name) {
		super(name);
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.add(Stat.ammo, StatValues.ammo(ObjectMap.of(this, shootType)));
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = PlatformTurretBuild::new;
	}

	public class PlatformTurretBuild extends TurretBuild {
		@Override
		public BulletType useAmmo() {
			return shootType;
		}

		@Override
		public boolean hasAmmo() {
			return true;
		}

		@Override
		public BulletType peekAmmo() {
			return shootType;
		}
	}
}
