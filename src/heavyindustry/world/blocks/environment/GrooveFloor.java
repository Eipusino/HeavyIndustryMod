package heavyindustry.world.blocks.environment;

import arc.graphics.g2d.Draw;
import heavyindustry.util.SpriteUtils;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;

import static mindustry.Vars.world;

public class GrooveFloor extends Floor {
	public GrooveFloor(String name) {
		super(name, 0);
	}

	@Override
	public void load() {
		super.load();
		variantRegions = SpriteUtils.splitIndex(name + "-sheet", 32, 32);
	}

	@Override
	protected boolean doEdge(Tile tile, Tile otherTile, Floor other) {
		return false;
	}

	@Override
	public void drawBase(Tile tile) {
		Draw.rect(variantRegions[getTileIndex(tile)], tile.worldx(), tile.worldy());
		Draw.alpha(1f);
		drawEdges(tile);
		drawOverlay(tile);
	}

	private byte getTileIndex(Tile tile) {
		byte index = 0;
		if (world.floor(tile.x, tile.y + 1) == this) index += 1;
		if (world.floor(tile.x + 1, tile.y) == this) index += 2;
		if (world.floor(tile.x, tile.y - 1) == this) index += 4;
		if (world.floor(tile.x - 1, tile.y) == this) index += 8;
		return index;
	}

	@Override
	protected void drawEdgesFlat(Tile tile, boolean sameLayer) {}
}
