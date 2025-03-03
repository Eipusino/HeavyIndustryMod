package heavyindustry.world.blocks.production;

import arc.Core;
import arc.math.Mathf;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.world.meta.HIStat;
import heavyindustry.world.meta.HIStatValues;
import mindustry.content.Items;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.Attribute;

/**
 * A smelter uses fuel to craft. Attribute tiles make it use less fuel.
 */
public class FuelCrafter extends GenericCrafter {
	public Attribute attribute = Attribute.heat;
	public Item fuelItem;

	public int fuelPerItem = 10, fuelPerCraft = -1;
	public int fuelCapacity = 30;
	/** 1 affinity = this amount removed from fuel use. */
	public float fuelUseReduction = 1.5f;

	public FuelCrafter(String name) {
		super(name);
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		int amount = Math.max(fuelPerCraft - Mathf.round(sumAttribute(attribute, x, y) * fuelUseReduction), 0);
		drawPlaceText(Core.bundle.format("bar.hi-fuel-use", amount > 0 ? amount : Core.bundle.get("bar.hi-fuel-unneeded")), x, y, valid);
	}

	@Override
	public void init() {
		if (fuelItem == null) {
			fuelItem = Items.coal;
		}

		if (fuelPerCraft < 0) fuelPerCraft = fuelPerItem;

		super.init();
	}

	@Override
	public void setBars() {
		super.setBars();
		addBar("hi-fuel", (FuelCrafterBuild tile) -> new Bar(
				() -> Core.bundle.format("bar.hi-fuel", tile.fuelNeeded() > 0 ? tile.fuel : Core.bundle.get("bar.hi-fuel-unneeded"), tile.fuelNeeded() > 0 ? ("/" + tile.fuelNeeded()) : ""),
				() -> fuelItem.color,
				() -> tile.fuelNeeded() > 0 ? (float) (tile.fuel / fuelCapacity) : 1f
		));
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.add(HIStat.fuel, HIStatValues.fuel(this));
	}

	public class FuelCrafterBuild extends GenericCrafterBuild {
		public int fuel;
		public float attrsum;

		@Override
		public boolean shouldConsume() {
			return fuel >= fuelNeeded() && super.shouldConsume();
		}

		@Override
		public void onProximityUpdate() {
			super.onProximityUpdate();

			attrsum = sumAttribute(attribute, tile.x, tile.y);
		}

		@Override
		public void consume() {
			fuel -= fuelNeeded();
			super.consume();
		}

		public int fuelNeeded() {
			return Math.max(fuelPerCraft - Mathf.round(attrsum * fuelUseReduction), 0);
		}

		@Override
		public boolean acceptItem(Building source, Item item) {
			return super.acceptItem(source, item) || item == fuelItem && fuel + fuelPerItem <= fuelCapacity && fuelNeeded() > 0f;
		}

		@Override
		public void handleItem(Building source, Item item) {
			if (item == fuelItem && fuel + fuelPerItem <= fuelCapacity && fuelNeeded() > 0f) {
				fuel += fuelPerItem;
				return;
			}

			super.handleItem(source, item);
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.i(fuel);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			fuel = read.i();
		}
	}
}
