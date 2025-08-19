package heavyindustry.world.blocks.sandbox;

import mindustry.Vars;
import mindustry.type.Item;
import mindustry.type.Liquid;

public class RandomSource extends AdaptiveSource {
	public short itemsPerSecond;

	public RandomSource(String name) {
		super(name);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = RandomSourceBuild::new;
	}

	public class RandomSourceBuild extends AdaptiveSourceBuild {
		protected float counter;

		@Override
		public void updateTile() {
			if (proximity.isEmpty()) return;

			counter += edelta();
			float limit = 60f / itemsPerSecond;

			if (outputItems == null) outputItems = Vars.content.items();

			while (counter >= limit) {
				for (int i = 0; i < outputItems.size; i++) {
					Item item = outputItems.random();

					items.set(item, 1);
					dump(item);
					items.set(item, 0);
					counter -= limit;
				}
			}

			liquids.clear();

			if (outputLiquids == null) outputLiquids = Vars.content.liquids();

			for (int i = 0; i < outputLiquids.size; i++) {
				Liquid liquid = outputLiquids.random();
				liquids.add(liquid, liquidCapacity);
				dumpLiquid(liquid);
			}
		}
	}
}
