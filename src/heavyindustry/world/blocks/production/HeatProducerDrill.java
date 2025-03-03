package heavyindustry.world.blocks.production;

import arc.math.Mathf;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawHeatOutput;
import mindustry.world.draw.DrawMulti;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class HeatProducerDrill extends DrawerDrill {
	public float heatOutput = 5f;

	public HeatProducerDrill(String name) {
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
		stats.add(Stat.output, heatOutput, StatUnit.heatUnits);
	}

	@Override
	public void setBars() {
		super.setBars();
		addBar("heat", (HeatProducerDrillBuild tile) -> new Bar("bar.heat", Pal.lightOrange, tile::heatFrac));
	}

	public class HeatProducerDrillBuild extends DrawerDrillBuild implements HeatBlock {
		public float heat;

		@Override
		public void updateTile() {
			super.updateTile();
			heat = lastDrillSpeed == 0 ? Mathf.approachDelta(heat, 0f, 0.3f * delta()) : Mathf.approachDelta(heat, heatOutput, 0.3f * delta());
		}

		@Override
		public float heatFrac() {
			return heat / heatOutput;
		}

		@Override
		public float heat() {
			return heat;
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
