package heavyindustry.world.draw;

import arc.*;
import arc.graphics.g2d.*;
import arc.util.*;
import heavyindustry.graphics.*;
import mindustry.entities.units.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.draw.*;

public class DrawHalfSpinner extends DrawBlock {
	public TextureRegion region;
	public String suffix = "";
	public boolean drawPlan = true;
	public float rotateSpeed, x, y, rotation;
	/** Any number <=0 disables layer changes. */
	public float layer = -1;

	public DrawHalfSpinner(String suffix, float rotateSpeed) {
		this.suffix = suffix;
		this.rotateSpeed = rotateSpeed;
	}

	@Override
	public void draw(Building build) {
		float z = Draw.z();
		if (layer > 0) Draw.z(layer);
		Drawn.drawHalfSpin(region, build.x + x, build.y + y, build.totalProgress() * rotateSpeed + rotation);
		Draw.z(z);
	}

	@Override
	public void drawPlan(Block block, BuildPlan plan, Eachable<BuildPlan> list) {
		if (!drawPlan) return;
		Draw.rect(region, plan.drawx(), plan.drawy());
	}

	@Override
	public TextureRegion[] icons(Block block) {
		return new TextureRegion[]{region};
	}

	@Override
	public void load(Block block) {
		region = Core.atlas.find(block.name + suffix);
	}
}
