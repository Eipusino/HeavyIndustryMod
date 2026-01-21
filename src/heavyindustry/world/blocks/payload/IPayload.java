package heavyindustry.world.blocks.payload;

import heavyindustry.math.IPosition;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.world.blocks.payloads.Payload;
import org.jetbrains.annotations.Nullable;

public interface IPayload extends Payload, IPosition {
	@Override
	default void update(@Nullable Unit unitHolder, @Nullable Building buildingHolder) {}

	@Override
	default boolean dump() {
		return false;
	}

	@Override
	default boolean fits(float s) {
		return size() / Vars.tilesize <= s;
	}

	@Override
	default float rotation() {
		return 0f;
	}

	@Override
	default void destroyed() {}

	@Override
	default float getX() {
		return x();
	}

	@Override
	default float getY() {
		return y();
	}

	@Override
	default void remove() {}
}
