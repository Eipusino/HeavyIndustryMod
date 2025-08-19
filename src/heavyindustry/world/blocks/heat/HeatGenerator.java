package heavyindustry.world.blocks.heat;

import arc.Core;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.heat.HeatConsumer;
import mindustry.world.blocks.power.PowerGenerator;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class HeatGenerator extends PowerGenerator {
	public float maxHeat = 180f;
	public float warmupSpeed = 0.4f;

	public HeatGenerator(String name) {
		super(name);
	}

	@Override
	public void init() {
		removeConsumers(c -> c instanceof ConsumePower);

		super.init();
	}

	@Override
	public void setBars() {
		super.setBars();

		addBar("heat", (HeatGeneratorBuild tile) -> new Bar(
				() -> Core.bundle.format("bar.heatpercent", Mathf.round(tile.heat()), Mathf.round(Mathf.clamp(tile.heat() / maxHeat) * 100)),
				() -> Pal.lightOrange,
				() -> tile.heat() / maxHeat
		));
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.add(Stat.input, maxHeat, StatUnit.heatUnits);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = HeatGeneratorBuild::new;
	}

	public class HeatGeneratorBuild extends GeneratorBuild implements HeatConsumer {
		public float[] sideHeat = new float[4];
		public float heat;
		public float totalProgress;
		public float warmup;

		@Override
		public void updateTile() {
			heat = calculateHeat(sideHeat);
			warmup = Mathf.lerpDelta(warmup, productionEfficiency > 0f ? 1f : 0f, warmupSpeed);
			totalProgress += productionEfficiency * Time.delta;
		}

		@Override
		public boolean shouldExplode() {
			return heat > 0f;
		}

		@Override
		public float totalProgress() {
			return totalProgress;
		}

		@Override
		public float warmup() {
			return warmup;
		}

		@Override
		public void updateEfficiencyMultiplier() {
			efficiency *= Mathf.clamp(heat);
			productionEfficiency = efficiency * Mathf.clamp(heat, 0f, maxHeat);
		}

		public float heat() {
			return heat;
		}

		@Override
		public float[] sideHeat() {
			return sideHeat;
		}

		@Override
		public float heatRequirement() {
			return maxHeat;
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.f(heat);
			write.f(warmup);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			heat = read.f();
			warmup = read.f();
		}
	}
}
