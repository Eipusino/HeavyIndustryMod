package heavyindustry.world.blocks.production;

import arc.Core;
import arc.func.Cons;
import arc.math.Interp;
import arc.math.Mathf;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.consumers.ConsumeItems;

public class AccelerationCrafter extends GenericCrafter {
	public float accelerationSpeed = 0.03f, decelerationSpeed = 0.05f;
	public Interp interp = Interp.smoother;

	public Cons<AcceleratingCrafterBuild> onCraft = tile -> {};

	public AccelerationCrafter(String name) {
		super(name);
	}

	@Override
	public void setBars() {
		super.setBars();

		addBar("craft-speed", (AcceleratingCrafterBuild tile) -> new Bar(() -> Core.bundle.format("bar.craft-speed", Mathf.round(tile.getDisplaySpeed() * 100f)), () -> Pal.surge, tile::getDisplaySpeed));
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = AcceleratingCrafterBuild::new;
	}

	public class AcceleratingCrafterBuild extends GenericCrafterBuild {
		public float speed;

		@Override
		public void updateTile() {
			float s = getSpeed();
			if (hasItems()) {
				progress += getProgressIncrease(craftTime) * s;
				totalProgress += delta() * s;
			}

			if (Mathf.chanceDelta(updateEffectChance * s)) {
				updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4));
			}

			if (canConsume()) {
				warmup = Mathf.approachDelta(warmup, 1f, warmupSpeed);
				float e = efficiency;
				if (speed <= e) {
					speed = Mathf.approachDelta(speed, e, accelerationSpeed * e);
				} else {
					speed = Mathf.approachDelta(speed, e, decelerationSpeed);
				}
			} else {
				warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
				speed = Mathf.approachDelta(speed, 0f, decelerationSpeed);
			}

			if (progress >= 1f) {
				craft();
				onCraft.get(this);
			}

			dumpOutputs();
		}

		public float getDisplaySpeed() {
			return hasItems() ? getSpeed() : 0f;
		}

		public float getSpeed() {
			return interp.apply(speed);
		}

		@Override
		public float getProgressIncrease(float baseTime) {
			return 1f / baseTime * delta();
		}

		public boolean hasItems() {
			ConsumeItems consumeItems = findConsumer(c -> c instanceof ConsumeItems);
			return consumeItems == null || consumeItems.efficiency(this) == 1;
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.f(speed);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			speed = read.f();
		}
	}
}
