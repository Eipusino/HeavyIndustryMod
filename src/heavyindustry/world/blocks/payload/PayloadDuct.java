package heavyindustry.world.blocks.payload;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.Nullable;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.io.TypeIO;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;

import static heavyindustry.util.Utils.splitLayers;

public class PayloadDuct extends PayloadBlock {
	public TextureRegion[][] regions;

	public PayloadDuct(String name) {
		super(name);
		update = true;
		acceptsPayload = true;
		outputsPayload = true;
		rotate = true;
		payloadSpeed = 2f;
	}

	@Override
	public void load() {
		super.load();
		regions = splitLayers(name + "-sheet", Math.max(region.height, region.width), 2);
	}

	public class PayloadDuctBuild extends PayloadBlockBuild<Payload> {
		public int tiling = 0;
		protected boolean out = false;
		protected @Nullable Building next;

		@Override
		public void updateTile() {
			super.updateTile();
			if (payload == null) return;

			if (out) {
				moveOutPayload();
			} else if (moveInPayload()) {
				out = true;
			}
		}

		@Override
		public boolean hasArrived() {
			return payVector.isZero(size * 4f);
		}

		@Override
		public boolean movePayload(Payload todump) {
			if (next != null && next.team == team && next.acceptPayload(this, todump)) {
				next.handlePayload(this, todump);
				out = false; //added just this
				return true;
			} else {
				return false;
			}
		}

		/*public boolean nextDuctMerge(int rotation) {
			Building the = nearby(Geometry.d4x(rotation), Geometry.d4y(rotation));
			return Mathf.within(the.x - x, the.y - y, 4f);
		}*/

		@Override
		public void onProximityUpdate() {
			noSleep();
			next = front();

			tiling = 0;

			for (int i = 0; i < 4; i++) {
				Building b = nearby(Geometry.d4x(i), Geometry.d4y(i));
				if (i == rotation || b != null && (b instanceof PayloadDuctBuild pay ? (pay.next == this && Mathf.within(pay.x - x, pay.y - y, 4f)) : b.block.outputsPayload)) {
					tiling |= (1 << i);
				}
			}
		}

		@Override
		public boolean canControlSelect(Unit unit) {
			return false;
		}

		@Override
		public void draw() { //todo draw
			Draw.z(Layer.blockUnder);
			//Draw.rect(regionLayers[0][tiling], x, y, 0f);

			drawPayload();

			Draw.z(35.1f);
			//Draw.rect(regionLayers[1][tiling], x, y, 0f);
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			TypeIO.writeBuilding(write, next);
			write.bool(out);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			next = TypeIO.readBuilding(read);
			out = read.bool();
		}
	}
}
