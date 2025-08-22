package heavyindustry.world.consumers;

import arc.Core;
import arc.scene.ui.layout.Table;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.ui.Bar;
import mindustry.ui.ReqImage;
import mindustry.world.Block;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;
import mindustry.world.meta.Stats;

public class ConsumeItemsUses extends ConsumeItems {
	public int uses;

	public ConsumeItemsUses(int use, ItemStack[] items) {
		super(items);
		uses = use;
	}

	@Override
	public void build(Building build, Table table) {
		table.table(c -> {
			int i = 0;
			for (ItemStack stack : items) {
				c.add(new ReqImage(StatValues.stack(stack.item, Math.round(stack.amount * multiplier.get(build))),
						() -> (build instanceof UseCounter cbuild && cbuild.getUses() > 0) || build.items.has(stack.item, Math.round(stack.amount * multiplier.get(build))))).padRight(8);
				if (++i % 4 == 0) c.row();
			}
		}).left();
	}

	@Override
	public void trigger(Building build) {
		if (build instanceof UseCounter cbuild) {
			if (cbuild.getUses() > 0) {
				cbuild.removeUses(1);
			} else if (build.items.has(items, multiplier.get(build))) {
				cbuild.addUses(uses);
				super.trigger(build);
			}
		}
	}

	@Override
	public float efficiency(Building build) {
		return build instanceof UseCounter cbuild && cbuild.getUses() > 0 ? 1f : super.efficiency(build);
	}

	// this whole mod is so poorly coded holy fuck
	@Override
	public void apply(Block block) {
		super.apply(block);

		Core.app.post(() -> {
			block.addBar("uses", tile -> {
				if (tile instanceof UseCounter counter) {
					Item item = items[0].item;
					return new Bar(Core.bundle.format("bar.usage", item.localizedName), Pal.powerBar, () -> ((float) counter.getUses()) / uses);
				}

				// how.
				return null;
			});
		});
	}

	@Override
	public void display(Stats stats) {
		float timePeriod = stats.timePeriod;
		if (timePeriod > 0f) {
			stats.timePeriod *= (uses + 1);
		}
		super.display(stats);
		stats.timePeriod = timePeriod;
		stats.add(booster ? Stat.booster : Stat.input, "[lightgray]" + Core.bundle.format("stat.consumeuses", uses + 1));
	}

	public interface UseCounter {
		int getUses();

		void setUses(int uses);

		default void addUses(int uses) {
			setUses(getUses() + uses);
		}

		default void removeUses(int uses) {
			setUses(getUses() - uses);
		}
	}
}
