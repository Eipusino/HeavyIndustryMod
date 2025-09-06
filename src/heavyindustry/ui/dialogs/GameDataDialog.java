package heavyindustry.ui.dialogs;

import arc.Core;
import heavyindustry.HVars;
import mindustry.Vars;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.type.Planet;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;

public class GameDataDialog extends BaseDialog {
	public GameDataDialog() {
		super("@settings.hi-cleardata");

		addCloseButton();

		cont.table(Tex.button, cat -> {
			for (Planet planet : Vars.content.planets()) {
				if (planet != null && planet.accessible && planet.techTree != null && planet.sectors.any()) {
					cat.button(
							Core.bundle.get("settings.clearresearch") + ": " + planet.localizedName, Icon.trash, Styles.flatt, Vars.iconMed,
							() -> Vars.ui.showConfirm(Core.bundle.get("settings.hi-clearresearch-confirm") + planet.localizedName, () -> HVars.resetTree(planet.techTree))
					).growX().marginLeft(8).height(50).row();
					cat.button(
							Core.bundle.get("settings.clearcampaignsaves") + ": " + planet.localizedName, Icon.trash, Styles.flatt, Vars.iconMed,
							() -> Vars.ui.showConfirm(Core.bundle.get("settings.hi-clearcampaignsaves-confirm") + planet.localizedName, () -> HVars.resetSaves(planet.sectors))
					).growX().marginLeft(8).height(50).row();
				}
			}
		}).width(400f).row();
	}
}
