package heavyindustry.world.blocks.distribution;

import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.StackConveyor.StackConveyorBuild;

import static mindustry.Vars.world;

public class RailStackBridge extends RailItemBridge {
	public RailStackBridge(String name) {
		super(name);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = RailStackBridgeBuild::new;
	}

	public class RailStackBridgeBuild extends RailItemBridgeBuild {
		public Item lastItem;
		public int amount = 0;

		@Override
		public void updateTile() {
			if (lastItem == null || !items.has(lastItem)) {
				lastItem = items.first();
			}
			super.updateTile();
		}

		@Override
		public void updateTransport(Building other) {
			transportCounter += edelta();

			while (transportCounter >= transportTime) {
				if (lastItem != null && items.total() >= itemCapacity && other instanceof RailStackBridgeBuild ot && ot.team == team && ot.items.total() < itemCapacity) {
					ot.amount = items.total();
					ot.items.add(lastItem, ot.amount);
					items.clear();
				}
				transportCounter -= transportTime;
			}
		}

		@Override
		public void doDump() {
			for (int i = 0; i < 4; i++) {
				Building other = nearby(i);
				if (other instanceof StackConveyorBuild ot && ot.team == team && ot.link == -1) ot.cooldown = 0;
				dumpAccumulate();
			}
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			if (this == source && items.total() < block.itemCapacity) return true;
			Tile other = world.tile(link);
			return (!((items.any() && !items.has(item)) || (items.total() >= getMaximumAccepted(item)))) && other != null && linkValid(tile, other);
		}
	}
}
