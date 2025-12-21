package heavyindustry.world.blocks.power;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.util.Eachable;
import heavyindustry.math.Mathm;
import mindustry.Vars;
import mindustry.core.Renderer;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.power.PowerGraph;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import java.util.Arrays;

/**
 * Combining the characteristics of laser nodes and diodes.
 * <p>Stitching it up is enough.
 *
 * @author Eipusino
 */
public class BeamDiode extends Block {
	public int range = 5;

	public Color laserColor1 = Color.white;
	public Color laserColor2 = new Color(0xffd9c2ff);
	public float pulseScl = 7, pulseMag = 0.05f;
	public float laserWidth = 0.4f;

	public TextureRegion laser, arrow;
	public TextureRegion[] laserEnds = new TextureRegion[2];

	public BeamDiode(String name) {
		super(name);
		rotate = true;
		update = true;
		solid = true;
		insulated = true;
		group = BlockGroup.power;
		noUpdateDisabled = true;
		schematicPriority = 10;
		envEnabled |= Env.space;
	}

	@Override
	public void load() {
		super.load();

		arrow = Core.atlas.find(name + "-arrow");
		laser = Core.atlas.find(name + "-beam", "power-beam");
		laserEnds[0] = Core.atlas.find(name + "-beam-end-out", "power-beam-end");
		laserEnds[1] = Core.atlas.find(name + "-beam-end-in", "power-beam-end");
	}

	@Override
	public void setStats() {
		super.setStats();

		stats.add(Stat.powerRange, range, StatUnit.blocks);
	}

	@Override
	public void init() {
		super.init();

		updateClipRadius((range + 1) * Vars.tilesize);
	}

	@Override
	public void setBars() {
		super.setBars();

		addBar("back", (BeamDiodeBuild tile) -> new Bar("bar.input", Pal.powerBar, () -> bar(tile.links[1])));
		addBar("front", (BeamDiodeBuild tile) -> new Bar("bar.output", Pal.powerBar, () -> bar(tile.links[0])));
	}

	@Override
	public void drawPlanRegion(BuildPlan plan, Eachable<BuildPlan> list) {
		Draw.rect(fullIcon, plan.drawx(), plan.drawy());
		Draw.rect(arrow, plan.drawx(), plan.drawy(), !rotate ? 0 : plan.rotation * 90);
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		for (int i = 0; i < 2; i++) {
			int maxLen = range + size / 2;
			Building dest = null;
			Point2 dir = Geometry.d4[Mathf.mod(rotation + 2 * i, 4)];
			int dx = dir.x, dy = dir.y;
			int offset = size / 2;
			for (int j = 1 + offset; j <= range + offset; j++) {
				Building other = Vars.world.build(x + j * dir.x, y + j * dir.y);

				if (other != null && other.isInsulated()) {
					break;
				}

				if (other != null && other.block.hasPower && other.team == Vars.player.team() && !(other.block instanceof PowerNode)) {
					maxLen = j;
					dest = other;
					break;
				}
			}

			Drawf.dashLine(Pal.placing, x * Vars.tilesize + dx * (Vars.tilesize * size / 2f + 2), y * Vars.tilesize + dy * (Vars.tilesize * size / 2f + 2), x * Vars.tilesize + dx * (maxLen) * Vars.tilesize, y * Vars.tilesize + dy * (maxLen) * Vars.tilesize);

			if (dest != null) {
				Drawf.square(dest.x, dest.y, dest.block.size * Vars.tilesize / 2f + 2.5f, 0f);
			}
		}
	}

	public float bar(Building tile) {
		return (tile != null && tile.block.hasPower) ? tile.power.graph.getLastPowerStored() / tile.power.graph.getTotalBatteryCapacity() : 0f;
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = BeamDiodeBuild::new;
	}

	public class BeamDiodeBuild extends Building {
		public Building[] links = new Building[2];
		public Tile[] dests = new Tile[2];
		public int lastChange = -2;

		@Override
		public void placed() {
			super.placed();
			updateDirections();
		}

		@Override
		public void updateTile() {
			if (lastChange != Vars.world.tileChanges) {
				lastChange = Vars.world.tileChanges;
				updateDirections();
			}

			if (tile == null || links[0] == null || links[1] == null || !links[1].block.hasPower || !links[0].block.hasPower || links[1].team != team || links[0].team != team)
				return;

			PowerGraph backGraph = links[1].power.graph;
			PowerGraph frontGraph = links[0].power.graph;
			if (backGraph == frontGraph) return;

			float backStored = backGraph.getBatteryStored() / backGraph.getTotalBatteryCapacity();
			float frontStored = frontGraph.getBatteryStored() / frontGraph.getTotalBatteryCapacity();

			if (backStored > frontStored) {
				float amount = backGraph.getBatteryStored() * (backStored - frontStored) / 2;

				amount = Mathm.clamp(amount, 0, frontGraph.getTotalBatteryCapacity() * (1 - frontStored));

				backGraph.transferPower(-amount);
				frontGraph.transferPower(amount);
			}
		}

		@Override
		public void onProximityUpdate() {
			super.onProximityUpdate();
			updateDirections();
		}

		@Override
		public void draw() {
			Draw.rect(region, x, y, 0);
			Draw.rect(arrow, x, y, rotate ? rotdeg() : 0);

			if (Mathf.zero(Renderer.laserOpacity)) return;

			Draw.z(Layer.power);
			Draw.alpha(Renderer.laserOpacity);
			float w = laserWidth + Mathf.absin(pulseScl, pulseMag);

			for (int i = 0; i < 2; i++) {
				if (dests[i] != null) {
					int dst = Math.max(Math.abs(dests[i].x - tile.x), Math.abs(dests[i].y - tile.y));

					if (dst > 1 + size / 2) {
						Point2 point = Geometry.d4[Mathf.mod(rotation + 2 * i, 4)];
						float poff = Vars.tilesize / 2f;
						Draw.color(laserColor1, laserColor2, (1f - links[i].power.graph.getSatisfaction()) * 0.86f + Mathf.absin(3f, 0.1f));
						Drawf.laser(laser, laserEnds[1 - i], laserEnds[i], x + poff * size * point.x, y + poff * size * point.y, dests[i].worldx() - poff * point.x, dests[i].worldy() - poff * point.y, w);
					}
				}
			}

			Draw.reset();
		}

		@Override
		public void pickedUp() {
			Arrays.fill(links, null);
			Arrays.fill(dests, null);
		}

		public void updateDirections() {
			for (int i = 0; i < 2; i++) {
				Point2 dir = Geometry.d4[Mathf.mod(rotation + 2 * i, 4)];
				links[i] = null;
				dests[i] = null;
				int offset = size / 2;

				for (int j = 1 + offset; j <= range + offset; j++) {
					Building other = Vars.world.build(tile.x + j * dir.x, tile.y + j * dir.y);

					if (other != null && other.isInsulated()) {
						break;
					}

					if (other != null && other.block.hasPower && other.team == team) {
						links[i] = other;
						dests[i] = Vars.world.tile(tile.x + j * dir.x, tile.y + j * dir.y);
						break;
					}
				}
			}
		}
	}
}
