package heavyindustry.world.draw;

import arc.graphics.g2d.*;
import mindustry.gen.*;
import mindustry.world.draw.*;

public class DrawTeam extends DrawBlock {
	@Override
	public void draw(Building build) {
		if (build.block.teamRegion.found()) {
			if (build.block.teamRegions[build.team.id] == build.block.teamRegion) {
				Draw.color(build.team.color);
			}

			Draw.rect(build.block.teamRegions[build.team.id], build.x, build.y);
			Draw.color();
		}
	}
}
