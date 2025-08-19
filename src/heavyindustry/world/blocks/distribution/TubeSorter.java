package heavyindustry.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Eachable;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.ui.MultiItemConfig;
import heavyindustry.ui.MultiItemData;
import heavyindustry.ui.MultiItemSelection;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.DirectionalItemBuffer;
import mindustry.world.meta.BlockGroup;

import static mindustry.Vars.headless;
import static mindustry.Vars.renderer;

public class TubeSorter extends Block {
	public TubeSorter(String name) {
		super(name);
		update = false;
		destructible = true;
		underBullets = true;
		instantTransfer = true;
		group = BlockGroup.transportation;
		configurable = true;
		unloadable = false;
		saveConfig = true;
		clearOnDoubleTap = true;

		MultiItemConfig.configure(this, (TubeSorterBuild build) -> build.data);
	}

	@Override
	public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list) {
		drawPlanConfigCenter(plan, plan.config, name + "-center", false);
	}

	@Override
	public boolean outputsItems() {
		return true;
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = TubeSorterBuild::new;
	}

	public class TubeSorterBuild extends Building {
		public final MultiItemData data = new MultiItemData();

		@Override
		public void configured(Unit player, Object value) {
			super.configured(player, value);

			if (!headless) {
				renderer.minimap.update(tile);
			}
		}

		@Override
		public void draw() {
			super.draw();

			if (data.length() > 0) {
				// Use getByIndex safely for the item, checking the index within bounds
				int itemIndex = (int) Time.time / 40 + id;
				Item item = data.getItem(itemIndex % data.length());
				if (item != null) {
					Draw.color(item.color);
					Draw.rect(Core.atlas.find(name + "-center"), x, y);
					Draw.color();
				}
			}
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			Building to = getTileTarget(item, source, false);
			return to != null && to.acceptItem(this, item) && to.team == team;
		}

		@Override
		public void handleItem(Building source, Item item) {
			getTileTarget(item, source, true).handleItem(this, item);
		}

		public boolean isSame(Building other) {
			return other != null && other.block.instantTransfer;
		}

		public Building getTileTarget(Item item, Building source, boolean flip) {
			int dir = source.relativeTo(tile.x, tile.y);
			if (dir == -1) return null;
			Building to;

			if ((item != null && data.isToggled(item) && enabled)) {
				// Prevent 3-chains
				if (isSame(source) && isSame(nearby(dir))) {
					return null;
				}
				to = nearby(dir);
			} else {
				Building a = nearby(Mathf.mod(dir - 1, 4));
				Building b = nearby(Mathf.mod(dir + 1, 4));
				boolean ac = a != null && !(a.block.instantTransfer && source.block.instantTransfer) &&
						a.acceptItem(this, item);
				boolean bc = b != null && !(b.block.instantTransfer && source.block.instantTransfer) &&
						b.acceptItem(this, item);

				if (ac && !bc) {
					to = a;
				} else if (bc && !ac) {
					to = b;
				} else if (!bc) {
					return null;
				} else {
					to = (rotation & (1 << dir)) == 0 ? a : b;
					if (flip) rotation ^= (1 << dir);
				}
			}

			return to;
		}

		@Override
		public void buildConfiguration(Table table) {
			MultiItemSelection.buildTable(table, data);
		}

		@Override
		public int[] config() {
			return data.config();
		}

		@Override
		public byte version() {
			return 2;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			data.write(write);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			data.read(read);

			if (revision == 1) {
				new DirectionalItemBuffer(20).read(read);
			}
		}
	}
}
