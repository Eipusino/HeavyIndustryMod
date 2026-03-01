package endfield.world.blocks.production;

import arc.math.Mathf;
import endfield.world.consumers.ConsumePowerMultiplier;
import mindustry.type.Item;
import mindustry.world.Tile;
import mindustry.world.blocks.production.BeamDrill;
import mindustry.world.consumers.Consume;
import mindustry.world.consumers.ConsumePower;

public class BeamDrill2 extends BeamDrill {
	public BeamDrill2(String name) {
		super(name);
	}

	@Override
	public void init() {
		for (Consume c : consumeBuilder) c.multiplier = b -> {
			int i = 0;
			Tile[] tiles = ((BeamDrillBuild) b).facing;
			for (Tile tile : tiles) {
				if (tile != null && tile.wallDrop() != null) ++i;
			}
			return (float) i / tiles.length;
		};

		super.init();
	}

	@Override
	public ConsumePower consumePower(float powerPerTick) {
		return consume(new ConsumePowerMultiplier(powerPerTick, 0f, false));
	}

	public class BeamDrillBuild2 extends BeamDrillBuild {
		@Override
		public void updateTile() {
			if (lasers[0] == null) updateLasers();

			if (timer(timerDump, dumpTime / timeScale)) {
				dump();
			}

			warmup = Mathf.approachDelta(warmup, Mathf.num(efficiency > 0), 1f / 60f);

			updateFacing();

			float multiplier = Mathf.lerp(1f, optionalBoostIntensity, optionalEfficiency);
			float drillTime = getDrillTime(lastItem);
			boostWarmup = Mathf.lerpDelta(boostWarmup, optionalEfficiency, 0.1f);
			lastDrillSpeed = (facingAmount * multiplier * timeScale) / drillTime;

			time += edelta() * multiplier;

			if (time >= drillTime) {
				for (Tile tile : facing) {
					Item drop = tile == null ? null : tile.wallDrop();
					if (items.total() < itemCapacity && drop != null) {
						offload(drop);
					}
				}
				time %= drillTime;
			}
		}
	}
}
