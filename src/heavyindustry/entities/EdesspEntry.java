package heavyindustry.entities;

import arc.graphics.g2d.TextureRegion;

public class EdesspEntry {
	public TextureRegion region;
	public float range;
	public float rot;
	public float rRot;

	public EdesspEntry() {}

	/*public EdesspEntry(TextureRegion reg, float ran, float rt, float rrt) {
		region = reg;
		range = ran;
		rot = rt;
		rRot = rrt;
	}*/

	public EdesspEntry set(TextureRegion reg, float ran, float rt, float rrt) {
		region = reg;
		range = ran;
		rot = rt;
		rRot = rrt;

		return this;
	}
}
