package endfield.util;

import arc.func.Floatc2;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.struct.Sort;
import mindustry.Vars;
import mindustry.world.Tile;
import org.jetbrains.annotations.NotNull;

/** Tool set used for directional edge coordinate traversal. */
public final class DirEdges {
	static final Point2[][][] edges = new Point2[Vars.maxBlockSize + 1][4][0];
	static final Point2[][][] angle = new Point2[Vars.maxBlockSize + 1][4][1];

	static {
		edges[0] = new Point2[4][0];
		angle[0] = new Point2[4][0];

		for (int i = 1; i < Vars.maxBlockSize; i++) {
			int off = (i + 1) % 2;
			int rad = i / 2;
			int minOff = -rad + off;

			for (int j = 0; j < 4; j++) {
				edges[i][j] = new Point2[i];
				angle[i][j] = new Point2[1];

				for (int m = minOff; m <= rad; m++) {
					switch (j) {
						case 0 -> edges[i][j][m + rad - off] = new Point2(rad + 1, m);
						case 1 -> edges[i][j][m + rad - off] = new Point2(m, rad + 1);
						case 2 -> edges[i][j][m + rad - off] = new Point2(-rad - 1 + off, m);
						case 3 -> edges[i][j][m + rad - off] = new Point2(m, -rad - 1 + off);
					}
				}

				switch (j) {
					case 0 -> angle[i][j][0] = new Point2(rad + 1, rad + 1);
					case 1 -> angle[i][j][0] = new Point2(-rad - 1 + off, rad + 1);
					case 2 -> angle[i][j][0] = new Point2(-rad - 1 + off, -rad - 1 + off);
					case 3 -> angle[i][j][0] = new Point2(rad + 1, -rad - 1 + off);
				}

				Sort.instance().sort(edges[i][j], (a, b) -> Float.compare(Mathf.angle(a.x, a.y), Mathf.angle(b.x, b.y)));
			}
		}
	}

	/** Don't let anyone instantiate this class. */
	private DirEdges() {}

	/**
	 * Obtain all coordinate arrays of the edges of a block of a certain size in one direction.
	 *
	 * @param size      The size of the block.
	 * @param direction Direction, integer, top left and bottom right order are 0 1 2 3, modulo.
	 */
	public static @NotNull Point2 @NotNull [] get(int size, int direction) {
		if (size < 0 || size > Vars.maxBlockSize)
			throw new IllegalArgumentException("Block size must be between 0 and " + Vars.maxBlockSize);

		return edges[size][Mathf.mod(direction, 4)];
	}

	/**
	 * Obtain all coordinate arrays of a block of a certain size on one side of a direction including the corner edges.
	 *
	 * @param size      The size of the block.
	 * @param direction Direction, integer, take 0 on the right, add 1 clockwise in sequence, take the corner position.
	 */
	public static @NotNull Point2 @NotNull [] get8(int size, int direction) {
		if (size < 0 || size > Vars.maxBlockSize)
			throw new IllegalArgumentException("Block size must be between 0 and " + Vars.maxBlockSize);

		int dir = Mathf.mod(direction, 8);
		return dir % 2 == 0 ? edges[size][dir / 2] : angle[size][dir / 2];
	}

	/**
	 * Traverse the edge coordinates on one side using callbacks.
	 *
	 * @param tile      The central floor must have blocks in the correct state on this floor.
	 * @param direction Traverse in the lateral direction.
	 * @param angles    Do you need to select four corners?
	 * @param posCons   Callback function for edge coordinates.
	 */
	public static void eachDirPos(Tile tile, int direction, boolean angles, Floatc2 posCons) {
		Point2[] points = angles ? get8(tile.block().size, direction) : get(tile.block().size, direction);

		for (Point2 point : points) {
			posCons.get(tile.x + point.x, tile.y + point.y);
		}
	}
}
