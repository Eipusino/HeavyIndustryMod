package heavyindustry.world.blocks.units;

import arc.Core;
import arc.func.Boolp;
import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Scaling;
import arc.util.Strings;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.entities.HEntity;
import heavyindustry.gen.Spawner;
import heavyindustry.type.Recipe;
import heavyindustry.ui.DelaySlideTable;
import heavyindustry.world.Worlds;
import heavyindustry.world.consumers.ConsumeRecipe;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.core.UI;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Iconc;
import mindustry.gen.Tex;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.io.TypeIO;
import mindustry.type.ItemStack;
import mindustry.type.PayloadSeq;
import mindustry.type.PayloadStack;
import mindustry.type.UnitType;
import mindustry.ui.Bar;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.units.UnitAssembler;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;
import mindustry.world.modules.ItemModule;

import static heavyindustry.HVars.MOD_NAME;
import static mindustry.Vars.net;
import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;

public class JumpGate extends Block {
	public Seq<UnitRecipe> recipeList = new Seq<>(UnitRecipe.class);
	public float warmupPerSpawn = 0.2f;
	public float maxWarmupSpeed = 3f;

	public float maxRadius = 180f;

	public int maxSpawnCount = 16;

	public TextureRegion arrowRegion, pointerRegion;

	public JumpGate(String name) {
		super(name);

		solid = true;
		sync = true;
		breakable = true;
		update = true;
		commandable = true;
		configurable = true;
		saveConfig = true;
		canOverdrive = false;
		logicConfigurable = true;
		clearOnDoubleTap = true;
		allowConfigInventory = false;
		unloadable = false;

		config(Integer.class, JumpGateBuild::changePlan);
		config(Float.class, JumpGateBuild::changeSpawnCount);
		configClear((JumpGateBuild e) -> e.recipeIndex = -1);

		consumeBuilder.add(new ConsumeRecipe(JumpGateBuild::recipe));
		consumeBuilder.each(c -> c.multiplier = b -> b instanceof JumpGateBuild gate ? gate.costMultiplier() : 1f);
	}

	public void addUnitRecipe(UnitType unitType, float craftTime, Recipe recipe) {
		UnitRecipe unitRecipe = new UnitRecipe();
		unitRecipe.unitType = unitType;
		unitRecipe.craftTime = craftTime;
		unitRecipe.recipe = recipe;
		recipeList.add(unitRecipe);
	}

	@Override
	public void load() {
		super.load();

		arrowRegion = Core.atlas.find(MOD_NAME + "-jump-gate-arrow");
		pointerRegion = Core.atlas.find(MOD_NAME + "-jump-gate-pointer");
	}

	@Override
	public void init() {
		super.init();
		clipSize = maxRadius;
	}

	@Override
	public void setBars() {
		super.setBars();

		addBar("progress", (JumpGateBuild tile) -> new Bar("bar.progress", Pal.ammo, tile::progress));
		addBar("efficiency", (JumpGateBuild tile) -> new Bar(() -> Core.bundle.format("bar.efficiency", Strings.autoFixed(tile.speedMultiplier * 100f, 0)), () -> Pal.techBlue, () -> tile.speedMultiplier / maxWarmupSpeed));
		addBar("units", (JumpGateBuild tile) -> new Bar(
				() -> tile.unitType() == null ? "[lightgray]" + Iconc.cancel :
						Core.bundle.format("bar.unitcap",
								Fonts.getUnicodeStr(tile.unitType().name),
								tile.team.data().countType(tile.unitType()),
								tile.unitType() == null ? Units.getStringCap(tile.team) : (tile.unitType().useUnitCap ? Units.getStringCap(tile.team) : "-")
						),
				() -> Pal.power,
				() -> tile.unitType() == null ? 0f : (tile.unitType().useUnitCap ? (float) tile.team.data().countType(tile.unitType()) / Units.getCap(tile.team) : 1f)
		));
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.add(Stat.output, table -> {
			table.row();

			for (UnitRecipe unitPlan : recipeList) {
				Recipe recipe = unitPlan.recipe;
				UnitType plan = unitPlan.unitType;
				table.table(Styles.grayPanel, t -> {

					if (plan.isBanned()) {
						t.image(Icon.cancel).color(Pal.remove).size(40);
						return;
					}

					if (plan.unlockedNow()) {
						t.image(plan.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit).with(i -> StatValues.withTooltip(i, plan));
						t.table(info -> {
							info.add(plan.localizedName).left();
							info.row();
							info.add(Strings.autoFixed(unitPlan.craftTime / 60f, 1) + " " + Core.bundle.get("unit.seconds")).color(Color.lightGray);
						}).left();

						t.table(req -> {
							req.right();
							int i = 0;
							for (ItemStack stack : recipe.inputItem) {
								if (++i % 6 == 0) req.row();
								req.add(StatValues.stack(stack.item, stack.amount, true)).pad(5);
							}
							for (PayloadStack stack : recipe.inputPayload) {
								if (++i % 6 == 0) req.row();
								req.add(StatValues.stack(stack.item, stack.amount, true)).pad(5);
							}
						}).right().grow().pad(10f);
					} else {
						t.image(Icon.lock).color(Pal.darkerGray).size(40);
					}
				}).growX().pad(5);
				table.row();
			}
		});
	}

	public static class UnitRecipe {
		public UnitType unitType = UnitTypes.alpha;
		public float craftTime = 10 * 60f;
		public Recipe recipe = Recipe.empty;
	}

	public Stack getReqStack(UnlockableContent content, Prov<CharSequence> display, Boolp valid) {
		return new Stack(
				new Table(o -> o.left().add(new Image(content.fullIcon)).size(32f).scaling(Scaling.fit)),
				new Table(t -> {
					t.left().bottom();
					t.label(() -> (valid.get() ? "[accent]" : "[negstat]") + display.get()).style(Styles.outlineLabel);
					t.pack();
				})
		);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = JumpGateBuild::new;
	}

	public class JumpGateBuild extends Building {
		public float speedMultiplier = 1f;
		public float progress;
		public float warmup;
		public float spawnWarmup;
		public int recipeIndex;
		public int spawnCount = 1;
		public @Nullable Vec2 command = new Vec2(Float.NaN, Float.NaN);

		public ItemModule tmpItem = new ItemModule();
		public Seq<Tile> tiles = new Seq<>(Tile.class);

		@Override
		public Vec2 getCommandPosition() {
			return command;
		}

		@Override
		public void onCommand(Vec2 target) {
			command.set(target);
		}

		@Override
		public PayloadSeq getPayloads() {
			return Worlds.teamPayloadData.getPayload(team);
		}

		@Override
		public void handlePayload(Building source, Payload payload) {
			getPayloads().add(payload.content(), 1);
			Fx.payloadDeposit.at(payload.x(), payload.y(), payload.angleTo(this), new UnitAssembler.YeetData(new Vec2(x, y), payload.content()));
		}

		public UnitRecipe unitRecipe() {
			if (recipeIndex < 0 || recipeIndex > recipeList.size - 1) return null;
			return recipeList.get(recipeIndex);
		}

		public UnitType unitType() {
			if (recipeIndex < 0 || recipeIndex > recipeList.size - 1) return null;
			return recipeList.get(recipeIndex).unitType;
		}

		public Recipe recipe() {
			if (unitRecipe() == null) return Recipe.empty;
			return unitRecipe().recipe;
		}

		public float craftTime() {
			if (recipeIndex < 0 || recipeIndex > recipeList.size - 1) return 0f;
			return recipeList.get(recipeIndex).craftTime;
		}

		public float costMultiplier() {
			return state.rules.teams.get(team).unitCostMultiplier * spawnCount;
		}

		public boolean canSpawn() {
			return unitRecipe() != null;
		}

		@Override
		public void drawSelect() {
			super.drawSelect();
			Drawf.dashCircle(x, y, maxRadius, team.color);

			if (unitType() != null) {
				drawItemSelection(unitType());
			}

			if (Float.isNaN(command.x) || Float.isNaN(command.y)) return;
			Lines.stroke(3f, Pal.gray);
			Lines.square(command.x, command.y, 8f, 45f);
			Lines.stroke(1f, team.color);
			Lines.square(command.x, command.y, 8f, 45f);
			Draw.reset();
		}

		@Override
		public void drawConfigure() {
			drawPlaceText(unitType() == null ? "@empty" : unitType().localizedName + " x" + spawnCount, tileX(), tileY(), true);
		}

		public void changePlan(int idx) {
			if (idx == -1) return;
			idx = Mathf.clamp(idx, 0, recipeList.size - 1);
			if (idx == recipeIndex) return;
			progress = 0f;
			recipeIndex = idx;
			speedMultiplier = 1f;
		}

		public void changeSpawnCount(float count) {
			spawnCount = Mathf.round(Mathf.clamp(count, 1, maxSpawnCount));
			progress = 0f;
			speedMultiplier = 1f;
		}

		public void findTiles() {
			tiles = HEntity.ableToSpawn(unitType(), x, y, maxRadius);
		}

		public void spawnUnit() {
			if (unitRecipe() == null) return;
			if (unitType() == null) return;

			if (!net.client()) {
				float rot = core() == null ? Angles.angle(x, y, command.x, command.y) : Angles.angle(core().x, core().y, x, y);
				Spawner spawner = new Spawner();
				Tile t = tiles.random();
				Tmp.v1.set(t.worldx(), t.worldy());
				spawner.init(unitType(), team, Tmp.v1, rot, Mathf.clamp(unitRecipe().craftTime / maxWarmupSpeed, 5f * 60, 15f * 60));
				if (command != null) spawner.commandPos.set(command.cpy());
				spawner.add();
			}

			speedMultiplier = Mathf.clamp(speedMultiplier + warmupPerSpawn, 1, maxWarmupSpeed);
		}

		@Override
		public void updateTile() {
			super.updateTile();
			warmup = Mathf.lerp(warmup, efficiency, 0.01f);
			spawnWarmup = Mathf.lerp(spawnWarmup, efficiency, 0.01f);
			items = closestCore() == null ? tmpItem : closestCore().items;
			if (unitRecipe() == null || unitType() == null) {
				progress = 0f;
				return;
			}
			if (canSpawn() && Units.canCreate(team, unitType())) {
				progress += getProgressIncrease(craftTime() * Mathf.sqrt(spawnCount));
			}
			if (progress >= 1) {
				findTiles();
				for (int i = 0; i < spawnCount; i++) {
					spawnUnit();
				}
				consume();
				progress = 0f;
			}
		}

		@Override
		public float getProgressIncrease(float baseTime) {
			return super.getProgressIncrease(baseTime) * speedMultiplier;
		}

		public boolean canConsume() {
			return !(unitRecipe() == null || unitType() == null) && canSpawn() && Units.canCreate(team, unitType());
		}

		@Override
		public void buildConfiguration(Table table) {
			table.table(inner -> {
				inner.background(Tex.paneSolid);
				inner.slider(1, maxSpawnCount, 1, 1, this::configure).growX().row();
				inner.image().size(320, 4).color(Pal.accent).padTop(12f).padBottom(8f).growX().row();
				inner.pane(selectionTable -> {
					for (int i = 0; i < recipeList.size; i++) {
						int l = i;
						UnitRecipe unitRecipe = recipeList.get(i);
						UnitType type = unitRecipe.unitType;
						selectionTable.button(button -> {
							button.table(selection -> selection.stack(
									new DelaySlideTable(
											() -> Pal.techBlue,
											() -> "          " + Core.bundle.format("bar.unitcap",
													type.localizedName,
													team.data().countType(type),
													type.useUnitCap ? Units.getStringCap(team) : "-"),
											() -> type.useUnitCap ? (float) team.data().countType(type) / Units.getCap(team) : 1f),
									new Table(image -> image.image(type.uiIcon).scaling(Scaling.fit).size(48, 48).padTop(6f).padBottom(6f).padLeft(8f)).left(),
									new Table(req -> {
										req.right();
										int j = 0;
										for (ItemStack stack : unitRecipe.recipe.inputItem) {
											if (++j % 3 == 0) req.row();
											req.add(getReqStack(stack.item, () -> Strings.format("@/@", UI.formatAmount((long) stack.amount * spawnCount), UI.formatAmount(items.get(stack.item))),
													() -> items.has(stack.item, stack.amount * spawnCount))).pad(5);
										}
										req.row();
										int k = 0;
										for (PayloadStack stack : unitRecipe.recipe.inputPayload) {
											if (++k % 4 == 0) req.row();
											req.add(getReqStack(stack.item, () -> Strings.format("@/@", UI.formatAmount((long) stack.amount * spawnCount), UI.formatAmount(getPayloads().get(stack.item))),
													() -> getPayloads().get(stack.item) >= stack.amount * spawnCount)).pad(5);
										}
									}).marginLeft(60).marginTop(36f).marginBottom(4f).left()
							).expandX().fillX()).growX();
							button.update(() -> {
								if (unitRecipe() == null) {
									button.setChecked(false);
								} else {
									button.setChecked(unitRecipe == unitRecipe());
								}
							});
						}, Styles.underlineb, () -> configure(l)).expandX().fillX().margin(0).pad(4);
						selectionTable.row();
					}
				}).scrollX(false).width(342).maxHeight(400).padRight(2).row();
			}).width(360);
		}

		@Override
		public Object config() {
			return unitType();
		}

		@Override
		public float progress() {
			return progress;
		}

		@Override
		public float warmup() {
			return warmup;
		}

		@Override
		public void draw() {
			super.draw();

			Draw.z(Layer.bullet);

			float scl = warmup() * 0.125f;
			float rot = totalProgress();

			Draw.color(team.color);
			Lines.stroke(8f * scl);
			Lines.square(x, y, size * tilesize / 2.5f, -rot);
			Lines.square(x, y, size * tilesize / 2f, rot);
			for (int i = 0; i < 4; i++) {
				float length = tilesize * size / 2f + 8f;
				float rotation = i * 90;
				float sin = Mathf.absin(totalProgress(), 16f, tilesize);
				float signSize = 0.75f + Mathf.absin(totalProgress() + 8f, 8f, 0.15f);

				Tmp.v1.trns(rotation + rot, -length);
				Draw.rect(arrowRegion, x + Tmp.v1.x, y + Tmp.v1.y, arrowRegion.width * scl, arrowRegion.height * scl, rotation + 90 + rot);
				length = tilesize * size / 2f + 3 + sin;
				Tmp.v1.trns(rotation, -length);
				Draw.rect(pointerRegion, x + Tmp.v1.x, y + Tmp.v1.y, pointerRegion.width * signSize * scl, pointerRegion.height * signSize * scl, rotation + 90);
			}
			Draw.color();
		}

		@Override
		public byte version() {
			return 1;
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.f(speedMultiplier);
			write.f(progress);
			write.i(recipeIndex);
			TypeIO.writeVec2(write, command);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			if (revision == 1) {
				speedMultiplier = read.f();
				progress = read.f();
				recipeIndex = read.i();
				command = TypeIO.readVec2(read);
			}
		}
	}
}
