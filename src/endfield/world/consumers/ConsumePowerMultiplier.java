package endfield.world.consumers;

import mindustry.gen.Building;
import mindustry.world.consumers.ConsumePower;

public class ConsumePowerMultiplier extends ConsumePower {
	protected ConsumePowerMultiplier() {}

	public ConsumePowerMultiplier(float usage, float capacity, boolean buffered) {
		super(usage, capacity, buffered);
	}

	@Override
	public float requestedPower(Building entity) {
		return buffered ?
				(1f - entity.power.status) * capacity :
				usage * (entity.shouldConsume() ? 1f : 0f) * multiplier.get(entity);
	}
}
