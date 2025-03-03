package heavyindustry.world.draw;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class DrawWeaveColor extends DrawBlock {
	public TextureRegion weave;
	public Color color;

	public DrawWeaveColor(Color col) {
		color = col;
	}

	@Override
	public void draw(Building build) {
		Draw.rect(weave, build.x, build.y, build.totalProgress());

		Draw.color(color);
		Draw.alpha(build.warmup());

		Lines.lineAngleCenter(
				build.x + Mathf.sin(build.totalProgress(), 6f, Vars.tilesize / 3f * build.block.size),
				build.y,
				90,
				build.block.size * Vars.tilesize / 2f);

		Draw.reset();
	}

	@Override
	public TextureRegion[] icons(Block block) {
		return new TextureRegion[]{weave};
	}

	@Override
	public void load(Block block) {
		weave = Core.atlas.find(block.name + "-weave");
	}
}
