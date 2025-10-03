package heavyindustry.ai;

import arc.util.Nullable;
import mindustry.entities.units.AIController;
import mindustry.entities.units.UnitController;

public interface BaseCommand extends UnitController {
	/**
	 * @return Current controller instance based on command.
	 * @apiNote Bind field mindustry.ai.types.CommandAI.commandController
	 */
	@Nullable
	AIController controller();
}
