package endfield.world.blocks.liquid;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;
import org.jetbrains.annotations.Nullable;

import static mindustry.Vars.content;

public class LiquidDirectionalUnloader extends Block {
	public TextureRegion arrowRegion, centerRegion, topRegion;
	public float speed = 200f;

	public LiquidDirectionalUnloader(String name) {
		super(name);

		update = true;
		solid = false;
		configurable = true;
		outputsLiquid = true;
		saveConfig = true;
		noUpdateDisabled = true;
		displayFlow = false;
		group = BlockGroup.liquids;
		envEnabled = Env.any;
		clearOnDoubleTap = true;
		rotate = true;

		config(Liquid.class, (LiquidDirectionalUnloaderBuild tile, Liquid liquid) -> tile.sortLiquid = liquid);
		configClear((LiquidDirectionalUnloaderBuild tile) -> tile.sortLiquid = null);
	}

	@Override
	public void load() {
		super.load();
		arrowRegion = Core.atlas.find(name + "-arrow");
		centerRegion = Core.atlas.find(name + "-center");
		topRegion = Core.atlas.find(name + "-top");
	}

	@Override
	public void setBars() {
		super.setBars();
		removeBar("liquid");
		addBar("back", (LiquidDirectionalUnloaderBuild tile) -> new Bar(
				() -> Core.bundle.get("bar.input"),
				() -> tile.sortLiquid == null ? Color.black : tile.sortLiquid.color,
				() -> tile.sortLiquid != null && tile.back() != null && tile.back().block != null && tile.back().block.hasLiquids && tile.back().block.liquidCapacity > 0 ?
						tile.back().liquids.get(tile.sortLiquid) / tile.back().block.liquidCapacity
						: 0f
		));
		addBar("front", (LiquidDirectionalUnloaderBuild tile) -> new Bar(
				() -> Core.bundle.get("bar.output"),
				() -> tile.sortLiquid == null ? Color.black : tile.sortLiquid.color,
				() -> tile.sortLiquid != null && tile.front() != null && tile.front().block != null && tile.front().block.hasLiquids && tile.front().block.liquidCapacity > 0 ?
						tile.front().liquids.get(tile.sortLiquid) / tile.front().block.liquidCapacity
						: 0f
		));
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		super.drawPlace(x, y, rotation, valid);
		Draw.rect(region, x, y, 0);
		Draw.rect(topRegion, x, y, 0);
		Draw.rect(arrowRegion, x, y, rotation);
	}

	@Override
	protected TextureRegion[] icons() {
		return new TextureRegion[]{region, topRegion, arrowRegion};
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = LiquidDirectionalUnloaderBuild::new;
	}

	public class LiquidDirectionalUnloaderBuild extends Building {
		public @Nullable Liquid sortLiquid = null;

		@Override
		public void updateTile() {
			Building front = front(), back = back();
			if (front != null && back != null && front.block != null && back.block != null && back.liquids != null && front.team == team && back.team == team && sortLiquid != null) {
				if (front.acceptLiquid(this, sortLiquid)) {
					float fl = front.liquids.get(sortLiquid), bl = back.liquids.get(sortLiquid), fc = front.block.liquidCapacity, bc = back.block.liquidCapacity;
					if (bl > 0 && bl / bc > fl / fc) {
						float amount = Math.min(speed, back.liquids.get(sortLiquid));
						float a = Math.min(amount, front.block.liquidCapacity - front.liquids.get(sortLiquid));
						float balance = Math.min(a, (bl / bc - fl / fc) * bc);
						front.handleLiquid(this, sortLiquid, balance);
						back.liquids.remove(sortLiquid, balance);
					}
				}
			}
		}

		@Override
		public void draw() {
			Draw.rect(region, x, y);
			Draw.color(sortLiquid == null ? Color.clear : sortLiquid.color);
			Draw.rect(centerRegion, x, y);
			Draw.color();
			Draw.rect(topRegion, x, y);
			Draw.rect(arrowRegion, x, y, rotdeg());
		}

		@Override
		public void buildConfiguration(Table table) {
			ItemSelection.buildTable(block, table, content.liquids(), () -> sortLiquid, this::configure);
		}

		@Override
		public Object config() {
			return sortLiquid;
		}

		@Override
		public byte version() {
			return 1;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.s(sortLiquid == null ? -1 : sortLiquid.id);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			int id = revision == 1 ? read.s() : read.b();
			sortLiquid = id == -1 ? null : content.liquid(id);
		}
	}
}
