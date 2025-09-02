package heavyindustry.world.meta;

import mindustry.Vars;
import mindustry.world.meta.BuildVisibility;

public final class HBuildVisibility {
	public static final BuildVisibility
			singlePlayer = new BuildVisibility(() -> Vars.state == null || !Vars.net.active()),
			techDsAvailable = new BuildVisibility(() -> Vars.state == null || Vars.state.rules.infiniteResources || Vars.player == null);

	/** Don't let anyone instantiate this class. */
	private HBuildVisibility() {}
}
