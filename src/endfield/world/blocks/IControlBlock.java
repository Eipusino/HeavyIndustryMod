package endfield.world.blocks;

import mindustry.world.blocks.ControlBlock;

public interface IControlBlock extends ControlBlock {
	@Override
	default boolean isControlled() {
		return unit().isPlayer();
	}

	@Override
	default boolean canControl() {
		return true;
	}

	@Override
	default boolean shouldAutoTarget() {
		return true;
	}
}
