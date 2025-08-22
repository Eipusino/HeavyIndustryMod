package heavyindustry.world.blocks.production;

import arc.Core;
import arc.math.Mathf;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.heat.HeatConsumer;

public class HeatDrill extends DrawerDrill {
	/** Base heat requirement for 100% efficiency. */
	public float heatRequirement = 10f;
	/** After heat meets this requirement, excess heat will be scaled by this number. */
	public float overheatScale = 1f;

	public HeatDrill(String name) {
		super(name);
	}

	@Override
	public void setBars() {
		super.setBars();

		addBar("heat", (HeatDrillBuild tile) -> new Bar(() ->
				Core.bundle.format("bar.heatpercent", (int) (tile.heat + 0.01f), (int) (tile.efficiencyScale() * 100 + 0.01f)),
				() -> Pal.lightOrange,
				() -> tile.heat / heatRequirement)
		);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = HeatDrillBuild::new;
	}

	public class HeatDrillBuild extends DrawerDrillBuild implements HeatConsumer {
		public float[] sideHeat = new float[4];
		public float heat = 0f;

		@Override
		public void updateTile() {
			heat = calculateHeat(sideHeat);

			super.updateTile();
		}

		@Override
		public float heatRequirement() {
			return heatRequirement;
		}

		@Override
		public float[] sideHeat() {
			return sideHeat;
		}

		public float warmupTarget() {
			return Mathf.clamp(heat / heatRequirement);
		}

		@Override
		public float efficiencyScale() {
			float over = Math.max(heat - heatRequirement, 0f);
			return warmupTarget() + over / heatRequirement * overheatScale;
		}

		@Override
		public float timeScale() {
			float over = Math.max(heat - heatRequirement, 0f);
			return timeScale * (warmupTarget() + over / heatRequirement * overheatScale);
		}
	}
}
