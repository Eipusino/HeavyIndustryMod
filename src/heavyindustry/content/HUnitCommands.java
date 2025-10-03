package heavyindustry.content;

import heavyindustry.ai.ApathyIAI;
import heavyindustry.ai.DespondencyAI;
import heavyindustry.ai.NullAI;
import heavyindustry.ai.YggdrasilAI;
import heavyindustry.gen.ApathyIUnit;
import heavyindustry.gen.DespondencyUnit;
import heavyindustry.gen.YggdrasilUnit;
import mindustry.ai.UnitCommand;

public final class HUnitCommands {
	public static UnitCommand nullUnitCommand, apathyUnitCommand, yggdrasilUnitCommand, despondencyUnitCommand;

	private HUnitCommands() {}

	public static void loadAll() {
		nullUnitCommand = new UnitCommand("nullAI", "none", u -> new NullAI());
		apathyUnitCommand = new UnitCommand("apathyAI", "mode-attack", u -> u instanceof ApathyIUnit ? new ApathyIAI() : null);
		yggdrasilUnitCommand = new UnitCommand("yggdrasilAI", "mode-attack", u -> u instanceof YggdrasilUnit ? new YggdrasilAI() : null);
		despondencyUnitCommand = new UnitCommand("despondencyAI", "mode-attack", u -> u instanceof DespondencyUnit ? new DespondencyAI() : null);
	}
}
