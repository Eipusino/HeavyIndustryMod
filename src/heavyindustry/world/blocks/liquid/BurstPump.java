package heavyindustry.world.blocks.liquid;

import arc.*;
import arc.audio.*;
import arc.math.*;
import arc.util.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.production.*;

public class BurstPump extends Pump {
	public float pumpTime = 180;
	public float outputAmount = 100;
	public Sound pumpSound = Sounds.drillImpact;
	public float pumpSoundVolume = 0.6f, pumpSoundPitchRand = 0.1f;

	public BurstPump(String name) {
		super(name);
	}

	public void setBars() {
		super.setBars();

		addBar("pumpProgress", (BurstPumpBuild e) ->
				new Bar(() -> Core.bundle.get("bar.progress"), () -> Pal.ammo, () -> e.counter / pumpTime));
	}

	public class BurstPumpBuild extends PumpBuild {
		public float counter;
		public @Nullable Liquid liquidDrop = null;

		@Override
		public void onProximityUpdate() {
			super.onProximityUpdate();

			amount = 0f;
			liquidDrop = null;

			for (Tile other : tile.getLinkedTiles(tempTiles)) {
				if (canPump(other)) {
					liquidDrop = other.floor().liquidDrop;
					amount += outputAmount / (size * size);
				}
			}
		}

		@Override
		public void updateTile() {
			if (liquidDrop != null) {
				counter += edelta();
				dumpLiquid(liquidDrop);
				if (counter >= pumpTime) {
					float maxPump = Math.min(liquidCapacity - liquids.get(liquidDrop), amount);
					counter %= pumpTime;
					liquids.add(liquidDrop, maxPump);
					pumpSound.at(x, y, 1f + Mathf.range(pumpSoundPitchRand), pumpSoundVolume);
				}
			}
		}
	}
}
