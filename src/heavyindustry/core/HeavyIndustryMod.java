package heavyindustry.core;

import arc.Core;
import arc.Events;
import arc.flabel.FLabel;
import arc.math.Mathf;
import arc.util.Align;
import arc.util.Log;
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
import heavyindustry.game.HTeam;
import heavyindustry.gen.Entitys;
import heavyindustry.gen.HIcon;
import heavyindustry.gen.HMusics;
import heavyindustry.gen.HSounds;
import heavyindustry.util.Utils;
import heavyindustry.world.Worlds;
import heavyindustry.graphics.HCacheLayer;
import heavyindustry.graphics.HShaders;
import heavyindustry.graphics.HTextures;
import heavyindustry.graphics.SizedGraphics;
import heavyindustry.input.InputAggregator;
import heavyindustry.mod.HMods;
import heavyindustry.mod.HScripts;
import heavyindustry.net.HCall;
import heavyindustry.ui.HFonts;
import heavyindustry.ui.HStyles;
import heavyindustry.ui.Elements;
import heavyindustry.ui.dialogs.HResearchDialog;
import heavyindustry.util.IconLoader;
import mindustry.game.EventType.ClientLoadEvent;
import mindustry.game.EventType.DisposeEvent;
import mindustry.game.EventType.FileTreeInitEvent;
import mindustry.game.EventType.MusicRegisterEvent;
import mindustry.mod.Mod;
import mindustry.ui.dialogs.BaseDialog;

import static heavyindustry.HVars.AUTHOR;
import static heavyindustry.HVars.inputAggregator;
import static heavyindustry.HVars.internalTree;
import static heavyindustry.HVars.LINK_GIT_HUB;
import static heavyindustry.HVars.MOD_NAME;
import static heavyindustry.HVars.sizedGraphics;
import static mindustry.Vars.headless;
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
public class HeavyIndustryMod extends Mod {
	FloatingText floatingText;

	public HeavyIndustryMod() {
		Log.info("Loaded HeavyIndustry Mod constructor.");

		HClassMap.load();

		Events.on(ClientLoadEvent.class, event -> {
			if (headless || Core.settings.getBool("hi-closed-dialog")) return;

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
					t.add(Utils.generateRandomString(10, 20)).left().growX().wrap().pad(4f).labelAlign(Align.left).row();
					t.add(Utils.generateRandomString(100, 200)).left().growX().wrap().width(550f).maxWidth(600f).pad(4f).labelAlign(Align.left).row();
				}).grow().center().maxWidth(600f);
			}};
			dialog.show();
		});

		Events.on(FileTreeInitEvent.class, event -> {
			if (!headless) {
				HFonts.onClient();
				HSounds.onClient();

				Core.app.post(() -> {
					HShaders.onClient();
					HTextures.onClient();
					HCacheLayer.onClient();

					inputAggregator = new InputAggregator();
					sizedGraphics = new SizedGraphics();
				});
			}
		});

		Events.on(MusicRegisterEvent.class, event -> {
			if (!headless) {
				HMusics.onClient();
			}
		});

		Events.on(DisposeEvent.class, event -> {
			if (!headless) {
				HShaders.dispose();
			}
		});

		//IOS does not support rhino js
		if (!Core.app.isIOS()) {
			Core.app.post(HScripts::init);
		}
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
		if (!headless) {
			HIcon.onClient();

			HStyles.onClient();
			Elements.onClient();
		}

		IconLoader.loadIcons(internalTree.child("other/icons.properties"));

		if (ui != null) {
			if (ui.settings != null) {
				//add heavy-industry settings
				ui.settings.addCategory(Core.bundle.format("hi-settings"), HIcon.reactionIcon, t -> {
					t.checkPref("hi-closed-dialog", false);
					t.checkPref("hi-floating-text", true);
					t.checkPref("hi-animated-shields", true);
					t.checkPref("hi-render-sort", false);
					t.sliderPref("hi-strobespeed", 3, 1, 20, 1, s -> Strings.autoFixed(s / 2f, 2));
				});
			}

			//Replace the original technology ResearchDialog
			//This is a rather foolish approach, but there is nothing we can do about it.
			HResearchDialog dialog = new HResearchDialog();
			ui.research.shown(() -> {
				dialog.show();
				if (ui.research != null) {
					Time.runTask(1f, ui.research::hide);
				}
			});
		}

		if (headless || ui == null || HMods.isEnabled("extra-utilities") || !Core.settings.getBool("hi-floating-text")) return;

		String massage = Core.bundle.get("hi-random-massage");
		String[] massageSplit = massage.split("&");

		floatingText = new FloatingText(massageSplit[Mathf.random(massageSplit.length - 1)]);
		floatingText.build(ui.menuGroup);
	}
}
