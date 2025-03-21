package heavyindustry.world.blocks.power;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.core.Renderer;
import mindustry.gen.Building;
import mindustry.world.blocks.power.PowerNode;

public class SmartPowerNode extends PowerNode {
	public SmartPowerNode(String name) {
		super(name);
		update = true;
	}

	public class SmartPowerNodeBuild extends PowerNodeBuild {
		public int lastID = -1;
		public Color darkColor = new Color(), lightColor = new Color();

		@Override
		public void updateTile() {
			updatePowerColor();
		}

		public void updatePowerColor() {
			int id = power.graph.getID();
			if (id != lastID) {
				float hue = Mathf.randomSeed(id, 360f);
				lightColor.fromHsv(hue, 1f, 1f);
				darkColor.fromHsv(hue + 6f, 1f, 1f).mul(0.75f);
				lastID = id;
			}
		}

		@Override
		public void draw() {
			Draw.rect(region, x, y);
			drawTeamTop();

			if (!Mathf.zero(Renderer.laserOpacity) && !isPayload()) {
				Draw.z(70f);
				setupColor(power.graph.getSatisfaction());

				for (int i = 0; i < power.links.size; ++i) {
					Building link = Vars.world.build(power.links.get(i));
					if (linkValid(this, link) && (!(link.block instanceof PowerNode) || link.id < id)) {
						drawLaser(x, y, link.x, link.y, size, link.block.size);
					}
				}

				Draw.reset();
			}
		}

		protected void setupColor(float satisfaction) {
			Draw.color(lightColor, darkColor, (1f - satisfaction) * 0.86f + Mathf.absin(3f, 0.1f));
			Draw.alpha(Renderer.laserOpacity);
		}
	}
}
