package heavyindustry.content;

import heavyindustry.ai.NullAI;
import mindustry.ai.UnitCommand;

public final class HUnitCommands {
	public static UnitCommand nullUnitCommand;

	private HUnitCommands() {}

	public static void loadAll() {
		nullUnitCommand = new UnitCommand("nullAI", "none", u -> new NullAI());
	}
}
