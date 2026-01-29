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
package endfield.core;

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
import endfield.Vars2;
import endfield.audio.Musics2;
import endfield.audio.Sounds2;
import endfield.content.Blocks2;
import endfield.content.Bullets2;
import endfield.content.Items2;
import endfield.content.Liquids2;
import endfield.content.Loadouts2;
import endfield.content.Overrides;
import endfield.content.Planets2;
import endfield.content.SectorPresets2;
import endfield.content.StatusEffects2;
import endfield.content.TechTrees;
import endfield.content.UnitCommands2;
import endfield.content.UnitTypes2;
import endfield.content.Weathers2;
import endfield.files.Files2;
import endfield.game.Team2;
import endfield.gen.Entitys;
import endfield.graphics.CacheLayer2;
import endfield.graphics.FastSizedGraphics;
import endfield.graphics.MathRenderer;
import endfield.graphics.Pixmaps2;
import endfield.graphics.Regions2;
import endfield.graphics.ScreenSampler;
import endfield.graphics.Shaders2;
import endfield.graphics.SizedGraphics;
import endfield.graphics.Textures2;
import endfield.graphics.g2d.CutBatch;
import endfield.graphics.g2d.DevastationBatch;
import endfield.graphics.g2d.FragmentationBatch;
import endfield.graphics.g2d.RangeExtractor;
import endfield.graphics.g2d.VaporizeBatch;
import endfield.mod.AdaptiveCoreDatabase;
import endfield.mod.Mods2;
import endfield.net.Call2;
import endfield.ui.Elements;
import endfield.ui.Fonts2;
import endfield.ui.Icon2;
import endfield.ui.Styles2;
import endfield.ui.Tex2;
import endfield.util.CollectionList;
import endfield.util.IconLoader;
import endfield.util.PlatformImpl;
import endfield.util.Strings2;
import endfield.util.script.Scripts2;
import endfield.world.Worlds;
import kotlin.KotlinVersion;
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

import static endfield.Vars2.AUTHOR;
import static endfield.Vars2.LINK_GIT_HUB;
import static endfield.Vars2.MOD_NAME;

/**
 * Main entry point of the mod. Handles startup things like content loading, entity registering, and utility
 * bindings.
 *
 * @author Eipusino
 * @see Vars2
 */
public final class EndFieldMod extends Mod {
	public static Mod instance;

	public static @Nullable ClassLoader lastLoader;
	public static @Nullable FloatingText floatingText;

	public static final List<Throwable> errors = new CollectionList<>(Throwable.class);

	static {
		loadLibrary();

		// This situation usually does not occur...
		if (Vars2.platformImpl == null) {
			Vars2.platformImpl = new DefaultImpl();
		}
	}

	public EndFieldMod() {
		instance = this;

		Log.info("Loaded ENDFIELD Mod constructor.");
		Log.infoTag("Kotlin", "Kotlin Version: " + KotlinVersion.CURRENT);

		ClassMap2.load();

		Events.on(ClientLoadEvent.class, event -> {
			if (Vars.headless || Vars2.isPlugin || Core.settings.getBool("closed-dialog")) return;

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
					t.image(Core.atlas.find(MOD_NAME + "-cover")).left().size(600f, 413f).pad(3f).row();
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
				Fonts2.load();
				Sounds2.load();

				Core.app.post(() -> {
					Pixmaps2.load();
					Textures2.load();
					Regions2.load();
					Shaders2.load();
					CacheLayer2.load();
					MathRenderer.load();
					RangeExtractor.load();

					Tex2.init();

					// If the platform allows, prioritize using high-performance MethodHandle
					Vars2.sizedGraphics = OS.isAndroid || OS.isIos ? new SizedGraphics() : new FastSizedGraphics();

					Vars2.fragBatch = new FragmentationBatch();
					Vars2.cutBatch = new CutBatch();
					Vars2.vaporBatch = new VaporizeBatch();
					Vars2.devasBatch = new DevastationBatch();
				});
			}
		});

		Events.on(MusicRegisterEvent.class, event -> {
			if (!Vars.headless) {
				Musics2.load();
			}
		});

		Events.on(DisposeEvent.class, event -> {
			if (!Vars.headless) {
				Shaders2.dispose();
			}
		});

		// To prevent damage to other mod, it can only be enabled during testing
		if (!OS.isIos) {
			Core.app.post(Scripts2::init);
		}

		ScreenSampler.resetMark();

		Vars2.listener = new EndFieldListener();
	}

	@Override
	public void loadContent() {
		Regions2.addAll();

		Call2.init();

		if (Vars2.isPlugin) return;

		Worlds.addAll();

		Entitys.load();
		Worlds.load();

		UnitCommands2.loadAll();

		Team2.load();
		Bullets2.load();
		Items2.load();
		StatusEffects2.load();
		Liquids2.load();
		UnitTypes2.load();
		Blocks2.loadInternal();
		Blocks2.load();
		Weathers2.load();
		Overrides.load();
		Planets2.load();
		SectorPresets2.load();
		TechTrees.load();
		Loadouts2.load();
	}

	@Override
	public void init() {
		Vars2.listener.updateInit();

		if (!Vars2.isPlugin) {
			UnitTypes2.init();
		}

		if (!Vars.headless) {
			Icon2.load();

			Tex2.load();
			Styles2.load();
			Elements.load();

			ScreenSampler.init();
			ScreenSampler.setup();
		}

		IconLoader.loadIcons(Vars2.internalTree.children("other", "icons.properties"));

		if (Vars.ui != null) {
			if (Vars.ui.settings != null) {
				//add endfield settings
				Vars.ui.settings.addCategory(Core.bundle.format("text.settings"), Icon2.reactionIcon, table -> {
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

			if (!Vars.headless && !Mods2.isEnabled("extra-utilities") && !Mods2.isX() && Core.settings.getBool("floating-text")) {
				String massage = Core.bundle.get("text.random-massage");
				String[] massages = massage.split("@");

				floatingText = new FloatingText(massages[Mathf.random(massages.length - 1)]);
				floatingText.build(Vars.ui.menuGroup);
			}
		}

		AdaptiveCoreDatabase.init();
	}

	private static @Nullable Class<?> loadLibrary(String fileName, String mainClassName, boolean showError) {
		return loadLibrary(fileName, mainClassName, showError, null);
	}

	private static @Nullable Class<?> loadLibrary(String fileName, String mainClassName, boolean showError, @Nullable ConsT<Class<?>, Throwable> callback) {
		ClassLoader mainLoader = Vars.mods.mainLoader();

		Fi sourceFile = Vars2.internalTree.child("libs").child(fileName + ".jar");
		if (!sourceFile.exists()) {
			Log.warn("File: '@' not exists", "libs/" + fileName + ".jar");

			return null;
		}

		Log.info("Loading @.jar", fileName);

		Time.mark();

		try {
			Fi toFile = Vars.dataDirectory.child("tmp/endfield/" + fileName + ".jar");

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

	private static void loadLibrary() {
		if (OS.isIos) return;

		String className = OS.isAndroid ? "endfield.android.AndroidImpl" : "endfield.desktop.DesktopImpl";

		loadLibrary("Impl", className, true, clazz -> Vars2.platformImpl = (PlatformImpl) clazz.getConstructor().newInstance());
	}
}
