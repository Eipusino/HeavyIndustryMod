package heavyindustry.world.draw;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

// Draws a hydraulic press.
public class DrawPress extends DrawBlock {
	public TextureRegion pressRegion;
	public float offsetX = 0f, offsetY = 0f;
	public float baseSize = 0.8f;
	public float grow = 0.2f;
	public String suffix = "-press";
	public Interp progress = Interp.slope;

	@Override
	public void draw(Building build) {
		float resizeProgress = progress.apply(build.progress());
		float resize = baseSize + resizeProgress * grow;
		Draw.xscl = Draw.yscl = resize;
		Draw.rect(pressRegion, build.x + offsetX, build.y + offsetY);
		Draw.xscl = Draw.yscl = 1f;
	}

	@Override
	public void load(Block block) {
		pressRegion = Core.atlas.find(block.name + suffix);
	}

	@Override
	public TextureRegion[] icons(Block block) {
		return new TextureRegion[]{pressRegion};
	}
}
