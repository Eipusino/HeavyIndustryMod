package endfield.world.blocks.power;

import arc.func.Cons;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.core.Renderer;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.blocks.power.BeamNode;
import mindustry.world.blocks.power.PowerGraph;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import java.util.Arrays;

public class PowerTower extends BeamNode {
	public Color baseColor = new Color(0xd4e1ffff);
	public int range = 8;
	public int linkRange = 5;

	public Cons<Building> selected = t -> Drawf.selected(t, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f)));

	public PowerTower(String name) {
		super(name);
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		for (int i = 0; i < 4; i++) {
			int maxLen = range + size / 2;
			Building dest = null;
			Point2 dir = Geometry.d4[i];
			int dx = dir.x, dy = dir.y;
			int offset = size / 2;
			for (int j = 1 + offset; j <= range + offset; j++) {
				Building other = Vars.world.build(x + j * dir.x, y + j * dir.y);

				//hit insulated wall
				if (other != null && other.isInsulated()) {
					break;
				}

				if (other != null && other.team == Vars.player.team() && other.block instanceof PowerTower) {
					maxLen = j;
					dest = other;
					break;
				}
			}

			Drawf.dashLine(Pal.placing,
					x * Vars.tilesize + dx * (Vars.tilesize * size / 2f + 2),
					y * Vars.tilesize + dy * (Vars.tilesize * size / 2f + 2),
					x * Vars.tilesize + dx * (maxLen) * Vars.tilesize,
					y * Vars.tilesize + dy * (maxLen) * Vars.tilesize
			);

			if (dest != null) {
				Drawf.square(dest.x, dest.y, dest.block.size * Vars.tilesize / 2f + 2.5f, 0f);
			}
		}

		x *= Vars.tilesize;
		y *= Vars.tilesize;
		x += offset;
		y += offset;

		Drawf.dashSquare(baseColor, x, y, linkRange * Vars.tilesize);
		Vars.indexer.eachBlock(Vars.player.team(), Tmp.r1.setCentered(x, y, linkRange * Vars.tilesize), b -> b.power != null && !(b instanceof PowerTowerBuild), selected);
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.remove(Stat.powerRange);
		stats.add(Stat.linkRange, range, StatUnit.blocks);
		stats.add(Stat.range, linkRange, StatUnit.blocks);
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = PowerTowerBuild::new;
	}

	public class PowerTowerBuild extends BeamNodeBuild {
		public Seq<Building> targets = new Seq<>(Building.class);

		@Override
		public void updateTile() {
			if (lastChange != Vars.world.tileChanges) {
				lastChange = Vars.world.tileChanges;
				updateLink();
				updateDirections();
			}
		}

		@Override
		public void updateDirections() {
			for (int i = 0; i < 4; i++) {
				Building prev = links[i];
				Point2 dir = Geometry.d4[i];
				links[i] = null;
				dests[i] = null;
				int offset = size / 2;
				//find first block with power in range
				for (int j = 1 + offset; j <= range + offset; j++) {
					Building other = Vars.world.build(tile.x + j * dir.x, tile.y + j * dir.y);

					//hit insulated wall
					if (other != null && other.isInsulated()) {
						break;
					}

					//power nodes do NOT play nice with beam nodes, do not touch them as that forcefully modifies their links
					if (other != null && other.block.hasPower && other.block.connectedPower && other.team == team && other.block instanceof PowerTower) {
						links[i] = other;
						dests[i] = Vars.world.tile(tile.x + j * dir.x, tile.y + j * dir.y);
						break;
					}
				}

				Building next = links[i];

				if (next != prev) {
					//unlinked, disconnect and reflow
					if (prev != null) {
						prev.power.links.removeValue(pos());
						power.links.removeValue(prev.pos());

						PowerGraph newgraph = new PowerGraph();
						//reflow from this point, covering all tiles on this side
						newgraph.reflow(this);

						if (prev.power.graph != newgraph) {
							//reflow power for other end
							PowerGraph og = new PowerGraph();
							og.reflow(prev);
						}
					}

					//linked to a new one, connect graphs
					if (next != null) {
						power.links.addUnique(next.pos());
						next.power.links.addUnique(pos());

						power.graph.addGraph(next.power.graph);
					}
				}
			}
		}

		@Override
		public void pickedUp() {
			Arrays.fill(links, null);
			Arrays.fill(dests, null);
			for (Building build : targets) {
				build.power.links.removeValue(pos());
				power.links.removeValue(build.pos());

				PowerGraph newgraph = new PowerGraph();
				//reflow from this point, covering all tiles on this side
				newgraph.reflow(this);

				if (build.power.graph != newgraph) {
					//reflow power for other end
					PowerGraph og = new PowerGraph();
					og.reflow(build);
				}
				targets.remove(build);
			}
		}

		public void updateLink() {
			//I know this is meaningless and stupid.
			Seq<Building> newTargets = new Seq<>(Building.class);
			Vars.indexer.eachBlock(Vars.player.team(), Tmp.r1.setCentered(x, y, linkRange * Vars.tilesize), b -> b.power != null && !(b instanceof PowerTowerBuild), newTargets::add);
			for (Building build : newTargets) {
				if (!targets.contains(build)) {
					targets.addUnique(build);
					power.links.addUnique(build.pos());
					build.power.links.addUnique(pos());

					power.graph.addGraph(build.power.graph);
				}
			}
			for (Building build : targets) {
				if (!newTargets.contains(build)) {
					build.power.links.removeValue(pos());
					power.links.removeValue(build.pos());

					PowerGraph newgraph = new PowerGraph();
					//reflow from this point, covering all tiles on this side
					newgraph.reflow(this);

					if (build.power.graph != newgraph) {
						//reflow power for other end
						PowerGraph og = new PowerGraph();
						og.reflow(build);
					}
					targets.remove(build);
				} else {
					if (!power.links.contains(build.pos())) {
						power.links.addUnique(build.pos());
						build.power.links.addUnique(pos());
						power.graph.addGraph(build.power.graph);
					}
				}
			}
		}

		@Override
		public void drawSelect() {
			super.drawSelect();

			Drawf.dashSquare(baseColor, x, y, linkRange * Vars.tilesize);
		}

		@Override
		public void draw() {
			super.draw();

			if (Mathf.zero(Renderer.laserOpacity)) return;

			Draw.z(Layer.power);
			Draw.color(laserColor1, laserColor2, (1f - power.graph.getSatisfaction()) * 0.86f + Mathf.absin(3f, 0.1f));
			Draw.alpha(Renderer.laserOpacity);
			float w = laserWidth + Mathf.absin(pulseScl, pulseMag);

			for (int i = 0; i < 4; i++) {
				if (dests[i] != null && links[i].wasVisible && (links[i].block instanceof PowerTower)) {

					int dst = Math.max(Math.abs(dests[i].x - tile.x), Math.abs(dests[i].y - tile.y));
					//don't draw lasers for adjacent blocks
					if (dst > 1 + size / 2) {
						Point2 point = Geometry.d4[i];
						float poff = Vars.tilesize / 2f;
						Drawf.laser(laser, laserEnd, x + poff * size * point.x, y + poff * size * point.y, dests[i].worldx() - poff * point.x, dests[i].worldy() - poff * point.y, w);
					}
				}
			}

			Draw.reset();
		}
	}
}
