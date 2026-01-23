package endfield.world.meta;

import mindustry.Vars;
import mindustry.world.meta.BuildVisibility;

public final class EBuildVisibility {
	public static final BuildVisibility singlePlayer = new BuildVisibility(() -> Vars.state == null || !Vars.net.active());
	public static final BuildVisibility techDsAvailable = new BuildVisibility(() -> Vars.state == null || Vars.state.rules.infiniteResources || Vars.player == null);

	/** Don't let anyone instantiate this class. */
	private EBuildVisibility() {}
}
