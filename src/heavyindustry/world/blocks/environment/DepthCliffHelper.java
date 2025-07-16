package heavyindustry.world.blocks.environment;

import mindustry.graphics.CacheLayer;
import mindustry.world.Block;

public class DepthCliffHelper extends Block {
	public DepthCliffHelper(String name) {
		super(name);
		breakable = alwaysReplace = false;
		solid = true;
		cacheLayer = CacheLayer.walls;
		fillsTile = false;
		hasShadow = false;
	}
}
