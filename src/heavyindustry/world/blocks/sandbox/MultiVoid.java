package heavyindustry.world.blocks.sandbox;

import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

public class MultiVoid extends Block {
	public MultiVoid(String name) {
		super(name);

		envEnabled = Env.any;

		update = solid = acceptsItems = hasLiquids = true;
		liquidCapacity = 10000f;
		group = BlockGroup.transportation;
	}

	@Override
	public void setBars() {
		super.setBars();
		removeBar("liquid");
	}

	@Override
	public boolean canReplace(Block other) {
		if (other.alwaysReplace) return true;
		return other.replaceable && (other != this || rotate) && group != BlockGroup.none && (other.group == BlockGroup.transportation || other.group == BlockGroup.liquids) &&
				(size == other.size || (size >= other.size && ((subclass != null && subclass == other.subclass) || group.anyReplace)));
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = MultiVoidBuild::new;
	}

	public class MultiVoidBuild extends Building {
		@Override
		public boolean acceptItem(Building source, Item item) {
			return enabled;
		}

		@Override
		public void handleItem(Building source, Item item) {}

		@Override
		public boolean acceptLiquid(Building source, Liquid liquid) {
			return enabled;
		}

		@Override
		public void handleLiquid(Building source, Liquid liquid, float amount) {}
	}
}
