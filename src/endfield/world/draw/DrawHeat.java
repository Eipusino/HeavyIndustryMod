package endfield.world.draw;

import arc.Core;
import arc.graphics.Blending;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class DrawHeat extends DrawBlock {
	public TextureRegion heat, glow;

	public Color heatColor = new Color(1f, 0.22f, 0.22f, 0.8f);
	public float heatPulse = 0.3f, heatPulseScl = 10f, glowMult = 1.2f;

	public int rotOffset = 0;
	public boolean drawGlow = true;

	public DrawHeat() {}

	public DrawHeat(int rotOff, boolean glow) {
		rotOffset = rotOff;
		drawGlow = glow;
	}

	@Override
	public void draw(Building build) {
		float rotdeg = (build.rotation + rotOffset) * 90;

		if (build.efficiency > 0) {
			Draw.z(Layer.blockAdditive);
			Draw.blend(Blending.additive);
			Draw.color(heatColor, build.efficiency * (heatColor.a * (1f - heatPulse + Mathf.absin(heatPulseScl, heatPulse))));
			if (heat.found()) Draw.rect(heat, build.x, build.y, rotdeg);
			Draw.color(Draw.getColor().mul(glowMult));
			if (drawGlow && glow.found()) Draw.rect(glow, build.x, build.y);
			Draw.blend();
			Draw.color();
		}
	}

	@Override
	public void load(Block block) {
		heat = Core.atlas.find(block.name + "-heat");
		glow = Core.atlas.find(block.name + "-glow");
	}
}
