package endfield.content;

import endfield.ai.NullAI;
import mindustry.ai.UnitCommand;

public final class UnitCommands2 {
	public static UnitCommand nullUnitCommand;

	private UnitCommands2() {}

	public static void loadAll() {
		nullUnitCommand = new UnitCommand("nullAI", "none", u -> new NullAI());
	}
}
