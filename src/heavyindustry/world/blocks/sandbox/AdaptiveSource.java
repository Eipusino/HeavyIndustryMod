package heavyindustry.world.blocks.sandbox;

import arc.graphics.Color;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.meta.Stat;

import static mindustry.Vars.content;

public class AdaptiveSource extends Block {
	public short itemsPerSecond;

	public float powerProduction = 1000000 / 60f;
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
		addBar("health", entity -> new Bar("stat.health", Pal.health, entity::healthf).blink(Color.white));
	}

	@Override
	public boolean outputsItems() {
		return true;
	}

	public class AdaptiveSourceBuild extends Building implements HeatBlock {
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
		public float getPowerProduction(){
			return enabled ? powerProduction : 0f;
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

