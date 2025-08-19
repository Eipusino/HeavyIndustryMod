package heavyindustry.world.blocks.production;

import arc.Core;
import arc.math.Mathf;
import arc.util.Strings;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.Fracker;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

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

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = GeneratorFrackerBuild::new;
	}

	public class GeneratorFrackerBuild extends FrackerBuild {
		@Override
		public float getPowerProduction() {
			return Math.max(validTiles + boost + (attribute == null ? 0 : attribute.env()), 0) * powerProduction;
		}
	}
}
