package endfield.world.blocks.defense;

import arc.util.Time;
import endfield.world.meta.Stats2;
import mindustry.world.blocks.defense.Wall;

public class HealingWall extends Wall {
	// reload between healing
	public float healReload = 1f;
	// how much heal does wall recieve
	public float healPercent = 7f;

	public HealingWall(String name) {
		super(name);
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.add(Stats2.healPercent, healPercent);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = HealingWallBuild::new;
	}

	public class HealingWallBuild extends WallBuild {
		public float charge = 0;
		boolean canHeal = true;

		@Override
		public void updateTile() {
			canHeal = true;
			charge += Time.delta;

			if (charge >= healReload && health() < maxHealth()) {
				charge = 0f;
				if (health() >= maxHealth()) canHeal = false;

				heal((maxHealth() / 5) * (healPercent) / 100f);
				recentlyHealed();
			}
		}
	}
}
