package endfield.world.draw;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import endfield.util.Sprites;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawFrames;

public class DrawAnim extends DrawFrames {
	public int size = 0;
	public TextureRegion icon;

	@Override
	public void draw(Building build) {
		Draw.rect(
				sine ?
						regions[(int) Mathf.absin(build.totalProgress(), interval, regions.length - 0.001f)] :
						regions[(int) ((build.totalProgress() / interval) % regions.length)],
				build.x, build.y);
	}

	@Override
	public TextureRegion[] icons(Block block) {
		return new TextureRegion[]{icon};
	}

	@Override
	public void load(Block block) {
		regions = Sprites.splitLayer(block.name + "-frame", (size > 0 ? size : block.size) * 32, 0);
		icon = Core.atlas.find(block.name + "-frame-icon");
	}
}
