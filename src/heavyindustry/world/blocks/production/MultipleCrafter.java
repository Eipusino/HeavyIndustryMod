/*
package heavyindustry.world.blocks.production;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.util.Strings;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.math.Mathm;
import heavyindustry.util.Arrays2;
import heavyindustry.util.CollectionList;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.gen.Iconc;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.world.Piece;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.consumers.ConsumeLiquids;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;
import mindustry.world.meta.Stats;

import java.util.Collections;
import java.util.List;

public class MultipleCrafter extends Piece {
	public FormulaStack formulas = new FormulaStack();
	public boolean dumpExtraLiquid = true;
	public boolean ignoreLiquidFullness = false;

	public MultipleCrafter(String name) {
		super(name);

		solid = true;
		update = true;
		hasItems = true;
		drawArrow = false;
		configurable = true;
		ambientSound = Sounds.machine;
		ambientSoundVolume = 0.03f;
		flags = EnumSet.of(BlockFlag.factory);
	}

//	@Override
//	public void setBars() {
//		super.setBars();
//		boolean added = false;
//		boolean outPower = false;
//		boolean consP = false;
//		List<Liquid> addedLiquids = new CollectionList<>(Liquid.class);
//		for (Formula f : formulas.formulas) {
//			if (f.powerProduction > 0) outPower = true;
//			if (f.inputs != null) {
//				for (var cons : f.inputs) {
//					if (cons instanceof ConsumePower) consP = true;
//					if (cons instanceof ConsumeLiquid consumeLiquid) {
//						added = true;
//						if (addedLiquids.contains(consumeLiquid.liquid)) continue;
//						addedLiquids.add(consumeLiquid.liquid);
//						addLiquidBar(consumeLiquid.liquid);
//					} else if (cons instanceof ConsumeLiquids consumeLiquids) {
//						added = true;
//						for (var stack : consumeLiquids.liquids) {
//							if (addedLiquids.contains(stack.liquid)) continue;
//							addedLiquids.add(stack.liquid);
//							addLiquidBar(stack.liquid);
//						}
//					}
//				}
//			}
//			if (f.outputLiquids != null) {
//				for (var out : f.outputLiquids) {
//					if (addedLiquids.contains(out.liquid)) continue;
//					addedLiquids.add(out.liquid);
//					addLiquidBar(out.liquid);
//				}
//			}
//
//			if (!added) {
//				if (formulas.outputLiquids()) {
//					addLiquidBar(build -> build.liquids.current());
//				}
//			}
//		}
//		if (outPower) {
//			addBar("outPower", (MultipleCrafterBuilding entity) -> new Bar(
//						() -> Core.bundle.format("bar.poweroutput",
//								Strings.fixed(entity.getPowerProduction() * 60 * entity.timeScale(), 1)),
//						() -> Pal.powerBar, () -> entity.efficiency));
//		}
//
//		if (consPower != null) {
//			addBar("power", (MultipleCrafterBuilding entity) -> new Bar(() -> {
//				float cur = entity.power.status * (entity.consPower == null ? 0f : entity.consPower.usage) * 60 * entity.timeScale() * (entity.shouldConsume() ? 1f : 0f)
//				return Iconc.power + " " + percent(cur, (entity.consPower == null ? 0f : entity.consPower.usage) * 60 * entity.timeScale() * (entity.shouldConsume() ? 1f : 0f),
//						entity.timeScale() * 100 * (entity.shouldConsume() ? 1f : 0f) * entity.efficiency);
//			}, () -> Pal.powerBar, () -> {
//				if (Mathf.zero(consPower.requestedPower(entity)) && entity.power.graph.getPowerProduced() + entity.power.graph.getBatteryStored() > 0f)
//					return 1f;
//				else return entity.power.status;
//			}));
//		}
//
//		addBar("productionProgress", (MultipleCrafterBuilding build) -> new Bar(() -> HStat.multi.localizedName, () -> Pal.ammo, build::progress));
//	}

	@Override
	public boolean rotatedOutput(int x, int y) {
		return false;
	}

	@Override
	public void init() {
		super.init();
		formulas.apply(this);
		if (hasPower && consumesPower) {
			List<ConsumePower> cs = new CollectionList<>(formulas.formulas.size(), ConsumePower.class);
			for (Formula f : formulas.formulas) {
				ConsumePower p = f.consPower;
				if (p != null) {
					cs.add(p);
				}
			}
			//ConsumePower[] csa = arrayOf();
			//consPower = new ConsumePowerMultiple(cs.toArray(csa));
		}
		hasConsumers = true;
	}

	@Override
	public boolean outputsItems() {
		return formulas.outputItems();
	}

	public class MultipleCrafterBuilding extends Building {
		public float progress = 0f;
		public float totalProgress = 0f;
		public float warmup = 0f;
		public int formulaIndex = 0;
		public Formula formula = formulas.getFormula(formulaIndex);
		public ItemStack[] outputItems = formula.outputItems;
		public LiquidStack[] outputLiquids = formula.outputLiquids;
		public float powerProductionTimer = 0f;
		public ConsumePower consPower = null;

		@Override
		public void draw() {
			super.draw();

			if (outputItems != null && outputItems.length > 0) {
				drawItemSelection(outputItems[0].item);
			} else if (outputLiquids != null && outputLiquids.length > 0) {
				drawItemSelection(outputLiquids[0].liquid);
			}
		}

		@Override
		public boolean shouldConsume() {
			if (outputItems != null) {
				for (ItemStack output : outputItems) {
					if (items.get(output.item) + output.amount > itemCapacity) {
						return false;
					}
				}
			}
			if (outputLiquids != null && !ignoreLiquidFullness) {
				boolean allFull = true;
				for (var output : outputLiquids) {
					if (liquids.get(output.liquid) >= liquidCapacity - 0.001f) {
						if (!dumpExtraLiquid) {
							return false;
						}
					} else {
						//if there's still space left, it's not full for all liquids
						allFull = false;
					}
				}
				//if there is no space left for any liquid, it can't reproduce
				if (allFull) {
					return false;
				}
			}
			return enabled;
		}

		@Override
		public Object config() {
			return formulaIndex;
		}

		@Override
		public void updateConsumption() {
			if (cheating()) {
				potentialEfficiency = enabled && productionValid() ? 1.0f : 0.0f;
				optionalEfficiency = shouldConsume() ? potentialEfficiency : 0.0f;
				efficiency = optionalEfficiency;
				shouldConsumePower = true;
				updateEfficiencyMultiplier();
				return;
			}
			if (!enabled) {
				optionalEfficiency = 0.0f;
				efficiency = optionalEfficiency;
				potentialEfficiency = efficiency;
				shouldConsumePower = false;
				return;
			}
			boolean update = shouldConsume() && productionValid();
			float minEfficiency = 1.0f;
			optionalEfficiency = 1.0f;
			efficiency = optionalEfficiency;
			shouldConsumePower = true;
			Consume[] nonOptionalConsumers = new CollectionList<>(formula.inputs).select(consume -> !consume.optional && !consume.ignore()).toArray();
			Consume[] optionalConsumers = new CollectionList<>(formula.inputs).select(consume -> consume.optional && !consume.ignore()).toArray();

			for (var cons : nonOptionalConsumers) {
				var result = cons.efficiency(this);
				if (cons != consPower && result <= 1.0E-7f) {
					shouldConsumePower = false;
				}
				minEfficiency = Math.min(minEfficiency, result);
			}
			for (var cons : optionalConsumers) {
				optionalEfficiency = Math.min(optionalEfficiency, cons.efficiency(this));
			}
			efficiency = minEfficiency;
			optionalEfficiency = Math.min(optionalEfficiency, minEfficiency);
			potentialEfficiency = efficiency;
			if (!update) {
				optionalEfficiency = 0.0f;
				efficiency = optionalEfficiency;
			}
			updateEfficiencyMultiplier();
			if (update && efficiency > 0) {
				for (var cons : formula.inputs) {
					cons.update(this);
				}
			}
		}

		@Override
		public void displayConsumption(Table table) {
			super.displayConsumption(table);
			formula.build(this, table);
		}

		@Override
		public void updateTile() {
			super.updateTile();
			formula = formulas.getFormula(formulaIndex);
			outputItems = formula.outputItems;
			outputLiquids = formula.outputLiquids;
			consPower = formula.consPower;
			if (efficiency > 0) {
				progress += getProgressIncrease(formula.craftTime);
				warmup = Mathf.approachDelta(warmup, warmupTarget(), formula.warmupSpeed);
				//continuously output based on efficiency
				if (outputLiquids != null) {
					var inc = getProgressIncrease(1f);
					for (var output : outputLiquids) {
						handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
					}
				}

				if (wasVisible && Mathf.chanceDelta(formula.updateEffectChance)) {
					formula.updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4));
				}
			} else {
				warmup = Mathf.approachDelta(warmup, 0f, formula.warmupSpeed);
			}
			totalProgress += warmup * Time.delta;

			if (progress >= 1f) {
				craft();
			}

			dumpOutputs();
		}

		@Override
		public void drawSelect() {
			super.drawSelect();
			if (outputLiquids != null) {
				for (int i = 0; i < outputLiquids.length; i++) {
					var dir = formula.liquidOutputDirections.length > i ? formula.liquidOutputDirections[i] : -1;

					if (dir != -1) {
						Draw.rect(outputLiquids[i].liquid.fullIcon,
								x + Geometry.d4x(dir + rotation) * (size * Vars.tilesize / 2f + 4),
								y + Geometry.d4y(dir + rotation) * (size * Vars.tilesize / 2f + 4), 8f, 8f);
					}
				}
			}
		}

		@Override
		public float getProgressIncrease(float baseTime) {
			if (ignoreLiquidFullness) {
				return super.getProgressIncrease(baseTime);
			}
			//limit progress increase by maximum amount of liquid it can produce
			var scaling = 1f;
			var max = 1f;
			if (outputLiquids != null) {
				max = 0f;
				for (var s : outputLiquids) {
					var value = (liquidCapacity - liquids.get(s.liquid)) / (s.amount * edelta());
					scaling = Math.min(scaling, value);
					max = Math.max(max, value);
				}
			}
			//when dumping excess take the maximum value instead of the minimum.
			return super.getProgressIncrease(baseTime) * (dumpExtraLiquid ? Math.min(max, 1.0f) : scaling);
		}

		@Override
		public float getPowerProduction() {
			return powerProductionTimer > 0f ? formula.powerProduction * efficiency : 0f;
		}

		public float warmupTarget() {
			return 1f;
		}

		@Override
		public float warmup() {
			return warmup;
		}

		@Override
		public float totalProgress() {
			return totalProgress;
		}

		public void craft() {
			formula.trigger(this);

			if (outputItems != null) {
				for (ItemStack output : outputItems) {
					for (int i = 0; i < output.amount; i++) {
						offload(output.item);
					}
				}
			}
			if (wasVisible) {
				formula.craftEffect.at(x, y);
			}
			progress %= 1f;
			powerProductionTimer += formula.craftTime / efficiency + 1f;
		}

		public void dumpOutputs() {
			if (outputItems != null && timer(timerDump, dumpTime / timeScale)) {
				for (var output : outputItems) {
					dump(output.item);
				}
			}
			if (outputLiquids != null) {
				for (int i = 0; i < outputLiquids.length; i++) {
					var dir = formula.liquidOutputDirections.length > i ? formula.liquidOutputDirections[i] : -1;

					dumpLiquid(outputLiquids[i].liquid, 2f, dir);
				}
			}
		}

//		@Override
//		public void buildConfiguration(Table table) {
//			super.buildConfiguration(table);
//			table.iTable { itable ->
//				for ((index, form) in formulas.formulas.withIndex()) {
//					itable.button({ button ->
//									button.iTableGY { inputTable ->
//									inputTable.table(IStyles.background42) {
//									form.inputs?.let { let ->
//									let.forEach { cons ->
//									cons.display(it)
//							}
//							}
//									it.add(TimeDisplay(formula.craftTime)).pad(5f)
//							}.margin(
//									5f
//							)
//							}.width(260f)
//							button.iTableG { arrow ->
//							arrow.image(IStyles.arrow1.apply {
//						texture.setFilter(Texture.TextureFilter.nearest)
//					}).size(180f, 80f)
//					}
//					button.iTableGY { out ->
//							out.table(IStyles.background42) {
//						form.outputItems?.let { items ->
//								items.forEach { itemStack ->
//								it.add(ItemDisplay(itemStack.item, itemStack.amount)).padRight(3f)
//						}
//						}
//						form.outputLiquids?.let { liquids ->
//								liquids.forEach { liquidStack ->
//								it.add(
//										LiquidDisplay(liquidStack.liquid, liquidStack.amount * 60,
//												localizedName = false)).padRight(3f)
//						}
//						}
//					}.margin(5f)
//					}.width(200f)
//					button.update {
//						button.isChecked = index == formulaIndex
//					}
//                    }, Button.ButtonStyle().apply {
//						up = IStyles.background21
//						down = IStyles.background22
//						checked = IStyles.background22
//					}) {
//						setIndex(index)
//						Vars.control.input.config.hideConfig()
//					}.height(100f).margin(10f).pad(3f).row()
//				}
//			}
//		}

		@Override
		public double sense(LAccess sensor) {
			if (sensor == LAccess.progress) return progress();
			return super.sense(sensor);
		}

		@Override
		public float progress() {
			return Mathm.clamp(progress);
		}

		@Override
		public int getMaximumAccepted(Item item) {
			return itemCapacity;
		}

		@Override
		public boolean shouldAmbientSound() {
			return efficiency > 0;
		}

		public void setIndex(int index) {
			formulaIndex = index;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.f(progress);
			write.f(warmup);
			write.b(formulaIndex);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			progress = read.f();
			warmup = read.f();
			formulaIndex = read.b();
		}
	}

	public static class FormulaStack {
		public List<Formula> formulas = new CollectionList<>(Formula.class);

		public FormulaStack with(Formula... formulas) {
			return new FormulaStack().addFormulas(formulas);
		}

		public Formula getFormula(int index) {
			return formulas.get(index);
		}

		public void setFormula(int index, Formula formula) {
			formulas.set(index, formula);
		}

		public FormulaStack addFormula(Formula formula) {
			formulas.add(formula);
			return this;
		}

		public FormulaStack addFormulas(Formula... fors) {
			Collections.addAll(formulas, fors);
			return this;
		}

		public boolean outputItems(int index) {
			return getFormula(index).outputItems != null;
		}

		public boolean outputItems() {
			for (Formula f : formulas) {
				if (f.outputItems != null) return true;
			}
			return false;
		}

		public boolean outputLiquids() {
			for (Formula f : formulas) {
				if (f.outputLiquids != null) return true;
			}
			return false;
		}

		public void trigger(Building build) {
			for (Formula f : formulas) {
				f.trigger(build);
			}
		}

		public void apply(Piece block) {
			for (Formula f : formulas) {
				f.apply(block);
			}
		}

		public int size() {
			return formulas.size();
		}
	}

	public static class Formula {
		public Consume[] inputs;
		public ItemStack[] outputItems;
		public LiquidStack[] outputLiquids;

		public float craftTime = 60f;

		public int[] liquidOutputDirections = {-1};

		public Effect craftEffect = Fx.none;
		public Effect updateEffect = Fx.none;

		public float updateEffectChance = 0.04f;

		public float warmupSpeed = 0.019f;
		public float powerProduction = 0f;

		public ConsumePower consPower = null;

		public void setInput(Consume... input) {
			inputs = input;
		}

		public void setOutput(ItemStack... items) {
			outputItems = items;
		}

		public void setOutput(LiquidStack... liquids) {
			outputLiquids = liquids;
		}

		public Formula set(Consume[] in, ItemStack[] items, LiquidStack[] liquids) {
			inputs = in;
			outputItems = items;
			outputLiquids = liquids;
			return this;
		}

		public Formula getPowerProduction(float value) {
			powerProduction = value;
			return this;
		}

		public void apply(Piece block) {
			if (inputs != null) {
				for (Consume c : inputs) {
					if (c instanceof ConsumePower p) {
						consPower = p;
					} else {
						c.apply(block);
					}
				}
			}

			if (powerProduction > 0) {
				block.hasPower = true;
				block.outputsPower = true;
			}
		}

		public void update(Building build) {
			if (inputs != null) {
				for (Consume c : inputs) {
					c.update(build);
				}
			}
		}

		public void trigger(Building build) {
			if (inputs != null) {
				for (Consume c : inputs) {
					c.trigger(build);
				}
			}
		}

		public void display(Stats stats, Piece block) {
			stats.timePeriod = craftTime;
			if (inputs != null) {
				for (Consume c : inputs) {
					c.display(stats);
				}
			}
			if ((block.hasItems && block.itemCapacity > 0) || outputItems != null) {
				stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);
			}

			if (outputItems != null) {
				stats.add(Stat.output, StatValues.items(craftTime, outputItems));
			}
			if (outputLiquids != null) {
				stats.add(Stat.output, StatValues.liquids(1f, outputLiquids));
			}

			if (powerProduction > 0) {
				stats.add(Stat.basePowerGeneration, powerProduction * 60f, StatUnit.powerSecond);
			}
		}

		public void build(Building build, Table table) {
			if (inputs != null) {
				table.pane(t -> {
					for (Consume c : inputs) {
						c.build(build, t);
					}
				});
			}

		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("Formula{").append("input=");
			Arrays2.append(builder, inputs);
			builder.append(", outputItems=");
			Arrays2.append(builder, outputItems);
			builder.append(", outputLiquids=");
			Arrays2.append(builder, outputLiquids);
			return builder.append(", craftTime=").append(craftTime).append(", powerProduction=").append(powerProduction).append('}').toString();
		}
	}
}
*/
