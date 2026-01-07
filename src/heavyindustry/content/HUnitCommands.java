package heavyindustry.content;

import heavyindustry.ai.NullAI;
import mindustry.ai.UnitCommand;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class HUnitCommands {
	public static UnitCommand nullUnitCommand;

	private HUnitCommands() {}

	@Internal
	public static void loadAll() {
		nullUnitCommand = new UnitCommand("nullAI", "none", u -> new NullAI());
	}
}
