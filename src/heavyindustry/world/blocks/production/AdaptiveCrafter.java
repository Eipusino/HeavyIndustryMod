package heavyindustry.world.blocks.production;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.math.Mathm;
import heavyindustry.type.Recipe;
import heavyindustry.util.CollectionList;
import heavyindustry.world.consumers.ConsumeRecipe;
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
import mindustry.mod.NoPatch;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.liquid.Conduit.ConduitBuild;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockStatus;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValue;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.world;

public class AdaptiveCrafter extends Block {
	/** Liquid output directions, specified in the same order as outputLiquids. Use -1 to dump in every direction. Rotations are relative to block. */
	public int[] liquidOutputDirections = {-1};

	/** if true, crafters with multiple liquid outputs will dump excess when there's still space for at least one liquid type */
	public boolean dumpExtraLiquid = true;
	public boolean ignoreLiquidFullness = false;

	public float craftTime = 80;
	public Effect craftEffect = Fx.none;
	public Effect updateEffect = Fx.none;
	public float updateEffectChance = 0.04f;
	public float updateEffectSpread = 4f;
	public float warmupSpeed = 0.019f;

	public DrawBlock drawer = new DrawDefault();

	public CollectionList<Recipe> recipes = new CollectionList<>(Recipe.class);

	@NoPatch
	public CollectionList<Item> itemOutput = new CollectionList<>(Item.class);
	@NoPatch
	public CollectionList<Liquid> liquidOutput = new CollectionList<>(Liquid.class);

	public float powerProduction = 0f;

	public AdaptiveCrafter(String name) {
		super(name);

		update = true;
		solid = true;
		sync = true;
		ambientSound = Sounds.loopMachine;
		ambientSoundVolume = 0.03f;
		flags = EnumSet.of(BlockFlag.factory);
		drawArrow = false;

		hasItems = true;
		hasLiquids = true;
		hasPower = true;

		consume(new ConsumeRecipe(AdaptiveCrafterBuild::getRecipe, AdaptiveCrafterBuild::getDisplayRecipe));
	}

	public static Table display(UnlockableContent content, float amount, float timePeriod) {
		Table table = new Table();
		Stack stack = new Stack();

		stack.add(new Table(o -> {
			o.left();
			o.add(new Image(content.uiIcon)).size(32f).scaling(Scaling.fit);
		}));

		if (amount != 0) {
			stack.add(new Table(t -> {
				t.left().bottom();
				t.add(amount >= 1000 ? UI.formatAmount((int) amount) : Strings.autoFixed(amount, 2)).style(Styles.outlineLabel);
				t.pack();
			}));
		}

		StatValues.withTooltip(stack, content);

		table.add(stack);
		table.add((content.localizedName + "\n") + "[lightgray]" + Strings.autoFixed(amount / (timePeriod / 60f), 2) + StatUnit.perSecond.localized()).padLeft(2).padRight(5).style(Styles.outlineLabel);
		return table;
	}

	@Override
	public void load() {
		super.load();

		drawer.load(this);
	}

	@Override
	public void init() {
		if (powerProduction > 0f) {
			consumesPower = false;
			outputsPower = true;

			// Don't do anything foolish.
			consPower = null;
			consumeBuilder.removeAll(c -> c instanceof ConsumePower);
		}

		super.init();

		recipes.each(recipe -> {
			for (ItemStack stack : recipe.inputItem) itemFilter[stack.item.id] = true;
			for (LiquidStack stack : recipe.inputLiquid) liquidFilter[stack.liquid.id] = true;

			for (ItemStack stack : recipe.outputItem) itemOutput.add(stack.item);
			for (LiquidStack stack : recipe.outputLiquid) liquidOutput.add(stack.liquid);
		});

		craftTime = 60f;
	}

	@Override
	public void setBars() {
		super.setBars();

		if (hasPower && outputsPower && powerProduction > 0f) {
			removeBar("power");
			addBar("power", (AdaptiveCrafterBuild tile) -> new Bar(() ->
					Core.bundle.format("bar.poweroutput",
							Strings.fixed(tile.getPowerProduction() * 60 * tile.timeScale(), 1)),
					() -> Pal.powerBar,
					() -> tile.warmup));

			addBar("asd", (AdaptiveCrafterBuild tile) -> new Bar(() ->
					Core.bundle.format("bar.poweroutput",
							Strings.fixed(tile.efficiency, 1)),
					() -> Pal.powerBar,
					() -> tile.warmup));
		}
	}

	@Override
	public void setStats() {
		super.setStats();

		if (powerProduction > 0) {
			stats.remove(Stat.powerUse);
			stats.add(Stat.basePowerGeneration, powerProduction * 60f, StatUnit.powerSecond);
		}

		stats.add(Stat.input, display());
		stats.remove(Stat.output);
		stats.remove(Stat.productionTime);
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
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		drawer.drawPlan(this, plan, list);
	}

	@Override
	public TextureRegion[] icons() {
		return drawer.finalIcons(this);
	}

	@Override
	public void getRegionsToOutline(Seq<TextureRegion> out) {
		drawer.getRegionsToOutline(this, out);
	}

	public StatValue display() {
		return table -> {
			table.row();
			table.table(cont -> {
				for (int i = 0; i < recipes.size; i++) {
					Recipe recipe = recipes.get(i);
					int j = i;
					cont.table(t -> {
						t.left().marginLeft(12f).add("[accent][" + (j + 1) + "]:[]").width(48f);
						t.table(inner -> {
							inner.table(row -> {
								row.left();
								for (ItemStack stack : recipe.inputItem)
									row.add(display(stack.item, stack.amount, recipe.craftTime));
								for (LiquidStack stack : recipe.inputLiquid)
									row.add(display(stack.liquid, stack.amount * Time.toSeconds, 60f));
							}).growX();
							inner.table(row -> {
								row.left();
								row.image(Icon.right).size(32f).padLeft(8f).padRight(12f);
								for (ItemStack stack : recipe.outputItem)
									row.add(display(stack.item, stack.amount, recipe.craftTime));
								for (LiquidStack stack : recipe.outputLiquid)
									row.add(display(stack.liquid, stack.amount * Time.toSeconds, 60f));
							}).growX();
						});
					}).fillX();
					cont.row();
				}
			});
		};
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = AdaptiveCrafterBuild::new;
	}

	public class AdaptiveCrafterBuild extends Building {
		public float progress;
		public float totalProgress;
		public float warmup;

		public int recipeIndex = -1;

		@Override
		public void draw() {
			drawer.draw(this);
		}

		@Override
		public void drawLight() {
			super.drawLight();
			drawer.drawLight(this);
		}

		public Recipe getRecipe() {
			if (recipeIndex < 0 || recipeIndex >= recipes.size) return null;
			return recipes.get(recipeIndex);
		}

		public Recipe getDisplayRecipe() {
			if (recipeIndex < 0 && recipes.size > 0) {
				return recipes.first();
			}
			return getRecipe();
		}

		public void updateRecipe() {
			for (int i = recipes.size - 1; i >= 0; i--) {
				boolean valid = true;

				Recipe recipe = recipes.get(i);

				for (ItemStack input : recipe.inputItem) {
					if (items.get(input.item) < input.amount) {
						valid = false;
						break;
					}
				}

				for (LiquidStack input : recipe.inputLiquid) {
					if (liquids.get(input.liquid) < input.amount * Time.delta) {
						valid = false;
						break;
					}
				}

				if (valid) {
					recipeIndex = i;
					return;
				}
			}
			recipeIndex = -1;
		}

		public boolean validRecipe() {
			if (recipeIndex < 0) return false;

			Recipe recipe = recipes.get(recipeIndex);

			for (ItemStack input : recipe.inputItem) {
				if (items.get(input.item) < input.amount) {
					return false;
				}
			}

			for (LiquidStack input : recipe.inputLiquid) {
				if (liquids.get(input.liquid) < input.amount * Time.delta) {
					return false;
				}
			}
			return true;
		}

		@Override
		public void updateTile() {
			if (!validRecipe()) updateRecipe();

			if (efficiency > 0) {
				progress += getProgressIncrease(craftTime);
				warmup = Mathf.approachDelta(warmup, warmupTarget(), warmupSpeed);

				if (wasVisible && Mathf.chanceDelta(updateEffectChance)) {
					updateEffect.at(x + Mathf.range(size * updateEffectSpread), y + Mathf.range(size * updateEffectSpread));
				}
			} else {
				warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
			}

			//TODO may look bad, revert to edelta() if so
			totalProgress += warmup * Time.delta;

			if (progress >= 1f) {
				craft();
			}

			dumpOutputs();

			Recipe recipe = getRecipe();

			if (recipe == null) return;

			if (efficiency > 0) {
				float inc = getProgressIncrease(1f);
				for (LiquidStack output : recipe.outputLiquid) {
					handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
				}
			}

			for (ItemStack stack : recipe.outputItem) {
				if (items.get(stack.item) >= itemCapacity) {
					items.set(stack.item, itemCapacity);
				}
			}
		}

		public void dumpOutputs() {
			boolean timer = timer(timerDump, dumpTime / timeScale);
			if (timer) {
				itemOutput.each(this::dump);
			}
			liquidOutput.each(output -> dumpLiquid(output, 2f, -1));
		}

		@Override
		public boolean shouldConsume() {
			Recipe recipe = getRecipe();

			if (recipe == null) return false;

			for (ItemStack output : recipe.outputItem) {
				if (items.get(output.item) + output.amount > itemCapacity) {
					return powerProduction > 0;
				}
			}
			if (!ignoreLiquidFullness) {
				if (recipe.outputLiquid.length == 0) return true;

				boolean allFull = true;
				for (LiquidStack output : recipe.outputLiquid) {
					if (liquids.get(output.liquid) >= liquidCapacity - 0.001f) {
						if (!dumpExtraLiquid) {
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
		public float getPowerProduction() {
			return powerProduction * warmup * efficiency;
		}

		@Override
		public float getProgressIncrease(float baseTime) {
			float scl = 0f;

			Recipe recipe = getRecipe();

			if (recipe != null) scl = recipe.craftTime / craftTime;

			return super.getProgressIncrease(baseTime) / scl;
		}

		public void craft() {
			Recipe recipe = getRecipe();

			if (recipe == null) return;

			consume();

			for (ItemStack stack : recipe.outputItem) {
				for (int i = 0; i < stack.amount; i++) {
					offload(stack.item);
				}
			}

			progress %= 1f;

			if (wasVisible) craftEffect.at(x, y);
			updateRecipe();
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

		@Override
		public BlockStatus status() {
			if (enabled && getRecipe() == null) return BlockStatus.noInput;
			return super.status();
		}

		@Override
		public double sense(LAccess sensor) {
			if (sensor == LAccess.progress) return progress();
			return super.sense(sensor);
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.f(progress);
			write.f(warmup);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			progress = read.f();
			warmup = read.f();
		}
	}
}
