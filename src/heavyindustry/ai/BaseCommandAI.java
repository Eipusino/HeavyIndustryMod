package heavyindustry.ai;

import mindustry.ai.types.CommandAI;
import mindustry.entities.units.AIController;
import org.jetbrains.annotations.Nullable;

public class BaseCommandAI extends CommandAI implements BaseCommand {
	@Override
	public @Nullable AIController controller() {
		return commandController;
	}
}
