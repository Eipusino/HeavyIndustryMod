package heavyindustry.world.blocks.environment;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import mindustry.game.Team;
import mindustry.world.blocks.environment.StaticWall;

import static heavyindustry.util.Utils.split;

public class ConnectedWall extends StaticWall {
	public ConnectedWall(String name) {
		super(name);
	}

	@Override
	public void load() {
		region = Core.atlas.find(name);

		customShadowRegion = Core.atlas.find(name + "-shadow");
		teamRegion = Core.atlas.find(name + "-team");

		large = Core.atlas.find(name + "-large");

		//load specific team regions
		teamRegions = new TextureRegion[Team.all.length];
		for (Team team : Team.all) {
			teamRegions[team.id] = teamRegion.found() && team.hasPalette ? Core.atlas.find(name + "-team-" + team.name, teamRegion) : teamRegion;
		}

		if (variants != 0) {
			variantRegions = new TextureRegion[variants];

			for (int i = 0; i < variants; i++) {
				variantRegions[i] = Core.atlas.find(name + (i + 1));
			}
			region = variantRegions[0];

			if (customShadow) {
				variantShadowRegions = new TextureRegion[variants];
				for (int i = 0; i < variants; i++) {
					variantShadowRegions[i] = Core.atlas.find(name + "-shadow" + (i + 1));
				}
			}
		} else {
			variantRegions = new TextureRegion[]{region};
		}

		int size = large.width / 2;
		split = large.split(size, size);
		if (split != null) {
			for (TextureRegion[] arr : split) {
				for (TextureRegion reg : arr) {
					reg.scale = region.scale;
				}
			}
		}

		if (autotile) {
			autotileRegions = split(name + "-autotile", 32, 12, 4);
		}
	}

	@Override
	public void init() {
		super.init();

		if (autotileRegions.length < 47) autotile = false;
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{region};
	}
}
