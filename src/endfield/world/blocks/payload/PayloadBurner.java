package endfield.world.blocks.payload;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.draw.DrawBlock;

// i eata da payload
public class PayloadBurner extends PayloadBlock {
	public Block consumedBlock;
	public float burnDuration = 60f * 60f;

	public DrawBlock topDrawer;

	public PayloadBurner(String name) {
		super(name);
	}

	@Override
	public void load() {
		super.load();
		topDrawer.load(this);
	}

	@Override
	protected TextureRegion[] icons() {
		return new TextureRegion[]{region, inRegion, topRegion};
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = PayloadBurnerBuild::new;
	}

	public class PayloadBurnerBuild extends PayloadBlockBuild<BuildPayload> {
		public float warmup;
		public float burnTime;

		@Override
		public void updateTile() {
			if (moveInPayload() && burnTime <= 0f) {
				payload = null;

				burnTime = burnDuration;
			}

			if (burnTime > 0f) {
				burnTime -= delta();
				warmup = Mathf.approachDelta(warmup, 1f, 0.02f);
			} else {
				warmup = Mathf.approachDelta(warmup, 0f, 0.02f);
			}
		}

		@Override
		public boolean acceptPayload(Building source, Payload payload) {
			return this.payload == null && payload instanceof BuildPayload buildPayload && buildPayload.block() == consumedBlock;
		}

		@Override
		public boolean shouldConsume() {
			return burnTime > 0f;
		}

		@Override
		public float warmup() {
			return warmup;
		}

		@Override
		public void draw() {
			Draw.rect(region, x, y);

			//draw input
			for (int i = 0; i < 4; i++) {
				if (blends(i)) {
					Draw.rect(inRegion, x, y, (i * 90f) - 180f);
				}
			}

			Draw.z(Layer.blockOver);
			drawPayload();

			Draw.z(Layer.blockOver + 0.1f);
			topDrawer.draw(this);
			Draw.rect(topRegion, x, y);
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.f(burnTime);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			burnTime = read.f();
		}
	}
}
