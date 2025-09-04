package heavyindustry.world.blocks.production;

import arc.Core;
import arc.math.geom.Vec2;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectSet;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.type.Recipe;
import heavyindustry.world.consumers.ConsumeRecipe;
import mindustry.content.Fx;
import mindustry.core.UI;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.type.PayloadSeq;
import mindustry.type.PayloadStack;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.meta.BlockStatus;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValue;
import mindustry.world.meta.StatValues;

public class AdaptiveCrafter extends GenericCrafter {
	public ObjectSet<UnlockableContent> payloadFilter = new ObjectSet<>();
	public Seq<Recipe> recipes = new Seq<>(Recipe.class);

	public Seq<Item> itemOutput = new Seq<>(Item.class);
	public Seq<Liquid> liquidOutput = new Seq<>(Liquid.class);
	public Seq<UnlockableContent> payloadOutput = new Seq<>(UnlockableContent.class);

	public float powerProduction = 0f;
	public int payloadCapacity = 10;

	public AdaptiveCrafter(String name) {
		super(name);

		hasItems = true;
		hasLiquids = true;
		hasPower = true;

		acceptsPayload = true;
		outputsPayload = true;

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
	public void init() {
		super.init();

		if (powerProduction > 0f) {
			consumesPower = false;
			outputsPower = true;
		}

		recipes.each(recipe -> {
			recipe.inputItem.each(stack -> itemFilter[stack.item.id] = true);
			recipe.inputLiquid.each(stack -> liquidFilter[stack.liquid.id] = true);
			recipe.inputPayload.each(stack -> payloadFilter.add(stack.item));

			recipe.outputItem.each(stack -> itemOutput.add(stack.item));
			recipe.outputLiquid.each(stack -> liquidOutput.add(stack.liquid));
			recipe.outputPayload.each(stack -> payloadOutput.add(stack.item));
		});

		outputItem = null;
		outputLiquid = null;

		outputItems = null;
		outputLiquids = null;

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
								recipe.inputItem.each(stack -> row.add(display(stack.item, stack.amount, recipe.craftTime)));
								recipe.inputLiquid.each(stack -> row.add(display(stack.liquid, stack.amount * Time.toSeconds, 60f)));
								recipe.inputPayload.each(stack -> row.add(display(stack.item, stack.amount, recipe.craftTime)));
							}).growX();
							inner.table(row -> {
								row.left();
								row.image(Icon.right).size(32f).padLeft(8f).padRight(12f);
								recipe.outputItem.each(stack -> row.add(display(stack.item, stack.amount, recipe.craftTime)));
								recipe.outputLiquid.each(stack -> row.add(display(stack.liquid, stack.amount * Time.toSeconds, 60f)));
								recipe.outputPayload.each(stack -> row.add(display(stack.item, stack.amount, recipe.craftTime)));
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

	public class AdaptiveCrafterBuild extends GenericCrafterBuild {
		public PayloadSeq payloads = new PayloadSeq();

		public int recipeIndex = -1;

		@Override
		public PayloadSeq getPayloads() {
			return payloads;
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

				for (ItemStack input : recipes.get(i).inputItem) {
					if (items.get(input.item) < input.amount) {
						valid = false;
						break;
					}
				}

				for (LiquidStack input : recipes.get(i).inputLiquid) {
					if (liquids.get(input.liquid) < input.amount * Time.delta) {
						valid = false;
						break;
					}
				}

				for (PayloadStack input : recipes.get(i).inputPayload) {
					if (getPayloads().get(input.item) < input.amount) {
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
			for (ItemStack input : recipes.get(recipeIndex).inputItem) {
				if (items.get(input.item) < input.amount) {
					return false;
				}
			}

			for (LiquidStack input : recipes.get(recipeIndex).inputLiquid) {
				if (liquids.get(input.liquid) < input.amount * Time.delta) {
					return false;
				}
			}

			for (PayloadStack input : recipes.get(recipeIndex).inputPayload) {
				if (getPayloads().get(input.item) < input.amount) {
					return false;
				}
			}
			return true;
		}

		@Override
		public void updateTile() {
			if (!validRecipe()) updateRecipe();

			super.updateTile();

			Recipe recipe = getRecipe();

			if (efficiency > 0) {
				if (recipe != null) {
					float inc = getProgressIncrease(1f);
					for (LiquidStack output : recipe.outputLiquid) {
						handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
					}
				}
			}

			if (recipe == null) return;
			recipe.outputItem.each(stack -> {
				if (items.get(stack.item) >= itemCapacity) {
					items.set(stack.item, itemCapacity);
				}
			});
			recipe.outputPayload.each(stack -> {
				if (getPayloads().get(stack.item) >= payloadCapacity) {
					getPayloads().remove(stack.item, getPayloads().get(stack.item) - payloadCapacity);
				}
			});
		}

		@Override
		public void dumpOutputs() {
			boolean timer = timer(timerDump, dumpTime / timeScale);
			if (timer) {
				itemOutput.each(this::dump);
				payloadOutput.each(output -> {
					BuildPayload payload = new BuildPayload((Block) output, team);
					payload.set(x, y, rotdeg());
					dumpPayload(payload);
				});
			}
			liquidOutput.each(output -> dumpLiquid(output, 2f, -1));
		}

		@Override
		public void handlePayload(Building source, Payload payload) {
			payloads.add(payload.content(), 1);
			Fx.payloadDeposit.at(payload.x(), payload.y(), payload.angleTo(this), new UnitAssembler.YeetData(new Vec2(x, y), payload.content()));
		}

		@Override
		public boolean acceptPayload(Building source, Payload payload) {
			return payloadFilter.contains(payload.content()) && getPayloads().get(payload.content()) < payloadCapacity;
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
			for (PayloadStack output : recipe.outputPayload) {
				if (getPayloads().get(output.item) + output.amount > payloadCapacity) {
					return powerProduction > 0;
				}
			}
			if (!ignoreLiquidFullness) {
				if (recipe.outputLiquid.isEmpty()) return true;
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

		@Override
		public void craft() {
			Recipe recipe = getRecipe();

			if (recipe == null) return;

			consume();

			recipe.outputItem.each(stack -> {
				for (int i = 0; i < stack.amount; i++) {
					offload(stack.item);
				}
			});
			recipe.outputPayload.each(stack -> payloads.add(stack.item, stack.amount));

			progress %= 1f;

			if (wasVisible) craftEffect.at(x, y);
			updateRecipe();
		}

		@Override
		public BlockStatus status() {
			if (enabled && getRecipe() == null) return BlockStatus.noInput;
			return super.status();
		}

		@Override
		public byte version() {
			return 1;
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			payloads.write(write);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			if (revision == 1) {
				payloads.read(read);
			}
		}
	}
}
