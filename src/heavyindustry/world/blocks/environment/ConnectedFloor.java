package heavyindustry.world.blocks.environment;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.util.Log;
import heavyindustry.util.Sprites;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.world.blocks.environment.Floor;

public class ConnectedFloor extends Floor {
	public ConnectedFloor(String name) {
		super(name);
	}

	public ConnectedFloor(String name, int variants) {
		super(name, variants);
	}

	@Override
	public void load() {
		region = Core.atlas.find(name);

		customShadowRegion = Core.atlas.find(name + "-shadow");
		teamRegion = Core.atlas.find(name + "-team");

		//load specific team regions
		teamRegions = new TextureRegion[Team.all.length];
		for (Team team : Team.all) {
			teamRegions[team.id] = teamRegion.found() && team.hasPalette ? Core.atlas.find(name + "-team-" + team.name, teamRegion) : teamRegion;
		}

		if (autotile) {
			variants = 0;
		}

		int tsize = (int) (Vars.tilesize / Draw.scl);

		if (tilingVariants > 0 && !Vars.headless) {
			tilingRegions = new TextureRegion[tilingVariants][][];
			for (int i = 0; i < tilingVariants; i++) {
				TextureRegion tile = Core.atlas.find(name + "-tile" + (i + 1));
				tilingRegions[i] = tile.split(tsize, tsize);
				tilingSize = tilingRegions[i].length;
			}

			for (int i = 0; i < tilingVariants; i++) {
				if (tilingRegions[i].length != tilingSize || tilingRegions[i][0].length != tilingSize) {
					Log.warn("Piece: @: In order to prevent crashes, tiling regions must all be valid regions with the same size. Tiling has been disabled. Sprite '@' has a width or height inconsistent with other tiles.", name, name + "-tile" + (i + 1));
					tilingVariants = 0;
				}
			}
		}

		if (variants > 0) {
			variantRegions = Sprites.splitLayer(name + "-sheet", 32, 0);
		} else {
			variantRegions = new TextureRegion[]{region};
		}

		if (autotile) {
			autotileRegions = Sprites.split(name + "-autotile", 32, 12, 4);
			if (autotileVariants > 1) {
				autotileVariantRegions = new TextureRegion[variants][];
				for (int i = 0; i < variants; i++) {
					autotileVariantRegions[i] = Sprites.split(name + "-" + (i + 1) + "-autotile", 32, 12, 4);
				}
			}
			if (autotileMidVariants > 1) {
				autotileMidRegions = Sprites.splitLayer(name + "-mid", 32, 0);
			}
		}

		if (Core.atlas.has(name + "-edge")) {
			edges = Core.atlas.find(name + "-edge").split(tsize, tsize);
		}
		edgeRegion = Core.atlas.find("edge");
	}

	@Override
	public TextureRegion[] icons() {
		return new TextureRegion[]{region};
	}

	@Override
	public Floor asFloor() {
		return this;
	}
}
