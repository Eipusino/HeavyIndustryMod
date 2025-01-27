package heavyindustry.world.draw;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import mindustry.*;
import mindustry.content.*;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.draw.*;

public class DrawPrinter extends DrawDefault {
	public Color printColor;
	public Color lightColor;

	public float moveLength = 8f;
	public Item toPrint;
	public float time;

	public TextureRegion bottom, lightRegion;

	public DrawPrinter(Item item) {
		toPrint = item;
	}

	public DrawPrinter() {
		toPrint = Items.copper;
	}

	public void draw(Building entity) {
		Draw.rect(bottom, entity.x, entity.y);
		Draw.color(printColor);
		Draw.alpha(entity.warmup());
		float sin = Mathf.sin(entity.totalProgress(), time, moveLength);
		for (int i : Mathf.signs) {
			Lines.lineAngleCenter(entity.x + i * sin, entity.y, 90, 12);
			Lines.lineAngleCenter(entity.x, entity.y + i * sin, 0, 12);
		}
		Draw.reset();

		Draw.rect(entity.block.region, entity.x, entity.y);

		Draw.draw(Layer.blockOver, () -> {
			Drawf.construct(entity.x, entity.y, toPrint.fullIcon, printColor, 0, entity.progress(), entity.progress(), entity.totalProgress() * 3f);
		});

		if (lightColor.a > 0.001f) {
			Draw.color(lightColor, entity.warmup());
			Draw.blend(Blending.additive);
			Draw.alpha(entity.warmup() * 0.85f);
			Draw.rect(lightRegion, entity.x, entity.y);
			Draw.blend();
			Draw.reset();
		}
	}

	@Override
	public void drawLight(Building build) {
		Drawf.light(build.x, build.y, build.warmup() * build.block.size * Vars.tilesize, lightColor, 0.7f);
	}

	@Override
	public void load(Block block) {
		bottom = Core.atlas.find(block.name + "-bottom");
		lightRegion = Core.atlas.find(block.name + "-light");
	}

	@Override
	public TextureRegion[] icons(Block block) {
		return new TextureRegion[]{bottom, block.region};
	}
}
