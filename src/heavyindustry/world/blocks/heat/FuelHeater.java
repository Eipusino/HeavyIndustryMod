package heavyindustry.world.blocks.heat;

import arc.math.Mathf;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.consumers.ConsumeItemFilter;
import mindustry.world.consumers.ConsumeLiquidFilter;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawHeatOutput;
import mindustry.world.draw.DrawMulti;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class FuelHeater extends GenericCrafter {
	public float heatOutput = 5f, warmupRate = 0.15f;

	public @Nullable ConsumeItemFilter filterItem;
	public @Nullable ConsumeLiquidFilter filterLiquid;

	public FuelHeater(String name) {
		super(name);
		drawer = new DrawMulti(new DrawDefault(), new DrawHeatOutput());
		rotate = true;
		rotateDraw = false;
		canOverdrive = false;
		drawArrow = true;
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.add(Stat.output, heatOutput, StatUnit.heatUnits);
	}

	@Override
	public void setBars() {
		super.setBars();
		addBar("heat", (FuelHeaterBuild tile) -> new Bar("bar.heat", Pal.lightOrange, () -> Math.min(tile.heat / heatOutput, 1f)));
	}

	@Override
	public void init() {
		filterItem = findConsumer(c -> c instanceof ConsumeItemFilter);
		filterLiquid = findConsumer(c -> c instanceof ConsumeLiquidFilter);
		super.init();
	}

	public class FuelHeaterBuild extends GenericCrafterBuild implements HeatBlock {
		public float heat, efficiencyMultiplier = 1f;

		@Override
		public void updateEfficiencyMultiplier() {
			if (filterItem != null) {
				float m = filterItem.efficiencyMultiplier(this);
				if (m > 0) efficiencyMultiplier = m;
			} else if (filterLiquid != null) {
				float m = filterLiquid.efficiencyMultiplier(this);
				if (m > 0) efficiencyMultiplier = m;
			}
		}

		@Override
		public void updateTile() {
			super.updateTile();
			heat = Mathf.approachDelta(heat, heatOutput * efficiency * efficiencyMultiplier, warmupRate * delta());
		}

		@Override
		public float heat() {
			return heat;
		}

		@Override
		public float heatFrac() {
			return heat / heatOutput;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.f(heat);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			heat = read.f();
		}
	}
}
