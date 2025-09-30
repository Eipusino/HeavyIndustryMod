package heavyindustry.world.blocks.production;

import arc.Core;
import arc.func.Boolf;
import arc.func.Func;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ImageButton;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Structs;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.ui.Elements;
import heavyindustry.util.ObjectBoolMapf;
import heavyindustry.util.CollectionOrderedMap;
import heavyindustry.world.consumers.ConsumeItem;
import mindustry.content.Fx;
import mindustry.core.UI;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.liquid.Conduit.ConduitBuild;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.consumers.ConsumeLiquid;
import mindustry.world.consumers.ConsumeLiquids;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;
import mindustry.world.meta.Stats;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

/**
 * MultiCrafter. You can freely choose to change the production formula.
 *
 * @author Eipusino
 * @see heavyindustry.world.blocks.heat.HeatMultiCrafter heat
 * @since 1.0.6
 */
public class MultiCrafter extends Block {
	/** Liquid output directions, specified in the same order as outputLiquids. Use -1 to dump in every direction. Rotations are relative to block. */
	public int[] liquidOutputDirections = {-1};

	/** PayloadRecipe {@link CraftPlan}. */
	public Seq<CraftPlan> craftPlans = new Seq<>(CraftPlan.class);
	/** If {@link MultiCrafter#useBlockDrawer} is false, use the drawer in the recipe for the block. */
	public DrawBlock drawer = new DrawDefault();
	/** Do you want to use the {@link MultiCrafter#drawer} inside the block itself. */
	public boolean useBlockDrawer = true;
	/** Whether multiple liquid outputs require different directions, please refer to {@link CraftPlan#liquidOutputDirections} to determine the value of this parameter. */
	public boolean hasDoubleOutput = false;
	/** Automatically add bar to liquid. */
	public boolean autoAddBar = true;
	/** Is liquid suspension display used. */
	public boolean useLiquidTable = true;
	/** How many formulas can be displayed at most once? */
	public int maxList = 4;

	public MultiCrafter(String name) {
		super(name);

		update = true;
		solid = true;
		hasItems = true;
		consumesPower = false;
		ambientSound = Sounds.machine;
		sync = true;
		ambientSoundVolume = 0.03f;
		flags = EnumSet.of(BlockFlag.factory);
		drawArrow = false;

		configurable = true;
		saveConfig = true;

		config(int[].class, (MultiCrafterBuild tile, int[] in) -> {
			if (in.length != 2) return;

			tile.rotation = in[0];

			if (craftPlans.isEmpty() || in[1] == -1) tile.craftPlan = null;
			tile.craftPlan = craftPlans.get(in[1]);
		});
	}

	@Override
	public void init() {
		for (CraftPlan plan : craftPlans) {
			plan.owner = this;
			plan.init();
			if (plan.outputLiquids.length > 0) {
				hasLiquids = true;
				outputsLiquid = true;
			}
			if (plan.outputItems.length > 0) {
				hasItems = true;
			}
			if (plan.consPower != null) {
				hasPower = true;
				consumesPower = true;
			}
			if (plan.powerProduction > 0) {
				hasPower = true;
				outputsPower = true;
			}
		}
		if (hasPower && consumesPower) consumePowerDynamic(MultiCrafterBuild::formulaPower);

		super.init();

		hasConsumers = craftPlans.any();
	}

	@Override
	public void setBars() {
		addBar("health", tile -> new Bar("stat.health", Pal.health, tile::healthf).blink(Color.white));

		if (consPower != null) {
			boolean buffered = consPower.buffered;
			float capacity = consPower.capacity;

			addBar("power", tile -> new Bar(
					() -> buffered ? Core.bundle.format("bar.poweramount", Float.isNaN(tile.power.status * capacity) ? "<ERROR>" : UI.formatAmount((int) (tile.power.status * capacity))) :
							Core.bundle.get("bar.power"),
					() -> Pal.powerBar,
					() -> Mathf.zero(consPower.requestedPower(tile)) && tile.power.graph.getPowerProduced() + tile.power.graph.getBatteryStored() > 0f ? 1f : tile.power.status)
			);
		}

		if (unitCapModifier != 0) {
			stats.add(Stat.maxUnits, (unitCapModifier < 0 ? "-" : "+") + Math.abs(unitCapModifier));
		}
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.add(Stat.output, table -> {
			table.row();

			for (CraftPlan plan : craftPlans) {
				table.table(Styles.grayPanel, info -> {
					info.left().defaults().left();
					Stats stat = new Stats();
					stat.timePeriod = plan.craftTime;
					if (plan.hasConsumers)
						for (Consume c : plan.consumers)
							c.display(stat);

					if ((hasItems && itemCapacity > 0) || plan.outputItems.length > 0)
						stat.add(Stat.productionTime, plan.craftTime / 60f, StatUnit.seconds);

					if (plan.outputItems.length > 0)
						stat.add(Stat.output, StatValues.items(plan.craftTime, plan.outputItems));

					if (plan.outputLiquids.length > 0)
						stat.add(Stat.output, StatValues.liquids(1f, plan.outputLiquids));

					info.table(t -> Elements.statTurnTable(stat, t)).pad(8).left();
				}).growX().left().pad(10);
				table.row();
			}
		});
	}

	@Override
	public void load() {
		super.load();
		if (useBlockDrawer) {
			drawer.load(this);
		} else {
			for (CraftPlan plan : craftPlans) {
				plan.drawer.load(this);
			}
		}
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		if (useBlockDrawer) {
			drawer.drawPlan(this, plan, list);
		} else {
			if (craftPlans.any()) {
				craftPlans.get(0).drawer.drawPlan(this, plan, list);
			} else {
				super.drawPlanRegion(plan, list);
			}
		}
	}

	@Override
	public boolean rotatedOutput(int fromX, int fromY, Tile destination) {
		if (!(destination.build instanceof ConduitBuild)) return false;

		Building crafter = world.build(fromX, fromY);
		if (crafter == null) return false;
		int relative = Mathf.mod(crafter.relativeTo(destination) - crafter.rotation, 4);
		for (int dir : liquidOutputDirections) {
			if (dir == -1 || dir == relative) return false;
		}

		return true;
	}

	@Override
	protected TextureRegion[] icons() {
		return useBlockDrawer ? drawer.icons(this) : craftPlans.any() ? craftPlans.get(0).drawer.icons(this) : super.icons();
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = MultiCrafterBuild::new;
	}

	public class MultiCrafterBuild extends Building {
		public CraftPlan craftPlan = craftPlans.any() ? craftPlans.get(0) : null;
		public float progress;
		public float totalProgress;
		public float warmup;

		public int[] configs = {0, 0};
		public int lastRotation = -1;

		public TextureRegionDrawable[] rotationIcon = {Icon.right, Icon.up, Icon.left, Icon.down};

		@Override
		public void draw() {
			if (craftPlan == null || useBlockDrawer) {
				drawer.draw(this);
			} else {
				craftPlan.drawer.draw(this);
			}
		}

		@Override
		public void drawStatus() {
			if (block.enableDrawStatus && craftPlan != null && craftPlan.hasConsumers) {
				float multiplier = block.size > 1 ? 1 : 0.64f;
				float brcX = x + (block.size * 8) / 2f - 8f * multiplier / 2f;
				float brcY = y - (block.size * 8) / 2f + 8f * multiplier / 2f;
				Draw.z(71f);
				Draw.color(Pal.gray);
				Fill.square(brcX, brcY, 2.5f * multiplier, 45);
				Draw.color(status().color);
				Fill.square(brcX, brcY, 1.5f * multiplier, 45);
				Draw.color();
			}
		}

		public float warmupTarget() {
			return 1f;
		}

		public float formulaPower() {
			if (craftPlan == null) return 0f;

			ConsumePower consumePower = craftPlan.consPower;
			if (consumePower == null) return 0f;

			return consumePower.usage;

		}

		@Override
		public float getPowerProduction() {
			return craftPlan == null ? 0f : craftPlan.powerProduction;
		}

		@Override
		public void updateTile() {
			if (lastRotation != rotation) {
				Fx.placeBlock.at(x, y, size);
				lastRotation = rotation;
			}

			if (craftPlan == null) return;
			if (efficiency > 0) {
				progress += getProgressIncrease(craftPlan.craftTime, craftPlan);
				warmup = Mathf.approachDelta(warmup, warmupTarget(), craftPlan.warmupSpeed);

				if (craftPlan.outputLiquids.length > 0) {
					float inc = getProgressIncrease(1f);
					for (LiquidStack output : craftPlan.outputLiquids) {
						handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
					}
				}

				if (wasVisible && Mathf.chanceDelta(craftPlan.updateEffectChance)) {
					craftPlan.updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4));
				}
			} else {
				warmup = Mathf.approachDelta(warmup, 0f, craftPlan.warmupSpeed);
			}

			totalProgress += warmup * Time.delta;

			if (progress >= 1f) {
				craft(craftPlan);
			}

			dumpOutputs(craftPlan);
		}

		@Override
		public float totalProgress() {
			return totalProgress;
		}

		@Override
		public float progress() {
			return progress;
		}

		public float getProgressIncrease(float baseTime, CraftPlan craftPlan) {
			if (craftPlan.ignoreLiquidFullness) {
				return super.getProgressIncrease(baseTime);
			}

			float scaling = 1f, max = 1f;
			if (craftPlan.outputLiquids.length > 0) {
				max = 0f;
				for (LiquidStack output : craftPlan.outputLiquids) {
					float value = (liquidCapacity - liquids.get(output.liquid)) / (output.amount * edelta());
					scaling = Math.min(scaling, value);
					max = Math.max(max, value);
				}
			}

			return super.getProgressIncrease(baseTime) * (craftPlan.dumpExtraLiquid ? Math.min(max, 1f) : scaling);
		}

		public void craft(CraftPlan craftPlan) {
			consume();

			for (ItemStack output : craftPlan.outputItems) {
				for (int i = 0; i < output.amount; i++) {
					offload(output.item);
				}
			}

			if (wasVisible) {
				craftPlan.craftEffect.at(x, y);
			}
			progress %= 1f;
		}

		public void dumpOutputs(CraftPlan craftPlan) {
			if (craftPlan.outputItems.length > 0 && timer(timerDump, dumpTime / timeScale)) {
				for (ItemStack output : craftPlan.outputItems) {
					dump(output.item);
				}
			}

			if (craftPlan.outputLiquids.length > 0) {
				for (int i = 0; i < craftPlan.outputLiquids.length; i++) {
					int dir = craftPlan.liquidOutputDirections.length > i ? craftPlan.liquidOutputDirections[i] : -1;

					dumpLiquid(craftPlan.outputLiquids[i].liquid, 2f, dir);
				}
			}
		}

		@Override
		public boolean shouldConsume() {
			if (craftPlan == null) return false;
			for (ItemStack output : craftPlan.outputItems) {
				if (items.get(output.item) + output.amount > itemCapacity) {
					return false;
				}
			}
			if (craftPlan.outputLiquids.length > 0 && !craftPlan.ignoreLiquidFullness) {
				boolean allFull = true;
				for (LiquidStack output : craftPlan.outputLiquids) {
					if (liquids.get(output.liquid) >= liquidCapacity - 0.001f) {
						if (!craftPlan.dumpExtraLiquid) {
							return false;
						}
					} else {
						allFull = false;
					}
				}
				if (allFull) {
					return false;
				}
			}
			return enabled;
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			if (craftPlan == null) return false;
			return craftPlan.getConsumeItem(item) && items.get(item) < itemCapacity;
		}

		@Override
		public boolean acceptLiquid(Building source, Liquid liquid) {
			if (craftPlan == null) return false;
			return block.hasLiquids && craftPlan.getConsumeLiquid(liquid);
		}

		@Override
		public void consume() {
			if (craftPlan == null) return;

			for (Consume cons : craftPlan.consumers) {
				cons.trigger(this);
			}
		}

		@Override
		public void displayConsumption(Table table) {
			if (craftPlan == null) return;
			table.left();
			CraftPlan[] lastCraftPlan = {craftPlan};
			table.table(t -> {
				table.update(() -> {
					if (lastCraftPlan[0] != craftPlan) {
						rebuild(t);
						lastCraftPlan[0] = craftPlan;
					}
				});
				rebuild(t);
			});
		}

		protected void rebuild(Table table) {
			table.clear();

			for (Consume cons : craftPlan.consumers) {
				if (!cons.optional || !cons.booster) {
					cons.build(this, table);
				}
			}
		}

		@Override
		public void drawSelect() {
			super.drawSelect();
			if (craftPlan == null || !useLiquidTable) return;

			if (craftPlan.outputLiquids.length > 0) {
				for (int i = 0; i < craftPlan.outputLiquids.length; i++) {
					int dir = craftPlan.liquidOutputDirections.length > i ? craftPlan.liquidOutputDirections[i] : -1;

					if (dir != -1) {
						Draw.rect(
								craftPlan.outputLiquids[i].liquid.fullIcon,
								x + Geometry.d4x(dir + rotation) * (size * tilesize / 2f + 4),
								y + Geometry.d4y(dir + rotation) * (size * tilesize / 2f + 4),
								8f, 8f
						);
					}
				}
			}
		}

		@Override
		public void displayBars(Table table) {
			super.displayBars(table);
			if (craftPlan == null) return;

			CraftPlan[] lastCraftPlan = {craftPlan};
			table.update(() -> {
				if (lastCraftPlan[0] != craftPlan) {
					rebuildBar(table);
					lastCraftPlan[0] = craftPlan;
				}
			});
			rebuildBar(table);
		}

		protected void rebuildBar(Table table) {
			table.clear();

			for (Func<Building, Bar> bar : block.listBars()) {
				Bar result = bar.get(this);
				if (result != null) {
					table.add(result).growX();
					table.row();
				}
			}

			if (craftPlan == null || craftPlan.barMap.isEmpty()) return;

			for (Func<Building, Bar> bar : craftPlan.listBars()) {
				Bar result = bar.get(this);
				if (result == null) continue;
				table.add(result).growX();
				table.row();
			}
		}

		@Override
		public boolean shouldAmbientSound() {
			return efficiency > 0;
		}

		@Override
		public void updateConsumption() {
			if (craftPlan == null) return;

			//everything is valid when cheating
			if (!craftPlan.hasConsumers || cheating()) {
				potentialEfficiency = enabled && productionValid() ? 1f : 0f;
				efficiency = optionalEfficiency = shouldConsume() ? potentialEfficiency : 0f;
				shouldConsumePower = true;
				updateEfficiencyMultiplier();
				return;
			}

			//disabled -> nothing works
			if (!enabled) {
				potentialEfficiency = efficiency = optionalEfficiency = 0f;
				shouldConsumePower = true;
				return;
			}

			boolean update = shouldConsume() && productionValid();

			float minEfficiency = 1f;

			//assume efficiency is 1 for the calculations below
			efficiency = optionalEfficiency = 1f;
			shouldConsumePower = true;

			//first pass: get the minimum efficiency of any consumer
			for (Consume cons : craftPlan.nonOptionalConsumers) {
				float result = cons.efficiency(this);

				if (cons != consPower && result <= 0.0000001f) {
					shouldConsumePower = false;
				}

				minEfficiency = Math.min(minEfficiency, result);
			}

			//same for optionals
			for (Consume cons : craftPlan.optionalConsumers) {
				optionalEfficiency = Math.min(optionalEfficiency, cons.efficiency(this));
			}

			//efficiency is now this minimum value
			efficiency = minEfficiency;
			optionalEfficiency = Math.min(optionalEfficiency, minEfficiency);

			//assign "potential"
			potentialEfficiency = efficiency;

			//no updating means zero efficiency
			if (!update) {
				efficiency = optionalEfficiency = 0f;
			}

			updateEfficiencyMultiplier();

			//second pass: update every consumer based on efficiency
			if (update && efficiency > 0) {
				for (Consume cons : craftPlan.updateConsumers) {
					cons.update(this);
				}
			}
		}

		@Override
		public void buildConfiguration(Table table) {
			Table rtc = new Table();
			rtc.left().defaults().size(55);

			Table cont = new Table().top();
			cont.left().defaults().left().growX();

			Runnable rebuild = () -> {
				rtc.clearChildren();
				if (hasDoubleOutput) {
					for (int i = 0; i < rotationIcon.length; i++) {
						ImageButton button = new ImageButton();
						int j = i;
						button.table(img -> img.image(rotationIcon[j]).color(Color.white).size(40).pad(10f));
						button.changed(() -> {
							configs[0] = j;
							configure(configs);
						});
						button.update(() -> button.setChecked(rotation == j));
						button.setStyle(Styles.clearNoneTogglei);
						rtc.add(button).tooltip(String.valueOf(i * 90));
					}
				}

				cont.clearChildren();
				for (CraftPlan plan : craftPlans) {
					ImageButton button = new ImageButton();
					button.table(info -> {
						info.left();
						info.table(from -> {
							Stats stat = new Stats();
							stat.timePeriod = plan.craftTime;
							if (plan.hasConsumers)
								for (Consume c : plan.consumers) {
									c.display(stat);
								}
							Elements.statToTable(stat, from);
						}).left().pad(6);
						info.row();
						info.table(to -> {
							if (plan.outputItems.length > 0) {
								StatValues.items(plan.craftTime, plan.outputItems).display(to);
							}

							if (plan.outputLiquids.length > 0) {
								StatValues.liquids(1f, plan.outputLiquids).display(to);
							}
						}).left().pad(6);
					}).grow().left().pad(5);
					button.setStyle(Styles.clearNoneTogglei);
					button.changed(() -> {
						configs[1] = craftPlans.indexOf(plan);
						configure(configs);
					});
					button.update(() -> button.setChecked(craftPlan == plan));
					cont.add(button);
					cont.row();
				}
			};

			rebuild.run();

			Table main = new Table().background(Styles.black6);

			main.add(rtc).left().row();

			ScrollPane pane = new ScrollPane(cont, Styles.smallPane);
			pane.setScrollingDisabled(true, false);

			if (block != null) {
				pane.setScrollYForce(block.selectScroll);
				pane.update(() -> block.selectScroll = pane.getScrollY());
			}

			pane.setOverscroll(false, false);
			main.add(pane).maxHeight(100 * maxList);
			table.top().add(main);
		}

		@Override
		public double sense(LAccess sensor) {
			if (sensor == LAccess.progress) return progress;
			return super.sense(sensor);
		}

		@Override
		public Object config() {
			return configs;
		}

		@Override
		public void configure(Object value) {
			super.configure(value);
			deselect();
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.f(progress);
			write.f(warmup);
			write.i(lastRotation);
			write.i(craftPlan == null || !craftPlans.contains(craftPlan) ? -1 : craftPlans.indexOf(craftPlan));
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			progress = read.f();
			warmup = read.f();
			lastRotation = read.i();
			int i = read.i();
			craftPlan = i == -1 ? null : craftPlans.get(i);
			configs[0] = rotation;
			configs[1] = i;
		}
	}

	public static class CraftPlan {
		/** Array of consumers used by this block. Only populated after init(). */
		public Consume[] consumers = {}, optionalConsumers = {}, nonOptionalConsumers = {}, updateConsumers = {};
		/** The single power consumer, if applicable. */
		public @Nullable ConsumePower consPower = null;

		public float craftTime = 60f;
		/** Set to true if this formula has any consumers in its array. */
		public boolean hasConsumers = false;

		public ItemStack[] outputItems = {};
		public LiquidStack[] outputLiquids = {};

		/** Liquid output directions, specified in the same order as outputLiquids. Use -1 to dump in every direction. Rotations are relative to block. */
		public int[] liquidOutputDirections = {-1};
		public boolean ignoreLiquidFullness = false;
		/** if true, crafters with multiple liquid outputs will dump excess when there's still space for at least one liquid type */
		public boolean dumpExtraLiquid = true;

		public float warmupSpeed = 0.02f;

		public float updateEffectChance = 0.05f;

		/** The amount of power produced per tick in case of an efficiency of 1.0, which represents 100%. */
		public float powerProduction = 0f;

		/** The block must be HeatBlock, otherwise the following variables are invalid. */
		public float heatOutput = 0f;
		public float heatRequirement = 0f;
		public float warmupRate = 0.15f;

		public float maxHeatEfficiency = 1f;

		public Effect updateEffect = Fx.none;
		public Effect craftEffect = Fx.none;

		public DrawBlock drawer = new DrawDefault();

		/** Consumption filters. */
		public ObjectBoolMapf<Item> itemFilter = new ObjectBoolMapf<>(Item.class);
		public ObjectBoolMapf<Liquid> liquidFilter = new ObjectBoolMapf<>(Liquid.class);

		public MultiCrafter owner = null;

		/** List for building-up consumption before init(). */
		protected Seq<Consume> consumeBuilder = new Seq<>(Consume.class);
		/** Map of bars by name. */
		protected CollectionOrderedMap<String, Func<Building, Bar>> barMap = new CollectionOrderedMap<>(String.class, Func.class);

		public void init() {
			consumers = consumeBuilder.toArray(Consume.class);
			optionalConsumers = consumeBuilder.select(consume -> consume.optional && !consume.ignore()).toArray(Consume.class);
			nonOptionalConsumers = consumeBuilder.select(consume -> !consume.optional && !consume.ignore()).toArray(Consume.class);
			updateConsumers = consumeBuilder.select(consume -> consume.update && !consume.ignore()).toArray(Consume.class);
			hasConsumers = consumers.length > 0;

			if (owner.autoAddBar) {
				if (!liquidFilter.isEmpty()) {
					for (Liquid liquid : liquidFilter.keys().toSeq()) {
						addLiquidBar(liquid);
					}
				}
				for (LiquidStack liquid : outputLiquids) {
					addLiquidBar(liquid.liquid);
				}
			}
		}

		public void setApply(UnlockableContent content) {
			if (content instanceof Item item) {
				itemFilter.put(item, true);
			}
			if (content instanceof Liquid liquid) {
				liquidFilter.put(liquid, true);
			}
		}

		public Iterable<Func<Building, Bar>> listBars() {
			return barMap.values();
		}

		public void addBar(String name, Func<Building, Bar> sup) {
			barMap.put(name, sup);
		}

		public void addLiquidBar(Liquid liquid) {
			addBar("liquid-" + liquid.name, build -> !liquid.unlockedNow() ? null : new Bar(
					() -> liquid.localizedName,
					liquid::barColor,
					() -> build.liquids.get(liquid) / owner.liquidCapacity
			));
		}

		@SuppressWarnings("unchecked")
		public <T extends Consume> T findConsumer(Boolf<Consume> filter) {
			return consumers.length == 0 ? (T) consumeBuilder.find(filter) : (T) Structs.find(consumers, filter);
		}

		public boolean hasConsumer(Consume cons) {
			return consumeBuilder.contains(cons);
		}

		public void removeConsumer(Consume cons) {
			if (consumers.length > 0) {
				return;
			}
			consumeBuilder.remove(cons);
		}

		public void removeConsumers(Boolf<Consume> b) {
			consumeBuilder.removeAll(b);
			//the power was removed, unassign it
			if (!consumeBuilder.contains(c -> c instanceof ConsumePower)) {
				consPower = null;
			}
		}

		public boolean getConsumeItem(Item item) {
			return itemFilter.containsKey(item) && itemFilter.get(item);
		}

		public boolean getConsumeLiquid(Liquid liquid) {
			return liquidFilter.containsKey(liquid) && liquidFilter.get(liquid);
		}

		public void consumeLiquid(Liquid liquid, float amount) {
			setApply(liquid);
			consume(new ConsumeLiquid(liquid, amount));
		}

		public void consumeLiquids(LiquidStack... stacks) {
			for (LiquidStack liquid : stacks) setApply(liquid.liquid);
			consume(new ConsumeLiquids(stacks));
		}

		public void consumePower(float powerPerTick) {
			consume(new ConsumePower(powerPerTick, 0.0f, false));
		}

		public void consumeItem(Item item) {
			setApply(item);
			consumeItem(item, 1);
		}

		public void consumeItem(Item item, int amount) {
			setApply(item);
			consume(new ConsumeItem(new ItemStack(item, amount)));
		}

		public void consumeItems(ItemStack... items) {
			for (ItemStack item : items) {
				setApply(item.item);
			}
			consume(new ConsumeItems(items));
		}

		public <T extends Consume> void consume(T consume) {
			if (consume instanceof ConsumePower cons) {
				consumeBuilder.removeAll(b -> b instanceof ConsumePower);
				consPower = cons;
			}
			consumeBuilder.add(consume);
		}
	}
}