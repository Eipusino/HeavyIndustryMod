package heavyindustry.world.blocks.production;

import arc.math.Mathf;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.world.consumers.ConsumeItemsUses.UseCounter;
import mindustry.gen.Building;
import mindustry.type.Item;

public class ItemConsumerDrill extends DrawerDrill {
	public ItemConsumerDrill(String name) {
		super(name);
	}

	public class ItemConsumerDrillBuild extends DrawerDrillBuild implements UseCounter {
		public int uses;

		@Override
		public boolean shouldConsume() {
			return dominantItem != null && items.get(dominantItem) < itemCapacity && enabled;
		}

		@Override
		public boolean shouldAmbientSound() {
			return efficiency > 0.01f && dominantItem != null && items.get(dominantItem) < itemCapacity;
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

			if (items.get(dominantItem) < itemCapacity && dominantItems > 0 && efficiency > 0) {
				float speed = Mathf.lerp(1f, liquidBoostIntensity, optionalEfficiency) * efficiency;

				lastDrillSpeed = (speed * dominantItems * warmup) / delay;
				warmup = Mathf.approachDelta(warmup, speed, warmupSpeed);
				progress += delta() * dominantItems * speed * warmup;

				if (Mathf.chanceDelta(updateEffectChance * warmup))
					updateEffect.at(x + Mathf.range(size * 2f), y + Mathf.range(size * 2f));
			} else {
				lastDrillSpeed = 0f;
				warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
				return;
			}

			if (dominantItems > 0 && progress >= delay && items.get(dominantItem) < itemCapacity) {
				consume();
				offload(dominantItem);

				progress %= delay;

				if (wasVisible && Mathf.chanceDelta(updateEffectChance * warmup))
					drillEffect.at(x + Mathf.range(drillEffectRnd), y + Mathf.range(drillEffectRnd), dominantItem.color);
			}
		}

		@Override
		public boolean canDump(Building to, Item item) {
			// actually so terrible
			return !consumesItem(item) || (dominantItem == item && items.has(item, 2));
		}

		@Override
		public int getUses() {
			return uses;
		}

		@Override
		public void setUses(int use) {
			uses = use;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.i(uses);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			uses = read.i();
		}
	}
}
