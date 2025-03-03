package heavyindustry.world.blocks.power;

import arc.Core;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.world.blocks.power.ThermalGenerator;

public class ThermalConsumeGenerator extends ThermalGenerator {
	public float itemDuration = 120f;

	public float warmupSpeed = 0.05f;
	public Effect consumeEffect = Fx.none;
	public float generateEffectRange = 3f;

	public ThermalConsumeGenerator(String name) {
		super(name);
		// it uses efficiency, which is already affected by whether the block is enabled
		noUpdateDisabled = false;
		displayEfficiency = false;
	}

	@Override
	public void setStats() {
		// man, why doesn't vanilla do this?
		stats.timePeriod = itemDuration;
		super.setStats();
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		super.drawPlace(x, y, rotation, valid);

		drawPlaceText(Core.bundle.formatFloat("bar.efficiency", sumAttribute(attribute, x, y) * 100f * displayEfficiencyScale, 1), x, y, valid);
	}

	public class ThermalConsumeGeneratorBuild extends ThermalGeneratorBuild {
		public float warmup, totalTime;

		@Override
		public void updateTile() {
			boolean valid = efficiency > 0f;

			warmup = Mathf.lerpDelta(warmup, valid ? 1f : 0f, warmupSpeed);
			totalTime += warmup * Time.delta;
			productionEfficiency = (sum + attribute.env()) * efficiency;

			if (valid && Mathf.chanceDelta(effectChance)) {
				generateEffect.at(x + Mathf.range(generateEffectRange), y + Mathf.range(generateEffectRange));
			}

			// items
			if (hasItems && valid && generateTime <= 0f) {
				consume();
				consumeEffect.at(x + Mathf.range(generateEffectRange), y + Mathf.range(generateEffectRange));
				generateTime = 1f;
			}

			if (outputLiquid != null) {
				float added = Math.min(productionEfficiency * delta() * outputLiquid.amount, liquidCapacity - liquids.get(outputLiquid.liquid));
				liquids.add(outputLiquid.liquid, added);
				dumpLiquid(outputLiquid.liquid);
			}

			generateTime -= delta() / itemDuration;
		}

		@Override
		public boolean consumeTriggerValid() {
			return generateTime > 0f;
		}

		@Override
		public float warmup() {
			return warmup;
		}

		@Override
		public float totalProgress() {
			return totalTime;
		}
	}
}
