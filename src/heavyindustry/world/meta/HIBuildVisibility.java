package heavyindustry.world.meta;

import mindustry.world.meta.*;

import static mindustry.Vars.*;

public final class HIBuildVisibility {
    public static BuildVisibility
            campaignOrSandboxOnly = new BuildVisibility(() -> state == null || state.isCampaign() || state.rules.infiniteResources),
            techDsAvailable = new BuildVisibility(() -> state == null || state.rules.infiniteResources || player == null);

    /** HIBuildVisibility should not be instantiated. */
    private HIBuildVisibility() {}
}