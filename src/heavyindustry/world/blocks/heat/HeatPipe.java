package heavyindustry.world.blocks.heat;

import arc.util.Nullable;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.Autotiler;
import mindustry.world.blocks.distribution.ChainedBuilding;
import mindustry.world.blocks.heat.HeatConductor;

public class HeatPipe extends HeatConductor implements Autotiler {
	public HeatPipe(String name) {
		super(name);
	}

	@Override
	public boolean blends(Tile tile, int i, int i1, int i2, int i3, Block block) {
		return false;
	}

	@Override
	protected void initBuilding() {
		if (buildType == null) buildType = HeatPipeBuild::new;
	}

	public class HeatPipeBuild extends HeatConductorBuild implements ChainedBuilding {
		public @Nullable Building next;
		public @Nullable HeatPipeBuild nextc;

		@Override
		public Building next() {
			return nextc;
		}
	}
}
