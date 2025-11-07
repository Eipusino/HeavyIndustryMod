package heavyindustry.world.blocks.storage;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Tex;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.world.blocks.storage.Unloader;

public class CoreUnloader extends Unloader {
	public CoreUnloader(String name) {
		super(name);
		acceptsItems = true;

		config(Item.class, (CoreUnloaderBuild tile, Item item) -> tile.sortItem = item);
		configClear((CoreUnloaderBuild tile) -> tile.sortItem = null);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = CoreUnloaderBuild::new;
	}

	public class CoreUnloaderBuild extends UnloaderBuild {
		public boolean output = true;
		public int fire = 0;

		@Override
		public void buildConfiguration(Table table) {
			super.buildConfiguration(table);
			table.table(Tex.button, root -> {
				root.add("@stat.output");
				root.check("", output, judge -> output = judge).size(60).row();
			});
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			if (!output && item == sortItem) {
				return items.get(item) < getMaximumAccepted(item);
			}
			return false;
		}

		@Override
		public void updateTile() {
			int amount = block.itemCapacity;
			var core = team.core();
			fire = Vars.state.rules.coreIncinerates ? 20 : 1;
			if (sortItem != null && core != null) {
				if (output) {
					if (items.get(sortItem) == 0 && core.items.get(sortItem) > amount - 1) {
						core.items.remove(sortItem, amount);
						items.add(sortItem, amount);
					}
					for (int a = 0; a < amount; a++) {
						dump(sortItem);
					}
				}
				if (!output && core.items.get(sortItem) < core.getMaximumAccepted(sortItem) / fire) {
					if (items.get(sortItem) == getMaximumAccepted(sortItem)) {
						core.items.add(sortItem, amount);
						items.remove(sortItem, amount);
					}
				}
			}
		}

		@Override
		public void draw() {
			super.draw();
			Draw.z(Layer.effect);

			if (sortItem == null) Draw.color(Color.white);
			else Draw.color(sortItem.color);

			if (output) Draw.alpha(Mathf.sin(0.05f * Time.time));
			else Draw.alpha(-Mathf.sin(0.05f * Time.time));

			Lines.square(x, y, 4 * Mathf.sin(0.025f * Time.time), 45);
		}
	}
}
