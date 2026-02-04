package endfield.mod;

import arc.files.Fi;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.serialization.Json;
import arc.util.serialization.Jval;
import mindustry.Vars;
import mindustry.content.Planets;
import mindustry.mod.Mods.LoadedMod;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.type.Planet;
import mindustry.type.UnitType;
import mindustry.world.Block;

import java.util.Arrays;

import static endfield.Vars2.MOD_NAME;

/**
 * from mod {@code AdaptiveCoreDatabase}.
 *
 * @author guiY
 */
public final class AdaptiveCoreDatabase {
	private static final Json json = new Json();

	private AdaptiveCoreDatabase() {}

	public static void init() {
		if (Mods2.isEnabled("adaptivecoredatabase")) return;

		boolean load = false;

		Seq<LoadedMod> mods = Vars.mods.list();
		for (LoadedMod mod : mods) {
			try {
				if (mod == null || mod.meta == null || mod.name.equals(MOD_NAME) || !mod.enabled()) continue;

				String name = mod.meta.name;
				Fi metaFile = mod.root.child("adc.json");
				if (!metaFile.exists()) {
					Log.info("mod " + name + " has no adc.json, skip...");
					continue;
				}
				Log.info("mod " + name + " load adc.json...");
				Meta meta = json.fromJson(Meta.class, Jval.read(metaFile.readString()).toString(Jval.Jformat.plain));
				for (Root root : meta.root) {
					if (root == null) continue;

					if (root.planet == null) {
						Log.warn("where is you planet?");
						continue;
					}
					Planet planetVanilla = Vars.content.planet(root.planet);
					Planet planetMod = Vars.content.planet(name + '-' + root.planet);
					Planet planet = planetVanilla != null ? planetVanilla : planetMod;
					if (planet == null) {
						Log.warn("can not find planet '" + name + '-' + root.planet + "' or '" + root.planet + '\'');
						continue;
					}
					if (root.items != null) {
						int i = 0;
						for (String s : root.items) {
							Item itemVanilla = Vars.content.item(s);
							Item itemMod = Vars.content.item(name + '-' + s);
							Item item = itemVanilla != null ? itemVanilla : itemMod;
							if (item == null) {
								Log.warn("can not find item '" + name + '-' + s + "' or '" + s + '\'');
								continue;
							}
							i++;
							item.shownPlanets.add(planet);
							item.postInit();
							load = true;
						}
						Log.info("mod " + name + " adds " + i + " items to " + planet.localizedName);
					} else {
						Log.info("mod " + name + " adc.json has no items");
					}
					if (root.liquids != null) {
						int i = 0;
						for (String s : root.liquids) {
							Liquid liquidVanilla = Vars.content.liquid(s);
							Liquid liquidMod = Vars.content.liquid(name + '-' + s);
							Liquid liquid = liquidVanilla != null ? liquidVanilla : liquidMod;
							if (liquid == null) {
								Log.warn("can not find liquid '" + name + '-' + s + "' or '" + s + '\'');
								continue;
							}
							i++;
							liquid.shownPlanets.add(planet);
							liquid.postInit();
						}
						Log.info("mod " + name + " adds " + i + " liquids to " + planet.localizedName);
					} else {
						Log.info("mod " + name + " adc.json has no liquids");
					}
					if (root.units != null) {
						int i = 0;
						for (String s : root.units) {
							UnitType unitVanilla = Vars.content.unit(s);
							UnitType unitMod = Vars.content.unit(name + '-' + s);
							UnitType unit = unitVanilla != null ? unitVanilla : unitMod;
							if (unit == null) {
								Log.err("can not find unit '" + name + '-' + s + "' or '" + s + '\'');
								continue;
							}
							i++;
							unit.shownPlanets.add(planet);
							unit.postInit();
						}
						Log.info("mod " + name + " adds " + i + " units to " + planet.localizedName);
					} else {
						Log.info("mod " + name + " adc.json has no units");
					}
				}
			} catch (Throwable e) {
				Log.err(e);
			}
		}

		if (load) {
			Log.info("last progress...");

			Seq<Planet> expSun = Vars.content.planets().copy().removeAll(p -> p == Planets.sun);

			Seq<Block> blocks = Vars.content.blocks();
			for (int i = 0; i < blocks.size; i++) {
				Block block = blocks.get(i);
				if (block.requirements.length == 0) {
					block.shownPlanets.addAll(expSun);
				} else {
					block.shownPlanets.clear();
				}

				block.postInit();
			}
		}
	}

	public static class Meta {
		public Root[] root;

		@Override
		public String toString() {
			return "Meta{" +
					"root=" + Arrays.toString(root) +
					'}';
		}
	}

	public static class Root {
		public String planet;
		public String[] items;
		public String[] liquids;
		public String[] units;

		@Override
		public String toString() {
			return "Root{" +
					"planet='" + planet + '\'' +
					", items=" + Arrays.toString(items) +
					'}';
		}
	}
}
