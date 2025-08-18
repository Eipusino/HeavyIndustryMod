package heavyindustry.world.blocks.sandbox;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.consumers.*;
import mindustry.world.meta.Stat;

public class AdaptiveSource extends Block {
	public float powerProduction = 1000000f / 60f;
	public float heatOutput = 1000f;

	public Seq<Item> outputItems;
	public Seq<Liquid> outputLiquids;

	protected AdaptiveSource(String name) {
		super(name);

		hasItems = true;
		hasLiquids = true;
		hasPower = true;
		outputsPower = true;
		consumesPower = false;
		update = true;
		displayFlow = false;
		canOverdrive = true;
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.remove(Stat.itemCapacity);
		stats.remove(Stat.liquidCapacity);
	}

	@Override
	public void setBars() {
		addBar("health", entity -> new Bar("stat.health", Pal.health, entity::healthf).blink(Color.white));
	}

	@Override
	public boolean outputsItems() {
		return true;
	}

	public class AdaptiveSourceBuild extends Building implements HeatBlock {
		@Override
		public void updateTile() {
			if (proximity.isEmpty()) return;

			if (outputItems == null) outputItems = Vars.content.items();
			if (outputLiquids == null) outputLiquids = Vars.content.liquids();

			for (int i = 0; i < proximity.size; i++) {
				Building bd = proximity.get(i);
				if (bd != null && bd.shouldConsume() && bd.block != null && bd.block.consumers != null) {
					for (Consume c : bd.block.consumers) {
						if (c instanceof ConsumeItems ci) {
							ItemStack[] is = ci.items;
							for (ItemStack ik : is) {
								for (int a = 0; a < ik.amount; a++) {
									if (bd.acceptItem(this, ik.item)) {
										bd.handleItem(this, ik.item);
									}
								}
							}
						} else if (c instanceof ConsumeItemFilter cf) {
							for (Item it : outputItems) {
								if (cf.filter.get(it) && bd.acceptItem(this, it)) {
									bd.handleItem(this, it);
								}
							}
						} else if (c instanceof ConsumeLiquid cl) {
							if (bd.acceptLiquid(this, cl.liquid)) {
								bd.handleLiquid(this, cl.liquid, cl.amount * bd.block.liquidCapacity);
							}
						} else if (c instanceof ConsumeLiquids cls) {
							LiquidStack[] ls = cls.liquids;
							for (LiquidStack lk : ls) {
								if (bd.acceptLiquid(this, lk.liquid)) {
									bd.handleLiquid(this, lk.liquid, lk.amount * bd.block.liquidCapacity);
								}
							}
						} else if (c instanceof ConsumeLiquidFilter lf) {
							for (Liquid lq : outputLiquids) {
								if (lf.filter.get(lq) && bd.acceptLiquid(this, lq)) {
									bd.handleLiquid(this, lq, lf.amount * bd.block.liquidCapacity);
								}
							}
						} else if (c instanceof ConsumeItemDynamic cd) {
							ItemStack[] is = cd.items.get(bd);
							for (ItemStack ik : is) {
								for (int a = 0; a < ik.amount; a++) {
									if (bd.acceptItem(this, ik.item)) {
										bd.handleItem(this, ik.item);
									}
								}
							}
						} else if (c instanceof ConsumeLiquidsDynamic ld) {
							LiquidStack[] ls = ld.liquids.get(bd);
							for (LiquidStack lk : ls) {
								if (bd.acceptLiquid(this, lk.liquid)) {
									bd.handleLiquid(this, lk.liquid, lk.amount * bd.block.liquidCapacity);
								}
							}
						}
					}
				}
			}
		}

		@Override
		public float getPowerProduction() {
			return enabled ? powerProduction : 0f;
		}

		@Override
		public float heat() {
			return enabled ? heatOutput : 0f;
		}

		@Override
		public float heatFrac() {
			return enabled ? heatOutput : 0f;
		}
	}
}

