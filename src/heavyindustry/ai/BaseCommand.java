package heavyindustry.ai;

import mindustry.entities.units.AIController;
import mindustry.entities.units.UnitController;
import org.jetbrains.annotations.Nullable;

public interface BaseCommand extends UnitController {
	/**
	 * @return Current controller instance based on command.
	 * @apiNote Bind field mindustry.ai.types.CommandAI.commandController
	 */
	@Nullable AIController controller();
}
