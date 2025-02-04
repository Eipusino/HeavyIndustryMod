package heavyindustry.world.draw;

import arc.graphics.g2d.*;
import mindustry.gen.*;
import mindustry.world.draw.*;

/**
 * Transform the Draw layer. JSON specific.
 *
 * @since 1.0.6
 */
public class DrawZSet extends DrawBlock {
	public float layer;

	public DrawZSet(float l) {
		layer = l;
	}

	@Override
	public void draw(Building build) {
		Draw.z(layer);
	}
}