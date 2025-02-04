package heavyindustry.world.blocks.production;

import arc.*;
import arc.math.*;
import arc.util.*;
import mindustry.graphics.*;
import mindustry.ui.*;
import mindustry.world.blocks.production.*;
import mindustry.world.meta.*;

public class GeneratorFracker extends Fracker {
	public float powerProduction = 1f;

	public GeneratorFracker(String name) {
		super(name);
		hasPower = true;
		consumesPower = false;
		outputsPower = true;
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.add(Stat.basePowerGeneration, powerProduction * 60f, StatUnit.powerSecond);
	}

	@Override
	public void setBars() {
		super.setBars();
		addBar("power", (GeneratorFrackerBuild tile) -> new Bar(
				() -> Core.bundle.format("bar.poweroutput", Strings.fixed(tile.getPowerProduction() * 60f * tile.timeScale(), 1)),
				() -> Pal.powerBar,
				() -> Mathf.num(tile.efficiency > 0f)
		));
	}

	public class GeneratorFrackerBuild extends FrackerBuild {
		@Override
		public float getPowerProduction() {
			return Math.max(validTiles + boost + (attribute == null ? 0 : attribute.env()), 0) * powerProduction;
		}
	}
}
