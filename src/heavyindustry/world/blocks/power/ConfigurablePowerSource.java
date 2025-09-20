package heavyindustry.world.blocks.power;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.TextField.TextFieldFilter;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.util.Utils;
import mindustry.gen.Building;
import mindustry.ui.Styles;
import mindustry.world.blocks.power.PowerBlock;
import mindustry.world.meta.Env;
import mindustry.world.meta.StatUnit;

public class ConfigurablePowerSource extends PowerBlock {
	public float initialPowerProduction = 1000f;

	public ConfigurablePowerSource(String name) {
		super(name);

		envEnabled = Env.any;

		outputsPower = true;
		consumesPower = false;
		configurable = saveConfig = true;
		canOverdrive = false;

		config(Float.class, (ConfigurablePowerSourceBuild tile, Float amount) -> tile.powerProduction = amount);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = ConfigurablePowerSourceBuild::new;
	}

	public class ConfigurablePowerSourceBuild extends Building {
		public float powerProduction = initialPowerProduction;

		@Override
		public float getPowerProduction() {
			return powerProduction / 60f;
		}

		@Override
		public void buildConfiguration(Table table) {
			table.table(Styles.black5, t -> {
				t.margin(6f);
				t.field(String.valueOf(powerProduction), text -> {
					configure(Strings.parseFloat(text));
				}).width(120).valid(Strings::canParsePositiveFloat).get().setFilter(TextFieldFilter.floatsOnly);
				t.add(Utils.statUnitName(StatUnit.powerSecond)).left();
			});
		}

		@Override
		public Object config() {
			return powerProduction;
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.f(powerProduction);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			powerProduction = read.f();
		}
	}
}
