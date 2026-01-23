package endfield.world.blocks.payload;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.TextField.TextFieldFilter;
import arc.scene.ui.layout.Table;
import arc.util.Eachable;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import endfield.content.Fx2;
import endfield.util.Get;
import endfield.world.blocks.power.ConfigurableBattery;
import endfield.world.blocks.storage.ConfigurableContainer;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Styles;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.meta.Env;
import mindustry.world.meta.StatUnit;

public class CapacityConfigurer extends PayloadBlock {
	public CapacityConfigurer(String name) {
		super(name);

		envEnabled = Env.any;

		size = 3;
		outputsPayload = true;
		rotate = true;
		canOverdrive = false;
		configurable = saveConfig = true;

		config(Integer.class, (CapacityConfigurerBuild tile, Integer cap) -> tile.configItemCap = cap);
		config(Float.class, (CapacityConfigurerBuild tile, Float cap) -> tile.configBatteryCap = cap);
		config(Object[].class, (CapacityConfigurerBuild tile, Object[] config) -> {
			tile.configItemCap = (int) config[0];
			tile.configBatteryCap = (float) config[1];
		});
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{region, inRegion, outRegion, topRegion};
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		Draw.rect(region, plan.drawx(), plan.drawy());
		Draw.rect(inRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
		Draw.rect(outRegion, plan.drawx(), plan.drawy(), plan.rotation * 90);
		Draw.rect(topRegion, plan.drawx(), plan.drawy());
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = CapacityConfigurerBuild::new;
	}

	public class CapacityConfigurerBuild extends PayloadBlockBuild<Payload> {
		public boolean exporting;
		public int configItemCap = 1000;
		//public float configLiquidCap = 1000f;
		public float configBatteryCap = 1000f;

		@Override
		public void updateTile() {
			if (exporting) {
				moveOutPayload();
			} else if (moveInPayload()) {
				configPayload();
			}
		}

		@Override
		public void draw() {
			Draw.rect(region, x, y);

			//draw input
			boolean fallback = true;
			for (int i = 0; i < 4; i++) {
				if (blends(i) && i != rotation) {
					Draw.rect(inRegion, x, y, (i * 90) - 180);
					fallback = false;
				}
			}
			if (fallback) Draw.rect(inRegion, x, y, rotation * 90);

			Draw.rect(outRegion, x, y, rotdeg());

			drawPayload();

			Draw.z(Layer.blockOver + 0.1f);
			Draw.rect(topRegion, x, y);
		}

		@Override
		public void handlePayload(Building source, Payload payload) {
			super.handlePayload(source, payload);
			exporting = false;
		}

		public void configPayload() {
			if (payload instanceof BuildPayload p) {
				Color configColor = null;
				if (p.build instanceof ConfigurableContainer.ConfigurableContainerBuild c && c.storageCapacity != configItemCap) {
					c.configure(configItemCap);
					configColor = Pal.items;
				}
				if (p.build instanceof ConfigurableBattery.ConfigurableBatteryBuild b && b.powerCapacity != configBatteryCap) {
					b.configure(configBatteryCap);
					configColor = Pal.powerBar;
				}

				if (configColor != null) {
					Fx2.storageConfiged.at(p.build.x, p.build.y, p.block().size, configColor, p.build);
				}
			}

			exporting = true;
		}

		@Override
		public void buildConfiguration(Table table) {
			super.buildConfiguration(table);
			table.table(Styles.black6, t -> {
				t.defaults().left();
				t.margin(6f);
				t.add("@block.extra-sand-redux-configurable-container.name").colspan(2);
				t.row();
				t.field(String.valueOf(configItemCap), text -> {
					configure(Strings.parseInt(text));
				}).width(120).valid(Strings::canParsePositiveInt).padLeft(8f).get().setFilter(TextFieldFilter.digitsOnly);
				t.add(Get.statUnitName(StatUnit.items)).left();
				t.row();
				t.add("@block.extra-sand-redux-configurable-battery.name").colspan(2);
				t.row();
				t.field(String.valueOf(configBatteryCap), text -> {
					configure(Strings.parseFloat(text));
				}).width(120).valid(text -> Strings.canParseFloat(text) && Strings.parseFloat(text) > 0).padLeft(8f).get().setFilter(TextFieldFilter.floatsOnly);
				t.add(Get.statUnitName(StatUnit.powerUnits)).left();
			});
		}

		@Override
		public Object config() {
			return new Object[]{configItemCap, configBatteryCap};
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.bool(exporting);
			write.i(configItemCap);
			write.f(configBatteryCap);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			exporting = read.bool();
			configItemCap = read.i();
			configBatteryCap = read.f();
		}
	}
}
