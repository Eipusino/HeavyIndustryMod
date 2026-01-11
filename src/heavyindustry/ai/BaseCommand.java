package heavyindustry.ai;

import mindustry.entities.units.AIController;
import mindustry.entities.units.UnitController;
import mindustry.gen.Bullet;
import mindustry.gen.Unit;
import org.jetbrains.annotations.Nullable;

public interface BaseCommand extends UnitController {
	@Override
	default void hit(Bullet bullet) {}

	@Override
	default boolean isValidController() {
		return true;
	}

	@Override
	default boolean isLogicControllable() {
		return false;
	}

	@Override
	default void updateUnit() {}

	@Override
	default void removed(Unit unit) {}

	@Override
	default void afterRead(Unit unit) {}

	/**
	 * @return Current controller instance based on command.
	 * @apiNote Bind field mindustry.ai.types.CommandAI.commandController
	 */
	@Nullable AIController controller();
}
