package heavyindustry.world.blocks.power;

import arc.math.Mathf;
import arc.struct.EnumSet;
import mindustry.world.blocks.power.PowerGenerator;
import mindustry.world.meta.Attribute;
import mindustry.world.meta.Env;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.state;

public class LunarGenerator extends PowerGenerator {
	public LunarGenerator(String name) {
		super(name);

		flags = EnumSet.of();
		envEnabled = Env.any;
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.remove(generationType);
		stats.add(generationType, powerProduction * 60.0f, StatUnit.powerSecond);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = LunarGeneratorBuild::new;
	}

	public class LunarGeneratorBuild extends GeneratorBuild {
		@Override
		public void updateTile() {
			productionEfficiency = enabled ? Mathf.maxZero(
					Attribute.light.env() +
							(state.rules.lighting ? 1f + state.rules.ambientLight.a : 1f)
			) : 0f;
		}
	}
}
