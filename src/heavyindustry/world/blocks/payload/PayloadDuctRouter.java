package heavyindustry.world.blocks.payload;

import arc.util.Nullable;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.type.UnitType;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadRouter;
import mindustry.world.blocks.payloads.UnitPayload;

public class PayloadDuctRouter extends PayloadDuct {
	public PayloadDuctRouter(String name) {
		super(name);

		outputsPayload = true;
		outputFacing = false;
		configurable = true;
		clearOnDoubleTap = true;

		config(Block.class, (PayloadRouter.PayloadRouterBuild tile, Block item) -> tile.sorted = item);
		config(UnitType.class, (PayloadRouter.PayloadRouterBuild tile, UnitType item) -> tile.sorted = item);
		configClear((PayloadRouter.PayloadRouterBuild tile) -> tile.sorted = null);
	}

	public class PayloadDuctRouterBuild extends PayloadDuctBuild {
		public @Nullable UnlockableContent sorted;
		public int recDir;
		public boolean matches, blocked;
		public float[] blinkDurations = {0, 0, 0, 0}; //todo

		public void pickNext() {
			if (payload != null) {
				if (matches) {
					//when the item matches, always move forward.
					rotation = recDir;
					onProximityUpdate();
				} else {
					int rotations = 0;
					do {
						rotation = (rotation + 1) % 4;
						//if it doesn't match the sort item and this router is facing forward, skip this rotation
						if (!matches && sorted != null && rotation == recDir) {
							rotation++;
						}
						onProximityUpdate();
					} while ((blocked || next == null) && ++rotations < 4);
				}
			} else {
				onProximityUpdate();
			}
		}

		@Override
		public void handlePayload(Building source, Payload payload) {
			super.handlePayload(source, payload);
			checkMatch();
			pickNext();
		}

		@Override
		public void onProximityUpdate() {
			super.onProximityUpdate();
			blocked = (next != null && next.block.solid && !(next.block.outputsPayload || next.block.acceptsPayload)) || (next != null && next.payloadCheck(rotation));
		}

		public void checkMatch() {
			matches = sorted != null &&
					(payload instanceof BuildPayload build && build.block() == sorted) ||
					(payload instanceof UnitPayload unit && unit.unit.type == sorted);
		}
	}
}
