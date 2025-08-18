package heavyindustry.world.blocks.payload;

import arc.math.geom.Vec2;
import arc.struct.ObjectSet;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.world.blocks.production.GeneratorCrafter;
import mindustry.content.Fx;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.type.ItemStack;
import mindustry.type.PayloadSeq;
import mindustry.type.PayloadStack;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.units.UnitAssembler;

public class PayloadCrafter extends GeneratorCrafter {
	public ObjectSet<UnlockableContent> payloadFilter = new ObjectSet<>();

	public int payloadCapacity = 10;

	public PayloadStack[] outputPayloads;

	public PayloadCrafter(String name) {
		super(name);

		acceptsPayload = true;
		outputsPayload = true;
	}

	public class PayloadCrafterBuild extends GeneratorCrafterBuild {
		public PayloadSeq payloads = new PayloadSeq();

		@Override
		public PayloadSeq getPayloads() {
			return payloads;
		}

		@Override
		public boolean shouldConsume() {
			if (outputItems != null) {
				for (var output : outputItems) {
					if (items.get(output.item) + output.amount > itemCapacity) {
						return powerProduction > 0;
					}
				}
			}
			if (outputPayloads != null) {
				for (var output : outputPayloads) {
					if (getPayloads().get(output.item) + output.amount > payloadCapacity) {
						return powerProduction > 0;
					}
				}
			}
			if (outputLiquids != null && !ignoreLiquidFullness) {
				boolean allFull = true;
				for (var output : outputLiquids) {
					if (liquids.get(output.liquid) >= liquidCapacity - 0.001f) {
						if (!dumpExtraLiquid) {
							return false;
						}
					} else {
						//if there's still space left, it's not full for all liquids
						allFull = false;
					}
				}
				//if there is no space left for any liquid, it can't reproduce
				if (allFull) {
					return false;
				}
			}
			return enabled;
		}

		@Override
		public void craft() {
			super.craft();
			if (outputPayloads != null) {
				for (PayloadStack output : outputPayloads) {
					payloads.add(output.item, output.amount);
				}
			}
		}

		@Override
		public boolean acceptPayload(Building source, Payload payload) {
			return payloadFilter.contains(payload.content()) && getPayloads().get(payload.content()) < payloadCapacity;
		}

		@Override
		public void handlePayload(Building source, Payload payload) {
			payloads.add(payload.content(), 1);
			Fx.payloadDeposit.at(payload.x(), payload.y(), payload.angleTo(this), new UnitAssembler.YeetData(new Vec2(x, y), payload.content()));
		}

		@Override
		public void dumpOutputs() {
			boolean timer = timer(timerDump, dumpTime / timeScale);
			if (outputItems != null && timer) {
				for (ItemStack output : outputItems) {
					dump(output.item);
				}
			}

			if (outputPayloads != null && timer) {
				for (PayloadStack output : outputPayloads) {
					BuildPayload payload = new BuildPayload((Block) output.item, team);
					payload.set(x, y, rotdeg());
					dumpPayload(payload);
				}
			}

			if (outputLiquids != null) {
				for (int i = 0; i < outputLiquids.length; i++) {
					int dir = liquidOutputDirections.length > i ? liquidOutputDirections[i] : -1;

					dumpLiquid(outputLiquids[i].liquid, 2f, dir);
				}
			}
		}

		@Override
		public byte version() {
			return 1;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			payloads.write(write);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			if (revision == 1) {
				payloads.read(read);
			}
		}
	}
}
