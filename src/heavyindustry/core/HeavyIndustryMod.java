package heavyindustry.core;

import arc.Core;
import arc.Events;
import arc.flabel.FLabel;
import arc.math.Mathf;
import arc.scene.ui.ImageButton.ImageButtonStyle;
import arc.util.Align;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Strings;
import arc.util.Time;
import arc.util.serialization.Jval;
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
import heavyindustry.game.HTeam;
import heavyindustry.gen.Entitys;
import heavyindustry.gen.HIcon;
import heavyindustry.gen.HMusics;
import heavyindustry.gen.HSounds;
import heavyindustry.util.Structf;
import heavyindustry.world.Worlds;
import heavyindustry.graphics.HCacheLayer;
import heavyindustry.graphics.HShaders;
import heavyindustry.graphics.HTextures;
import heavyindustry.graphics.SizedGraphics;
import heavyindustry.input.InputAggregator;
import heavyindustry.mod.LoadMod;
import heavyindustry.mod.ModJS;
import heavyindustry.net.HCall;
import heavyindustry.ui.HFonts;
import heavyindustry.ui.HStyles;
import heavyindustry.ui.Elements;
import heavyindustry.ui.dialogs.HResearchDialog;
import heavyindustry.util.IconLoader;
import mindustry.Vars;
import mindustry.ctype.Content;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.game.EventType.DisposeEvent;
import mindustry.game.EventType.FileTreeInitEvent;
import mindustry.game.EventType.MusicRegisterEvent;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.mod.Mod;
import mindustry.mod.Mods.LoadedMod;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

import java.lang.reflect.Field;

import static heavyindustry.HVars.inputAggregator;
import static heavyindustry.HVars.internalTree;
import static heavyindustry.HVars.sizedGraphics;
import static mindustry.Vars.headless;
import static mindustry.Vars.iconMed;
import static mindustry.Vars.mods;
import static mindustry.Vars.ui;

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
	/** Commonly used static read-only String. Do not change unless you know what you're doing. */
	public static final String MOD_NAME = "heavy-industry";
	/** The author of this mod. */
	public static final String AUTHOR = "Eipusino";
	/** The GitHub address of this mod. */
	public static final String LINK_GIT_HUB = "https://github.com/Eipusino/HeavyIndustryMod";

	/** The meta of this mod. */
	public static final Jval modJson;
	/** Is this mod in plugin mode. In this mode, the mod will not load content. */
	public static final boolean isPlugin;

	static @Nullable FloatingText floatingText;

	/** If needed, please call {@link #loaded()} for the LoadedMod of this mod. */
	static @Nullable LoadedMod loaded;

	static {
		modJson = LoadMod.getMeta(internalTree.root);

		isPlugin = Structf.get(() -> modJson.get("plugin").asBool(), false);

		LoadMod.addBlacklistedMods();
	}

	public HeavyIndustryMod() {
		Log.info("Loaded HeavyIndustry Mod constructor.");

		HClassMap.load();

		Events.on(ClientLoadEvent.class, event -> {
			if (isPlugin || headless || Core.settings.getBool("hi-closed-dialog")) return;

			FLabel label = new FLabel(Core.bundle.get("hi-author") + AUTHOR);
			BaseDialog dialog = new BaseDialog(Core.bundle.get("hi-name")) {{
				buttons.button(Core.bundle.get("close"), this::hide).size(210f, 64f);
				buttons.button((Core.bundle.get("hi-link-github")), () -> {
					if (!Core.app.openURI(LINK_GIT_HUB)) {
						ui.showErrorMessage("@linkfail");
						Core.app.setClipboardText(LINK_GIT_HUB);
					}
				}).size(210f, 64f);
				cont.pane(t -> {
					t.image(Core.atlas.find(MOD_NAME + "-cover")).left().size(600f, 403f).pad(3f).row();
					t.add(Core.bundle.get("hi-version")).left().growX().wrap().pad(4f).labelAlign(Align.left).row();
					t.add(label).left().row();
					t.add(Core.bundle.get("hi-class")).left().growX().wrap().pad(4f).labelAlign(Align.left).row();
					t.add(Core.bundle.get("hi-oh-no")).left().growX().wrap().pad(4f).labelAlign(Align.left).row();
					t.add(Core.bundle.get("hi-oh-no-l")).left().growX().wrap().width(550f).maxWidth(600f).pad(4f).labelAlign(Align.left).row();
				}).grow().center().maxWidth(600f);
			}};
			dialog.show();
		});

		Events.on(FileTreeInitEvent.class, event -> {
			if (!headless) {
				HFonts.load();
				HSounds.load();

				Core.app.post(() -> {
					HShaders.init();
					HTextures.init();
					HCacheLayer.init();

					inputAggregator = new InputAggregator();
					sizedGraphics = new SizedGraphics();
				});
			}
		});

		Events.on(MusicRegisterEvent.class, event -> {
			if (!headless) {
				HMusics.load();
			}
		});

		Events.on(DisposeEvent.class, event -> {
			if (!headless) {
				HShaders.dispose();
			}
		});

		//IOS does not support rhino js
		if (!Core.app.isIOS()) {
			Core.app.post(ModJS::init);
		}
	}

	@Override
	public void loadContent() {
		HCall.init();

		Entitys.load();
		Worlds.load();

		if (!isPlugin) {
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

		try {
			Field[] fields = Vars.class.getDeclaredFields();
			for (Field field : fields) {
				if (field.getName().equals("arcVersion")) {
					Log.warn(Core.bundle.get("hi-arc-warn"));

					break;
				}
			}
		} catch (Exception e) {
			Log.err(e);
		}
	}

	@Override
	public void init() {
		if (!headless) {
			HIcon.load();

			HStyles.onClient();
			Elements.onClient();
		}

		IconLoader.loadIcons(internalTree.child("other/icons.properties"));

		LoadedMod mod = loaded();

		if (mod != null && isPlugin) {
			mod.meta.hidden = true;
			mod.meta.name = MOD_NAME + "-plugin";
			mod.meta.displayName = Core.bundle.get("hi-name") + " Plugin";
			mod.meta.version = Structf.get(() -> modJson.get("version").asString() + "-plug-in", mod.meta.version);
		}

		if (ui != null) {
			if (ui.settings != null) {
				//add heavy-industry settings
				ui.settings.addCategory(Core.bundle.format("hi-settings"), HIcon.reactionIcon, t -> {
					t.checkPref("hi-closed-dialog", false);
					t.checkPref("hi-floating-text", true);
					t.checkPref("hi-animated-shields", true);
					t.sliderPref("hi-strobespeed", 3, 1, 20, 1, s -> Strings.autoFixed(s / 2f, 2));
					//this fucking sucks
					t.table(Tex.button, c -> {
						c.button("@settings.game", Icon.settings, Styles.flatt, iconMed, () -> {}).growX().marginLeft(8f).height(50f).row();
						c.button("@settings.controls", Icon.move, Styles.flatt, iconMed, () -> {}).growX().marginLeft(8f).height(50f).row();
						c.button("@settings.hi-cleardata", Icon.save, Styles.flatt, iconMed, () -> {
							if (Elements.gameDataDialog != null) {
								Elements.gameDataDialog.show();
							}
						}).growX().marginLeft(8f).height(50f).pad(12f).row();
					}).width(Math.min(Core.graphics.getWidth() / 1.2f, 460f)).padBottom(45f);
					t.fill(c -> c.bottom().right().button(Icon.github, new ImageButtonStyle(), () -> {
						if (!Core.app.openURI(LINK_GIT_HUB)) {
							ui.showInfoFade("@linkfail");
							Core.app.setClipboardText(LINK_GIT_HUB);
						}
					}).marginTop(9f).marginLeft(10f).tooltip("@setting.hi-github-join").size(84f, 45f).name("@setting.github"));
				});
			}

			//Replace the original technology ResearchDialog
			//This is a rather foolish approach, but there is nothing we can do about it.
			var dialog = new HResearchDialog();
			ui.research.shown(() -> {
				dialog.show();
				if (ui.research != null) {
					Time.runTask(1f, ui.research::hide);
				}
			});
		}

		if (headless || ui == null || isEnabled("extra-utilities") || !Core.settings.getBool("hi-floating-text")) return;

		String massage = Core.bundle.get("hi-random-massage");
		String[] massageSplit = massage.split("&");

		floatingText = new FloatingText(massageSplit[Mathf.random(massageSplit.length - 1)]);
		floatingText.build(ui.menuGroup);
	}

	/** Omitting longer mod names is generally used to load mod sprites. */
	public static String name(String add) {
		return MOD_NAME + "-" + add;
	}

	public static String name() {
		return MOD_NAME + "-";
	}

	public static boolean isHeavyIndustry(@Nullable Content content) {
		return content != null && isHeavyIndustry(content.minfo.mod);
	}

	public static boolean isHeavyIndustry(@Nullable LoadedMod mod) {
		return mod != null && mod == loaded();
	}

	/** Safely obtain the {@code LoadedMod} for this mod. */
	public static LoadedMod loaded() {
		if (loaded == null) loaded = mods.getMod(MOD_NAME);
		return loaded;
	}

	public static boolean isEnabled(String name) {
		LoadedMod mod = mods.getMod(name);
		return mod != null && mod.isSupported() && mod.enabled();
	}
}
