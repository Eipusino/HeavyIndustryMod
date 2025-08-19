package heavyindustry.world.blocks.power;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.TextField.TextFieldFilter;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.graphics.Drawn;
import heavyindustry.util.Utils;
import mindustry.gen.Building;
import mindustry.ui.Styles;
import mindustry.world.blocks.power.PowerBlock;
import mindustry.world.meta.Env;
import mindustry.world.meta.StatUnit;

public class ConfigurablePowerVoid extends PowerBlock {
	public float initialPowerConsumption = 1000f;
	public TextureRegion colorRegion;

	public ConfigurablePowerVoid(String name) {
		super(name);

		envEnabled = Env.any;

		outputsPower = false;
		consumesPower = true;
		configurable = saveConfig = true;
		canOverdrive = false;

		consumePowerDynamic((ConfigurablePowerVoidBuild tile) -> tile.powerConsumption / 60f);

		config(Float.class, (ConfigurablePowerVoidBuild tile, Float amount) -> tile.powerConsumption = amount);
	}

	@Override
	public void load() {
		super.load();
		colorRegion = Core.atlas.find(name + "-strobe");
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = ConfigurablePowerVoidBuild::new;
	}

	public class ConfigurablePowerVoidBuild extends Building {
		public float powerConsumption = initialPowerConsumption;

		@Override
		public void draw() {
			super.draw();

			Drawn.setStrobeColor();
			Draw.rect(colorRegion, x, y);
			Draw.color();
		}

		@Override
		public void buildConfiguration(Table table) {
			table.table(Styles.black5, t -> {
				t.margin(6f);
				t.field(String.valueOf(powerConsumption), text -> {
					configure(Strings.parseFloat(text));
				}).width(120).valid(Strings::canParsePositiveFloat).get().setFilter(TextFieldFilter.floatsOnly);
				t.add(Utils.statUnitName(StatUnit.powerSecond)).left();
			});
		}

		@Override
		public Object config() {
			return powerConsumption;
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.f(powerConsumption);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			powerConsumption = read.f();
		}
	}
}
