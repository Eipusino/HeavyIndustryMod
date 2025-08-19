package heavyindustry.world.blocks.production;

import arc.Core;
import arc.math.Mathf;
import arc.util.Strings;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

/**
 * A factory that can produce electricity.
 *
 * @author Eipusino
 */
public class GeneratorCrafter extends GenericCrafter {
	public float powerProduction = 1f;

	public GeneratorCrafter(String name) {
		super(name);

		hasPower = true;
	}

	@Override
	public void setStats() {
		super.setStats();
		if (powerProduction > 0) {
			stats.add(Stat.basePowerGeneration, powerProduction * 60f, StatUnit.powerSecond);
		}
	}

	@Override
	public void init() {
		if (powerProduction > 0) {
			removeConsumers(c -> c instanceof ConsumePower);

			consumesPower = false;
		}
		super.init();
	}

	@Override
	public void setBars() {
		super.setBars();
		if (hasPower && outputsPower && powerProduction > 0f) {
			addBar("power", (GeneratorCrafterBuild tile) -> new Bar(
					() -> Core.bundle.format("bar.poweroutput", Strings.fixed(tile.getPowerProduction() * 60f * tile.timeScale(), 1)),
					() -> Pal.powerBar,
					() -> Mathf.num(tile.efficiency > 0f)
			));
		}
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = GeneratorCrafterBuild::new;
	}

	public class GeneratorCrafterBuild extends GenericCrafterBuild {
		@Override
		public float getPowerProduction() {
			return powerProduction * warmup * efficiency;
		}
	}
}
