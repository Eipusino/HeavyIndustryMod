package heavyindustry.world.meta;

import mindustry.world.meta.BuildVisibility;

import static mindustry.Vars.net;
import static mindustry.Vars.player;
import static mindustry.Vars.state;

public final class HBuildVisibility {
	public static BuildVisibility
			singlePlayer = new BuildVisibility(() -> state == null || !net.active()),
			techDsAvailable = new BuildVisibility(() -> state == null || state.rules.infiniteResources || player == null);

	/** Don't let anyone instantiate this class. */
	private HBuildVisibility() {}
}
