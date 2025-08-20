package heavyindustry.world.consumers;

import arc.func.Floatf;
import mindustry.gen.Building;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.meta.Stats;

public class ConsumeBufferedPowerDynamic extends ConsumePower {
	private final Floatf<Building> dynamicCapacity;

	@SuppressWarnings("unchecked")
	public <T extends Building> ConsumeBufferedPowerDynamic(Floatf<T> capacity) {
		super(0, 1, true);
		dynamicCapacity = (Floatf<Building>) capacity;

		update = true;
	}

	@Override
	public boolean ignore() {
		return false;
	}

	@Override
	public void update(Building build) {
		capacity = dynamicCapacity.get(build);
	}

	@Override
	public float efficiency(Building build) {
		return 1f;
	}

	@Override
	public void display(Stats stats) {
		//Power capacity varies, don't display
	}

	public float getPowerCapacity(Building build) {
		return dynamicCapacity.get(build);
	}
}
