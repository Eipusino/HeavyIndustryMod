package heavyindustry.world.blocks.payload;

//import arc.graphics.*;
//import arc.graphics.g2d.*;
//import arc.math.*;
//import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.io.*;
//import heavyindustry.ui.*;
import mindustry.content.*;
import mindustry.entities.*;
//import mindustry.entities.units.*;
import mindustry.gen.*;
//import mindustry.graphics.*;
import mindustry.type.*;
//import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.payloads.*;
import mindustry.world.draw.*;
import mindustry.world.meta.*;

//import static mindustry.Vars.*;

/**
 * @deprecated Deprecated API, please use {@link PayloadCrafter}
 *
 * @since 1.0.6
 */
@Deprecated
@SuppressWarnings("unused")
public class MoreGenericCrafter extends PayloadBlock {
	public @Nullable ItemStack[] outputItems;
	public @Nullable LiquidStack[] outputLiquids;
	public @Nullable Block outputPayload;
	public @Nullable Block inputPayload;

	public boolean dumpExtraLiquid = false;
	public boolean ignoreLiquidFullness = false;
	public int[] liquidOutputDirections = {-1};

	public float craftTime = 60f;
	public Effect craftEffect = Fx.none;
	public Effect updateEffect = Fx.none;
	public float updateEffectChance = 0.04f;
	public float warmupSpeed = 0.019f;

	public DrawBlock drawer = new DrawDefault();

	public MoreGenericCrafter(String name) {
		super(name);
		update = true;
		solid = true;
		hasItems = true;
		ambientSound = Sounds.machine;
		sync = true;
		ambientSoundVolume = 0.03f;
		flags = EnumSet.of(BlockFlag.factory);
		rotate = true;
	}

	/*public static StatValue payload(Block pay) {
		return (table) -> {
			table.add(new ItemImage(pay.uiIcon, 1));
			table.add(pay.localizedName).padLeft(2f).padRight(5f).color(Color.lightGray).style(Styles.outlineLabel);
		};
	}

	@Override
	public void setStats() {
		stats.timePeriod = craftTime;
		super.setStats();
		stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);

		if (outputItems != null) {
			stats.add(Stat.output, StatValues.items(craftTime, outputItems));
		}

		if (outputLiquids != null) {
			stats.add(Stat.output, StatValues.liquids(1f, outputLiquids));
		}

		if (outputPayload != null) {
			stats.add(Stat.output, payload(outputPayload));
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
			for (LiquidStack stack : outputLiquids) {
				addLiquidBar(stack.liquid);
			}
		}

		addBar("progress", (MoreGenericCrafterBuild tile) -> new Bar("bar.progress", Pal.ammo, () ->
				(tile.payload == null || tile.payload.block() != outputPayload) ? tile.progress : 1f));
	}

	@Override
	public boolean rotatedOutput(int x, int y) {
		return outputPayload != null;
	}

	@Override
	public void load() {
		super.load();

		drawer.load(this);
	}

	@Override
	public void init() {
		if (outputLiquids != null) outputsLiquid = true;
		if (outputPayload != null) outputsPayload = true;
		if (outputItems != null) hasItems = true;
		if (outputLiquids != null) hasLiquids = true;

		super.init();
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		drawer.drawPlan(this, plan, list);
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
	public TextureRegion[] icons() {
		return drawer.finalIcons(this);
	}

	@Override
	public boolean outputsItems() {
		return outputItems != null;
	}

	@Override
	public void getRegionsToOutline(Seq<TextureRegion> out) {
		drawer.getRegionsToOutline(this, out);
	}*/

	@Deprecated
	public class MoreGenericCrafterBuild extends PayloadBlockBuild<BuildPayload> {
		public float progress;
		public float totalProgress;
		public float warmup;

		/*@Override
		public boolean shouldActiveSound() {
			return shouldConsume();
		}

		@Override
		public boolean acceptPayload(Building source, Payload payload) {
			return this.payload == null; //&& inputPayload != null && ((BuildPayload)payload).block() == inputPayload && shouldConsume();
		}

		@Override
		public boolean shouldConsume() {
			if (outputItems != null) {
				for (ItemStack output : outputItems) {
					if (items.get(output.item) + output.amount > itemCapacity) {
						return false;
					}
				}
			}
			if (outputLiquids != null && !ignoreLiquidFullness) {
				boolean allFull = true;
				for (LiquidStack output : outputLiquids) {
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
			if (outputPayload != null && payload != null && payload.block() == outputPayload) {
				return false;
			}

			return enabled;
		}

		@Override
		public void updateTile() {
			if (efficiency > 0 && moveInPayload()) {

				progress += getProgressIncrease(craftTime);
				warmup = Mathf.approachDelta(warmup, 1, warmupSpeed);

				//continuously output based on efficiency
				if (outputLiquids != null) {
					float inc = getProgressIncrease(1f);
					for (LiquidStack output : outputLiquids) {
						handleLiquid(this, output.liquid, Math.min(output.amount * inc, liquidCapacity - liquids.get(output.liquid)));
					}
				}

				if (wasVisible && Mathf.chanceDelta(updateEffectChance)) {
					updateEffect.at(x + Mathf.range(size * 4f), y + Mathf.range(size * 4));
				}
			} else {
				warmup = Mathf.approachDelta(warmup, 0f, warmupSpeed);
			}

			totalProgress += warmup * Time.delta;

			if (progress >= 1f) {
				craft();
			}

			dumpOutputs();

			if (payload != null && payload.block() == outputPayload) {
				moveOutPayload();
			}
		}

		public void craft() {
			consume();

			if (outputItems != null) {
				for (ItemStack output : outputItems) {
					for (int i = 0; i < output.amount; i++) {
						offload(output.item);
					}
				}
			}

			if (outputPayload != null) payload = new BuildPayload(outputPayload, team);
			else payload = null;

			if (wasVisible) {
				craftEffect.at(x, y);
			}
			progress %= 1f;
		}

		public void dumpOutputs() {
			if (outputItems != null && timer(timerDump, dumpTime / timeScale)) {
				for (ItemStack output : outputItems) {
					dump(output.item);
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
		public void draw() {
			drawer.draw(this);
			drawPayload();
		}

		@Override
		public void drawLight() {
			super.drawLight();
			drawer.drawLight(this);
		}

		@Override
		public void handlePayload(Building source, Payload payload) {
			super.handlePayload(source, payload);
		}*/

		@Override
		public float progress() {
			return progress;
		}

		@Override
		public float totalProgress() {
			return totalProgress;
		}

		@Override
		public float warmup() {
			return warmup;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.f(progress);
			write.f(warmup);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			progress = read.f();
			warmup = read.f();
		}
	}
}
