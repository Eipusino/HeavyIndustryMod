package heavyindustry.world.draw;

import arc.graphics.g2d.*;
import mindustry.gen.*;
import mindustry.world.draw.*;

public class DrawZSet extends DrawBlock {
	public float layer;

	public DrawZSet(float l) {
        layer = l;
	}

	public void draw(Building build) {
		Draw.z(layer);
	}
}