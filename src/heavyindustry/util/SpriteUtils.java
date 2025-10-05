package heavyindustry.util;

import arc.graphics.g2d.TextureRegion;
import arc.math.geom.Point2;
import arc.struct.IntIntMap;

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
	public static final int[] index4r4 = {
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
	public static final int[] index4r12raw = {
			0, 2, 10, 8, 143, 46, 78, 31, 38, 111, 110, 76,
			4, 6, 14, 12, 39, 127, 239, 77, 55, 95, 175, 207,
			5, 7, 15, 13, 23, 191, 223, 141, 63, 255, 240, 205,
			1, 3, 11, 9, 79, 27, 139, 47, 19, 155, 159, 137
	};

	public static final int[] index4r12 = new int[index4r12raw.length];
	public static final IntIntMap index4r12map = new IntIntMap();

	static {
		int[] indices = new int[index4r12raw.length];
		for (int i = 0; i < index4r12raw.length; i++) {
			indices[i] = i;
		}

		for (int i = 1; i < indices.length; i++) {
			int key = indices[i];
			int keyValue = index4r12raw[key];
			int j = i - 1;

			while (j >= 0 && index4r12raw[indices[j]] > keyValue) {
				indices[j + 1] = indices[j];
				j = j - 1;
			}
			indices[j + 1] = key;
		}

		for (int i = 0; i < indices.length; i++) {
			index4r12[indices[i]] = i;
		}

		for (int i = 0; i < index4r12raw.length; i++) {
			index4r12map.put(index4r12raw[i], index4r12[i]);
		}
	}

	private SpriteUtils() {}

	public static TextureRegion[][] split(TextureRegion region, int size, int pad) {
		int x = region.getX();
		int y = region.getY();
		int width = region.width;
		int height = region.height;

		int pWidth = size + pad * 2;
		int pHeight = size + pad * 2;

		int sw = width / pWidth;
		int sh = height / pHeight;

		int startX = x;

		TextureRegion[][] tiles = new TextureRegion[sw][sh];
		for (int cy = 0; cy < sh; cy++, y += pHeight) {
			x = startX;
			for (int cx = 0; cx < sw; cx++, x += pWidth) {
				tiles[cx][cy] = new TextureRegion(region.texture, x + pad, y + pad, size, size);
			}
		}

		return tiles;
	}

	public static TextureRegion[] splitInLayers(TextureRegion region, int size) {
		return splitInLayers(region, size, 0);
	}

	public static TextureRegion[] splitInLayers(TextureRegion region, int size, int pad) {
		return splitInLayers(region, size, pad, null);
	}

	public static TextureRegion[] splitInLayers(TextureRegion region, int size, int pad, int[] indexMap) {
		int x = region.getX();
		int y = region.getY();
		int width = region.width;
		int height = region.height;

		int pWidth = size + pad * 2;
		int pHeight = size + pad * 2;

		int sw = width / pWidth;
		int sh = height / pHeight;

		int startX = x;
		TextureRegion[] tiles = new TextureRegion[sw * sh];
		for (int cy = 0; cy < sh; cy++, y += pHeight) {
			x = startX;
			for (int cx = 0; cx < sw; cx++, x += pWidth) {
				int index = cx + cy * sw;
				if (indexMap != null) {
					tiles[indexMap[index]] = new TextureRegion(region.texture, x + pad, y + pad, size, size);
				} else {
					tiles[index] = new TextureRegion(region.texture, x + pad, y + pad, size, size);
				}
			}
		}

		return tiles;
	}
}
