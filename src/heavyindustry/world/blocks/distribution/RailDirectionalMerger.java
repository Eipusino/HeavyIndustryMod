package heavyindustry.world.blocks.distribution;

import arc.util.Nullable;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Edges;

public class RailDirectionalMerger extends RailDirectionalRouter {
	public RailDirectionalMerger(String name) {
		super(name);
	}

	public class RailDirectionalMergerBuild extends RailDirectionalRouterBuild {
		@Nullable
		public Building target() {
			if (front() == null) return null;
			if (front().team == team && front().acceptItem(this, current)) {
				return front();
			}
			return null;
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			return current == null && items.total() == 0 && (sortItem == null || item == sortItem) &&
					(Edges.getFacingEdge(source.tile, tile).relativeTo(tile) != (rotation + 2) % 4);
		}

		@Override
		public int removeStack(Item item, int amount) {
			int removed = super.removeStack(item, amount);
			if (item == current) current = null;
			return removed;
		}
	}
}
