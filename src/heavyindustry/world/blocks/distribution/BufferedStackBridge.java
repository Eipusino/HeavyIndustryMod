package heavyindustry.world.blocks.distribution;

import heavyindustry.world.blocks.distribution.StackBridge.StackBridgeBuild;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.distribution.BufferedItemBridge;
import mindustry.world.blocks.distribution.StackConveyor.StackConveyorBuild;

import static mindustry.Vars.world;

public class BufferedStackBridge extends BufferedItemBridge {
	public BufferedStackBridge(String name) {
		super(name);
	}

	public class BufferedStackBridgeBuild extends BufferedItemBridgeBuild {
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
			if (lastItem != null && items.total() >= block.itemCapacity && other instanceof StackBridgeBuild ot && ot.team == team && ot.items.total() < block.itemCapacity) {
				ot.amount = items.total();
				ot.items.add(lastItem, ot.amount);
				items.clear();
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
			return (!((items.any() && !items.has(item)) || (items.total() >= getMaximumAccepted(item)))) && other != null && block instanceof StackBridge bl && bl.linkValid(tile, other);
		}
	}
}
