package endfield.world.blocks.distribution;

import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.blocks.distribution.StackConveyor.StackConveyorBuild;

import static mindustry.Vars.world;

/**
 * Multiple items can be transported together to the other end.
 * And it can also increase the packaging point of the stack conveyor belt at the output end to the highest speed.
 */
public class StackBridge extends ItemBridge {
	public StackBridge(String name) {
		super(name);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = StackBridgeBuild::new;
	}

	public class StackBridgeBuild extends ItemBridgeBuild {
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
				if (lastItem != null && items.total() >= itemCapacity && other instanceof StackBridgeBuild ot && ot.team == team && ot.items.total() < itemCapacity) {
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
