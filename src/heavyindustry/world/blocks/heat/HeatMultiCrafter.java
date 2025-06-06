package heavyindustry.world.blocks.heat;

import arc.Core;
import arc.math.Mathf;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.world.blocks.production.MultiCrafter;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.heat.HeatConsumer;

/**
 * Why is the heat separated separately? Mainly to avoid various strange small problems.
 * <p>At present, there is no testing. If there are any bugs, please make sure to report them to the author.
 *
 * @author Eipusino
 */
public class HeatMultiCrafter extends MultiCrafter {
	protected boolean consumeHeat, outputHeat;

	public HeatMultiCrafter(String name) {
		super(name);

		rotateDraw = false;
		rotate = true;
		canOverdrive = false;
		drawArrow = true;
	}

	@Override
	public void init() {
		for (CraftPlan craftPlan : craftPlans) {
			if (craftPlan.heatOutput > 0f) {
				outputHeat = true;
			}
			if (craftPlan.heatRequirement > 0f) {
				consumeHeat = true;
			}
		}
		super.init();
	}

	@Override
	public void setBars() {
		super.setBars();

		if (outputHeat) addBar("heatoutput", (HeatMultiCrafterBuild tile) -> new Bar("bar.heat", Pal.lightOrange, () -> tile.craftPlan != null ? tile.heat / tile.craftPlan.heatOutput : 0));
		if (consumeHeat) addBar("heatconsume", (HeatMultiCrafterBuild tile) -> new Bar(
				() -> Core.bundle.format("bar.heatpercent", tile.heatRequirement, tile.craftPlan != null ? Math.min((tile.heatRequirement / tile.craftPlan.heatRequirement * 100), tile.craftPlan.maxHeatEfficiency) : 0),
				() -> Pal.lightOrange,
				() -> tile.craftPlan != null ? tile.heatRequirement / tile.craftPlan.heatRequirement : 0)
		);
	}

	public class HeatMultiCrafterBuild extends MultiCrafterBuild implements HeatBlock, HeatConsumer {
		public float heat;
		public float heatRequirement;
		public float[] sideHeat = new float[4];

		@Override
		public void updateTile() {
			super.updateTile();
			if (craftPlan == null) return;

			heat = Mathf.approachDelta(heat, craftPlan.heatOutput * efficiency, craftPlan.warmupRate * delta());

			if (craftPlan.heatRequirement > 0) {
				heatRequirement = calculateHeat(sideHeat);
			}
		}

		@Override
		public void updateEfficiencyMultiplier() {
			super.updateEfficiencyMultiplier();
			if (craftPlan == null) return;

			if (craftPlan.heatRequirement > 0) {
				efficiency *= Math.min(Math.max(heatRequirement / craftPlan.heatRequirement, cheating() ? craftPlan.maxHeatEfficiency : 0f), craftPlan.maxHeatEfficiency);
			}
		}

		@Override
		public float heat() {
			return heat;
		}

		@Override
		public float heatFrac() {
			return craftPlan != null ? heat / craftPlan.heatOutput : 0f;
		}

		@Override
		public float[] sideHeat() {
			return sideHeat;
		}

		@Override
		public float heatRequirement() {
			return craftPlan != null ? craftPlan.heatRequirement : 0f;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.f(heat);
			write.f(heatRequirement);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			heat = read.f();
			heatRequirement = read.f();
		}
	}
}
