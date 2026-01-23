package endfield.world.blocks.defense.turrets;

import arc.Core;
import arc.math.Mathf;
import arc.util.Strings;
import arc.util.Time;
import mindustry.entities.bullet.BulletType;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.defense.turrets.ItemTurret;


// thanks to Sputnuc for giving me this code
public class AccelTurret extends ItemTurret {
	public float speedUpPerShoot = 2;
	public float maxAccel = 0.5f;
	public float cooldownSpeed = 1;

	public AccelTurret(String name) {
		super(name);
	}

	@Override
	public void setBars() {
		super.setBars();
		addBar("speed-up", (AccelTurretBuild tile) -> new Bar(
				() -> Core.bundle.format("bar.speed-up", Strings.autoFixed((tile.speedUp) * 100, 0)),
				() -> Pal.powerBar,
				() -> tile.speedUp / maxAccel
		));
	}

	@Override
	public void initBuilding() {
		if (buildType == null) buildType = AccelTurretBuild::new;
	}

	public class AccelTurretBuild extends ItemTurretBuild {
		public float speedUp = 0;

		@Override
		public void updateTile() {
			//coolDown progress
			if (!isShooting() || !hasAmmo()) {
				speedUp = Mathf.lerpDelta(speedUp, 0, cooldownSpeed / 20);
			}
			super.updateTile();
		}

		@Override
		public void updateShooting() {
			//override shooting method
			if (reloadCounter >= reload) {

				BulletType type = peekAmmo();

				shoot(type);
				reloadCounter = 0;
			} else {
				reloadCounter += (1 + speedUp) * Time.delta * peekAmmo().reloadMultiplier * baseReloadSpeed();
			}
		}

		@Override
		public void shoot(BulletType type) {
			//speedUp per shoot
			super.shoot(type);
			if (speedUp < maxAccel) {
				speedUp += speedUpPerShoot * Time.delta;
			} else {
				speedUp = maxAccel;
			}
		}
	}
}