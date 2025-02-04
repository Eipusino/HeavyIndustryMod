package heavyindustry.world.draw;

import mindustry.gen.*;
import mindustry.world.draw.*;

/**
 * Draw a TeamTop for the Block. JSON specific.
 *
 * @since 1.0.6
 */
public class DrawTeam extends DrawBlock {
	@Override
	public void draw(Building build) {
		if (build.block.teamRegion.found()) {
			build.drawTeamTop();
		}
	}
}
