package endfield.content;

import endfield.ai.NullAI;
import mindustry.ai.UnitCommand;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class UnitCommands2 {
	public static UnitCommand nullUnitCommand;

	private UnitCommands2() {}

	@Internal
	public static void loadAll() {
		nullUnitCommand = new UnitCommand("nullAI", "none", u -> new NullAI());
	}
}
