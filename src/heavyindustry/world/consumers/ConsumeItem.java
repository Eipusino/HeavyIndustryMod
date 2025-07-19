package heavyindustry.world.consumers;

import arc.scene.ui.layout.Table;
import mindustry.gen.Building;
import mindustry.type.ItemStack;
import mindustry.ui.ReqImage;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;
import mindustry.world.meta.Stats;

public class ConsumeItem extends Consume {
	public ItemStack stack;

	public ConsumeItem(ItemStack item) {
		stack = item;
	}

	/** Mods. */
	protected ConsumeItem() {}

	@Override
	public void apply(Block block) {
		block.hasItems = true;
		block.acceptsItems = true;
		block.itemFilter[stack.item.id] = true;
	}

	@Override
	public void build(Building build, Table table) {
		table.table(c -> c.add(new ReqImage(StatValues.stack(stack.item, Math.round(stack.amount * multiplier.get(build))),
				() -> build.items.has(stack.item, Math.round(stack.amount * multiplier.get(build))))).padRight(8)).left();
	}

	@Override
	public void trigger(Building build) {
		build.items.remove(stack.item, Math.round(stack.amount * multiplier.get(build)));
	}

	@Override
	public float efficiency(Building build) {
		return build.consumeTriggerValid() || build.items.has(stack.item, Math.round(stack.amount * multiplier.get(build))) ? 1f : 0f;
	}

	@Override
	public void display(Stats stats) {
		stats.add(booster ? Stat.booster : Stat.input, stats.timePeriod < 0 ? StatValues.items(stack) : StatValues.items(stats.timePeriod, stack));
	}
}
