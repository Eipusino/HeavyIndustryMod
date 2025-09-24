package heavyindustry.world.blocks.power;

import arc.scene.ui.layout.Table;
import mindustry.gen.Icon;
import mindustry.ui.Styles;
import mindustry.world.blocks.power.PowerGenerator;

public class HandGenerator extends PowerGenerator {
	public float max = 150f, number = 20f;

	public HandGenerator(String name) {
		super(name);

		configurable = true;
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = HandGeneratorBuild::new;
	}

	public class HandGeneratorBuild extends GeneratorBuild {
		@Override
		public void update() {
			if (productionEfficiency > 0) productionEfficiency -= 0.5f;
		}

		@Override
		public void buildConfiguration(Table table) {
			table.button(Icon.upOpen, Styles.defaulti, () -> {
				if (productionEfficiency < max) productionEfficiency += number; //
			}).size(45);
		}
	}
}
