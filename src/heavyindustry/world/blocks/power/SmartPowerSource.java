package heavyindustry.world.blocks.power;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Time;
import heavyindustry.graphics.Drawn;
import mindustry.gen.Building;
import mindustry.world.blocks.power.PowerBlock;
import mindustry.world.meta.Env;

public class SmartPowerSource extends PowerBlock {
	public float powerOverhead = 1000f / 60f;
	public TextureRegion colorRegion;

	public SmartPowerSource(String name) {
		super(name);

		envEnabled = Env.any;

		outputsPower = true;
		consumesPower = false;
		canOverdrive = false;
	}

	@Override
	public void load() {
		super.load();
		colorRegion = Core.atlas.find(name + "-strobe");
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = SmartPowerSourceBuild::new;
	}

	public class SmartPowerSourceBuild extends Building {
		@Override
		public void draw() {
			super.draw();

			Drawn.setStrobeColor();
			Draw.rect(colorRegion, x, y);
			Draw.color();
		}

		@Override
		public float getPowerProduction() {
			return enabled ? power.graph.getPowerNeeded() / Time.delta + powerOverhead : 0;
		}
	}
}
