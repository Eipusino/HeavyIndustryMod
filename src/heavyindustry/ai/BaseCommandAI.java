package heavyindustry.ai;

import mindustry.ai.types.CommandAI;
import mindustry.entities.units.AIController;

public class BaseCommandAI extends CommandAI implements BaseCommand {
	@Override
	public AIController controller() {
		return commandController;
	}
}
