package heavyindustry.world.blocks.payload;

import arc.Core;
import arc.Events;
import arc.audio.Sound;
import arc.func.Cons;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Interp;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import arc.struct.IntIntMap;
import arc.struct.Queue;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Scaling;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.content.HFx;
import heavyindustry.ui.CraftGridImage;
import heavyindustry.util.IntMapf;
import heavyindustry.world.meta.HStat;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType.UnitCreateEvent;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Sounds;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.PayloadStack;
import mindustry.type.UnitType;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.payloads.UnitPayload;

public class PayloadManufacturingGrid extends PayloadBlock {
	public static final Queue<PayloadManufacturingGridBuild> gridQueue = new Queue<>(16, PayloadManufacturingGridBuild.class);

	public static long lastTime = 0;
	public static int pitchSeq = 0;

	public Seq<PayloadManufacturingRecipe> recipes = new Seq<>(PayloadManufacturingRecipe.class);
	public float craftTime = 60f;
	public float ingredientRadius = 4f * Vars.tilesize;
	public Interp mergeInterp = Interp.pow2In, sizeInterp = a -> 1f - Interp.pow2In.apply(a);
	public Effect mergeEffect = Fx.producesmoke, loadEffect = Fx.producesmoke,
			craftEffect = HFx.payloadManufacture, failEffect = HFx.payloadManufactureFail;
	public Sound craftSound = Sounds.place;

	public TextureRegion stackRegion, stackBottomRegion1, stackBottomRegion2;

	public PayloadManufacturingGrid(String name) {
		super(name);
		rotate = true;
		acceptsPayload = true;
		outputsPayload = true;

		ambientSound = Sounds.conveyor;
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		Draw.rect(region, plan.drawx(), plan.drawy());
		Draw.rect(outRegion, plan.drawx(), plan.drawy(), plan.rotation * 90f);
	}


	@Override
	public void load() {
		super.load();

		stackRegion = Core.atlas.find(name + "-stack");
		stackBottomRegion1 = Core.atlas.find(name + "-stack-bottom1");
		stackBottomRegion2 = Core.atlas.find(name + "-stack-bottom2");
	}

	public static float calcPitch() {
		if (Time.timeSinceMillis(lastTime) < 2000) {
			lastTime = Time.millis();
			pitchSeq++;
			if (pitchSeq > 30) {
				pitchSeq = 0;
			}
			return 1f + Mathf.clamp(pitchSeq / 30f) * 1.9f;
		} else {
			pitchSeq = 0;
			lastTime = Time.millis();
			return Mathf.random(0.7f, 1.3f);
		}
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{region, outRegion};
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.add(HStat.recipes, table -> {
			table.row();

			for (PayloadManufacturingRecipe recipe : recipes) {
				table.table(Styles.grayPanel, t -> {
					CraftGridImage image = new CraftGridImage();

					if (recipe.shapelessRequirements != null) {
						int total = 0;
						for (PayloadStack stack : recipe.shapelessRequirements) {
							total += stack.amount;
						}
						int size = Mathf.round(Mathf.sqrt(total));
						int addedX = 0, addedY = 0;
						// so terrible
						for (PayloadStack stack : recipe.shapelessRequirements) {
							for (int i = 0; i < stack.amount; i++) {
								image.items.put(Point2.pack(addedX, addedY), stack.item);
								addedX++;
								if (addedX >= size) {
									addedX = 0;
									addedY++;
								}
							}
						}
					} else {
						for (var entry : recipe.requirements) {
							image.items.put(entry.key, entry.value);
						}
					}

					t.table(requirements -> {
						t.add(image).size(image.itemsWidth(), image.itemsHeight()).pad(10f);
					}).left().grow().pad(10f);

					t.table(arrow -> {
						arrow.image(Icon.right).size(80f).color(Pal.darkishGray);
					}).grow().pad(10f);

					t.table(result -> {
						result.image(recipe.result.uiIcon).scaling(Scaling.fit).size(120f);
					}).right().grow().pad(10f);

					if (recipe.shapelessRequirements != null) {
						t.row();
						t.add("@recipe.shapeless").pad(10f).bottom().left().color(Pal.gray);
					}
				}).growX().pad(5f);
				table.row();
			}
		});
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = PayloadManufacturingGridBuild::new;
	}

	public class PayloadManufacturingGridBuild extends PayloadBlockBuild<Payload> {
		public boolean crafting, failed, moveOut, merge, dirty;
		public float progress;
		public IntIntMap ingredients = new IntIntMap();
		public Point2 offset = new Point2();
		public Vec2 center = new Vec2();
		// for checking whether or not to begin crafting
		public Seq<PayloadManufacturingGridBuild> chained = new Seq<>(PayloadManufacturingGridBuild.class);

		@Override
		public void updateTile() {
			super.updateTile();

			if (dirty) {
				Tmp.p1.set(0, 0);
				Tmp.p2.set(0, 0);
				for (var entry : ingredients) {
					int ix = Point2.x(entry.key), iy = Point2.y(entry.key);

					Tmp.p1.set(Math.min(ix, Tmp.p1.x), Math.min(iy, Tmp.p1.y));
					Tmp.p2.set(Math.max(ix, Tmp.p2.x), Math.max(iy, Tmp.p2.y));
				}
				offset.set(0, 0).sub(Tmp.p1);
				Tmp.p2.add(offset);
				center.set(Tmp.p2.x / 2f, Tmp.p2.y / 2f).scl(ingredientRadius);

				dirty = false;
			}

			if (payload != null) {
				if (moveOut) {
					moveOutPayload();
				} else {
					moveInPayload(crafting);

					if (crafting) {
						// wait for inputs to finish first.
						boolean canManufacture = true;
						int trns = block.size / 2 + 1;

						for (int i = 0; i < 4; i++) {
							if (i == rotation) continue;

							Building in = nearby(Geometry.d4x(i) * trns, Geometry.d4y(i) * trns);
							if (in instanceof PayloadManufacturingGridBuild grid && acceptGrid(grid, true)) {
								if (grid.crafting && !grid.ingredients.isEmpty()) {
									canManufacture = false;
									break;
								}
							}
						}

						if (canManufacture && !ingredients.isEmpty()) {
							progress += getProgressIncrease(craftTime);

							if (progress >= 1f) {
								progress = 0f;

								// merge ingredients
								if (front() instanceof PayloadManufacturingGridBuild next && merge) {
									Point2 add = Geometry.d4(next.relativeTo(this));
									for (var entry : ingredients) {
										int ix = Point2.x(entry.key) + add.x, iy = Point2.y(entry.key) + add.y;
										next.ingredients.put(Point2.pack(ix, iy), entry.value);
									}
									next.dirty = true;

									mergeEffect.at(Tmp.v1.trns(rotdeg(), block.size * Vars.tilesize - (ingredientRadius / 2f)).add(this));
									craftSound.at(this, calcPitch());
								} else {
									// match recipes
									Seq<PayloadManufacturingRecipe> possibleRecipes = recipes.select(r -> {
										if (!r.result.unlockedNow()) return false;

										if (r.result instanceof Block b && Vars.state.rules.isBanned(b)) {
											return false;
										}
										if (r.result instanceof UnitType unit && Vars.state.rules.isBanned(unit)) {
											return false;
										}

										if (r.shapelessRequirements != null) {
											int amount = 0;
											for (PayloadStack stack : r.shapelessRequirements) {
												amount += stack.amount;
											}
											return amount == ingredients.size;
										}

										return r.requirements.size == ingredients.size;
									});

									Seq<PayloadStack> accumulated = new Seq<>(PayloadStack.class);

									for (var entry : ingredients) {
										Building build = Vars.world.build(entry.value);

										if (build instanceof PayloadManufacturingGridBuild grid && grid.payload != null) {
											// accumulator thingy for shapeless recipes
											PayloadStack stack = accumulated.find(s -> s.item == grid.payload.content());
											if (stack != null) {
												stack.amount++;
											} else {
												accumulated.add(new PayloadStack(grid.payload.content(), 1));
											}

											possibleRecipes.each(recipe -> {
												if (recipe.shapelessRequirements != null) return;
												UnlockableContent content = grid.payload.content();
												int position = Point2.pack(Point2.x(entry.key) + offset.x, Point2.y(entry.key) + offset.y);

												if (recipe.requirements.get(position) != content) {
													possibleRecipes.remove(recipe);
												}
											});
										} else {
											// how the FUCK
											possibleRecipes.clear();
											break;
										}
									}

									// filter out the shapeless recipes
									for (PayloadManufacturingRecipe recipe : possibleRecipes) {
										if (recipe.shapelessRequirements != null && recipe.shapelessRequirements.length == accumulated.size) {
											for (PayloadStack stack : recipe.shapelessRequirements) {
												boolean found = false;
												// this is so ass?
												for (PayloadStack aStack : accumulated) {
													if (stack.item == aStack.item && stack.amount == aStack.amount) {
														found = true;
														break;
													}
												}
												if (found) continue;

												possibleRecipes.remove(recipe);
												break;
											}
										}
									}

									// successful recipe
									if (!possibleRecipes.isEmpty()) {
										PayloadManufacturingRecipe recipe = possibleRecipes.first();

										// clear all
										chained.each(b -> b.crafting, b -> {
											b.payload = null;
											b.crafting = false;
										});

										// create payload
										if (recipe.result instanceof UnitType unitResult) {
											Unit unit = unitResult.create(team);
											payload = new UnitPayload(unit);
											Events.fire(new UnitCreateEvent(unit, this));
										} else if (recipe.result instanceof Block blockResult) {
											payload = new BuildPayload(blockResult, team);
										}

										moveOut = true;
										craftEffect.at(x, y, payRotation - 90f, payload);
									} else {
										// failed
										failed = true;
										chained.each(b -> {
											b.crafting = false;
											if (b.payload != null) {
												failEffect.at(b.x, b.y, b.payRotation - 90f, b.payload);
											}
										});
									}
								}

								ingredients.clear();
								dirty = true;
							}
						}
					} else {
						payRotation = Angles.moveToward(payRotation, 90f, payloadRotateSpeed * delta());

						// start crafting once all grid slots are filled
						if (!chained.contains(b -> !b.canCraft())) {
							chained.each(PayloadManufacturingGridBuild::initCrafting);

							// terrible
							chained.each(grid -> {
								grid.merge = grid.front() instanceof PayloadManufacturingGridBuild next && next.acceptGrid(grid, true);
							});
						}
					}
				}
			} else {
				moveOut = false;
			}
		}

		public void initCrafting() {
			crafting = true;
			ingredients.put(0, pos());
			dirty = true;
		}

		public boolean canCraft() {
			return !failed && payload != null && hasArrived();
		}

		public boolean acceptGrid(PayloadManufacturingGridBuild other, boolean checkCrafting) {
			return (crafting || !checkCrafting) && other.block == block && other.team == team && (other.x == x || other.y == y) && other.relativeTo(this) == other.rotation;
		}

		@Override
		public boolean acceptPayload(Building source, Payload pay) {
			return payload == null && !moveOut;
		}

		@Override
		public void handlePayload(Building source, Payload payload) {
			super.handlePayload(source, payload);
			loadEffect.at(this);
		}

		public void drawIngredient(float x, float y, Payload payload, boolean bottom) {
			float z = Draw.z();

			if (bottom) {
				Draw.z(z - 0.02f);
				Draw.rect(rotation == 0 || rotation == 3 ? stackBottomRegion1 : stackBottomRegion2, x, y, rotdeg());
			}

			Draw.z(z - 0.03f);
			Drawf.shadow(x, y, ingredientRadius * 2f);
			Draw.z(z - 0.01f);
			Draw.rect(stackRegion, x, y);
			if (payload != null) {
				Draw.z(z - 0.001f);
				Drawf.shadow(x, y, payload.size() * 2f);
				Draw.z(z);
				Draw.rect(payload.icon(), x, y);
			}

			Draw.z(z);
		}

		public void drawIngredients(Vec2 offset, float scl) {
			Draw.scl(scl);
			offset.scl(scl);
			float z = Draw.z();

			Draw.z(z);
			for (var entry : ingredients) {
				Building build = Vars.world.build(entry.value);

				if (build instanceof PayloadManufacturingGridBuild grid && grid.payload != null) {
					float dx = x + offset.x + (Point2.x(entry.key) * ingredientRadius * scl),
							dy = y + offset.y + (Point2.y(entry.key) * ingredientRadius * scl);

					drawIngredient(dx, dy, grid.payload, entry.key == 0 && merge);
				}
			}
		}

		@Override
		public void draw() {
			Draw.rect(region, x, y);
			for (int i = 0; i < 4; i++) {
				if (blends(i) && i != rotation) {
					Draw.rect(inRegion, x, y, (i * 90f) - 180f);
				}
			}
			Draw.rect(outRegion, x, y, rotdeg());

			Draw.z(Layer.blockOver);
			if (crafting) {
				if (merge) {
					Tmp.v1.trns(rotdeg(), mergeInterp.apply(progress) * (block.size * Vars.tilesize - ingredientRadius));
					drawIngredients(Tmp.v1, 1f);
				} else {
					Tmp.v1.setZero().lerp(
							offset.x * ingredientRadius - center.x,
							offset.y * ingredientRadius - center.y,
							progress
					);
					drawIngredients(Tmp.v1, sizeInterp.apply(progress));
				}
			} else if (payload != null) {
				if (!moveOut) {
					drawIngredient(x, y, null, true);
				}

				payload.draw();
			}
		}

		@Override
		public boolean shouldAmbientSound() {
			return crafting && efficiency > 0f;
		}

		@Override
		public float ambientVolume() {
			return super.ambientVolume();
		}

		@Override
		public void onProximityAdded() {
			super.onProximityAdded();
			updateChained();
		}

		@Override
		public void onProximityRemoved() {
			super.onProximityRemoved();

			for (Building build : proximity) {
				if (build instanceof PayloadManufacturingGridBuild grid) {
					grid.updateChained();
				}
			}
		}

		public void updateChained() {
			chained.clear();
			gridQueue.clear();
			gridQueue.add(this);

			while (!gridQueue.isEmpty()) {
				PayloadManufacturingGridBuild next = gridQueue.removeLast();
				next.failed = false;
				chained.add(next);

				for (Building build : next.proximity) {
					if (build instanceof PayloadManufacturingGridBuild grid && (next.acceptGrid(grid, false) || grid.acceptGrid(next, false)) && grid.chained != chained) {
						grid.chained = chained;
						gridQueue.addFirst(grid);
					}
				}
			}
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.bool(crafting);
			write.bool(moveOut);
			write.bool(merge);
			write.f(progress);

			write.i(ingredients.size);

			for (var entry : ingredients) {
				write.i(entry.key);
				write.i(entry.value);
			}
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			crafting = read.bool();
			moveOut = read.bool();
			merge = read.bool();
			progress = read.f();

			int size = read.i();

			for (int i = 0; i < size; i++) {
				int key = read.i();
				int pos = read.i();
				ingredients.put(key, pos);
			}

			dirty = true;
		}
	}

	public static class PayloadManufacturingRecipe {
		public IntMapf<UnlockableContent> requirements = new IntMapf<>(UnlockableContent.class);
		// if not null then this recipe is shapeless
		public PayloadStack[] shapelessRequirements;
		public UnlockableContent result;

		public PayloadManufacturingRecipe(UnlockableContent res, Cons<PayloadManufacturingRecipe> run) {
			result = res;
			run.get(this);
		}

		public PayloadManufacturingRecipe(UnlockableContent res, PayloadStack[] requirements) {
			result = res;
			shapelessRequirements = requirements;
		}

		public void mapRequirements(UnlockableContent[][] array) {
			for (int y = 0; y < array.length; y++) {
				UnlockableContent[] row = array[array.length - 1 - y];

				for (int x = 0; x < row.length; x++) {
					requirements.put(Point2.pack(x, y), row[x]);
				}
			}
		}
	}
}
