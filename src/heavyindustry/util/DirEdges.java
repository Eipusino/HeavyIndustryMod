package heavyindustry.util;

import arc.func.*;
import arc.math.*;
import arc.math.geom.*;
import mindustry.world.*;

import java.util.*;

import static mindustry.Vars.*;

/** Tool set used for directional edge coordinate traversal. */
public final class DirEdges {
    private static final Point2[][][] edges = new Point2[maxBlockSize + 1][4][0];
    private static final Point2[][][] angle = new Point2[maxBlockSize + 1][4][1];

    static {
        edges[0] = new Point2[4][0];
        angle[0] = new Point2[4][0];

        for (int i = 1; i < maxBlockSize; i++) {
            int off = (i + 1) % 2;
            int rad = i / 2;
            int minOff = -rad + off;

            for (int j = 0; j < 4; j++) {
                edges[i][j] = new Point2[i];
                for (int m = minOff; m <= rad; m++) {
                    switch (j) {
                        case 0 -> edges[i][j][m + rad - off] = new Point2(rad + 1, m);
                        case 1 -> edges[i][j][m + rad - off] = new Point2(m, rad + 1);
                        case 2 -> edges[i][j][m + rad - off] = new Point2(-rad - 1 + off, m);
                        case 3 -> edges[i][j][m + rad - off] = new Point2(m, -rad - 1 + off);
                    }
                }

                angle[i][j] = new Point2[]{
                        switch (j) {
                            case 0 -> new Point2(rad + 1, rad + 1);
                            case 1 -> new Point2(-rad - 1 + off, rad + 1);
                            case 2 -> new Point2(-rad - 1 + off, -rad - 1 + off);
                            case 3 -> new Point2(rad + 1, -rad - 1 + off);
                            default -> throw new IllegalStateException("Unexpected value: " + j);
                        }
                };

                Arrays.sort(edges[i][j], (a, b) -> Float.compare(Mathf.angle(a.x, a.y), Mathf.angle(b.x, b.y)));
            }
        }
    }

    /** DirEdges should not be instantiated. */
    private DirEdges() {}

    /**
     * Obtain all coordinate arrays of the edges of a block of a certain size in one direction.
     *
     * @param size      The size of the block.
     * @param direction Direction, integer, top left and bottom right order are 0 1 2 3, modulo.
     */
    public static Point2[] get(int size, int direction) {
        if (size < 0 || size > maxBlockSize)
            throw new RuntimeException("Block size must be between 0 and " + maxBlockSize);

        return edges[size][Mathf.mod(direction, 4)];
    }

    /**
     * Obtain all coordinate arrays of a block of a certain size on one side of a direction including the corner edges.
     *
     * @param size      The size of the block.
     * @param direction Direction, integer, take 0 on the right, add 1 clockwise in sequence, take the corner position.
     */
    public static Point2[] get8(int size, int direction) {
        if (size < 0 || size > maxBlockSize)
            throw new RuntimeException("Block size must be between 0 and " + maxBlockSize);

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
        tile = tile.build.tile;
        Point2[] arr = angles ? get8(tile.block().size, direction) : get(tile.block().size, direction);

        for (Point2 p : arr) {
            posCons.get(tile.x + p.x, tile.y + p.y);
        }
    }
}
