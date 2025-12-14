package heavyindustry.world.blocks.distribution;

import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Edges;
import org.jetbrains.annotations.Nullable;

public class DirectionalMerger extends DirectionalRouter {
	public DirectionalMerger(String name) {
		super(name);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = RailDirectionalMergerBuild::new;
	}

	public class RailDirectionalMergerBuild extends RailDirectionalRouterBuild {
		public @Nullable Building target() {
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
