package heavyindustry.util;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Point2;
import arc.struct.IntIntMap;

import java.util.Arrays;

/**
 * The utility set for split sprite.
 * <p>This class may be considered for merging into {@link Utils} in future versions.
 *
 * @since 1.0.4
 */
public final class SpriteUtils {
	public static final Point2[] orthogonalPos = {
			new Point2(0, 1),
			new Point2(1, 0),
			new Point2(0, -1),
			new Point2(-1, 0)
	};

	public static final Point2[][] diagonalPos = {
			{new Point2(1, 0), new Point2(1, 1), new Point2(0, 1)},
			{new Point2(1, 0), new Point2(1, -1), new Point2(0, -1)},
			{new Point2(-1, 0), new Point2(-1, -1), new Point2(0, -1)},
			{new Point2(-1, 0), new Point2(-1, 1), new Point2(0, 1)}
	};

	public static final Point2[] proximityPos = {
			new Point2(0, 1),
			new Point2(1, 0),
			new Point2(0, -1),
			new Point2(-1, 0),

			new Point2(1, 1),
			new Point2(1, -1),
			new Point2(-1, -1),
			new Point2(-1, 1)
	};

	/*
		  1
		4   2
		  3
	*/
	public static final short[] index4_4 = {
			0, 2, 10, 8,
			4, 6, 14, 12,
			5, 7, 15, 13,
			1, 3, 11, 9
	};

	/*
		8 1 5
		4   2
		7 3 6
	*/
	public static final short[] index4_12raw = {
			0, 2, 10, 8, 143, 46, 78, 31, 38, 111, 110, 76,
			4, 6, 14, 12, 39, 127, 239, 77, 55, 95, 175, 207,
			5, 7, 15, 13, 23, 191, 223, 141, 63, 255, 240, 205,
			1, 3, 11, 9, 79, 27, 139, 47, 19, 155, 159, 137
	};

	public static final short[] index4_12 = new short[index4_12raw.length];
	public static final IntIntMap index4_12map = new IntIntMap();

	static {
		short[] indices = new short[index4_12raw.length];
		for (short s = 0; s < index4_12raw.length; s++) {
			indices[s] = s;
		}

		Arrays.sort(indices);

		for (short s = 0; s < indices.length; s++) {
			index4_12[indices[s]] = s;
		}

		for (short s = 0; s < index4_12raw.length; s++) {
			index4_12map.put(index4_12raw[s], index4_12[s]);
		}
	}

	private SpriteUtils() {}

	public static TextureRegion[] splitIndex(String name, int tileWidth, int tileHeight) {
		return splitIndex(Core.atlas.find(name), tileWidth, tileHeight);
	}

	public static TextureRegion[] splitIndex(String name, int tileWidth, int tileHeight, int pad) {
		return splitIndex(Core.atlas.find(name), tileWidth, tileHeight, pad);
	}

	public static TextureRegion[] splitIndex(String name, int tileWidth, int tileHeight, int pad, int[] indexMap) {
		return splitIndex(Core.atlas.find(name), tileWidth, tileHeight, pad, indexMap);
	}

	public static TextureRegion[] splitIndex(TextureRegion region, int tileWidth, int tileHeight) {
		return splitIndex(region, tileWidth, tileHeight, 0);
	}

	public static TextureRegion[] splitIndex(TextureRegion region, int tileWidth, int tileHeight, int pad) {
		return splitIndex(region, tileWidth, tileHeight, pad, null);
	}

	public static TextureRegion[] splitIndex(TextureRegion region, int tileWidth, int tileHeight, int pad, int[] indexMap) {
		int x = region.getX();
		int y = region.getY();
		int width = region.width;
		int height = region.height;

		int pWidth = tileWidth + pad * 2;
		int pHeight = tileHeight + pad * 2;

		int sw = width / pWidth;
		int sh = height / pHeight;

		int startX = x;
		TextureRegion[] tiles = new TextureRegion[sw * sh];
		for (int cy = 0; cy < sh; cy++, y += pHeight) {
			x = startX;
			for (int cx = 0; cx < sw; cx++, x += pWidth) {
				int index = cx + cy * sw;
				if (indexMap != null) {
					tiles[indexMap[index]] = new TextureRegion(region.texture, x + pad, y + pad, tileWidth, tileHeight);
				} else {
					tiles[index] = new TextureRegion(region.texture, x + pad, y + pad, tileWidth, tileHeight);
				}
			}
		}

		return tiles;
	}
}
