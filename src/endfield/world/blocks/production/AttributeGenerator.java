package endfield.world.blocks.production;

import arc.Core;
import arc.math.Mathf;
import arc.util.Strings;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.AttributeCrafter;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

/**
 * Same as GeneratorCrafter, but power output is affected by Attribute.
 *
 * @author Eipusino
 */
public class AttributeGenerator extends AttributeCrafter {
	public float powerProduction = 1f;

	public AttributeGenerator(String name) {
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
	public void init() {
		removeConsumers(c -> c instanceof ConsumePower);
		super.init();
	}

	@Override
	public void setBars() {
		super.setBars();
		addBar("power", (AttributeGeneratorBuild tile) -> new Bar(
				() -> Core.bundle.format("bar.poweroutput", Strings.fixed(tile.getPowerProduction() * 60f * tile.timeScale(), 1)),
				() -> Pal.powerBar,
				() -> Mathf.num(tile.efficiency > 0f)
		));
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = AttributeGeneratorBuild::new;
	}

	public class AttributeGeneratorBuild extends AttributeCrafterBuild {
		@Override
		public float getPowerProduction() {
			return efficiency > 0f ? powerProduction * efficiencyMultiplier() : 0f;
		}
	}
}
