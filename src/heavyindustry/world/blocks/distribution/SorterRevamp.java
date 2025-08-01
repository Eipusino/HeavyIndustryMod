package heavyindustry.world.blocks.distribution;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.layout.Table;
import arc.util.Eachable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.DirectionalItemBuffer;
import mindustry.world.Tile;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.meta.BlockGroup;

public class SorterRevamp extends Block {
	public TextureRegion cross, source;
	public boolean invert = false;

	public SorterRevamp(String name) {
		super(name);

		update = false;
		destructible = true;
		underBullets = true;
		instantTransfer = false;
		group = BlockGroup.transportation;
		configurable = true;
		unloadable = false;
		saveConfig = true;
		clearOnDoubleTap = true;
		itemCapacity = 1;

		config(Item.class, (SorterRevampBuild tile, Item item) ->   tile.sortItem = item );
		configClear( (SorterRevampBuild tile) ->   tile.sortItem = null );
	}

	@Override
	public void load() {
		super.load();

		cross = Core.atlas.find(name + "-cross", "cross-full");
		source = Core.atlas.find("source-bottom");
	}

	@Override
	public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list) {
		drawPlanConfigCenter(plan, plan.config, "center", true);
	}

	@Override
	public boolean outputsItems() {
		return true;
	}

	@Override
	public int minimapColor(Tile tile) {
		return tile.build instanceof SorterRevampBuild sort ? sort.sortItem == null ? 0 : sort.sortItem.color.rgba() : 0;
	}

	@Override
	protected TextureRegion[] icons() {
		return new TextureRegion[]{source, region};
	}

	public class SorterRevampBuild extends Building {
		public Item sortItem;

		public boolean r0, r1;

		@Override
		public void configured(Unit builder, Object value) {
			super.configured(builder, value);

			if (!Vars.headless) {
				Vars.renderer.minimap.update(tile);
			}
		}

		@Override
		public void draw() {
			Draw.rect(region, x, y);
			if (sortItem == null) {
				Draw.rect(cross, x, y);
			} else {
				Draw.color(sortItem.color);
				Fill.square(x, y, Vars.tilesize / 2f - 0.00001f);
				Draw.color();
			}

			//super.draw();
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			Building to = getTargetTile(item, this, source, r0);

			r0 = !r0;
			return to != null && to.acceptItem(this, item) && to.team == team;
		}

		@Override
		public void handleItem(Building source, Item item) {
			Building to = getTargetTile(item, this, source, r1);
			to.handleItem(this, item);
			r1 = !r1;
		}

		public Building getTargetTile(Item item, Building fromBlock, Building source, boolean flip) {
			byte from = relativeToEdge(source.tile);
			Building to = fromBlock.nearby(Mathf.mod(from + 2, 4));
			boolean canFow = to != null && to.acceptItem(fromBlock, item) && to.team == team && (((item == sortItem) != invert) == enabled);
			boolean inv = invert == enabled;

			if (!canFow || inv) {
				if (!inv) to = null;
				int offset = flip ? -1 : 1;
				Building a = fromBlock.nearby(Mathf.mod(from + offset, 4));
				boolean ab = a != null && a.team == team && a.acceptItem(fromBlock, item);
				if (ab) {
					to = a;
				}
			}

			return to;
		}

		@Override
		public void buildConfiguration(Table table) {
			ItemSelection.buildTable(block, table, Vars.content.items(), () -> sortItem, this::configure, selectionRows, selectionColumns);
		}

		@Override
		public Object config() {
			return sortItem;
		}

		@Override
		public byte version() {
			return 2;
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.s(sortItem == null ? -1 : sortItem.id);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			sortItem = Vars.content.item(read.s());
			if (revision == 1) {
				new DirectionalItemBuffer(20).read(read);
			}
		}
	}
}
