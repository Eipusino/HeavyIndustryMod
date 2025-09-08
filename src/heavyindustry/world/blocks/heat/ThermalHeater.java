package heavyindustry.world.blocks.heat;

import arc.math.Mathf;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.blocks.power.ThermalGenerator;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawHeatOutput;
import mindustry.world.draw.DrawMulti;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class ThermalHeater extends ThermalGenerator {
	public float heatOutput = 5f, warmupRate = 0.15f;

	public ThermalHeater(String name) {
		super(name);

		drawer = new DrawMulti(new DrawDefault(), new DrawHeatOutput());
		rotateDraw = false;
		rotate = true;
		canOverdrive = false;
		drawArrow = true;
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.add(Stat.output, heatOutput * size * size, StatUnit.heatUnits);
	}

	@Override
	public void setBars() {
		super.setBars();
		addBar("heat", (ThermalHeaterBuild tile) -> new Bar("bar.heat", Pal.lightOrange, () -> Math.min(tile.heat / heatOutput, 1f)));
	}

	@Override
	public boolean rotatedOutput(int x, int y) {
		return false;
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = ThermalHeaterBuild::new;
	}

	public class ThermalHeaterBuild extends ThermalGeneratorBuild implements HeatBlock {
		public float heat;

		@Override
		public void updateTile() {
			super.updateTile();
			heat = Mathf.approachDelta(heat, heatOutput * efficiency * productionEfficiency, warmupRate * delta());
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
