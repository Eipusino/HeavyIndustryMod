package endfield.world.blocks.power;

import arc.Core;
import arc.util.Strings;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.world.blocks.power.ConsumeGenerator;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;
import org.jetbrains.annotations.Nullable;

public class AdvancedConsumeGenerator extends ConsumeGenerator {
	// make them able to output multiple items and liquids
	public @Nullable ItemStack outputItem;
	public ItemStack @Nullable [] outputItems;
	public @Nullable LiquidStack outputLiquid;
	public LiquidStack @Nullable [] outputLiquids;
	public int[] liquidOutputDirections = new int[]{-1};

	// is production bar will be displayed
	public boolean progressBar = false;

	public AdvancedConsumeGenerator(String name) {
		super(name);
	}

	@Override
	public void setStats() {
		super.setStats();
		if (outputItems != null) {
			stats.add(Stat.output, StatValues.items(itemDuration, outputItems));
		}
		if (outputLiquids != null) {
			stats.add(Stat.output, StatValues.liquids(1f, outputLiquids));
		}
	}

	@Override
	public void setBars() {
		super.setBars();
		if (outputLiquids != null && outputLiquids.length > 0) {
			removeBar("liquid");

			for (LiquidStack stack : outputLiquids) {
				addLiquidBar(stack.liquid);
			}
		}
		if (progressBar) {
			addBar("progress", (AdvancedConsumeGeneratorBuild tile) -> new Bar(
					() -> Core.bundle.format("bar.production-progress", Strings.fixed(tile.totalProgress() / itemDuration * 100, 1)),
					() -> Pal.accent,
					() -> tile.totalProgress() / itemDuration)
			);
		}
	}

	@Override
	public void init() {
		if (outputItems == null && outputItem != null) {
			outputItems = new ItemStack[]{outputItem};
		}

		if (outputLiquids == null && outputLiquid != null) {
			outputLiquids = new LiquidStack[]{outputLiquid};
		}

		if (outputLiquid == null && outputLiquids != null && outputLiquids.length > 0) {
			outputLiquid = outputLiquids[0];
		}

		outputsLiquid = outputLiquids != null;
		if (outputItems != null) {
			hasItems = true;
		}

		if (outputLiquids != null) {
			hasLiquids = true;
		}

		super.init();
	}

	public boolean outputsItems() {
		return outputItems != null;
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = AdvancedConsumeGeneratorBuild::new;
	}

	public class AdvancedConsumeGeneratorBuild extends ConsumeGeneratorBuild {
		@Override
		public void updateTile() {
			if (outputLiquids != null) {
				float inc = getProgressIncrease(1f);

				for (LiquidStack output : outputLiquids) {
					handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
				}
			}
			craft();
			dumpOutputs();
			super.updateTile();
		}

		public void craft() {
			consume();
			if (outputItems != null) {
				for (ItemStack output : outputItems) {
					for (int i = 0; i < output.amount; ++i) {
						offload(output.item);
					}
				}
			}
		}

		public void dumpOutputs() {
			if (outputItems != null && timer(timerDump, (float) dumpTime / timeScale)) {
				for (ItemStack output : outputItems) {
					dump(output.item);
				}
			}

			if (outputLiquids != null) {
				for (int i = 0; i < outputLiquids.length; ++i) {
					int dir = liquidOutputDirections.length > i ? liquidOutputDirections[i] : -1;
					dumpLiquid(outputLiquids[i].liquid, 2f, dir);
				}
			}
		}
	}
}
