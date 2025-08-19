package heavyindustry.world.blocks.power;

import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.Damage;
import mindustry.world.blocks.power.VariableReactor;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class ConsumeVariableReactor extends VariableReactor {
	public float itemDuration = 120f;

	public ConsumeVariableReactor(String name) {
		super(name);
		hasItems = true;
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.add(Stat.productionTime, itemDuration / 60, StatUnit.seconds);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = ConsumeVariableReactorBuild::new;
	}

	public class ConsumeVariableReactorBuild extends VariableReactorBuild {
		public float consumeItemTimer;

		@Override
		public void updateTile() {
			heat = calculateHeat(sideHeat);
			productionEfficiency = efficiency;
			warmup = Mathf.lerpDelta(warmup, productionEfficiency > 0 ? 1f : 0f, warmupSpeed);
			if (instability >= 1) kill();

			totalProgress += productionEfficiency * Time.delta;
			if (Mathf.chanceDelta(effectChance * warmup)) {
				effect.at(x, y, effectColor);
				Damage.damage(team, x, y, 40f, 100f, true, true, true, true, null);
			}

			consumeItemTimer += Time.delta * efficiency;
			if (efficiency > 0 && consumeItemTimer >= itemDuration) {
				consumeItemTimer %= itemDuration;
				consume();
			}
		}
	}
}
