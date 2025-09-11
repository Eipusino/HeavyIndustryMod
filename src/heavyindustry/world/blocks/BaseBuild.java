package heavyindustry.world.blocks;

import mindustry.gen.Building;
import mindustry.gen.Buildingc;
import mindustry.world.Block;

public interface BaseBuild extends Buildingc {
	Building build();

	Block block();
}
