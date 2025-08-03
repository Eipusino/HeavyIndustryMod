package heavyindustry.world.draw;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import heavyindustry.graphics.Drawn;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawRegion;

public class DrawStrobe extends DrawRegion {
	public DrawStrobe(String suffix) {
		super(suffix);

		drawPlan = false;
	}

	public DrawStrobe() {
		this("-strobe");
	}

	@Override
	public void draw(Building build) {
		Drawn.setStrobeColor();
		super.draw(build);
		Draw.color();
	}

	@Override
	public TextureRegion[] icons(Block block) {
		return new TextureRegion[]{};
	}
}
