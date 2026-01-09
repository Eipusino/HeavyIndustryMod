/*
	Copyright (c) Eipusino 2021
	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.
	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.
	You should have received a copy of the GNU General Public License
	along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package heavyindustry.core;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.flabel.FLabel;
import arc.func.ConsT;
import arc.math.Mathf;
import arc.util.Align;
import arc.util.Log;
import arc.util.OS;
import arc.util.Strings;
import arc.util.Time;
import heavyindustry.HVars;
import heavyindustry.audio.HMusics;
import heavyindustry.audio.HSounds;
import heavyindustry.content.HBlocks;
import heavyindustry.content.HBullets;
import heavyindustry.content.HItems;
import heavyindustry.content.HLiquids;
import heavyindustry.content.HLoadouts;
import heavyindustry.content.HOverrides;
import heavyindustry.content.HPlanets;
import heavyindustry.content.HSectorPresets;
import heavyindustry.content.HStatusEffects;
import heavyindustry.content.HTechTree;
import heavyindustry.content.HUnitCommands;
import heavyindustry.content.HUnitTypes;
import heavyindustry.content.HWeathers;
import heavyindustry.files.Files2;
import heavyindustry.game.HTeam;
import heavyindustry.gen.Entitys;
import heavyindustry.graphics.HCacheLayer;
import heavyindustry.graphics.HPixmaps;
import heavyindustry.graphics.HRegions;
import heavyindustry.graphics.HShaders;
import heavyindustry.graphics.HTextures;
import heavyindustry.graphics.FastSizedGraphics;
import heavyindustry.graphics.MathRenderer;
import heavyindustry.graphics.ScreenSampler;
import heavyindustry.graphics.SizedGraphics;
import heavyindustry.graphics.g2d.CutBatch;
import heavyindustry.graphics.g2d.DevastationBatch;
import heavyindustry.graphics.g2d.FragmentationBatch;
import heavyindustry.graphics.g2d.RangeExtractor;
import heavyindustry.graphics.g2d.VaporizeBatch;
import heavyindustry.mod.AdaptiveCoreDatabase;
import heavyindustry.mod.HMods;
import heavyindustry.mod.HScripts;
import heavyindustry.net.HCall;
import heavyindustry.ui.Elements;
import heavyindustry.ui.HFonts;
import heavyindustry.ui.HIcon;
import heavyindustry.ui.HStyles;
import heavyindustry.ui.HTex;
import heavyindustry.util.CollectionList;
import heavyindustry.util.IconLoader;
import heavyindustry.util.PlatformImpl;
import heavyindustry.util.Strings2;
import heavyindustry.world.Worlds;
import mindustry.Vars;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.game.EventType.DisposeEvent;
import mindustry.game.EventType.FileTreeInitEvent;
import mindustry.game.EventType.MusicRegisterEvent;
import mindustry.mod.Mod;
import mindustry.mod.ModClassLoader;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.Setting;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static heavyindustry.HVars.AUTHOR;
import static heavyindustry.HVars.LINK_GIT_HUB;
import static heavyindustry.HVars.MOD_NAME;

/**
 * Main entry point of the mod. Handles startup things like content loading, entity registering, and utility
 * bindings.
 * <p><strong>Until the issue with Mindustry MultiDex Mod is resolved, I will try to minimize the amount of
 * code in the mod as much as possible, which means it is unlikely to have too many built-in utilities.</strong>
 *
 * @author Eipusino
 * @see HVars
 */
public final class HeavyIndustryMod extends Mod {
	public static Mod instance;

	public static @Nullable ClassLoader lastLoader;
	public static @Nullable FloatingText floatingText;

	public static final List<Throwable> errors = new CollectionList<>(Throwable.class);

	static {
		loadLibrary();

		if (HVars.platformImpl == null) {
			// This situation usually does not occur...
			HVars.platformImpl = new DefaultImpl();
		}
	}

	public HeavyIndustryMod() {
		if (instance != null) return;

		instance = this;

		if (Core.graphics != null && !Core.graphics.isGL30Available()) {
			Log.warn("The current device does not support OpenGL 3.0 (on desktop) or OpenGL ES 3.0 (on android)!");
		}

		Log.info("Loaded HeavyIndustry Mod constructor.");

		HClassMap.load();

		Events.on(ClientLoadEvent.class, event -> {
			if (Vars.headless || HVars.isPlugin || Core.settings.getBool("closed-dialog")) return;

			FLabel label = new FLabel(Core.bundle.get("text.author") + AUTHOR);
			BaseDialog dialog = new BaseDialog(Core.bundle.get("text.name")) {{
				buttons.button(Core.bundle.get("close"), this::hide).size(210f, 64f);
				buttons.button(Core.bundle.get("text.link-github"), () -> {
					if (!Core.app.openURI(LINK_GIT_HUB)) {
						Vars.ui.showErrorMessage("@linkfail");
						Core.app.setClipboardText(LINK_GIT_HUB);
					}
				}).size(210f, 64f);
				cont.pane(t -> {
					t.image(Core.atlas.find(MOD_NAME + "-cover")).left().size(600f, 403f).pad(3f).row();
					t.add(Core.bundle.get("text.version")).left().growX().wrap().pad(4f).labelAlign(Align.left).row();
					t.add(label).left().row();
					t.add(Core.bundle.get("text.type")).left().growX().wrap().pad(4f).labelAlign(Align.left).row();
					t.add(Strings2.generateRandomString(10, 20)).left().growX().wrap().pad(4f).labelAlign(Align.left).row();
					t.add(Strings2.generateRandomString(100, 200)).left().growX().wrap().width(550f).maxWidth(600f).pad(4f).labelAlign(Align.left).row();
					t.add(Core.bundle.get("text.other-contributor")).left().growX().wrap().width(550f).maxWidth(600f).pad(4f).labelAlign(Align.left).row();
				}).grow().center().maxWidth(600f);
			}};
			dialog.show();
		});

		Events.on(FileTreeInitEvent.class, event -> {
			if (!Vars.headless) {
				HFonts.load();
				HSounds.load();

				Core.app.post(() -> {
					HPixmaps.load();
					HTextures.load();
					HRegions.load();
					HShaders.load();
					HCacheLayer.load();
					MathRenderer.load();
					RangeExtractor.load();

					HTex.init();

					HVars.sizedGraphics = OS.isAndroid || OS.isIos ? new SizedGraphics() : new FastSizedGraphics();

					HVars.fragBatch = new FragmentationBatch();
					HVars.cutBatch = new CutBatch();
					HVars.vaporBatch = new VaporizeBatch();
					HVars.devasBatch = new DevastationBatch();
				});
			}
		});

		Events.on(MusicRegisterEvent.class, event -> {
			if (!Vars.headless) {
				HMusics.load();
			}
		});

		Events.on(DisposeEvent.class, event -> {
			if (!Vars.headless) {
				HShaders.dispose();
			}
		});

		// To prevent damage to other mod, it can only be enabled during testing
		if (!OS.isIos) {
			Core.app.post(HScripts::init);
		}

		ScreenSampler.resetMark();

		HVars.listener = new HeavyIndustryListener();

		try {
			HTest.instance.test();
		} catch (Throwable e) {
			Log.err(e);
		}
	}

	@Override
	public void loadContent() {
		HRegions.addAll();

		if (HVars.isPlugin) return;

		HCall.init();

		Entitys.load();
		Worlds.load();

		HUnitCommands.loadAll();

		HTeam.load();
		HBullets.load();
		HItems.load();
		HStatusEffects.load();
		HLiquids.load();
		HUnitTypes.load();
		HBlocks.loadInternal();
		HBlocks.load();
		HWeathers.load();
		HOverrides.load();
		HPlanets.load();
		HSectorPresets.load();
		HTechTree.load();
		HLoadouts.load();
	}

	@Override
	public void init() {
		HVars.listener.updateInit();

		if (!HVars.isPlugin) {
			HUnitTypes.init();
		}

		if (!Vars.headless) {
			HIcon.load();

			HTex.load();
			HStyles.load();
			Elements.load();

			ScreenSampler.setup();
		}

		IconLoader.loadIcons(HVars.internalTree.children("other", "icons.properties"));

		if (Vars.ui != null) {
			if (Vars.ui.settings != null) {
				//add heavy-industry settings
				Vars.ui.settings.addCategory(Core.bundle.format("text.settings"), HIcon.reactionIcon, table -> {
					table.checkPref("closed-dialog", false);
					table.checkPref("floating-text", true);
					table.checkPref("animated-shields", true);
					table.checkPref("tesla-range", true);
					table.sliderPref("vaporize-batch", 300, 0, 1000, 1, s -> Strings.autoFixed(s, 2));
					table.pref(new Setting(Core.bundle.get("text.game-data")) {
						@Override
						public void add(SettingsTable table) {
							table.button(name, Elements.gameDataDialog::show).margin(14).width(200f).pad(6);
							table.row();
						}
					});
					table.pref(new Setting(Core.bundle.get("text.export-data")) {
						@Override
						public void add(SettingsTable table) {
							table.button(name, Worlds::exportBlockData).margin(14).width(200f).pad(6);
							table.row();
						}
					});
				});
			}

			if (!Vars.headless && !HMods.isEnabled("extra-utilities") && !HMods.isX() && Core.settings.getBool("floating-text")) {
				String massage = Core.bundle.get("text.random-massage");
				String[] massages = massage.split("@");

				floatingText = new FloatingText(massages[Mathf.random(massages.length - 1)]);
				floatingText.build(Vars.ui.menuGroup);
			}
		}

		AdaptiveCoreDatabase.init();
	}

	public static @Nullable Class<?> loadLibrary(String fileName, String mainClassName, boolean showError) {
		return loadLibrary(fileName, mainClassName, showError, c -> {});
	}

	public static @Nullable Class<?> loadLibrary(String fileName, String mainClassName, boolean showError, @Nullable ConsT<Class<?>, Throwable> callback) {
		ClassLoader mainLoader = Vars.mods.mainLoader();

		Fi sourceFile = HVars.internalTree.child("libs").child(fileName + ".jar");
		if (!sourceFile.exists()) {
			Log.warn("File: '@' not exists", "libs/" + fileName + ".jar");

			return null;
		}

		Log.info("Loading @.jar", fileName);

		Time.mark();

		try {
			Fi toFile = Vars.dataDirectory.child("tmp/heavy-industry/" + fileName + ".jar");

			Files2.delete(toFile);

			sourceFile.copyTo(toFile);
			ClassLoader loader = Vars.platform.loadJar(toFile, mainLoader);

			if (mainLoader instanceof ModClassLoader mod) mod.addChild(loader);

			Class<?> clazz = Class.forName(mainClassName, true, loader);
			lastLoader = loader;

			if (callback != null) callback.get(clazz);

			return clazz;
		} catch (Throwable e) {
			if (showError) {
				errors.add(e);

				Log.err(Strings.format("Unexpected exception when loading '@'", sourceFile), e);
			}

			return null;
		} finally {
			Log.info("Loaded '@' in @ms", sourceFile.name(), Time.elapsed());
		}
	}

	public static void addErr(Throwable t) {
		if (t != null) errors.add(t);
	}

	static void loadLibrary() {
		if (OS.isIos) return;

		String className = OS.isAndroid ? "heavyindustry.android.AndroidImpl" : "heavyindustry.desktop.DesktopImpl";

		loadLibrary("Impl", className, true, clazz -> HVars.platformImpl = (PlatformImpl) clazz.getConstructor().newInstance());
	}
}
