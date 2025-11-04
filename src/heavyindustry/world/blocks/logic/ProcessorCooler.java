package heavyindustry.world.blocks.logic;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Nullable;
import heavyindustry.math.Mathm;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.Liquid;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.blocks.logic.LogicBlock.LogicBuild;
import mindustry.world.consumers.ConsumeLiquidBase;
import mindustry.world.consumers.ConsumeLiquidFilter;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

public class ProcessorCooler extends Block {
	public TextureRegion heatRegion, liquidRegion, topRegion;

	public boolean useTopRegion = false; //set automatically
	public Color heatColor = Pal.turretHeat;
	public boolean acceptCoolant = false;
	public int boost = 2;
	public int maxProcessors = 2;

	public float maxBoost = 5;

	public @Nullable ConsumeLiquidBase liquidConsumer;

	public ProcessorCooler(String name) {
		super(name);
		update = true;
		solid = true;
		rotate = false;
		canOverdrive = false;
	}

	@Override
	public void init() {
		liquidConsumer = findConsumer(c -> c instanceof ConsumeLiquidBase);

		if (acceptCoolant && liquidConsumer == null) {
			liquidConsumer = consume(new ConsumeLiquidFilter(l -> l.temperature <= 0.5f && l.flammability < 0.1f && !l.gas, 0.15f));
		}

		super.init();
	}

	@Override
	public void load() {
		super.load();
		heatRegion = Core.atlas.find(name + "-heat", region);
		if (liquidConsumer != null) {
			liquidRegion = Core.atlas.find(name + "-liquid");
		}
		topRegion = Core.atlas.find(name + "-top");
		useTopRegion = Core.atlas.isFound(topRegion);
	}

	@Override
	public TextureRegion[] icons() {
		if (useTopRegion) return new TextureRegion[]{region, topRegion};
		return super.icons();
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.add(Stat.output, "[orange]@[] \uF7E4", maxProcessors);
		stats.add(Stat.speedIncrease, boost * 100, StatUnit.percent);
	}

	@Override
	public void setBars() {
		super.setBars();
		addBar("boost", (ProcessorCoolerBuild tile) -> new Bar(() -> Core.bundle.format("bar.boost", tile.realBoost() * 100), () -> Pal.accent, () -> tile.realBoost() / maxBoost));
		addBar("links", (ProcessorCoolerBuild tile) -> new Bar(() -> Core.bundle.format("bar.hi-coolprocs", tile.usedLinks, maxProcessors), () -> Pal.ammo, () -> tile.heat));
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = ProcessorCoolerBuild::new;
	}

	public class ProcessorCoolerBuild extends Building {
		public float heat = 0;
		public int usedLinks = 0;

		public int realBoost() {
			if (enabled && canConsume() && efficiency > 0.8f) {
				if (acceptCoolant) {
					Liquid liquid = liquids.current();
					return Math.max(1, Mathf.round((1f + liquid.heatCapacity) * boost));
				}
				return boost;
			}
			return 1;
		}

		@Override
		public void updateTile() {
			int count = 0;

			int b = realBoost();
			if (b >= 2) for (Building p : proximity) {
				if (count >= maxProcessors) break;
				if (p instanceof LogicBuild) {
					for (int i = 0; i < b - 1; i++) p.updateTile();
					count++;
				}
			}
			usedLinks = count;
			heat = Mathf.lerpDelta(heat, Mathm.clamp(((float) count) / maxProcessors), 0.03f);
		}

		@Override
		public void draw() {
			super.draw();
			if (liquids != null) {
				if (liquids.currentAmount() > 0.01f)
					LiquidBlock.drawTiledFrames(size, x, y, 0f, 0f, 0f, 0f, liquids.current(), liquids.get(liquids.current()) / liquidCapacity);
			}
			if (useTopRegion) Draw.rect(topRegion, x, y);
			if (heat > 0.01f) {
				Draw.blend(Blending.additive);
				Draw.color(heatColor, heat * Mathf.absin(9f, 1f));
				Draw.rect(heatRegion, x, y);
				Draw.blend();
			}
			Draw.color();
		}
	}
}
