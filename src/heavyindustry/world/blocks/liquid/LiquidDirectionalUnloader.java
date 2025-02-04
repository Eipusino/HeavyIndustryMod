package heavyindustry.world.blocks.liquid;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.scene.ui.layout.*;
import arc.util.io.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public class LiquidDirectionalUnloader extends Block {
	public TextureRegion arrowRegion, centerRegion, topRegion;

	public float speed = 10f;

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
				() -> {
					if (tile.sortLiquid != null && tile.back() != null && tile.back().block != null && tile.back().block.hasLiquids && tile.back().block.liquidCapacity > 0) {
						return (tile.back().liquids.get(tile.sortLiquid) / tile.back().block.liquidCapacity);
					} else return 0;
				}
		));
		addBar("front", (LiquidDirectionalUnloaderBuild tile) -> new Bar(
				() -> Core.bundle.get("bar.output"),
				() -> tile.sortLiquid == null ? Color.black : tile.sortLiquid.color,
				() -> {
					if (tile.sortLiquid != null && tile.front() != null && tile.front().block != null && tile.front().block.hasLiquids && tile.front().block.liquidCapacity > 0) {
						return (tile.front().liquids.get(tile.sortLiquid) / tile.front().block.liquidCapacity);
					} else return 0;
				}
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

	public class LiquidDirectionalUnloaderBuild extends Building {
		public Liquid sortLiquid = null;

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
			ItemSelection.buildTable(LiquidDirectionalUnloader.this, table, content.liquids(), () -> sortLiquid, this::configure);
		}

		@Override
		public Liquid config() {
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
