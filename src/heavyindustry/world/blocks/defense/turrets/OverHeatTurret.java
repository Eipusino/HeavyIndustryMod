package heavyindustry.world.blocks.defense.turrets;

import arc.Core;
import arc.math.Mathf;
import arc.util.Strings;
import arc.util.Time;
import mindustry.entities.bullet.BulletType;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.defense.turrets.ItemTurret;

public class OverHeatTurret extends ItemTurret {
	// overheat amount required to overheat
	public float overheatAmount = 25f;
	// how much time needed to cooldown turret
	public float timeToCooldown = 240;

	public OverHeatTurret(String name) {
		super(name);
	}

	@Override
	public void setBars() {
		super.setBars();
		addBar("over-heat", (OverHeatTurretBuild tile) -> new Bar(
				() -> Core.bundle.format("bar.over-heat", Strings.autoFixed((tile.overHeat) * 100, 0)),
				() -> Pal.redDust,
				() -> tile.overHeat / overheatAmount
		));
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = OverHeatTurretBuild::new;
	}

	public class OverHeatTurretBuild extends ItemTurretBuild {
		public float overHeat = 0;

		@Override
		public void updateTile() {
			//coolDown progress
			if (!isShooting() || !hasAmmo()) {
				overHeat = Mathf.lerpDelta(overHeat, 0f, 1f / 20f);
			}
			super.updateTile();
		}

		@Override
		public void shoot(BulletType type) {
			if (overHeat <= overheatAmount) {
				super.shoot(type);
				overHeat += 0.7f * Time.delta;
			} else {
				if (timeToCooldown >= 0f) {
					timeToCooldown -= Time.delta;
				} else {
					overHeat = 0f;
				}
			}
		}
	}
}
