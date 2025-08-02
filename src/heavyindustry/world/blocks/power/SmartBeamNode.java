package heavyindustry.world.blocks.power;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.core.Renderer;
import mindustry.game.Team;
import mindustry.graphics.Layer;
import mindustry.world.blocks.power.BeamNode;

public class SmartBeamNode extends BeamNode {
	public TextureRegion topRegion;

	public SmartBeamNode(String name) {
		super(name);
	}

	@Override
	public void load() {
		super.load();

		topRegion = Core.atlas.find(name + "-top", "white");
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
			super.draw();

			if (topRegion.found() && !Mathf.zero(Renderer.laserOpacity) && team != Team.derelict) {
				float z = Draw.z();
				Draw.z(Layer.block);
				Draw.color(lightColor, darkColor, (1f - power.graph.getSatisfaction()) * 0.86f + Mathf.absin(3f, 0.1f));
				Draw.alpha(Renderer.laserOpacity);

				Draw.rect(topRegion, x, y);

				Draw.z(z);
				Draw.reset();
			}
		}
	}
}
