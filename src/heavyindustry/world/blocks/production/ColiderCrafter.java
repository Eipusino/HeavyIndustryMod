package heavyindustry.world.blocks.production;

import arc.math.Mathf;
import arc.util.Time;
import heavyindustry.world.meta.HStat;
import mindustry.world.blocks.production.GenericCrafter;

public class ColiderCrafter extends GenericCrafter {
	// what chance it to produce item/liquid. 1 = 100%
	public double produceChance = 0.5f;

	public ColiderCrafter(String name) {
		super(name);
	}

	@Override
	public void setStats() {
		stats.add(HStat.produceChance, produceChance * 100f + "%");

		super.setStats();
	}

	public boolean chance(double produceChance) {
		return Mathf.chance(produceChance);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = ColiderCrafterBuild::new;
	}

	public class ColiderCrafterBuild extends GenericCrafterBuild {
		@Override
		public void updateTile() {
			if (efficiency > 0) {

				progress += getProgressIncrease(craftTime);
				warmup = Mathf.approachDelta(warmup, warmupTarget(), warmupSpeed);

				if (outputLiquids != null) {
					float inc = getProgressIncrease(1f);
					for (var output : outputLiquids) {
						handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
					}
				}

				if (wasVisible && Mathf.chanceDelta(updateEffectChance)) {
					updateEffect.at(x + Mathf.range(size * updateEffectSpread), y + Mathf.range(size * updateEffectSpread));
				}
			} else {
				warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
			}
			totalProgress += warmup * Time.delta;

			if (progress >= 1f) {
				boolean chanced = Mathf.chance(produceChance);
				if (chanced) {
					craft();
				}
			}
			boolean chanced = Mathf.chance(produceChance);
			if (chanced) {
				dumpOutputs();
			}
		}
	}
}
