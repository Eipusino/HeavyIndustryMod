package heavyindustry.world.blocks.environment;

import arc.graphics.g2d.*;
import arc.math.*;
import heavyindustry.util.*;
import mindustry.world.*;
import mindustry.world.blocks.environment.*;

/**
 * Waiting for Anuke to allow Floor to save data to tile.data.
 */
public class DataFloor extends Floor {
	public static final short MAX = 256;

	public int length;

	public DataFloor(String name) {
		super(name, 0);
	}

	@Override
	public void load() {
		super.load();
		variantRegions = SpriteUtils.splitIndex(name + "-sheet", 32, 32);
		length = Math.min(variantRegions.length, MAX);
	}

	@Override
	protected boolean doEdge(Tile tile, Tile otherTile, Floor other) {
		return false;
	}

	@Override
	public void drawBase(Tile tile) {
		int data = tile.data;
		if (data < 0) data += MAX;
		int index = Mathf.clamp(data, 0, Math.min(variantRegions.length - 1, MAX - 1));
		Draw.rect(variantRegions[index], tile.worldx(), tile.worldy());
	}
}
