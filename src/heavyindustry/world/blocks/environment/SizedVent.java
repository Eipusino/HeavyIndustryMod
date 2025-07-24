package heavyindustry.world.blocks.environment;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.util.Time;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.SteamVent;

import static heavyindustry.util.Structf.resize;
import static heavyindustry.util.Utils.split;
import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

/**
 * A {@link SteamVent} that can be of any size. Spans multiple tiles; only the middle tile (or in case of {@code size % 2 == 0},
 * the bottom-left middle) should update and draw the actual sprite.
 *
 * @author Eipusino
 */
public class SizedVent extends SteamVent {
	protected static Point2[][] offsets = new Point2[0][];

	public TextureRegion[] splitRegion;

	public int border;

	public Block parent = Blocks.air;
	public Effect effect = Fx.ventSteam;
	public float effectSpacing = 15f;

	public SizedVent(String name) {
		super(name);
		border = 0;
	}

	public SizedVent(String name, int size, int border) {
		super(name);
		this.size = size;
		this.border = border;
	}

	@Override
	public void load() {
		super.load();
		splitRegion = split(name + "-sheet", size * 32, 0);
	}

	@Override
	public void drawBase(Tile tile) {
		parent.drawBase(tile);
		if (checkAdjacent(tile)) {
			float x = tile.worldx(), y = tile.worldy();
			if (size % 2 == 0) {
				x += tilesize / 2f;
				y += tilesize / 2f;
			}

			Draw.rect(splitRegion[variant(tile.x, tile.y)], x, y);
		}
	}

	@Override
	public int variant(int x, int y) {
		return Mathf.randomSeed(Point2.pack(x, y), 0, Math.max(0, splitRegion.length - 1));
	}

	@Override
	public boolean updateRender(Tile tile) {
		return checkAdjacent(tile);
	}

	@Override
	public void renderUpdate(UpdateRenderState state) {
		Tile tile = state.tile;
		if (clear(tile) && (state.data += Time.delta) >= effectSpacing) {
			float x = tile.worldx(), y = tile.worldy();
			if (size % 2 == 0) {
				x += tilesize / 2f;
				y += tilesize / 2f;
			}

			effect.at(x, y);
			state.data = 0f;
		}
	}

	public boolean checkAdjacent(Tile tile) {
		for (Point2 point : getOffsets(size)) {
			Tile other = world.tile(tile.x + point.x, tile.y + point.y);
			if (other == null || other.floor() != this) return false;
		}

		return true;
	}

	public boolean clear(Tile tile) {
		for (Point2 point : getOffsets(size - border * 2)) {
			Tile other = world.tile(tile.x + point.x, tile.y + point.y);
			if (other != null && other.block() != Blocks.air) return false;
		}

		return true;
	}

	public static Point2[] getOffsets(int size) {
		if (size < 1) throw new IllegalArgumentException("Size may not < 1 (" + size + " < 1).");

		int index = size - 1;
		if (index >= offsets.length) {
			int from = offsets.length;
			offsets = resize(offsets, index + 1, null);

			for (int i = from; i < offsets.length; i++) offsets[i] = createOffsets(i + 1);
		}

		return offsets[index];
	}

	protected static Point2[] createOffsets(int size) {
		if (size == 1) return new Point2[]{new Point2(0, 0)};
		int offset = (size - 1) / 2;

		Point2[] out = new Point2[size * size];
		for (int y = 0; y < size; y++) {
			int row = y * size;
			for (int x = 0; x < size; x++) out[row + x] = new Point2(x - offset, y - offset);
		}

		return out;
	}

	@Override
	public Floor asFloor() {
		return this;
	}
}
