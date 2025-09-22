package heavyindustry.world.blocks.defense;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.ui.TextField.TextFieldFilter;
import arc.scene.ui.layout.Table;
import arc.util.Eachable;
import arc.util.Strings;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.content.Fx;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.ui.Styles;
import mindustry.world.blocks.defense.MendProjector;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;

import static mindustry.Vars.indexer;
import static mindustry.Vars.player;
import static mindustry.Vars.tilesize;

public class ConfigurableMendProjector extends MendProjector {
	protected static final Vec2 configs = new Vec2();

	public ConfigurableMendProjector(String name) {
		super(name);

		envEnabled = Env.any;

		configurable = saveConfig = true;
		hasPower = hasItems = false;
		suppressable = false;

		config(Vec2.class, (ConfigurableMendBuild tile, Vec2 values) -> {
			tile.repairTime = values.x;
			tile.setRange = values.y;
		});
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.remove(Stat.repairTime);
		stats.remove(Stat.range);
		stats.remove(Stat.boostEffect);
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		//skip the stuff added in MendProjector
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

	float extractHealPercent(float repairTime) {
		if (repairTime == 0) return 1f; // No dividing by 0 on my watch.
		return 1f / (repairTime * 60f / reload);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = ConfigurableMendBuild::new;
	}

	public class ConfigurableMendBuild extends MendBuild {
		public float repairTime = 100f / healPercent * reload / 60f, setRange = range;

		@Override
		public void draw() {
			Draw.rect(region, x, y);

			float f = 1f - (Time.time / 100f) % 1f;

			Draw.color(baseColor, phaseColor, phaseHeat);
			Draw.alpha(heat * Mathf.absin(Time.time, 50f / Mathf.PI2, 1f) * 0.5f);
			Draw.rect(topRegion, x, y);
			Draw.alpha(1f);
			Lines.stroke((2f * f + 0.2f) * heat);
			Lines.square(x, y, Math.min(1f + (1f - f) * size * tilesize / 2f, size * tilesize / 2f));

			Draw.reset();
		}

		@Override
		public void updateTile() {
			smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.08f);
			heat = Mathf.lerpDelta(heat, efficiency > 0 ? 1f : 0f, 0.08f);
			charge += heat * delta();

			phaseHeat = Mathf.lerpDelta(phaseHeat, optionalEfficiency, 0.1f);

			if (optionalEfficiency > 0 && timer(timerUse, useTime)) {
				consume();
			}

			if (charge >= reload) {
				charge = 0f;

				float healPercent = extractHealPercent(repairTime);
				indexer.eachBlock(this, setRange, Building::damaged, other -> {
					other.heal(other.maxHealth() * healPercent * efficiency);
					other.recentlyHealed();
					Fx.healBlockFull.at(other.x, other.y, other.block.size, baseColor, other.block);
				});
			}
		}

		@Override
		public void drawSelect() {
			indexer.eachBlock(this, setRange, other -> true, other -> Drawf.selected(other, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f))));

			Drawf.dashCircle(x, y, setRange, baseColor);
		}

		@Override
		public void buildConfiguration(Table table) {
			table.table(Styles.black5, t -> {
				t.margin(6f);
				t.field(String.valueOf(repairTime), text -> {
					float newRepairTime = repairTime;
					if (Strings.canParsePositiveFloat(text)) {
						newRepairTime = Strings.parseFloat(text);
					}
					configure(configs.set(newRepairTime, setRange));
				}).width(120).get().setFilter(TextFieldFilter.floatsOnly);
				t.add(Core.bundle.get("unit.seconds")).left();

				t.row();
				t.field(String.valueOf(setRange / 8f), text -> {
					float newRange = setRange;
					if (Strings.canParsePositiveFloat(text)) {
						newRange = Math.min(Strings.parseFloat(text), 1000f) * 8f;
					}
					configure(configs.set(repairTime, newRange));
				}).width(120).get().setFilter(TextFieldFilter.floatsOnly);
				t.add(Core.bundle.get("unit.blocks")).left();
			});
		}

		@Override
		public float range() {
			return setRange;
		}

		@Override
		public Object config() {
			return configs.set(repairTime, setRange).cpy();
		}

		@Override
		public void write(Writes write) {
			super.write(write);

			write.f(repairTime);
			write.f(setRange);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);

			repairTime = read.f();
			setRange = read.f();
		}
	}
}
