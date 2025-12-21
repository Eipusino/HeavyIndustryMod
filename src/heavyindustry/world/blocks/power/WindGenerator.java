package heavyindustry.world.blocks.power;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;
import heavyindustry.graphics.Drawm;
import heavyindustry.math.Mathm;
import heavyindustry.type.weather.EffectWeather;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.game.Team;
import mindustry.gen.Groups;
import mindustry.gen.WeatherState;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.input.Placement;
import mindustry.world.Tile;
import mindustry.world.blocks.power.PowerGenerator;
import mindustry.world.meta.Env;
import mindustry.world.meta.StatUnit;

public class WindGenerator extends PowerGenerator {
	public int spacing = 9;
	public float boostWeather = 0.25f;
	public float baseRotateSpeed = 4f;
	public float rotChangeTime = Mathf.randomSeed(id, 80.0f, 300.0f);

	public WindGenerator(String name) {
		super(name);
		flags = EnumSet.of();
		envEnabled = Env.any;
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.remove(generationType);
		stats.add(generationType, powerProduction * 60.0f, StatUnit.powerSecond);
	}

	@Override
	public void drawOverlay(float x, float y, int rotation) {
		if (spacing < 1) return;
		float size = (spacing * 2 + this.size / 2f) * Vars.tilesize;
		x -= size / 2f;
		y -= size / 2f;
		Drawm.dashPoly(size / 8f, Pal.accent,
				x, y,
				x + size, y,
				x + size, y + size,
				x, y + size);
	}

	@Override
	public boolean canPlaceOn(Tile tile, Team team, int rotation) {
		if (spacing < 1) return true;
		int off = 1 - size % 2;
		for (int x = tile.x - spacing + off; x <= tile.x + spacing; x++) {
			for (int y = tile.y - spacing + off; y <= tile.y + spacing; y++) {
				Tile t = Vars.world.tile(x, y);
				if (t != null && t.block() instanceof WindGenerator s && (s == this || s.intersectsSpacing(t.build.tile, tile)))
					return false;
			}
		}
		return true;
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		super.drawPlanRegion(plan, list);
		Draw.mixcol();
		int off = 1 - size % 2;
		Tile tile = plan.tile();
		if (spacing < 1 || tile == null) return;
		for (int x = tile.x - spacing + off; x <= tile.x + spacing; x++) {
			for (int y = tile.y - spacing + off; y <= tile.y + spacing; y++) {
				Tile t = Vars.world.tile(x, y);
				if (t != null && t.block() instanceof WindGenerator s && (s == this || s.intersectsSpacing(t.build.tile, tile))) {
					Drawf.selected(t.build, Pal.remove);
				}
			}
		}
	}

	public boolean intersectsSpacing(int sx, int sy, int ox, int oy, int ext) { //TODO untested with larger than 1x1
		if (spacing < 1) return true;
		int spacingOffset = spacing + ext;
		int sizeOffset = 1 - (size & 1);

		return ox >= sx + sizeOffset - spacingOffset && ox <= sx + spacingOffset &&
				oy >= sy + sizeOffset - spacingOffset && oy <= sy + spacingOffset;
	}

	public boolean intersectsSpacing(Tile self, Tile other) {
		return intersectsSpacing(self.x, self.y, other.x, other.y, 0);
	}

	@Override
	public void changePlacementPath(Seq<Point2> points, int rotation) {
		if (spacing >= 1)
			Placement.calculateNodes(points, this, rotation, (point, other) -> intersectsSpacing(point.x, point.y, other.x, other.y, 1));
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = WindGeneratorBuild::new;
	}

	public class WindGeneratorBuild extends GeneratorBuild {
		public float boost;
		public float lastRotation, targetRotation, rot, nextChangeTime, startTime;

		public WindGeneratorBuild() {
			baseRotation();
		}

		@Override
		public void updateTile() {
			if (enabled) {
				boost = Mathf.lerpDelta(boost, !Groups.weather.isEmpty() ? 1.1f : 0.0f, 0.05f);
				productionEfficiency = 1 + (boostWeather * boost);
			}
		}

		public float baseRotation() {
			float currentTime = Time.time / baseRotateSpeed;
			float progress = (currentTime - startTime) / rotChangeTime;
			progress = Mathm.clamp(progress, 0, 1);

			WeatherState w = Groups.weather.find(ws -> ws.weather instanceof EffectWeather e && e.useWindVector);

			if (!Groups.weather.isEmpty() && w != null) {
				float windRotation = w.windVector.angle() + 90f;

				if (!Mathf.equal(targetRotation, windRotation, 0.001f)) {
					lastRotation = rot;
					targetRotation = windRotation;
					startTime = currentTime;
					nextChangeTime = currentTime + rotChangeTime;
					progress = 0;
				}
			} else if (currentTime > nextChangeTime) {
				lastRotation = rot;
				targetRotation = Mathf.random(360f);
				startTime = currentTime;
				nextChangeTime = currentTime + rotChangeTime;
				progress = 0;
			}

			rot = Mathf.lerp(lastRotation, targetRotation, progress);
			return rot;
		}

		@Override
		public void write(Writes write) {
			super.write(write);
			write.f(boost);
			write.f(lastRotation);
			write.f(targetRotation);
			write.f(rot);
		}

		@Override
		public void read(Reads read, byte revision) {
			super.read(read, revision);
			boost = read.f();
			lastRotation = read.f();
			targetRotation = read.f();
			rot = read.f();
		}
	}
}
