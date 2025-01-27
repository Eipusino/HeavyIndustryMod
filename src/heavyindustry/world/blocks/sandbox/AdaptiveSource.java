package heavyindustry.world.blocks.sandbox;

import mindustry.type.*;
import mindustry.world.blocks.heat.*;
import mindustry.world.blocks.sandbox.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class AdaptiveSource extends PowerSource {
	public short itemsPerSecond;

	public float heatOutput = 1000f;

	protected AdaptiveSource(String name) {
		super(name);
		hasItems = true;
		hasLiquids = true;
		update = true;
		displayFlow = false;
		canOverdrive = true;
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.remove(Stat.itemCapacity);
		stats.remove(Stat.liquidCapacity);
	}

	@Override
	public void setBars() {
		super.setBars();
		removeBar("items");
		removeBar("liquids");
		removeBar("connections");
	}

	@Override
	public boolean outputsItems() {
		return true;
	}

	public class AdaptiveSourceBuild extends PowerSourceBuild implements HeatBlock {
		protected float counter;

		@Override
		public void updateTile() {
			if (proximity.isEmpty()) return;

			counter += edelta();
			float limit = 60f / itemsPerSecond;

			while (counter >= limit) {
				for (Item item : content.items()) {
					items.set(item, 1);
					dump(item);
					items.set(item, 0);
					counter -= limit;
				}
			}

			liquids.clear();
			for (Liquid liquid : content.liquids()) {
				liquids.add(liquid, liquidCapacity);
				dumpLiquid(liquid);
			}
		}

		@Override
		public float heat() {
			return heatOutput;
		}

		@Override
		public float heatFrac() {
			return heatOutput;
		}
	}
}

