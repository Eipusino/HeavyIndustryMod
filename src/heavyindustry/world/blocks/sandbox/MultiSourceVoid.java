package heavyindustry.world.blocks.sandbox;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import heavyindustry.graphics.Drawn;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

public class MultiSourceVoid extends MultiSource {
	public TextureRegion rainbow;

	public MultiSourceVoid(String name) {
		super(name);

		envEnabled = Env.any;

		acceptsItems = hasLiquids = true;
	}

	@Override
	public void load() {
		super.load();

		rainbow = Core.atlas.find(name + "-rainbow");
	}

	@Override
	public boolean canReplace(Block other) {
		if (other.alwaysReplace) return true;
		return other.replaceable && (other != this || rotate) && group != BlockGroup.none && (other.group == BlockGroup.transportation || other.group == BlockGroup.liquids) &&
				(size == other.size || (size >= other.size && ((subclass != null && subclass == other.subclass) || group.anyReplace)));
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = MultiSourceVoidBuild::new;
	}

	public class MultiSourceVoidBuild extends MultiSourceBuild {
		@Override
		public void draw() {
			super.draw();
			Drawn.setStrobeColor();
			Draw.rect(rainbow, x, y);
			Draw.color();
		}

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
