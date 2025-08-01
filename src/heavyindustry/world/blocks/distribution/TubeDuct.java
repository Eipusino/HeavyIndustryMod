package heavyindustry.world.blocks.distribution;

import arc.graphics.g2d.TextureRegion;
import heavyindustry.util.Utils;
import mindustry.world.blocks.distribution.Duct;

public class TubeDuct extends Duct {
	public TubeDuct(String name) {
		super(name);
	}

	@Override
	public void load() {
		super.load();

		topRegions = Utils.split(name + "-top", 32, 0);
		botRegions = Utils.split(name + "-bot", 32, 0);
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{region};
	}

	public class TubeDuctBuild extends DuctBuild {
		//there's nothing to change...
	}
}
