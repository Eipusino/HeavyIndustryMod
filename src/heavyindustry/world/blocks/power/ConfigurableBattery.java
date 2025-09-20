package heavyindustry.world.blocks.power;

import arc.Core;
import arc.math.Mathf;
import arc.scene.ui.TextField.TextFieldFilter;
import arc.scene.ui.layout.Table;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.ui.Elements;
import heavyindustry.util.Utils;
import heavyindustry.world.consumers.ConsumeBufferedPowerDynamic;
import mindustry.gen.Icon;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.blocks.power.Battery;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawPower;
import mindustry.world.draw.DrawRegion;
import mindustry.world.meta.Env;
import mindustry.world.meta.StatUnit;

public class ConfigurableBattery extends Battery {
	protected static float powerCapacitySetting;

	public float initialPowerCapacity = 1000f;

	public ConfigurableBattery(String name) {
		super(name);

		envEnabled = Env.any;

		configurable = saveConfig = true;
		update = true;

		drawer = new DrawMulti(new DrawDefault(), new DrawPower(), new DrawRegion("-top"));

		consume(new ConsumeBufferedPowerDynamic((ConfigurableBatteryBuild tile) -> tile.powerCapacity));

		config(Float.class, (ConfigurableBatteryBuild tile, Float capacity) -> {
			float amount = tile.powerCapacity * tile.power.status;
			tile.powerCapacity = capacity;
			tile.block.consPower.update(tile);
			tile.power.status = Math.min(amount, tile.powerCapacity) / tile.powerCapacity;
		});
		config(Boolean.class, (ConfigurableBatteryBuild tile, Boolean ignored) -> tile.power.status = 0);
	}

	@Override
	public void setBars() {
		super.setBars();

		if (consPower instanceof ConsumeBufferedPowerDynamic dynamic) {
			removeBar("power");
			addBar("power", tile -> new Bar(() -> {
						float capacity = dynamic.getPowerCapacity(tile);
						return Core.bundle.format("bar.poweramount", Float.isNaN(tile.power.status * capacity) ? "<ERROR>" : Elements.round(tile.power.status * capacity)) + "/" + Elements.round(capacity);
					}, () -> Pal.powerBar, () -> Mathf.zero(consPower.requestedPower(tile)) && tile.power.graph.getPowerProduced() + tile.power.graph.getBatteryStored() > 0f ? 1f : tile.power.status)
			);
		}
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = ConfigurableBatteryBuild::new;
	}

	public class ConfigurableBatteryBuild extends BatteryBuild {
		public float powerCapacity = initialPowerCapacity;

		@Override
		public void buildConfiguration(Table table) {
			powerCapacitySetting = powerCapacity;
			table.table(Styles.black5, t -> {
				t.margin(6f);
				t.field(String.valueOf(powerCapacitySetting), text -> {
					powerCapacitySetting = Strings.parseFloat(text);
				}).width(120).valid(text -> Strings.canParseFloat(text) && Strings.parseFloat(text) > 0).get().setFilter(TextFieldFilter.floatsOnly);
				t.add(Utils.statUnitName(StatUnit.powerUnits)).left();
				t.button(Icon.save, () -> configure(powerCapacitySetting)).padLeft(6);
				t.button(Icon.trash, () -> configure(false)).tooltip("@hi-storage.delete-contents");
			});
		}

		@Override
		public Object config() {
			return powerCapacity;
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.f(powerCapacity);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			powerCapacity = read.f();
		}
	}
}
