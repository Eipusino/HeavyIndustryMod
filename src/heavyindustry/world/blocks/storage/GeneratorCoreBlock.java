package heavyindustry.world.blocks.storage;

import arc.Core;
import arc.func.Func;
import arc.util.Strings;
import heavyindustry.math.Mathm;
import mindustry.core.UI;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.storage.CoreBlock;

public class GeneratorCoreBlock extends CoreBlock {
	public float powerProduction = 60 / 60f;

	public GeneratorCoreBlock(String name) {
		super(name);
		hasPower = true;
		conductivePower = true;
		outputsPower = true;
		consumesPower = false;
	}

	@Override
	public void setBars() {
		super.setBars();
		addBar("poweroutput", (GeneratorCoreBuild tile) ->
				new Bar(() -> Core.bundle.format("bar.poweroutput", Strings.fixed(powerProduction * 60 + 0.0001f, 1)), () -> Pal.powerBar, () -> 1f));
		addBar("power", makePowerBalance());
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = GeneratorCoreBuild::new;
	}

	public static Func<Building, Bar> makePowerBalance() {
		return tile -> new Bar(
				() -> Core.bundle.format("bar.powerbalance",
						((tile.power.graph.getPowerBalance() >= 0 ? "+" : "") + UI.formatAmount((long) (tile.power.graph.getPowerBalance() * 60 + 0.0001f)))),
				() -> Pal.powerBar,
				() -> Mathm.clamp(tile.power.graph.getLastPowerProduced() / tile.power.graph.getLastPowerNeeded())
		);
	}

	public class GeneratorCoreBuild extends CoreBuild {
		@Override
		public void onProximityUpdate() {
			super.onProximityUpdate();

			if (!allowUpdate()) {
				enabled = false;
			}
		}

		@Override
		public float getPowerProduction() {
			return enabled ? powerProduction : 0f;
		}
	}
}
