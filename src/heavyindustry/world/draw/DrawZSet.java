package heavyindustry.world.draw;

import arc.graphics.g2d.Draw;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.draw.DrawBlock;

/**
 * Transform the Draw layer. JSON specific.
 *
 * @since 1.0.6
 */
public class DrawZSet extends DrawBlock {
	public float layer;

	public DrawZSet() {
		this(Layer.block);
	}

	public DrawZSet(float l) {
		layer = l;
	}

	@Override
	public void draw(Building build) {
		Draw.z(layer);
	}
}