package heavyindustry.world.blocks.payload;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.Image;
import arc.scene.ui.ImageButton;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Scl;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Scaling;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.graphics.Drawn;
import heavyindustry.world.consumers.ConsumeLiquidDynamic;
import heavyindustry.world.meta.HIStat;
import heavyindustry.world.meta.HIStatValues;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.ui.ReqImage;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.meta.Stat;

import static mindustry.Vars.content;
import static mindustry.Vars.state;

/**
 * TODO debug
 *
 * @since 1.0.6
 */
public class PayloadCrafter extends PayloadBlock {
	public Seq<PayloadRecipe> recipes = new Seq<>();
	public boolean hasTop = true;
	private float scrollPos;

	public PayloadCrafter(String name) {
		super(name);

		update = true;
		rotate = true;
		configurable = logicConfigurable = true;

		config(Block.class, (PayloadCrafterBuild tile, Block block) -> {
			if (tile.recipe != block) tile.progress = 0f;
			if (canProduce(block)) {
				tile.recipe = block;
			}
		});

		configClear((PayloadCrafterBuild tile) -> {
			tile.recipe = null;
			tile.progress = 0f;
		});
	}

	public void recipes(Block... blocks) {
		for (Block b : blocks) {
			recipes.add(new PayloadRecipe(b));
		}
	}

	public void recipes(PayloadRecipe... newRecipes) {
		for (PayloadRecipe r : newRecipes) {
			recipes.add(r);
		}
	}

	public void setRecipeProductionStats() {
		for (PayloadRecipe r : recipes) {
			if (r.outputBlock != null) {
				r.outputBlock.stats.add(HIStat.producer, s -> {
					s.row();
					s.table(Styles.grayPanel, t -> {
						t.left().defaults().top().left();
						if (state.rules.bannedBlocks.contains(this)) {
							t.image(Icon.cancel).color(Pal.remove).size(40);
							return;
						}

						t.image(fullIcon).size(96f);
						t.table(n -> {
							n.defaults().left();
							n.add(localizedName);
							n.row();
							HIStatValues.infoButton(n, this, 4f * 8f).padTop(4f);
						}).padLeft(8f);
					}).left().top().growX().margin(10f).padTop(5).padBottom(5);
				});

				if (r.hasInputBlock()) {
					r.inputBlock.stats.add(HIStat.produce, s -> {
						s.row();
						s.table(Styles.grayPanel, t -> {
							t.left().defaults().top().left();
							if (state.rules.bannedBlocks.contains(r.outputBlock)) {
								t.image(Icon.cancel).color(Pal.remove).size(40);
								return;
							}
							if (!r.outputBlock.unlockedNow()) {
								t.image(Icon.lock).color(Pal.darkerGray).size(40);
								t.add("@pm-missing-research").center().left();
								return;
							}

							t.image(r.outputBlock.fullIcon).size(96f);
							t.table(n -> {
								n.defaults().left();
								n.add(r.outputBlock.localizedName);
								n.row();
								HIStatValues.infoButton(n, this, 4f * 8f).padTop(4f);
							}).padLeft(8f);
						}).left().top().growX().margin(10f).padTop(5).padBottom(5);
					});
				}
			}
		}
	}

	@Override
	public void init() {
		if (recipes.contains(r -> r.powerUse > 0)) {
			consumePowerDynamic(b -> ((PayloadCrafterBuild) b).powerUse());
		}
		if (recipes.contains(r -> r.itemRequirements != null)) {
			consume(new ConsumeItemDynamic((PayloadCrafterBuild e) -> e.hasRecipe() && e.recipe().itemRequirements != null ? e.recipe().itemRequirements : ItemStack.empty));
		}
		if (recipes.contains(r -> r.liquidRequirements != null)) {
			consume(new ConsumeLiquidDynamic((PayloadCrafterBuild e) -> e.hasRecipe() ? e.recipe().liquidRequirements : null));
		}
		if (recipes.contains(r -> r.inputBlock != null)) acceptsPayload = true;
		if (recipes.contains(r -> r.outputBlock != null)) outputsPayload = true;

		super.init();
	}

	@Override
	public void load() {
		super.load();

		inRegion = Core.atlas.find(name + "-in", Core.atlas.find("factory-in-" + size + regionSuffix, "prog-mats-factory-in-" + size + regionSuffix));
		outRegion = Core.atlas.find(name + "-out", Core.atlas.find("factory-out-" + size + regionSuffix, "prog-mats-factory-out-" + size + regionSuffix));
		if (!hasTop) topRegion = Core.atlas.find("clear");
	}

	@Override
	public TextureRegion[] icons() {
		if (recipes.contains(PayloadRecipe::hasInputBlock)) {
			return new TextureRegion[]{region, inRegion, outRegion, topRegion};
		}
		return new TextureRegion[]{region, outRegion, topRegion};
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.remove(Stat.powerUse);
		stats.remove(Stat.itemCapacity);
		stats.remove(Stat.liquidCapacity);

		stats.add(HIStat.recipes, HIStatValues.payloadProducts(recipes));
	}

	@Override
	public void setBars() {
		super.setBars();

		if (hasLiquids) {
			removeBar("liquid");
			addBar("liquid", (PayloadCrafterBuild entity) -> {
				Liquid l = entity.hasRecipe() ? entity.recipe().getLiquidInput() : null;
				return new Bar(
						() -> l != null ? l.localizedName : Core.bundle.get("bar.liquid"),
						() -> l != null ? l.barColor() : Color.white,
						() -> entity.liquids == null || l == null ? 0f : entity.liquids.get(l) / liquidCapacity
				);
			});
		}

		addBar("progress", (PayloadCrafterBuild entity) -> new Bar(
				"bar.progress",
				Pal.ammo,
				() -> entity.recipe() == null ? 0f : (entity.progress / entity.recipe().craftTime)
		));
	}

	@Override
	public void drawPlanRegion(BuildPlan req, Eachable<BuildPlan> list) {
		Draw.rect(region, req.drawx(), req.drawy());
		if (recipes.contains(r -> r.inputBlock != null))
			Draw.rect(inRegion, req.drawx(), req.drawy(), req.rotation * 90);
		Draw.rect(outRegion, req.drawx(), req.drawy(), req.rotation * 90);
		Draw.rect(topRegion, req.drawx(), req.drawy());
	}

	public boolean canProduce(Block b) {
		if (recipes.contains(r -> r.outputBlock == b)) {
			return recipes.find(r -> r.outputBlock == b).unlocked();
		}
		return false;
	}

	public static class PayloadRecipe {
		public float craftTime;
		public boolean requiresUnlock = true;
		public boolean blockBuild = true, centerBuild;

		public ItemStack[] itemRequirements;
		public LiquidStack liquidRequirements;
		public float powerUse;
		public Block inputBlock;

		public Block outputBlock;

		public PayloadRecipe(Block block) {
			outputBlock = block;
			itemRequirements = block.requirements;
		}

		public PayloadRecipe(Block block, float powerUse, float craftTime) {
			this(block);
			this.craftTime = craftTime;
			this.powerUse = powerUse;
		}

		public PayloadRecipe(Block outputBlock, Block inputBlock, float powerUse, float craftTime) {
			this(outputBlock, powerUse, craftTime);
			this.inputBlock = inputBlock;
		}

		public Liquid getLiquidInput() {
			return liquidRequirements != null ? liquidRequirements.liquid : null;
		}

		public boolean hasLiquidInput(Liquid liquid) {
			return liquidRequirements != null && liquidRequirements.liquid == liquid;
		}

		public boolean hasInputBlock() {
			return inputBlock != null;
		}

		public boolean showReqList() {
			return itemRequirements.length > 0 || liquidRequirements != null;
		}

		public boolean unlocked() {
			return !requiresUnlock || outputBlock.unlockedNow();
		}
	}

	public class PayloadCrafterBuild extends PayloadBlockBuild<BuildPayload> {
		public float progress, time, heat;
		public @Nullable Block recipe;
		public boolean produce;

		public @Nullable PayloadRecipe recipe() {
			return recipes.find(r -> r.outputBlock == recipe);
		}

		public boolean hasRecipe() {
			return recipe() != null;
		}

		@Override
		public Object senseObject(LAccess sensor) {
			if (sensor == LAccess.config) return recipe;
			if (sensor == LAccess.progress) return progress;
			return super.senseObject(sensor);
		}

		@Override
		public void updateTile() {
			super.updateTile();
			PayloadRecipe recipe = recipe();
			produce = recipe != null && canConsume() &&
					(recipe.inputBlock != null ? (payload != null && hasArrived() && payload.block() == recipe.inputBlock) : payload == null);

			if (payload != null) {
				if (recipe != null) {
					if (payload.block() != recipe.inputBlock) {
						moveOutPayload();
					}
				} else if (!recipes.contains(r -> r.inputBlock == payload.block())) {
					moveOutPayload();
				}
			}

			if (recipe != null && payload != null && payload.block() == recipe.inputBlock) {
				moveInPayload(false);
			}

			if (produce && recipe != null) {
				progress += edelta();

				if (progress >= recipe.craftTime) {
					craft(recipe);
				}
			} else if (recipe == null || !canConsume()) {
				progress = 0f;
			}

			heat = Mathf.lerpDelta(heat, Mathf.num(produce), 0.15f);
			time += heat * delta();
		}

		public void craft(PayloadRecipe recipe) {
			consume();

			payload = new BuildPayload(recipe.outputBlock, team);
			payVector.setZero();
			progress %= 1f;
		}

		public float powerUse() {
			return hasRecipe() ? recipe().powerUse : 0f;
		}

		@Override
		public void draw() {
			Draw.rect(region, x, y);

			//draw input
			if (curInput()) {
				for (int i = 0; i < 4; i++) {
					if (blends(i) && i != rotation) {
						Draw.rect(inRegion, x, y, (i * 90f) - 180f);
					}
				}
			}

			Draw.rect(outRegion, x, y, rotdeg());

			if (recipe != null) {
				PayloadRecipe r = recipe();
				Draw.draw(Layer.blockBuilding, () -> {
					if (r.blockBuild) {
						for (TextureRegion region : recipe.getGeneratedIcons()) {
							if (r.centerBuild) {
								Drawn.blockBuildCenter(x, y, region, recipe.rotate ? rotdeg() : 0, progress / r.craftTime);
							} else {
								Drawn.blockBuild(x, y, region, recipe.rotate ? rotdeg() : 0, progress / r.craftTime);
							}
						}
					} else {
						Drawf.construct(this, recipe.fullIcon, 0, progress / r.craftTime, heat, time);
					}
				});

				if (r.blockBuild) {
					Draw.z(Layer.blockBuilding + 0.01f);
					Draw.color(Pal.accent, heat);

					Lines.lineAngleCenter(x + Mathf.sin(time, 10f, Vars.tilesize / 2f * recipe.size + 1f), y, 90, recipe.size * Vars.tilesize + 1f);

					Draw.reset();
				}
			}
			Draw.z(Layer.blockBuilding + 1f);
			Draw.rect(topRegion, x, y);

			drawPayload();
		}

		public boolean curInput() {
			return hasRecipe() && recipe().inputBlock != null;
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			return items != null && items.get(item) < getMaximumAccepted(item);
		}

		@Override
		public int getMaximumAccepted(Item item) {
			if (recipe() == null) return 0;
			for (ItemStack stack : recipe().itemRequirements) {
				if (stack.item == item) return stack.amount * 2;
			}
			return 0;
		}

		@Override
		public boolean acceptLiquid(Building source, Liquid liquid) {
			return liquids != null && hasRecipe() && recipe().hasLiquidInput(liquid);
		}

		@Override
		public void buildConfiguration(Table table) {
			ButtonGroup<ImageButton> group = new ButtonGroup<>();
			group.setMinCheckCount(0);
			Table cont = new Table();
			cont.defaults().size(40);

			int i = 0;

			for (Block b : content.blocks()) {
				if (recipes.contains(r -> r.outputBlock == b)) {
					Cell<ImageButton> cell = cont.button(Tex.clear, Styles.clearTogglei, 24, () -> {
					}).group(group);
					ImageButton button = cell.get();
					PayloadRecipe r = recipes.find(rec -> rec.outputBlock == b);
					button.update(() -> button.setChecked(recipe == b));

					if (r.unlocked()) {
						button.getStyle().imageUp = new TextureRegionDrawable(b.uiIcon);
						cell.tooltip(b.localizedName);
					} else {
						button.getStyle().imageUp = Icon.lock;
						cell.tooltip("@pm-missing-research");
					}
					button.changed(() -> configure(button.isChecked() ? b : null));

					if (i++ % 4 == 3) {
						cont.row();
					}
				}
			}

			//add extra blank spaces so it looks nice
			if (i % 4 != 0) {
				int remaining = 4 - (i % 4);
				for (int j = 0; j < remaining; j++) {
					cont.image(Styles.black6);
				}
			}

			ScrollPane pane = new ScrollPane(cont, Styles.smallPane);
			pane.setScrollingDisabled(true, false);
			pane.setScrollYForce(scrollPos);
			pane.update(() -> scrollPos = pane.getScrollY());

			pane.setOverscroll(false, false);
			table.add(pane).maxHeight(Scl.scl(40 * 5));
		}

		@Override
		public Object config() {
			return recipe;
		}

		@Override
		public boolean acceptPayload(Building source, Payload payload) {
			PayloadRecipe r = recipe();
			return this.payload == null && r != null && payload instanceof BuildPayload p && p.block() == r.inputBlock;
		}

		@Override
		public void display(Table table) {
			super.display(table);

			Image prev = new Image();
			TextureRegionDrawable prevReg = new TextureRegionDrawable();

			table.row();
			table.table(p -> {
				p.update(() -> {
					p.clear();
					if (hasRecipe() && recipe().hasInputBlock()) {
						p.label(() -> Core.bundle.get("pm-requires")).color(Color.lightGray).padRight(2f);
						prev.setDrawable(prevReg.set(recipe().inputBlock.uiIcon));
						ReqImage r = new ReqImage(prev, () -> payload != null && hasArrived() && payload.block() == recipe().inputBlock);
						r.setSize(32);
						p.add(r).size(32).padBottom(-4).padRight(2);
						p.label(() -> recipe().inputBlock.localizedName).color(Color.lightGray);
					}
				});
			}).left();

			TextureRegionDrawable reg = new TextureRegionDrawable();

			table.row();
			table.table(t -> {
				t.left();
				t.image().update(i -> {
					i.setDrawable(recipe == null ? Icon.cancel : reg.set(recipe.uiIcon));
					i.setScaling(Scaling.fit);
					i.setColor(recipe == null ? Color.lightGray : Color.white);
				}).size(32).padBottom(-4).padRight(2);
			}).left().get().label(() -> recipe == null ? "@none" : recipe.localizedName).color(Color.lightGray);
		}

		@Override
		public boolean shouldAmbientSound() {
			return super.shouldAmbientSound() && produce;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.s(recipe == null ? -1 : recipe.id);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			if (revision >= 1) {
				recipe = Vars.content.block(read.s());
			}
		}

		@Override
		public byte version() {
			return 1;
		}
	}
}
