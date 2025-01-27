package heavyindustry.world.draw;

import arc.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.draw.*;

import static heavyindustry.util.Utils.*;

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
		regions = split(block.name + "-frame", (size > 0 ? size : block.size) * 32, 0);
		icon = Core.atlas.find(block.name + "-frame-icon");
	}
}
