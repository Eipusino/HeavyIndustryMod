package heavyindustry.world.draw;

import mindustry.gen.Building;
import mindustry.world.draw.DrawBlock;

/**
 * Draw a TeamTop for the Block. JSON specific.
 *
 * @since 1.0.6
 */
public class DrawTeam extends DrawBlock {
	public DrawTeam() {}

	@Override
	public void draw(Building build) {
		if (build.block.teamRegion.found()) {
			build.drawTeamTop();
		}
	}
}
