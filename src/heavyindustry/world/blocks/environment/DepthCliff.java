package heavyindustry.world.blocks.environment;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Geometry;
import arc.util.Tmp;
import heavyindustry.content.HBlocks;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.gen.Call;
import mindustry.graphics.CacheLayer;
import mindustry.world.Block;
import mindustry.world.Tile;

import static heavyindustry.HVars.name;
import static heavyindustry.util.Utils.split;
import static mindustry.Vars.world;

public class DepthCliff extends Block {
	public float colorMultiplier = 1.5f;
	public boolean useMapColor = true;
	public TextureRegion[] cliffs;

	public DepthCliff(String name) {
		super(name);
		breakable = alwaysReplace = false;
		solid = true;
		saveData = true;
		cacheLayer = CacheLayer.walls;
		fillsTile = false;
		hasShadow = false;
	}

	public static void processCliffs() {
		world.tiles.eachTile(tile -> {
			if (tile.block() instanceof DepthCliff && tile.data == 0) {
				for (int i = 0; i < 4; i++) {
					if (tile.nearby(i).block() instanceof DepthCliffHelper) tile.data = (byte) (i + 1);
				}
				if (tile.data == 0) for (int i = 0; i < 4; i++) {
					if (tile.nearby(Geometry.d8edge(i)).block() instanceof DepthCliffHelper) tile.data = (byte) (i + 5);
				}
				for (int i = 0; i < 4; i++) {
					if (tile.nearby(i).block() instanceof DepthCliffHelper && tile.nearby((i + 1) % 4).block() instanceof DepthCliffHelper) tile.data = (byte) (i + 9);
				}
				if (tile.data == 0) tile.setBlock(Blocks.air);
			}
		});
		world.tiles.eachTile(tile -> {
			if (tile.block() instanceof DepthCliffHelper) Call.setTile(tile, Blocks.air, Team.derelict, 0);
		});
	}

	public static void unProcessCliffs() {
		world.tiles.eachTile(tile -> {
			if (HBlocks.cliffHelper != null && tile.block() instanceof DepthCliff && tile.data != 0) {
				if (tile.data <= 4) {
					tile.nearby(tile.data - 1).setBlock(HBlocks.cliffHelper);
				} else if (tile.data <= 8) {
					tile.nearby(Geometry.d8edge(tile.data - 5)).setBlock(HBlocks.cliffHelper);
				} else {
					tile.nearby(Geometry.d4(tile.data - 9)).setBlock(HBlocks.cliffHelper);
					tile.nearby(Geometry.d4(tile.data - 8)).setBlock(HBlocks.cliffHelper);
				}
				tile.data = 0;
			}
		});
	}

	@Override
	public void drawBase(Tile tile) {
		if (tile.data == 0) {
			Draw.color();
			Draw.rect(region, tile.drawx(), tile.drawy());
		} else {
			if (useMapColor) Draw.color(Tmp.c1.set(tile.floor().mapColor).mul(colorMultiplier));
			Draw.rect(cliffs[tile.data - 1], tile.drawx(), tile.drawy());
		}
		Draw.color();
	}

	@Override
	public void load() {
		super.load();
		cliffs = split(name("cliffs"), 48, 0);
	}

	@Override
	public int minimapColor(Tile tile) {
		return Tmp.c1.set(tile.floor().mapColor).mul(1.2f).rgba();
	}
}
