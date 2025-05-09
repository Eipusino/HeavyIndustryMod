package heavyindustry.world.blocks.power;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Point2;
import mindustry.core.Renderer;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.blocks.power.BeamNode;

import static mindustry.Vars.tilesize;

public class SmartBeamNode extends BeamNode {
	public SmartBeamNode(String name) {
		super(name);
	}

	public class SmartBeamNodeBuild extends BeamNodeBuild {
		public int lastId = -1;
		public Color darkColor = new Color(), lightColor = new Color();

		@Override
		public void updateTile() {
			super.updateTile();
			updatePowerColor();
		}

		public void updatePowerColor() {
			int id = power.graph.getID();
			if (id != lastId) {
				float hue = Mathf.randomSeed(id, 360f);
				lightColor.fromHsv(hue, 1f, 1f);
				darkColor.fromHsv(hue + 6f, 1f, 1f).mul(0.75f);
				lastId = id;
			}
		}

		@Override
		public void draw() {
			Draw.rect(region, x, y);
			drawTeamTop();

			if (Mathf.zero(Renderer.laserOpacity)) return;

			Draw.z(Layer.power);
			Draw.color(lightColor, darkColor, (1f - power.graph.getSatisfaction()) * 0.86f + Mathf.absin(3f, 0.1f));
			Draw.alpha(Renderer.laserOpacity);
			float w = laserWidth + Mathf.absin(pulseScl, pulseMag);

			for (int i = 0; i < 4; i++) {
				if (dests[i] != null && links[i].wasVisible && (!(links[i].block instanceof BeamNode node) ||
						(links[i].tileX() != tileX() && links[i].tileY() != tileY()) ||
						(links[i].id > id && range >= node.range) || range > node.range)) {

					int dst = Math.max(Math.abs(dests[i].x - tile.x), Math.abs(dests[i].y - tile.y));
					//don't draw lasers for adjacent blocks
					if (dst > 1 + size / 2) {
						Point2 point = Geometry.d4[i];
						float poff = tilesize / 2f;
						Drawf.laser(laser, laserEnd, x + poff * size * point.x, y + poff * size * point.y, dests[i].worldx() - poff * point.x, dests[i].worldy() - poff * point.y, w);
					}
				}
			}

			Draw.reset();
		}
	}
}
