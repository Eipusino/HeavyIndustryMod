package heavyindustry.core;

import arc.Core;
import arc.Events;
import arc.files.Fi;
import arc.flabel.FLabel;
import arc.func.Cons;
import arc.math.Mathf;
import arc.util.Align;
import arc.util.Log;
import arc.util.OS;
import arc.util.Strings;
import arc.util.Time;
import heavyindustry.HVars;
import heavyindustry.content.HBlocks;
import heavyindustry.content.HBullets;
import heavyindustry.content.HItems;
import heavyindustry.content.HLiquids;
import heavyindustry.content.HOverrides;
import heavyindustry.content.HPlanets;
import heavyindustry.content.HSectorPresets;
import heavyindustry.content.HStatusEffects;
import heavyindustry.content.HTechTree;
import heavyindustry.content.HUnitTypes;
import heavyindustry.content.HWeathers;
import heavyindustry.files.HFiles;
import heavyindustry.game.HTeam;
import heavyindustry.gen.Entitys;
import heavyindustry.gen.HIcon;
import heavyindustry.gen.HMusics;
import heavyindustry.gen.HSounds;
import heavyindustry.graphics.MathRenderer;
import heavyindustry.graphics.SizedGraphics;
import heavyindustry.util.PlatformImpl;
import heavyindustry.util.Utils;
import heavyindustry.world.Worlds;
import heavyindustry.graphics.HCacheLayer;
import heavyindustry.graphics.HShaders;
import heavyindustry.graphics.HTextures;
import heavyindustry.input.InputAggregator;
import heavyindustry.mod.HMods;
import heavyindustry.mod.HScripts;
import heavyindustry.net.HCall;
import heavyindustry.ui.HFonts;
import heavyindustry.ui.HStyles;
import heavyindustry.ui.Elements;
import heavyindustry.ui.dialogs.HResearchDialog;
import heavyindustry.util.IconLoader;
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
	static ClassLoader lastLoader;
	static Class<?> platformImpl;

	static FloatingText floatingText;

	static {
		try {
			// Load Impl.jar from the libs path inside the mod, This is a very important part of the mod's reflection function.
			loadLibrary();
		} catch (Throwable e) {
			Log.err(e);
		}

		if (HVars.platformImpl == null) {
			// This situation usually does not occur...
			HVars.platformImpl = new DefaultImpl();
		}
	}

	public HeavyIndustryMod() {
		Log.info("Loaded HeavyIndustry Mod constructor.");

		HClassMap.load();

		Events.on(ClientLoadEvent.class, event -> {
			if (Vars.headless || Core.settings.getBool("hi-closed-dialog")) return;

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
					t.add(Utils.generateRandomString(10, 20)).left().growX().wrap().pad(4f).labelAlign(Align.left).row();
					t.add(Utils.generateRandomString(100, 200)).left().growX().wrap().width(550f).maxWidth(600f).pad(4f).labelAlign(Align.left).row();
				}).grow().center().maxWidth(600f);
			}};
			dialog.show();
		});

		Events.on(FileTreeInitEvent.class, event -> {
			if (!Vars.headless) {
				HFonts.onClient();
				HSounds.onClient();

				Core.app.post(() -> {
					HTextures.onClient();
					HShaders.onClient();
					HCacheLayer.onClient();
					MathRenderer.onClient();

					HVars.sizedGraphics = new SizedGraphics();
					HVars.inputAggregator = new InputAggregator();
				});
			}
		});

		Events.on(MusicRegisterEvent.class, event -> {
			if (!Vars.headless) {
				HMusics.onClient();
			}
		});

		Events.on(DisposeEvent.class, event -> {
			if (!Vars.headless) {
				HShaders.dispose();
			}
		});

		Core.app.post(HScripts::init);
	}

	@Override
	public void loadContent() {
		HCall.init();

		Entitys.load();
		Worlds.load();

		HBullets.load();
		HTeam.load();
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
	}

	@Override
	public void init() {
		HUnitTypes.loadImmunities();

		if (!Vars.headless) {
			HIcon.onClient();

			HStyles.onClient();
			Elements.onClient();
		}

		IconLoader.loadIcons(HVars.internalTree.child("other/icons.properties"));

		if (Vars.ui != null) {
			if (Vars.ui.settings != null) {
				//add heavy-industry settings
				Vars.ui.settings.addCategory(Core.bundle.format("hi-settings"), HIcon.reactionIcon, table -> {
					table.checkPref("hi-closed-dialog", false);
					table.checkPref("hi-floating-text", true);
					table.checkPref("hi-animated-shields", true);
					table.sliderPref("hi-strobe-speed", 3, 1, 20, 1, s -> Strings.autoFixed(s / 2f, 2));
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

			// Replace the original technology ResearchDialog
			// This is a rather foolish approach, but there is nothing we can do about it.
			HResearchDialog dialog = new HResearchDialog();
			Vars.ui.research.shown(() -> {
				dialog.show();
				Time.runTask(1f, () -> {
					if (Vars.ui.research != null) Vars.ui.research.hide();
				});
			});

			if (!Vars.headless && !HMods.isEnabled("extra-utilities") && !HMods.isX() && Core.settings.getBool("hi-floating-text")) {
				String massage = Core.bundle.get("hi-random-massage");
				String[] massageSplit = massage.split("&");

				floatingText = new FloatingText(massageSplit[Mathf.random(massageSplit.length - 1)]);
				floatingText.build(Vars.ui.menuGroup);
			}
		}
	}

	public static Class<?> loadLibrary(String fileName, String mainClassName, boolean showError) {
		return loadLibrary(fileName, mainClassName, showError, c -> {});
	}

	public static Class<?> loadLibrary(String fileName, String mainClassName, boolean showError, Cons<Class<?>> callback) {
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
			HFiles.delete(toFile);
			sourceFile.copyTo(toFile);
			ClassLoader loader = Vars.platform.loadJar(toFile, mainLoader);

			if (mainLoader instanceof ModClassLoader mod) mod.addChild(loader);

			Class<?> clazz = Class.forName(mainClassName, true, loader);
			lastLoader = loader;

			if (callback != null) callback.get(clazz);

			return clazz;
		} catch (Throwable e) {
			if (showError) {
				Log.err(Strings.format("Unexpected exception when loading '@'", sourceFile), e);
			}

			return null;
		} finally {
			Log.info("Loaded '@' in @ms", sourceFile.name(), Time.elapsed());
		}
	}

	static void loadLibrary() {
		platformImpl = loadLibrary("Impl", OS.isAndroid ? "heavyindustry.android.AndroidImpl" : "heavyindustry.desktop.DesktopImpl", true, impl -> {
			try {
				if (impl.getConstructor().newInstance() instanceof PlatformImpl core) {
					HVars.platformImpl = core;
				}
			} catch (Throwable e) {
				Log.err(e);
			}
		});
	}
}
