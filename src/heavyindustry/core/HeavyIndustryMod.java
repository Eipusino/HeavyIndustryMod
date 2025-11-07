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
import arc.util.Nullable;
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
import heavyindustry.files.FileUtils;
import heavyindustry.game.HTeam;
import heavyindustry.gen.Entitys;
import heavyindustry.graphics.HCacheLayer;
import heavyindustry.graphics.HShaders;
import heavyindustry.graphics.HTextures;
import heavyindustry.graphics.MathRenderer;
import heavyindustry.graphics.ScreenSampler;
import heavyindustry.graphics.g2d.CutBatch;
import heavyindustry.graphics.g2d.DevastationBatch;
import heavyindustry.graphics.g2d.FragmentationBatch;
import heavyindustry.graphics.g2d.RangeExtractor;
import heavyindustry.graphics.g2d.VaporizeBatch;
import heavyindustry.input.InputAggregator;
import heavyindustry.mod.AdaptiveCoreDatabase;
import heavyindustry.mod.ModUtils;
import heavyindustry.mod.ScriptUtils;
import heavyindustry.net.HCall;
import heavyindustry.ui.Elements;
import heavyindustry.ui.HFonts;
import heavyindustry.ui.HIcon;
import heavyindustry.ui.HStyles;
import heavyindustry.util.CollectionList;
import heavyindustry.util.IconLoader;
import heavyindustry.util.PlatformImpl;
import heavyindustry.util.StringUtils;
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

	static final List<Throwable> errors = new CollectionList<>(Throwable.class);

	/**
	 * If true, all APIs within the mod will be imported into {@code Vars.mods.getScripts().scope} for testing
	 * some APIs on the console.
	 */
	static boolean test = true;

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
			if (Vars.headless || HVars.isPlugin || Core.settings.getBool("hi-closed-dialog")) return;

			FLabel label = new FLabel(Core.bundle.get("hi-author") + AUTHOR);
			BaseDialog dialog = new BaseDialog(Core.bundle.get("hi-name")) {{
				buttons.button(Core.bundle.get("close"), this::hide).size(210f, 64f);
				buttons.button(Core.bundle.get("hi-link-github"), () -> {
					if (!Core.app.openURI(LINK_GIT_HUB)) {
						Vars.ui.showErrorMessage("@linkfail");
						Core.app.setClipboardText(LINK_GIT_HUB);
					}
				}).size(210f, 64f);
				cont.pane(t -> {
					t.image(Core.atlas.find(MOD_NAME + "-cover")).left().size(600f, 403f).pad(3f).row();
					t.add(Core.bundle.get("hi-version")).left().growX().wrap().pad(4f).labelAlign(Align.left).row();
					t.add(label).left().row();
					t.add(Core.bundle.get("hi-type")).left().growX().wrap().pad(4f).labelAlign(Align.left).row();
					t.add(StringUtils.generateRandomString(10, 20)).left().growX().wrap().pad(4f).labelAlign(Align.left).row();
					t.add(StringUtils.generateRandomString(100, 200)).left().growX().wrap().width(550f).maxWidth(600f).pad(4f).labelAlign(Align.left).row();
					t.add(Core.bundle.get("hi-other-contributor")).left().growX().wrap().width(550f).maxWidth(600f).pad(4f).labelAlign(Align.left).row();
				}).grow().center().maxWidth(600f);
			}};
			dialog.show();
		});

		Events.on(FileTreeInitEvent.class, event -> {
			if (!Vars.headless) {
				HFonts.load();
				HSounds.load();

				Core.app.post(() -> {
					HTextures.load();
					HShaders.load();
					HCacheLayer.load();
					MathRenderer.load();
					RangeExtractor.load();

					HVars.fragBatch = new FragmentationBatch();
					HVars.cutBatch = new CutBatch();
					HVars.vaporBatch = new VaporizeBatch();
					HVars.devasBatch = new DevastationBatch();

					HVars.inputAggregator = new InputAggregator();
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
		if (test && !OS.isIos) {
			Core.app.post(ScriptUtils::init);
		}

		ScreenSampler.resetMark();

		HVars.listener = new HeavyIndustryListener();
	}

	@Override
	public void loadContent() {
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
		HeavyIndustryListener.updateInit();

		if (!HVars.isPlugin) {
			HUnitTypes.init();
		}

		if (!Vars.headless) {
			HIcon.load();

			HStyles.load();
			Elements.load();

			ScreenSampler.setup();
		}

		IconLoader.loadIcons(HVars.internalTree.resolves("other", "icons.properties"));

		if (Vars.ui != null) {
			if (Vars.ui.settings != null) {
				//add heavy-industry settings
				Vars.ui.settings.addCategory(Core.bundle.format("hi-settings"), HIcon.reactionIcon, table -> {
					table.checkPref("hi-closed-dialog", false);
					table.checkPref("hi-floating-text", true);
					table.checkPref("hi-animated-shields", true);
					table.checkPref("hi-tesla-range", true);
					table.sliderPref("hi-vaporize-batch", 300, 0, 1000, 1, s -> Strings.autoFixed(s, 2));
					table.pref(new Setting(Core.bundle.get("hi-game-data")) {
						@Override
						public void add(SettingsTable table) {
							table.button(name, Elements.gameDataDialog::show).margin(14).width(200f).pad(6);
							table.row();
						}
					});
					table.pref(new Setting(Core.bundle.get("hi-export-data")) {
						@Override
						public void add(SettingsTable table) {
							table.button(name, Worlds::exportBlockData).margin(14).width(200f).pad(6);
							table.row();
						}
					});
				});
			}

			if (!Vars.headless && !ModUtils.isEnabled("extra-utilities") && !ModUtils.isX() && Core.settings.getBool("hi-floating-text")) {
				String massage = Core.bundle.get("hi-random-massage");
				String[] massages = massage.split("&");

				floatingText = new FloatingText(massages[Mathf.random(massages.length - 1)]);
				floatingText.build(Vars.ui.menuGroup);
			}
		}

		AdaptiveCoreDatabase.init();
	}

	public static @Nullable Class<?> loadLibrary(String fileName, String mainClassName, boolean showError) {
		return loadLibrary(fileName, mainClassName, showError, c -> {});
	}

	public static @Nullable Class<?> loadLibrary(String fileName, String mainClassName, boolean showError, ConsT<Class<?>, Throwable> callback) {
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

			FileUtils.delete(toFile);

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

		loadLibrary("Impl", OS.isAndroid ? "heavyindustry.android.AndroidImpl" : "heavyindustry.desktop.DesktopImpl", true, clazz -> {
			Object instance = clazz.getConstructor().newInstance();

			if (instance instanceof PlatformImpl core) {
				HVars.platformImpl = core;
			}
		});
	}
}
