package heavyindustry.world.blocks.liquid;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.layout.Table;
import arc.util.Eachable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.blocks.ItemSelection;
import mindustry.world.blocks.liquid.LiquidBlock.LiquidBuild;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;
import org.jetbrains.annotations.Nullable;

import static mindustry.Vars.content;

public class LiquidUnloader extends Block {
	public TextureRegion centerRegion;
	public float speed = 100f;

	public LiquidUnloader(String name) {
		super(name);

		update = true;
		solid = true;
		hasLiquids = true;
		configurable = true;
		outputsLiquid = true;
		saveConfig = true;
		noUpdateDisabled = true;
		displayFlow = false;
		group = BlockGroup.liquids;
		envEnabled = Env.any;
		clearOnDoubleTap = true;

		config(Liquid.class, (LiquidUnloaderBuild tile, Liquid liquid) -> tile.sortLiquid = liquid);
		configClear((LiquidUnloaderBuild tile) -> tile.sortLiquid = null);
	}

	@Override
	public void load() {
		super.load();
		centerRegion = Core.atlas.find(name + "-center");
	}

	@Override
	public void setBars() {
		super.setBars();

		removeBar("liquid");
	}

	@Override
	public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list) {
		drawPlanConfigCenter(plan, plan.config, name + "-center", true);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = LiquidUnloaderBuild::new;
	}

	public class LiquidUnloaderBuild extends Building {
		public @Nullable Liquid sortLiquid = null;
		public @Nullable Liquid lastSort = null;
		public @Nullable Building dumpingTo = null;

		public float offset = 0f;

		@Override
		public void updateTile() {
			if (lastSort != sortLiquid) {
				liquids.clear();
				lastSort = sortLiquid;
			}
			for (int i = 0; i < proximity.size; i++) {
				int pos = (int) (offset + i) % proximity.size;
				Building other = proximity.get(pos);

				if (other.interactable(team) && other.block.hasLiquids && !(other instanceof LiquidBuild && other.block.size == 1) && sortLiquid != null && other.liquids.get(sortLiquid) > 0) {
					dumpingTo = other;
					if (liquids.get(sortLiquid) < block.liquidCapacity) {
						float amount = Math.min(speed, other.liquids.get(sortLiquid));
						liquids.add(sortLiquid, amount);
						other.liquids.remove(sortLiquid, amount);
					}
				}
			}
			if (proximity.any()) {
				offset++;
				offset %= proximity.size;
			}
			dumpLiquid(liquids.current());
		}

		@Override
		public boolean canDumpLiquid(Building to, Liquid liquid) {
			return to != dumpingTo;
		}

		@Override
		public void draw() {
			super.draw();
			Draw.color(sortLiquid == null ? Color.clear : sortLiquid.color);
			Draw.rect(centerRegion, x, y);
			Draw.color();
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
