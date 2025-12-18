package heavyindustry.world.meta;

import arc.Core;
import arc.func.Boolf;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.Image;
import arc.scene.ui.Label;
import arc.scene.ui.TextButton;
import arc.scene.ui.layout.Cell;
import arc.scene.ui.layout.Collapser;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectFloatMap;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Scaling;
import arc.util.Strings;
import heavyindustry.ui.Elements;
import heavyindustry.ui.ItemDisplay;
import heavyindustry.world.blocks.production.FuelCrafter;
import mindustry.Vars;
import mindustry.content.StatusEffects;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.maps.Map;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.StatusEffect;
import mindustry.type.UnitType;
import mindustry.type.Weapon;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValue;
import mindustry.world.meta.StatValues;
import org.jetbrains.annotations.ApiStatus.Obsolete;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;

public final class HStatValues {
	/** Don't let anyone instantiate this class. */
	private HStatValues() {}

	public static <T extends UnlockableContent> StatValue ammo(ObjectMap<T, BulletType[]> map, boolean all) {
		return ammo(map, 0, false, all);
	}

	public static <T extends UnlockableContent> StatValue ammo(ObjectMap<T, BulletType[]> map, boolean showUnit, boolean all) {
		return ammo(map, 0, showUnit, all);
	}

	public static <T extends UnlockableContent> StatValue ammo(ObjectMap<T, BulletType[]> map, int indent, boolean showUnit, boolean all) {
		return table -> {

			table.row();

			Seq<T> orderedKeys = map.keys().toSeq();
			orderedKeys.sort();

			for (T t : orderedKeys) {
				boolean compact = t instanceof UnitType && !showUnit || indent > 0;
				if (!compact && !(t instanceof Turret)) {
					table.table(item -> {
						item.image(t.uiIcon).size(3 * 8).padRight(4).left().top().with(i -> StatValues.withTooltip(i, t, false));
						item.add(t.localizedName).padRight(10).left().top();
					}).left().pad(10);
				}
				table.row();
				table.table(tip -> {
					if (all) tip.add(Core.bundle.get("stat.eu-multi-all")).left();
					else tip.add(Core.bundle.get("stat.eu-multi-flow")).left();
				}).left().padBottom(5);
				table.row();
				for (BulletType type : map.get(t)) {
					if (type.spawnUnit != null && type.spawnUnit.weapons.size > 0) {
						ammo(ObjectMap.of(t, type.spawnUnit.weapons.first().bullet), indent, false, all).display(table);
						return;
					}

					//no point in displaying unit icon twice

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
							bt.row();
							bt.add(Core.bundle.format("bullet.buildingdamage", (int) (type.buildingDamageMultiplier * 100)));
						}

						if (type.rangeChange != 0 && !compact) {
							bt.row();
							bt.add(Core.bundle.format("bullet.range", (type.rangeChange > 0 ? "+" : "-") + Strings.autoFixed(type.rangeChange / Vars.tilesize, 1)));
						}

						if (type.splashDamage > 0) {
							bt.row();
							bt.add(Core.bundle.format("bullet.splashdamage", (int) type.splashDamage, Strings.fixed(type.splashDamageRadius / Vars.tilesize, 1)));
						}

						if (!compact && !Mathf.equal(type.ammoMultiplier, 1f) && type.displayAmmoMultiplier && (!(t instanceof Turret turret) || turret.displayAmmoMultiplier)) {
							bt.row();
							bt.add(Core.bundle.format("bullet.multiplier", (int) type.ammoMultiplier));
						}

						if (!compact && !Mathf.equal(type.reloadMultiplier, 1f)) {
							bt.row();
							bt.add(Core.bundle.format("bullet.reload", Strings.autoFixed(type.reloadMultiplier * 100, 2)));
						}

						if (type.knockback > 0) {
							bt.row();
							bt.add(Core.bundle.format("bullet.knockback", Strings.autoFixed(type.knockback, 2)));
						}

						if (type.healPercent > 0f) {
							bt.row();
							bt.add(Core.bundle.format("bullet.healpercent", Strings.autoFixed(type.healPercent, 2)));
						}

						if (type.healAmount > 0f) {
							bt.row();
							bt.add(Core.bundle.format("bullet.healamount", Strings.autoFixed(type.healAmount, 2)));
						}

						if (type.pierce || type.pierceCap != -1) {
							bt.row();
							bt.add(type.pierceCap == -1 ? "@bullet.infinitepierce" : Core.bundle.format("bullet.pierce", type.pierceCap));
						}

						if (type.incendAmount > 0) {
							bt.row();
							bt.add("@bullet.incendiary");
						}

						if (type.homingPower > 0.01f) {
							bt.row();
							bt.add("@bullet.homing");
						}

						if (type.lightning > 0) {
							bt.row();
							bt.add(Core.bundle.format("bullet.lightning", type.lightning, type.lightningDamage < 0 ? type.damage : type.lightningDamage));
						}

						if (type.pierceArmor) {
							bt.row();
							bt.add("@bullet.armorpierce");
						}

						if (type.status != StatusEffects.none) {
							bt.row();
							bt.add((type.status.minfo.mod == null ? type.status.emoji() : "") + "[stat]" + type.status.localizedName + "[lightgray] ~ [stat]" + ((int) (type.statusDuration / 60f)) + "[lightgray] " + Core.bundle.get("unit.seconds"));
						}

						if (type.intervalBullet != null) {
							bt.row();

							Table ic = new Table();
							StatValues.ammo(ObjectMap.of(t, type.intervalBullet), true, false).display(ic);
							Collapser coll = new Collapser(ic, true);
							coll.setDuration(0.1f);

							bt.table(it -> {
								it.left().defaults().left();

								it.add(Core.bundle.format("bullet.interval", Strings.autoFixed(type.intervalBullets / type.bulletInterval * 60, 2)));
								it.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
							});
							bt.row();
							bt.add(coll);
						}

						if (type.fragBullet != null) {
							bt.row();

							Table fc = new Table();
							StatValues.ammo(ObjectMap.of(t, type.fragBullet), true, false).display(fc);
							Collapser coll = new Collapser(fc, true);
							coll.setDuration(0.1f);

							bt.table(ft -> {
								ft.left().defaults().left();

								ft.add(Core.bundle.format("bullet.frags", type.fragBullets));
								ft.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
							});
							bt.row();
							bt.add(coll);
						}
					}).padTop(compact ? 0 : -9).padLeft(indent * 8).left().get().background(compact ? null : Tex.underline);

					table.row();
				}
			}
		};
	}

	public static void ammo3(Table table, BulletType bullet) {
		table.left().defaults().padRight(3).left();

		if (bullet.damage > 0) {
			if (bullet.continuousDamage() > 0) {
				table.row();
				table.add(Core.bundle.format("bullet.damage", bullet.continuousDamage()) + StatUnit.perSecond.localized());
			} else {
				table.row();
				table.add(Core.bundle.format("bullet.damage", bullet.damage));
			}
		}

		/*if (bullet instanceof EmpBulletType emp) {
			table.row();
			table.add(Core.bundle.format("bullet.empDamage", emp.empDamage, emp.empRange > 0 ? "[lightgray]~ [accent]" + emp.empRange / Vars.tilesize + "[lightgray]" + StatUnit.blocks.localized() : ""));
		}

		if (bullet instanceof HeatBulletType heat) {
			table.row();
			table.table(t -> {
				t.left().defaults().padRight(3).left();
				t.image(OtherContents.meltdown.uiIcon).size(25).scaling(Scaling.fit);
				t.add(Core.bundle.format("infos.heatAmmo", Strings.autoFixed(heat.meltDownTime / 60, 1), Strings.autoFixed(heat.melDamageScl * 60, 1), heat.maxExDamage > 0 ? heat.maxExDamage : Math.max(heat.damage, heat.splashDamage)));
			});
		}*/

		if (bullet.buildingDamageMultiplier != 1) {
			table.row();
			table.add(Core.bundle.format("bullet.buildingdamage", (int) (bullet.buildingDamageMultiplier * 100)));
		}

		if (bullet.rangeChange != 0) {
			table.row();
			table.add(Core.bundle.format("bullet.range", (bullet.rangeChange > 0 ? "+" : "-") + Strings.autoFixed(bullet.rangeChange / Vars.tilesize, 1)));
		}

		if (bullet.splashDamage > 0) {
			table.row();
			table.add(Core.bundle.format("bullet.splashdamage", (int) bullet.splashDamage, Strings.fixed(bullet.splashDamageRadius / Vars.tilesize, 1)));
		}

		if (bullet.knockback > 0) {
			table.row();
			table.add(Core.bundle.format("bullet.knockback", Strings.autoFixed(bullet.knockback, 2)));
		}

		if (bullet.healPercent > 0f) {
			table.row();
			table.add(Core.bundle.format("bullet.healpercent", Strings.autoFixed(bullet.healPercent, 2)));
		}

		if (bullet.healAmount > 0f) {
			table.row();
			table.add(Core.bundle.format("bullet.healamount", Strings.autoFixed(bullet.healAmount, 2)));
		}

		if (bullet.pierce || bullet.pierceCap != -1) {
			table.row();
			table.add(bullet.pierceCap == -1 ? "@bullet.infinitepierce" : Core.bundle.format("bullet.pierce", bullet.pierceCap));
		}

		if (bullet.incendAmount > 0) {
			table.row();
			table.add("@bullet.incendiary");
		}

		if (bullet.homingPower > 0.01f) {
			table.row();
			table.add("@bullet.homing");
		}

		if (bullet.lightning > 0) {
			table.row();
			table.add(Core.bundle.format("bullet.lightning", bullet.lightning, bullet.lightningDamage < 0 ? bullet.damage : bullet.lightningDamage));
		}

		if (bullet.pierceArmor) {
			table.row();
			table.add("@bullet.armorpierce");
		}

		if (bullet.status != StatusEffects.none && bullet.status != null) {
			table.row();
			table.add((bullet.status.minfo.mod == null ? bullet.status.emoji() : "") + "[stat]" + bullet.status.localizedName + "[lightgray] ~ " +
					"[stat]" + Strings.autoFixed(bullet.statusDuration / 60f, 1) + "[lightgray] " + Core.bundle.get("unit.seconds"));
		}

		if (bullet.intervalBullet != null) {
			table.row();

			Table ic = new Table();
			ammo3(ic, bullet.intervalBullet);
			Collapser coll = new Collapser(ic, true);
			coll.setDuration(0.1f);

			table.table(it -> {
				it.left().defaults().left();

				it.add(Core.bundle.format("bullet.interval", Strings.autoFixed(bullet.intervalBullets / bullet.bulletInterval * 60, 2)));
				it.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
			});
			table.row();
			table.add(coll).padLeft(16);
		}

		if (bullet.fragBullet != null) {
			table.row();

			Table ic = new Table();
			ammo3(ic, bullet.fragBullet);
			Collapser coll = new Collapser(ic, true);
			coll.setDuration(0.1f);

			table.table(ft -> {
				ft.left().defaults().left();

				ft.add(Core.bundle.format("bullet.frags", bullet.fragBullets));
				ft.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
			});
			table.row();
			table.add(coll).padLeft(16);
		}

		table.row();
	}

	public static StatValue stringBoosters(float reload, float maxUsed, float multiplier, boolean baseReload, Boolf<Liquid> filter, String key) {
		return table -> {
			table.row();
			table.table(c -> {
				Seq<Liquid> liquids = Vars.content.liquids();
				for (int i = 0; i < liquids.size; i++) {
					Liquid liquid = liquids.get(i);
					if (!filter.get(liquid)) continue;

					c.image(liquid.uiIcon).size(3 * 8).scaling(Scaling.fit).padRight(4).right().top();
					c.add(liquid.localizedName).padRight(10).left().top();
					c.table(Tex.underline, bt -> {
						bt.left().defaults().padRight(3).left();

						float reloadRate = (baseReload ? 1f : 0f) + maxUsed * multiplier * liquid.heatCapacity;
						float standardReload = baseReload ? reload : reload / (maxUsed * multiplier * 0.4f);
						float result = standardReload / (reload / reloadRate);
						bt.add(Core.bundle.format(key, Strings.autoFixed(result, 2)));
					}).left().padTop(-9);
					c.row();
				}
			}).colspan(table.getColumns());
			table.row();
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
			}).padTop(-9f).left().get().background(Tex.underline);
		};
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

	public static <T extends UnlockableContent> StatValue ammoString(ObjectMap<T, BulletType> map, String add) {
		return table -> {
			for (T i : map.keys()) {
				table.row();
				table.table(c -> {
					c.image(i.uiIcon).size(32).scaling(Scaling.fit).padRight(4).left().top();
					c.add(Core.bundle.get("stat-" + add + "-" + i.name + ".ammo")).padRight(10).left().top();
					c.background(Tex.underline);
				}).left();
				table.row();
			}
		};
	}

	public static StatValue ammoString(Item i, String add) {
		return table -> {
			table.row();
			table.table(c -> {
				c.image(i.uiIcon).size(32).scaling(Scaling.fit).padRight(4).left().top();
				c.add(Core.bundle.get("stat-" + add + "-" + i.name + ".ammo")).padRight(10).left().top();
				c.background(Tex.underline);
			}).left();
			table.row();
		};
	}

	public static StatValue itemRangeBoosters(String unit, float timePeriod, StatusEffect[] status, float rangeBoost, ItemStack[] items, boolean replace, Boolf<Item> filter) {
		return table -> {
			table.row();
			table.table(c -> {
				Seq<Item> seq = Vars.content.items();
				for (int i = 0; i < seq.size; i++) {
					Item item = seq.get(i);
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
									bt.add(Elements.selfStyleImageButton(new TextureRegionDrawable(s.uiIcon), Styles.emptyi, () -> Vars.ui.content.show(s))).padTop(2f).padBottom(6f).size(42);
									//bt.button(new TextureRegionDrawable(s.uiIcon), () -> Vars.ui.content.show(s)).padTop(2f).padBottom(6f).size(50);
									bt.add(s.localizedName).padLeft(5);
								}
								if (replace) {
									bt.row();
									bt.add(Core.bundle.get("statValue.replace"));
								}
							}
							bt.row();
							if (rangeBoost != 0)
								bt.add("[lightgray]+[stat]" + Strings.autoFixed(rangeBoost / Vars.tilesize, 2) + "[lightgray] " + StatUnit.blocks.localized()).row();
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

	public static StatValue ability(String name, int abs) {
		return table -> {
			table.row();
			for (int i = 0; i < abs; i++) {
				int j = i;
				table.table(c -> {
					c.add(
							Core.bundle.format("stat-" + "abi", j + 1) + " " +
									Core.bundle.get("stat-" + name + ".abi-" + j + ".name")
					).padRight(10).left().top();
				}).left();
				table.row();
				table.table(c -> {
					c.add(Core.bundle.get("stat-" + name + ".abi-" + j + ".description")).padRight(10).left().top();
					c.row();
					c.background(Tex.underline);
				}).left();
				table.row();
			}
		};
	}

	public static StatValue fuel(FuelCrafter crafter) {
		return table -> table.table(t -> {
			t.image(crafter.fuelItem.uiIcon).size(3 * 8).padRight(4).right().top();
			t.add(crafter.fuelItem.localizedName).padRight(10).left().top();

			t.table(ft -> {
				ft.clearChildren();
				ft.left().defaults().padRight(3).left();

				ft.add(Core.bundle.format("stat.fuel.input", crafter.fuelPerItem));

				ft.row();
				ft.add(Core.bundle.format("stat.fuel.use", crafter.fuelPerCraft));

				ft.row();
				ft.add(Core.bundle.format("stat.fuel.capacity", crafter.fuelCapacity));

				if (crafter.attribute != null) {
					ft.row();
					ft.table(at -> {
						Runnable[] rebuild = {null};
						Map[] lastMap = {null};

						rebuild[0] = () -> {
							at.clearChildren();
							at.left();

							at.add("@stat.fuel.affinity");

							if (Vars.state.isGame()) {
								Seq<Floor> blocks = Vars.content.blocks()
										.select(block -> block instanceof Floor f && Vars.indexer.isBlockPresent(block) && f.attributes.get(crafter.attribute) != 0 && !(f.isLiquid && !crafter.floating))
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
							Map current = Vars.state.isGame() ? Vars.state.map : null;

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

			ht.add(Core.bundle.format("stat.flare-health", health));
			ht.row();
			ht.add(Core.bundle.format("stat.flare-attraction", attraction));
			ht.row();
			ht.add(Core.bundle.format("stat.flare-lifetime", (int) (duration / 60f)));
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
				Seq<Block> blocks = Vars.content.blocks();
				for (int n = 0; n < blocks.size; i++) {
					Block block = blocks.get(n);
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

	public static <T extends UnlockableContent> StatValue ammo(ObjectMap<T, BulletType> map, int indent, boolean showUnit) {
		return table -> {
			table.row();

			Seq<T> orderedKeys = map.keys().toSeq();
			orderedKeys.sort();

			for (T t : orderedKeys) {
				boolean compact = t instanceof UnitType && !showUnit || indent > 0;

				BulletType type = map.get(t);

				if (type.spawnUnit != null && type.spawnUnit.weapons.size > 0) {
					ammo(ObjectMap.of(t, type.spawnUnit.weapons.first().bullet), indent, false).display(table);
					continue;
				}

				table.table(Styles.grayPanel, bt -> {
					bt.left().top().defaults().padRight(3).left();
					//no point in displaying unit icon twice
					if (!compact && !(t instanceof Turret)) {
						bt.table(title -> {
							title.image(t.uiIcon).size(3 * 8).padRight(4).right().scaling(Scaling.fit).top();
							title.add(t.localizedName).padRight(10).left().top();
						});
						bt.row();
					}

					if (type.damage > 0 && (type.collides || type.splashDamage <= 0)) {
						if (type.continuousDamage() > 0) {
							bt.add(Core.bundle.format("bullet.damage", type.continuousDamage()) + StatUnit.perSecond.localized());
						} else {
							bt.add(Core.bundle.format("bullet.damage", type.damage));
						}
					}

					buildSharedBulletTypeStat(type, t, bt, compact);

					if (type.intervalBullet != null) {
						bt.row();

						Table ic = new Table();
						StatValues.ammo(ObjectMap.of(t, type.intervalBullet), true, false).display(ic);
						Collapser coll = new Collapser(ic, true);
						coll.setDuration(0.1f);

						bt.table(it -> {
							it.left().defaults().left();

							it.add(Core.bundle.format("bullet.interval", Strings.autoFixed(type.intervalBullets / type.bulletInterval * 60, 2)));
							it.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
						});
						bt.row();
						bt.add(coll);
					}

					if (type.fragBullet != null) {
						bt.row();

						Table fc = new Table();
						StatValues.ammo(ObjectMap.of(t, type.fragBullet), true, false).display(fc);
						Collapser coll = new Collapser(fc, true);
						coll.setDuration(0.1f);

						bt.table(ft -> {
							ft.left().defaults().left();

							ft.add(Core.bundle.format("bullet.frags", type.fragBullets));
							ft.button(Icon.downOpen, Styles.emptyi, () -> coll.toggle(false)).update(i -> i.getStyle().imageUp = (!coll.isCollapsed() ? Icon.upOpen : Icon.downOpen)).size(8).padLeft(16f).expandX();
						});
						bt.row();
						bt.add(coll);
					}

				}).padLeft(indent * 5).padTop(5).padBottom(compact ? 0 : 5).growX().margin(compact ? 0 : 10);
				table.row();
			}
		};
	}

	public static void buildSharedBulletTypeStat(BulletType type, UnlockableContent t, Table bt, boolean compact) {
		if (type.buildingDamageMultiplier != 1) {
			bt.row();
			bt.add(Core.bundle.format("bullet.buildingdamage", ammoStat((int) (type.buildingDamageMultiplier * 100 - 100))));
		}

		if (type.rangeChange != 0 && !compact) {
			bt.row();
			bt.add(Core.bundle.format("bullet.range", ammoStat(type.rangeChange / Vars.tilesize)));
		}

		if (type.splashDamage > 0) {
			bt.row();
			bt.add(Core.bundle.format("bullet.splashdamage", (int) type.splashDamage, Strings.fixed(type.splashDamageRadius / Vars.tilesize, 1)));
		}

		if (!compact && !Mathf.equal(type.ammoMultiplier, 1f) && type.displayAmmoMultiplier && (!(t instanceof Turret turret) || turret.displayAmmoMultiplier)) {
			bt.row();
			bt.add(Core.bundle.format("bullet.multiplier", (int) type.ammoMultiplier));
		}

		if (!compact && !Mathf.equal(type.reloadMultiplier, 1f)) {
			bt.row();
			bt.add(Core.bundle.format("bullet.reload", ammoStat((int) (type.reloadMultiplier * 100 - 100))));
		}

		if (type.knockback > 0) {
			bt.row();
			bt.add(Core.bundle.format("bullet.knockback", Strings.autoFixed(type.knockback, 2)));
		}

		if (type.healPercent > 0f) {
			bt.row();
			bt.add(Core.bundle.format("bullet.healpercent", Strings.autoFixed(type.healPercent, 2)));
		}

		if (type.healAmount > 0f) {
			bt.row();
			bt.add(Core.bundle.format("bullet.healamount", Strings.autoFixed(type.healAmount, 2)));
		}

		if (type.pierce || type.pierceCap != -1) {
			bt.row();
			bt.add(type.pierceCap == -1 ? "@bullet.infinitepierce" : Core.bundle.format("bullet.pierce", type.pierceCap));
		}

		if (type.incendAmount > 0) {
			bt.row();
			bt.add("@bullet.incendiary");
		}

		if (type.homingPower > 0.01f) {
			bt.row();
			bt.add("@bullet.homing");
		}

		if (type.lightning > 0) {
			bt.row();
			bt.add(Core.bundle.format("bullet.lightning", type.lightning, type.lightningDamage < 0 ? type.damage : type.lightningDamage));
		}

		if (type.pierceArmor) {
			bt.row();
			bt.add("@bullet.armorpierce");
		}

		if (type.maxDamageFraction > 0) {
			bt.row();
			bt.add(Core.bundle.format("bullet.maxdamagefraction", (int) (type.maxDamageFraction * 100)));
		}

		if (type.suppressionRange > 0) {
			bt.row();
			bt.add(Core.bundle.format("bullet.suppression", Strings.autoFixed(type.suppressionDuration / 60f, 2), Strings.fixed(type.suppressionRange / Vars.tilesize, 1)));
		}

		if (type.status != StatusEffects.none) {
			bt.row();
			bt.add((type.status.hasEmoji() ? type.status.emoji() : "") + "[stat]" + type.status.localizedName + (type.status.reactive ? "" : "[lightgray] ~ [stat]" +
					((int) (type.statusDuration / 60f)) + "[lightgray] " + Core.bundle.get("unit.seconds"))).with(c -> StatValues.withTooltip(c, type.status));
		}

		if (!type.targetMissiles) {
			bt.row();
			bt.add("@bullet.notargetsmissiles");
		}

		if (!type.targetBlocks) {
			bt.row();
			bt.add("@bullet.notargetsbuildings");
		}
	}

	public static StatValue boosters(float reload, float maxUsed, float multiplier, boolean baseReload, Boolf<Liquid> filter, boolean noReloadBoost) {
		return table -> {
			table.row();
			table.table(c -> {
				Seq<Liquid> liquids = Vars.content.liquids();
				for (int i = 0; i < liquids.size; i++) {
					Liquid liquid = liquids.get(i);
					if (!filter.get(liquid)) continue;

					c.table(Styles.grayPanel, b -> {
						b.image(liquid.uiIcon).size(40).pad(10f).left().scaling(Scaling.fit);
						b.table(info -> {
							info.add(liquid.localizedName).left().row();
							info.add(Strings.autoFixed(maxUsed * 60f, 2) + StatUnit.perSecond.localized()).left().color(Color.lightGray);
						});

						b.table(bt -> {

							bt.right().defaults().padRight(3).left();
							float reloadRate = (baseReload ? 1f : 0f) + maxUsed * multiplier * liquid.heatCapacity;
							float standardReload = baseReload ? reload : reload / (maxUsed * multiplier * 0.4f);
							float result = standardReload / (reload / reloadRate);
							if (!noReloadBoost)
								bt.add(Core.bundle.format("bullet.reload", Strings.autoFixed(result * 100, 2))).pad(5).right().row();
							bt.add(Core.bundle.format("stat.speed-up-turret-coolant", Strings.autoFixed((liquid.heatCapacity + 1) * 100, 2), Strings.autoFixed((1 / (liquid.heatCapacity + 1)) * 100, 0))).pad(5);
						}).right().grow().pad(10f).padRight(15f);
					}).growX().pad(5).row();
				}
			}).growX().colspan(table.getColumns());
			table.row();
		};
	}

	public static StatValue weapons(UnitType unit, Seq<Weapon> weapons) {
		return table -> {
			table.row();
			for (int i = 0; i < weapons.size; i++) {
				Weapon weapon = weapons.get(i);

				if (weapon.flipSprite || !weapon.hasStats(unit)) {
					//flipped weapons are not given stats
					continue;
				}

				TextureRegion region = !weapon.name.isEmpty() ? Core.atlas.find(weapon.name + "-preview", weapon.region) : null;

				table.table(Styles.grayPanel, w -> {
					w.left().top().defaults().padRight(3).left();
					if (region != null && region.found() && weapon.showStatSprite)
						w.image(region).size(60).scaling(Scaling.bounded).left().top();
					w.row();

					if (weapon.inaccuracy > 0) {
						w.row();
						w.add("[lightgray]" + Stat.inaccuracy.localized() + ": [white]" + (int) weapon.inaccuracy + " " + StatUnit.degrees.localized());
					}
					if (!weapon.alwaysContinuous && weapon.reload > 0) {
						w.row();
						w.add("[lightgray]" + Stat.reload.localized() + ": " + (weapon.mirror ? "2x " : "") + "[white]" + Strings.autoFixed(60f / weapon.reload * weapon.shoot.shots, 2) + " " + StatUnit.perSecond.localized());
					}

					ammo(ObjectMap.of(unit, weapon.bullet), 0, false).display(w);

				}).growX().pad(5).margin(10);
				table.row();
			}
		};
	}

	public static StatValue content(UnlockableContent content) {
		return table -> {
			table.row();
			table.table(t -> {
				t.image(content.uiIcon).size(3 * 8);
				t.add("[lightgray]" + content.localizedName).padLeft(6);
				infoButton(t, content, 4 * 8).padLeft(6);
			});
		};
	}

	//for AmmoListValue
	@Obsolete(since = "1.0.8")
	public static Cell<Label> sep(Table table, String text) {
		table.row();
		return table.add(text);
	}

	//for AmmoListValue
	@Deprecated
	@ScheduledForRemoval
	public static TextureRegion icon(UnlockableContent content) {
		return content.uiIcon;
	}

	//for AmmoListValue
	public static String ammoStat(float value) {
		return (value > 0 ? "[stat]+" : "[negstat]") + Strings.autoFixed(value, 1);
	}

	public static Cell<TextButton> infoButton(Table table, UnlockableContent content, float size) {
		return table.button("?", Styles.flatBordert, () -> Vars.ui.content.show(content)).size(size).left().name("contentinfo");
	}
}
