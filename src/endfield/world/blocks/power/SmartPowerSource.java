package endfield.world.blocks.power;

import arc.util.Time;
import mindustry.gen.Building;
import mindustry.world.blocks.power.PowerBlock;
import mindustry.world.meta.Env;

public class SmartPowerSource extends PowerBlock {
	public float powerOverhead = 1000f / 60f;

	public SmartPowerSource(String name) {
		super(name);

		envEnabled = Env.any;

		outputsPower = true;
		consumesPower = false;
		canOverdrive = false;
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = SmartPowerSourceBuild::new;
	}

	public class SmartPowerSourceBuild extends Building {
		@Override
		public float getPowerProduction() {
			return enabled ? power.graph.getPowerNeeded() / Time.delta + powerOverhead : 0;
		}
	}
}
