package heavyindustry.world.blocks.sandbox;

import arc.graphics.Color;
import arc.struct.Seq;
import heavyindustry.type.Recipe;
import heavyindustry.world.consumers.ConsumeRecipe;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.type.PayloadStack;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.consumers.ConsumeItemFilter;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.consumers.ConsumeLiquidFilter;
import mindustry.world.consumers.ConsumeLiquids;
import mindustry.world.consumers.ConsumeLiquidsDynamic;
import mindustry.world.meta.Stat;

public class AdaptiveSource extends Block {
	public float powerProduction = 1000000f / 60f;
	public float heatOutput = 1000f;

	public Seq<Item> outputItems;
	public Seq<Liquid> outputLiquids;

	protected AdaptiveSource(String name) {
		super(name);

		hasItems = true;
		hasLiquids = true;
		hasPower = true;
		outputsPower = true;
		consumesPower = false;
		update = true;
		displayFlow = false;
		canOverdrive = true;
		createRubble = false;
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.remove(Stat.itemCapacity);
		stats.remove(Stat.liquidCapacity);
	}

	@Override
	public void setBars() {
		addBar("health", tile -> new Bar("stat.health", Pal.health, tile::healthf).blink(Color.white));
	}

	@Override
	public boolean outputsItems() {
		return true;
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = AdaptiveSourceBuild::new;
	}

	public class AdaptiveSourceBuild extends Building implements HeatBlock {
		@Override
		public void updateTile() {
			if (proximity.isEmpty()) return;

			if (outputItems == null) outputItems = Vars.content.items();
			if (outputLiquids == null) outputLiquids = Vars.content.liquids();

			for (int i = 0; i < proximity.size; i++) {
				Building build = proximity.get(i);
				if (build == null || !build.shouldConsume() || build.block == null || build.block.consumers == null) continue;
				for (Consume consume : build.block.consumers) {
					if (consume instanceof ConsumeItems cons) {
						ItemStack[] items = cons.items;
						for (ItemStack item : items) {
							for (int a = 0; a < item.amount; a++) {
								if (build.acceptItem(this, item.item)) {
									build.handleItem(this, item.item);
								}
							}
						}
					} else if (consume instanceof ConsumeItemFilter cons) {
						for (Item item : outputItems) {
							if (cons.filter.get(item) && build.acceptItem(this, item)) {
								build.handleItem(this, item);
							}
						}
					} else if (consume instanceof ConsumeLiquid cons) {
						if (build.acceptLiquid(this, cons.liquid) && build.liquids.get(cons.liquid) < build.block.liquidCapacity) {
							build.handleLiquid(this, cons.liquid, build.block.liquidCapacity - build.liquids.get(cons.liquid));
						}
					} else if (consume instanceof ConsumeLiquids cons) {
						LiquidStack[] liquids = cons.liquids;
						for (LiquidStack liquid : liquids) {
							if (build.acceptLiquid(this, liquid.liquid) && build.liquids.get(liquid.liquid) < build.block.liquidCapacity) {
								build.handleLiquid(this, liquid.liquid, build.block.liquidCapacity - build.liquids.get(liquid.liquid));
							}
						}
					} else if (consume instanceof ConsumeLiquidFilter cons) {
						for (Liquid liquid : outputLiquids) {
							if (cons.filter.get(liquid) && build.acceptLiquid(this, liquid) && build.liquids.get(liquid) < build.block.liquidCapacity) {
								build.handleLiquid(this, liquid, build.block.liquidCapacity - build.liquids.get(liquid));
							}
						}
					} else if (consume instanceof ConsumeItemDynamic cons) {
						ItemStack[] items = cons.items.get(build);
						for (ItemStack item : items) {
							for (int a = 0; a < item.amount; a++) {
								if (build.acceptItem(this, item.item)) {
									build.handleItem(this, item.item);
								}
							}
						}
					} else if (consume instanceof ConsumeLiquidsDynamic cons) {
						LiquidStack[] liquids = cons.liquids.get(build);
						for (LiquidStack liquid : liquids) {
							if (build.acceptLiquid(this, liquid.liquid) && build.liquids.get(liquid.liquid) < build.block.liquidCapacity) {
								build.handleLiquid(this, liquid.liquid, build.block.liquidCapacity - build.liquids.get(liquid.liquid));
							}
						}
					} else if (consume instanceof ConsumeRecipe cons) {
						Recipe recipe = cons.recipes.get(build);

						if (recipe == null) continue;

						for (ItemStack stack : recipe.inputItem) {
							if (build.acceptItem(this, stack.item)) build.handleItem(this, stack.item);
						}
						for (LiquidStack stack : recipe.inputLiquid) {
							if (build.acceptLiquid(this, stack.liquid) && build.liquids.get(stack.liquid) < build.block.liquidCapacity) {
								build.handleLiquid(this, stack.liquid, build.block.liquidCapacity - build.liquids.get(stack.liquid));
							}
						}
						for (PayloadStack stack : recipe.inputPayload) {
						}
					}
				}
			}
		}

		@Override
		public void splashLiquid(Liquid liquid, float amount) {}

		@Override
		public float getPowerProduction() {
			return enabled ? powerProduction : 0f;
		}

		@Override
		public float heat() {
			return enabled ? heatOutput : 0f;
		}

		@Override
		public float heatFrac() {
			return enabled ? heatOutput : 0f;
		}
	}
}

