package endfield.world.blocks.liquid;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.blocks.production.SolidPump;

public class LiquidExtractor extends SolidPump {
	public TextureRegion rotatorRegion1;

	public LiquidExtractor(String name) {
		super(name);
	}

	@Override
	public void load() {
		super.load();
		rotatorRegion1 = Core.atlas.find(name + "-rotator1");
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{region};
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = LiquidExtractorBuild::new;
	}

	public class LiquidExtractorBuild extends SolidPumpBuild {
		@Override
		public void draw() {
			Draw.rect(bottomRegion, x, y);
			Draw.z(Layer.blockCracks);
			super.drawCracks();
			Draw.z(Layer.blockAfterCracks);

			Drawf.liquid(liquidRegion, x, y, liquids.get(result) / liquidCapacity, result.color);
			Drawf.spinSprite(rotatorRegion, x, y, pumpTime * rotateSpeed);
			Drawf.spinSprite(rotatorRegion1, x, y, pumpTime * -rotateSpeed / 3);
			Draw.rect(topRegion, x, y);
		}
	}
}
