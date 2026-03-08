package endfield.world.draw;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawDefault;

public class DrawFactories extends DrawDefault {
	public TextureRegion rotator, rotator2, bottom, liquid, pressor, top;
	public Color liquidColor = Color.white;
	public float drawRotator;
	public float drawRotator2;
	public float[] pressorSet = {};
	public boolean drawTop;

	@Override
	public void draw(Building build) {
		Draw.rect(bottom, build.x, build.y);

		if (liquidColor.a > 0.001f) {
			Draw.color(liquidColor);
			Draw.alpha(build.liquids.currentAmount() / build.block.liquidCapacity);
			Draw.rect(liquid, build.x, build.y);
			Draw.reset();
		}
		if (drawRotator != 0) Draw.rect(rotator, build.x, build.y, drawRotator * build.totalProgress());
		if (drawRotator2 != 0) Draw.rect(rotator2, build.x, build.y, drawRotator2 * build.totalProgress());

		if (pressorSet.length == 4) {
			for (int arm = 0; arm < 4; arm++) {
				int offest = arm - 1;
				Vec2 armVec = new Vec2();
				armVec.trns(pressorSet[2] + 90 * offest, Mathf.absin(build.totalProgress(), pressorSet[0], pressorSet[1] * build.warmup()));
				Draw.rect(pressor, build.x + armVec.x, build.y + armVec.y, 90 * offest + pressorSet[3]);
			}
		}

		Draw.rect(build.block.region, build.x, build.y);
		if (drawTop) Draw.rect(top, build.x, build.y);
	}

	@Override
	public void load(Block block) {
		String name = block.name;

		rotator = Core.atlas.find(name + "-rotator");
		rotator2 = Core.atlas.find(name + "-rotator2");
		bottom = Core.atlas.find(name + "-bottom");
		liquid = Core.atlas.find(name + "-liquid");
		pressor = Core.atlas.find(name + "-pressor");
		top = Core.atlas.find(name + "-top");
	}

	@Override
	public TextureRegion[] icons(Block block) {
		return new TextureRegion[]{bottom, block.region};
	}
}
