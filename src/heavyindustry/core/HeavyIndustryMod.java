package heavyindustry.core;

import arc.Core;
import arc.Events;
import arc.flabel.FLabel;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mat;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.Group;
import arc.scene.event.Touchable;
import arc.scene.ui.ImageButton.ImageButtonStyle;
import arc.scene.ui.layout.Scl;
import arc.util.Align;
import arc.util.Log;
import arc.util.Nullable;
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
import heavyindustry.game.HTeams;
import heavyindustry.gen.EntityRegister;
import heavyindustry.gen.HIcon;
import heavyindustry.gen.HMusics;
import heavyindustry.gen.HSounds;
import heavyindustry.gen.WorldRegister;
import heavyindustry.graphics.Draw3d;
import heavyindustry.graphics.HCacheLayer;
import heavyindustry.graphics.HShaders;
import heavyindustry.graphics.HTextures;
import heavyindustry.graphics.SpecialMenuRenderer;
import heavyindustry.graphics.ScreenSampler;
import heavyindustry.graphics.SizedGraphics;
import heavyindustry.input.InputAggregator;
import heavyindustry.io.WorldData;
import heavyindustry.mod.LoadMod;
import heavyindustry.mod.ModJS;
import heavyindustry.net.HCall;
import heavyindustry.ui.HFonts;
import heavyindustry.ui.HStyles;
import heavyindustry.ui.Elements;
import heavyindustry.ui.dialogs.HResearchDialog;
import heavyindustry.util.IconLoader;
import heavyindustry.util.Utils;
import kotlin.KotlinVersion;
import mindustry.ctype.Content;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.game.EventType.DisposeEvent;
import mindustry.game.EventType.FileTreeInitEvent;
import mindustry.game.EventType.MusicRegisterEvent;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.mod.Mod;
import mindustry.mod.Mods.LoadedMod;
import mindustry.ui.Fonts;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable;
import mindustry.ui.dialogs.SettingsMenuDialog.SettingsTable.Setting;
import mindustry.ui.fragments.MenuFragment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static heavyindustry.HVars.inputAggregator;
import static heavyindustry.HVars.internalTree;
import static heavyindustry.HVars.MOD_NAME;
import static heavyindustry.HVars.name;
import static heavyindustry.HVars.sizedGraphics;
import static mindustry.Vars.headless;
import static mindustry.Vars.iconMed;
import static mindustry.Vars.macNotchHeight;
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
	/** The author of this mod. */
	public static final String AUTHOR = "Eipusino";
	/** The GitHub address of this mod. */
	public static final String LINK_GIT_HUB = "https://github.com/Eipusino/HeavyIndustryMod";

	/** The meta of this mod. */
	public static final Jval modJson;
	/** Is this mod in plugin mode. In this mode, the mod will not load content. */
	public static final boolean isPlugin;

	/** If needed, please call {@link #loaded()} for the LoadedMod of this mod. */
	private static LoadedMod loaded;

	static {
		modJson = LoadMod.getMeta(internalTree.root);
		isPlugin = modJson != null && modJson.has("plugin") && modJson.isBoolean() && modJson.get("plugin").asBool();

		LoadMod.addBlacklistedMods();
	}

	public HeavyIndustryMod() {
		if (Core.graphics != null && !Core.graphics.isGL30Available()) {
			throw new UnsupportedOperationException("HeavyIndustryMod only runs with OpenGL 3.0 (on desktop) or OpenGL ES 3.0 (on android) and above!");
		}

		Log.infoTag("Kotlin", "Version: " + KotlinVersion.CURRENT);
		Log.info("Loaded HeavyIndustry Mod constructor.");

		HClassMap.load();

		Events.on(ClientLoadEvent.class, event -> {
			if (isPlugin) return;

			boolean special = Core.settings.getBool("hi-special");

			try {
				if (special) {
					var field = MenuFragment.class.getDeclaredField("renderer");
					field.setAccessible(true);
					field.set(ui.menufrag, new SpecialMenuRenderer());
				}
			} catch (Exception e) {
				Log.err("Failed to replace renderer", e);
			}

			String close = Core.bundle.get("close");

			dia: {
				if (headless || Core.settings.getBool("hi-closed-dialog") || isAprilFoolsDay()) break dia;

				FLabel label = new FLabel(Core.bundle.get("hi-author") + AUTHOR);
				BaseDialog dialog = new BaseDialog(Core.bundle.get("hi-name")) {{
					buttons.button(close, this::hide).size(210f, 64f);
					buttons.button((Core.bundle.get("hi-link-github")), () -> {
						if (!Core.app.openURI(LINK_GIT_HUB)) {
							ui.showErrorMessage("@linkfail");
							Core.app.setClipboardText(LINK_GIT_HUB);
						}
					}).size(210f, 64f);
					cont.pane(t -> {
						t.image(Core.atlas.find(name(special ? "cover-special" : "cover"))).left().size(600f, 403f).pad(3f).row();
						t.add(Core.bundle.get("hi-version")).left().growX().wrap().pad(4f).labelAlign(Align.left).row();
						t.add(label).left().row();
						t.add(Core.bundle.get("hi-class")).left().growX().wrap().pad(4f).labelAlign(Align.left).row();
						t.add(Core.bundle.get("hi-oh-no")).left().growX().wrap().pad(4f).labelAlign(Align.left).row();
						t.add(Core.bundle.get("hi-oh-no-l")).left().growX().wrap().width(550f).maxWidth(600f).pad(4f).labelAlign(Align.left).row();
					}).grow().center().maxWidth(600f);
				}};
				dialog.show();
			}
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
			if (!headless)
				HMusics.load();
		});

		Events.on(DisposeEvent.class, event -> {
			if (!headless)
				HShaders.dispose();
		});

		Core.app.post(ModJS::init);
	}

	@Override
	public void loadContent() {
		HCall.init();

		EntityRegister.load();
		WorldRegister.load();

		HBullets.load();

		if (!isPlugin) {
			HTeams.load();
			HItems.load();
			HStatusEffects.load();
			HLiquids.load();
			HUnitTypes.load();
			HBlocks.load();
			HWeathers.load();
			HOverrides.load();
			HPlanets.load();
			HSectorPresets.load();
			HTechTree.load();
		}

		Utils.loadItems();

		//Load the content of the accessory module.
		LoadMod.loadContent();
	}

	@Override
	public void init() {
		if (!headless) {
			HIcon.load();

			//Set up screen sampler.
			ScreenSampler.setup();
			Draw3d.init();

			HStyles.onClient();
			Elements.onClient();
		}

		WorldData.init();

		IconLoader.loadIcons(internalTree.child("other/icons.properties"));

		if (!headless && !isPlugin && mods.locateMod("extra-utilities") == null && isAprilFoolsDay()) {
			HOverrides.loadAprilFoolsDay();

			if (ui != null) {
				Events.on(ClientLoadEvent.class, event -> Time.runTask(10f, () -> {
					BaseDialog dialog = new BaseDialog(Core.bundle.get("hi-name")) {
						int con = 0;
						float bx, by;
					{
						cont.add(Core.bundle.get("hi-ap-main"));
						buttons.button("", this::hide).update(b -> {
							b.setText(con > 0 ? con == 5 ? Core.bundle.get("hi-ap-happy") : Core.bundle.get("hi-ap-click") : Core.bundle.get("hi-ap-ok"));
							if (con > 0) {
								b.x = bx;
								b.y = by;
							}
						}).size(140, 50).center();
					}
						@Override
						public void hide() {
							if (con >= 5) {
								super.hide();
								return;
							}
							con++;
							bx = Mathf.random(width * 0.8f);
							by = Mathf.random(height * 0.8f);
						}
					};
					dialog.show();
				}));
			}
		}

		LoadedMod theMod = loaded();

		if (theMod != null) {
			theMod.meta.author = AUTHOR;
			if (isPlugin) {
				theMod.meta.hidden = true;
				theMod.meta.name = MOD_NAME + "-plugin";
				theMod.meta.displayName = Core.bundle.get("hi-name") + " Plugin";
			}
		}

		if (ui != null) {
			if (ui.settings != null) {
				//add heavy-industry settings
				ui.settings.addCategory(Core.bundle.format("hi-settings"), HIcon.reactionIcon, t -> {
					t.checkPref("hi-closed-dialog", false);
					t.checkPref("hi-floating-text", true);
					t.checkPref("hi-animated-shields", true);
					t.checkPref("hi-serpulo-sector-invasion", true);
					t.checkPref("hi-special", false);
					t.checkPref("hi-developer-mode", false);
					t.pref(new Setting(Core.bundle.get("hi-show-donor-and-develop")) {
						@Override
						public void add(SettingsTable table) {
							table.button(name, Elements.ddItemsList::toShow).margin(14).width(200f).pad(6);
							table.row();
						}
					});
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
					}).marginTop(9f).marginLeft(10f).tooltip("@setting.hi-github-join").size(84f, 45f).name("github"));
				});
			}

			//Replace the original technology ResearchDialog
			//This is a rather foolish approach, but there is nothing we can do about it.
			var dialog = new HResearchDialog();
			ui.research.shown(() -> {
				dialog.show();
				Objects.requireNonNull(ui.research);
				Time.runTask(1f, ui.research::hide);
			});
		}

		set: {
			String massage = Core.bundle.get("hi-random-massage");
			String[] massageSplit = massage.split("&");

			if (headless || ui == null || mods.locateMod("extra-utilities") != null || !Core.settings.getBool("hi-floating-text")) break set;

			var fl = new FloatingText(massageSplit[Mathf.random(massageSplit.length - 1)]);
			fl.build(ui.menuGroup);
		}
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

	public static boolean isAprilFoolsDay() {
		var date = LocalDate.now();
		var sdf = DateTimeFormatter.ofPattern("MMdd");
		String fd = sdf.format(date);
		return fd.equals("0401");
	}
}

class FloatingText {
	static final Mat setMat = new Mat(), reMat = new Mat();
	static final Vec2 vec2 = new Vec2();

	final String title;

	FloatingText(String v) {
		title = v;
	}

	void build(Group parent) {
		parent.fill((x, y, w, h) -> {
			TextureRegion logo = Core.atlas.find("logo");
			float width = Core.graphics.getWidth(), height = Core.graphics.getHeight() - Core.scene.marginTop;
			float logoScl = Scl.scl(1) * logo.scale;
			float logoWidth = Math.min(logo.width * logoScl, Core.graphics.getWidth() - Scl.scl(20));
			float logoHeight = logoWidth * (float) logo.height / logo.width;

			float fx = (int) (width / 2f);
			float fy = (int) (height - 6 - logoHeight) + logoHeight / 2 - (Core.graphics.isPortrait() ? Scl.scl(30f) : 0f);
			if (Core.settings.getBool("macnotch")) {
				fy -= Scl.scl(macNotchHeight);
			}

			float ex = fx + logoWidth / 3 - Scl.scl(1f), ey = fy - logoHeight / 3f - Scl.scl(2f);
			float ang = 12 + Mathf.sin(Time.time, 8, 2f);

			float dst = Mathf.dst(ex, ey, 0, 0);
			vec2.set(0, 0);
			float dx = Utils.dx(0, dst, vec2.angleTo(ex, ey) + ang);
			float dy = Utils.dy(0, dst, vec2.angleTo(ex, ey) + ang);

			reMat.set(Draw.trans());

			Draw.trans(setMat.setToTranslation(ex - dx, ey - dy).rotate(ang));
			Fonts.outline.draw(title, ex, ey, Color.yellow, Math.min(30f / title.length(), 1.5f) + Mathf.sin(Time.time, 8, 0.2f), false, Align.center);

			Draw.trans(reMat);
			Draw.reset();
		}).touchable = Touchable.disabled;
	}
}
