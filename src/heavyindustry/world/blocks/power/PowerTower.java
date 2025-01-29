package heavyindustry.world.blocks.power;

import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import mindustry.*;
import mindustry.core.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.world.blocks.power.*;
import mindustry.world.meta.*;

import java.util.*;

import static mindustry.Vars.*;

public class PowerTower extends BeamNode {
	public Color baseColor = Color.valueOf("d4e1ff");
	public int range = 8;
	public int linkRange = 5;

	public PowerTower(String name) {
		super(name);
	}

	@Override
	public void drawPlace(int x, int y, int rotation, boolean valid) {
		for (int i = 0; i < 4; i++) {
			int maxLen = range + size / 2;
			Building dest = null;
			var dir = Geometry.d4[i];
			int dx = dir.x, dy = dir.y;
			int offset = size / 2;
			for (int j = 1 + offset; j <= range + offset; j++) {
				var other = world.build(x + j * dir.x, y + j * dir.y);

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
					x * tilesize + dx * (tilesize * size / 2f + 2),
					y * tilesize + dy * (tilesize * size / 2f + 2),
					x * tilesize + dx * (maxLen) * tilesize,
					y * tilesize + dy * (maxLen) * tilesize
			);

			if (dest != null) {
				Drawf.square(dest.x, dest.y, dest.block.size * tilesize / 2f + 2.5f, 0f);
			}
		}

		x *= tilesize;
		y *= tilesize;
		x += offset;
		y += offset;

		Drawf.dashSquare(baseColor, x, y, linkRange * tilesize);
		indexer.eachBlock(player.team(), Tmp.r1.setCentered(x, y, linkRange * tilesize), b -> (b.power != null) && !(b instanceof PowerTowerBuild), t ->
                Drawf.selected(t, Tmp.c1.set(baseColor).a(Mathf.absin(4f, 1f)))
        );
	}

	@Override
	public void setStats() {
		super.setStats();
		stats.remove(Stat.powerRange);
		stats.add(Stat.linkRange, range, StatUnit.blocks);
		stats.add(Stat.range, linkRange, StatUnit.blocks);
	}

	public class PowerTowerBuild extends BeamNodeBuild {
		public Seq<Building> targets = new Seq<>();

		@Override
		public void updateTile() {
			if (lastChange != world.tileChanges) {
				lastChange = world.tileChanges;
				updateLink();
				updateDirections();
			}
		}

		@Override
		public void updateDirections() {
			for (int i = 0; i < 4; i++) {
				var prev = links[i];
				var dir = Geometry.d4[i];
				links[i] = null;
				dests[i] = null;
				int offset = size / 2;
				//find first block with power in range
				for (int j = 1 + offset; j <= range + offset; j++) {
					var other = world.build(tile.x + j * dir.x, tile.y + j * dir.y);

					//hit insulated wall
					if (other != null && other.isInsulated()) {
						break;
					}

					//power nodes do NOT play nice with beam nodes, do not touch them as that forcefully modifies their links
					if (other != null && other.block.hasPower && other.block.connectedPower && other.team == team && other.block instanceof PowerTower) {
						links[i] = other;
						dests[i] = world.tile(tile.x + j * dir.x, tile.y + j * dir.y);
						break;
					}
				}

				var next = links[i];

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
			for (var build : targets) {
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
			Seq<Building> newTargets = new Seq<>();
			indexer.eachBlock(player.team(), Tmp.r1.setCentered(x, y, linkRange * tilesize), b -> (b.power != null) && !(b instanceof PowerTowerBuild), newTargets::add);
			for (var build : newTargets) {
				if (!targets.contains(build)) {
					targets.addUnique(build);
					power.links.addUnique(build.pos());
					build.power.links.addUnique(pos());

					power.graph.addGraph(build.power.graph);
				}
			}
			for (var build : targets) {
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

			Drawf.dashSquare(baseColor, x, y, linkRange * tilesize);
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
						var point = Geometry.d4[i];
						float poff = tilesize / 2f;
						Drawf.laser(laser, laserEnd, x + poff * size * point.x, y + poff * size * point.y, dests[i].worldx() - poff * point.x, dests[i].worldy() - poff * point.y, w);
					}
				}
			}

			Draw.reset();
		}
	}
}
