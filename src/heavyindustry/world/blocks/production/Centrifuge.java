package heavyindustry.world.blocks.production;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.util.Nullable;
import heavyindustry.content.HFx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Tile;
import mindustry.world.blocks.liquid.Conduit.ConduitBuild;
import mindustry.world.blocks.production.Separator;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class Centrifuge extends Separator {
	/** Written to outputLiquids as a single-element array if outputLiquids is null. */
	public @Nullable LiquidStack outputLiquid;
	/** Overwrites outputLiquid if not null. */
	public @Nullable LiquidStack[] outputLiquids;
	/** Liquid output directions, specified in the same order as outputLiquids. Use -1 to dump in every direction. Rotations are relative to block. */
	public int[] liquidOutputDirections = {-1};

	public Effect fullEffect = HFx.centrifugeFull;

	/** if true, crafters with multiple liquid outputs will dump excess when there's still space for at least one liquid type */
	public boolean dumpExtraLiquid = true;
	public boolean ignoreLiquidFullness = false;

	public Centrifuge(String name) {
		super(name);
	}

	@Override
	public void setStats() {
		super.setStats();

		if (outputLiquids != null) {
			stats.add(Stat.output, StatValues.liquids(1f, outputLiquids));
		}
	}

	@Override
	public void setBars() {
		super.setBars();

		//set up liquid bars for liquid outputs
		if (outputLiquids != null && outputLiquids.length > 0) {
			//no need for dynamic liquid bar
			removeBar("liquid");

			//then display output buffer
			for (var stack : outputLiquids) {
				addLiquidBar(stack.liquid);
			}
		}
	}

	@Override
	public boolean rotatedOutput(int fromX, int fromY, Tile destination) {
		if (!(destination.build instanceof ConduitBuild)) return false;

		Building crafter = world.build(fromX, fromY);
		if (crafter == null) return false;
		int relative = Mathf.mod(crafter.relativeTo(destination) - crafter.rotation, 4);
		for (int dir : liquidOutputDirections) {
			if (dir == -1 || dir == relative) return false;
		}

		return true;
	}

	@Override
	public void init() {
		if (outputLiquids == null && outputLiquid != null) {
			outputLiquids = new LiquidStack[]{outputLiquid};
		}
		//write back to outputLiquid, as it helps with sensing
		if (outputLiquid == null && outputLiquids != null && outputLiquids.length > 0) {
			outputLiquid = outputLiquids[0];
		}
		outputsLiquid = outputLiquids != null;

		if (outputLiquids != null) hasLiquids = true;

		super.init();
	}

	@Override
	public void drawOverlay(float x, float y, int rotation) {
		if (outputLiquids != null) {
			for (int i = 0; i < outputLiquids.length; i++) {
				int dir = liquidOutputDirections.length > i ? liquidOutputDirections[i] : -1;

				if (dir != -1) {
					Draw.rect(
							outputLiquids[i].liquid.fullIcon,
							x + Geometry.d4x(dir + rotation) * (size * tilesize / 2f + 4),
							y + Geometry.d4y(dir + rotation) * (size * tilesize / 2f + 4),
							8f, 8f
					);
				}
			}
		}
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = CentrifugeBuild::new;
	}

	public class CentrifugeBuild extends SeparatorBuild {
		@Override
		public boolean shouldConsume() {
			int total = items.total();
			//very inefficient way of allowing separators to ignore input buffer storage
			if (consItems != null) {
				for (ItemStack stack : consItems.items) {
					total -= items.get(stack.item);
				}
			}
			boolean allFull = false;
			if (outputLiquids != null && !ignoreLiquidFullness) {
				for (var output : outputLiquids) {
					if (liquids.get(output.liquid) >= liquidCapacity - 0.001f) {
						if (!dumpExtraLiquid) {
							allFull = true;
						}
					} else {
						//if there's still space left, it's not full for all liquids
						allFull = true;
					}
				}
			}

			return total < itemCapacity && allFull && enabled;
		}

		@Override
		public void updateTile() {
			totalProgress += warmup * delta();

			if (efficiency > 0) {
				progress += getProgressIncrease(craftTime);
				warmup = Mathf.lerpDelta(warmup, 1f, 0.02f);

				//continuously output based on efficiency
				if (outputLiquids != null) {
					float inc = getProgressIncrease(1f);
					for (var output : outputLiquids) {
						handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
					}
				}
			} else {
				warmup = Mathf.lerpDelta(warmup, 0f, 0.02f);
			}

			if (progress >= 1f) {
				progress %= 1f;
				int sum = 0;
				for (ItemStack stack : results) sum += stack.amount;

				int i = Mathf.randomSeed(seed++, 0, sum - 1);
				int count = 0;
				Item item = null;

				//guaranteed desync since items are random - won't be fixed and probably isn't too important
				for (ItemStack stack : results) {
					if (i >= count && i < count + stack.amount) {
						item = stack.item;
						break;
					}
					count += stack.amount;
				}

				consume();

				if (item != null && items.get(item) < itemCapacity) {
					offload(item);
				}
			}

			if (timer(timerDump, dumpTime / timeScale)) {
				dump();
			}

			if (outputLiquids != null) {
				for (int i = 0; i < outputLiquids.length; i++) {
					if (liquids.get(outputLiquids[i].liquid) >= liquidCapacity && Mathf.chanceDelta(0.5f * edelta()))
						fullEffect.at(x, y, outputLiquids[i].liquid.color);
					int dir = liquidOutputDirections.length > i ? liquidOutputDirections[i] : -1;

					dumpLiquid(outputLiquids[i].liquid, 2f, dir);
				}
			}
		}

		@Override
		public float getProgressIncrease(float baseTime) {
			if (ignoreLiquidFullness) {
				return super.getProgressIncrease(baseTime);
			}

			//limit progress increase by maximum amount of liquid it can produce
			float scaling = 1f, max = 1f;
			if (outputLiquids != null) {
				max = 0f;
				for (var s : outputLiquids) {
					float value = (liquidCapacity - liquids.get(s.liquid)) / (s.amount * edelta());
					scaling = Math.min(scaling, value);
					max = Math.max(max, value);
				}
			}

			//when dumping excess take the maximum value instead of the minimum.
			return super.getProgressIncrease(baseTime) * (dumpExtraLiquid ? Math.min(max, 1f) : scaling);
		}
	}
}
