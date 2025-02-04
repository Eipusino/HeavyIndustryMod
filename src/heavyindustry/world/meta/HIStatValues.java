package heavyindustry.world.meta;

import arc.*;
import arc.func.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.scene.style.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;
import arc.util.*;
import heavyindustry.ui.*;
import heavyindustry.util.*;
import heavyindustry.world.blocks.production.*;
import mindustry.content.*;
import mindustry.ctype.*;
import mindustry.entities.bullet.*;
import mindustry.gen.*;
import mindustry.maps.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.defense.turrets.*;
import mindustry.world.blocks.environment.*;
import mindustry.world.meta.*;

import static mindustry.Vars.*;

public final class HIStatValues {
	/** Don't let anyone instantiate this class. */
	private HIStatValues() {}

	public static <T extends UnlockableContent> StatValue ammo(ObjectMap<T, BulletType[]> map) {
		return ammo(map, 0, false);
	}

	public static <T extends UnlockableContent> StatValue ammo(ObjectMap<T, BulletType[]> map, boolean showUnit) {
		return ammo(map, 0, showUnit);
	}

	public static <T extends UnlockableContent> StatValue ammo(ObjectMap<T, BulletType[]> map, int indent, boolean showUnit) {
		return table -> {
			table.row();

			Seq<T> orderedKeys = map.keys().toSeq();
			orderedKeys.sort();

			for (T t : orderedKeys) {
				boolean compact = t instanceof UnitType && !showUnit || indent > 0;
				if (!compact && !(t instanceof Turret)) {
					table.image(icon(t)).size(3 * 8).padRight(4).right().top();
					table.add(t.localizedName).padRight(10).left().top();
				}
				table.row();
				for (BulletType type : map.get(t)) {
					if (type.spawnUnit != null && type.spawnUnit.weapons.any()) {
						ammo(ObjectMap.of(t, type.spawnUnit.weapons.first().bullet), indent, false).display(table);
						return;
					}

					table.table(bt -> {
						bt.left().defaults().padRight(3).left();

						if (type.damage > 0 && (type.collides || type.splashDamage <= 0)) {
							if (type.continuousDamage() > 0) {
								bt.add(Core.bundle.format("bullet.damage", type.continuousDamage()) + StatUnit.perSecond.localized());
							} else {
								bt.add(Core.bundle.format("bullet.damage", type.damage));
							}
						}

						if (type.buildingDamageMultiplier != 1) {
							sep(bt, Core.bundle.format("bullet.buildingdamage", (int) (type.buildingDamageMultiplier * 100)));
						}

						if (type.rangeChange != 0 && !compact) {
							sep(bt, Core.bundle.format("bullet.range", (type.rangeChange > 0 ? "+" : "-") + Strings.autoFixed(type.rangeChange / tilesize, 1)));
						}

						if (type.splashDamage > 0) {
							sep(bt, Core.bundle.format("bullet.splashdamage", (int) type.splashDamage, Strings.fixed(type.splashDamageRadius / tilesize, 1)));
						}

						if (!compact && !Mathf.equal(type.ammoMultiplier, 1f) && type.displayAmmoMultiplier && (!(t instanceof Turret) || ((Turret) t).displayAmmoMultiplier)) {
							sep(bt, Core.bundle.format("bullet.multiplier", (int) type.ammoMultiplier));
						}

						if (!compact && !Mathf.equal(type.reloadMultiplier, 1f)) {
							sep(bt, Core.bundle.format("bullet.reload", Strings.autoFixed(type.reloadMultiplier, 2)));
						}

						if (type.knockback > 0) {
							sep(bt, Core.bundle.format("bullet.knockback", Strings.autoFixed(type.knockback, 2)));
						}

						if (type.healPercent > 0f) {
							sep(bt, Core.bundle.format("bullet.healpercent", Strings.autoFixed(type.healPercent, 2)));
						}

						if (type.healAmount > 0f) {
							sep(bt, Core.bundle.format("bullet.healamount", Strings.autoFixed(type.healAmount, 2)));
						}

						if (type.pierce || type.pierceCap != -1) {
							sep(bt, type.pierceCap == -1 ? "@bullet.infinitepierce" : Core.bundle.format("bullet.pierce", type.pierceCap));
						}

						if (type.incendAmount > 0) {
							sep(bt, "@bullet.incendiary");
						}

						if (type.homingPower > 0.01f) {
							sep(bt, "@bullet.homing");
						}

						if (type.lightning > 0) {
							sep(bt, Core.bundle.format("bullet.lightning", type.lightning, type.lightningDamage < 0 ? type.damage : type.lightningDamage));
						}

						if (type.pierceArmor) {
							sep(bt, "@bullet.armorpierce");
						}

						if (type.status != StatusEffects.none) {
							sep(bt, (type.status.minfo.mod == null ? type.status.emoji() : "") + "[stat]" + type.status.localizedName + "[lightgray] ~ [stat]" + ((int) (type.statusDuration / 60f)) + "[lightgray] " + Core.bundle.get("unit.seconds"));
						}

						if (type.fragBullet != null) {
							sep(bt, Core.bundle.format("bullet.frags", type.fragBullets));
							bt.row();

							StatValues.ammo(ObjectMap.of(t, type.fragBullet), indent + 1, false).display(bt);
						}
					}).padTop(compact ? 0 : -9).padLeft(indent * 8).left().get().background(compact ? null : Tex.underline);

					table.row();
				}
			}
		};
	}

	public static StatValue teslaZapping(float damage, float maxTargets, StatusEffect status) {
		return table -> {
			table.row();
			table.table(t -> {
				t.left().defaults().padRight(3).left();

				t.add(Core.bundle.format("bullet.lightning", maxTargets, damage));
				t.row();

				if (status != StatusEffects.none) {
					t.add((status.minfo.mod == null ? status.emoji() : "") + "[stat]" + status.localizedName);
				}
			}).padTop(-9).left().get().background(Tex.underline);
		};
	}

	private static void sep(Table table, String text) {
		table.row();
		table.add(text);
	}

	private static TextureRegion icon(UnlockableContent t) {
		return t.uiIcon;
	}

	public static StatValue colorString(Color color, CharSequence s) {
		return table -> {
			table.row();
			table.table(c -> {
				c.image(((TextureRegionDrawable) Tex.whiteui).tint(color)).size(32).scaling(Scaling.fit).padRight(4).left().top();
				c.add(s).padRight(10).left().top();
			}).left();
			table.row();
		};
	}

	public static <T extends UnlockableContent> StatValue ammoString(ObjectMap<T, BulletType> map) {
		return table -> {
			for (T i : map.keys()) {
				table.row();
				table.table(c -> {
					c.image(icon(i)).size(32).scaling(Scaling.fit).padRight(4).left().top();
					c.add(Core.bundle.get("stat-" + i.name + ".ammo")).padRight(10).left().top();
					c.background(Tex.underline);
				}).left();
				table.row();
			}
		};
	}

	public static StatValue itemRangeBoosters(String unit, float timePeriod, StatusEffect[] status, float rangeBoost, ItemStack[] items, boolean replace, Boolf<Item> filter) {
		return table -> {
			table.row();
			table.table(c -> {
				for (Item item : content.items()) {
					if (!filter.get(item)) continue;

					c.table(Styles.grayPanel, b -> {
						for (ItemStack stack : items) {
							if (timePeriod < 0) {
								b.add(new ItemDisplay(stack.item, stack.amount, true)).pad(20f).left();
							} else {
								b.add(new ItemDisplay(stack.item, stack.amount, timePeriod, true)).pad(20f).left();
							}
							if (items.length > 1) b.row();
						}

						b.table(bt -> {
							bt.left().defaults().left();
							if (status.length > 0) {
								for (StatusEffect s : status) {
									if (s == StatusEffects.none) continue;
									bt.row();
									bt.add(Utils.selfStyleImageButton(new TextureRegionDrawable(s.uiIcon), Styles.emptyi, () -> ui.content.show(s))).padTop(2f).padBottom(6f).size(42);
									bt.add(s.localizedName).padLeft(5);
								}
								if (replace) {
									bt.row();
									bt.add(Core.bundle.get("hi-stat-value-replace"));
								}
							}
							bt.row();
							if (rangeBoost != 0)
								bt.add("[lightgray]+[stat]" + Strings.autoFixed(rangeBoost / tilesize, 2) + "[lightgray] " + StatUnit.blocks.localized()).row();
						}).right().grow().pad(10f).padRight(15f);
					}).growX().pad(5).padBottom(-5).row();
				}
			}).growX().colspan(table.getColumns());
			table.row();
		};
	}

	public static StatValue fuelEfficiency(Floor floor, float multiplier) {
		return table -> table.stack(
				new Image(floor.uiIcon).setScaling(Scaling.fit),
				new Table(t -> t.top().right().add((multiplier < 0 ? "[accent]" : "[scarlet]+") + Strings.autoFixed(multiplier * 100, 2)).style(Styles.outlineLabel))
		);
	}

	public static StatValue fuel(FuelCrafter crafter) {
		return table -> table.table(t -> {
			t.image(icon(crafter.fuelItem)).size(3 * 8).padRight(4).right().top();
			t.add(crafter.fuelItem.localizedName).padRight(10).left().top();

			t.table(ft -> {
				ft.clearChildren();
				ft.left().defaults().padRight(3).left();

				ft.add(Core.bundle.format("stat.hi-fuel.input", crafter.fuelPerItem));

				sep(ft, Core.bundle.format("stat.hi-fuel.use", crafter.fuelPerCraft));

				sep(ft, Core.bundle.format("stat.hi-fuel.capacity", crafter.fuelCapacity));

				if (crafter.attribute != null) {
					ft.row();
					ft.table(at -> {
						Runnable[] rebuild = {null};
						Map[] lastMap = {null};

						rebuild[0] = () -> {
							at.clearChildren();
							at.left();

							at.add("@stat.hi-fuel.affinity");

							if (state.isGame()) {
								Seq<Floor> blocks = content.blocks()
										.select(block -> block instanceof Floor f && indexer.isBlockPresent(block) && f.attributes.get(crafter.attribute) != 0 && !(f.isLiquid && !crafter.floating))
										.<Floor>as().with(s -> s.sort(f -> f.attributes.get(crafter.attribute)));

								if (blocks.any()) {
									int i = 0;
									for (Floor block : blocks) {
										fuelEfficiency(block, block.attributes.get(crafter.attribute) * crafter.fuelUseReduction / -100f).display(at);
										if (++i % 5 == 0) {
											at.row();
										}
									}
								} else {
									at.add("@none.inmap");
								}
							} else {
								at.add("@stat.show-inmap");
							}
						};

						rebuild[0].run();

						//rebuild when map changes.
						at.update(() -> {
							Map current = state.isGame() ? state.map : null;

							if (current != lastMap[0]) {
								rebuild[0].run();
								lastMap[0] = current;
							}
						});
					});
				}
			}).left().get().background(Tex.underline);
		});
	}

	public static StatValue signalFlareHealth(float health, float attraction, float duration) {
		return table -> table.table(ht -> {
			ht.left().defaults().padRight(3).left();

			ht.add(Core.bundle.format("stat.hi-flare-health", health));
			ht.row();
			ht.add(Core.bundle.format("stat.hi-flare-attraction", attraction));
			ht.row();
			ht.add(Core.bundle.format("stat.hi-flare-lifetime", (int)(duration / 60f)));
		}).padTop(-9f).left().get().background(Tex.underline);
	}

	public static StatValue staticDamage(float damage, float reload, StatusEffect status) {
		return table -> table.table(t -> {
			t.left().defaults().padRight(3).left();

			t.add(Core.bundle.format("bullet.damage", damage * 60f / reload) + StatUnit.perSecond.localized());
			t.row();

			if (status != StatusEffects.none) {
				t.add((status.minfo.mod == null ? status.emoji() : "") + "[stat]" + status.localizedName);
				t.row();
			}
		}).padTop(-9).left().get().background(Tex.underline);
	}

	public static StatValue drillAblesStack(float drillTime, int outputAmount, ObjectFloatMap<Item> multipliers, Boolf<Block> filter) {
		return table -> {
			table.row();
			table.table(c -> {
				int i = 0;
				for (Block block : content.blocks()) {
					if (!filter.get(block)) continue;

					c.table(Styles.grayPanel, b -> {
						b.image(block.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit);
						b.table(info -> {
							info.left();
							info.add(block.localizedName).left().row();
							info.add(block.itemDrop.emoji()).left();
						}).grow();
						if (multipliers != null) {
							b.add(Strings.autoFixed(drillTime, 2) + StatUnit.perSecond.localized())
									.right().pad(10f).padRight(15f).color(Color.lightGray);
						}
					}).growX().pad(5);
					if (++i % 2 == 0) c.row();
				}
			}).growX().colspan(table.getColumns());
		};
	}
}
