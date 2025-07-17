package heavyindustry.world.blocks.environment;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Point2;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

import static heavyindustry.struct.Collectionsf.arrayOf;
import static heavyindustry.util.Utils.splitUnLayers;

public class TiledFloor extends Floor {
	public TextureRegion[][] largeSpilt;

	public int splitTileSize = 4;
	public int splitVariants = 12;

	public TiledFloor(String name) {
		super(name);
	}

	public TiledFloor(String name, int sSize, int sVar) {
		super(name);
		splitTileSize = sSize;
		splitVariants = sVar;
	}

	@Override
	public void load() {
		super.load();

		largeSpilt = splitUnLayers(name + "-sheet", 32);
	}

	@Override
	public TextureRegion[] icons() {
		return arrayOf(region);
	}

	private void drawTile(Tile tile) {
		int tx = tile.x / splitTileSize * splitTileSize;
		int ty = tile.y / splitTileSize * splitTileSize;

		int index = Mathf.randomSeed(Point2.pack(tx, ty), 0, splitVariants - 1);
		int ix = index * splitTileSize + tile.x - tx;
		int iy = splitTileSize - (tile.y - ty) - 1;
		Draw.rect(largeSpilt[ix][iy], tile.worldx(), tile.worldy());
	}

	@Override
	public void drawBase(Tile tile) {
		drawTile(tile);
		Draw.alpha(1f);
		//drawEdges(tile);
		drawOverlay(tile);
	}
}
