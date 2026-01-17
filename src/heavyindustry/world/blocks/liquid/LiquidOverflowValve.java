package heavyindustry.world.blocks.liquid;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.type.Liquid;
import mindustry.world.blocks.liquid.LiquidBlock;
import mindustry.world.meta.Stat;

public class LiquidOverflowValve extends LiquidBlock {
	public boolean invert = false;

	public LiquidOverflowValve(String name) {
		super(name);

		floating = true;
		canOverdrive = false;
		instantTransfer = true;
		liquidCapacity *= 1000f;
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.remove(Stat.liquidCapacity);
	}

	@Override
	public void setBars() {
		super.setBars();
		removeBar("liquid");
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{region};
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = LiquidOverfloatValveBuild::new;
	}

	public class LiquidOverfloatValveBuild extends Building {
		@Override
		public void draw() {
			Draw.rect(region, x, y);
		}

		/*@Override
		public boolean acceptLiquid(Building source, Liquid liquid) {
			Building to = getTileTarget(source, liquid);

			return to != null && to.team == team && to.acceptLiquid(this, liquid) && to.liquids.get(liquid) < to.block.liquidCapacity;
		}

		@Override
		public void handleLiquid(Building source, Liquid liquid, float amount) {
			Building target = getTileTarget(source, liquid);

			if (target != null) target.handleLiquid(this, liquid, amount);
		}

		public @Nullable Building getTileTarget(Building source, Liquid liquid) {
			if (!enabled) return null;

			int from = relativeToEdge(source.tile);
			if (from == -1) return null;

			Building to = nearby((from + 2) % 4);

			boolean fromInst = source.block.instantTransfer;
			boolean canForward = to != null && to.team == team && !(fromInst && to.block.instantTransfer) && to.acceptLiquid(this, liquid) && to.liquids.get(liquid) < to.block.liquidCapacity;

			if (!canForward || invert) {
				Building a = nearby(Mathf.mod(from - 1, 4));
				Building b = nearby(Mathf.mod(from + 1, 4));
				boolean ac = a != null && a.team == team && !(fromInst && a.block.instantTransfer) && a.acceptLiquid(this, liquid) && a.liquids.get(liquid) < a.block.liquidCapacity;
				boolean bc = b != null && b.team == team && !(fromInst && b.block.instantTransfer) && b.acceptLiquid(this, liquid) && b.liquids.get(liquid) < b.block.liquidCapacity;

				if (!ac && !bc) {
					return invert && canForward ? to : null;
				}

				if (ac && !bc) {
					to = a;
				} else if (!ac) {
					to = b;
				} else {
					to = (rotation & (1 << from)) == 0 ? a : b;
				}
			}

			return to instanceof LiquidOverfloatValveBuild valve ? valve.getLiquidDestination(this, liquid) : to;
		}*/

		@Override
		public Building getLiquidDestination(Building source, Liquid liquid) {
			if (!enabled) return this;

			int from = relativeToEdge(source.tile);
			if (from == -1) return this;

			Building to = nearby((from + 2) % 4);

			boolean canForward = to != null && to.acceptLiquid(this, liquid) && to.liquids.get(liquid) < to.block.liquidCapacity;

			if (!canForward || invert) {
				Building a = nearby(Mathf.mod(from - 1, 4));
				Building b = nearby(Mathf.mod(from + 1, 4));
				boolean ac = a != null && a.acceptLiquid(this, liquid) && !(a.block instanceof LiquidOverflowValve) && a.liquids.get(liquid) < a.block.liquidCapacity;
				boolean bc = b != null && b.acceptLiquid(this, liquid) && !(b.block instanceof LiquidOverflowValve) && b.liquids.get(liquid) < b.block.liquidCapacity;

				if (!ac && !bc) {
					return invert && canForward ? to : this;
				}

				if (ac && !bc) {
					to = a;
				} else if (!ac) {
					to = b;
				} else {
					to = (rotation & (1 << from)) == 0 ? a : b;
				}
			}

			return to.getLiquidDestination(this, liquid);
		}
	}
}
