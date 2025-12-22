package heavyindustry.world.consumers;

import arc.func.Func;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.LiquidStack;
import mindustry.ui.ReqImage;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;

public class ConsumeLiquidDynamic extends Consume {
	public final Func<Building, LiquidStack> liquids;

	@SuppressWarnings("unchecked")
	public <T extends Building> ConsumeLiquidDynamic(Func<T, LiquidStack> func) {
		liquids = (Func<Building, LiquidStack>) func;
	}

	@Override
	public void apply(Block block) {
		block.hasLiquids = true;
	}

	@Override
	public void build(Building build, Table table) {
		LiquidStack[] current = {liquids.get(build)};

		table.table(cont -> {
			table.update(() -> {
				if (current[0] != liquids.get(build)) {
					rebuild(build, cont);
					current[0] = liquids.get(build);
				}
			});

			rebuild(build, cont);
		});
	}

	private void rebuild(Building build, Table table) {
		table.clear();

		LiquidStack stack = liquids.get(build);
		table.add(new ReqImage(stack.liquid.uiIcon,
				() -> build.liquids != null && build.liquids.get(stack.liquid) > 0)).size(Vars.iconMed).padRight(8);
	}

	@Override
	public void update(Building build) {
		float mult = multiplier.get(build);

		LiquidStack stack = liquids.get(build);
		build.liquids.remove(stack.liquid, stack.amount * build.edelta() * mult);
	}

	@Override
	public float efficiency(Building build) {
		float ed = build.edelta();
		if (ed <= 0.00000001f) return 0f;
		float min = 1f;

		LiquidStack stack = liquids.get(build);
		min = Math.min(build.liquids.get(stack.liquid) / (stack.amount * ed * multiplier.get(build)), min);

		return min;
	}
}
