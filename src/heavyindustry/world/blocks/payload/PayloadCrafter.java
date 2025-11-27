package heavyindustry.world.blocks.payload;

import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.type.Recipe;
import heavyindustry.util.CollectionList;
import heavyindustry.util.CollectionObjectSet;
import heavyindustry.world.blocks.production.AdaptiveCrafter;
import mindustry.content.Fx;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.mod.NoPatch;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.PayloadSeq;
import mindustry.type.PayloadStack;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.meta.StatValue;

public class PayloadCrafter extends AdaptiveCrafter {
	public @NoPatch CollectionObjectSet<UnlockableContent> payloadFilter = new CollectionObjectSet<>(UnlockableContent.class);

	public @NoPatch CollectionList<UnlockableContent> payloadOutput = new CollectionList<>(UnlockableContent.class);

	public int payloadCapacity = 10;

	public PayloadCrafter(String name) {
		super(name);

		acceptsPayload = true;
		outputsPayload = true;
	}

	@Override
	public void init() {
		super.init();

		recipes.each(recipe -> {
			for (PayloadStack stack : recipe.inputPayload) payloadFilter.add(stack.item);
			for (PayloadStack stack : recipe.outputPayload) payloadOutput.add(stack.item);
		});
	}

	@Override
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
								for (ItemStack stack : recipe.inputItem) row.add(display(stack.item, stack.amount, recipe.craftTime));
								for (LiquidStack stack : recipe.inputLiquid) row.add(display(stack.liquid, stack.amount * Time.toSeconds, 60f));
								for (PayloadStack stack : recipe.inputPayload) row.add(display(stack.item, stack.amount, recipe.craftTime));
							}).growX();
							inner.table(row -> {
								row.left();
								row.image(Icon.right).size(32f).padLeft(8f).padRight(12f);
								for (ItemStack stack : recipe.outputItem) row.add(display(stack.item, stack.amount, recipe.craftTime));
								for (LiquidStack stack : recipe.outputLiquid) row.add(display(stack.liquid, stack.amount * Time.toSeconds, 60f));
								for (PayloadStack stack : recipe.outputPayload) row.add(display(stack.item, stack.amount, recipe.craftTime));
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
		if (buildType == null) buildType = PayloadCrafterBuild::new;
	}

	public class PayloadCrafterBuild extends AdaptiveCrafterBuild {
		public PayloadSeq payloads = new PayloadSeq();

		@Override
		public PayloadSeq getPayloads() {
			return payloads;
		}

		@Override
		public boolean validRecipe() {
			if (recipeIndex < 0) return false;

			Recipe recipe = recipes.get(recipeIndex);

			for (PayloadStack input : recipe.inputPayload) {
				if (getPayloads().get(input.item) < input.amount) {
					return false;
				}
			}
			return super.validRecipe();
		}

		@Override
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

				for (PayloadStack input : recipe.inputPayload) {
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

		@Override
		public void updateTile() {
			super.updateTile();

			Recipe recipe = getRecipe();

			if (recipe == null) return;

			for (PayloadStack stack : recipe.outputPayload) {
				if (getPayloads().get(stack.item) >= payloadCapacity) {
					getPayloads().remove(stack.item, getPayloads().get(stack.item) - payloadCapacity);
				}
			}
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
		public void handlePayload(Building source, Payload payload) {
			payloads.add(payload.content(), 1);
			Fx.payloadDeposit.at(payload.x(), payload.y(), payload.angleTo(this), new UnitAssembler.YeetData(new Vec2(x, y), payload.content()));
		}

		@Override
		public boolean acceptPayload(Building source, Payload payload) {
			return payloadFilter.contains(payload.content()) && getPayloads().get(payload.content()) < payloadCapacity;
		}

		@Override
		public void craft() {
			Recipe recipe = getRecipe();

			if (recipe == null) return;

			consume();

			for (ItemStack stack : recipe.outputItem) {
				for (int i = 0; i < stack.amount; i++) {
					offload(stack.item);
				}
			}
			for (PayloadStack stack : recipe.outputPayload) payloads.add(stack.item, stack.amount);

			progress %= 1f;

			if (wasVisible) craftEffect.at(x, y);
			updateRecipe();
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
