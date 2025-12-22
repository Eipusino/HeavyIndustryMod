package heavyindustry.world.consumers;

import arc.func.Floatf;
import mindustry.gen.Building;
import mindustry.world.consumers.ConsumePower;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.Stats;

public class ConsumePowerDynamic2 extends ConsumePower {
	public final Floatf<Building> powers;
	public float displayedPowerUsage;

	public <T extends Building> ConsumePowerDynamic2(Floatf<T> func) {
		this(0f, func);
	}

	@SuppressWarnings("unchecked")
	public <T extends Building> ConsumePowerDynamic2(float displayed, Floatf<T> func) {
		super(0, 0, false);
		displayedPowerUsage = displayed;
		powers = (Floatf<Building>) func;
	}

	@Override
	public float requestedPower(Building entity) {
		return powers.get(entity);
	}

	@Override
	public void display(Stats stats) {
		if (displayedPowerUsage != 0f) {
			stats.add(Stat.powerUse, displayedPowerUsage * 60f, StatUnit.powerSecond);
		}
	}
}
