package heavyindustry.world.blocks.heat;

import arc.graphics.g2d.TextureRegion;
import arc.scene.ui.TextField.TextFieldFilter;
import arc.scene.ui.layout.Table;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Strings;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.util.Utils;
import heavyindustry.world.draw.DrawStrobe;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.heat.HeatBlock;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawHeatOutput;
import mindustry.world.draw.DrawMulti;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.Env;
import mindustry.world.meta.StatUnit;

public class ConfigurableHeatSource extends Block {
	public DrawBlock drawer = new DrawMulti(new DrawDefault(), new DrawStrobe(), new DrawHeatOutput());

	public ConfigurableHeatSource(String name) {
		super(name);

		envDisabled = Env.any;

		configurable = saveConfig = true;

		update = true;
		solid = true;
		sync = true;
		flags = EnumSet.of(BlockFlag.factory);

		rotateDraw = false;
		rotate = true;
		canOverdrive = false;
		drawArrow = true;

		config(Float.class, (ConfigurableHeatSourceBuild tile, Float heat) -> tile.heat = heat);
	}

	@Override
	public void load() {
		super.load();

		drawer.load(this);
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		drawer.drawPlan(this, plan, list);
	}

	@Override
	public TextureRegion[] icons() {
		return drawer.finalIcons(this);
	}

	@Override
	public void getRegionsToOutline(Seq<TextureRegion> out) {
		drawer.getRegionsToOutline(this, out);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = ConfigurableHeatSourceBuild::new;
	}

	public class ConfigurableHeatSourceBuild extends Building implements HeatBlock {
		public float heat = 20;

		@Override
		public void draw() {
			drawer.draw(this);
		}

		@Override
		public void drawLight() {
			super.drawLight();
			drawer.drawLight(this);
		}

		@Override
		public void buildConfiguration(Table table) {
			table.table(Styles.black5, t -> {
				t.margin(6f);
				t.field(String.valueOf(heat), text -> {
					configure(Strings.parseFloat(text));
				}).width(120).valid(Strings::canParsePositiveFloat).get().setFilter(TextFieldFilter.floatsOnly);
				t.add(Utils.statUnitName(StatUnit.heatUnits)).left();
			});
		}

		@Override
		public Object config() {
			return heat;
		}

		@Override
		public float warmup() {
			return 1f;
		}

		@Override
		public float heatFrac() {
			return 1f;
		}

		@Override
		public float heat() {
			return heat;
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.f(heat);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			heat = read.f();
		}
	}
}
