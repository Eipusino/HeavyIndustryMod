package heavyindustry.world.blocks.production;

import arc.math.Mathf;
import heavyindustry.math.Mathm;
import mindustry.world.blocks.production.Drill;

public class PressureDrill extends Drill {
	public float maxFactor = 3f;
	public float minPowerNeed = 10f;

	public PressureDrill(String name) {
		super(name);
	}

	public class PressureDrillBuild extends Drill.DrillBuild {
		public PressureDrillBuild() {
			super();
		}

		@Override
		public void updateTile() {
			if (timer(timerDump, dumpTime)) {
				dump(dominantItem != null && items.has(dominantItem) ? dominantItem : null);
			}

			if (dominantItem == null) {
				return;
			}

			timeDrilled += warmup * delta();

			float delay = getDrillTime(dominantItem);

			if (items.total() < itemCapacity && dominantItems > 0 && efficiency > 0) {
				float powerFactor = power == null || consPower == null ? 1f : power.graph.getPowerBalance() / (minPowerNeed * consPower.requestedPower(this));
				float finalFactor = (float) Math.sqrt(Math.min(Math.max(powerFactor, 1f), maxFactor));

				float speed = Mathf.lerp(1f, liquidBoostIntensity, optionalEfficiency) * efficiency * finalFactor;
				//float speed = Mathf.lerp(1f, liquidBoostIntensity, optionalEfficiency) * efficiency;

				lastDrillSpeed = (speed * dominantItems * warmup) / delay;
				warmup = Mathf.approachDelta(warmup, speed, warmupSpeed);
				progress += delta() * dominantItems * speed * warmup;

				if (Mathm.chanceDelta(updateEffectChance * warmup))
					updateEffect.at(x + Mathf.range(size * 2f), y + Mathf.range(size * 2f));
			} else {
				lastDrillSpeed = 0f;
				warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
				return;
			}

			if (dominantItems > 0 && progress >= delay && items.total() < itemCapacity) {
				int amount = (int) (progress / delay);
				for (int i = 0; i < amount; i++) {
					offload(dominantItem);
				}

				progress %= delay;

				if (wasVisible && Mathm.chanceDelta(drillEffectChance * warmup)) drillEffect.at(x + Mathf.range(drillEffectRnd), y + Mathf.range(drillEffectRnd), dominantItem.color);
			}
		}
	}
}
