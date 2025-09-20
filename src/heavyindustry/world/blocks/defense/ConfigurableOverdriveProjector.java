package heavyindustry.world.blocks.defense;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.scene.ui.TextField.TextFieldFilter;
import arc.scene.ui.layout.Table;
import arc.util.Eachable;
import arc.util.Strings;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.entities.units.BuildPlan;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.ui.Styles;
import mindustry.world.blocks.defense.OverdriveProjector;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;

import static mindustry.Vars.indexer;
import static mindustry.Vars.player;
import static mindustry.Vars.tilesize;

public class ConfigurableOverdriveProjector extends OverdriveProjector {
	protected static final Vec2 configs = new Vec2();

	public ConfigurableOverdriveProjector(String name) {
		super(name);

		envEnabled = Env.any;

		configurable = saveConfig = true;
		hasPower = hasItems = hasBoost = false;

		config(Vec2.class, (ConfigurableOverdriveBuild tile, Vec2 values) -> {
			tile.boost = values.x;
			tile.setRange = values.y;
		});
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.remove(Stat.speedIncrease);
		stats.remove(Stat.range);
		stats.remove(Stat.productionTime);
	}

	@Override
	public void setBars() {
		super.setBars();
		addBar("boost", (ConfigurableOverdriveBuild tile) -> new Bar(() -> Core.bundle.format("bar.boost", Mathf.round(tile.boost)), () -> Pal.accent, () -> 1));
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		//skip the stuff added in OverdriveProjector
		drawPotentialLinks(x, y);
		drawOverlay(x * tilesize + offset, y * tilesize + offset, rotation);
	}

	@Override
	public void drawDefaultPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) { //Don't run drawPlanConfig
		TextureRegion reg = getPlanRegion(plan, list);
		Draw.rect(reg, plan.drawx(), plan.drawy(), !rotate || !rotateDraw ? 0 : plan.rotation * 90);

		if (plan.worldContext && player != null && teamRegion != null && teamRegion.found()) {
			if (teamRegions[player.team().id] == teamRegion) Draw.color(player.team().color);
			Draw.rect(teamRegions[player.team().id], plan.drawx(), plan.drawy());
			Draw.color();
		}
	}

	@Override
	public void drawPlanConfig(BuildPlan plan, Eachable<BuildPlan> list) {
		float realRange = plan.config != null ? ((Vec2) plan.config).y : range;

		Drawf.dashCircle(plan.drawx(), plan.drawy(), realRange, baseColor);

		indexer.eachBlock(player.team(), plan.drawx(), plan.drawy(), realRange, other -> other.block.canOverdrive, other -> Drawf.selected(other, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f))));
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = ConfigurableOverdriveBuild::new;
	}

	public class ConfigurableOverdriveBuild extends OverdriveBuild {
		float boost = 100, setRange = range;

		@Override
		public void draw() {
			Draw.rect(region, x, y);

			float f = 1f - (Time.time / 100f) % 1f;

			Draw.color(baseColor, phaseColor, phaseHeat);
			Draw.alpha(heat * Mathf.absin(Time.time, 50f / Mathf.PI2, 1f) * 0.5f);
			Draw.rect(topRegion, x, y);
			Draw.alpha(1f);
			Lines.stroke((2f * f + 0.1f) * heat);

			float r = Math.max(0f, Mathf.clamp(2f - f * 2f) * size * tilesize / 2f - f - 0.2f), w = Mathf.clamp(0.5f - f) * size * tilesize;
			Lines.beginLine();
			for (int i = 0; i < 4; i++) {
				Lines.linePoint(x + Geometry.d4(i).x * r + Geometry.d4(i).y * w, y + Geometry.d4(i).y * r - Geometry.d4(i).x * w);
				if (f < 0.5f)
					Lines.linePoint(x + Geometry.d4(i).x * r - Geometry.d4(i).y * w, y + Geometry.d4(i).y * r + Geometry.d4(i).x * w);
			}
			Lines.endLine(true);

			Draw.reset();
		}

		@Override
		public void updateTile() {
			smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.08f);
			heat = Mathf.lerpDelta(heat, efficiency > 0 ? 1f : 0f, 0.08f);
			charge += heat * Time.delta;

			if (hasBoost) {
				phaseHeat = Mathf.lerpDelta(phaseHeat, optionalEfficiency, 0.1f);
			}

			if (charge >= reload) {
				charge = 0f;
				indexer.eachBlock(this, setRange, other -> other.block.canOverdrive, other -> other.applyBoost(realBoost(), reload + 1f));
			}

			if (efficiency > 0) {
				useProgress += delta();
			}

			if (useProgress >= useTime) {
				consume();
				useProgress %= useTime;
			}
		}

		@Override
		public void drawSelect() {
			indexer.eachBlock(this, setRange, other -> other.block.canOverdrive, other -> Drawf.selected(other, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f))));

			Drawf.dashCircle(x, y, setRange, baseColor);
		}

		@Override
		public void buildConfiguration(Table table) {
			table.table(Styles.black5, t -> {
				t.margin(6f);
				t.add("+");
				t.field(String.valueOf(boost), text -> {
					float newBoost = boost;
					if (Strings.canParsePositiveFloat(text)) {
						newBoost = Strings.parseFloat(text);
					}
					configure(configs.set(newBoost, setRange));
				}).width(120).get().setFilter(TextFieldFilter.floatsOnly);
				t.add(Core.bundle.get("unit.percent")).left();

				t.row();
				t.add();
				t.field(String.valueOf(setRange / 8f), text -> {
					float newRange = setRange;
					if (Strings.canParsePositiveFloat(text)) {
						newRange = Strings.parseFloat(text) * 8f;
					}
					configure(configs.set(boost, newRange));
				}).width(120).get().setFilter(TextFieldFilter.floatsOnly);
				t.add(Core.bundle.get("unit.blocks")).left();
			});
		}

		@Override
		public Object config() {
			return configs.set(boost, setRange).cpy();
		}

		@Override
		public float realBoost() {
			return (boost + 100) / 100;
		}

		@Override
		public float range() {
			return setRange;
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.f(boost);
			write.f(setRange);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			boost = read.f();
			setRange = read.f();
		}
	}
}
