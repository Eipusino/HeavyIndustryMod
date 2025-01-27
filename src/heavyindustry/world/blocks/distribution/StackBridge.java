package heavyindustry.world.blocks.distribution;

import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.distribution.StackConveyor.*;

import static mindustry.Vars.*;

/**
 * Multiple items can be transported together to the other end.
 * And it can also increase the packaging point of the stack conveyor belt at the output end to the highest speed.
 */
public class StackBridge extends BufferedItemBridge {
	protected Item lastItem;
	protected int amount = 0;

	public StackBridge(String name) {
		super(name);
	}

	public class StackBridgeBuild extends BufferedItemBridgeBuild {
		protected Item getLastItem() {
			return lastItem;
		}

		protected void setLastItem(Item v) {
			lastItem = v;
		}

		protected int getAmount() {
			return amount;
		}

		protected void setAmount(int v) {
			amount = v;
		}

		@Override
		public void updateTile() {
			if (getLastItem() == null || !items.has(getLastItem())) {
				setLastItem(items.first());
			}
			super.updateTile();
		}

		@Override
		public void updateTransport(Building other) {
			if (items.total() >= block.itemCapacity && other instanceof StackBridgeBuild ot && ot.team == team && ot.items.total() < block.itemCapacity) {
				ot.setAmount(items.total());
				ot.items.add(lastItem, ot.getAmount());
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
